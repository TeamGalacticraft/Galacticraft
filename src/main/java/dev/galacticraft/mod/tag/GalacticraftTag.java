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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import dev.galacticraft.api.gas.Gas;
import dev.galacticraft.api.registry.AddonRegistry;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.mixin.BlockTagsAccessor;
import dev.galacticraft.mod.world.biome.GalacticraftBiome;
import dev.galacticraft.mod.world.biome.GalacticraftBiomeKey;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.fabricmc.fabric.mixin.tag.extension.MixinRequiredTagListRegistry;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.SetTag;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftTag {
    public static final TagFactory<Gas> GAS_TAG_FACTORY = TagFactory.of(Gas.REGISTRY_KEY, "tags/gas");

    public static final Tag.Identified<Fluid> OIL = TagFactory.FLUID.create(new Identifier(Constant.COMMON_NAMESPACE, "oil"));
    public static final Tag.Identified<Fluid> FUEL = TagFactory.FLUID.create(new Identifier(Constant.COMMON_NAMESPACE, "fuel"));
    public static final Tag.Identified<Fluid> LIQUID_OXYGEN = TagFactory.FLUID.create(new Identifier(Constant.COMMON_NAMESPACE, "oxygen"));

    public static final Tag.Identified<Block> INFINIBURN_MOON = TagFactory.BLOCK.create(new Identifier(Constant.MOD_ID, "infiniburn_moon"));
    public static final Tag.Identified<Block> BASE_STONE_MOON = TagFactory.BLOCK.create(new Identifier(Constant.MOD_ID, "base_stone_moon"));
    public static final Tag.Identified<Block> MOON_STONE_ORE_REPLACABLES = TagFactory.BLOCK.create(new Identifier(Constant.MOD_ID, "moon_stone_ore_replaceables"));
    public static final Tag.Identified<Block> LUNASLATE_ORE_REPLACABLES = TagFactory.BLOCK.create(new Identifier(Constant.MOD_ID, "lunaslate_ore_replaceables"));

    public static final Tag<Biome> MOON_HIGHLANDS = new LazyDefaultedTag<>(TagFactory.BIOME, new Identifier(Constant.MOD_ID, "moon_highlands"), () -> ImmutableList.of(
            BuiltinRegistries.BIOME.getOrThrow(GalacticraftBiomeKey.Moon.HIGHLANDS),
            BuiltinRegistries.BIOME.getOrThrow(GalacticraftBiomeKey.Moon.HIGHLANDS_HILLS),
            BuiltinRegistries.BIOME.getOrThrow(GalacticraftBiomeKey.Moon.HIGHLANDS_VALLEY),
            BuiltinRegistries.BIOME.getOrThrow(GalacticraftBiomeKey.Moon.HIGHLANDS_FLAT),
            BuiltinRegistries.BIOME.getOrThrow(GalacticraftBiomeKey.Moon.HIGHLANDS_EDGE)
    ));

    public static final Tag<Biome> MOON_MARE = new LazyDefaultedTag<>(TagFactory.BIOME, new Identifier(Constant.MOD_ID, "moon_mare"), () -> ImmutableList.of( //fixme
            BuiltinRegistries.BIOME.getOrThrow(GalacticraftBiomeKey.Moon.MARE),
            BuiltinRegistries.BIOME.getOrThrow(GalacticraftBiomeKey.Moon.MARE_HILLS),
            BuiltinRegistries.BIOME.getOrThrow(GalacticraftBiomeKey.Moon.MARE_VALLEY),
            BuiltinRegistries.BIOME.getOrThrow(GalacticraftBiomeKey.Moon.MARE_FLAT),
            BuiltinRegistries.BIOME.getOrThrow(GalacticraftBiomeKey.Moon.MARE_EDGE)
    ));

    public static final Tag.Identified<Gas> OXYGEN = GAS_TAG_FACTORY.create(new Identifier(Constant.MOD_ID, "oxygen"));

    public static final Tag.Identified<Item> DIAMONDS = TagFactory.ITEM.create(new Identifier(Constant.COMMON_NAMESPACE, "diamonds"));
    public static final Tag.Identified<Item> SILICONS = TagFactory.ITEM.create(new Identifier(Constant.COMMON_NAMESPACE, "silicons"));
    public static final Tag.Identified<Item> REDSTONES = TagFactory.ITEM.create(new Identifier(Constant.COMMON_NAMESPACE, "redstones"));

    public static void register() {
        BlockTagsAccessor.getRequiredTags().add(MOON_STONE_ORE_REPLACABLES.getId().toString());
        BlockTagsAccessor.getRequiredTags().add(LUNASLATE_ORE_REPLACABLES.getId().toString());
        BlockTagsAccessor.getRequiredTags().add(BASE_STONE_MOON.getId().toString());
        BlockTagsAccessor.getRequiredTags().add(INFINIBURN_MOON.getId().toString());
    }
}
