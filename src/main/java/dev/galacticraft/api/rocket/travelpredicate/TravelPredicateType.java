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

package dev.galacticraft.api.rocket.travelpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.galacticraft.api.registry.BuiltInRocketRegistries;
import dev.galacticraft.api.rocket.part.*;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import net.minecraft.core.Holder;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public abstract class TravelPredicateType<C extends TravelPredicateConfig> {
    private final @NotNull Holder.Reference<TravelPredicateType<?>> reference = BuiltInRocketRegistries.TRAVEL_PREDICATE_TYPE.createIntrusiveHolder(this);
    private final @NotNull MapCodec<ConfiguredTravelPredicate<C, TravelPredicateType<C>>> codec;

    public TravelPredicateType(@NotNull Codec<C> configCodec) {
        this.codec = configCodec.fieldOf("config").xmap(this::configure, ConfiguredTravelPredicate::config);
    }

    public ConfiguredTravelPredicate<C, TravelPredicateType<C>> configure(C config) {
        return new ConfiguredTravelPredicate<>(config, this);
    }

    public abstract Result canTravel(CelestialBody<?, ?> from, CelestialBody<?, ?> to, Holder<RocketCone<?, ?>> cone, Holder<RocketBody<?, ?>> body, Holder<RocketFin<?, ?>> fin, Holder<RocketBooster<?, ?>> booster, Holder<RocketEngine<?, ?>> engine, Holder<RocketUpgrade<?, ?>> upgrade, C config);

    public MapCodec<ConfiguredTravelPredicate<C, TravelPredicateType<C>>> codec() {
        return this.codec;
    }

    @ApiStatus.Internal
    public @NotNull Holder.Reference<TravelPredicateType<?>> getReference() {
        return reference;
    }

    public enum Result implements StringRepresentable {
        /**
         * Allow other rocket parts to decide whether the player may visit the celestial body
         */
        PASS,

        /**
         * Forcefully block access to celestial body - overrides {@link Result#ALLOW ALLOW}
         */
        BLOCK,

        /**
         * Allow access to celestial body.
         * Can be overridden by {@link Result#BLOCK BLOCK}
         */
        ALLOW;

        public static final EnumCodec<Result> CODEC = StringRepresentable.fromEnum(Result::values);

        public Result merge(Result other) {
            if (other == PASS) return this;
            if (this == PASS) return other;
            if (other == BLOCK || this == BLOCK) return BLOCK;
            return ALLOW;
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
