package dev.galacticraft.mod.content.block.special;

import com.mojang.serialization.MapCodec;
import dev.galacticraft.mod.content.GCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

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

    //  Manages the updates to the torch web to determine if this block is the bottom and therefore shows the torch.
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        // If above changed and it isn't a torch web block (and in theory not a solid block) then it breaks.
        if (direction == Direction.UP && !neighborState.is(GCBlocks.TORCH_WEB)) {
            return Blocks.AIR.defaultBlockState();
        // If below changed and it isn't a torch web then this is the bottom.
        } else if (direction == Direction.DOWN && !neighborState.is(GCBlocks.TORCH_WEB)){
            return defaultBlockState().setValue(BOTTOM, true);
        }
        // No change
        return state;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return this.canAttachTo(level, pos.above(), Direction.DOWN);
    }

    private boolean canAttachTo(BlockGetter world, BlockPos pos, Direction side) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.isFaceSturdy(world, pos, side) || blockState.is(GCBlocks.TORCH_WEB);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> compositeStateBuilder) {
        compositeStateBuilder.add(BOTTOM);
    }
}
