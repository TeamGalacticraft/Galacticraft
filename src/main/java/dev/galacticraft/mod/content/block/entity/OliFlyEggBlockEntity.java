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

import dev.galacticraft.mod.content.GCBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;

public class OliFlyEggBlockEntity extends BlockEntity {
    public static final double TRIGGER_RADIUS = 5.0;
    public static final int REQUIRED_TICKS = 20 * 10; // 10 seconds

    private int proximityTicks = 0;

    public OliFlyEggBlockEntity(BlockPos pos, BlockState state) {
        super(GCBlockEntityTypes.OLI_FLY_EGG, pos, state);
    }

    public void serverTick() {
        if (!(this.level instanceof ServerLevel server)) return;

        Player near = server.getNearestPlayer(
                this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5,
                TRIGGER_RADIUS,
                player -> !player.isSpectator()
        );

        if (near != null) {
            if (proximityTicks < REQUIRED_TICKS) proximityTicks++;
        } else {
            proximityTicks = 0;
        }

        if (proximityTicks >= REQUIRED_TICKS) {
            server.playSound(null, worldPosition, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 0.8f, 0.9f + server.random.nextFloat() * 0.2f);
            server.setBlock(worldPosition, Blocks.REDSTONE_BLOCK.defaultBlockState(), 3); //TODO change from redstone block to spawning an oli fly eventually
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider registryLookup) {
        super.saveAdditional(compound, registryLookup);
        compound.putInt("ProxTicks", proximityTicks);
    }

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider registryLookup) {
        super.loadAdditional(compound, registryLookup);
        this.proximityTicks = compound.getInt("ProxTicks");
    }
}