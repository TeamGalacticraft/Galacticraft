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
import net.minecraft.world.level.block.Block;

public class GCBlockTags {
    public static final TagKey<Block> FOOTPRINTS = galacticraftTag("footprints");

    public static final TagKey<Block> INFINIBURN_MOON = galacticraftTag("infiniburn_moon");
    public static final TagKey<Block> INFINIBURN_VENUS = galacticraftTag("infiniburn_venus");
    public static final TagKey<Block> INFINIBURN_ASTEROID = galacticraftTag("infiniburn_asteroid");
    public static final TagKey<Block> INFINIBURN_SATELLITE = galacticraftTag("infiniburn_satellite");

    public static final TagKey<Block> BASE_STONE_MOON = galacticraftTag("base_stone_moon");
    public static final TagKey<Block> MOON_CARVER_REPLACEABLES = galacticraftTag("moon_carver_replaceables");
    public static final TagKey<Block> MOON_CRATER_CARVER_REPLACEABLES = galacticraftTag("moon_crater_carver_replaceables");

    public static final TagKey<Block> MOON_STONE_ORE_REPLACEABLES = galacticraftTag("moon_stone_ore_replaceables");
    public static final TagKey<Block> MOON_BASALT_ORE_REPLACEABLES = galacticraftTag("moon_basalt_ore_replaceables");
    public static final TagKey<Block> LUNASLATE_ORE_REPLACEABLES = galacticraftTag("lunaslate_ore_replaceables");
    public static final TagKey<Block> MARS_STONE_ORE_REPLACEABLES = galacticraftTag("mars_stone_ore_replaceables");
    public static final TagKey<Block> ASTEROID_ROCK_ORE_REPLACEABLES = galacticraftTag("asteroid_rock_ore_replaceables");
    public static final TagKey<Block> SOFT_VENUS_ROCK_ORE_REPLACEABLES = galacticraftTag("soft_venus_rock_ore_replaceables");
    public static final TagKey<Block> HARD_VENUS_ROCK_ORE_REPLACEABLES = galacticraftTag("hard_venus_rock_ore_replaceables");

    public static final TagKey<Block> ORE_BEARING_GROUND_MOON_STONE = commonTag("ore_bearing_ground/moon_stone");
    public static final TagKey<Block> ORE_BEARING_GROUND_MOON_BASALT = commonTag("ore_bearing_ground/moon_basalt");
    public static final TagKey<Block> ORE_BEARING_GROUND_LUNASLATE = commonTag("ore_bearing_ground/lunaslate");
    public static final TagKey<Block> ORE_BEARING_GROUND_MARS_STONE = commonTag("ore_bearing_ground/mars_stone");
    public static final TagKey<Block> ORE_BEARING_GROUND_ASTEROID_ROCK = commonTag("ore_bearing_ground/asteroid_rock");
    public static final TagKey<Block> ORE_BEARING_GROUND_VENUS_ROCK = commonTag("ore_bearing_ground/venus_rock");
    public static final TagKey<Block> ORE_BEARING_GROUND_SOFT_VENUS_ROCK = commonTag("ore_bearing_ground/venus_rock/soft");
    public static final TagKey<Block> ORE_BEARING_GROUND_HARD_VENUS_ROCK = commonTag("ore_bearing_ground/venus_rock/hard");

    public static final TagKey<Block> ORES_IN_GROUND_MOON_STONE = commonTag("ores_in_ground/moon_stone");
    public static final TagKey<Block> ORES_IN_GROUND_MOON_BASALT = commonTag("ores_in_ground/moon_basalt");
    public static final TagKey<Block> ORES_IN_GROUND_LUNASLATE = commonTag("ores_in_ground/lunaslate");
    public static final TagKey<Block> ORES_IN_GROUND_MARS_STONE = commonTag("ores_in_ground/mars_stone");
    public static final TagKey<Block> ORES_IN_GROUND_ASTEROID_ROCK = commonTag("ores_in_ground/asteroid_rock");
    public static final TagKey<Block> ORES_IN_GROUND_VENUS_ROCK = commonTag("ores_in_ground/venus_rock");
    public static final TagKey<Block> ORES_IN_GROUND_SOFT_VENUS_ROCK = commonTag("ores_in_ground/venus_rock/soft");
    public static final TagKey<Block> ORES_IN_GROUND_HARD_VENUS_ROCK = commonTag("ores_in_ground/venus_rock/hard");

