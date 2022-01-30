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

package dev.galacticraft.mod.world.biome;

import dev.galacticraft.mod.Constant;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public interface GalacticraftBiomeKey {
    interface Moon {
        RegistryKey<Biome> HIGHLANDS = key(Constant.Biome.Moon.HIGHLANDS);
        RegistryKey<Biome> HIGHLANDS_FLAT = key(Constant.Biome.Moon.HIGHLANDS_FLAT);
        RegistryKey<Biome> HIGHLANDS_HILLS = key(Constant.Biome.Moon.HIGHLANDS_HILLS);
        RegistryKey<Biome> HIGHLANDS_EDGE = key(Constant.Biome.Moon.HIGHLANDS_EDGE);
        RegistryKey<Biome> HIGHLANDS_VALLEY = key(Constant.Biome.Moon.HIGHLANDS_VALLEY);
        RegistryKey<Biome> MARE = key(Constant.Biome.Moon.MARE);
        RegistryKey<Biome> MARE_FLAT = key(Constant.Biome.Moon.MARE_FLAT);
        RegistryKey<Biome> MARE_HILLS = key(Constant.Biome.Moon.MARE_HILLS);
        RegistryKey<Biome> MARE_EDGE = key(Constant.Biome.Moon.MARE_EDGE);
        RegistryKey<Biome> MARE_VALLEY = key(Constant.Biome.Moon.MARE_VALLEY);
    }

    private static RegistryKey<Biome> key(String id) {
        return RegistryKey.of(Registry.BIOME_KEY, Constant.id(id));
    }
}
