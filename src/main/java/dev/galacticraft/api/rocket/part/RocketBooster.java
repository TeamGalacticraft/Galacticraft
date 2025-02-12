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

package dev.galacticraft.api.rocket.part;

import com.mojang.serialization.Codec;
import dev.galacticraft.api.registry.BuiltInRocketRegistries;
import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.api.rocket.part.config.RocketBoosterConfig;
import dev.galacticraft.api.rocket.part.type.RocketBoosterType;
import dev.galacticraft.impl.rocket.part.RocketBoosterImpl;
import dev.galacticraft.mod.util.StreamCodecs;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.item.EitherHolder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public non-sealed interface RocketBooster<C extends RocketBoosterConfig, T extends RocketBoosterType<C>> extends RocketPart<C, T> {
    Codec<RocketBooster<?, ?>> DIRECT_CODEC = BuiltInRocketRegistries.ROCKET_BOOSTER_TYPE.byNameCodec().dispatch(RocketBooster::type, RocketBoosterType::codec);
    Codec<Holder<RocketBooster<?, ?>>> CODEC = RegistryFileCodec.create(RocketRegistries.ROCKET_BOOSTER, DIRECT_CODEC);
    Codec<HolderSet<RocketBooster<?, ?>>> LIST_CODEC = RegistryCodecs.homogeneousList(RocketRegistries.ROCKET_BOOSTER, DIRECT_CODEC);
    StreamCodec<RegistryFriendlyByteBuf, Holder<RocketBooster<?, ?>>> STREAM_CODEC = StreamCodecs.ofHolder(RocketRegistries.ROCKET_BOOSTER);

    Codec<EitherHolder<RocketBooster<?, ?>>> EITHER_CODEC = EitherHolder.codec(RocketRegistries.ROCKET_BOOSTER, CODEC);
    StreamCodec<RegistryFriendlyByteBuf, EitherHolder<RocketBooster<?, ?>>> EITHER_STREAM_CODEC = EitherHolder.streamCodec(RocketRegistries.ROCKET_BOOSTER, STREAM_CODEC);

    @Contract(pure = true, value = "_, _ -> new")
    static @NotNull <C extends RocketBoosterConfig, T extends RocketBoosterType<C>> RocketBooster<C, T> create(@NotNull C config, @NotNull T type) {
        return new RocketBoosterImpl<>(config, type);
    }

    /**
     * Returns the maximum velocity of this booster in blocks/tick.
     *
     * @return the maximum velocity of this booster blocks/tick.
     */
    @Contract(pure = true)
    default double getMaximumVelocity() {
        return this.type().getMaximumVelocity(this.config());
    }

    /**
     * Returns the acceleration of this booster in blocks/tick^2.
     *
     * @return the acceleration of this booster blocks/tick^2.
     */
    @Contract(pure = true)
    default double getAccelerationPerTick() {
        return this.type().getAccelerationPerTick(this.config());
    }

    /**
     * Returns the amount of fuel consumed by this booster per tick.
     *
     * @return the amount of fuel consumed by this booster per tick.
     */
    @Contract(pure = true)
    default long getFuelUsagePerTick() {
        return this.type().getFuelUsagePerTick(this.config());
    }
}
