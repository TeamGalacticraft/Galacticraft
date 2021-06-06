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

import dev.galacticraft.mod.api.block.FluidPipe;
import dev.galacticraft.mod.item.StandardWrenchItem;
import dev.galacticraft.mod.util.ConnectingBlockUtil;
import dev.galacticraft.mod.util.FluidUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import static dev.galacticraft.mod.block.special.fluidpipe.GlassFluidPipeBlock.COLOR;
import static dev.galacticraft.mod.block.special.fluidpipe.GlassFluidPipeBlock.PULL;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class PipeWalkway extends FluidPipe {
    public static final DirectionProperty FACING = Properties.FACING;
    private static final VoxelShape[] shape = new VoxelShape[64];

    public PipeWalkway(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(Properties.NORTH, false)
                .with(Properties.EAST, false)
                .with(Properties.SOUTH, false)
                .with(Properties.WEST, false)
                .with(Properties.UP, false)
                .with(Properties.DOWN, false)
                .with(FACING, Direction.UP)
                .with(PULL, false)
                .with(COLOR, DyeColor.WHITE));
    }

    private static int getFacingMask(Direction dir) {
        return 1 << (dir.getId());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShape(state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShape(state);
    }

    private VoxelShape getShape(BlockState state) {
        int index = getFacingMask(state.get(FACING));
        if (shape[index] != null) {
            return shape[index];
        }
        return shape[index] = ConnectingBlockUtil.createWalkwayShape(state.get(FACING));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return ConnectingBlockUtil.rotateConnections(state, rotation);
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return ConnectingBlockUtil.mirror(state, mirror);
    }

    public boolean canConnect(BlockState state, BlockState neighborState, BlockPos pos, BlockPos neighborPos, WorldAccess world, Direction facing) {
        /* This code tests if the connecting block is on the top of the walkway (it used to be unable to connect there)
        try {
            if (pos.offset(state.get(FACING)).equals(neighborPos))
                return false;
            if (neighborPos.offset(neighborState.get(FACING)).equals(pos))
                return false;
        } catch (IllegalArgumentException ignored) {}
        */
        if (FluidUtil.isExtractableOrInsertable((World) world, neighborPos, facing.getOpposite()))
            return true;
        return (neighborState.getBlock() instanceof FluidPipe); //&& (neighborState.get(COLOR) == state.get(COLOR));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getStackInHand(hand).isEmpty()) {
            if (player.getStackInHand(hand).getItem() instanceof DyeItem dye) {
                ItemStack stack = player.getStackInHand(hand).copy();
                DyeColor color = dye.getColor();
                if (color != state.get(COLOR)) {
                    stack.decrement(1);
                    player.setStackInHand(hand, stack);
                    world.setBlockState(pos, state.with(COLOR, color));
                    return ActionResult.SUCCESS;
                } else {
                    return ActionResult.FAIL;
                }
            }
            if (player.getStackInHand(hand).getItem() instanceof StandardWrenchItem) {
                ItemStack stack = player.getStackInHand(hand).copy();
                stack.damage(1, world.random, player instanceof ServerPlayerEntity ? ((ServerPlayerEntity) player) : null);
                player.setStackInHand(hand, stack);
                //world.setBlockState(pos, state.with(PULL, !state.get(PULL)));
                return ActionResult.SUCCESS;
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        BlockState state = this.getDefaultState();
        for (Direction direction : Direction.values()) {
            state = state.with(ConnectingBlockUtil.getBooleanProperty(direction), this.canConnect(state,
                    context.getWorld().getBlockState(context.getBlockPos().offset(direction)),
                    context.getBlockPos(),
                    context.getBlockPos().offset(direction),
                    context.getWorld(),
                    direction));
        }
        return state.with(FACING, context.getPlayerLookDirection().getOpposite());
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return state.with(ConnectingBlockUtil.getBooleanProperty(facing), this.canConnect(state, neighborState, pos, neighborPos, world, facing));
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(Properties.NORTH, Properties.EAST, Properties.WEST, Properties.SOUTH, Properties.UP, Properties.DOWN, FACING, PULL, COLOR);
    }
}