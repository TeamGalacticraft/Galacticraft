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

package dev.galacticraft.api.rocket;

import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.api.rocket.part.*;
import dev.galacticraft.api.rocket.travelpredicate.TravelPredicateType;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.impl.rocket.RocketDataImpl;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FastColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public interface RocketData {
    @Contract("_, _, _, _, _, _, _ -> new")
    static @NotNull RocketData create(int color, @Nullable ResourceKey<RocketCone<?, ?>> cone, @Nullable ResourceKey<RocketBody<?, ?>> body,
                                      @Nullable ResourceKey<RocketFin<?, ?>> fin, @Nullable ResourceKey<RocketBooster<?, ?>> booster,
                                      @Nullable ResourceKey<RocketEngine<?, ?>> engine, @Nullable ResourceKey<RocketUpgrade<?, ?>> upgrade) {
        return new RocketDataImpl(color, cone, body, fin, booster, engine, upgrade);
    }

    @Unmodifiable
    static RocketData fromNbt(@Nullable CompoundTag nbt) {
        return RocketDataImpl.fromNbt(nbt == null ? RocketDataImpl.DEFAULT_ROCKET : nbt);
    }

    @Unmodifiable
    static RocketData fromNetwork(FriendlyByteBuf buf) {
        return RocketDataImpl.fromNetwork(buf);
    }

    int color();

    default int red() {
        return FastColor.ARGB32.red(this.color());
    }

    default int green() {
        return FastColor.ARGB32.green(this.color());
    }

    default int blue() {
        return FastColor.ARGB32.blue(this.color());
    }

    default int alpha() {
        return FastColor.ARGB32.alpha(this.color());
    }

    @Nullable ResourceKey<RocketCone<?, ?>> cone();

    @Nullable ResourceKey<RocketBody<?, ?>> body();

    @Nullable ResourceKey<RocketFin<?, ?>> fin();

    @Nullable ResourceKey<RocketBooster<?, ?>> booster();

    @Nullable ResourceKey<RocketEngine<?, ?>> engine();

    @Nullable ResourceKey<RocketUpgrade<?, ?>> upgrade();

    default void toNbt(@NotNull CompoundTag nbt) {
        nbt.putInt("Color", this.color());
        if (this.cone() != null) nbt.putString("Cone", this.cone().location().toString());
        if (this.body() != null) nbt.putString("Body", this.body().location().toString());
        if (this.fin() != null) nbt.putString("Fin", this.fin().location().toString());
        if (this.booster() != null) nbt.putString("Booster", this.booster().location().toString());
        if (this.engine() != null) nbt.putString("Engine", this.engine().location().toString());
        if (this.upgrade() != null) nbt.putString("Upgrade", this.upgrade().location().toString());
    }

    default void toNetwork(FriendlyByteBuf buf) {
        buf.writeVarInt(this.color());
        buf.writeBoolean(this.cone() != null);
        buf.writeBoolean(this.body() != null);
        buf.writeBoolean(this.fin() != null);
        buf.writeBoolean(this.booster() != null);
        buf.writeBoolean(this.engine() != null);
        buf.writeBoolean(this.upgrade() != null);
        if (this.cone() != null) buf.writeResourceLocation(this.cone().location());
        if (this.body() != null) buf.writeResourceLocation(this.body().location());
        if (this.fin() != null) buf.writeResourceLocation(this.fin().location());
        if (this.booster() != null) buf.writeResourceLocation(this.booster().location());
        if (this.engine() != null) buf.writeResourceLocation(this.engine().location());
        if (this.upgrade() != null) buf.writeResourceLocation(this.upgrade().location());
    }

    default boolean isValid() {
        return this.cone() != null && this.body() != null && this.fin() != null && this.engine() != null;
    }

    default boolean canTravel(@NotNull RegistryAccess manager, CelestialBody<?, ?> from, CelestialBody<?, ?> to) {
        if (!this.isValid()) return false;
        TravelPredicateType.Result type = TravelPredicateType.Result.PASS;
        RocketCone<?, ?> cone = manager.registryOrThrow(RocketRegistries.ROCKET_CONE).get(this.cone());
        RocketBody<?, ?> body = manager.registryOrThrow(RocketRegistries.ROCKET_BODY).get(this.body());
        RocketFin<?, ?> fin = manager.registryOrThrow(RocketRegistries.ROCKET_FIN).get(this.fin());
        RocketBooster<?, ?> booster = manager.registryOrThrow(RocketRegistries.ROCKET_BOOSTER).get(this.booster());
        RocketEngine<?, ?> engine = manager.registryOrThrow(RocketRegistries.ROCKET_ENGINE).get(this.engine());
        RocketUpgrade<?, ?> upgrade = manager.registryOrThrow(RocketRegistries.ROCKET_UPGRADE).get(this.upgrade());

        assert cone != null;
        assert body != null;
        assert fin != null;
        assert engine != null;

        type = type.merge(cone.travelPredicate().canTravel(from, to, cone, body, fin, booster, engine, upgrade));
        type = type.merge(body.travelPredicate().canTravel(from, to, cone, body, fin, booster, engine, upgrade));
        type = type.merge(fin.travelPredicate().canTravel(from, to, cone, body, fin, booster, engine, upgrade));
        type = type.merge(booster == null ? TravelPredicateType.Result.PASS : booster.travelPredicate().canTravel(from, to, cone, body, fin, booster, engine, upgrade));
        type = type.merge(engine.travelPredicate().canTravel(from, to, cone, body, fin, booster, engine, upgrade));
        type = type.merge(upgrade == null ? TravelPredicateType.Result.PASS : upgrade.travelPredicate().canTravel(from, to, cone, body, fin, booster, engine, upgrade));

        return type == TravelPredicateType.Result.ALLOW;
    }}