    public static final TagKey<Block> ALUMINUM_ORES = commonTag("ores/aluminum");
    public static final TagKey<Block> CHEESE_ORES = commonTag("ores/cheese");
    public static final TagKey<Block> DESH_ORES = commonTag("ores/desh");
    public static final TagKey<Block> LEAD_ORES = commonTag("ores/lead");
    public static final TagKey<Block> LUNAR_SAPPHIRE_ORES = commonTag("ores/lunar_sapphire");
    public static final TagKey<Block> METEORIC_IRON_ORES = commonTag("ores/meteoric_iron");
    public static final TagKey<Block> OLIVINE_ORES = commonTag("ores/olivine");
    public static final TagKey<Block> SILICON_ORES = commonTag("ores/silicon");
    public static final TagKey<Block> SOLAR_ORES = commonTag("ores/solar_dust");
    public static final TagKey<Block> TIN_ORES = commonTag("ores/tin");
    public static final TagKey<Block> TITANIUM_ORES = commonTag("ores/titanium");

    public static final TagKey<Block> ALUMINUM_BLOCKS = commonTag("storage_blocks/aluminum");
    public static final TagKey<Block> CHEESE_BLOCKS = commonTag("storage_blocks/cheese");
    public static final TagKey<Block> DESH_BLOCKS = commonTag("storage_blocks/desh");
    public static final TagKey<Block> LEAD_BLOCKS = commonTag("storage_blocks/lead");
    public static final TagKey<Block> LUNAR_SAPPHIRE_BLOCKS = commonTag("storage_blocks/lunar_sapphire");
    public static final TagKey<Block> METEORIC_IRON_BLOCKS = commonTag("storage_blocks/meteoric_iron");
    public static final TagKey<Block> OLIVINE_BLOCKS = commonTag("storage_blocks/olivine");
    public static final TagKey<Block> SILICON_BLOCKS = commonTag("storage_blocks/silicon");
    public static final TagKey<Block> TIN_BLOCKS = commonTag("storage_blocks/tin");
    public static final TagKey<Block> TITANIUM_BLOCKS = commonTag("storage_blocks/titanium");

    public static final TagKey<Block> RAW_ALUMINUM_BLOCKS = commonTag("storage_blocks/raw_aluminum");
    public static final TagKey<Block> RAW_DESH_BLOCKS = commonTag("storage_blocks/raw_desh");
    public static final TagKey<Block> RAW_LEAD_BLOCKS = commonTag("storage_blocks/raw_lead");
    public static final TagKey<Block> RAW_METEORIC_IRON_BLOCKS = commonTag("storage_blocks/raw_meteoric_iron");
    public static final TagKey<Block> RAW_TIN_BLOCKS = commonTag("storage_blocks/raw_tin");
    public static final TagKey<Block> RAW_TITANIUM_BLOCKS = commonTag("storage_blocks/raw_titanium");

    public static final TagKey<Block> MACHINES = galacticraftTag("machines");
    public static final TagKey<Block> GLASS_FLUID_PIPES = galacticraftTag("glass_fluid_pipes");
    public static final TagKey<Block> STAINED_GLASS_FLUID_PIPES = galacticraftTag("glass_fluid_pipes/stained");

    public static final TagKey<Block> SLABS = galacticraftTag("slabs");
    public static final TagKey<Block> STAIRS = galacticraftTag("stairs");
    public static final TagKey<Block> WALLS = galacticraftTag("walls");

    public static final TagKey<Block> MOON_COBBLESTONES = commonTag("cobblestones/moon");
    public static final TagKey<Block> LUNASLATE_COBBLESTONES = commonTag("cobblestones/lunaslate");
    public static final TagKey<Block> MARS_COBBLESTONES = commonTag("cobblestones/mars");

    public static final TagKey<Block> DECORATION_BLOCKS = galacticraftTag("decoration_blocks");
    public static final TagKey<Block> ALUMINUM_DECORATION_BLOCKS = galacticraftTag("decoration_blocks/aluminum");
    public static final TagKey<Block> BRONZE_DECORATION_BLOCKS = galacticraftTag("decoration_blocks/bronze");
    public static final TagKey<Block> COPPER_DECORATION_BLOCKS = galacticraftTag("decoration_blocks/copper");
    public static final TagKey<Block> DARK_DECORATION_BLOCKS = galacticraftTag("decoration_blocks/dark");
    public static final TagKey<Block> IRON_DECORATION_BLOCKS = galacticraftTag("decoration_blocks/iron");
    public static final TagKey<Block> METEORIC_IRON_DECORATION_BLOCKS = galacticraftTag("decoration_blocks/meteoric_iron");
    public static final TagKey<Block> STEEL_DECORATION_BLOCKS = galacticraftTag("decoration_blocks/steel");
    public static final TagKey<Block> TIN_DECORATION_BLOCKS = galacticraftTag("decoration_blocks/tin");
    public static final TagKey<Block> TITANIUM_DECORATION_BLOCKS = galacticraftTag("decoration_blocks/titanium");

    public static TagKey<Block> commonTag(String path) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constant.COMMON_NAMESPACE, path));
    }

    public static TagKey<Block> galacticraftTag(String path) {
        return TagKey.create(Registries.BLOCK, Constant.id(path));
    }

    public static void register() {
    }
}
