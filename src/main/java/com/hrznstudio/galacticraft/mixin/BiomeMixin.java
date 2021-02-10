/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.accessor.GCBiomePropertyAccessor;
import com.hrznstudio.galacticraft.api.biome.BiomeProperty;
import com.hrznstudio.galacticraft.api.biome.BiomePropertyType;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Mixin(Biome.class)
public abstract class BiomeMixin implements GCBiomePropertyAccessor {
    @Unique
    private final Map<BiomePropertyType<?>, BiomeProperty<?>> properties = new HashMap<>();

    @Override
    public <T> T getProperty(@NotNull BiomePropertyType<T> type) {
        return (T) properties.getOrDefault(type, type.create()).getValue();
    }

    @Override
    public <T> void setProperty(BiomePropertyType<T> type, T value) {
        this.properties.put(type, type.create(value));
    }

    @Override
    public Map<BiomePropertyType<?>, BiomeProperty<?>> getProperties() {
        return properties;
    }
}
