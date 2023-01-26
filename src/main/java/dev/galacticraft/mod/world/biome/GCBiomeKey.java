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

package dev.galacticraft.mod.world.biome;

import dev.galacticraft.mod.Constant;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public interface GCBiomeKey {
    interface Moon {
        ResourceKey<Biome> HIGHLANDS = key(Constant.Biome.Moon.HIGHLANDS);
        ResourceKey<Biome> HIGHLANDS_FLAT = key(Constant.Biome.Moon.HIGHLANDS_FLAT);
        ResourceKey<Biome> HIGHLANDS_HILLS = key(Constant.Biome.Moon.HIGHLANDS_HILLS);
        ResourceKey<Biome> HIGHLANDS_EDGE = key(Constant.Biome.Moon.HIGHLANDS_EDGE);
        ResourceKey<Biome> HIGHLANDS_VALLEY = key(Constant.Biome.Moon.HIGHLANDS_VALLEY);
        ResourceKey<Biome> MARE = key(Constant.Biome.Moon.MARE);
        ResourceKey<Biome> MARE_FLAT = key(Constant.Biome.Moon.MARE_FLAT);
        ResourceKey<Biome> MARE_HILLS = key(Constant.Biome.Moon.MARE_HILLS);
        ResourceKey<Biome> MARE_EDGE = key(Constant.Biome.Moon.MARE_EDGE);
        ResourceKey<Biome> MARE_VALLEY = key(Constant.Biome.Moon.MARE_VALLEY);
    }

    private static ResourceKey<Biome> key(String id) {
        return ResourceKey.create(Registry.BIOME_REGISTRY, Constant.id(id));
    }
}
