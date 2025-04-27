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

import com.google.common.annotations.VisibleForTesting;
import dev.galacticraft.mod.api.block.FluidLoggable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class IronGratingBlock extends Block implements FluidLoggable {
    @VisibleForTesting
    public static final EnumProperty<State> STATE = EnumProperty.create("state", State.class);
    private static final VoxelShape UPPER_SHAPE = Block.box(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape LOWER_SHAPE = Block.box(0.0D, 6.0D, 0.0D, 16.0D, 8.0D, 16.0D);

    public IronGratingBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(FLUID, ResourceLocation.withDefaultNamespace("invalid"))
                .setValue(FlowingFluid.LEVEL, 8)
                .setValue(STATE, State.UPPER)
                .setValue(FlowingFluid.FALLING, false));
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState stateFrom, Direction axisDirection) {
        return stateFrom.is(this) && state.getValue(STATE) == stateFrom.getValue(STATE) || super.skipRendering(state, stateFrom, axisDirection);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        BlockState state = this.defaultBlockState().setValue(STATE, State.LOWER);
        state = FluidLoggable.applyFluidState(blockPlaceContext.getLevel(), state, blockPlaceContext.getClickedPos());

        Direction direction = blockPlaceContext.getClickedFace();
        BlockPos blockPos = blockPlaceContext.getClickedPos();
        if (direction != Direction.DOWN && (direction == Direction.UP || !(blockPlaceContext.getClickLocation().y - (double) blockPos.getY() > 0.5))) {
            state.setValue(STATE, State.UPPER);
        }

        return state;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState blockState, BlockGetter blockView, BlockPos blockPos, CollisionContext context) {
        return blockState.getValue(STATE) == State.UPPER ? UPPER_SHAPE : LOWER_SHAPE;
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborBlockState, LevelAccessor world, BlockPos blockPos, BlockPos neighborBlockPos) {
        FluidLoggable.tryScheduleFluidTick(world, state, blockPos);
        return super.updateShape(state, direction, neighborBlockState, world, blockPos, neighborBlockPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FLUID, STATE, FlowingFluid.LEVEL, FlowingFluid.FALLING);
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return FluidLoggable.createFluidState(state);
    }

    @Override
    public @NotNull Optional<SoundEvent> getPickupSound() {
        return Fluids.WATER.getPickupSound();
    }

    @VisibleForTesting
    public enum State implements StringRepresentable {
        UPPER("upper"),
        LOWER("lower");

        private final String name;

        State(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
