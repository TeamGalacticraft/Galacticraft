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

package dev.galacticraft.mod.content.block.special.walkway;

import com.mojang.serialization.MapCodec;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.FluidLoggable;
import dev.galacticraft.mod.api.block.FluidPipe;
import dev.galacticraft.mod.api.block.entity.Walkway;
import dev.galacticraft.mod.api.pipe.Pipe;
import dev.galacticraft.mod.content.block.entity.networked.FluidPipeWalkwayBlockEntity;
import dev.galacticraft.mod.content.block.special.fluidpipe.GlassFluidPipeBlock;
import dev.galacticraft.mod.content.block.special.fluidpipe.PipeBlockEntity;
import dev.galacticraft.mod.util.ConnectingBlockUtil;
import dev.galacticraft.mod.util.DirectionUtil;
import dev.galacticraft.mod.util.FluidUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class FluidPipeWalkway extends FluidPipe implements FluidLoggable {
    public static final MapCodec<FluidPipeWalkway> CODEC = simpleCodec(FluidPipeWalkway::new);
    private static final VoxelShape[] SHAPES = new VoxelShape[64];

    public FluidPipeWalkway(Properties settings) {
        super(settings);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(FLUID, INVALID)
                .setValue(FlowingFluid.LEVEL, 8)
                .setValue(FlowingFluid.FALLING, false));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    private static int getFacingMask(Direction direction) {
        return 1 << direction.get3DDataValue();
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        if (level.getBlockEntity(blockPos) instanceof FluidPipeWalkwayBlockEntity walkway) {
            var index = getFacingMask(walkway.getDirection());
            if (SHAPES[index] != null) {
                return ConnectingBlockUtil.getVoxelShape(walkway, GlassFluidPipeBlock.NORTH, GlassFluidPipeBlock.SOUTH, GlassFluidPipeBlock.EAST, GlassFluidPipeBlock.WEST, GlassFluidPipeBlock.UP, GlassFluidPipeBlock.DOWN, SHAPES[index]);
            }
            return ConnectingBlockUtil.getVoxelShape(walkway, GlassFluidPipeBlock.NORTH, GlassFluidPipeBlock.SOUTH, GlassFluidPipeBlock.EAST, GlassFluidPipeBlock.WEST, GlassFluidPipeBlock.UP, GlassFluidPipeBlock.DOWN, SHAPES[index] = ConnectingBlockUtil.createWalkwayShape(walkway.getDirection()));
        }
        return ConnectingBlockUtil.WALKWAY_TOP;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState()
                .setValue(FLUID, BuiltInRegistries.FLUID.getKey(fluidState.getType()))
                .setValue(FlowingFluid.LEVEL, Math.max(fluidState.getAmount(), 1))
                .setValue(FlowingFluid.FALLING, fluidState.hasProperty(FlowingFluid.FALLING) ? fluidState.getValue(FlowingFluid.FALLING) : false);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        var itemStack = player.getItemInHand(interactionHand);
        if (level.getBlockEntity(blockPos) instanceof FluidPipeWalkwayBlockEntity walkway) {
            if (itemStack.getItem() instanceof DyeItem dye) {
                var stack = itemStack.copy();
                var color = dye.getDyeColor();
                if (color != walkway.getColor()) {
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    player.setItemInHand(interactionHand, stack);
                    walkway.setColor(color);
                    level.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_IMMEDIATE);
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
                else {
                    return InteractionResult.PASS;
                }
            }
        }
        return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);

        if (level.getBlockEntity(blockPos) instanceof FluidPipeWalkwayBlockEntity walkway && livingEntity instanceof Player player) {
            for (var interactionHand : InteractionHand.values()) {
                var stack = player.getItemInHand(interactionHand);

                if (stack.getItem() instanceof DyeItem dye && dye.getDyeColor() != walkway.getColor()) {
                    walkway.setColor(dye.getDyeColor());
                    var copy = stack.copy();

                    if (!player.getAbilities().instabuild) {
                        copy.shrink(1);
                    }

                    player.setItemInHand(interactionHand, copy);
                }
            }

            walkway.setDirection(Direction.orderedByNearest(player)[0].getOpposite());

            for (var direction : Constant.Misc.DIRECTIONS) {
                if (walkway.getDirection() != direction) {
                    if (level.getBlockEntity(blockPos.relative(direction)) instanceof Pipe pipe) {
                        if (pipe.canConnect(direction.getOpposite())) {
                            walkway.getConnections()[direction.ordinal()] = true;
                            continue;
                        }
                    }
                    else if (FluidUtil.canAccessFluid(level, blockPos.relative(direction), direction)) {
                        walkway.getConnections()[direction.ordinal()] = true;
                        continue;
                    }
                }
                walkway.getConnections()[direction.ordinal()] = false;
            }
            level.updateNeighborsAt(blockPos, blockState.getBlock());
        }
    }

    @Override
    public BlockState updateShape(BlockState blockState, Direction facing, BlockState neighborState, LevelAccessor level, BlockPos blockPos, BlockPos neighborPos) {
        if (!this.isEmpty(blockState)) {
            level.scheduleTick(blockPos, BuiltInRegistries.FLUID.get(blockState.getValue(FLUID)), BuiltInRegistries.FLUID.get(blockState.getValue(FLUID)).getTickDelay(level));
        }
        return blockState;
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborChanged(blockState, level, blockPos, block, fromPos, notify);
        var distance = fromPos.subtract(blockPos);

        if (Math.abs(distance.getX() + distance.getY() + distance.getZ()) == 1 && level.getBlockEntity(blockPos) instanceof Walkway walkway) {
            var direction = DirectionUtil.fromNormal(distance);
            if (direction != walkway.getDirection()) {
                if (level.getBlockEntity(blockPos.relative(direction)) instanceof Pipe pipe) {
                    if (pipe.canConnect(direction.getOpposite())) {
                        if (walkway.getConnections()[direction.ordinal()] != (walkway.getConnections()[direction.ordinal()] = true)) {
                            level.neighborChanged(blockPos.relative(direction), blockState.getBlock(), blockPos);
                            level.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_IMMEDIATE);
                        }
                        return;
                    }
                }
                else if (FluidUtil.canAccessFluid(level, blockPos.relative(direction), direction)) {
                    if (walkway.getConnections()[direction.ordinal()] != (walkway.getConnections()[direction.ordinal()] = true)) {
                        level.neighborChanged(blockPos.relative(direction), blockState.getBlock(), blockPos);
                        level.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_IMMEDIATE);
                    }
                    return;
                }
            }
            walkway.getConnections()[Objects.requireNonNull(direction).ordinal()] = false;
            level.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_IMMEDIATE);
        }
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        if (this.isEmpty(blockState)) {
            return EMPTY_STATE;
        }

        var state1 = BuiltInRegistries.FLUID.get(blockState.getValue(FLUID)).defaultFluidState();

        if (state1.getValues().containsKey(FlowingFluid.LEVEL)) {
            state1 = state1.setValue(FlowingFluid.LEVEL, blockState.getValue(FlowingFluid.LEVEL));
        }
        if (state1.getValues().containsKey(FlowingFluid.FALLING)) {
            state1 = state1.setValue(FlowingFluid.FALLING, blockState.getValue(FlowingFluid.FALLING));
        }
        return state1;
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FLUID, FlowingFluid.LEVEL, FlowingFluid.FALLING);
    }

    @Override
    @Nullable
    public PipeBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new FluidPipeWalkwayBlockEntity(blockPos, blockState);
    }
}