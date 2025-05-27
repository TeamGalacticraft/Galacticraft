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
import net.minecraft.world.item.Item;

public class GCItemTags {
    public static final TagKey<Item> WRENCHES = commonTag("tools/wrench");

    public static final TagKey<Item> THERMAL_HEAD = galacticraftTag("thermal/head");
    public static final TagKey<Item> THERMAL_CHEST = galacticraftTag("thermal/chest");
    public static final TagKey<Item> THERMAL_PANTS = galacticraftTag("thermal/pants");
    public static final TagKey<Item> THERMAL_BOOTS = galacticraftTag("thermal/boots");
    public static final TagKey<Item> OXYGEN_MASKS = galacticraftTag("oxygen/mask");
    public static final TagKey<Item> OXYGEN_GEAR = galacticraftTag("oxygen/gear");
    public static final TagKey<Item> OXYGEN_TANKS = galacticraftTag("oxygen/tank");
    public static final TagKey<Item> ACCESSORIES = galacticraftTag("accessories");
    public static final TagKey<Item> PARACHUTES = galacticraftTag("parachutes");
    public static final TagKey<Item> FREQUENCY_MODULES = galacticraftTag("frequency_modules");
    public static final TagKey<Item> SHIELD_CONTROLLERS = galacticraftTag("shield_controllers");

    public static final TagKey<Item> GLASS_FLUID_PIPES = galacticraftTag("glass_fluid_pipes");
    public static final TagKey<Item> STAINED_GLASS_FLUID_PIPES = galacticraftTag("glass_fluid_pipes/stained");

    public static final TagKey<Item> BATTERIES = commonTag("batteries");

    public static final TagKey<Item> SILICONS = commonTag("silicon");
    public static final TagKey<Item> OLIVINE_SHARDS = commonTag("gems/olivine");
    public static final TagKey<Item> LUNAR_SAPPHIRES = commonTag("gems/lunar_sapphire");

    public static final TagKey<Item> ALUMINUM_ORES = commonTag("ores/aluminum");
    public static final TagKey<Item> CHEESE_ORES = commonTag("ores/cheese");
    public static final TagKey<Item> DESH_ORES = commonTag("ores/desh");
    public static final TagKey<Item> LEAD_ORES = commonTag("ores/lead");
    public static final TagKey<Item> LUNAR_SAPPHIRE_ORES = commonTag("ores/lunar_sapphire");
    public static final TagKey<Item> METEORIC_IRON_ORES = commonTag("ores/meteoric_iron");
    public static final TagKey<Item> OLIVINE_ORES = commonTag("ores/olivine");
    public static final TagKey<Item> SILICON_ORES = commonTag("ores/silicon");
    public static final TagKey<Item> SOLAR_ORES = commonTag("ores/solar_dust");
    public static final TagKey<Item> TIN_ORES = commonTag("ores/tin");
    public static final TagKey<Item> TITANIUM_ORES = commonTag("ores/titanium");

    public static final TagKey<Item> ALUMINUM_BLOCKS = commonTag("storage_blocks/aluminum");
    public static final TagKey<Item> CHEESE_BLOCKS = commonTag("storage_blocks/cheese");
    public static final TagKey<Item> DESH_BLOCKS = commonTag("storage_blocks/desh");
    public static final TagKey<Item> LEAD_BLOCKS = commonTag("storage_blocks/lead");
    public static final TagKey<Item> LUNAR_SAPPHIRE_BLOCKS = commonTag("storage_blocks/lunar_sapphire");
    public static final TagKey<Item> METEORIC_IRON_BLOCKS = commonTag("storage_blocks/meteoric_iron");
    public static final TagKey<Item> OLIVINE_BLOCKS = commonTag("storage_blocks/olivine");
    public static final TagKey<Item> SILICON_BLOCKS = commonTag("storage_blocks/silicon");
    public static final TagKey<Item> TIN_BLOCKS = commonTag("storage_blocks/tin");
    public static final TagKey<Item> TITANIUM_BLOCKS = commonTag("storage_blocks/titanium");

    public static final TagKey<Item> RAW_ALUMINUM_BLOCKS = commonTag("storage_blocks/raw_aluminum");
    public static final TagKey<Item> RAW_DESH_BLOCKS = commonTag("storage_blocks/raw_desh");
    public static final TagKey<Item> RAW_LEAD_BLOCKS = commonTag("storage_blocks/raw_lead");
    public static final TagKey<Item> RAW_METEORIC_IRON_BLOCKS = commonTag("storage_blocks/raw_meteoric_iron");
    public static final TagKey<Item> RAW_TIN_BLOCKS = commonTag("storage_blocks/raw_tin");
    public static final TagKey<Item> RAW_TITANIUM_BLOCKS = commonTag("storage_blocks/raw_titanium");

    public static final TagKey<Item> ALUMINUM_RAW_MATERIALS = commonTag("raw_materials/aluminum");
    public static final TagKey<Item> DESH_RAW_MATERIALS = commonTag("raw_materials/desh");
    public static final TagKey<Item> LEAD_RAW_MATERIALS = commonTag("raw_materials/lead");
    public static final TagKey<Item> METEORIC_IRON_RAW_MATERIALS = commonTag("raw_materials/meteoric_iron");
    public static final TagKey<Item> TIN_RAW_MATERIALS = commonTag("raw_materials/tin");
    public static final TagKey<Item> TITANIUM_RAW_MATERIALS = commonTag("raw_materials/titanium");

