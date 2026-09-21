/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

import dev.galacticraft.mod.content.block.entity.decoration.CannedFoodBlockEntity;
import dev.galacticraft.mod.content.item.CannedFoodItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static dev.galacticraft.mod.content.item.GCItems.EMPTY_CAN;
import static net.minecraft.world.level.block.Blocks.AIR;

public class CannedFoodBlock extends Block implements EntityBlock {
    public static final float[][][] POSITIONS = {
            {},
            {{8, 0, 8}},
            {{4, 0, 8}, {12, 0, 8}},
            {{4, 0, 4}, {12, 0, 6}, {6, 0, 12}},
            {{4, 0, 4}, {12, 0, 4}, {4, 0, 12}, {12, 0, 12}},
            {{4, 0, 4}, {12, 0, 4}, {4, 0, 12}, {12, 0, 12}, {8, 8, 8}},
            {{4, 0, 4}, {12, 0, 4}, {4, 0, 12}, {12, 0, 12}, {4, 8, 8}, {12, 8, 8}},
            {{4, 0, 4}, {12, 0, 4}, {4, 0, 12}, {12, 0, 12}, {4, 8, 4}, {12, 8, 6}, {6, 8, 12}},
            {{4, 0, 4}, {12, 0, 4}, {4, 0, 12}, {12, 0, 12}, {4, 8, 4}, {12, 8, 4}, {4, 8, 12}, {12, 8, 12}}
    };

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty COUNT = IntegerProperty.create("count", 1, 8);

    public CannedFoodBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(COUNT, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(COUNT);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite())
                .setValue(COUNT, 1);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CannedFoodBlockEntity(pos, state);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape shape = Shapes.empty();
        Direction direction = state.getValue(FACING);
        float a = direction.getStepX();
        float b = direction.getStepZ();

        for (float[] position : CannedFoodBlock.POSITIONS[state.getValue(COUNT)]) {
            float x = a * (position[2] - 8) + b * (position[0] - 8);
            float y = position[1];
            float z = b * (position[2] - 8) - a * (position[0] - 8);

            shape = Shapes.join(shape, Block.box(x + 5, y, z + 5, x + 11, y + 8, z + 11), BooleanOp.OR);
        }

        return shape;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof CannedFoodBlockEntity blockEntity) {
                blockEntity.dropStoredCans(level, pos);
            }
            super.onRemove(state, level, pos, newState, moved);
        }
    }

    @Override
    protected boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        BlockPos below = blockPos.below();
        BlockState belowState = levelReader.getBlockState(below);
        return belowState.isFaceSturdy(levelReader, below, Direction.UP) || (belowState.getBlock() instanceof CannedFoodBlock && belowState.getValue(COUNT) == 8);
    }

    @Override
    protected BlockState updateShape(BlockState blockState, Direction direction, BlockState neighborState, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos neighborPos) {
        if (!blockState.canSurvive(levelAccessor, blockPos)) {
            return AIR.defaultBlockState();
        }
        return super.updateShape(blockState, direction, neighborState, levelAccessor, blockPos, neighborPos);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos pos, BlockState state) {
        if (levelReader.getBlockEntity(pos) instanceof CannedFoodBlockEntity blockEntity) {
            for (ItemStack stack : blockEntity.getCanContents()) {
                if (CannedFoodItem.getSize(stack) > 0) {
                    return stack.copy();
                }
            }
        }

        return EMPTY_CAN.getDefaultInstance();
    }
}
