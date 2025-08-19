/*
 * Copyright (c) 2019-2025 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TorchWebBlock extends Block {

    public static final MapCodec<TorchWebBlock> CODEC = simpleCodec(TorchWebBlock::new);

    public static final BooleanProperty BOTTOM = BlockStateProperties.BOTTOM;

    public TorchWebBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(BOTTOM, false));
    }

    protected static final VoxelShape WEB_VOXEL = Block.box(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
    protected static final VoxelShape TORCH_VOXEL = Block.box(5.0, 3.0, 5.0, 11.0, 16.0, 11.0);

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getValue(BOTTOM)) {
            return TORCH_VOXEL;
        } else {
            return WEB_VOXEL;
        }
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
        } else if (direction == Direction.DOWN && !neighborState.is(GCBlocks.TORCH_WEB)) {
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
