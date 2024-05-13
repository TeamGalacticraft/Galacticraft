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

package dev.galacticraft.mod.api.block;

import com.google.common.collect.Lists;
import dev.galacticraft.mod.Constant;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FluidLoggable extends BucketPickup, LiquidBlockContainer {
    FluidState EMPTY_STATE = Fluids.EMPTY.defaultFluidState();
    ResourceLocation INVALID = new ResourceLocation("invalid");
    String DOT_REP = "_gc_dot_";
    String DASH_REP = "_gc_dash_"; // yes this is bad.... but who's going to name a mod/fluid something like that
    String COLON_REP = "_gc_colon_";

    Property<ResourceLocation> FLUID = new Property<>("fluid", ResourceLocation.class) {
        private static final List<ResourceLocation> VALUES = Util.make(Lists.newArrayList(), list ->
        {
            if (list.isEmpty()) {
                for (var fluid : BuiltInRegistries.FLUID) {
                    if (fluid instanceof FlowingFluid) {
                        list.add(BuiltInRegistries.FLUID.getKey(fluid));
                    }
                }
                list.add(Constant.Misc.EMPTY);
                list.add(INVALID);
            }
        });

        @Override
        public Collection<ResourceLocation> getPossibleValues() {
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
    default boolean canPlaceLiquid(@Nullable Player player, BlockGetter view, BlockPos pos, BlockState state, Fluid fluid) {
        return fluid instanceof FlowingFluid;
    }

    @Override
    default boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!(fluidState.getType() instanceof FlowingFluid))
            return false;
        if (this.isEmpty(state)) {
            if (!world.isClientSide()) {
                world.setBlock(pos, state.setValue(FLUID, BuiltInRegistries.FLUID.getKey(fluidState.getType())).setValue(FlowingFluid.LEVEL, Math.max(fluidState.getAmount(), 1)).setValue(FlowingFluid.FALLING, fluidState.getValue(FlowingFluid.FALLING)), 3);
                world.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(world));
            }
            return true;
        }
        else if (BuiltInRegistries.FLUID.getKey(fluidState.getType()).equals(state.getValue(FLUID))) {
            if (!world.isClientSide()) {
                world.setBlock(pos, state.setValue(FlowingFluid.LEVEL, Math.max(fluidState.getAmount(), 1)).setValue(FlowingFluid.FALLING, fluidState.getValue(FlowingFluid.FALLING)), 3);
                world.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(world));
            }
            return true;
        }
        // replace current grating fluid or make it contains source block
        else if (!BuiltInRegistries.FLUID.get(state.getValue(FLUID)).defaultFluidState().isSource() || !BuiltInRegistries.FLUID.getKey(fluidState.getType()).equals(state.getValue(FLUID))) {
            if (!world.isClientSide()) {
                world.setBlock(pos, state.setValue(FLUID, BuiltInRegistries.FLUID.getKey(fluidState.getType())).setValue(FlowingFluid.LEVEL, Math.max(fluidState.getAmount(), 1)), 3);
                world.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(world));
            }
            return true;
        }
        else if (fluidState.getType() == Fluids.EMPTY) {
            world.setBlock(pos, state.setValue(FLUID, Constant.Misc.EMPTY).setValue(FlowingFluid.LEVEL, 1).setValue(FlowingFluid.FALLING, false), 3);
        }
        return false;
    }

    @Override
    default ItemStack pickupBlock(@Nullable Player player, LevelAccessor world, BlockPos pos, BlockState state) {
        if (!this.isEmpty(state)) {
            if (BuiltInRegistries.FLUID.get(state.getValue(FLUID)).defaultFluidState().isSource()) {
                world.setBlock(pos, state.setValue(FLUID, Constant.Misc.EMPTY).setValue(FlowingFluid.LEVEL, 1), 3);
                return new ItemStack(BuiltInRegistries.FLUID.get(state.getValue(FLUID)).getBucket());
            }
        }
        return ItemStack.EMPTY;
    }

    default boolean isEmpty(BlockState state) {
        return state.getValue(FLUID).equals(Constant.Misc.EMPTY) || state.getValue(FLUID).equals(INVALID);
    }

    @Override
    default Optional<SoundEvent> getPickupSound() {
        return Optional.empty();
    }
}