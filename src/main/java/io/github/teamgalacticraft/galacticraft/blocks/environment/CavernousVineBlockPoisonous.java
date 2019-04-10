package io.github.teamgalacticraft.galacticraft.blocks.environment;

import io.github.teamgalacticraft.galacticraft.blocks.GalacticraftBlocks;
import io.github.teamgalacticraft.galacticraft.entity.damage.GalacticraftDamageSource;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
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
    public void onEntityCollision(BlockState blockState_1, World world_1, BlockPos blockPos_1, Entity entity_1) {
        entity_1.damage(GalacticraftDamageSource.VINE_POISON, 5.0f);
        if (entity_1.getVelocity().y < 0.15) {
            entity_1.addVelocity(0, 0.2D, 0);
        } else {
            entity_1.setVelocity(0, 0.15D, 0);
        }
    }

    @Override
    public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
        if (hand == Hand.MAIN && (playerEntity.getActiveItem().getItem() instanceof ShearsItem)) {
            world.setBlockState(blockPos, GalacticraftBlocks.CAVERNOUS_VINE_BLOCK.getDefaultState());
            return true;
        }
        return false;
    }
}
