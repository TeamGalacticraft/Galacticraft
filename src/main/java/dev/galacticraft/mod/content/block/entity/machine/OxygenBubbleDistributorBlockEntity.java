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
import dev.galacticraft.mod.network.s2c.BubbleSizePayload;
import dev.galacticraft.mod.network.s2c.BubbleUpdatePayload;
import dev.galacticraft.mod.screen.OxygenBubbleDistributorMenu;
import dev.galacticraft.mod.util.FluidUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OxygenBubbleDistributorBlockEntity extends MachineBlockEntity implements AtmosphereProvider {
    public static final int MAX_SIZE = 32;

    public static final int CHARGE_SLOT = 0;
    public static final int OXYGEN_INPUT_SLOT = 1; // REVIEW: should this be 0 or 1?
    public static final int OXYGEN_TANK = 0;
    public static final long MAX_OXYGEN = FluidUtil.bucketsToDroplets(50);

    private static final StorageSpec SPEC = StorageSpec.of(
            MachineItemStorage.spec(
                    ItemResourceSlot.builder(TransferType.TRANSFER)
                            .pos(8, 62)
                            .capacity(1)
                            .filter(ResourceFilters.CAN_EXTRACT_ENERGY)
                            .icon(Pair.of(InventoryMenu.BLOCK_ATLAS, Constant.SlotSprite.ENERGY)),
                    ItemResourceSlot.builder(TransferType.PROCESSING)
                            .pos(31, 62)
                            .capacity(1)
                            .filter(ResourceFilters.canExtractFluid(Gases.OXYGEN))
                            .icon(Pair.of(InventoryMenu.BLOCK_ATLAS, Constant.SlotSprite.OXYGEN_TANK))
            ),
            MachineEnergyStorage.spec(
                    Galacticraft.CONFIG.machineEnergyStorageSize(),
                    Galacticraft.CONFIG.oxygenCollectorEnergyConsumptionRate() * 2,
                    0
            ),
            MachineFluidStorage.spec(
                    FluidResourceSlot.builder(TransferType.STRICT_INPUT)
                            .pos(31, 8)
                            .capacity(OxygenBubbleDistributorBlockEntity.MAX_OXYGEN)
                            .filter(ResourceFilters.ofResource(Gases.OXYGEN))
            )
    );

    private boolean bubbleVisible = true;
    private double size = 0;
    private int targetSize = 0;
    private int players = 0;
    private double prevSize;

    public OxygenBubbleDistributorBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.OXYGEN_BUBBLE_DISTRIBUTOR, pos, state, SPEC);
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
        profiler.push("transaction");
        MachineStatus status;
        try {
            if (this.energyStorage().canExtract(Galacticraft.CONFIG.oxygenCollectorEnergyConsumptionRate())) { //todo: config
                profiler.push("bubble");
                if (this.size > this.targetSize) {
                    this.setSize(Math.max(this.size - 0.1F, this.targetSize)); //todo: change rate based on SA or volume
                }

                profiler.pop();

                this.trySyncSize(level, pos, profiler);

                profiler.push("bubbler_distributor_transfer");
                long oxygenRequired = Math.max((long) ((4.0 / 3.0) * Math.PI * this.size * this.size * this.size), 1); //todo: balance values
                FluidResourceSlot slot = this.fluidStorage().slot(OXYGEN_TANK);

                if (slot.canExtract(oxygenRequired)) {
                    slot.extract(oxygenRequired);
                    this.energyStorage().extract(Galacticraft.CONFIG.oxygenCollectorEnergyConsumptionRate());
                    if (this.size < this.targetSize) {
                        this.setSize(this.size + 0.05D); //todo: change rate based on SA or volume
                    }
                    profiler.pop();
                    return GCMachineStatuses.DISTRIBUTING;
                } else {
                    status = GCMachineStatuses.NOT_ENOUGH_OXYGEN;
                    profiler.pop();
                }
            } else {
                status = MachineStatuses.NOT_ENOUGH_ENERGY;
            }
        } finally {
            profiler.pop();
        }
        profiler.push("size");

        if (this.size > 0) {
            this.setSize(0.0);
            this.trySyncSize(level, pos, profiler);
        }

        if (this.size < 0) {
            this.setSize(0);
        }
        profiler.pop();
        return status;
    }

    @Override
    protected void tickDisabled(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        if (this.size > 0) this.setSize(0);
        this.trySyncSize(level, pos, profiler);

        super.tickDisabled(level, pos, state, profiler);
    }

    private void trySyncSize(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull ProfilerFiller profiler) {
        // Could maybe get away with running this 1 in 10 ticks to reduce network traffic
        if (this.prevSize != this.size || this.players != level.players().size()) {
            this.players = level.players().size();
            this.prevSize = this.size;
            profiler.push("network");
            for (ServerPlayer player : level.players()) {
                if (this.size < 0) this.size = 0;
                ServerPlayNetworking.send(player, new BubbleSizePayload(pos, this.size));
            }
            profiler.pop();
        }
    }

    public void distributeOxygenToArea(double targetSize, double prevSize) {
        if (targetSize <= prevSize) {
            this.handleAllocation(targetSize, false);
        }
        this.handleAllocation(targetSize, true);
        assert this.level != null;
        for (BlockPos pos : BlockPos.betweenClosed(this.worldPosition.getX() - Mth.ceil(targetSize), this.worldPosition.getY() - Mth.ceil(targetSize), this.worldPosition.getZ() - Mth.ceil(targetSize),
                this.worldPosition.getX() + Mth.ceil(targetSize), this.worldPosition.getY() + Mth.ceil(targetSize), this.worldPosition.getZ() + Mth.ceil(targetSize))) {
            this.level.galacticraft$addAtmosphericProvider(pos, this.worldPosition);
        }
    }

    private void handleAllocation(double size, boolean allocate) {
        int minX = SectionPos.blockToSectionCoord(this.worldPosition.getX() - Mth.ceil(size)) - 1;
        int minZ = SectionPos.blockToSectionCoord(this.worldPosition.getZ() - Mth.ceil(size)) - 1;
        int maxX = SectionPos.blockToSectionCoord(this.worldPosition.getX() + Mth.ceil(size)) + 1;
        int maxZ = SectionPos.blockToSectionCoord(this.worldPosition.getZ() + Mth.ceil(size)) + 1;
        int minSection = Math.max(0, this.level.getSectionIndex(this.worldPosition.getY() - Mth.ceil(size)) - 1);
        int maxSection = Math.min(this.level.getMaxSection(), this.level.getSectionIndex(this.worldPosition.getY() + Mth.ceil(size)) + 1);

        ChunkPos.rangeClosed(new ChunkPos(minX, minZ), new ChunkPos(maxX, maxZ)).forEach(chunkPos -> {
            LevelChunk chunk = this.level.getChunk(chunkPos.x, chunkPos.z);
            LevelChunkSection[] sections = chunk.getSections();
            for (int i = minSection; i < maxSection; i++) {
                ((ChunkOxygenAccessor)chunk).galacticraft$markSectionDirty(i);
                if (allocate) {
                    ((ChunkSectionOxygenAccessor) sections[i]).galacticraft$ensureSpaceFor(this.worldPosition);
                } else {
                    ((ChunkSectionOxygenAccessor) sections[i]).galacticraft$deallocate(this.worldPosition);
                }
            }
        });
    }

    public int getTargetSize() {
        return this.targetSize;
    }

    public void setTargetSize(int targetSize) {
        this.distributeOxygenToArea(targetSize, this.targetSize);
        this.targetSize = targetSize;
        this.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);
        tag.putInt(Constant.Nbt.MAX_SIZE, this.targetSize);
        tag.putDouble(Constant.Nbt.SIZE, this.size);
        tag.putBoolean(Constant.Nbt.VISIBLE, this.bubbleVisible);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);
        this.size = tag.getDouble(Constant.Nbt.SIZE);
        if (this.size < 0) this.size = 0;
        this.targetSize = tag.getInt(Constant.Nbt.MAX_SIZE);
        if (this.targetSize < 0 || this.targetSize > MAX_SIZE) this.targetSize = 0;
        this.bubbleVisible = tag.getBoolean(Constant.Nbt.VISIBLE);
    }

    public double getSize() {
        return this.size;
    }

    public void setSize(double size) {
        this.size = size;
        this.setChanged();
    }

    public boolean isBubbleVisible() {
        return this.size < 0.0D || this.bubbleVisible;
    }

    public void setBubbleVisible(boolean bubbleVisible) {
        this.bubbleVisible = bubbleVisible;
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
    }

    @Override
    public @Nullable MachineMenu<? extends MachineBlockEntity> createMenu(int syncId, Inventory inv, Player player) {
        return new OxygenBubbleDistributorMenu(syncId, player, this);
    }

    @Override
    public @NotNull CustomPacketPayload createUpdatePayload() {
        return new BubbleUpdatePayload(this.getBlockPos(), this.targetSize, this.size, this.bubbleVisible);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag, registryLookup);
        this.populateUpdateTag(tag);
        return tag;
    }

    @Override
    public boolean canBreathe(double x, double y, double z) { // -0.5 as bubble is on top of block
        return this.worldPosition.distToCenterSqr(x, y - 0.5, z) <= this.size * this.size;
    }

    @Override
    public boolean canBreathe(BlockPos pos) {
        return this.worldPosition.distToCenterSqr(pos.getX() + 0.5, pos.getY() + 0.5 - 0.5, pos.getZ() + 0.5) <= this.size * this.size;
    }

    @Override
    public void notifyStateChange(BlockPos pos, BlockState newState) {

    }

    public void onBroken() {
        this.distributeOxygenToArea(0.0, this.targetSize);
    }
}