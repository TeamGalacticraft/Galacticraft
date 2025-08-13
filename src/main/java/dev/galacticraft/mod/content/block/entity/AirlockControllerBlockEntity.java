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

package dev.galacticraft.mod.content.block.entity;

import com.mojang.authlib.GameProfile;
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.configuration.RedstoneMode;
import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.storage.MachineEnergyStorage;
import dev.galacticraft.machinelib.api.storage.StorageSpec;
import dev.galacticraft.mod.content.AirlockState;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCSounds;
import dev.galacticraft.mod.content.block.machine.airlock.AirlockFrameScanner;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.screen.AirlockControllerMenu;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class AirlockControllerBlockEntity extends MachineBlockEntity {
    // --- NBT keys
    private static final String NBT_PROX = "ProximityOpen";
    private static final String NBT_SEALED = "SealedFrames";

    // --- UI-config (persisted) ---
    private byte proximityOpen = 0;

    private static final StorageSpec SPEC = StorageSpec.of(
            MachineEnergyStorage.spec(0, 0)
    );

    // --- Runtime ---
    private List<AirlockFrameScanner.Result> lastFrames = Collections.emptyList();
    private Map<Long, AirlockFrameScanner.Result> lastFrameMap = Collections.emptyMap();
    private final Set<Long> sealedFrames = new HashSet<>();

    private AirlockState state = AirlockState.NONE;
    private int ticks = 0;

    public AirlockControllerBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.AIRLOCK_CONTROLLER, pos, state, SPEC);
    }

    // --- Persisted config ---

    public byte getProximityOpen() { return this.proximityOpen; }
    public void setProximityOpen(byte v) {
        this.proximityOpen = (byte) Math.max(0, Math.min(5, v));
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);
        tag.putByte(NBT_PROX, this.proximityOpen);

        // persist sealed frame IDs (longs)
        long[] arr = this.sealedFrames.stream().mapToLong(Long::longValue).toArray();
        tag.putLongArray(NBT_SEALED, arr);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);
        if (tag.contains(NBT_PROX)) {
            this.proximityOpen = tag.getByte(NBT_PROX);
        }
        // read sealed IDs; don't try to reseal yet (level may not be ready)
        if (tag.contains(NBT_SEALED)) {
            this.sealedFrames.clear();
            for (long id : tag.getLongArray(NBT_SEALED)) this.sealedFrames.add(id);
        }
        onLoad();
    }

    // --- Core logic ---

    /** Re-apply seals after the chunk loads. */
    public void onLoad() {
        if (!(this.level instanceof ServerLevel server)) return;

        // Re-scan the frames for current geometry
        List<AirlockFrameScanner.Result> frames = AirlockFrameScanner.scanAll(server, this.worldPosition);
        Map<Long, AirlockFrameScanner.Result> frameMap = indexFrames(frames);

        // Place seals for frames that were persisted as sealed
        boolean changed = false;
        for (long id : this.sealedFrames) {
            AirlockFrameScanner.Result f = frameMap.get(id);
            if (f != null) {
                // Ensure they're sealed (idempotent: only places over air)
                seal(f);
                changed = true;
            }
        }

        // restore runtime caches/state
        this.lastFrames = frames;
        this.lastFrameMap = frameMap;

        // recompute state enum
        if (this.sealedFrames.isEmpty() || frames.isEmpty()) this.state = AirlockState.NONE;
        else if (this.sealedFrames.size() == frames.size()) this.state = AirlockState.ALL;
        else this.state = AirlockState.PARTIAL;

        if (changed) {
            BlockState s = server.getBlockState(this.worldPosition);
            server.sendBlockUpdated(this.worldPosition, s, s, Block.UPDATE_CLIENTS);
            setChanged();
        }
    }

    private void serverTick() {
        ServerLevel server = (ServerLevel) this.level;
        assert server != null;
        if ((++this.ticks % 5) != 0) return; // tick every 5

        List<AirlockFrameScanner.Result> frames = AirlockFrameScanner.scanAll(server, this.worldPosition);
        Map<Long, AirlockFrameScanner.Result> frameMap = indexFrames(frames);
        boolean framesChanged = !sameFrames(frames, this.lastFrames);

        boolean powered = server.getBestNeighborSignal(this.worldPosition) > 0;
        RedstoneMode mode = this.getRedstoneMode();
        boolean redstoneAllows = mode.isActive(powered);

        Set<Long> nextSealed = new HashSet<>();
        if (redstoneAllows && !frames.isEmpty()) {
            final double r = this.proximityOpen;
            for (AirlockFrameScanner.Result f : frames) {
                boolean anyAuthorizedNear = false;
                if (r > 0) {
                    AABB expanded = expandedInterior(f, r);
                    for (Player p : server.getEntitiesOfClass(Player.class, expanded)) {
                        if (this.getSecurity().hasAccess(p)) {
                            anyAuthorizedNear = true;
                            break;
                        }
                    }
                }
                if (!anyAuthorizedNear) {
                    nextSealed.add(frameId(f));
                }
            }
        }

        boolean anyChange = false;

        for (long id : new HashSet<>(this.sealedFrames)) {
            if (!nextSealed.contains(id)) {
                AirlockFrameScanner.Result f = this.lastFrameMap.getOrDefault(id, frameMap.get(id));
                if (f != null) {
                    unseal(f);
                    this.sealedFrames.remove(id);
                    anyChange = true;
                }
            }
        }

        for (long id : nextSealed) {
            if (!this.sealedFrames.contains(id)) {
                AirlockFrameScanner.Result f = frameMap.get(id);
                if (f != null) {
                    seal(f);
                    this.sealedFrames.add(id);
                    anyChange = true;
                }
            }
        }

        AirlockState newState;
        if (frames.isEmpty() || this.sealedFrames.isEmpty()) newState = AirlockState.NONE;
        else if (this.sealedFrames.size() == frames.size()) newState = AirlockState.ALL;
        else newState = AirlockState.PARTIAL;

        boolean stateChanged = (newState != this.state);
        this.state = newState;

        if (anyChange || framesChanged || stateChanged) {
            this.lastFrames = frames;
            this.lastFrameMap = frameMap;

            BlockState s = server.getBlockState(this.worldPosition);
            server.sendBlockUpdated(this.worldPosition, s, s, Block.UPDATE_CLIENTS);
            setChanged();
        }
    }

    // --- Helpers ---

    private static Map<Long, AirlockFrameScanner.Result> indexFrames(List<AirlockFrameScanner.Result> list) {
        Map<Long, AirlockFrameScanner.Result> out = new HashMap<>(list.size());
        for (AirlockFrameScanner.Result r : list) out.put(frameId(r), r);
        return out;
    }

    private static long frameId(AirlockFrameScanner.Result f) {
        int h = 1;
        h = 31 * h + f.plane.ordinal();
        h = 31 * h + f.minX; h = 31 * h + f.minY; h = 31 * h + f.minZ;
        h = 31 * h + f.maxX; h = 31 * h + f.maxY; h = 31 * h + f.maxZ;
        return (h & 0xffffffffL);
    }

    private static AABB expandedInterior(AirlockFrameScanner.Result f, double r) {
        AABB interior = switch (f.plane) {
            case XY -> new AABB(f.minX + 1, f.minY + 1, f.minZ,     f.maxX,     f.maxY,     f.maxZ);
            case XZ -> new AABB(f.minX + 1, f.minY,     f.minZ + 1, f.maxX,     f.maxY,     f.maxZ);
            case YZ -> new AABB(f.minX,     f.minY + 1, f.minZ + 1, f.maxX,     f.maxY,     f.maxZ);
        };
        return interior.inflate(Math.max(r, 0) + 1.0e-4);
    }

    private static boolean sameFrames(List<AirlockFrameScanner.Result> a, List<AirlockFrameScanner.Result> b) {
        if (a == b) return true;
        if (a == null || b == null || a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) {
            var x = a.get(i); var y = b.get(i);
            if (x.plane != y.plane) return false;
            if (x.minX != y.minX || x.minY != y.minY || x.minZ != y.minZ) return false;
            if (x.maxX != y.maxX || x.maxY != y.maxY || x.maxZ != y.maxZ) return false;
        }
        return true;
    }

    private void seal(AirlockFrameScanner.Result f) {
        if (!(this.level instanceof ServerLevel server)) return;

        boolean anyAir = false;
        switch (f.plane) {
            case XY -> {
                final int z = f.minZ;
                for (int x = f.minX + 1; x <= f.maxX - 1; x++)
                    for (int y = f.minY + 1; y <= f.maxY - 1; y++)
                        if (server.getBlockState(new BlockPos(x, y, z)).isAir()) anyAir = true;
            }
            case XZ -> {
                final int y = f.minY;
                for (int x = f.minX + 1; x <= f.maxX - 1; x++)
                    for (int z = f.minZ + 1; z <= f.maxZ - 1; z++)
                        if (server.getBlockState(new BlockPos(x, y, z)).isAir()) anyAir = true;
            }
            case YZ -> {
                final int x = f.minX;
                for (int y = f.minY + 1; y <= f.maxY - 1; y++)
                    for (int z = f.minZ + 1; z <= f.maxZ - 1; z++)
                        if (server.getBlockState(new BlockPos(x, y, z)).isAir()) anyAir = true;
            }
        }
        if (anyAir) {
            BlockPos center = new BlockPos((f.minX + f.maxX) / 2, (f.minY + f.maxY) / 2, (f.minZ + f.maxZ) / 2);
            server.playSound(null, center, GCSounds.PLAYER_CLOSEAIRLOCK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        switch (f.plane) {
            case XY -> {
                final int z = f.minZ;
                for (int x = f.minX + 1; x <= f.maxX - 1; x++)
                    for (int y = f.minY + 1; y <= f.maxY - 1; y++) {
                        BlockPos p = new BlockPos(x, y, z);
                        if (server.getBlockState(p).isAir())
                            server.setBlock(p, GCBlocks.AIR_LOCK_SEAL.defaultBlockState()
                                            .setValue(dev.galacticraft.mod.content.block.special.AirlockSealBlock.FACING, f.sealFacing),
                                    Block.UPDATE_ALL);
                    }
            }
            case XZ -> {
                final int y = f.minY;
                for (int x = f.minX + 1; x <= f.maxX - 1; x++)
                    for (int z = f.minZ + 1; z <= f.maxZ - 1; z++) {
                        BlockPos p = new BlockPos(x, y, z);
                        if (server.getBlockState(p).isAir())
                            server.setBlock(p, GCBlocks.AIR_LOCK_SEAL.defaultBlockState()
                                            .setValue(dev.galacticraft.mod.content.block.special.AirlockSealBlock.FACING, f.sealFacing),
                                    Block.UPDATE_ALL);
                    }
            }
            case YZ -> {
                final int x = f.minX;
                for (int y = f.minY + 1; y <= f.maxY - 1; y++)
                    for (int z = f.minZ + 1; z <= f.maxZ - 1; z++) {
                        BlockPos p = new BlockPos(x, y, z);
                        if (server.getBlockState(p).isAir())
                            server.setBlock(p, GCBlocks.AIR_LOCK_SEAL.defaultBlockState()
                                            .setValue(dev.galacticraft.mod.content.block.special.AirlockSealBlock.FACING, f.sealFacing),
                                    Block.UPDATE_ALL);
                    }
            }
        }
    }

    private void unseal(AirlockFrameScanner.Result f) {
        if (!(this.level instanceof ServerLevel server)) return;

        boolean hadSeal = false;
        switch (f.plane) {
            case XY -> {
                final int z = f.minZ;
                for (int x = f.minX + 1; x <= f.maxX - 1; x++)
                    for (int y = f.minY + 1; y <= f.maxY - 1; y++) {
                        BlockPos p = new BlockPos(x, y, z);
                        if (server.getBlockState(p).is(GCBlocks.AIR_LOCK_SEAL)) {
                            hadSeal = true;
                            server.setBlock(p, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                        }
                    }
            }
            case XZ -> {
                final int y = f.minY;
                for (int x = f.minX + 1; x <= f.maxX - 1; x++)
                    for (int z = f.minZ + 1; z <= f.maxZ - 1; z++) {
                        BlockPos p = new BlockPos(x, y, z);
                        if (server.getBlockState(p).is(GCBlocks.AIR_LOCK_SEAL)) {
                            hadSeal = true;
                            server.setBlock(p, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                        }
                    }
            }
            case YZ -> {
                final int x = f.minX;
                for (int y = f.minY + 1; y <= f.maxY - 1; y++)
                    for (int z = f.minZ + 1; z <= f.maxZ - 1; z++) {
                        BlockPos p = new BlockPos(x, y, z);
                        if (server.getBlockState(p).is(GCBlocks.AIR_LOCK_SEAL)) {
                            hadSeal = true;
                            server.setBlock(p, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                        }
                    }
            }
        }
        if (hadSeal) {
            BlockPos center = new BlockPos((f.minX + f.maxX) / 2, (f.minY + f.maxY) / 2, (f.minZ + f.maxZ) / 2);
            server.playSound(null, center, GCSounds.PLAYER_OPENAIRLOCK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    // --- UI / Status ---

    public AirlockState getAirlockState() { return this.state; }
    public List<AirlockFrameScanner.Result> getLastFrames() { return this.lastFrames; }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(Translations.Ui.AIRLOCK_DEFAULT_NAME);
    }

    @Override
    protected void tickConstant(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        serverTick();
        super.tickConstant(level, pos, state, profiler);
    }

    @Override
    protected @NotNull MachineStatus tick(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        return switch (this.state) {
            case ALL -> GCMachineStatuses.AIRLOCK_ENABLED;
            case PARTIAL -> GCMachineStatuses.AIRLOCK_PARTIAL;
            case NONE -> GCMachineStatuses.AIRLOCK_DISABLED;
        };
    }

    @Override
    public @Nullable MachineMenu<? extends MachineBlockEntity> createMenu(int syncId, Inventory inventory, Player player) {
        return new AirlockControllerMenu(syncId, player, this);
    }

    @Override
    public void setRemoved() {
        try {
            if (this.level instanceof ServerLevel server) {
                boolean shouldUnseal = false;

                boolean chunkLoaded;
                chunkLoaded = server.isLoaded(this.worldPosition);

                if (chunkLoaded) {
                    BlockEntity current = server.getBlockEntity(this.worldPosition);
                    if (current == null || current != this) {
                        shouldUnseal = true;
                    } else {
                        var stateAtPos = server.getBlockState(this.worldPosition);
                        if (stateAtPos.isAir()) {
                            shouldUnseal = true;
                        }
                    }
                }

                if (shouldUnseal) {
                    for (long id : this.sealedFrames) {
                        var f = this.lastFrameMap.get(id);
                        if (f != null) unseal(f);
                    }
                    this.sealedFrames.clear();
                }
            }
        } finally {
            super.setRemoved();
        }
    }
}