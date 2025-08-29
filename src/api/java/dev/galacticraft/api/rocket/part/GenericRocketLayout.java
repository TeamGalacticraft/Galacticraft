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

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.api.rocket.travelpredicate.TravelPredicateType;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.EitherHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record GenericRocketLayout(
        Optional<EitherHolder<RocketCone<?, ?>>> cone,
        Optional<EitherHolder<RocketBody<?, ?>>> body,
        Optional<EitherHolder<RocketFin<?, ?>>> fin,
        Optional<EitherHolder<RocketBooster<?, ?>>> booster,
        Optional<EitherHolder<RocketEngine<?, ?>>> engine,
        Optional<EitherHolder<RocketUpgrade<?, ?>>> upgrade
) implements RocketLayout {
    public static final Codec<GenericRocketLayout> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RocketCone.EITHER_CODEC.optionalFieldOf("cone").forGetter(GenericRocketLayout::cone),
            RocketBody.EITHER_CODEC.optionalFieldOf("body").forGetter(GenericRocketLayout::body),
            RocketFin.EITHER_CODEC.optionalFieldOf("fin").forGetter(GenericRocketLayout::fin),
            RocketBooster.EITHER_CODEC.optionalFieldOf("booster").forGetter(GenericRocketLayout::booster),
            RocketEngine.EITHER_CODEC.optionalFieldOf("engine").forGetter(GenericRocketLayout::engine),
            RocketUpgrade.EITHER_CODEC.optionalFieldOf("upgrade").forGetter(GenericRocketLayout::upgrade)
    ).apply(instance, GenericRocketLayout::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, GenericRocketLayout> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(RocketCone.EITHER_STREAM_CODEC),
            GenericRocketLayout::cone,
            ByteBufCodecs.optional(RocketBody.EITHER_STREAM_CODEC),
            GenericRocketLayout::body,
            ByteBufCodecs.optional(RocketFin.EITHER_STREAM_CODEC),
            GenericRocketLayout::fin,
            ByteBufCodecs.optional(RocketBooster.EITHER_STREAM_CODEC),
            GenericRocketLayout::booster,
            ByteBufCodecs.optional(RocketEngine.EITHER_STREAM_CODEC),
            GenericRocketLayout::engine,
            ByteBufCodecs.optional(RocketUpgrade.EITHER_STREAM_CODEC),
            GenericRocketLayout::upgrade,
            GenericRocketLayout::new
    );

    public static GenericRocketLayout of(
            @Nullable EitherHolder<RocketCone<?, ?>> cone,
            @Nullable EitherHolder<RocketBody<?, ?>> body,
            @Nullable EitherHolder<RocketFin<?, ?>> fin,
            @Nullable EitherHolder<RocketBooster<?, ?>> booster,
            @Nullable EitherHolder<RocketEngine<?, ?>> engine,
            @Nullable EitherHolder<RocketUpgrade<?, ?>> upgrade) {
        return new GenericRocketLayout(Optional.ofNullable(cone), Optional.ofNullable(body), Optional.ofNullable(fin), Optional.ofNullable(booster), Optional.ofNullable(engine), Optional.ofNullable(upgrade));
    }

    @Override
    public Codec<? extends GenericRocketLayout> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends GenericRocketLayout> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public List<EitherHolder<? extends RocketPart<?, ?>>> getParts() {
        ImmutableList.Builder<EitherHolder<? extends RocketPart<?, ?>>> builder = ImmutableList.builder();
        cone.ifPresent(builder::add);
        body.ifPresent(builder::add);
        fin.ifPresent(builder::add);
        booster.ifPresent(builder::add);
        engine.ifPresent(builder::add);
        upgrade.ifPresent(builder::add);
        return builder.build();
    }

    @Override
    public boolean isValid() {
        return this.cone().isPresent() && this.body().isPresent() && this.fin().isPresent() && this.engine().isPresent();
    }

    @Override
    public boolean canTravel(HolderLookup.@NotNull Provider lookup, CelestialBody<?, ?> from, CelestialBody<?, ?> to) {
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

    private static <T> @Nullable Holder<T> maybeGet(@NotNull HolderLookup.Provider lookup, Optional<EitherHolder<T>> holder) {
        return holder.isPresent() ? holder.get().unwrap(lookup).orElse(null) : null;
    }
}
