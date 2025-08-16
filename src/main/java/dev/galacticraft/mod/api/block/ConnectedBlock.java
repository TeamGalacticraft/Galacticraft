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

package dev.galacticraft.mod.api.block;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.util.ConnectingBlockUtil;
import dev.galacticraft.mod.util.DirectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ConnectedBlock extends Block {
    public ConnectedBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(BlockStateProperties.NORTH, false)
                .setValue(BlockStateProperties.SOUTH, false)
                .setValue(BlockStateProperties.EAST, false)
                .setValue(BlockStateProperties.WEST, false)
                .setValue(BlockStateProperties.UP, false)
                .setValue(BlockStateProperties.DOWN, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.NORTH).add(BlockStateProperties.SOUTH)
                .add(BlockStateProperties.EAST).add(BlockStateProperties.WEST)
                .add(BlockStateProperties.UP).add(BlockStateProperties.DOWN);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, level, pos, oldState, notify);
        if (!oldState.is(state.getBlock())) {
            BlockState blockState = state;
            for (Direction direction : Constant.Misc.DIRECTIONS) {
                blockState = blockState.setValue(ConnectingBlockUtil.getBooleanProperty(direction), this.connectsTo(level, pos, state, direction, pos.relative(direction)));
            }
            if (blockState != oldState) {
                level.setBlockAndUpdate(pos, blockState);
                if (!level.isClientSide) {
                    for (Direction direction : Constant.Misc.DIRECTIONS) {
                        if (blockState.getValue(ConnectingBlockUtil.getBooleanProperty(direction)) != state.getValue(ConnectingBlockUtil.getBooleanProperty(direction))) {
                            BlockPos neighborPos = pos.relative(direction);
                            this.onConnectionUpdate(((ServerLevel) level), pos, blockState, direction, neighborPos, level.getBlockState(neighborPos));
                        }
                    }
                }
            }
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        assert state != null;
        for (Direction direction : Constant.Misc.DIRECTIONS) {
            state = state.setValue(ConnectingBlockUtil.getBooleanProperty(direction), this.connectsTo(ctx.getLevel(), ctx.getClickedPos(), state, direction, ctx.getClickedPos().relative(direction)));
        }
        return state;
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockState blockState = this.updateConnection(level, pos, state, direction, neighborPos);
        if (state != blockState && level instanceof ServerLevel l) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be != null) {
                this.onConnectionUpdate(l, pos, blockState, direction, neighborPos, neighborState);
            }
        }
        return blockState;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborChanged(state, level, pos, sourceBlock, sourcePos, notify);
        if (!level.isClientSide) {
            Direction dir = DirectionUtil.fromNormal(sourcePos.getX() - pos.getX(), sourcePos.getY() - pos.getY(), sourcePos.getZ() - pos.getZ());
            BlockState sourceState = level.getBlockState(sourcePos);
            BlockState blockState = this.updateShape(state, dir, sourceState, level, pos, sourcePos);
            if (blockState != state) {
                level.setBlock(pos, blockState, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
                this.onConnectionUpdate((ServerLevel) level, pos, blockState, dir, sourcePos, sourceState);
            }
        }
    }

    protected abstract boolean canConnectTo(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, BlockPos neighborPos, BlockState neighborState);
    protected void onConnectionUpdate(ServerLevel level, BlockPos pos, BlockState state, Direction direction, BlockPos neighborPos, BlockState neighborState) {
    }

    protected BlockState updateConnection(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, BlockPos neighborPos) {
        return state.setValue(ConnectingBlockUtil.getBooleanProperty(direction), this.connectsTo(level, pos, state, direction, neighborPos));
    }

    protected boolean connectsTo(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, BlockPos neighborPos) {
        BlockState neighborState = level.getBlockState(neighborPos);
        boolean canConnect = this.canConnectTo(level, pos, state, direction, neighborPos, neighborState);

        if (neighborState.getBlock() instanceof ConnectedBlock neighbor) {
            canConnect &= neighbor.canConnectTo(level, neighborPos, neighborState, direction.getOpposite(), pos, state);
        }
        return canConnect;
    }
}
