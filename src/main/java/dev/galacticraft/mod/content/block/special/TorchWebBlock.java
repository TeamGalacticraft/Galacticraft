package dev.galacticraft.mod.content.block.special;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.state.BlockBehaviour.simpleCodec;

public class TorchWebBlock extends Block {

    public static final MapCodec<TorchWebBlock> CODEC = simpleCodec(TorchWebBlock::new);

    public static final BooleanProperty BOTTOM = BlockStateProperties.BOTTOM;

    public TorchWebBlock(BlockBehaviour.Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(BOTTOM, false));
    }

    @Override
    public MapCodec<TorchWebBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos above = ctx.getClickedPos().above();
        BlockState aboveState = ctx.getLevel().getBlockState(above);

        if (aboveState.getBlock() instanceof TorchWebBlock) {
            if (!ctx.getLevel().isClientSide()) {
                ctx.getLevel().setBlock(above, aboveState.setValue(BOTTOM, false), Block.UPDATE_ALL);
            }
        }

        return defaultBlockState().setValue(BOTTOM, true);
    }

    @Override
    public void onStateReplaced(WorldAccess world, BlockPos pos, BlockState state) {
        BlockState aboveState = world.getBlockState(pos.up()); // get blockstate of the block underneath
        if(aboveState.isOf(WWBlocks.HANGING_VINES)) { // if it's a hanging vines block
            world.setBlockState(pos.up(), aboveState.with(END, true), Block.NOTIFY_ALL);
        }
    }

    @Override
    protected BlockState getStateForNeighborUpdate(
            BlockState state,
            WorldView world,
            ScheduledTickView tickView,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            Random random
    ) {
        if (!state.canPlaceAt(world, pos)) {
            tickView.scheduleBlockTick(pos, this, 1);
        }

        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState aboveState = world.getBlockState(pos.up());
        return aboveState.isFullCube(world,pos.up()) || aboveState.isOf(WWBlocks.HANGING_VINES);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(END);
    }
}
