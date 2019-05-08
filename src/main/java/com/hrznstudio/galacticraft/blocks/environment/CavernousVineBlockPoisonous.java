package com.hrznstudio.galacticraft.blocks.environment;

import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import com.hrznstudio.galacticraft.entity.damage.GalacticraftDamageSource;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ShearsItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CavernousVineBlockPoisonous extends CavernousVineBlock {

    public CavernousVineBlockPoisonous(Settings settings) {
        super(settings);
    }

    @Override
    public void onCollided(LivingEntity entity) {
        // Override the one from CavernousVineBlock and only bring them up. Apply damage and rotate player.
        dragEntityUp(entity);
        entity.damage(GalacticraftDamageSource.VINE_POISON, 5.0f);
        entity.yaw += 0.4F; // Spin the player
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (playerEntity.getStackInHand(hand).getItem() instanceof ShearsItem) {
            world.setBlockState(blockPos, GalacticraftBlocks.CAVERNOUS_VINE.getDefaultState().with(VINES, blockState.get(VINES)));
            world.playSound(blockPos.getX(), blockPos.getY(), blockPos.getZ(), SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, 1f, 1f, true);
            return true;
        }
        return false;
    }
}
