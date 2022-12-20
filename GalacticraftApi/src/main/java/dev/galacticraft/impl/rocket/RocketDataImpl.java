/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.api.rocket.part.RocketPartType;
import dev.galacticraft.api.rocket.travelpredicate.TravelPredicateType;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.impl.Constant;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@ApiStatus.Internal
public record RocketDataImpl(int color, ResourceLocation cone, ResourceLocation body, ResourceLocation fin, ResourceLocation booster,
                             ResourceLocation bottom, ResourceLocation upgrade) implements RocketData {
    public static final RocketDataImpl EMPTY = new RocketDataImpl(0xffffffff, new ResourceLocation(Constant.MOD_ID, "invalid"), new ResourceLocation(Constant.MOD_ID, "invalid"), new ResourceLocation(Constant.MOD_ID, "invalid"), new ResourceLocation(Constant.MOD_ID, "invalid"), new ResourceLocation(Constant.MOD_ID, "invalid"), new ResourceLocation(Constant.MOD_ID, "invalid"));

    public static RocketDataImpl fromNbt(@NotNull CompoundTag nbt) {
        if (nbt.getBoolean("Empty")) return empty();
        return new RocketDataImpl(
                nbt.getInt("Color"),
                new ResourceLocation(nbt.getString("cone")),
                new ResourceLocation(nbt.getString("body")),
                new ResourceLocation(nbt.getString("fin")),
                new ResourceLocation(nbt.getString("booster")),
                new ResourceLocation(nbt.getString("bottom")),
                new ResourceLocation(nbt.getString("upgrade"))
        );
    }

    public static RocketDataImpl empty() {
        return EMPTY;
    }

    @Contract("_ -> param1")
    @Override
    public @NotNull CompoundTag toNbt(@NotNull CompoundTag nbt) {
        if (this.isEmpty()) {
            nbt.putBoolean("Empty", true);
            return nbt;
        }
        nbt.putInt("color", this.color());
        nbt.putString("cone", this.cone().toString());
        nbt.putString("body", this.body().toString());
        nbt.putString("fin", this.fin().toString());
        nbt.putString("booster", this.booster().toString());
        nbt.putString("bottom", this.bottom().toString());
        nbt.putString("upgrade", this.upgrade().toString());
        return nbt;
    }

    @Override
    public int red() {
        return this.color() >> 16 & 0xFF;
    }

    @Override
    public int green() {
        return this.color() >> 8 & 0xFF;
    }

    @Override
    public int blue() {
        return this.color() & 0xFF;
    }

    @Override
    public int alpha() {
        return this.color() >> 24 & 0xFF;
    }

    @Override
    public boolean isEmpty() {
        return this == EMPTY;
    }

    @Override
    public boolean canTravelTo(RegistryAccess manager, CelestialBody<?, ?> celestialBodyType) {
        Object2BooleanMap<RocketPart> map = new Object2BooleanArrayMap<>();
        TravelPredicateType.AccessType type = TravelPredicateType.AccessType.PASS;
        Registry<RocketPart> registry = RocketPart.getRegistry(manager);
        for (ResourceLocation part : this.parts()) {
            RocketPart rocketPart = RocketPart.getById(registry, part);
            map.put(rocketPart, true);
            type = type.merge(this.travel(manager, rocketPart, celestialBodyType, map));
        }
        return type == TravelPredicateType.AccessType.ALLOW;
    }

    private TravelPredicateType.AccessType travel(RegistryAccess manager, @NotNull RocketPart part, CelestialBody<?, ?> type, Object2BooleanMap<RocketPart> map) {
        return part.travelPredicate().canTravelTo(type, p -> map.computeBooleanIfAbsent((RocketPart) p, p1 -> {
            if (Arrays.asList(this.parts()).contains(p1)) {
                map.put((RocketPart) p, false);
                return travel(manager, p1, type, map) != TravelPredicateType.AccessType.BLOCK;
            } else {
                return false;
            }
        }));
    }

    @Override
    public ResourceLocation getPartForType(@NotNull RocketPartType type) {
        return this.parts()[type.ordinal()];
    }

    @Contract(" -> new")
    @Override
    public ResourceLocation @NotNull [] parts() {
        return new ResourceLocation[]{this.cone(), this.body(), this.fin(), this.booster(), this.bottom(), this.upgrade()};
    }
}
