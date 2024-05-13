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

import com.google.common.annotations.VisibleForTesting;
import dev.galacticraft.mod.api.block.FluidLoggable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
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
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class GratingBlock extends Block implements FluidLoggable {
    @VisibleForTesting
    public static final EnumProperty<State> STATE = EnumProperty.create("state", State.class);
    private static final VoxelShape UPPER_SHAPE = Block.box(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape LOWER_SHAPE = Block.box(0.0D, 6.0D, 0.0D, 16.0D, 8.0D, 16.0D);

    public GratingBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(FLUID, new ResourceLocation("invalid"))
                .setValue(FlowingFluid.LEVEL, 8)
                .setValue(STATE, State.UPPER)
                .setValue(FlowingFluid.FALLING, false));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        FluidState fluidState = blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos());
        BlockPos blockPos = blockPlaceContext.getClickedPos();
        BlockState blockState = this.defaultBlockState().setValue(STATE, State.LOWER).setValue(FLUID, BuiltInRegistries.FLUID.getKey(fluidState.getType())).setValue(FlowingFluid.LEVEL, Math.max(fluidState.getAmount(), 1)).setValue(FlowingFluid.FALLING, fluidState.hasProperty(FlowingFluid.FALLING) ? fluidState.getValue(FlowingFluid.FALLING) : false);
        Direction direction = blockPlaceContext.getClickedFace();
        return direction != Direction.DOWN && (direction == Direction.UP || !(blockPlaceContext.getClickLocation().y - (double) blockPos.getY() > 0.5)) ? blockState : blockState.setValue(STATE, State.UPPER);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockView, BlockPos blockPos, CollisionContext context) {
        return blockState.getValue(STATE) == State.UPPER ? UPPER_SHAPE : LOWER_SHAPE;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborBlockState, LevelAccessor world, BlockPos blockPos, BlockPos neighborBlockPos) {
        if (!this.isEmpty(state)) {
            world.scheduleTick(blockPos, BuiltInRegistries.FLUID.get(state.getValue(FLUID)), BuiltInRegistries.FLUID.get(state.getValue(FLUID)).getTickDelay(world));
        }

        return super.updateShape(state, direction, neighborBlockState, world, blockPos, neighborBlockPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FLUID, STATE, FlowingFluid.LEVEL, FlowingFluid.FALLING);
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
    public Optional<SoundEvent> getPickupSound() {
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
