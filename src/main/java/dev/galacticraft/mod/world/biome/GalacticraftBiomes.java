/*
 * Copyright (c) 2019-2021 Team Galacticraft
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
public class GalacticraftBiomes {

    public static class Moon {
        public static final RegistryKey<Biome> HIGHLANDS_PLAINS = RegistryKey.of(Registry.BIOME_KEY, new Identifier(Constant.MOD_ID, Constant.Biome.Moon.HIGHLANDS_PLAINS));
        public static final RegistryKey<Biome> HIGHLANDS_ROCKS = RegistryKey.of(Registry.BIOME_KEY, new Identifier(Constant.MOD_ID, Constant.Biome.Moon.HIGHLANDS_ROCKS));
        public static final RegistryKey<Biome> HIGHLANDS_VALLEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier(Constant.MOD_ID, Constant.Biome.Moon.HIGHLANDS_VALLEY));
        public static final RegistryKey<Biome> MARE_PLAINS = RegistryKey.of(Registry.BIOME_KEY, new Identifier(Constant.MOD_ID, Constant.Biome.Moon.MARE_PLAINS));
        public static final RegistryKey<Biome> MARE_ROCKS = RegistryKey.of(Registry.BIOME_KEY, new Identifier(Constant.MOD_ID, Constant.Biome.Moon.MARE_ROCKS));
        public static final RegistryKey<Biome> MARE_EDGE = RegistryKey.of(Registry.BIOME_KEY, new Identifier(Constant.MOD_ID, Constant.Biome.Moon.MARE_EDGE));

        private static void init() {
        }
    }

    public static void register() {
        Moon.init();
    }
}
