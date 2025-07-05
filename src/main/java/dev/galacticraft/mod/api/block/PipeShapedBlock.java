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

import dev.galacticraft.mod.api.block.entity.Connected;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PipeShapedBlock<BE extends BlockEntity & Connected> extends Block implements EntityBlock {
    public final VoxelShape[] shapes;

    protected PipeShapedBlock(float radius, BlockBehaviour.Properties properties) {
        super(properties);
        this.shapes = makeShapes(radius);
    }

    @Override
    public abstract @Nullable BE newBlockEntity(BlockPos pos, BlockState state);

    public abstract boolean canConnectTo(Level level, BlockPos thisPos, Direction direction, BlockPos neighborPos, BlockState thisState);

    protected abstract void onConnectionChanged(Level level, BlockPos thisPos, Direction direction, BlockPos neighborPos);

    protected boolean updateConnection(BlockState currentState, BlockPos pos, Direction side, BlockPos neighborPos, Level level) {
        if (level.getBlockEntity(pos) instanceof Connected pipe) {
            boolean canConnect = this.canConnectTo(level, pos, side, neighborPos, currentState);

            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof PipeShapedBlock<?> neighbor) {
                canConnect &= neighbor.canConnectTo(level, neighborPos, side.getOpposite(), pos, neighborState);
            }

            boolean currentlyConnected = pipe.getConnections()[side.get3DDataValue()];
            pipe.getConnections()[side.get3DDataValue()] = canConnect;
            level.sendBlockUpdated(pos, currentState, currentState, Block.UPDATE_IMMEDIATE);
            return canConnect != currentlyConnected;
        } else {
            return false;
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, placer, itemStack);

        for (Direction direction : Direction.values()) {
            this.updateConnection(state, pos, direction, pos.relative(direction), level);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean notify) {
        super.neighborChanged(state, level, pos, block, neighborPos, notify);

        Direction direction = Direction.fromDelta(neighborPos.getX() - pos.getX(), neighborPos.getY() - pos.getY(), neighborPos.getZ() - pos.getZ());
        if (direction == null)
            return;

        if (this.updateConnection(state, pos, direction, neighborPos, level)) {
            this.onConnectionChanged(level, pos, direction, neighborPos);
        }
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor levelAccessor, BlockPos pos, BlockPos neighborPos) {
        if (levelAccessor instanceof Level level) {
            if (this.updateConnection(state, pos, direction, neighborPos, level)) {
                this.onConnectionChanged(level, pos, direction, neighborPos);
            }
        }

        return state;
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (world.getBlockEntity(pos) instanceof Connected connected) {
            return this.shapes[generateAABBIndex(connected)];
        }
        return this.shapes[0];
    }

    public static VoxelShape[] makeShapes(float radius) {
        Direction[] directions = Direction.values();

        float f = 0.5F - radius;
        float g = 0.5F + radius;
        VoxelShape voxelShape = Block.box(
                f * 16.0F, f * 16.0F, f * 16.0F, g * 16.0F, g * 16.0F, g * 16.0F
        );
        VoxelShape[] voxelShapes = new VoxelShape[directions.length];

        for (int i = 0; i < directions.length; i++) {
            Direction direction = directions[i];
            voxelShapes[i] = Shapes.box(
                    0.5 + Math.min(-radius, (double)direction.getStepX() * 0.5),
                    0.5 + Math.min(-radius, (double)direction.getStepY() * 0.5),
                    0.5 + Math.min(-radius, (double)direction.getStepZ() * 0.5),
                    0.5 + Math.max(radius, (double)direction.getStepX() * 0.5),
                    0.5 + Math.max(radius, (double)direction.getStepY() * 0.5),
                    0.5 + Math.max(radius, (double)direction.getStepZ() * 0.5)
            );
        }

        VoxelShape[] voxelShapes2 = new VoxelShape[64];

        for (int j = 0; j < 64; j++) {
            VoxelShape voxelShape2 = voxelShape;

            for (int k = 0; k < directions.length; k++) {
                if ((j & 1 << k) != 0) {
                    voxelShape2 = Shapes.or(voxelShape2, voxelShapes[k]);
                }
            }

            voxelShapes2[j] = voxelShape2;
        }

        return voxelShapes2;
    }

    public static int generateAABBIndex(Connected connected) {
        int i = 0;

        Direction[] directions = Direction.values();
        for (int j = 0; j < directions.length; j++) {
            if (connected.getConnections()[directions[j].ordinal()]) {
                i |= 1 << j;
            }
        }

        return i;
    }
}
