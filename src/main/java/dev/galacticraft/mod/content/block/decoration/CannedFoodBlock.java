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

package dev.galacticraft.mod.content.block.decoration;

import dev.galacticraft.mod.client.model.CannedFoodBakedModel;
import dev.galacticraft.mod.content.block.entity.decoration.CannedFoodBlockEntity;
import dev.galacticraft.mod.content.item.CannedFoodItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dev.galacticraft.mod.content.item.GCItems.EMPTY_CAN;

public class CannedFoodBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public CannedFoodBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CannedFoodBlockEntity(pos, state);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape shape = Shapes.empty();
        if (world.getBlockEntity(pos) instanceof CannedFoodBlockEntity cannedFoodBlockEntity) {
            Direction direction = state.getValue(FACING);
            float a = direction.getStepX();
            float b = direction.getStepZ();

            for (float[] position : CannedFoodBakedModel.POSITIONS[cannedFoodBlockEntity.getCanCount()]) {
                float x = a * (position[2] - 8) + b * (position[0] - 8);
                float y = position[1];
                float z = b * (position[2] - 8) - a * (position[0] - 8);

                shape = Shapes.join(shape, Block.box(x + 5, y, z + 5, x + 11, y + 8, z + 11), BooleanOp.OR);
            }
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
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos pos, BlockState state) {
        if (levelReader.isClientSide() && levelReader.getBlockEntity(pos) instanceof CannedFoodBlockEntity blockEntity) {
            HitResult hitResult = Minecraft.getInstance().hitResult;
            if (hitResult != null) {
                Vec3 location = hitResult.getLocation();
                double minDist = 1.0D;

                Direction direction = state.getValue(FACING);
                float a = direction.getStepX();
                float b = direction.getStepZ();

                ItemStack itemStack = ItemStack.EMPTY;
                List<ItemStack> canContents = blockEntity.getCanContents();
                int canCount = canContents.size();
                for (int i = 0; i < canCount; i++) {
                    float[] position = CannedFoodBakedModel.POSITIONS[canCount][i];
                    double x = pos.getX() + (a * (position[2] - 8) + b * (position[0] - 8) + 5) / 16.0D;
                    double y = pos.getY() + position[1] / 16.0D;
                    double z = pos.getZ() + (b * (position[2] - 8) - a * (position[0] - 8) + 5) / 16.0D;

                    AABB shape = new AABB(x, y, z, x + 0.375D, y + 0.5D, z + 0.375D);
                    double dist = shape.distanceToSqr(location);
                    if (dist < minDist) {
                        minDist = dist;
                        itemStack = canContents.get(i);
                    }
                }

                if (CannedFoodItem.getSize(itemStack) > 0) {
                    return itemStack;
                }
            }
        }
        return EMPTY_CAN.getDefaultInstance();
    }
}
