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

package dev.galacticraft.impl.rocket;

import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.rocket.part.*;
import dev.galacticraft.api.rocket.travelpredicate.TravelPredicateType;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.mod.content.GCRocketParts;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

@ApiStatus.Internal
public record RocketDataImpl(int color, ResourceLocation cone, ResourceLocation body, ResourceLocation fin, ResourceLocation booster,
                             ResourceLocation bottom, ResourceLocation upgrade) implements RocketData {
    public static final CompoundTag DEFAULT_ROCKET = Util.make(new CompoundTag(), tag -> {
        tag.putInt("Tier", 1);
        tag.putInt("Color", 0xFFFFFFFF);
        tag.putString("Cone", GCRocketParts.TIER_1_CONE.location().toString());
        tag.putString("Body", GCRocketParts.TIER_1_BODY.location().toString());
        tag.putString("Fin", GCRocketParts.TIER_1_FIN.location().toString());
        tag.putString("Booster", GCRocketParts.TIER_1_BOOSTER.location().toString());
        tag.putString("Bottom", GCRocketParts.TIER_1_BOTTOM.location().toString());
//        tag.putString("Upgrade", GCRocketParts.STORAGE_UPGRADE.location().toString());
    });

    public static RocketDataImpl fromNbt(CompoundTag nbt) {
        return new RocketDataImpl(
                nbt.getInt("Color"),
                nbt.contains("Cone") ? new ResourceLocation(nbt.getString("Cone")) : null,
                nbt.contains("Body") ? new ResourceLocation(nbt.getString("Body")) : null,
                nbt.contains("Fin") ? new ResourceLocation(nbt.getString("Fin")) : null,
                nbt.contains("Booster") ? new ResourceLocation(nbt.getString("Booster")) : null,
                nbt.contains("Bottom") ? new ResourceLocation(nbt.getString("Bottom")) : null,
                nbt.contains("Upgrade") ? new ResourceLocation(nbt.getString("Upgrade")) : null
        );
    }

    @Contract("_ -> param1")
    @Override
    public @NotNull CompoundTag toNbt(@NotNull CompoundTag nbt) {
        nbt.putInt("Color", this.color());
        if (this.cone != null) nbt.putString("Cone", this.cone.toString());
        if (this.body != null) nbt.putString("Body", this.body.toString());
        if (this.fin != null) nbt.putString("Fin", this.fin.toString());
        if (this.booster != null) nbt.putString("Booster", this.booster.toString());
        if (this.bottom != null) nbt.putString("Bottom", this.bottom.toString());
        if (this.upgrade != null) nbt.putString("Upgrade", this.upgrade.toString());
        return nbt;
    }

    @Override
    public boolean canTravel(@NotNull RegistryAccess manager, CelestialBody<?, ?> from, CelestialBody<?, ?> to) {
        TravelPredicateType.Result type = TravelPredicateType.Result.PASS;
        RocketCone<?, ?> cone = manager.registryOrThrow(RocketRegistries.ROCKET_CONE).get(this.cone());
        RocketBody<?, ?> body = manager.registryOrThrow(RocketRegistries.ROCKET_BODY).get(this.body());
        RocketFin<?, ?> fin = manager.registryOrThrow(RocketRegistries.ROCKET_FIN).get(this.fin());
        RocketBooster<?, ?> booster = manager.registryOrThrow(RocketRegistries.ROCKET_BOOSTER).get(this.booster());
        RocketBottom<?, ?> bottom = manager.registryOrThrow(RocketRegistries.ROCKET_BOTTOM).get(this.bottom());
        RocketUpgrade<?, ?> upgrade = manager.registryOrThrow(RocketRegistries.ROCKET_UPGRADE).get(this.upgrade());

        assert cone != null;
        assert body != null;
        assert fin != null;
        assert booster != null;
        assert bottom != null;
        assert upgrade != null;

        type = type.merge(cone.travelPredicate().canTravel(from, to, cone, body, fin, booster, bottom, upgrade));
        type = type.merge(body.travelPredicate().canTravel(from, to, cone, body, fin, booster, bottom, upgrade));
        type = type.merge(fin.travelPredicate().canTravel(from, to, cone, body, fin, booster, bottom, upgrade));
        type = type.merge(booster.travelPredicate().canTravel(from, to, cone, body, fin, booster, bottom, upgrade));
        type = type.merge(bottom.travelPredicate().canTravel(from, to, cone, body, fin, booster, bottom, upgrade));
        type = type.merge(upgrade.travelPredicate().canTravel(from, to, cone, body, fin, booster, bottom, upgrade));

        return type == TravelPredicateType.Result.ALLOW;
    }
}
