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

package dev.galacticraft.api.rocket.part;

import com.mojang.serialization.Codec;
import dev.galacticraft.api.codec.StreamCodecs;
import dev.galacticraft.api.registry.BuiltInRocketRegistries;
import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.api.rocket.part.config.RocketFinConfig;
import dev.galacticraft.api.rocket.part.type.RocketFinType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.item.EitherHolder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record RocketFin<C extends RocketFinConfig, T extends RocketFinType<C>>(@NotNull C config, @NotNull T type) implements RocketPart<C, T> {
    public static final Codec<RocketFin<?, ?>> DIRECT_CODEC = BuiltInRocketRegistries.ROCKET_FIN_TYPE.byNameCodec().dispatch(RocketFin::type, RocketFinType::codec);
    public static final Codec<Holder<RocketFin<?, ?>>> CODEC = RegistryFileCodec.create(RocketRegistries.ROCKET_FIN, DIRECT_CODEC);
    public static final Codec<HolderSet<RocketFin<?, ?>>> LIST_CODEC = RegistryCodecs.homogeneousList(RocketRegistries.ROCKET_FIN, DIRECT_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<RocketFin<?, ?>>> STREAM_CODEC = StreamCodecs.ofHolder(RocketRegistries.ROCKET_FIN);

    public static final Codec<EitherHolder<RocketFin<?, ?>>> EITHER_CODEC = EitherHolder.codec(RocketRegistries.ROCKET_FIN, CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, EitherHolder<RocketFin<?, ?>>> EITHER_STREAM_CODEC = EitherHolder.streamCodec(RocketRegistries.ROCKET_FIN, STREAM_CODEC);

    @Contract(pure = true, value = "_, _ -> new")
    public static @NotNull <C extends RocketFinConfig, T extends RocketFinType<C>> RocketFin<C, T> create(@NotNull C config, @NotNull T type) {
        return new RocketFin<>(config, type);
    }

    public boolean canManeuver() {
        return this.type().canManeuver(this.config());
    }
}
