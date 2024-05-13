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

import dev.galacticraft.mod.api.block.FluidLoggable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin extends Item {
    @Shadow
    @Final
    private Fluid content;

    @Shadow
    protected abstract void playEmptySound(@Nullable Player player, LevelAccessor levelAccessor, BlockPos blockPos);

    BucketItemMixin() {
        super(null);
    }

    @Inject(method = "emptyContents", cancellable = true, at = @At("HEAD"))
    private void emptyFluidLoggable_gc(@Nullable Player player, Level level, BlockPos blockPos, @Nullable BlockHitResult blockHitResult, CallbackInfoReturnable<Boolean> info) {
        var blockState = level.getBlockState(blockPos);
        var block = blockState.getBlock();

        if (block instanceof FluidLoggable fluidLoggable) {
            fluidLoggable.placeLiquid(level, blockPos, blockState, ((FlowingFluid) this.content).getSource(false));
            this.playEmptySound(player, level, blockPos);
            info.setReturnValue(true);
        }
    }

    @Inject(method = "use", at = @At(value = "INVOKE", target = "net/minecraft/world/level/block/BucketPickup.getPickupSound()Ljava/util/Optional;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void getPickupSoundFluidLoggable_gc(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> info, ItemStack itemStack, BlockHitResult blockHitResult, BlockPos blockPos, Direction direction, BlockPos blockPos2, BlockState blockState, BucketPickup bucketPickup, ItemStack itemStack2) {
        if (blockState.getBlock() instanceof FluidLoggable) {
            BuiltInRegistries.FLUID.get(blockState.getValue(FluidLoggable.FLUID)).getPickupSound().ifPresent(soundEvent -> player.playSound(soundEvent, 1.0F, 1.0F));
        }
    }

    @ModifyVariable(method = "emptyContents", at = @At("STORE"),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "net/minecraft/world/level/block/state/BlockState.canBeReplaced(Lnet/minecraft/world/level/material/Fluid;)Z")),
            index = 9, ordinal = 1)
    private boolean checkIfFluidLoggable_gc(boolean defaultValue, @Nullable Player player, Level level, BlockPos blockPos, @Nullable BlockHitResult blockHitResult) {
        var blockState = level.getBlockState(blockPos);
        var block = blockState.getBlock();
        return defaultValue || block instanceof FluidLoggable && ((FluidLoggable) block).canPlaceLiquid(player, level, blockPos, blockState, this.content);
    }

    @ModifyVariable(method = "use", at = @At("STORE"),
            slice = @Slice(
                    from = @At(value = "FIELD", target = "net/minecraft/world/level/material/Fluids.WATER:Lnet/minecraft/world/level/material/FlowingFluid;")),
            index = 10, ordinal = 2)
    private BlockPos checkIfFluidLoggableOnUse_gc(BlockPos defaultValue, Level level, Player player, InteractionHand interactionHand) {
        var blockHitResult = getPlayerPOVHitResult(level, player, this.content == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
        var blockPos = blockHitResult.getBlockPos();
        var blockState = level.getBlockState(blockPos);
        return blockState.getBlock() instanceof FluidLoggable ? blockPos : defaultValue;
    }
}