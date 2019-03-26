package io.github.teamgalacticraft.galacticraft.blocks.environment;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class CavernousVineBlock extends Block implements Waterloggable {

    public CavernousVineBlock(Settings settings) {
        super(settings);
        settings.noCollision();
    }

    @Override
    public void onEntityCollision(BlockState blockState_1, World world_1, BlockPos blockPos_1, Entity entity_1) {
        super.onEntityCollision(blockState_1, world_1, blockPos_1, entity_1);
        entity_1.damage(DamageSource.CACTUS, 5.0f);
        entity_1.addVelocity(0.0D, 0.1D, 0.0D);
    }

    @Override
    public void onBlockAdded(BlockState state_1, World world, BlockPos pos, BlockState state_2) {
        BlockPos pos2 = pos;
        BlockPos pos3 = pos;
        pos2 = pos2.add(0, -1, 0);
        pos3 = pos3.add(0, 1, 0);
        if (!world.getBlockState(pos2).getBlock().equals(Blocks.AIR)) { //If on ground
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
        if (world.getBlockState(pos3).getBlock().equals(Blocks.AIR)) { //If not below block
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }
}
