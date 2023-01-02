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

package dev.galacticraft.api.rocket;

import dev.galacticraft.api.rocket.part.RocketPartType;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.impl.Constant;
import dev.galacticraft.impl.rocket.RocketDataImpl;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public interface RocketData {
    ResourceLocation INVALID_ID = new ResourceLocation(Constant.MOD_ID, "invalid");

    static RocketData create(int color, ResourceLocation cone, ResourceLocation body, ResourceLocation fin, ResourceLocation booster, ResourceLocation bottom, ResourceLocation upgrade) {
        if (cone == INVALID_ID
                || body == INVALID_ID
                || fin == INVALID_ID
                || booster == INVALID_ID
                || bottom == INVALID_ID
                || upgrade == INVALID_ID) return empty();
        return new RocketDataImpl(color, cone, body, fin, booster, bottom, upgrade);
    }

    static @NotNull @Unmodifiable RocketData fromNbt(CompoundTag nbt) {
        return RocketDataImpl.fromNbt(nbt);
    }

    @Contract(pure = true)
    static @NotNull @Unmodifiable RocketData empty() {
        return RocketDataImpl.empty();
    }

    CompoundTag toNbt(CompoundTag nbt);

    int color();

    int red();

    int green();

    int blue();

    int alpha();

    ResourceLocation cone();

    ResourceLocation body();

    ResourceLocation fin();

    ResourceLocation booster();

    ResourceLocation bottom();

    ResourceLocation upgrade();

    boolean isEmpty();

    boolean canTravelTo(RegistryAccess manager, CelestialBody<?, ?> celestialBodyType);

    ResourceLocation getPartForType(RocketPartType type);

    ResourceLocation[] parts();
}
