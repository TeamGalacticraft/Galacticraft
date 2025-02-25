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

package dev.galacticraft.api.rocket;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.api.component.GCDataComponents;
import dev.galacticraft.api.rocket.part.*;
import dev.galacticraft.api.rocket.travelpredicate.TravelPredicateType;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.mod.content.GCRocketParts;
import dev.galacticraft.mod.util.StreamCodecs;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.EitherHolder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record RocketData(Optional<EitherHolder<RocketCone<?, ?>>> cone, Optional<EitherHolder<RocketBody<?, ?>>> body,
                         Optional<EitherHolder<RocketFin<?, ?>>> fin,
                         Optional<EitherHolder<RocketBooster<?, ?>>> booster,
                         Optional<EitherHolder<RocketEngine<?, ?>>> engine,
                         Optional<EitherHolder<RocketUpgrade<?, ?>>> upgrade, int color
) {
    public RocketData(@Nullable EitherHolder<RocketCone<?, ?>> cone, @Nullable EitherHolder<RocketBody<?, ?>> body,
                      @Nullable EitherHolder<RocketFin<?, ?>> fin,
                      @Nullable EitherHolder<RocketBooster<?, ?>> booster,
                      @Nullable EitherHolder<RocketEngine<?, ?>> engine,
                      @Nullable EitherHolder<RocketUpgrade<?, ?>> upgrade, int color) {
        this(Optional.ofNullable(cone), Optional.ofNullable(body), Optional.ofNullable(fin), Optional.ofNullable(booster), Optional.ofNullable(engine), Optional.ofNullable(upgrade), color);
    }
    public static final Codec<RocketData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RocketCone.EITHER_CODEC.optionalFieldOf("cone").forGetter(RocketData::cone),
            RocketBody.EITHER_CODEC.optionalFieldOf("body").forGetter(RocketData::body),
            RocketFin.EITHER_CODEC.optionalFieldOf("fin").forGetter(RocketData::fin),
            RocketBooster.EITHER_CODEC.optionalFieldOf("booster").forGetter(RocketData::booster),
            RocketEngine.EITHER_CODEC.optionalFieldOf("engine").forGetter(RocketData::engine),
            RocketUpgrade.EITHER_CODEC.optionalFieldOf("upgrade").forGetter(RocketData::upgrade),
            Codec.INT.fieldOf("color").forGetter(RocketData::color)
            ).apply(instance, RocketData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RocketData> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistriesTrusted(CODEC);

    @Contract("_, _, _, _, _, _, _ -> new")
    static @NotNull RocketData create(int color, @Nullable Holder<RocketCone<?, ?>> cone, @Nullable Holder<RocketBody<?, ?>> body,
                                      @Nullable Holder<RocketFin<?, ?>> fin, @Nullable Holder<RocketBooster<?, ?>> booster,
                                      @Nullable Holder<RocketEngine<?, ?>> engine, @Nullable Holder<RocketUpgrade<?, ?>> upgrade) {
        return new RocketData(maybeHolder(cone), maybeHolder(body), maybeHolder(fin), maybeHolder(booster), maybeHolder(engine), maybeHolder(upgrade), color);
    }

    @Contract("_, _, _, _, _, _, _ -> new")
    static @NotNull RocketData create(int color, @Nullable ResourceKey<RocketCone<?, ?>> cone, @Nullable ResourceKey<RocketBody<?, ?>> body,
                                      @Nullable ResourceKey<RocketFin<?, ?>> fin, @Nullable ResourceKey<RocketBooster<?, ?>> booster,
                                      @Nullable ResourceKey<RocketEngine<?, ?>> engine, @Nullable ResourceKey<RocketUpgrade<?, ?>> upgrade) {
        return new RocketData(maybeHolder(cone), maybeHolder(body), maybeHolder(fin), maybeHolder(booster), maybeHolder(engine), maybeHolder(upgrade), color);
    }

    @Contract("_, _, _, _, _, _, _ -> new")
    static @NotNull RocketData create(int color, @Nullable EitherHolder<RocketCone<?, ?>> cone, @Nullable EitherHolder<RocketBody<?, ?>> body,
                                      @Nullable EitherHolder<RocketFin<?, ?>> fin, @Nullable EitherHolder<RocketBooster<?, ?>> booster,
                                      @Nullable EitherHolder<RocketEngine<?, ?>> engine, @Nullable EitherHolder<RocketUpgrade<?, ?>> upgrade) {
        return new RocketData(Optional.of(cone), Optional.of(body), Optional.of(fin), Optional.of(booster), Optional.of(engine), Optional.of(upgrade), color);
    }

    //TODO: HSV?
    public int red() {
        return FastColor.ARGB32.red(this.color());
    }

    public int green() {
        return FastColor.ARGB32.green(this.color());
    }

    public int blue() {
        return FastColor.ARGB32.blue(this.color());
    }

    public int alpha() {
        return FastColor.ARGB32.alpha(this.color());
    }

    public boolean isValid() {
        return this.cone() != null && this.body() != null && this.fin() != null && this.engine() != null;
    }

    public DataComponentPatch asPatch() {
        DataComponentPatch.Builder builder = DataComponentPatch.builder();
        maybeSet(builder, GCDataComponents.ROCKET_DATA, this);
        return builder.build();
    }

    public boolean canTravel(@NotNull HolderLookup.Provider lookup, CelestialBody<?, ?> from, CelestialBody<?, ?> to) {
        Holder<RocketCone<?, ?>> cone = maybeGet(lookup, this.cone);
        Holder<RocketBody<?, ?>> body = maybeGet(lookup, this.body);
        Holder<RocketFin<?, ?>> fin = maybeGet(lookup, this.fin);
        Holder<RocketBooster<?, ?>> booster = maybeGet(lookup, this.booster);
        Holder<RocketEngine<?, ?>> engine = maybeGet(lookup, this.engine);
        Holder<RocketUpgrade<?, ?>> upgrade = maybeGet(lookup, this.upgrade);

        if (cone != null && body != null && fin != null && engine != null) {
            TravelPredicateType.Result type = TravelPredicateType.Result.PASS;
            type = type.merge(cone.value().travelPredicate().canTravel(from, to, cone, body, fin, booster, engine, upgrade));
            type = type.merge(body.value().travelPredicate().canTravel(from, to, cone, body, fin, booster, engine, upgrade));
            type = type.merge(fin.value().travelPredicate().canTravel(from, to, cone, body, fin, booster, engine, upgrade));
            type = type.merge(booster == null ? TravelPredicateType.Result.PASS : booster.value().travelPredicate().canTravel(from, to, cone, body, fin, booster, engine, upgrade));
            type = type.merge(engine.value().travelPredicate().canTravel(from, to, cone, body, fin, booster, engine, upgrade));
            type = type.merge(upgrade == null ? TravelPredicateType.Result.PASS : upgrade.value().travelPredicate().canTravel(from, to, cone, body, fin, booster, engine, upgrade));
            return type == TravelPredicateType.Result.ALLOW;
        }

        return false;
    }

    private static <T> Optional<EitherHolder<T>> maybeHolder(@Nullable Holder<T> holder) {
        return holder == null ? Optional.empty() : Optional.of(new EitherHolder<>(holder));
    }

    private static <T> Optional<EitherHolder<T>> maybeHolder(@Nullable ResourceKey<T> key) {
        return key == null ? Optional.empty() : Optional.of(new EitherHolder<>(key));
    }

    private static <T> @Nullable Holder<T> maybeGet(@NotNull HolderLookup.Provider lookup, Optional<EitherHolder<T>> holder) {
        return holder.isPresent() ? holder.get().unwrap(lookup).orElse(null) : null;
    }

    private <T> void maybeSet(DataComponentPatch.Builder builder, DataComponentType<T> type, @Nullable T value) {
        if (value != null) {
            builder.set(type, value);
        } else {
            builder.remove(type);
        }
    }
}
