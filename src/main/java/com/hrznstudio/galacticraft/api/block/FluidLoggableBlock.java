/*
 * Copyright (c) 2020 HRZN LTD
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

package com.hrznstudio.galacticraft.api.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public interface FluidLoggableBlock extends FluidDrainable, FluidFillable {

    String DOT_REP = "___56_1___";
    String DASH_REP = "__89_00___"; // yes this is bad.... but who's gonna name a mod/fluid something like that
    String COLON_REP = "__2_211_23";

    Property<Identifier> FLUID = new Property<Identifier>("fluid", Identifier.class) {
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
        public Optional<Identifier> parse(String name) {
            return Optional.of(new Identifier(name.replace(DOT_REP, ".").replace(DASH_REP, "-").replace(COLON_REP, ":")));
        }

        @Override
        public String name(Identifier value) {
            if (value.toString().contains(DOT_REP) || value.toString().contains(DASH_REP) || value.toString().contains(COLON_REP))
                throw new RuntimeException("Bad fluid!" + value);
            return value.toString().replace(".", DOT_REP).replace("-", DASH_REP).replace(":", COLON_REP);
        }
    };

    @Override
    default boolean canFillWithFluid(BlockView view, BlockPos pos, BlockState state, Fluid fluid) {
        return state.get(FLUID).equals(new Identifier("empty"));
    }

    @Override
    default boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (state.get(FLUID).equals(new Identifier("empty"))) {
            if (!world.isClient()) {
                world.setBlockState(pos, state.with(FLUID, Registry.FLUID.getId(fluidState.getFluid()))
                        .with(FlowableFluid.LEVEL, Math.max(fluidState.getLevel(), 1)), 3);
                world.getFluidTickScheduler().schedule(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    default Fluid tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
        if (!state.get(FLUID).equals(new Identifier("empty"))) {
            world.setBlockState(pos, state.with(FLUID, new Identifier("empty")), 3);
            if (Registry.FLUID.get(state.get(FLUID)).getDefaultState().isStill()) {
                return Registry.FLUID.get(state.get(FLUID));
            }
        }
        return Fluids.EMPTY;
    }

    BlockState getPlacementState(ItemPlacementContext context);
}