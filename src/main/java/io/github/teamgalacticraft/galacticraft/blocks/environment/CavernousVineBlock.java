package io.github.teamgalacticraft.galacticraft.blocks.environment;

import io.github.teamgalacticraft.galacticraft.blocks.GalacticraftBlocks;
import io.github.teamgalacticraft.galacticraft.entity.damage.GalacticraftDamageSource;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

import java.util.Random;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class CavernousVineBlock extends Block implements Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public CavernousVineBlock(Settings settings) {
        super(settings);
        settings.noCollision();
        this.setDefaultState(this.stateFactory.getDefaultState().with(WATERLOGGED, false));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        FluidState fluidState_1 = context.getWorld().getFluidState(context.getBlockPos());
        return super.getPlacementState(context).with(WATERLOGGED, fluidState_1.getFluid() == Fluids.WATER);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState, Direction direction, BlockState neighborBlockState, IWorld world, BlockPos blockPos, BlockPos neighborBlockPos) {
        if (blockState.get(WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(blockState, direction, neighborBlockState, world, blockPos, neighborBlockPos);
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.with(WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        return blockState.get(WATERLOGGED) ? Fluids.WATER.getState(false) : super.getFluidState(blockState);
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
    public boolean canPlaceAt(BlockState state, ViewableWorld viewableWorld, BlockPos pos) {
        BlockPos pos2 = pos;
        BlockPos pos3 = pos;
        pos2 = pos2.add(0, -1, 0);
        pos3 = pos3.add(0, 1, 0);
        //If it isn't on the ground and it is below a block
        return (!viewableWorld.getBlockState(pos3).getBlock().equals(Blocks.AIR)) && (viewableWorld.getBlockState(pos2).getBlock().equals(Blocks.AIR) || viewableWorld.getBlockState(pos2).getBlock().equals(GalacticraftBlocks.CAVERNOUS_VINE_BLOCK));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos pos_2, boolean boolean_1) {
        super.neighborUpdate(state, world, pos, block, pos_2, boolean_1);
        if (!canPlaceAt(state, world, pos)) {
            world.breakBlock(pos, false);
        }
    }

    @Override
    public void onScheduledTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!canPlaceAt(state, world, pos)) {
            world.breakBlock(pos, false);
        }
    }
}
