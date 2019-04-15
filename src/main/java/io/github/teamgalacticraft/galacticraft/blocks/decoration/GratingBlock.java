package io.github.teamgalacticraft.galacticraft.blocks.decoration;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Hand;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GratingBlock extends Block implements Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    protected static final EnumProperty<GratingState> GRATING_STATE = EnumProperty.create("grating_state", GratingState.class);

    public enum GratingState implements StringRepresentable {
        UPPER("upper"),
        LOWER("lower");

        GratingState(String name) {
            this.name = name;
        }

        private String name;

        @Override
        public String asString() {
            return this.name;
        }
    }


    public GratingBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateFactory.getDefaultState().with(WATERLOGGED, false).with(GRATING_STATE, GratingState.UPPER));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
        return blockState.get(GRATING_STATE) == GratingState.UPPER ?
                Block.createCuboidShape(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D) :
                Block.createCuboidShape(0.0D, 6.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
        BlockState blockState = this.getDefaultState().with(GRATING_STATE, GratingState.LOWER).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
        BlockPos blockPos = context.getBlockPos();
        Direction direction = context.getFacing();

        return direction != Direction.DOWN && (direction == Direction.UP || context.getPos().y - (double)blockPos.getY() <= 0.5D) ? blockState : blockState.with(GRATING_STATE, GratingState.UPPER);
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
        builder.with(GRATING_STATE);
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        return blockState.get(WATERLOGGED) ? Fluids.WATER.getState(false) : super.getFluidState(blockState);
    }

    @Override
    public boolean activate(BlockState blockState_1, World world_1, BlockPos blockPos_1, PlayerEntity playerEntity_1, Hand hand_1, BlockHitResult blockHitResult_1) {
        System.out.println(blockState_1.get(GRATING_STATE).asString());
        return true;
    }
}
