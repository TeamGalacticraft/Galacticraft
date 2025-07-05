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

package dev.galacticraft.mod.content;

import dev.galacticraft.api.registry.ExtinguishableBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class GCExtinguishable {
    public static void register() {
        // Normal Fire
        ExtinguishableBlockRegistry.INSTANCE.add(Blocks.LANTERN,
                state -> GCBlocks.UNLIT_LANTERN.defaultBlockState()
                        .setValue(LanternBlock.HANGING, state.getValue(LanternBlock.HANGING))
                        .setValue(LanternBlock.WATERLOGGED, state.getValue(LanternBlock.WATERLOGGED)),
                GCExtinguishable::extinguishLantern
        );
        ExtinguishableBlockRegistry.INSTANCE.add(Blocks.TORCH,
                state -> GCBlocks.UNLIT_TORCH.defaultBlockState(),
                GCExtinguishable::extinguishTorch
        );
        ExtinguishableBlockRegistry.INSTANCE.add(Blocks.WALL_TORCH,
                state -> GCBlocks.UNLIT_WALL_TORCH.defaultBlockState()
                        .setValue(WallTorchBlock.FACING, state.getValue(WallTorchBlock.FACING)),
                GCExtinguishable::extinguishTorch
        );
        ExtinguishableBlockRegistry.INSTANCE.add(Blocks.CAMPFIRE,
                state -> state.getValue(BlockStateProperties.LIT) ? state.setValue(BlockStateProperties.LIT, false) : null,
                GCExtinguishable::extinguishCampfire
        );
        ExtinguishableBlockRegistry.INSTANCE.add(Blocks.FIRE, Blocks.AIR.defaultBlockState(), GCExtinguishable::extinguishFire);

        // Soul Fire
        ExtinguishableBlockRegistry.INSTANCE.add(Blocks.SOUL_LANTERN,
                state -> GCBlocks.UNLIT_SOUL_LANTERN.defaultBlockState()
                        .setValue(LanternBlock.HANGING, state.getValue(LanternBlock.HANGING))
                        .setValue(LanternBlock.WATERLOGGED, state.getValue(LanternBlock.WATERLOGGED)),
                GCExtinguishable::extinguishLantern
        );
        ExtinguishableBlockRegistry.INSTANCE.add(Blocks.SOUL_TORCH,
                state -> GCBlocks.UNLIT_SOUL_TORCH.defaultBlockState(),
                GCExtinguishable::extinguishTorch
        );
        ExtinguishableBlockRegistry.INSTANCE.add(Blocks.SOUL_WALL_TORCH,
                state -> GCBlocks.UNLIT_SOUL_WALL_TORCH.defaultBlockState()
                        .setValue(WallTorchBlock.FACING, state.getValue(WallTorchBlock.FACING)),
                GCExtinguishable::extinguishTorch
        );
        ExtinguishableBlockRegistry.INSTANCE.add(Blocks.SOUL_CAMPFIRE,
                state -> state.getValue(BlockStateProperties.LIT) ? state.setValue(BlockStateProperties.LIT, false) : null,
                GCExtinguishable::extinguishCampfire
        );
        ExtinguishableBlockRegistry.INSTANCE.add(Blocks.SOUL_FIRE, Blocks.AIR.defaultBlockState(), GCExtinguishable::extinguishFire);

        // Candles
        ExtinguishableBlockRegistry.INSTANCE.add(BlockTags.CANDLES,
                state -> state.getValue(BlockStateProperties.LIT) ? state.setValue(BlockStateProperties.LIT, false) : null,
                GCExtinguishable::extinguishCandle
        );
        ExtinguishableBlockRegistry.INSTANCE.add(BlockTags.CANDLE_CAKES,
                state -> state.getValue(BlockStateProperties.LIT) ? state.setValue(BlockStateProperties.LIT, false) : null,
                GCExtinguishable::extinguishCandle
        );
    }

    public static void extinguishLantern(ExtinguishableBlockRegistry.Context context) {
        if (context.level() instanceof ServerLevel level) {
            BlockPos pos = context.pos();
            BlockState state = context.state();
            double x = pos.getX() + 0.5D;
            double y = pos.getY() + (state.getValue(LanternBlock.HANGING) ? 0.25D : 0.1875D);
            double z = pos.getZ() + 0.5D;

            for (Direction direction : Direction.Plane.HORIZONTAL) {
                level.sendParticles(ParticleTypes.SMOKE, x + 0.27 * (double) direction.getStepX(), y, z + 0.27 * (double) direction.getStepZ(), 0, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public static void extinguishTorch(ExtinguishableBlockRegistry.Context context) {
        if (context.level() instanceof ServerLevel level) {
            BlockPos pos = context.pos();
            double x = pos.getX() + 0.5D;
            double y = pos.getY() + 0.92D;
            double z = pos.getZ() + 0.5D;

            BlockState state = context.state();
            Direction direction = state.getBlock() instanceof WallTorchBlock ? state.getValue(WallTorchBlock.FACING).getOpposite() : Direction.UP;
            level.sendParticles(ParticleTypes.SMOKE, x + 0.27 * (double) direction.getStepX(), y, z + 0.27 * (double) direction.getStepZ(), 0, 0.0D, 0.0D, 0.0D, 0.0D);
            level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public static void extinguishCandle(ExtinguishableBlockRegistry.Context context) {
        if (context.state().getBlock() instanceof AbstractCandleBlock candle) {
            BlockPos pos = context.pos();
            if (context.level() instanceof ServerLevel level) {
                candle.getParticleOffsets(context.state()).forEach(vec3 ->
                        level.sendParticles(ParticleTypes.SMOKE, (double) pos.getX() + vec3.x(), (double) pos.getY() + vec3.y(), (double) pos.getZ() + vec3.z(), 0, 0.0D, 0.1D, 0.0D, 0.0D)
                );
            }
            context.level().playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public static void extinguishCampfire(ExtinguishableBlockRegistry.Context context) {
        context.level().playSound(null, context.pos(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.2F, 1.0F);
    }

    public static void extinguishFire(ExtinguishableBlockRegistry.Context context) {
        RandomSource randomSource = context.level().getRandom();
        context.level().playSound(null, context.pos(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.25F, 2.6F + (randomSource.nextFloat() - randomSource.nextFloat()) * 0.8F);
    }
}
