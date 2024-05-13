/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.content.block.decoration;

import dev.galacticraft.mod.Constant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VacuumGlassBlock extends Block {
    public VacuumGlassBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.X)
                .setValue(BlockStateProperties.NORTH, false)
                .setValue(BlockStateProperties.EAST, false)
                .setValue(BlockStateProperties.SOUTH, false)
                .setValue(BlockStateProperties.WEST, false)
                .setValue(BlockStateProperties.UP, false)
                .setValue(BlockStateProperties.DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.HORIZONTAL_AXIS, BlockStateProperties.NORTH, BlockStateProperties.EAST, BlockStateProperties.SOUTH, BlockStateProperties.WEST, BlockStateProperties.UP, BlockStateProperties.DOWN);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborChanged(state, level, pos, sourceBlock, sourcePos, notify);
        Direction direction = Direction.fromDelta(sourcePos.getX() - pos.getX(), sourcePos.getY() - pos.getY(), sourcePos.getZ() - pos.getZ());
        BlockState neighbor = level.getBlockState(sourcePos);
        assert direction != null;
        boolean connect = neighbor.getBlock() == this;
        if (connect && direction.getAxis().isVertical()) {
            connect = (neighbor.getValue(BlockStateProperties.NORTH) == state.getValue(BlockStateProperties.NORTH)
                    && neighbor.getValue(BlockStateProperties.EAST) == state.getValue(BlockStateProperties.EAST)
                    && neighbor.getValue(BlockStateProperties.SOUTH) == state.getValue(BlockStateProperties.SOUTH)
                    && neighbor.getValue(BlockStateProperties.WEST) == state.getValue(BlockStateProperties.WEST)) || (calculateAxis(state) != null && calculateAxis(state) == calculateAxis(neighbor));
        }

        BlockState state1 = state.setValue(propertyFromDirection(direction), connect);
        Direction.Axis axis = calculateAxis(state);
        if (axis != null) state1 = state1.setValue(BlockStateProperties.HORIZONTAL_AXIS, axis);

        level.setBlockAndUpdate(pos, state1);
    }

    private static @Nullable Direction.Axis calculateAxis(BlockState neighbor) {
        if (neighbor.getValue(BlockStateProperties.NORTH) || neighbor.getValue(BlockStateProperties.SOUTH)) {
            if (!(neighbor.getValue(BlockStateProperties.EAST) || neighbor.getValue(BlockStateProperties.WEST))) {
                return Direction.Axis.Z;
            }
        } else if (neighbor.getValue(BlockStateProperties.EAST) || neighbor.getValue(BlockStateProperties.WEST)) {
            return Direction.Axis.X;
        } else {
            return neighbor.getValue(BlockStateProperties.HORIZONTAL_AXIS);
        }
        return null;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return super.getShape(state, world, pos, context);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        Level level = ctx.getLevel();
        for (Direction direction : Constant.Misc.HORIZONTALS) {
            BlockState state = level.getBlockState(pos.setWithOffset(ctx.getClickedPos(), direction));
            if (state.getBlock() == this) {
                return super.getStateForPlacement(ctx).setValue(propertyFromDirection(direction), true);
            }
        }
        return super.getStateForPlacement(ctx).setValue(BlockStateProperties.HORIZONTAL_AXIS, ctx.getHorizontalDirection().getAxis() == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);
    }

    private static BooleanProperty propertyFromDirection(@NotNull Direction direction) {
        return switch (direction) {
            case UP -> BlockStateProperties.UP;
            case DOWN -> BlockStateProperties.DOWN;
            case NORTH -> BlockStateProperties.NORTH;
            case EAST -> BlockStateProperties.EAST;
            case SOUTH -> BlockStateProperties.SOUTH;
            case WEST -> BlockStateProperties.WEST;
        };
    }
}
