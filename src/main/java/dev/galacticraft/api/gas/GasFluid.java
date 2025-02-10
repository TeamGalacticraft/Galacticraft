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

package dev.galacticraft.api.gas;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @see Gas
 */
@ApiStatus.Experimental
@Deprecated
public final class GasFluid extends Fluid implements FluidVariantAttributeHandler, Gas {
    @ApiStatus.Internal
    public static final List<GasFluid> GAS_FLUIDS = new ArrayList<>(); // used for registering client hooks

    private final @NotNull Component name;
    private final @NotNull String symbol;
    private final @NotNull ResourceLocation texture;
    private final int tint;
    private final @NotNull Object2IntFunction<FluidVariant> luminance;
    private final @NotNull Object2IntFunction<FluidVariant> viscosity;
    private final @NotNull Optional<SoundEvent> fillSound;
    private final @NotNull Optional<SoundEvent> emptySound;

    private GasFluid(@NotNull Component name, @NotNull ResourceLocation texture, @NotNull String symbol, int tint, @NotNull Object2IntFunction<FluidVariant> luminance, @NotNull Object2IntFunction<FluidVariant> viscosity, @NotNull Optional<SoundEvent> fillSound, @NotNull Optional<SoundEvent> emptySound) {
        this.name = name;
        this.symbol = symbol.replaceAll("0", "₀")
                .replaceAll("1", "₁")
                .replaceAll("2", "₂")
                .replaceAll("3", "₃")
                .replaceAll("4", "₄")
                .replaceAll("5", "₅")
                .replaceAll("6", "₆")
                .replaceAll("7", "₇")
                .replaceAll("8", "₈")
                .replaceAll("9", "₉");
        this.texture = texture;
        this.tint = tint;
        this.luminance = luminance;
        this.viscosity = viscosity;
        this.fillSound = fillSound;
        this.emptySound = emptySound;

        GAS_FLUIDS.add(this);
        FluidVariantAttributes.register(this, this);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull GasFluid create(@NotNull Component name, @NotNull ResourceLocation texture, @NotNull String symbol) {
        return create(name, texture, symbol, v -> 0);
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull GasFluid create(@NotNull Component name, @NotNull ResourceLocation texture, @NotNull String symbol, @NotNull Object2IntFunction<FluidVariant> luminance) {
        return create(name, texture, symbol, luminance, v -> 50);
    }

    @Contract("_, _, _, _, _ -> new")
    public static @NotNull GasFluid create(@NotNull Component name, @NotNull ResourceLocation texture, @NotNull String symbol, @NotNull Object2IntFunction<FluidVariant> luminance, @NotNull Object2IntFunction<FluidVariant> viscosity) {
        return create(name, texture, symbol, luminance, viscosity, Optional.empty(), Optional.empty());
    }

    @Contract("_, _, _, _, _, _, _ -> new")
    public static @NotNull GasFluid create(@NotNull Component name, @NotNull ResourceLocation texture, @NotNull String symbol, @NotNull Object2IntFunction<FluidVariant> luminance, @NotNull Object2IntFunction<FluidVariant> viscosity, @NotNull Optional<SoundEvent> fillSound, @NotNull Optional<SoundEvent> emptySound) {
        return create(name, texture, symbol, 0xFFFFFFFF, luminance, viscosity, fillSound, emptySound);
    }

    @Contract("_, _, _, _ -> new")
    public static @NotNull GasFluid create(@NotNull Component name, @NotNull ResourceLocation texture, @NotNull String symbol, int tint) {
        return create(name, texture, symbol, tint, v -> 0);
    }

    @Contract("_, _, _, _, _ -> new")
    public static @NotNull GasFluid create(@NotNull Component name, @NotNull ResourceLocation texture, @NotNull String symbol, int tint, @NotNull Object2IntFunction<FluidVariant> luminance) {
        return create(name, texture, symbol, tint, luminance, v -> 50);
    }

    @Contract("_, _, _, _, _, _ -> new")
    public static @NotNull GasFluid create(@NotNull Component name, @NotNull ResourceLocation texture, @NotNull String symbol, int tint, @NotNull Object2IntFunction<FluidVariant> luminance, @NotNull Object2IntFunction<FluidVariant> viscosity) {
        return create(name, texture, symbol, tint, luminance, viscosity, Optional.empty(), Optional.empty());
    }

    @Contract("_, _, _, _, _, _, _, _ -> new")
    public static @NotNull GasFluid create(@NotNull Component name, @NotNull ResourceLocation texture, @NotNull String symbol, int tint, @NotNull Object2IntFunction<FluidVariant> luminance, @NotNull Object2IntFunction<FluidVariant> viscosity, @NotNull Optional<SoundEvent> fillSound, @NotNull Optional<SoundEvent> emptySound) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(texture);
        Preconditions.checkNotNull(symbol);
        Preconditions.checkNotNull(luminance);
        Preconditions.checkNotNull(viscosity);
        Preconditions.checkNotNull(fillSound);
        Preconditions.checkNotNull(emptySound);

        return new GasFluid(name, texture, symbol, tint, luminance, viscosity, fillSound, emptySound);
    }

    @Override
    public @NotNull Item getBucket() {
        return Items.AIR;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter world, BlockPos pos, Fluid fluid, Direction direction) {
        return true;
    }

    @Override
    protected @NotNull Vec3 getFlow(BlockGetter world, BlockPos pos, FluidState state) {
        return Vec3.ZERO;
    }

    @Override
    public int getTickDelay(LevelReader world) {
        return 0;
    }

    @Override
    protected float getExplosionResistance() {
        return 0;
    }

    @Override
    public float getHeight(FluidState state, BlockGetter world, BlockPos pos) {
        return 0.0F;
    }

    @Override
    public float getOwnHeight(FluidState state) {
        return 0.0F;
    }

    @Override
    protected @NotNull BlockState createLegacyBlock(FluidState state) {
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSource(FluidState state) {
        return true;
    }

    @Override
    public int getAmount(FluidState state) {
        return 0;
    }

    @Override
    public @NotNull VoxelShape getShape(FluidState state, BlockGetter world, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public Component getName(FluidVariant fluidVariant) {
        return this.name;
    }

    @Override
    public Optional<SoundEvent> getFillSound(FluidVariant variant) {
        return this.fillSound;
    }

    @Override
    public Optional<SoundEvent> getEmptySound(FluidVariant variant) {
        return this.emptySound;
    }

    @Override
    public int getLuminance(FluidVariant variant) {
        return this.luminance.getInt(variant);
    }

    @Override
    public int getViscosity(FluidVariant variant, @Nullable Level world) {
        return this.viscosity.getInt(variant);
    }

    @Override
    public boolean isLighterThanAir(FluidVariant variant) {
        return true;
    }

    public @NotNull ResourceLocation getTexture() {
        return this.texture;
    }

    public int getTint() {
        return this.tint;
    }

    @Override
    public @NotNull Component getName() {
        return this.name;
    }

    @Override
    public @NotNull String getSymbol() {
        return this.symbol;
    }
}
