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

public record RocketData(
        RocketLayout layout,
        int color
) {
    public static final Codec<RocketData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RocketLayout.CODEC.fieldOf("layout").forGetter(RocketData::layout),
            Codec.INT.optionalFieldOf("color", 0xFFFFFFFF).forGetter(RocketData::color)
    ).apply(instance, RocketData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RocketData> STREAM_CODEC = StreamCodec.composite(
            RocketLayout.STREAM_CODEC,
            RocketData::layout,
            ByteBufCodecs.INT,
            RocketData::color,
            RocketData::new
    );

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
        return this.layout.isValid();
    }

    public DataComponentPatch asPatch() {
        DataComponentPatch.Builder builder = DataComponentPatch.builder();
        maybeSet(builder, GCDataComponents.ROCKET_DATA, this);
        return builder.build();
    }

    public boolean canTravel(@NotNull HolderLookup.Provider lookup, CelestialBody<?, ?> from, CelestialBody<?, ?> to) {
        return this.layout.canTravel(lookup, from, to);
    }

    private <T> void maybeSet(DataComponentPatch.Builder builder, DataComponentType<T> type, @Nullable T value) {
        if (value != null) {
            builder.set(type, value);
        } else {
            builder.remove(type);
        }
    }
}
