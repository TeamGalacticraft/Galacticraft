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

package dev.galacticraft.mod.content;

import com.google.common.collect.ImmutableMap;
import dev.galacticraft.mod.Constant;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Helper class to make registering things cleaner
 */
public class GCRegistry<T> {
    private final Registry<T> registry;
    private final List<Holder.Reference<T>> entries = new ArrayList<>();

    public GCRegistry(Registry<T> registry) {
        this.registry = registry;
    }

    protected ResourceLocation getId(String id) {
        return Constant.id(id);
    }

    public <V extends T> V register(String id, V object) {
        entries.add(Registry.registerForHolder(registry, getId(id), object));
        return object;
    }

    public <V extends T> Holder.Reference<V> registerForHolder(String id, V object) {
        var holder = Registry.registerForHolder(registry, getId(id), object);
        entries.add(holder);
        return (Holder.Reference<V>) holder;
    }

    public <V extends T> ColorSet<V> registerColored(String id, Function<DyeColor, V> consumer) {
        ImmutableMap.Builder<DyeColor, V> colorMap = new ImmutableMap.Builder<>();
        for (DyeColor color : DyeColor.values()) {
            colorMap.put(color, register(color.getName() + '_' + id, consumer.apply(color)));
        }

        return new ColorSet<>(colorMap.build());
    }

    public List<Holder.Reference<T>> getEntries() {
        return entries;
    }

    public record ColorSet<T>(Map<DyeColor, T> colorMap) {
        public T get(DyeColor color) {
            return colorMap.get(color);
        }
    }
}