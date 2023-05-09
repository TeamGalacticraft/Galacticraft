/*
 * Copyright (c) 2019-2023 Team Galacticraft
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
import dev.galacticraft.mod.api.block.WireBlock;
import dev.galacticraft.mod.api.block.entity.Walkway;
import dev.galacticraft.mod.api.wire.Wire;
import dev.galacticraft.mod.content.block.entity.networked.WireBlockEntity;
import dev.galacticraft.mod.content.block.entity.networked.WireWalkwayBlockEntity;
import dev.galacticraft.mod.util.ConnectingBlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.Objects;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class WireWalkway extends WireBlock implements FluidLoggable {
    private static final VoxelShape[] SHAPES = new VoxelShape[64];

    public WireWalkway(Properties settings) {
        super(settings);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(FLUID, INVALID)
                .setValue(FlowingFluid.LEVEL, 8)
                .setValue(FlowingFluid.FALLING, false));
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
                .setValue(FLUID, BuiltInRegistries.FLUID.getKey(fluidState.getType()))
                .setValue(FlowingFluid.LEVEL, Math.max(fluidState.getAmount(), 1))
                .setValue(FlowingFluid.FALLING, fluidState.hasProperty(FlowingFluid.FALLING) ? fluidState.getValue(FlowingFluid.FALLING) : false);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        final Walkway blockEntity = (Walkway) world.getBlockEntity(pos);
        assert placer != null;
        assert blockEntity != null;
        BlockHitResult blockHitResult = Item.getPlayerPOVHitResult(world, (Player) placer, ClipContext.Fluid.SOURCE_ONLY);
        blockEntity.setDirection(blockHitResult.getDirection());
        for (Direction direction : Direction.values()) {
            if (blockEntity.getDirection() != direction) {
                final BlockEntity blockEntity1 = world.getBlockEntity(pos.relative(direction));
                if (blockEntity1 instanceof Wire wire) {
                    if (wire.canConnect(direction.getOpposite())) {
                        blockEntity.getConnections()[direction.ordinal()] = true;
                        continue;
                    }
                } else if (EnergyStorage.SIDED.find(world, pos.relative(direction), direction.getOpposite()) != null) {
                    blockEntity.getConnections()[direction.ordinal()] = true;
                    continue;
                }
            }
            blockEntity.getConnections()[direction.ordinal()] = false;
        }
        world.updateNeighborsAt(pos, state.getBlock());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (!this.isEmpty(state)) {
            world.scheduleTick(pos, BuiltInRegistries.FLUID.get(state.getValue(FLUID)), BuiltInRegistries.FLUID.get(state.getValue(FLUID)).getTickDelay(world));
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
            if (direction != blockEntity.getDirection()) {
                final BlockEntity blockEntity1 = world.getBlockEntity(pos.relative(direction));
                if (blockEntity1 instanceof Wire wire) {
                    if (wire.canConnect(direction.getOpposite())) {
                        if (blockEntity.getConnections()[direction.ordinal()] != (blockEntity.getConnections()[direction.ordinal()] = true)) {
                            world.neighborChanged(pos.relative(direction), state.getBlock(), pos);
                            if (!world.isClientSide) ((ServerLevel) world).getChunkSource().blockChanged(pos);
                        }
                        return;
                    }
                } else if (EnergyStorage.SIDED.find(world, pos.relative(direction), direction.getOpposite()) != null) {
                    if (blockEntity.getConnections()[direction.ordinal()] != (blockEntity.getConnections()[direction.ordinal()] = true)) {
                        world.neighborChanged(pos.relative(direction), state.getBlock(), pos);
                        if (!world.isClientSide) ((ServerLevel) world).getChunkSource().blockChanged(pos);
                    }
                    return;
                }
            }
            blockEntity.getConnections()[Objects.requireNonNull(direction).ordinal()] = false;
            if (!world.isClientSide) ((ServerLevel) world).getChunkSource().blockChanged(pos);
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (this.isEmpty(state)) return EMPTY_STATE;
        FluidState state1 = BuiltInRegistries.FLUID.get(state.getValue(FLUID)).defaultFluidState();
        if (state1.getValues().containsKey(FlowingFluid.LEVEL)) {
            state1 = state1.setValue(FlowingFluid.LEVEL, state.getValue(FlowingFluid.LEVEL));
        }
        if (state1.getValues().containsKey(FlowingFluid.FALLING)) {
            state1 = state1.setValue(FlowingFluid.FALLING, state.getValue(FlowingFluid.FALLING));
        }
        return state1;
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FLUID, FlowingFluid.LEVEL, FlowingFluid.FALLING);
    }

    @Override
    public @Nullable WireBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WireWalkwayBlockEntity(pos, state);
    }
}