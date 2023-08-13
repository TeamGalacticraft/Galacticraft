/*
 * Copyright (c) 2019-2023 Team Galacticraft
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
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCRocketParts;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.*;

@ApiStatus.Internal
public record RocketDataImpl(int color, ResourceLocation cone, ResourceLocation body, ResourceLocation fin, ResourceLocation booster,
                             ResourceLocation bottom, ResourceLocation[] upgrades) implements RocketData {
    public static final RocketDataImpl EMPTY = new RocketDataImpl(0xffffffff, Constant.Misc.INVALID, Constant.Misc.INVALID, Constant.Misc.INVALID, Constant.Misc.INVALID, Constant.Misc.INVALID, new ResourceLocation[0]);
    public static final CompoundTag DEFAULT_ROCKET = Util.make(new CompoundTag(), tag -> {
        tag.putInt("Tier", 1);
        tag.putInt("Color", 0xFFFFFFFF);
        tag.putString("Cone", GCRocketParts.TIER_1_CONE.location().toString());
        tag.putString("Body", GCRocketParts.TIER_1_BODY.location().toString());
        tag.putString("Fin", GCRocketParts.TIER_1_FIN.location().toString());
        tag.putString("Booster", GCRocketParts.TIER_1_BOOSTER.location().toString());
        tag.putString("Bottom", GCRocketParts.TIER_1_BOTTOM.location().toString());
        tag.put("Upgrade", new ListTag());
    });

    public static RocketDataImpl fromNbt(@Nullable CompoundTag nbt) {
        if (nbt.getBoolean("Empty")) return empty();
        ListTag upgradeList = nbt.getList("Upgrade", Tag.TAG_STRING);
        ResourceLocation[] upgrades = new ResourceLocation[upgradeList.size()];
        for (int i = 0; i < upgradeList.size(); i++) {
            upgrades[i] = new ResourceLocation(upgradeList.getString(i));
        }
        return new RocketDataImpl(
                nbt.getInt("Color"),
                new ResourceLocation(nbt.getString("Cone")),
                new ResourceLocation(nbt.getString("Body")),
                new ResourceLocation(nbt.getString("Fin")),
                new ResourceLocation(nbt.getString("Booster")),
                new ResourceLocation(nbt.getString("Bottom")),
                upgrades
        );
    }

    public static @Unmodifiable @NotNull RocketDataImpl empty() {
        return EMPTY;
    }

    @Contract("_ -> param1")
    @Override
    public @NotNull CompoundTag toNbt(@NotNull CompoundTag nbt) {
        if (this.isEmpty()) {
            nbt.putBoolean("Empty", true);
            return nbt;
        }
        nbt.putInt("Color", this.color());
        nbt.putString("Cone", this.cone().toString());
        nbt.putString("Body", this.body().toString());
        nbt.putString("Fin", this.fin().toString());
        nbt.putString("Booster", this.booster().toString());
        nbt.putString("Bottom", this.bottom().toString());
        ListTag list = new ListTag();
        for (ResourceLocation upgrade : this.upgrades) {
            list.add(StringTag.valueOf(upgrade.toString()));
        }
        nbt.put("Upgrades", list);
        return nbt;
    }

    @Override
    public boolean isEmpty() {
        return this == EMPTY;
    }

    @Override
    public boolean canTravel(@NotNull RegistryAccess manager, CelestialBody<?, ?> from, CelestialBody<?, ?> to) {
        TravelPredicateType.Result type = TravelPredicateType.Result.PASS;
        RocketCone<?, ?> cone = manager.registryOrThrow(RocketRegistries.ROCKET_CONE).get(this.cone());
        RocketBody<?, ?> body = manager.registryOrThrow(RocketRegistries.ROCKET_BODY).get(this.body());
        RocketFin<?, ?> fin = manager.registryOrThrow(RocketRegistries.ROCKET_FIN).get(this.fin());
        RocketBooster<?, ?> booster = manager.registryOrThrow(RocketRegistries.ROCKET_BOOSTER).get(this.booster());
        RocketBottom<?, ?> bottom = manager.registryOrThrow(RocketRegistries.ROCKET_BOTTOM).get(this.bottom());
        RocketUpgrade<?, ?>[] upgrades = new RocketUpgrade[this.upgrades.length];

        Registry<RocketUpgrade<?, ?>> registry = manager.registryOrThrow(RocketRegistries.ROCKET_UPGRADE);
        for (int i = 0; i < this.upgrades.length; i++) {
            upgrades[i] = registry.get(this.upgrades[i]);
            assert upgrades[i] != null;
        }
        assert cone != null;
        assert body != null;
        assert fin != null;
        assert booster != null;
        assert bottom != null;

        type = type.merge(cone.travelPredicate().canTravel(from, to, cone, body, fin, booster, bottom, upgrades));
        type = type.merge(body.travelPredicate().canTravel(from, to, cone, body, fin, booster, bottom, upgrades));
        type = type.merge(fin.travelPredicate().canTravel(from, to, cone, body, fin, booster, bottom, upgrades));
        type = type.merge(booster.travelPredicate().canTravel(from, to, cone, body, fin, booster, bottom, upgrades));
        type = type.merge(bottom.travelPredicate().canTravel(from, to, cone, body, fin, booster, bottom, upgrades));
        for (RocketUpgrade<?, ?> upgrade : upgrades) {
            type = type.merge(upgrade.travelPredicate().canTravel(from, to, cone, body, fin, booster, bottom, upgrades));
        }

        return type == TravelPredicateType.Result.ALLOW;
    }
}
