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

import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.machine.configuration.RedstoneMode;
import dev.galacticraft.machinelib.api.storage.MachineEnergyStorage;
import dev.galacticraft.machinelib.api.storage.MachineItemStorage;
import dev.galacticraft.machinelib.api.storage.StorageSpec;
import dev.galacticraft.mod.Galacticraft;
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
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import static dev.galacticraft.mod.content.block.special.AirlockSealBlock.FACING;

public class AirlockControllerBlockEntity extends MachineBlockEntity {
    // Proximity setting (0..5). 0 = off.
    private byte proximityOpen = 0;

    // DUMMY storage so MachineLib doesn't crash on zero-slot specs
    private static final StorageSpec SPEC = StorageSpec.of(
            MachineEnergyStorage.spec(
                    0, 0
            )
    );

    // runtime
    public boolean active;
    public boolean lastActive;
    private List<AirlockFrameScanner.Result> lastFrames = java.util.Collections.emptyList();
    public int ticks = 0;

    public AirlockControllerBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.AIRLOCK_CONTROLLER, pos, state, SPEC);
    }

    public byte getProximityOpen() { return this.proximityOpen; }
    public void setProximityOpen(byte v) { this.proximityOpen = (byte)Math.max(0, Math.min(5, v)); setChanged(); }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);
        tag.putByte("ProximityOpen", this.proximityOpen);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);
        if (tag.contains("ProximityOpen")) this.proximityOpen = tag.getByte("ProximityOpen");
    }

    private void serverTick() {
        if (this.level == null || this.level.isClientSide()) return;
        this.ticks++;

        if (this.ticks % 5 != 0) return;

        // Re-scan frames
        List<AirlockFrameScanner.Result> frames = AirlockFrameScanner.scanAll(this.level, this.worldPosition);
        boolean framesChanged = !sameFrames(frames, this.lastFrames);

        // RedstoneMode: true = machine should be "active" given powered state
        boolean powered = this.level.getBestNeighborSignal(this.worldPosition) > 0;
        RedstoneMode mode = this.getRedstoneMode();
        boolean redstoneAllows = mode.isActive(powered);

        // Proximity: open (disable) only for *authorized* nearby players
        boolean near = false;
        if (!frames.isEmpty() && this.proximityOpen > 0) {
            double r = this.proximityOpen;
            AABB big = null;
            for (var f : frames) {
                AABB expanded = getExpanded(f, r);
                big = (big == null) ? expanded : big.minmax(expanded);
            }
            if (big != null) {
                // ONLY count players who are allowed by MachineLib security
                var players = this.level.getEntitiesOfClass(Player.class, big);
                for (Player p : players) {
                    if (this.getSecurity().hasAccess(p)) {
                        near = true;
                        break;
                    }
                }
            }
        }

        boolean newActive = redstoneAllows && !near;
        boolean activeChanged = (newActive != this.active);

        if (activeChanged || framesChanged) {
            // Unseal old frames
            for (var f : this.lastFrames) unseal(f);

            // Seal new if active
            if (newActive) {
                for (var f : frames) seal(f);
            }

            this.active = newActive;
            this.lastFrames = frames;

            BlockState s = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, s, s, Block.UPDATE_CLIENTS);
        }
    }

    private static @NotNull AABB getExpanded(AirlockFrameScanner.Result f, double r) {
        AABB interior = switch (f.plane) {
            case XY -> new AABB(f.minX + 1, f.minY + 1, f.minZ,     f.maxX,     f.maxY,     f.maxZ);
            case XZ -> new AABB(f.minX + 1, f.minY,     f.minZ + 1, f.maxX,     f.maxY,     f.maxZ);
            case YZ -> new AABB(f.minX,     f.minY + 1, f.minZ + 1, f.maxX,     f.maxY,     f.maxZ);
        };
        interior.inflate(0.0001); // avoid zero-thickness issues on the plane
        return interior.inflate(r);
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
        boolean anyAir = false;
        switch (f.plane) {
            case XY -> {
                int z = f.minZ;
                for (int x = f.minX + 1; x <= f.maxX - 1; x++)
                    for (int y = f.minY + 1; y <= f.maxY - 1; y++)
                        if (this.level.getBlockState(new BlockPos(x, y, z)).isAir()) anyAir = true;
            }
            case XZ -> {
                int y = f.minY;
                for (int x = f.minX + 1; x <= f.maxX - 1; x++)
                    for (int z = f.minZ + 1; z <= f.maxZ - 1; z++)
                        if (this.level.getBlockState(new BlockPos(x, y, z)).isAir()) anyAir = true;
            }
            case YZ -> {
                int x = f.minX;
                for (int y = f.minY + 1; y <= f.maxY - 1; y++)
                    for (int z = f.minZ + 1; z <= f.maxZ - 1; z++)
                        if (this.level.getBlockState(new BlockPos(x, y, z)).isAir()) anyAir = true;
            }
        }
        if (anyAir) {
            BlockPos center = new BlockPos((f.minX + f.maxX)/2, (f.minY + f.maxY)/2, (f.minZ + f.maxZ)/2);
            this.level.playSound(null, center, GCSounds.PLAYER_CLOSEAIRLOCK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        switch (f.plane) {
            case XY -> {
                int z = f.minZ;
                for (int x = f.minX + 1; x <= f.maxX - 1; x++)
                    for (int y = f.minY + 1; y <= f.maxY - 1; y++) {
                        BlockPos p = new BlockPos(x, y, z);
                        if (this.level.getBlockState(p).isAir())
                            this.level.setBlock(p, GCBlocks.AIR_LOCK_SEAL.defaultBlockState().setValue(FACING, f.sealFacing), Block.UPDATE_ALL);
                    }
            }
            case XZ -> {
                int y = f.minY;
                for (int x = f.minX + 1; x <= f.maxX - 1; x++)
                    for (int z = f.minZ + 1; z <= f.maxZ - 1; z++) {
                        BlockPos p = new BlockPos(x, y, z);
                        if (this.level.getBlockState(p).isAir())
                            this.level.setBlock(p, GCBlocks.AIR_LOCK_SEAL.defaultBlockState().setValue(FACING, f.sealFacing), Block.UPDATE_ALL);
                    }
            }
            case YZ -> {
                int x = f.minX;
                for (int y = f.minY + 1; y <= f.maxY - 1; y++)
                    for (int z = f.minZ + 1; z <= f.maxZ - 1; z++) {
                        BlockPos p = new BlockPos(x, y, z);
                        if (this.level.getBlockState(p).isAir())
                            this.level.setBlock(p, GCBlocks.AIR_LOCK_SEAL.defaultBlockState().setValue(FACING, f.sealFacing), Block.UPDATE_ALL);
                    }
            }
        }
    }

    public void unseal(AirlockFrameScanner.Result f) {
        boolean hadSeal = false;
        switch (f.plane) {
            case XY -> {
                int z = f.minZ;
                for (int x = f.minX + 1; x <= f.maxX - 1; x++)
                    for (int y = f.minY + 1; y <= f.maxY - 1; y++) {
                        BlockPos p = new BlockPos(x, y, z);
                        if (this.level.getBlockState(p).is(GCBlocks.AIR_LOCK_SEAL)) {
                            hadSeal = true;
                            this.level.setBlock(p, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                        }
                    }
            }
            case XZ -> {
                int y = f.minY;
                for (int x = f.minX + 1; x <= f.maxX - 1; x++)
                    for (int z = f.minZ + 1; z <= f.maxZ - 1; z++) {
                        BlockPos p = new BlockPos(x, y, z);
                        if (this.level.getBlockState(p).is(GCBlocks.AIR_LOCK_SEAL)) {
                            hadSeal = true;
                            this.level.setBlock(p, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                        }
                    }
            }
            case YZ -> {
                int x = f.minX;
                for (int y = f.minY + 1; y <= f.maxY - 1; y++)
                    for (int z = f.minZ + 1; z <= f.maxZ - 1; z++) {
                        BlockPos p = new BlockPos(x, y, z);
                        if (this.level.getBlockState(p).is(GCBlocks.AIR_LOCK_SEAL)) {
                            hadSeal = true;
                            this.level.setBlock(p, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                        }
                    }
            }
        }
        if (hadSeal) {
            BlockPos center = new BlockPos((f.minX + f.maxX)/2, (f.minY + f.maxY)/2, (f.minZ + f.maxZ)/2);
            this.level.playSound(null, center, GCSounds.PLAYER_OPENAIRLOCK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(Translations.Ui.AIRLOCK_OWNER, ""); // fill in owner if you track it
    }

    @Override
    protected @NotNull MachineStatus tick(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        serverTick();
        return GCMachineStatuses.SEALED;
    }

    @Override
    protected void tickDisabled(@NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ProfilerFiller profiler) {
        serverTick();

        super.tickDisabled(level, pos, state, profiler);
    }

    @Override
    public AirlockControllerMenu createMenu(int syncId, Inventory inventory, Player player) {
        return new AirlockControllerMenu(syncId, player, this);
    }

    public List<AirlockFrameScanner.Result> getLastFrames() {
        return this.lastFrames;
    }

    /** Unseal on BE removal (block broken / replaced). */
    @Override
    public void setRemoved() {
        if (!this.level.isClientSide() && this.lastFrames != null) {
            for (var f : this.lastFrames) unseal(f);
        }
        super.setRemoved();
    }
}