/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.content.GCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LunarHomeAnchorBlockEntity extends BlockEntity {
    public static final int PROTECTION_RADIUS = 15;
    private BlockState mimicState = GCBlocks.TIN_DECORATION.block().defaultBlockState();
    private boolean needsInitialScan = true;

    public LunarHomeAnchorBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.LUNAR_HOME_ANCHOR, pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, LunarHomeAnchorBlockEntity entity) {
        if (entity.needsInitialScan) {
            entity.needsInitialScan = false;
            entity.scanAndUpdate();
        }
    }

    public BlockState getMimicState() {
        return this.mimicState;
    }

    public void scanAndUpdate() {
        Level level = getLevel();
        if (level == null) return;

        Map<Block, Integer> counts = new HashMap<>();
        BlockPos pos = getBlockPos();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                BlockPos neighborPos = pos.offset(dx, 0, dz);
                BlockState neighbor = level.getBlockState(neighborPos);
                if (!neighbor.isAir() && neighbor.canOcclude()) {
                    counts.merge(neighbor.getBlock(), 1, Integer::sum);
                }
            }
        }

        BlockState newMimic;
        if (counts.isEmpty()) {
            newMimic = GCBlocks.TIN_DECORATION.block().defaultBlockState();
        } else {
            Block mostCommon = Collections.max(counts.entrySet(), Map.Entry.comparingByValue()).getKey();
            newMimic = mostCommon.defaultBlockState();
        }

        if (!this.mimicState.is(newMimic.getBlock())) {
            this.mimicState = newMimic;
            setChanged();
            level.sendBlockUpdated(pos, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registryLookup) {
        super.loadAdditional(tag, registryLookup);
        if (tag.contains("MimicState")) {
            this.mimicState = NbtUtils.readBlockState(registryLookup.lookupOrThrow(Registries.BLOCK), tag.getCompound("MimicState"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registryLookup) {
        super.saveAdditional(tag, registryLookup);
        tag.put("MimicState", NbtUtils.writeBlockState(this.mimicState));
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        return this.saveWithoutMetadata(registryLookup);
    }
}
