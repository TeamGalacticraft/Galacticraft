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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.material.Fluid;

public class GCTags {
    public static final TagKey<Fluid> OIL = TagKey.create(Registries.FLUID, Constant.id("oil"));
    public static final TagKey<Fluid> OIL_COMMON = TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(Constant.COMMON_NAMESPACE, "oil"));
    public static final TagKey<Fluid> FUEL = TagKey.create(Registries.FLUID, Constant.id("fuel"));
    public static final TagKey<Fluid> FUEL_COMMON = TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(Constant.COMMON_NAMESPACE, "fuel"));
    public static final TagKey<Fluid> SULFURIC_ACID = TagKey.create(Registries.FLUID, Constant.id("sulfuric_acid"));
    public static final TagKey<Fluid> SULFURIC_ACID_COMMON = TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(Constant.COMMON_NAMESPACE, "sulfuric_acid"));
    public static final TagKey<Fluid> LIQUID_OXYGEN = TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(Constant.COMMON_NAMESPACE, "oxygen"));
    public static final TagKey<Fluid> OXYGEN = TagKey.create(Registries.FLUID, Constant.id("oxygen"));
    public static final TagKey<Fluid> NON_BREATHABLE = TagKey.create(Registries.FLUID, Constant.id("non_breathable"));

    public static final TagKey<Block> INFINIBURN_MOON = TagKey.create(Registries.BLOCK, Constant.id("infiniburn_moon"));
    public static final TagKey<Block> INFINIBURN_VENUS = TagKey.create(Registries.BLOCK, Constant.id("infiniburn_venus"));
    public static final TagKey<Block> BASE_STONE_MOON = TagKey.create(Registries.BLOCK, Constant.id("base_stone_moon"));
    public static final TagKey<Block> MOON_CARVER_REPLACEABLES = TagKey.create(Registries.BLOCK, Constant.id("moon_carver_replaceables"));
    public static final TagKey<Block> MOON_CRATER_CARVER_REPLACEABLES = TagKey.create(Registries.BLOCK, Constant.id("moon_crater_carver_replaceables"));
    public static final TagKey<Block> MOON_STONE_ORE_REPLACABLES = TagKey.create(Registries.BLOCK, Constant.id("moon_stone_ore_replaceables"));
    public static final TagKey<Block> LUNASLATE_ORE_REPLACABLES = TagKey.create(Registries.BLOCK, Constant.id("lunaslate_ore_replaceables"));
    public static final TagKey<Block> MACHINES = TagKey.create(Registries.BLOCK, Constant.id("machines"));
    public static final TagKey<Block> FOOTPRINTS = TagKey.create(Registries.BLOCK, Constant.id("footprints"));

    public static final TagKey<Biome> MOON = TagKey.create(Registries.BIOME, Constant.id("moon"));
    public static final TagKey<Biome> VENUS = TagKey.create(Registries.BIOME, Constant.id("venus"));

    public static final TagKey<Biome> MOON_PILLAGER_BASE_HAS_STRUCTURE = TagKey.create(Registries.BIOME, Constant.id("has_structure/moon_pillager_base"));
    public static final TagKey<Biome> MOON_VILLAGE_HIGHLANDS_HAS_STRUCTURE = TagKey.create(Registries.BIOME, Constant.id("has_structure/moon_village_highlands"));
    public static final TagKey<Biome> MOON_RUINS_HAS_STRUCTURE = TagKey.create(Registries.BIOME, Constant.id("has_structure/moon_ruins"));
    public static final TagKey<Biome> MOON_BOSS_HAS_STRUCTURE = TagKey.create(Registries.BIOME, Constant.id("has_structure/moon_boss"));

    public static final TagKey<Item> ALUMINUM_INGOTS = commonTag("ingots/aluminum");
    public static final TagKey<Item> RAW_ALUMINUM_ORES = commonTag("raw_materials/aluminum");
    public static final TagKey<Item> LEAD_INGOTS = commonTag("ingots/lead");
    public static final TagKey<Item> RAW_LEAD_ORES = commonTag("raw_materials/lead");
    public static final TagKey<Item> SILICONS = commonTag("gems/silicon");
    public static final TagKey<Item> STEEL_INGOTS = commonTag("ingots/steel");
    public static final TagKey<Item> TIN_INGOTS = commonTag("ingots/tin");
    public static final TagKey<Item> RAW_TIN_ORES = commonTag("raw_materials/tin");
    public static final TagKey<Item> COMPRESSED_STEEL = commonTag("plates/steel");
    public static final TagKey<Item> COMPRESSED_IRON = commonTag("plates/iron");
    public static final TagKey<Item> COMPRESSED_TIN = commonTag("plates/tin");

    public static final TagKey<Structure> MOON_RUINS = TagKey.create(Registries.STRUCTURE, Constant.id("moon_ruins"));

    public static final TagKey<DimensionType> FOOTPRINTS_DIMENSIONS = TagKey.create(Registries.DIMENSION_TYPE, Constant.id("footprints"));
    public static final TagKey<DimensionType> SPACE = TagKey.create(Registries.DIMENSION_TYPE, Constant.id("space"));

    public static final TagKey<EntityType<?>> HAS_FOOTPRINTS = TagKey.create(Registries.ENTITY_TYPE, Constant.id("has_footprints"));

    public static TagKey<Item> commonTag(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Constant.COMMON_NAMESPACE, path));
    }

    public static void register() {
    }
}
