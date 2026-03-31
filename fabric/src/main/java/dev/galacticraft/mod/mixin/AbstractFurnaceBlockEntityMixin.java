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

package dev.galacticraft.mod.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin extends BlockEntity {
    AbstractFurnaceBlockEntityMixin() {
        super(null, null, null);
    }

    @Inject(method = "serverTick", at = @At("HEAD"))
    private static void gc$extinguishFurnace(Level level, BlockPos blockPos, BlockState blockState, AbstractFurnaceBlockEntity be, CallbackInfo ci) {
        if (be.litTime > 0 && gc$shouldExtinguish(level, blockPos, blockState)) {
            be.litTime = 0;
            blockState = blockState.setValue(AbstractFurnaceBlock.LIT, false);
            level.setBlock(blockPos, blockState, Block.UPDATE_ALL);
            AbstractFurnaceBlockEntity.setChanged(level, blockPos, blockState);
            RandomSource randomSource = level.getRandom();
            level.playSound(null, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.25F, 2.6F + (randomSource.nextFloat() - randomSource.nextFloat()) * 0.8F);
        }
    }

    @ModifyExpressionValue(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;canBurn(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/crafting/RecipeHolder;Lnet/minecraft/core/NonNullList;I)Z"))
    private static boolean gc$canBurn(boolean original, Level level, BlockPos blockPos, BlockState blockState, AbstractFurnaceBlockEntity be) {
        if (gc$shouldExtinguish(level, blockPos, blockState)) {
            return false;
        }
        return original;
    }

    @Unique
    private static boolean gc$shouldExtinguish(Level level, BlockPos blockPos, BlockState blockState) {
        return !level.isBreathable(blockPos.relative(blockState.getValue(AbstractFurnaceBlock.FACING)))
                && !level.isBreathable(blockPos);
    }
}