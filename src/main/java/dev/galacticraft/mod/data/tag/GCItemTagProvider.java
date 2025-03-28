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

package dev.galacticraft.mod.data.tag;

import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.tag.GCItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class GCItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public GCItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture, new GCBlockTagProvider(output, completableFuture));
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ItemTags.AXES)
                .add(GCItems.HEAVY_DUTY_AXE)
                .add(GCItems.DESH_AXE)
                .add(GCItems.TITANIUM_AXE);
        this.tag(ItemTags.HOES)
                .add(GCItems.HEAVY_DUTY_HOE)
                .add(GCItems.DESH_HOE)
                .add(GCItems.TITANIUM_HOE);
        this.tag(ItemTags.PICKAXES)
                .add(GCItems.HEAVY_DUTY_PICKAXE)
                .add(GCItems.DESH_PICKAXE)
                .add(GCItems.TITANIUM_PICKAXE);
        this.tag(ItemTags.SHOVELS)
                .add(GCItems.HEAVY_DUTY_SHOVEL)
                .add(GCItems.DESH_SHOVEL)
                .add(GCItems.TITANIUM_SHOVEL);
        this.tag(ItemTags.SWORDS)
                .add(GCItems.HEAVY_DUTY_SWORD)
                .add(GCItems.DESH_SWORD)
                .add(GCItems.TITANIUM_SWORD);
        this.tag(ItemTags.CLUSTER_MAX_HARVESTABLES)
                .add(GCItems.HEAVY_DUTY_PICKAXE)
                .add(GCItems.DESH_PICKAXE)
                .add(GCItems.TITANIUM_PICKAXE);
        this.tag(ItemTags.HEAD_ARMOR)
                .add(GCItems.HEAVY_DUTY_HELMET)
                .add(GCItems.DESH_HELMET)
                .add(GCItems.TITANIUM_HELMET);
        this.tag(ItemTags.CHEST_ARMOR)
                .add(GCItems.HEAVY_DUTY_CHESTPLATE)
                .add(GCItems.DESH_CHESTPLATE)
                .add(GCItems.TITANIUM_CHESTPLATE);
        this.tag(ItemTags.LEG_ARMOR)
                .add(GCItems.HEAVY_DUTY_LEGGINGS)
                .add(GCItems.DESH_LEGGINGS)
                .add(GCItems.TITANIUM_LEGGINGS);
        this.tag(ItemTags.FOOT_ARMOR)
                .add(GCItems.HEAVY_DUTY_BOOTS)
                .add(GCItems.DESH_BOOTS)
                .add(GCItems.TITANIUM_BOOTS);
        this.tag(ItemTags.FREEZE_IMMUNE_WEARABLES)
                .add(GCItems.THERMAL_PADDING_BOOTS)
                .add(GCItems.THERMAL_PADDING_LEGGINGS)
                .add(GCItems.THERMAL_PADDING_CHESTPIECE)
                .add(GCItems.THERMAL_PADDING_HELMET)
                .add(GCItems.ISOTHERMAL_PADDING_BOOTS)
                .add(GCItems.ISOTHERMAL_PADDING_LEGGINGS)
                .add(GCItems.ISOTHERMAL_PADDING_CHESTPIECE)
                .add(GCItems.ISOTHERMAL_PADDING_HELMET);

        this.tag(GCItemTags.WRENCHES)
                .add(GCItems.STANDARD_WRENCH);

        this.tag(GCItemTags.OXYGEN_MASKS)
                .add(GCItems.OXYGEN_MASK);
        this.tag(GCItemTags.OXYGEN_GEAR)
                .add(GCItems.OXYGEN_GEAR);
        this.tag(GCItemTags.OXYGEN_TANKS)
                .add(GCItems.SMALL_OXYGEN_TANK)
                .add(GCItems.MEDIUM_OXYGEN_TANK)
                .add(GCItems.LARGE_OXYGEN_TANK)
                .add(GCItems.INFINITE_OXYGEN_TANK);

        this.tag(GCItemTags.PARACHUTES)
                .add(GCItems.PARACHUTE.get(DyeColor.WHITE))
                .add(GCItems.PARACHUTE.get(DyeColor.ORANGE))
                .add(GCItems.PARACHUTE.get(DyeColor.MAGENTA))
                .add(GCItems.PARACHUTE.get(DyeColor.LIGHT_BLUE))
                .add(GCItems.PARACHUTE.get(DyeColor.YELLOW))
                .add(GCItems.PARACHUTE.get(DyeColor.LIME))
                .add(GCItems.PARACHUTE.get(DyeColor.PINK))
                .add(GCItems.PARACHUTE.get(DyeColor.GRAY))
                .add(GCItems.PARACHUTE.get(DyeColor.LIGHT_GRAY))
                .add(GCItems.PARACHUTE.get(DyeColor.CYAN))
                .add(GCItems.PARACHUTE.get(DyeColor.PURPLE))
                .add(GCItems.PARACHUTE.get(DyeColor.BLUE))
                .add(GCItems.PARACHUTE.get(DyeColor.BROWN))
                .add(GCItems.PARACHUTE.get(DyeColor.GREEN))
                .add(GCItems.PARACHUTE.get(DyeColor.RED))
                .add(GCItems.PARACHUTE.get(DyeColor.BLACK));

        this.tag(GCItemTags.FREQUENCY_MODULES)
                .add(GCItems.FREQUENCY_MODULE);
        this.tag(GCItemTags.SHIELD_CONTROLLERS)
                .add(GCItems.SHIELD_CONTROLLER);
        this.tag(GCItemTags.ACCESSORIES)
                .addTag(GCItemTags.OXYGEN_MASKS)
                .addTag(GCItemTags.OXYGEN_GEAR)
                .addTag(GCItemTags.OXYGEN_TANKS)
                .addTag(GCItemTags.PARACHUTES)
                .addTag(GCItemTags.FREQUENCY_MODULES)
                .addTag(GCItemTags.SHIELD_CONTROLLERS);

        this.tag(GCItemTags.BATTERIES)
                .add(GCItems.BATTERY)
                .add(GCItems.INFINITE_BATTERY);

        tag(GCItemTags.EVOLVED_CREEPER_DROP_MUSIC_DISCS)
                .add(GCItems.LEGACY_MUSIC_DISC_MARS)
                .add(GCItems.LEGACY_MUSIC_DISC_MIMAS)
                .add(GCItems.LEGACY_MUSIC_DISC_ORBIT)
                .add(GCItems.LEGACY_MUSIC_DISC_SPACERACE);

        // Ore Tags
        this.tag(ItemTags.IRON_ORES)
                .add(GCBlocks.MARS_IRON_ORE.asItem(), GCBlocks.ASTEROID_IRON_ORE.asItem());
        this.tag(ItemTags.COPPER_ORES)
                .add(GCBlocks.MOON_COPPER_ORE.asItem(), GCBlocks.LUNASLATE_COPPER_ORE.asItem(), GCBlocks.MARS_COPPER_ORE.asItem(), GCBlocks.VENUS_COPPER_ORE.asItem());
        this.tag(GCItemTags.SILICON_ORES)
                .add(GCBlocks.SILICON_ORE.asItem(), GCBlocks.DEEPSLATE_SILICON_ORE.asItem(), GCBlocks.ASTEROID_SILICON_ORE.asItem());
        this.tag(GCItemTags.TIN_ORES)
                .add(GCBlocks.TIN_ORE.asItem(), GCBlocks.DEEPSLATE_TIN_ORE.asItem(), GCBlocks.MOON_TIN_ORE.asItem(), GCBlocks.LUNASLATE_TIN_ORE.asItem(), GCBlocks.MARS_TIN_ORE.asItem(), GCBlocks.VENUS_TIN_ORE.asItem());
        this.tag(GCItemTags.ALUMINUM_ORES)
                .add(GCBlocks.ALUMINUM_ORE.asItem(), GCBlocks.DEEPSLATE_ALUMINUM_ORE.asItem(), GCBlocks.ASTEROID_ALUMINUM_ORE.asItem(), GCBlocks.VENUS_ALUMINUM_ORE.asItem());
        this.tag(GCItemTags.CHEESE_ORES)
                .add(GCBlocks.MOON_CHEESE_ORE.asItem());
        this.tag(GCItemTags.LUNAR_SAPPHIRE_ORES)
                .add(GCBlocks.LUNAR_SAPPHIRE_ORE.asItem());
        this.tag(GCItemTags.OLIVINE_ORES)
                .add(GCBlocks.OLIVINE_BASALT.asItem(), GCBlocks.RICH_OLIVINE_BASALT.asItem());
        this.tag(GCItemTags.METEORIC_IRON_ORES)
                .add(GCBlocks.FALLEN_METEOR.asItem());
        this.tag(GCItemTags.DESH_ORES)
                .add(GCBlocks.DESH_ORE.asItem());
        this.tag(GCItemTags.TITANIUM_ORES)
                .add(GCBlocks.ILMENITE_ORE.asItem());
        this.tag(GCItemTags.LEAD_ORES)
                .add(GCBlocks.GALENA_ORE.asItem());
        this.tag(ConventionalItemTags.ORES)
                .addTag(GCItemTags.SILICON_ORES)
                .addTag(GCItemTags.TIN_ORES)
                .addTag(GCItemTags.ALUMINUM_ORES)
                .addTag(GCItemTags.CHEESE_ORES)
                .addTag(GCItemTags.LUNAR_SAPPHIRE_ORES)
                .addTag(GCItemTags.OLIVINE_ORES)
                .addTag(GCItemTags.METEORIC_IRON_ORES)
                .addTag(GCItemTags.DESH_ORES)
                .addTag(GCItemTags.TITANIUM_ORES)
                .addTag(GCItemTags.LEAD_ORES);

        this.tag(GCItemTags.TIN_BLOCKS)
                .add(GCBlocks.TIN_BLOCK.asItem());
        this.tag(GCItemTags.ALUMINUM_BLOCKS)
                .add(GCBlocks.ALUMINUM_BLOCK.asItem());
        this.tag(GCItemTags.METEORIC_IRON_BLOCKS)
                .add(GCBlocks.METEORIC_IRON_BLOCK.asItem());
        this.tag(GCItemTags.DESH_BLOCKS)
                .add(GCBlocks.DESH_BLOCK.asItem());
        this.tag(GCItemTags.TITANIUM_BLOCKS)
                .add(GCBlocks.TITANIUM_BLOCK.asItem());
        this.tag(GCItemTags.LEAD_BLOCKS)
                .add(GCBlocks.LEAD_BLOCK.asItem());
        this.tag(GCItemTags.SILICON_BLOCKS)
                .add(GCBlocks.SILICON_BLOCK.asItem());
        this.tag(GCItemTags.LUNAR_SAPPHIRE_BLOCKS)
                .add(GCBlocks.LUNAR_SAPPHIRE_BLOCK.asItem());
        this.tag(GCItemTags.OLIVINE_BLOCKS)
                .add(GCBlocks.OLIVINE_BLOCK.asItem());
        this.tag(GCItemTags.RAW_TIN_BLOCKS)
                .add(GCBlocks.RAW_TIN_BLOCK.asItem());
        this.tag(GCItemTags.RAW_ALUMINUM_BLOCKS)
                .add(GCBlocks.RAW_ALUMINUM_BLOCK.asItem());
        this.tag(GCItemTags.RAW_METEORIC_IRON_BLOCKS)
                .add(GCBlocks.RAW_METEORIC_IRON_BLOCK.asItem());
        this.tag(GCItemTags.RAW_DESH_BLOCKS)
                .add(GCBlocks.RAW_DESH_BLOCK.asItem());
        this.tag(GCItemTags.RAW_TITANIUM_BLOCKS)
                .add(GCBlocks.RAW_TITANIUM_BLOCK.asItem());
        this.tag(GCItemTags.RAW_LEAD_BLOCKS)
                .add(GCBlocks.RAW_LEAD_BLOCK.asItem());
        this.tag(ConventionalItemTags.STORAGE_BLOCKS)
                .addTag(GCItemTags.TIN_BLOCKS)
                .addTag(GCItemTags.ALUMINUM_BLOCKS)
                .addTag(GCItemTags.METEORIC_IRON_BLOCKS)
                .addTag(GCItemTags.DESH_BLOCKS)
                .addTag(GCItemTags.TITANIUM_BLOCKS)
                .addTag(GCItemTags.LEAD_BLOCKS)
                .addTag(GCItemTags.SILICON_BLOCKS)
                .addTag(GCItemTags.LUNAR_SAPPHIRE_BLOCKS)
                .addTag(GCItemTags.OLIVINE_BLOCKS)
                .addTag(GCItemTags.RAW_TIN_BLOCKS)
                .addTag(GCItemTags.RAW_ALUMINUM_BLOCKS)
                .addTag(GCItemTags.RAW_METEORIC_IRON_BLOCKS)
                .addTag(GCItemTags.RAW_DESH_BLOCKS)
                .addTag(GCItemTags.RAW_TITANIUM_BLOCKS)
                .addTag(GCItemTags.RAW_LEAD_BLOCKS);

        this.tag(ConventionalItemTags.CLUSTERS)
                .add(GCBlocks.OLIVINE_CLUSTER.asItem());

        this.tag(GCItemTags.SILICONS)
                .add(GCItems.SILICON);

        this.tag(GCItemTags.LUNAR_SAPPHIRES)
                .add(GCItems.LUNAR_SAPPHIRE);
        this.tag(GCItemTags.OLIVINE_SHARDS)
                .add(GCItems.OLIVINE_SHARD);
        this.tag(ConventionalItemTags.GEMS)
                .addTag(GCItemTags.LUNAR_SAPPHIRES)
                .addTag(GCItemTags.OLIVINE_SHARDS);

        this.tag(GCItemTags.TIN_INGOTS)
                .add(GCItems.TIN_INGOT);
        this.tag(GCItemTags.ALUMINUM_INGOTS)
                .add(GCItems.ALUMINUM_INGOT);
        this.tag(GCItemTags.METEORIC_IRON_INGOTS)
                .add(GCItems.METEORIC_IRON_INGOT);
        this.tag(GCItemTags.DESH_INGOTS)
                .add(GCItems.DESH_INGOT);
        this.tag(GCItemTags.TITANIUM_INGOTS)
                .add(GCItems.TITANIUM_INGOT);
        this.tag(GCItemTags.LEAD_INGOTS)
                .add(GCItems.LEAD_INGOT);
        this.tag(ConventionalItemTags.INGOTS)
                .addTag(GCItemTags.TIN_INGOTS)
                .addTag(GCItemTags.ALUMINUM_INGOTS)
                .addTag(GCItemTags.METEORIC_IRON_INGOTS)
                .addTag(GCItemTags.DESH_INGOTS)
                .addTag(GCItemTags.TITANIUM_INGOTS)
                .addTag(GCItemTags.LEAD_INGOTS);

        this.tag(GCItemTags.TIN_RAW_MATERIALS)
                .add(GCItems.RAW_TIN);
        this.tag(GCItemTags.ALUMINUM_RAW_MATERIALS)
                .add(GCItems.RAW_ALUMINUM);
        this.tag(GCItemTags.METEORIC_IRON_RAW_MATERIALS)
                .add(GCItems.RAW_METEORIC_IRON);
        this.tag(GCItemTags.DESH_RAW_MATERIALS)
                .add(GCItems.RAW_DESH);
        this.tag(GCItemTags.TITANIUM_RAW_MATERIALS)
                .add(GCItems.RAW_TITANIUM);
        this.tag(GCItemTags.LEAD_RAW_MATERIALS)
                .add(GCItems.RAW_LEAD);
        this.tag(ConventionalItemTags.RAW_MATERIALS)
                .addTag(GCItemTags.TIN_RAW_MATERIALS)
                .addTag(GCItemTags.ALUMINUM_RAW_MATERIALS)
                .addTag(GCItemTags.METEORIC_IRON_RAW_MATERIALS)
                .addTag(GCItemTags.DESH_RAW_MATERIALS)
                .addTag(GCItemTags.TITANIUM_RAW_MATERIALS)
                .addTag(GCItemTags.LEAD_RAW_MATERIALS);

        this.tag(GCItemTags.TIN_NUGGETS)
                .add(GCItems.TIN_NUGGET);
        this.tag(GCItemTags.ALUMINUM_NUGGETS)
                .add(GCItems.ALUMINUM_NUGGET);
        this.tag(GCItemTags.METEORIC_IRON_NUGGETS)
                .add(GCItems.METEORIC_IRON_NUGGET);
        this.tag(GCItemTags.DESH_NUGGETS)
                .add(GCItems.DESH_NUGGET);
        this.tag(GCItemTags.TITANIUM_NUGGETS)
                .add(GCItems.TITANIUM_NUGGET);
        this.tag(GCItemTags.LEAD_NUGGETS)
                .add(GCItems.LEAD_NUGGET);
        this.tag(ConventionalItemTags.NUGGETS)
                .addTag(GCItemTags.TIN_NUGGETS)
                .addTag(GCItemTags.ALUMINUM_NUGGETS)
                .addTag(GCItemTags.METEORIC_IRON_NUGGETS)
                .addTag(GCItemTags.DESH_NUGGETS)
                .addTag(GCItemTags.TITANIUM_NUGGETS)
                .addTag(GCItemTags.LEAD_NUGGETS);

        this.tag(GCItemTags.COPPER_PLATES)
                .add(GCItems.COMPRESSED_COPPER);
        this.tag(GCItemTags.IRON_PLATES)
                .add(GCItems.COMPRESSED_IRON);
        this.tag(GCItemTags.TIN_PLATES)
                .add(GCItems.COMPRESSED_TIN);
        this.tag(GCItemTags.ALUMINUM_PLATES)
                .add(GCItems.COMPRESSED_ALUMINUM);
        this.tag(GCItemTags.BRONZE_PLATES)
                .add(GCItems.COMPRESSED_BRONZE);
        this.tag(GCItemTags.STEEL_PLATES)
                .add(GCItems.COMPRESSED_STEEL);
        this.tag(GCItemTags.METEORIC_IRON_PLATES)
                .add(GCItems.COMPRESSED_METEORIC_IRON);
        this.tag(GCItemTags.DESH_PLATES)
                .add(GCItems.COMPRESSED_DESH);
        this.tag(GCItemTags.TITANIUM_PLATES)
                .add(GCItems.COMPRESSED_TITANIUM);
        this.tag(GCItemTags.TIER_1_HEAVY_DUTY_PLATES)
                .add(GCItems.TIER_1_HEAVY_DUTY_PLATE);
        this.tag(GCItemTags.TIER_2_HEAVY_DUTY_PLATES)
                .add(GCItems.TIER_2_HEAVY_DUTY_PLATE);
        this.tag(GCItemTags.TIER_3_HEAVY_DUTY_PLATES)
                .add(GCItems.TIER_3_HEAVY_DUTY_PLATE);
        this.tag(GCItemTags.HEAVY_DUTY_PLATES)
                .addTag(GCItemTags.TIER_1_HEAVY_DUTY_PLATES)
                .addTag(GCItemTags.TIER_2_HEAVY_DUTY_PLATES)
                .addTag(GCItemTags.TIER_3_HEAVY_DUTY_PLATES);
        this.tag(GCItemTags.PLATES)
                .addTag(GCItemTags.COPPER_PLATES)
                .addTag(GCItemTags.IRON_PLATES)
                .addTag(GCItemTags.TIN_PLATES)
                .addTag(GCItemTags.ALUMINUM_PLATES)
                .addTag(GCItemTags.BRONZE_PLATES)
                .addTag(GCItemTags.STEEL_PLATES)
                .addTag(GCItemTags.METEORIC_IRON_PLATES)
                .addTag(GCItemTags.DESH_PLATES)
                .addTag(GCItemTags.TITANIUM_PLATES)
                .addTag(GCItemTags.HEAVY_DUTY_PLATES);

        this.tag(GCItemTags.STEEL_RODS)
                .add(GCItems.STEEL_POLE);
        this.tag(GCItemTags.DESH_RODS)
                .add(GCItems.DESH_STICK);
        this.tag(ConventionalItemTags.RODS)
                .addTag(GCItemTags.STEEL_RODS)
                .addTag(GCItemTags.DESH_RODS);

        this.tag(GCItemTags.TIN_CANISTERS)
                .add(GCItems.TIN_CANISTER);
        this.tag(GCItemTags.COPPER_CANISTERS)
                .add(GCItems.COPPER_CANISTER);
        this.tag(GCItemTags.CANISTERS)
                .addTag(GCItemTags.TIN_CANISTERS)
                .addTag(GCItemTags.COPPER_CANISTERS);

        this.tag(GCItemTags.SOLAR_DUSTS)
                .add(GCItems.SOLAR_DUST);
        this.tag(ConventionalItemTags.DUSTS)
                .addTag(GCItemTags.SOLAR_DUSTS);

        this.tag(ConventionalItemTags.BREAD_FOODS)
                .add(GCItems.BURGER_BUN);
        this.tag(ItemTags.MEAT)
                .add(GCItems.GROUND_BEEF)
                .add(GCItems.BEEF_PATTY);
        this.tag(ConventionalItemTags.RAW_MEAT_FOODS)
                .add(GCItems.GROUND_BEEF);
        this.tag(ConventionalItemTags.COOKED_MEAT_FOODS)
                .add(GCItems.BEEF_PATTY);
        this.tag(ConventionalItemTags.EDIBLE_WHEN_PLACED_FOODS)
                .add(GCBlocks.MOON_CHEESE_WHEEL.asItem());
        this.tag(GCItemTags.CHEESE_FOODS)
                .add(GCItems.MOON_CHEESE_CURD)
                .add(GCItems.MOON_CHEESE_SLICE)
                .add(GCBlocks.MOON_CHEESE_WHEEL.asItem())
                .add(GCItems.CHEESEBURGER);
        this.tag(ConventionalItemTags.FOODS)
                .addTag(GCItemTags.CHEESE_FOODS);

        this.tag(ItemTags.WOLF_FOOD)
                .add(GCItems.MOON_CHEESE_CURD)
                .add(GCItems.MOON_CHEESE_SLICE);

        this.tag(ConventionalItemTags.WHITE_DYED).add(GCItems.PARACHUTE.get(DyeColor.WHITE));
        this.tag(ConventionalItemTags.ORANGE_DYED).add(GCItems.PARACHUTE.get(DyeColor.ORANGE));
        this.tag(ConventionalItemTags.MAGENTA_DYED).add(GCItems.PARACHUTE.get(DyeColor.MAGENTA));
        this.tag(ConventionalItemTags.LIGHT_BLUE_DYED).add(GCItems.PARACHUTE.get(DyeColor.LIGHT_BLUE));
        this.tag(ConventionalItemTags.YELLOW_DYED).add(GCItems.PARACHUTE.get(DyeColor.YELLOW));
        this.tag(ConventionalItemTags.LIME_DYED).add(GCItems.PARACHUTE.get(DyeColor.LIME));
        this.tag(ConventionalItemTags.PINK_DYED).add(GCItems.PARACHUTE.get(DyeColor.PINK));
        this.tag(ConventionalItemTags.GRAY_DYED).add(GCItems.PARACHUTE.get(DyeColor.GRAY));
        this.tag(ConventionalItemTags.LIGHT_GRAY_DYED).add(GCItems.PARACHUTE.get(DyeColor.LIGHT_GRAY));
        this.tag(ConventionalItemTags.CYAN_DYED).add(GCItems.PARACHUTE.get(DyeColor.CYAN));
        this.tag(ConventionalItemTags.PURPLE_DYED).add(GCItems.PARACHUTE.get(DyeColor.PURPLE));
        this.tag(ConventionalItemTags.BLUE_DYED).add(GCItems.PARACHUTE.get(DyeColor.BLUE));
        this.tag(ConventionalItemTags.BROWN_DYED).add(GCItems.PARACHUTE.get(DyeColor.BROWN));
        this.tag(ConventionalItemTags.GREEN_DYED).add(GCItems.PARACHUTE.get(DyeColor.GREEN));
        this.tag(ConventionalItemTags.RED_DYED).add(GCItems.PARACHUTE.get(DyeColor.RED));
        this.tag(ConventionalItemTags.BLACK_DYED).add(GCItems.PARACHUTE.get(DyeColor.BLACK));

        this.tag(GCItemTags.OIL_BUCKETS).add(GCItems.CRUDE_OIL_BUCKET);
        this.tag(GCItemTags.FUEL_BUCKETS).add(GCItems.FUEL_BUCKET);
        this.tag(GCItemTags.SULFURIC_ACID_BUCKETS).add(GCItems.SULFURIC_ACID_BUCKET);
        this.tag(ConventionalItemTags.BUCKETS)
                .addTag(GCItemTags.OIL_BUCKETS)
                .addTag(GCItemTags.FUEL_BUCKETS)
                .addTag(GCItemTags.SULFURIC_ACID_BUCKETS);

        this.tag(ConventionalItemTags.PLAYER_WORKSTATIONS_FURNACES)
                .add(GCBlocks.ELECTRIC_FURNACE.asItem())
                .add(GCBlocks.ELECTRIC_ARC_FURNACE.asItem());
    }

    protected FabricTagProvider<Item>.FabricTagBuilder tag(TagKey<Item> tag) {
        return this.getOrCreateTagBuilder(tag);
    }
}
