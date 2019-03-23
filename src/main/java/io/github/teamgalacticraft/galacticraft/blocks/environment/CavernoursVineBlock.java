package io.github.teamgalacticraft.galacticraft.blocks.environment;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class CavernoursVineBlock extends Block implements Waterloggable {

    public CavernoursVineBlock(Settings settings) {
        super(settings);
        settings.noCollision();
    }

    @Override
    public void onEntityCollision(BlockState blockState_1, World world_1, BlockPos blockPos_1, Entity entity_1) {
        super.onEntityCollision(blockState_1, world_1, blockPos_1, entity_1);
        entity_1.damage(DamageSource.CACTUS, 5.0f);
    }

}
