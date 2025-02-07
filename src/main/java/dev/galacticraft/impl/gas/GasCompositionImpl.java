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

package dev.galacticraft.impl.gas;

import dev.galacticraft.api.gas.GasComposition;
import dev.galacticraft.api.gas.Gases;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

public record GasCompositionImpl(@NotNull Object2DoubleMap<ResourceKey<Fluid>> composition, double temperature, float pressure) implements GasComposition {
    public static GasComposition readPacket(@NotNull FriendlyByteBuf buf) {
        int size = buf.readInt();
        Builder builder = new Builder();
        builder.pressure(buf.readFloat());
        builder.temperature(buf.readDouble());
        for (int i = 0; i < size; i++) {
            builder.gas(ResourceKey.create(Registries.FLUID, buf.readResourceLocation()), buf.readDouble());
        }
        return builder.build();
    }

    public boolean breathable() {
        double oxygen = this.composition().getOrDefault(ResourceKey.create(Registries.FLUID, Gases.OXYGEN_ID), 0.0);
        return oxygen > 195000.0 && oxygen < 235000.0; //195000ppm to 235000ppm (19.5% to 23.5%)
    }

    public void writePacket(@NotNull FriendlyByteBuf buf) {
        buf.writeInt(this.composition.size());
        buf.writeFloat(this.pressure);
        buf.writeDouble(this.temperature);
        for (Object2DoubleMap.Entry<ResourceKey<Fluid>> entry : this.composition.object2DoubleEntrySet()) {
            buf.writeResourceLocation(entry.getKey().location());
            buf.writeDouble(entry.getDoubleValue());
        }
    }
}
