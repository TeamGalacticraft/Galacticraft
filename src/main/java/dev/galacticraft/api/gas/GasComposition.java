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

package dev.galacticraft.api.gas;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.impl.codec.MapCodec;
import dev.galacticraft.impl.gas.GasCompositionImpl;
import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface GasComposition {
    MapCodec<ResourceKey<Fluid>, Double, Object2DoubleMap<ResourceKey<Fluid>>> MAP_CODEC = MapCodec.create(Object2DoubleArrayMap::new, ResourceKey.codec(Registries.FLUID), Codec.DOUBLE);
    Codec<GasComposition> CODEC = RecordCodecBuilder.create(atmosphericInfoInstance -> atmosphericInfoInstance.group(
            MAP_CODEC.fieldOf("composition").forGetter(GasComposition::composition),
            Codec.DOUBLE.fieldOf("temperature").forGetter(GasComposition::temperature),
            Codec.FLOAT.fieldOf("pressure").forGetter(GasComposition::pressure)
    ).apply(atmosphericInfoInstance, GasComposition::create));

    @Contract("_, _, _ -> new")
    static @NotNull GasComposition create(@NotNull Object2DoubleMap<ResourceKey<Fluid>> composition, double temperature, float pressure) {
        return new GasCompositionImpl(composition, temperature, pressure);
    }

    static GasComposition readPacket(@NotNull FriendlyByteBuf buf) {
        return GasCompositionImpl.readPacket(buf);
    }

    boolean breathable();

    void writePacket(FriendlyByteBuf buf);

    @NotNull Object2DoubleMap<ResourceKey<Fluid>> composition();

    double temperature();

    float pressure();

    class Builder {
        private final Object2DoubleMap<ResourceKey<Fluid>> composition = new Object2DoubleArrayMap<>();
        private double temperature = 15.0;
        private float pressure = 1.0f;

        public Builder temperature(double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder pressure(float pressure) {
            this.pressure = pressure;
            return this;
        }

        public Builder gas(ResourceKey<Fluid> gas, double ppm) {
            this.composition.put(gas, ppm);
            return this;
        }

        public Builder gas(ResourceLocation gas, double ppm) {
            return this.gas(ResourceKey.create(Registries.FLUID, gas), ppm);
        }

        public GasComposition build() {
            return new GasCompositionImpl(this.composition, this.temperature, this.pressure);
        }
    }
}
