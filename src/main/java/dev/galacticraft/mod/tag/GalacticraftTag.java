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

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.world.biome.Biome;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftTag {
    public static final Tag.Identified<Fluid> OIL = null;//TagFactory.FLUID.create(new Identifier(Constant.COMMON_NAMESPACE, "oil"));
    public static final Tag.Identified<Fluid> FUEL = null;//TagFactory.FLUID.create(new Identifier(Constant.COMMON_NAMESPACE, "fuel"));
    public static final Tag.Identified<Fluid> LIQUID_OXYGEN = null;//TagFactory.FLUID.create(new Identifier(Constant.COMMON_NAMESPACE, "oxygen"));

    public static final Tag.Identified<Block> INFINIBURN_MOON = null;//TagFactory.BLOCK.create(new Identifier(Constant.MOD_ID, "infiniburn_moon"));
    public static final Tag.Identified<Block> BASE_STONE_MOON = null;//TagFactory.BLOCK.create(new Identifier(Constant.MOD_ID, "base_stone_moon"));
    public static final Tag.Identified<Block> MOON_STONE_ORE_REPLACABLES = null;//TagFactory.BLOCK.create(new Identifier(Constant.MOD_ID, "moon_stone_ore_replaceables"));
    public static final Tag.Identified<Block> LUNASLATE_ORE_REPLACABLES = null;//TagFactory.BLOCK.create(new Identifier(Constant.MOD_ID, "lunaslate_ore_replaceables"));

    public static final Tag<Biome> MOON_HIGHLANDS = null;//new LazyDefaultedTag<>(TagFactory.BIOME, new Identifier(Constant.MOD_ID, "moon_highlands"), () -> ImmutableList.of(
//            BuiltinRegistries.BIOME.getOrThrow(GalacticraftBiomeKey.Moon.HIGHLANDS),
//            BuiltinRegistries.BIOME.getOrThrow(GalacticraftBiomeKey.Moon.HIGHLANDS_HILLS),
//            BuiltinRegistries.BIOME.getOrThrow(GalacticraftBiomeKey.Moon.HIGHLANDS_VALLEY),
//            BuiltinRegistries.BIOME.getOrThrow(GalacticraftBiomeKey.Moon.HIGHLANDS_FLAT),
//            BuiltinRegistries.BIOME.getOrThrow(GalacticraftBiomeKey.Moon.HIGHLANDS_EDGE)
//    ));

    public static final Tag<Biome> MOON_MARE = null;//new LazyDefaultedTag<>(TagFactory.BIOME, new Identifier(Constant.MOD_ID, "moon_mare"), () -> ImmutableList.of( //fixme
//            BuiltinRegistries.BIOME.getOrThrow(GalacticraftBiomeKey.Moon.MARE),
//            BuiltinRegistries.BIOME.getOrThrow(GalacticraftBiomeKey.Moon.MARE_HILLS),
//            BuiltinRegistries.BIOME.getOrThrow(GalacticraftBiomeKey.Moon.MARE_VALLEY),
//            BuiltinRegistries.BIOME.getOrThrow(GalacticraftBiomeKey.Moon.MARE_FLAT),
//            BuiltinRegistries.BIOME.getOrThrow(GalacticraftBiomeKey.Moon.MARE_EDGE)
//    ));

    public static final Tag<Fluid> OXYGEN = null;//FluidTags.getTagGroup().getTag(new Identifier(Constant.MOD_ID, "oxygen"));

    public static final Tag.Identified<Item> DIAMONDS = null;//TagFactory.ITEM.create(new Identifier(Constant.COMMON_NAMESPACE, "diamonds"));
    public static final Tag.Identified<Item> SILICONS = null;//TagFactory.ITEM.create(new Identifier(Constant.COMMON_NAMESPACE, "silicons"));
    public static final Tag.Identified<Item> REDSTONES = null;//TagFactory.ITEM.create(new Identifier(Constant.COMMON_NAMESPACE, "redstones"));

    public static void register() {
//        BlockTagsAccessor.getRequiredTags().add(MOON_STONE_ORE_REPLACABLES.getId().toString());
//        BlockTagsAccessor.getRequiredTags().add(LUNASLATE_ORE_REPLACABLES.getId().toString());
//        BlockTagsAccessor.getRequiredTags().add(BASE_STONE_MOON.getId().toString());
//        BlockTagsAccessor.getRequiredTags().add(INFINIBURN_MOON.getId().toString());
    }
}
