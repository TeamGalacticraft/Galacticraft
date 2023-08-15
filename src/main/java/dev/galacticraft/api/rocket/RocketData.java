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

import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.impl.rocket.RocketDataImpl;
import dev.galacticraft.mod.Constant;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

public interface RocketData {
    ResourceLocation INVALID_ID = Constant.Misc.INVALID;

    @Contract("_, _, _, _, _, _, _ -> new")
    static @NotNull RocketData create(int color, ResourceLocation cone, ResourceLocation body, ResourceLocation fin, ResourceLocation booster, ResourceLocation bottom, ResourceLocation[] upgrades) {
        assert cone != INVALID_ID
                && body != INVALID_ID
                && fin != INVALID_ID
                && booster != INVALID_ID
                && bottom != INVALID_ID;
        return new RocketDataImpl(color, cone, body, fin, booster, bottom, upgrades);
    }

    @Unmodifiable
    static RocketData fromNbt(@Nullable CompoundTag nbt) {
        return RocketDataImpl.fromNbt(nbt == null ? RocketDataImpl.DEFAULT_ROCKET : nbt);
    }

    @Contract(pure = true)
    static @NotNull @Unmodifiable RocketData empty() {
        return RocketDataImpl.empty();
    }

    CompoundTag toNbt(CompoundTag nbt);

    int color();

    default int red() {
        return this.color() >> 16 & 0xFF;
    }

    default int green() {
        return this.color() >> 8 & 0xFF;
    }

    default int blue() {
        return this.color() & 0xFF;
    }

    default int alpha() {
        return this.color() >> 24 & 0xFF;
    }

    default int upgradeCount() {
        return this.upgrades().length;
    }

    ResourceLocation cone();

    ResourceLocation body();

    ResourceLocation fin();

    ResourceLocation booster();

    ResourceLocation bottom();

    ResourceLocation[] upgrades();

    boolean isEmpty();

    boolean canTravel(RegistryAccess manager, CelestialBody<?, ?> from, CelestialBody<?, ?> to);

}
