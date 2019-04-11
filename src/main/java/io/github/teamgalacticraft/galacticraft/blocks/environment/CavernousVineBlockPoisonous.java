package io.github.teamgalacticraft.galacticraft.blocks.environment;

import io.github.teamgalacticraft.galacticraft.blocks.GalacticraftBlocks;
import io.github.teamgalacticraft.galacticraft.entity.damage.GalacticraftDamageSource;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class CavernousVineBlockPoisonous extends CavernousVineBlock {

    public CavernousVineBlockPoisonous(Settings settings) {
        super(settings);
    }

    @Override
    protected void onCollided(LivingEntity entity) {
        // Override the one from CavernousVineBlock and only bring them up. Apply damage and rotate player.
        dragEntityUp(entity);
        entity.damage(GalacticraftDamageSource.VINE_POISON, 5.0f);
        entity.yaw += 0.4F; // Spin the player
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
