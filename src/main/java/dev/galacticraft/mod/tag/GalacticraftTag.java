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

package dev.galacticraft.mod.tag;

import dev.galacticraft.mod.Constant;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftTag {
    public static final TagKey<Fluid> OIL = TagKey.of(Registry.FLUID_KEY, new Identifier(Constant.COMMON_NAMESPACE, "oil"));
    public static final TagKey<Fluid> FUEL = TagKey.of(Registry.FLUID_KEY, new Identifier(Constant.COMMON_NAMESPACE, "fuel"));
    public static final TagKey<Fluid> LIQUID_OXYGEN = TagKey.of(Registry.FLUID_KEY, new Identifier(Constant.COMMON_NAMESPACE, "oxygen"));

    public static final TagKey<Block> INFINIBURN_MOON = TagKey.of(Registry.BLOCK_KEY, new Identifier(Constant.MOD_ID, "infiniburn_moon"));
    public static final TagKey<Block> BASE_STONE_MOON = TagKey.of(Registry.BLOCK_KEY, new Identifier(Constant.MOD_ID, "base_stone_moon"));
    public static final TagKey<Block> MOON_CARVER_REPLACEABLES = TagKey.of(Registry.BLOCK_KEY, new Identifier(Constant.MOD_ID, "moon_carver_replacables"));
    public static final TagKey<Block> MOON_CRATER_CARVER_REPLACEABLES = TagKey.of(Registry.BLOCK_KEY, new Identifier(Constant.MOD_ID, "moon_crater_carver_replaceables"));
    public static final TagKey<Block> MOON_STONE_ORE_REPLACABLES = TagKey.of(Registry.BLOCK_KEY, new Identifier(Constant.MOD_ID, "moon_stone_ore_replaceables"));
    public static final TagKey<Block> LUNASLATE_ORE_REPLACABLES = TagKey.of(Registry.BLOCK_KEY, new Identifier(Constant.MOD_ID, "lunaslate_ore_replaceables"));

    public static final TagKey<Biome> MOON = TagKey.of(Registry.BIOME_KEY, new Identifier(Constant.MOD_ID, "moon"));
    public static final TagKey<Biome> MOON_HIGHLANDS = TagKey.of(Registry.BIOME_KEY, new Identifier(Constant.MOD_ID, "moon_highlands"));
    public static final TagKey<Biome> MOON_MARE = TagKey.of(Registry.BIOME_KEY, new Identifier(Constant.MOD_ID, "moon_mare"));

    public static final TagKey<Biome> MOON_PILLAGER_BASE_HAS_STRUCTURE = TagKey.of(Registry.BIOME_KEY, new Identifier(Constant.MOD_ID, "has_structure/moon_pillager_base"));
    public static final TagKey<Biome> MOON_VILLAGE_HIGHLANDS_HAS_STRUCTURE = TagKey.of(Registry.BIOME_KEY, new Identifier(Constant.MOD_ID, "has_structure/moon_village_highlands"));

    public static final TagKey<Fluid> OXYGEN = TagKey.of(Registry.FLUID_KEY, new Identifier(Constant.MOD_ID, "oxygen"));

    public static final TagKey<Item> SILICON = TagKey.of(Registry.ITEM_KEY, new Identifier(Constant.COMMON_NAMESPACE, "silicon"));

    public static final TagKey<Structure> MOON_RUINS = TagKey.of(Registry.STRUCTURE_KEY, new Identifier(Constant.MOD_ID, "moon_ruins"));

    public static void register() {
    }
}
