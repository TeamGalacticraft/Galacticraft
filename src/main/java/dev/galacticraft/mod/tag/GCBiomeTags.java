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

package dev.galacticraft.mod.tag;

import dev.galacticraft.mod.Constant;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class GCBiomeTags {
    public static final TagKey<Biome> MOON = TagKey.create(Registries.BIOME, Constant.id("moon"));
    public static final TagKey<Biome> VENUS = TagKey.create(Registries.BIOME, Constant.id("venus"));
    public static final TagKey<Biome> ASTEROID = TagKey.create(Registries.BIOME, Constant.id("asteroid"));

    public static final TagKey<Biome> MOON_PILLAGER_BASE_HAS_STRUCTURE = TagKey.create(Registries.BIOME, Constant.id("has_structure/moon_pillager_base"));
    public static final TagKey<Biome> MOON_VILLAGE_HIGHLANDS_HAS_STRUCTURE = TagKey.create(Registries.BIOME, Constant.id("has_structure/moon_village_highlands"));
    public static final TagKey<Biome> MOON_RUINS_HAS_STRUCTURE = TagKey.create(Registries.BIOME, Constant.id("has_structure/moon_ruins"));
    public static final TagKey<Biome> MOON_BOSS_HAS_STRUCTURE = TagKey.create(Registries.BIOME, Constant.id("has_structure/moon_boss"));

    public static void register() {
    }
}
