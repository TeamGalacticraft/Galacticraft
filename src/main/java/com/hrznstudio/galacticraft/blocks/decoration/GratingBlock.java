/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.blocks.decoration;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.AbstractProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GratingBlock extends Block implements FluidDrainable, FluidFillable {

    private static final String DOT_REP = "___56_1___";
    private static final String DASH_REP = "__89_00___"; //yes this is bad.... but who's gonna name a mod/fluid named smth like that
    private static final String COLON_REP = "__2_211_23";

    public static final AbstractProperty<Identifier> FLUID = new AbstractProperty<Identifier>("fluid", Identifier.class) {
        private final List<Identifier> VALUES = new ArrayList<>();

        @Override
        public Collection<Identifier> getValues() {
            if (VALUES.isEmpty()) {
                for (Fluid f : Registry.FLUID) {
                    VALUES.add(Registry.FLUID.getId(f));
                }
                VALUES.add(new Identifier("empty"));
            }

            return VALUES;
        }

        @Override
        public Optional<Identifier> getValue(String name) {
            return Optional.of(new Identifier(name.replace(DOT_REP, ".").replace(DASH_REP, "-").replace(COLON_REP, ":")));
        }

        @Override
        public String getName(Identifier value) {
            if (value.toString().contains(DOT_REP) || value.toString().contains(DASH_REP) || value.toString().contains(COLON_REP))
                throw new RuntimeException("Bad fluid!" + value);
            return value.toString().replace(".", DOT_REP).replace("-", DASH_REP).replace(":", COLON_REP);
        }
    };

    public GratingBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateFactory.getDefaultState().with(FLUID, new Identifier("empty"))
                .with(BaseFluid.LEVEL, 8).with(GRATING_STATE, GratingState.UPPER));
    }

    public boolean canFillWithFluid(BlockView view, BlockPos pos, BlockState state, Fluid fluid) {
        return state.get(FLUID).equals(new Identifier("empty"));
    }

    public boolean tryFillWithFluid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (state.get(FLUID).equals(new Identifier("empty"))) {
            if (!world.isClient()) {
                world.setBlockState(pos, state.with(FLUID, Registry.FLUID.getId(fluidState.getFluid()))
                        .with(BaseFluid.LEVEL, Math.max(fluidState.getLevel(), 1)), 3);
                world.getFluidTickScheduler().schedule(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
            }

            return true;
        } else {
            return false;
        }
    }

    protected static final EnumProperty<GratingState> GRATING_STATE = EnumProperty.of("grating_state", GratingState.class);

    public Fluid tryDrainFluid(IWorld world, BlockPos pos, BlockState state) {
        if (!state.get(FLUID).equals(new Identifier("empty"))) {
            world.setBlockState(pos, state.with(FLUID, new Identifier("empty")), 3);
            if (Registry.FLUID.get(state.get(FLUID)).getDefaultState().isStill()) {
                return Registry.FLUID.get(state.get(FLUID));
            }
        }
        return Fluids.EMPTY;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext EntityContext) {
        return blockState.get(GRATING_STATE) == GratingState.UPPER ?
                Block.createCuboidShape(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D) :
                Block.createCuboidShape(0.0D, 6.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
        BlockState blockState = this.getDefaultState().with(GRATING_STATE, GratingState.LOWER)
                .with(FLUID, Registry.FLUID.getId(fluidState.getFluid()))
                .with(BaseFluid.LEVEL, Math.max(fluidState.getLevel(), 1));
        BlockPos blockPos = context.getBlockPos();
        Direction direction = context.getPlayerFacing();

        return direction != Direction.DOWN && (direction == Direction.UP || context.getBlockPos().getY() - (double) blockPos.getY() <= 0.5D) ? blockState : blockState.with(GRATING_STATE, GratingState.UPPER);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborBlockState, IWorld world, BlockPos blockPos, BlockPos neighborBlockPos) {
        if (!state.get(FLUID).equals(new Identifier("empty"))) {
            world.getFluidTickScheduler().schedule(blockPos, Registry.FLUID.get(state.get(FLUID)), Registry.FLUID.get(state.get(FLUID)).getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborBlockState, world, blockPos, neighborBlockPos);
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
        builder.add(FLUID, BaseFluid.LEVEL, GRATING_STATE);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        FluidState state1 = Registry.FLUID.get(state.get(FLUID)).getDefaultState();
        if (state1.getEntries().containsKey(BaseFluid.LEVEL)) {
            state1 = state1.with(BaseFluid.LEVEL, state.get(BaseFluid.LEVEL));
        }
        return state1;
    }

    public enum GratingState implements StringIdentifiable {
        UPPER("upper"),
        LOWER("lower");

        private String name;

        GratingState(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }
}