    public static final TagKey<Item> ALUMINUM_INGOTS = commonTag("ingots/aluminum");
    public static final TagKey<Item> DESH_INGOTS = commonTag("ingots/desh");
    public static final TagKey<Item> LEAD_INGOTS = commonTag("ingots/lead");
    public static final TagKey<Item> METEORIC_IRON_INGOTS = commonTag("ingots/meteoric_iron");
    public static final TagKey<Item> STEEL_INGOTS = commonTag("ingots/steel");
    public static final TagKey<Item> TIN_INGOTS = commonTag("ingots/tin");
    public static final TagKey<Item> TITANIUM_INGOTS = commonTag("ingots/titanium");

    public static final TagKey<Item> ALUMINUM_NUGGETS = commonTag("nuggets/aluminum");
    public static final TagKey<Item> DESH_NUGGETS = commonTag("nuggets/desh");
    public static final TagKey<Item> LEAD_NUGGETS = commonTag("nuggets/lead");
    public static final TagKey<Item> METEORIC_IRON_NUGGETS = commonTag("nuggets/meteoric_iron");
    public static final TagKey<Item> TIN_NUGGETS = commonTag("nuggets/tin");
    public static final TagKey<Item> TITANIUM_NUGGETS = commonTag("nuggets/titanium");

    public static final TagKey<Item> PLATES = commonTag("plates");
    public static final TagKey<Item> ALUMINUM_PLATES = commonTag("plates/aluminum");
    public static final TagKey<Item> BRONZE_PLATES = commonTag("plates/bronze");
    public static final TagKey<Item> COPPER_PLATES = commonTag("plates/copper");
    public static final TagKey<Item> DESH_PLATES = commonTag("plates/desh");
    public static final TagKey<Item> IRON_PLATES = commonTag("plates/iron");
    public static final TagKey<Item> METEORIC_IRON_PLATES = commonTag("plates/meteoric_iron");
    public static final TagKey<Item> STEEL_PLATES = commonTag("plates/steel");
    public static final TagKey<Item> TIN_PLATES = commonTag("plates/tin");
    public static final TagKey<Item> TITANIUM_PLATES = commonTag("plates/titanium");
    public static final TagKey<Item> HEAVY_DUTY_PLATES = commonTag("plates/heavy_duty");
    public static final TagKey<Item> TIER_1_HEAVY_DUTY_PLATES = commonTag("plates/heavy_duty/tier_1");
    public static final TagKey<Item> TIER_2_HEAVY_DUTY_PLATES = commonTag("plates/heavy_duty/tier_2");
    public static final TagKey<Item> TIER_3_HEAVY_DUTY_PLATES = commonTag("plates/heavy_duty/tier_3");

    public static final TagKey<Item> STEEL_RODS = commonTag("rods/steel");
    public static final TagKey<Item> DESH_RODS = commonTag("rods/desh");

    public static final TagKey<Item> CANISTERS = commonTag("canisters");
    public static final TagKey<Item> COPPER_CANISTERS = commonTag("canisters/copper");
    public static final TagKey<Item> TIN_CANISTERS = commonTag("canisters/tin");
    
    public static final TagKey<Item> SOLAR_DUSTS = commonTag("dusts/solar");

    public static final TagKey<Item> CHEESE_FOODS = commonTag("foods/cheese");
    public static final TagKey<Item> CANNED_FOODS = commonTag("foods/canned");
    public static final TagKey<Item> UNCANNABLE_FOODS = galacticraftTag("uncannable_foods");

    public static final TagKey<Item> ROCKET_STORAGE_UPGRADE_ITEMS = galacticraftTag("rocket_storage_upgrade_items");
    public static final TagKey<Item> EVOLVED_CREEPER_DROP_MUSIC_DISCS = galacticraftTag("evolved_creeper_drop_music_discs");

    public static final TagKey<Item> SLABS = galacticraftTag("slabs");
    public static final TagKey<Item> STAIRS = galacticraftTag("stairs");
    public static final TagKey<Item> WALLS = galacticraftTag("walls");

    public static final TagKey<Item> MOON_COBBLESTONES = commonTag("cobblestones/moon");
    public static final TagKey<Item> LUNASLATE_COBBLESTONES = commonTag("cobblestones/lunaslate");
    public static final TagKey<Item> MARS_COBBLESTONES = commonTag("cobblestones/mars");

    public static final TagKey<Item> OIL_BUCKETS = commonTag("buckets/oil");
    public static final TagKey<Item> FUEL_BUCKETS = commonTag("buckets/fuel");
    public static final TagKey<Item> SULFURIC_ACID_BUCKETS = commonTag("buckets/sulfuric_acid");

    public static TagKey<Item> commonTag(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Constant.COMMON_NAMESPACE, path));
    }

    public static TagKey<Item> galacticraftTag(String path) {
        return TagKey.create(Registries.ITEM, Constant.id(path));
    }

    public static void register() {
    }
}
