package com.hrznstudio.galacticraft.blocks.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CrudeOilBlock extends FluidBlock {
    public CrudeOilBlock(BaseFluid fluid, Settings settings) {
        super(fluid, settings);
    }

    public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
        if (entity instanceof LivingEntity && this.getFluidState(blockState).getFluid().isStill(this.getFluidState(blockState))) {
            if (entity instanceof PlayerEntity) {
                if (((PlayerEntity) entity).isCreative()) {
                    return;
                }
            }
            ((LivingEntity) entity).addPotionEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 6 * 20));
        }
    }


}
