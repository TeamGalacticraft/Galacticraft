/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.mod.content.block.special.walkway;

import dev.galacticraft.mod.api.block.FluidLoggable;
import dev.galacticraft.mod.api.block.entity.Walkway;
import dev.galacticraft.mod.content.block.entity.WalkwayBlockEntity;
import dev.galacticraft.mod.util.ConnectingBlockUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class WalkwayBlock extends Block implements FluidLoggable, EntityBlock {
    private static final VoxelShape[] SHAPES = new VoxelShape[64];

    public WalkwayBlock(Properties settings) {
        super(settings);

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(FLUID, INVALID)
                .setValue(FlowingFluid.LEVEL, 8));
    }

    private static int getFacingMask(Direction dir) {
        return 1 << (dir.get3DDataValue());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (world.getBlockEntity(pos) instanceof Walkway walkway && walkway.getDirection() != null) {
            int index = getFacingMask(walkway.getDirection());
            if (SHAPES[index] != null) {
                return SHAPES[index];
            }
            return SHAPES[index] = ConnectingBlockUtil.createWalkwayShape(walkway.getDirection());
        }
        return ConnectingBlockUtil.WALKWAY_TOP;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState()
                .setValue(FLUID, Registry.FLUID.getKey(fluidState.getType()))
                .setValue(FlowingFluid.LEVEL, Math.max(fluidState.getAmount(), 1));
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        final Walkway blockEntity = (Walkway) world.getBlockEntity(pos);
        assert placer != null;
        assert blockEntity != null;
        blockEntity.setDirection(Direction.orderedByNearest(placer)[0].getOpposite());
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.values()) {
            if (blockEntity.getDirection() != direction) {
                if (world.getBlockEntity(mutable.set(pos).move(direction)) instanceof Walkway walkway) {
                    if (walkway.getDirection() != direction.getOpposite()) {
                        blockEntity.getConnections()[direction.ordinal()] = true;
                        continue;
                    }
                }
            }
            blockEntity.getConnections()[direction.ordinal()] = false;
        }
        world.updateNeighborsAt(pos, state.getBlock());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (!this.isEmpty(state)) {
            world.scheduleTick(pos, Registry.FLUID.get(state.getValue(FLUID)), Registry.FLUID.get(state.getValue(FLUID)).getTickDelay(world));
        }
        return state;
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborChanged(state, world, pos, block, fromPos, notify);
        final BlockPos distance = fromPos.subtract(pos);
        if (Math.abs(distance.getX() + distance.getY() + distance.getZ()) == 1) {
            final Walkway blockEntity = (Walkway) world.getBlockEntity(pos);
            assert blockEntity != null;
            final Direction direction = Direction.fromNormal(distance);
            if (world.getBlockEntity(fromPos) instanceof Walkway walkway) {
                if (walkway.getDirection() != null) {
                    if (!fromPos.relative(walkway.getDirection()).equals(pos)) {
                        if (!pos.relative(blockEntity.getDirection()).equals(fromPos)) {
                            if (blockEntity.getConnections()[direction.ordinal()] != (blockEntity.getConnections()[direction.ordinal()] = true)) {
                                world.neighborChanged(pos.relative(direction), state.getBlock(), pos);
                                if (!world.isClientSide) ((ServerLevel) world).getChunkSource().blockChanged(pos);
                            }
                            return;
                        }
                    }
                }
            }
            blockEntity.getConnections()[Objects.requireNonNull(direction).ordinal()] = false;
            if (!world.isClientSide) ((ServerLevel) world).getChunkSource().blockChanged(pos);
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (this.isEmpty(state)) return EMPTY_STATE;
        FluidState state1 = Registry.FLUID.get(state.getValue(FLUID)).defaultFluidState();
        if (state1.getValues().containsKey(FlowingFluid.LEVEL)) {
            state1 = state1.setValue(FlowingFluid.LEVEL, state.getValue(FlowingFluid.LEVEL));
        }
        return state1;
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FLUID, FlowingFluid.LEVEL);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WalkwayBlockEntity(pos, state);
    }
}