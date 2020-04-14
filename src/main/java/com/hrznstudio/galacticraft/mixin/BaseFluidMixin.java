package com.hrznstudio.galacticraft.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BaseFluid.class)
public abstract class BaseFluidMixin {
    @Redirect(method = "onScheduledTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    private boolean onScheduledTickGC(World world, BlockPos pos, BlockState state, int flags) {
        if (world.getBlockState(pos).getBlock() instanceof FluidDrainable && world.getBlockState(pos).getBlock() instanceof FluidFillable) {
            System.out.println("Ok");
            System.out.println(state.getBlock());
            System.out.println("?//?");
            if (state.isAir()) {
                ((FluidDrainable) world.getBlockState(pos).getBlock()).tryDrainFluid(world, pos, world.getBlockState(pos));
                return true;
            } else {
                ((FluidDrainable) world.getBlockState(pos).getBlock()).tryDrainFluid(world, pos, world.getBlockState(pos));
                ((FluidFillable) world.getBlockState(pos).getBlock()).tryFillWithFluid(world, pos, world.getBlockState(pos), state.getFluidState());
                return true;
            }
        }
        return world.setBlockState(pos, state, flags);
    }
}
