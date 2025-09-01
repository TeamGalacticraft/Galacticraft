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

package dev.galacticraft.impl.internal.mixin.oxygen.extinguish;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.galacticraft.api.accessor.GCBlockExtensions;
import dev.galacticraft.mod.content.GCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LanternBlock.class)
public class LanternBlockMixin implements GCBlockExtensions {
    @WrapMethod(method = "getStateForPlacement")
    private BlockState extinguishNoAir(BlockPlaceContext ctx, Operation<BlockState> original) {
        BlockState state = original.call(ctx);
        if (state != null && !ctx.getLevel().galacticraft$isBreathable(ctx.getClickedPos())) {
            if (state.getBlock() == Blocks.LANTERN) {
                return GCBlocks.UNLIT_LANTERN.withPropertiesOf(state);
            } else if (state.getBlock() == Blocks.SOUL_LANTERN) {
                return GCBlocks.UNLIT_SOUL_LANTERN.withPropertiesOf(state);
            }
        }
        return state;
    }

    @Override
    public boolean galacticraft$hasLegacyExtinguishTransform(BlockState state) {
        return true;
    }

    @Override
    public BlockState galacticraft$extinguishBlockPlace(BlockPos pos, BlockState state) {
        if (state.getBlock() == Blocks.LANTERN) {
            return GCBlocks.UNLIT_LANTERN.withPropertiesOf(state);
        } else if (state.getBlock() == Blocks.SOUL_LANTERN) {
            return GCBlocks.UNLIT_SOUL_LANTERN.withPropertiesOf(state);
        }
        return state;
    }

    @Override
    public boolean galacticraft$hasAtmosphereListener(BlockState state) {
        return true;
    }

    @Override
    public void galacticraft$onAtmosphereChange(ServerLevel level, BlockPos pos, BlockState state, boolean breathable) {
        if (!breathable) {
            if (state.getBlock() == Blocks.LANTERN || state.getBlock() == Blocks.SOUL_LANTERN) {
                double x = pos.getX() + 0.5D;
                double y = pos.getY() + (state.getValue(LanternBlock.HANGING) ? 0.25D : 0.1875D);
                double z = pos.getZ() + 0.5D;

                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    level.sendParticles(ParticleTypes.SMOKE, x + 0.27 * (double) direction.getStepX(), y, z + 0.27 * (double) direction.getStepZ(), 0, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);

                if (state.getBlock() == Blocks.LANTERN) {
                    level.setBlock(pos, GCBlocks.UNLIT_LANTERN.withPropertiesOf(state), Block.UPDATE_ALL_IMMEDIATE);
                } else if (state.getBlock() == Blocks.SOUL_LANTERN) {
                    level.setBlock(pos, GCBlocks.UNLIT_SOUL_LANTERN.withPropertiesOf(state), Block.UPDATE_ALL_IMMEDIATE);
                }
            }
        }
    }
}
