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
import dev.galacticraft.api.rocket.part.config.RocketEngineConfig;
import dev.galacticraft.api.rocket.part.type.RocketEngineType;
import dev.galacticraft.impl.rocket.part.RocketEngineImpl;
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

public non-sealed interface RocketEngine<C extends RocketEngineConfig, T extends RocketEngineType<C>> extends RocketPart<C, T> {
    Codec<RocketEngine<?, ?>> DIRECT_CODEC = BuiltInRocketRegistries.ROCKET_ENGINE_TYPE.byNameCodec().dispatch(RocketEngine::type, RocketEngineType::codec);
    Codec<Holder<RocketEngine<?, ?>>> CODEC = RegistryFileCodec.create(RocketRegistries.ROCKET_ENGINE, DIRECT_CODEC);
    Codec<HolderSet<RocketEngine<?, ?>>> LIST_CODEC = RegistryCodecs.homogeneousList(RocketRegistries.ROCKET_ENGINE, DIRECT_CODEC);
    StreamCodec<RegistryFriendlyByteBuf, Holder<RocketEngine<?, ?>>> STREAM_CODEC = StreamCodecs.ofHolder(RocketRegistries.ROCKET_ENGINE);

    Codec<EitherHolder<RocketEngine<?, ?>>> EITHER_CODEC = EitherHolder.codec(RocketRegistries.ROCKET_ENGINE, CODEC);
    StreamCodec<RegistryFriendlyByteBuf, EitherHolder<RocketEngine<?, ?>>> EITHER_STREAM_CODEC = EitherHolder.streamCodec(RocketRegistries.ROCKET_ENGINE, STREAM_CODEC);
    
    @Contract(pure = true, value = "_, _ -> new")
    static @NotNull <C extends RocketEngineConfig, T extends RocketEngineType<C>> RocketEngine<C, T> create(@NotNull C config, @NotNull T type) {
        return new RocketEngineImpl<>(config, type);
    }

    /**
     * Returns the fuel capacity of this rocket.
     *
     * @return the fuel capacity of this rocket. 1 bucket = 81000.
     * @see net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
     */
    default long getFuelCapacity() {
        return this.type().getFuelCapacity(this.config());
    }
}
