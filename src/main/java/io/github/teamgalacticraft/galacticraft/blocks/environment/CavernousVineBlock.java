package io.github.teamgalacticraft.galacticraft.blocks.environment;

import io.github.teamgalacticraft.galacticraft.blocks.GalacticraftBlocks;
import io.github.teamgalacticraft.galacticraft.entity.damage.GCDamageSource;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
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
        entity_1.damage(GCDamageSource.VINE_POISON, 5.0f);
        entity_1.setVelocity(entity_1.getVelocity().x, 0.15D, entity_1.getVelocity().z);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        BlockPos pos2 = pos;
        BlockPos pos3 = pos;
        pos2 = pos2.add(0, -1, 0);
        pos3 = pos3.add(0, 1, 0);
        if (!world.getBlockState(pos2).getBlock().equals(Blocks.AIR) && !world.getBlockState(pos2).getBlock().equals(GalacticraftBlocks.CAVERNOUS_VINE_BLOCK)) { //If on ground
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
        if (world.getBlockState(pos3).getBlock().equals(Blocks.AIR)) { //If not below block
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }
}
