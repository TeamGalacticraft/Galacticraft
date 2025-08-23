/*
 * Copyright (c) 2019-2025 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.content.block.entity.machine;

import com.mojang.datafixers.util.Pair;
import dev.galacticraft.api.block.entity.AtmosphereProvider;
import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.impl.internal.accessor.ChunkOxygenAccessor;
import dev.galacticraft.impl.internal.accessor.ChunkSectionOxygenAccessor;
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.filter.ResourceFilters;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.MachineStatuses;
import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.storage.MachineEnergyStorage;
import dev.galacticraft.machinelib.api.storage.MachineFluidStorage;
import dev.galacticraft.machinelib.api.storage.MachineItemStorage;
import dev.galacticraft.machinelib.api.storage.StorageSpec;
import dev.galacticraft.machinelib.api.storage.slot.FluidResourceSlot;
import dev.galacticraft.machinelib.api.storage.slot.ItemResourceSlot;
import dev.galacticraft.machinelib.api.transfer.TransferType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.network.s2c.OxygenSealerUpdatePayload;
import dev.galacticraft.mod.screen.OxygenSealerMenu;
import dev.galacticraft.mod.tag.GCBlockTags;
import dev.galacticraft.mod.util.FluidUtil;
import it.unimi.dsi.fastutil.objects.*;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;

public class OxygenSealerBlockEntity extends MachineBlockEntity implements AtmosphereProvider {
    public static final int CHARGE_SLOT = 0;
    public static final int OXYGEN_INPUT_SLOT = 1;
    public static final int OXYGEN_TANK = 0;

    public static final long MAX_OXYGEN = FluidUtil.bucketsToDroplets(20);
    public static final int SEAL_CHECK_TIME = 40;
    public static final int MAX_SEALER_VOLUME = 1024; //2048

    private int sealCheckTime = SEAL_CHECK_TIME;

    private static final StorageSpec SPEC = StorageSpec.of(
            MachineItemStorage.spec(
                    ItemResourceSlot.builder(TransferType.TRANSFER)
                            .pos(8, 62)
                            .capacity(1)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY)
                            .icon(Pair.of(InventoryMenu.BLOCK_ATLAS, Constant.SlotSprite.ENERGY)),
                    ItemResourceSlot.builder(TransferType.PROCESSING) // todo: drop for decompressor?
                            .pos(31, 62)
                            .capacity(1)
                            .filter(ResourceFilters.canExtractFluid(Gases.OXYGEN))
                            .icon(Pair.of(InventoryMenu.BLOCK_ATLAS, Constant.SlotSprite.OXYGEN_TANK))
            ),
            MachineEnergyStorage.spec(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    Galacticraft.CONFIG.oxygenSealerEnergyConsumptionRate() * 2,
                    0
            ),
            MachineFluidStorage.spec(
                    FluidResourceSlot.builder(TransferType.STRICT_INPUT)
                            .pos(31, 8)
                            .capacity(OxygenSealerBlockEntity.MAX_OXYGEN)
                            .filter(ResourceFilters.ofResource(Gases.OXYGEN))
            )
    );
    private final Object2BooleanMap<BlockPos> sealedPositions = new Object2BooleanOpenHashMap<>();

    public OxygenSealerBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.OXYGEN_SEALER, pos, state, SPEC);
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        super.tickConstant(level, pos, state, profiler);
        profiler.push("extract_resources");
        this.chargeFromSlot(CHARGE_SLOT);
        this.takeFluidFromSlot(OXYGEN_INPUT_SLOT, OXYGEN_TANK, Gases.OXYGEN);
        profiler.pop();
    }

    @Override
    protected @NotNull MachineStatus tick(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        // Check if the machine has enough energy
        if (!this.energyStorage().canExtract(Galacticraft.CONFIG.oxygenSealerEnergyConsumptionRate())) {
            return MachineStatuses.NOT_ENOUGH_ENERGY;
        }

        // Check if the oxygen tank is empty
        if (this.fluidStorage().slot(OXYGEN_TANK).isEmpty()) {
            return GCMachineStatuses.NOT_ENOUGH_OXYGEN;
        }

        if (!this.sealedPositions.isEmpty()) {
            this.energyStorage().extract(Galacticraft.CONFIG.oxygenSealerEnergyConsumptionRate());
            this.fluidStorage().slot(OXYGEN_TANK).extract(Galacticraft.CONFIG.oxygenSealerOxygenConsumptionRate());
            return GCMachineStatuses.SEALED;
        } else if (--this.sealCheckTime == 0) {
            this.sealCheckTime = SEAL_CHECK_TIME;

            if (level.getBlockState(pos.relative(Direction.UP)).isCollisionShapeFullBlock(this.level, pos.relative(Direction.UP))) {
                return GCMachineStatuses.BLOCKED;
            }

            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
            Object2BooleanMap<BlockPos> visited = new Object2BooleanOpenHashMap<>();
            ObjectArrayFIFOQueue<SourcedPos> queue = new ObjectArrayFIFOQueue<>(32);

            visited.put(pos, true);
            visited.put(pos.relative(Direction.UP), false);
            queue.enqueue(new SourcedPos(pos.relative(Direction.UP), Direction.DOWN));

            while (!queue.isEmpty() && (queue.size() + visited.size() <= MAX_SEALER_VOLUME)) {
                SourcedPos current = queue.dequeue();
                if (level.isOutsideBuildHeight(current.pos.getY())) {
                    if (queue.isEmpty()) queue.enqueue(new SourcedPos(null, Direction.DOWN)); // ensure no accidental passes!
                    break;
                }
                for (Direction direction : Direction.values()) {
                    if (direction == current.direction) continue;
                    mutable.setWithOffset(current.pos, direction);
                    if (!visited.containsKey(mutable)) {
                        BlockState bs = level.getBlockState(mutable);
                        boolean full = isSealable(mutable, bs);
                        BlockPos ps = mutable.immutable();
                        visited.put(ps, full);
                        if (!full) {
                            queue.enqueue(new SourcedPos(ps, direction.getOpposite()));
                        }
                    }
                }
            }

            if (queue.isEmpty()) {
                // found area - seal it!
                this.sealBlocks(visited);
            } // no area, no change.
        }
        return GCMachineStatuses.AREA_TOO_LARGE;
    }

    public boolean isSealed() {
        return this.isActive();
    }

    public int getSealTickTime() {
        return this.sealCheckTime;
    }

    private void sealBlocks(Object2BooleanMap<BlockPos> visited) {
        if (visited.isEmpty()) return;
        Object2ObjectOpenHashMap<BlockPos, LevelChunkSection> visitedSections = new Object2ObjectOpenHashMap<>();
        BlockPos.MutableBlockPos section = new BlockPos.MutableBlockPos();
        for (BlockPos pos : visited.keySet()) {
            section.set(SectionPos.blockToSectionCoord(pos.getX()), this.level.getSectionIndex(pos.getY()), SectionPos.blockToSectionCoord(pos.getZ()));
            if (!visitedSections.containsKey(section)) {
                LevelChunk chunk = this.level.getChunk(section.getX(), section.getZ());
                LevelChunkSection section1 = chunk.getSection(section.getY());
                visitedSections.put(section.immutable(), section1);
                ((ChunkOxygenAccessor) chunk).galacticraft$markSectionDirty(section.getY());
                ((ChunkSectionOxygenAccessor) section1).galacticraft$ensureSpaceFor(this.worldPosition);
            }
            ((ChunkSectionOxygenAccessor) visitedSections.get(section)).galacticraft$add(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, this.worldPosition);
        }

        this.sealedPositions.putAll(visited);
        this.markChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);
        long[] positions = new long[this.sealedPositions.size()];
        BitSet set = new BitSet(this.sealedPositions.size());
        int i = 0;
        for (Object2BooleanMap.Entry<BlockPos> e : this.sealedPositions.object2BooleanEntrySet()) {
            positions[i] = e.getKey().asLong();
            set.set(i++, e.getBooleanValue());
        }
        tag.putLongArray(Constant.Nbt.SEALED, positions);
        tag.putLongArray(Constant.Nbt.SOLID, set.toLongArray());
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);
        long[] sealed = tag.getLongArray(Constant.Nbt.SEALED);
        BitSet solid = BitSet.valueOf(tag.getLongArray(Constant.Nbt.SOLID));
        for (int i = 0; i < sealed.length; i++) {
            this.sealedPositions.put(BlockPos.of(sealed[i]), solid.get(i));
        }
    }

    @Nullable
    @Override
    public MachineMenu<? extends MachineBlockEntity> createMenu(int syncId, Inventory inv, Player player) {
        return new OxygenSealerMenu(syncId, player, this);
    }

    @Override
    public boolean canBreathe(double x, double y, double z) {
        return this.isSealed() && this.sealedPositions.containsKey(BlockPos.containing(x, y, z));
    }

    @Override
    public boolean canBreathe(BlockPos pos) {
        return this.isSealed() && this.sealedPositions.containsKey(pos);
    }

    @Override
    public void notifyStateChange(BlockPos pos, BlockState newState) {
        if (pos.equals(this.worldPosition)) {
            this.destroySeal();
            return;
        }
        if (this.sealedPositions.containsKey(pos)) {
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
            if (this.sealedPositions.getBoolean(pos)) {
                if (!isSealable(pos, newState)) {
                    this.sealedPositions.put(pos, false);

                    // was sealed -> now not. check for leaks
                    Object2BooleanMap<BlockPos> visited = new Object2BooleanOpenHashMap<>();
                    boolean sealable = true;

                    for (Direction direction : Direction.values()) {
                        mutable.setWithOffset(pos, direction);
                        if (!this.sealedPositions.containsKey(mutable) && !visited.containsKey(mutable)) {
                            if (isSealable(mutable, level.getBlockState(mutable))) {
                                visited.put(mutable.immutable(), true); // newly uncovered WALL.
                                if (this.sealedPositions.size() == MAX_SEALER_VOLUME) {
                                    sealable = false;
                                    break;
                                }
                            } else if (!this.trySeal(pos, direction, visited) || visited.size() + this.sealedPositions.size() > MAX_SEALER_VOLUME) { // newly made unsealed hole - try to seal it.
                                sealable = false;
                                break;
                            }
                        }
                    }
                    if (sealable) {
                        // it's still sealed - update!
                        this.sealBlocks(visited);
                    } else {
                        // no longer sealed - destroy.
                        this.destroySeal();
                    }
                }
            } else {
                if (isSealable(pos, newState)) {
                    this.sealedPositions.put(pos, true);
                    ObjectOpenHashSet<BlockPos> visited = new ObjectOpenHashSet<>();
                    ObjectOpenHashSet<BlockPos> visitedNonSolid = new ObjectOpenHashSet<>();
                    BlockPos target = this.worldPosition.relative(Direction.UP);

                    boolean anyPass = false;
                    // was not sealed -> now is. check if blocks others
                    // can use pure virtual search (no leaks possible)
                    for (Direction direction : Direction.values()) {
                        mutable.setWithOffset(pos, direction);
                        if (this.sealedPositions.containsKey(mutable)) {
                            // need to navigate back to sealer +1y to confirm connection
                            visited.clear();
                            visitedNonSolid.clear();
                            if (!this.tryNavigateTo(mutable, target, visited, visitedNonSolid)) {
                                this.destroySubSeal(visited, visitedNonSolid);
                            } else {
                                anyPass = true;
                            }
                        }
                    }
                    if (!anyPass) {
                        // This should not happen???
                        this.destroySeal();
                    }
                }
            }

            this.markChanged();
        }
    }

    private void destroySubSeal(ObjectOpenHashSet<BlockPos> visited, ObjectOpenHashSet<BlockPos> visitedNonSolid) {
        for (BlockPos pos : visitedNonSolid) {
            this.sealedPositions.removeBoolean(pos);
            visited.remove(pos);
            this.level.removeAtmosphericProvider(pos, this.worldPosition);
        }

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        // only solids left - check for adjacent still sealed before removing
        for (BlockPos pos : visited) {
            boolean anyPath = false;
            for (Direction direction : Direction.values()) {
                mutable.setWithOffset(pos, direction);
                if (!this.sealedPositions.getOrDefault(mutable, true)) {
                    anyPath = true;
                    break;
                }
            }
            if (!anyPath) {
                // all surrounding blocks either SOLID (not transitive), or otherwise not breathable - this block is not breathable anymore
                this.sealedPositions.removeBoolean(pos);
                this.level.removeAtmosphericProvider(pos, this.worldPosition);
            }
        }
    }

    private boolean tryNavigateTo(BlockPos.MutableBlockPos mutable, BlockPos target, ObjectOpenHashSet<BlockPos> visited, ObjectOpenHashSet<BlockPos> visitedNonSolid) {
        BlockPos start = mutable.immutable();
        ObjectArrayFIFOQueue<BlockPos> queue = new ObjectArrayFIFOQueue<>(32);
        queue.enqueue(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            BlockPos current = queue.dequeue();
            for (Direction direction : Direction.values()) {
                mutable.setWithOffset(current, direction);
                if (target.equals(mutable)) return true;
                if (this.sealedPositions.containsKey(mutable)) {
                    if (!visited.contains(mutable)) {
                        BlockPos pos = mutable.immutable();
                        visited.add(pos);
                        if (!this.sealedPositions.getBoolean(mutable)) {
                            visitedNonSolid.add(pos);
                            queue.enqueue(pos);
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean trySeal(BlockPos from, Direction fromDir, Object2BooleanMap<BlockPos> visited) {
        BlockPos.MutableBlockPos mutable = new  BlockPos.MutableBlockPos();
        ObjectArrayFIFOQueue<SourcedPos> queue = new ObjectArrayFIFOQueue<>(32);

        visited.put(from, false);
        visited.put(from.relative(fromDir), false); // newly uncovered AIR.
        queue.enqueue(new SourcedPos(from.relative(fromDir), fromDir.getOpposite()));

        while (!queue.isEmpty() && (this.sealedPositions.size() + queue.size() + visited.size() <= MAX_SEALER_VOLUME)) {
            SourcedPos current = queue.dequeue();
            if (this.level.isOutsideBuildHeight(current.pos.getY())) return false;

            for (Direction direction : Direction.values()) {
                if (direction == current.direction) continue;
                mutable.setWithOffset(current.pos, direction);
                // if NOT sealed and NOT already visited, it's a new spot.
                if (!this.sealedPositions.containsKey(mutable) && !visited.containsKey(mutable)) {
                    BlockState bs = level.getBlockState(mutable);
                    boolean full = isSealable(mutable, bs);
                    BlockPos ps = mutable.immutable();
                    visited.put(ps, full);
                    if (!full) {
                        queue.enqueue(new SourcedPos(ps, direction.getOpposite()));
                    }
                }
            }
        }

        return queue.isEmpty();
    }

    public void destroySeal() {
        for (BlockPos pos : this.sealedPositions.keySet()) {
            // todo: make more efficient
            ((ChunkSectionOxygenAccessor) this.level.getChunkAt(pos).getSection(this.level.getSectionIndex(pos.getY()))).galacticraft$deallocate(this.worldPosition);
        }
        this.sealedPositions.clear();
        markChanged();
    }

    private void markChanged() {
        this.setChanged();
        CustomPacketPayload updatePayload = this.createUpdatePayload();
        for (ServerPlayer player : PlayerLookup.tracking(this)) {
            ServerPlayNetworking.send(player, updatePayload);
        }
    }

    private boolean isSealable(BlockPos pos, BlockState newState) {
        return newState.isCollisionShapeFullBlock(this.level, pos) || newState.is(GCBlockTags.SEALABLE);
    }

    @Override
    public @NotNull CustomPacketPayload createUpdatePayload() {
        BlockPos[] positions = this.sealedPositions.keySet().toArray(new BlockPos[0]);
        BitSet values = new BitSet(this.sealedPositions.size());
        int i = 0;
        for (boolean value : this.sealedPositions.values()) {
            if (value) values.set(i);
            i++;
        }
        return new OxygenSealerUpdatePayload(this.worldPosition, positions, values);
    }

    @Override
    public void populateUpdateTag(CompoundTag tag) {
        super.populateUpdateTag(tag);
        long[] positions = new long[this.sealedPositions.size()];
        BitSet set = new BitSet(this.sealedPositions.size());
        int i = 0;
        for (Object2BooleanMap.Entry<BlockPos> e : this.sealedPositions.object2BooleanEntrySet()) {
            positions[i] = e.getKey().asLong();
            set.set(i++, e.getBooleanValue());
        }
        tag.putLongArray(Constant.Nbt.SEALED, positions);
        tag.putLongArray(Constant.Nbt.SOLID, set.toLongArray());
    }

    public void handleUpdate(OxygenSealerUpdatePayload payload) {
        this.sealedPositions.clear();
        BlockPos[] positions = payload.positions();
        BitSet set = payload.set();
        for (int i = 0; i < positions.length; i++) {
            this.sealedPositions.put(positions[i], set.get(i));
        }
    }

    private record SourcedPos(BlockPos pos, Direction direction) {}
}
