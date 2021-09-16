/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.block.special.walkway;

import dev.galacticraft.mod.api.block.FluidLoggable;
import dev.galacticraft.mod.api.block.FluidPipe;
import dev.galacticraft.mod.api.block.entity.Walkway;
import dev.galacticraft.mod.api.pipe.Pipe;
import dev.galacticraft.mod.block.entity.PipeWalkwayBlockEntity;
import dev.galacticraft.mod.block.special.fluidpipe.PipeBlockEntity;
import dev.galacticraft.mod.util.ConnectingBlockUtil;
import dev.galacticraft.mod.util.FluidUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class PipeWalkway extends FluidPipe implements FluidLoggable {
    private static final VoxelShape[] SHAPES = new VoxelShape[64];

    public PipeWalkway(Settings settings) {
        super(settings);
    }

    private static int getFacingMask(Direction dir) {
        return 1 << (dir.getId());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
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
    public BlockState getPlacementState(ItemPlacementContext context) {
        FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
        return this.getDefaultState()
                .with(FLUID, Registry.FLUID.getId(fluidState.getFluid()))
                .with(FlowableFluid.LEVEL, Math.max(fluidState.getLevel(), 1));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        final ItemStack stack = player.getStackInHand(hand);
        if (!stack.isEmpty() && stack.getItem() instanceof DyeItem dye) {
            final PipeWalkwayBlockEntity be = ((PipeWalkwayBlockEntity) world.getBlockEntity(pos));
            assert be != null;
            if (dye.getColor() != be.getColor()) {
                be.setColor(dye.getColor());
                final ItemStack copy = stack.copy();
                copy.decrement(1);
                player.setStackInHand(hand, copy);
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        final PipeWalkwayBlockEntity blockEntity = (PipeWalkwayBlockEntity) world.getBlockEntity(pos);
        assert placer != null;
        assert blockEntity != null;
        for (Hand hand : Hand.values()) {
            final ItemStack stack = placer.getStackInHand(hand);
            if (stack.getItem() instanceof DyeItem dye && dye.getColor() != blockEntity.getColor()) {
                blockEntity.setColor(dye.getColor());
                final ItemStack copy = stack.copy();
                copy.decrement(1);
                placer.setStackInHand(hand, copy);
            }
        }
        blockEntity.setDirection(Direction.getEntityFacingOrder(placer)[0].getOpposite());
        for (Direction direction : Direction.values()) {
            if (blockEntity.getDirection() != direction) {
                if (world.getBlockEntity(pos.offset(direction)) instanceof Pipe pipe) {
                    if (pipe.canConnect(direction.getOpposite())) {
                        blockEntity.getConnections()[direction.ordinal()] = true;
                        continue;
                    }
                } else if (FluidUtil.canAccessFluid(world, pos.offset(direction), direction)) {
                    blockEntity.getConnections()[direction.ordinal()] = true;
                    continue;
                }
            }
            blockEntity.getConnections()[direction.ordinal()] = false;
        }
        world.updateNeighborsAlways(pos, state.getBlock());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!this.isEmpty(state)) {
            world.getFluidTickScheduler().schedule(pos, Registry.FLUID.get(state.get(FLUID)), Registry.FLUID.get(state.get(FLUID)).getTickRate(world));
        }
        return state;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        final BlockPos distance = fromPos.subtract(pos);
        if (Math.abs(distance.getX() + distance.getY() + distance.getZ()) == 1) {
            final Walkway blockEntity = (Walkway) world.getBlockEntity(pos);
            assert blockEntity != null;
            final Direction direction = Direction.fromVector(distance);
            assert direction != null;
            if (direction != blockEntity.getDirection()) {
                if (world.getBlockEntity(pos.offset(direction)) instanceof Pipe pipe) {
                    if (pipe.canConnect(direction.getOpposite())) {
                        if (blockEntity.getConnections()[direction.ordinal()] != (blockEntity.getConnections()[direction.ordinal()] = true)) {
                            world.updateNeighbor(pos.offset(direction), state.getBlock(), pos);
                            if (!world.isClient) blockEntity.sync();
                        }
                        return;
                    }
                } else if (FluidUtil.canAccessFluid(world, pos.offset(direction), direction)) {
                    if (blockEntity.getConnections()[direction.ordinal()] != (blockEntity.getConnections()[direction.ordinal()] = true)) {
                        world.updateNeighbor(pos.offset(direction), state.getBlock(), pos);
                        if (!world.isClient) blockEntity.sync();
                    }
                    return;
                }
            }
            blockEntity.getConnections()[Objects.requireNonNull(direction).ordinal()] = false;
            if (!world.isClient) blockEntity.sync();
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (this.isEmpty(state)) return EMPTY_STATE;
        FluidState state1 = Registry.FLUID.get(state.get(FLUID)).getDefaultState();
        if (state1.getEntries().containsKey(FlowableFluid.LEVEL)) {
            state1 = state1.with(FlowableFluid.LEVEL, state.get(FlowableFluid.LEVEL));
        }
        return state1;
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FLUID, FlowableFluid.LEVEL);
    }

    @Override
    public @Nullable PipeBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PipeWalkwayBlockEntity(pos, state);
    }
}