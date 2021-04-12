/*
 * Copyright (c) 2019-2021 HRZN LTD
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

import com.hrznstudio.galacticraft.Constants;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public interface FluidLoggableBlock extends BucketPickup, LiquidBlockContainer {

    String DOT_REP = "_gcr_dot_";
    String DASH_REP = "_gcr_dash__"; // yes this is bad.... but who's gonna name a mod/fluid something like that
    String COLON_REP = "_gcr_colon_";

    Property<ResourceLocation> FLUID = new Property<ResourceLocation>("fluid", ResourceLocation.class) {
        private final List<ResourceLocation> VALUES = new LinkedList<>();

        @Override
        public Collection<ResourceLocation> getPossibleValues() {
            if (VALUES.isEmpty()) {
                for (Fluid f : Registry.FLUID) {
                    if (f instanceof FlowingFluid) {
                        VALUES.add(Registry.FLUID.getKey(f));
                    }
                }
                VALUES.add(Constants.Misc.EMPTY);
            }
            return VALUES;
        }

        @Override
        public Optional<ResourceLocation> getValue(String name) {
            return Optional.of(new ResourceLocation(name.replace(DOT_REP, ".").replace(DASH_REP, "-").replace(COLON_REP, ":")));
        }

        @Override
        public String getName(ResourceLocation value) {
            if (value.toString().contains(DOT_REP) || value.toString().contains(DASH_REP) || value.toString().contains(COLON_REP))
                throw new RuntimeException("Bad fluid!" + value);
            return value.toString().replace(".", DOT_REP).replace("-", DASH_REP).replace(":", COLON_REP);
        }
    };

    @Override
    default boolean canPlaceLiquid(BlockGetter view, BlockPos pos, BlockState state, Fluid fluid) {
        return state.getValue(FLUID).equals(Constants.Misc.EMPTY);
    }

    @Override
    default boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (state.getValue(FLUID).equals(Constants.Misc.EMPTY)) {
            if (!world.isClientSide()) {
                world.setBlock(pos, state.setValue(FLUID, Registry.FLUID.getKey(fluidState.getType()))
                        .setValue(FlowingFluid.LEVEL, Math.max(fluidState.getAmount(), 1)), 3);
                world.getLiquidTicks().scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(world));
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    default Fluid takeLiquid(LevelAccessor world, BlockPos pos, BlockState state) {
        if (!state.getValue(FLUID).equals(Constants.Misc.EMPTY)) {
            world.setBlock(pos, state.setValue(FLUID, Constants.Misc.EMPTY), 3);
            if (Registry.FLUID.get(state.getValue(FLUID)).defaultFluidState().isSource()) {
                return Registry.FLUID.get(state.getValue(FLUID));
            }
        }
        return Fluids.EMPTY;
    }

    BlockState getPlacementState(BlockPlaceContext context);
}