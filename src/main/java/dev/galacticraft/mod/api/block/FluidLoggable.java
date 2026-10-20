/*
 * Copyright (c) 2019-2026 Team Galacticraft
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
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public interface FluidLoggable extends BucketPickup, LiquidBlockContainer {
    FluidState EMPTY_STATE = Fluids.EMPTY.defaultFluidState();
    ResourceLocation INVALID = ResourceLocation.withDefaultNamespace("invalid");
    String DOT_REP = "_gc_dot_";
    String DASH_REP = "_gc_dash_"; // yes this is bad.... but who's going to name a mod/fluid something like that
    String COLON_REP = "_gc_colon_";
    /**
     * The key of a custom value in {@code fabric.mod.json} that other mods can use to make their fluids fluid loggable,
     * e.g. {@code "custom": {"galacticraft:fluidloggable": ["mymod:oil", "mymod:flowing_oil"]}}.
     */
    String FLUIDLOGGABLE_KEY = Constant.MOD_ID + ":fluidloggable";

    /**
     * The possible values of {@link #FLUID}. This is a fixed list of fluid IDs rather than the contents of the fluid registry,
     * so it doesn't depend on when the fluids are registered, and it is the same on the client and the server.
     */
    Property<ResourceLocation> FLUID = new Property<>("fluid", ResourceLocation.class) {
        private static final List<ResourceLocation> VALUES = Util.make(Lists.newArrayList(), list -> {
            Set<ResourceLocation> fluids = new TreeSet<>(List.of(
                    BuiltInRegistries.FLUID.getKey(Fluids.WATER),
                    BuiltInRegistries.FLUID.getKey(Fluids.FLOWING_WATER),
                    BuiltInRegistries.FLUID.getKey(Fluids.LAVA),
                    BuiltInRegistries.FLUID.getKey(Fluids.FLOWING_LAVA),
                    Constant.id(Constant.Fluid.CRUDE_OIL_STILL),
                    Constant.id(Constant.Fluid.CRUDE_OIL_FLOWING),
                    Constant.id(Constant.Fluid.FUEL_STILL),
                    Constant.id(Constant.Fluid.FUEL_FLOWING),
                    Constant.id(Constant.Fluid.SULFURIC_ACID_STILL),
                    Constant.id(Constant.Fluid.SULFURIC_ACID_FLOWING)
            ));
            for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
                CustomValue value = mod.getMetadata().getCustomValue(FLUIDLOGGABLE_KEY);
                if (value == null) continue;
                if (value.getType() != CustomValue.CvType.ARRAY) {
                    Constant.LOGGER.warn("Ignoring '{}' of mod {}: expected an array of fluid IDs", FLUIDLOGGABLE_KEY, mod.getMetadata().getId());
                    continue;
                }
                for (CustomValue entry : value.getAsArray()) {
                    ResourceLocation id = entry.getType() == CustomValue.CvType.STRING ? ResourceLocation.tryParse(entry.getAsString()) : null;
                    if (id == null || !id.toString().replace(".", DOT_REP).replace("-", DASH_REP).replace(":", COLON_REP).matches("[a-z0-9_]+")) {
                        Constant.LOGGER.warn("Ignoring fluid '{}' in '{}' of mod {}: not a valid fluid ID", entry, FLUIDLOGGABLE_KEY, mod.getMetadata().getId());
                        continue;
                    }
                    fluids.add(id);
                }
            }
            list.addAll(fluids);
            list.add(Constant.Misc.EMPTY);
            list.add(INVALID);
        });

        @Override
        public @NotNull Collection<ResourceLocation> getPossibleValues() {
            return VALUES;
        }

        @Override
        public @NotNull Optional<ResourceLocation> getValue(String name) {
            // Values that aren't in the list (e.g. from a mod that was removed) are left out, so the block loads without a fluid
            return Optional.ofNullable(ResourceLocation.tryParse(name.replace(DOT_REP, ".").replace(DASH_REP, "-").replace(COLON_REP, ":")))
                    .filter(VALUES::contains);
        }

        @Override
        public @NotNull String getName(ResourceLocation value) {
            if (value.toString().contains(DOT_REP) || value.toString().contains(DASH_REP) || value.toString().contains(COLON_REP))
                throw new RuntimeException("Bad fluid!" + value);
            return value.toString().replace(".", DOT_REP).replace("-", DASH_REP).replace(":", COLON_REP);
        }
    };

    /**
     * {@return whether fluid loggable blocks can hold the given fluid}
     */
    static boolean canHold(Fluid fluid) {
        return fluid instanceof FlowingFluid && FLUID.getPossibleValues().contains(BuiltInRegistries.FLUID.getKey(fluid));
    }

    /**
     * Makes fluid loggable blocks give off the light of the fluid they hold, like lava.
     */
    static BlockBehaviour.Properties withFluidLight(BlockBehaviour.Properties properties) {
        return properties.lightLevel(FluidLoggable::getLightEmission);
    }

    /**
     * {@return the light level of the block of the fluid in the given state}
     * This is called when the block is created, so a fluid that isn't registered yet gives off no light.
     */
    static int getLightEmission(BlockState state) {
        FluidState fluidState = createFluidState(state);
        return fluidState.isEmpty() ? 0 : fluidState.createLegacyBlock().getLightEmission();
    }

    /**
     * Applies the effects of the block of the fluid in the given state to an entity inside it, e.g. the blindness of crude oil.
     * Effects that come from the fluid itself, like burning in lava, already work through {@link #createFluidState(BlockState)}.
     */
    static void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        FluidState fluidState = createFluidState(state);
        if (!fluidState.isEmpty()) {
            fluidState.createLegacyBlock().entityInside(level, pos, entity);
        }
    }

    @Override
    default boolean canPlaceLiquid(@Nullable Player player, BlockGetter view, BlockPos pos, BlockState state, Fluid fluid) {
        return canHold(fluid);
    }

    @Override
    default boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!canHold(fluidState.getType()))
            return false;
        if (!hasFluid(state)) {
            if (!world.isClientSide()) {
                world.setBlock(pos, state.setValue(FLUID, BuiltInRegistries.FLUID.getKey(fluidState.getType())).setValue(FlowingFluid.LEVEL, Math.max(fluidState.getAmount(), 1)).setValue(FlowingFluid.FALLING, fluidState.getValue(FlowingFluid.FALLING)), Block.UPDATE_ALL);
                world.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(world));
            }
            return true;
        } else if (BuiltInRegistries.FLUID.getKey(fluidState.getType()).equals(state.getValue(FLUID))) {
            if (!world.isClientSide()) {
                world.setBlock(pos, state.setValue(FlowingFluid.LEVEL, Math.max(fluidState.getAmount(), 1)).setValue(FlowingFluid.FALLING, fluidState.getValue(FlowingFluid.FALLING)), Block.UPDATE_ALL);
                world.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(world));
            }
            return true;
        }
        // replace current grating fluid or make it contains source block
        else if (!BuiltInRegistries.FLUID.get(state.getValue(FLUID)).defaultFluidState().isSource() || !BuiltInRegistries.FLUID.getKey(fluidState.getType()).equals(state.getValue(FLUID))) {
            if (!world.isClientSide()) {
                world.setBlock(pos, state.setValue(FLUID, BuiltInRegistries.FLUID.getKey(fluidState.getType())).setValue(FlowingFluid.LEVEL, Math.max(fluidState.getAmount(), 1)), Block.UPDATE_ALL);
                world.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(world));
            }
            return true;
        } else if (fluidState.getType() == Fluids.EMPTY) {
            world.setBlock(pos, state.setValue(FLUID, Constant.Misc.EMPTY).setValue(FlowingFluid.LEVEL, 1).setValue(FlowingFluid.FALLING, false), Block.UPDATE_ALL);
        }
        return false;
    }

    @Override
    default @NotNull ItemStack pickupBlock(@Nullable Player player, LevelAccessor world, BlockPos pos, BlockState state) {
        if (hasFluid(state)) {
            if (BuiltInRegistries.FLUID.get(state.getValue(FLUID)).defaultFluidState().isSource()) {
                world.setBlock(pos, state.setValue(FLUID, Constant.Misc.EMPTY).setValue(FlowingFluid.LEVEL, 1), Block.UPDATE_ALL);
                return new ItemStack(BuiltInRegistries.FLUID.get(state.getValue(FLUID)).getBucket());
            }
        }
        return ItemStack.EMPTY;
    }

    static boolean hasFluid(BlockState state) {
        return !(state.getValue(FLUID).equals(Constant.Misc.EMPTY) || state.getValue(FLUID).equals(INVALID));
    }

    @Override
    default @NotNull Optional<SoundEvent> getPickupSound() {
        return Optional.empty();
    }

    static FluidState createFluidState(BlockState blockState) {
        if (!hasFluid(blockState)) {
            return EMPTY_STATE;
        }

        FluidState fluidState = BuiltInRegistries.FLUID.get(blockState.getValue(FLUID)).defaultFluidState();

        if (fluidState.getValues().containsKey(FlowingFluid.LEVEL)) {
            fluidState = fluidState.setValue(FlowingFluid.LEVEL, blockState.getValue(FlowingFluid.LEVEL));
        }
        if (fluidState.getValues().containsKey(FlowingFluid.FALLING)) {
            fluidState = fluidState.setValue(FlowingFluid.FALLING, blockState.getValue(FlowingFluid.FALLING));
        }
        return fluidState;
    }

    static void tryScheduleFluidTick(LevelAccessor level, BlockState blockState, BlockPos pos) {
        if (hasFluid(blockState)) {
            level.scheduleTick(pos, BuiltInRegistries.FLUID.get(blockState.getValue(FLUID)), BuiltInRegistries.FLUID.get(blockState.getValue(FLUID)).getTickDelay(level));
        }
    }

    static <O, S extends StateHolder<O,S>> S applyDefaultState(S state) {
        return state
                .setValue(FLUID, INVALID)
                .setValue(FlowingFluid.LEVEL, 8)
                .setValue(FlowingFluid.FALLING, false);
    }

    static <O, S extends StateHolder<O,S>> void addStateDefinitions(StateDefinition.Builder<O, S> builder) {
        builder.add(FLUID, FlowingFluid.LEVEL, FlowingFluid.FALLING);
    }

    static BlockState applyFluidState(Level level, BlockState blockState, BlockPos pos) {
        FluidState fluidState = level.getFluidState(pos);
        if (!canHold(fluidState.getType())) {
            fluidState = EMPTY_STATE;
        }
        return blockState
                .setValue(FLUID, BuiltInRegistries.FLUID.getKey(fluidState.getType()))
                .setValue(FlowingFluid.LEVEL, Math.max(fluidState.getAmount(), 1))
                .setValue(FlowingFluid.FALLING, fluidState.hasProperty(FlowingFluid.FALLING) ? fluidState.getValue(FlowingFluid.FALLING) : false);
    }
}