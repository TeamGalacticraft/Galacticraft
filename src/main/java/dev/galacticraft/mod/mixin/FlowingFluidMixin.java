/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.mixin;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.FluidLoggable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMixin {
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1))
    private boolean onScheduledTickFill_gc(Level level, BlockPos blockPos, BlockState blockState, int flags) {
        var blockState1 = level.getBlockState(blockPos);
        if (blockState1.getBlock() instanceof FluidLoggable fillable) {
            fillable.placeLiquid(level, blockPos, blockState1, blockState.getFluidState());
            return true;
        }
        return level.setBlock(blockPos, blockState, flags);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 0))
    private boolean onScheduledTickDrain_gc(Level level, BlockPos blockPos, BlockState blockState, int flags) {
        var blockState1 = level.getBlockState(blockPos);
        if (blockState1.getBlock() instanceof FluidLoggable drainable) {
            drainable.pickupBlock(null, level, blockPos, blockState1);
            level.setBlock(blockPos, blockState1.setValue(FluidLoggable.FLUID, Constant.Misc.EMPTY).setValue(FlowingFluid.LEVEL, 1).setValue(FlowingFluid.FALLING, false), 3);
            return true;
        }
        return level.setBlock(blockPos, blockState, flags);
    }
}
