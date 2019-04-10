package io.github.teamgalacticraft.galacticraft.blocks.environment;

import io.github.teamgalacticraft.galacticraft.blocks.GalacticraftBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CavernousVineBlockPoisonous extends CavernousVineBlock {

    public CavernousVineBlockPoisonous(Settings settings) {
        super(settings);
    }

    @Override
    protected void onCollided(LivingEntity entity) {
        // Override the one from CavernousVineBlock and only bring them up. Dont damage.
        dragEntityUp(entity);
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (playerEntity.getStackInHand(hand).getItem() instanceof ShearsItem) {
            world.setBlockState(blockPos, GalacticraftBlocks.CAVERNOUS_VINE_BLOCK.getDefaultState());
            return true;
        }
        return false;
    }
}
