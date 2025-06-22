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

import dev.galacticraft.mod.api.block.entity.PipeColor;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCRegistry.ColorSet;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.tag.GCBlockTags;
import dev.galacticraft.mod.tag.GCItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class GCItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public GCItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture, FabricTagProvider.BlockTagProvider blockTags) {
        super(output, completableFuture, blockTags);
    }

    public static final Map<DyeColor, TagKey<Item>> DYED_ITEM_TAGS = Util.make(new EnumMap<>(DyeColor.class), map -> {
        map.put(DyeColor.WHITE, ConventionalItemTags.WHITE_DYED);
        map.put(DyeColor.ORANGE, ConventionalItemTags.ORANGE_DYED);
        map.put(DyeColor.MAGENTA, ConventionalItemTags.MAGENTA_DYED);
        map.put(DyeColor.LIGHT_BLUE, ConventionalItemTags.LIGHT_BLUE_DYED);
        map.put(DyeColor.YELLOW, ConventionalItemTags.YELLOW_DYED);
        map.put(DyeColor.LIME, ConventionalItemTags.LIME_DYED);
        map.put(DyeColor.PINK, ConventionalItemTags.PINK_DYED);
        map.put(DyeColor.GRAY, ConventionalItemTags.GRAY_DYED);
        map.put(DyeColor.LIGHT_GRAY, ConventionalItemTags.LIGHT_GRAY_DYED);
        map.put(DyeColor.CYAN, ConventionalItemTags.CYAN_DYED);
        map.put(DyeColor.PURPLE, ConventionalItemTags.PURPLE_DYED);
        map.put(DyeColor.BLUE, ConventionalItemTags.BLUE_DYED);
        map.put(DyeColor.BROWN, ConventionalItemTags.BROWN_DYED);
        map.put(DyeColor.GREEN, ConventionalItemTags.GREEN_DYED);
        map.put(DyeColor.RED, ConventionalItemTags.RED_DYED);
        map.put(DyeColor.BLACK, ConventionalItemTags.BLACK_DYED);
    });

    protected<T extends ItemLike> void addColorSet(ColorSet<T> set, @Nullable TagKey<Item> itemTag) {
        for (Map.Entry<DyeColor, TagKey<Item>> entry : DYED_ITEM_TAGS.entrySet()) {
            this.tag(entry.getValue()).add(set.get(entry.getKey()).asItem());
            if (itemTag != null) {
                this.tag(itemTag).add(set.get(entry.getKey()).asItem());
            }
        }
    }

    protected<T extends ItemLike> void addColorSet(ColorSet<T> set) {
        this.addColorSet(set, null);
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

        // Thermal armor
        this.tag(ItemTags.FREEZE_IMMUNE_WEARABLES)
                .add(GCItems.THERMAL_PADDING_BOOTS)
                .add(GCItems.THERMAL_PADDING_LEGGINGS)
                .add(GCItems.THERMAL_PADDING_CHESTPIECE)
                .add(GCItems.THERMAL_PADDING_HELMET)
                .add(GCItems.ISOTHERMAL_PADDING_BOOTS)
                .add(GCItems.ISOTHERMAL_PADDING_LEGGINGS)
                .add(GCItems.ISOTHERMAL_PADDING_CHESTPIECE)
                .add(GCItems.ISOTHERMAL_PADDING_HELMET);
        this.tag(GCItemTags.THERMAL_HEAD)
                .add(GCItems.THERMAL_PADDING_HELMET)
                .add(GCItems.ISOTHERMAL_PADDING_HELMET);
        this.tag(GCItemTags.THERMAL_CHEST)
                .add(GCItems.THERMAL_PADDING_CHESTPIECE)
                .add(GCItems.ISOTHERMAL_PADDING_CHESTPIECE);
        this.tag(GCItemTags.THERMAL_PANTS)
                .add(GCItems.THERMAL_PADDING_LEGGINGS)
                .add(GCItems.ISOTHERMAL_PADDING_LEGGINGS);
        this.tag(GCItemTags.THERMAL_BOOTS)
                .add(GCItems.THERMAL_PADDING_BOOTS)
                .add(GCItems.ISOTHERMAL_PADDING_BOOTS);

        // Glass fluid pipes
        this.copy(GCBlockTags.GLASS_FLUID_PIPES, GCItemTags.GLASS_FLUID_PIPES);
        this.copy(GCBlockTags.STAINED_GLASS_FLUID_PIPES, GCItemTags.STAINED_GLASS_FLUID_PIPES);
        for (Map.Entry<DyeColor, TagKey<Item>> entry : DYED_ITEM_TAGS.entrySet()) {
            PipeColor color = PipeColor.fromDye(entry.getKey());
            Item pipe = GCBlocks.GLASS_FLUID_PIPES.get(color).asItem();
            this.tag(entry.getValue()).add(pipe);
        }

        // Oxygen equipment
        this.tag(GCItemTags.OXYGEN_MASKS)
                .add(GCItems.OXYGEN_MASK);
        this.tag(GCItemTags.OXYGEN_GEAR)
                .add(GCItems.OXYGEN_GEAR);
        this.tag(GCItemTags.OXYGEN_TANKS)
                .add(GCItems.SMALL_OXYGEN_TANK)
                .add(GCItems.MEDIUM_OXYGEN_TANK)
                .add(GCItems.LARGE_OXYGEN_TANK)
                .add(GCItems.INFINITE_OXYGEN_TANK);

        // Other accessories
        this.addColorSet(GCItems.PARACHUTE, GCItemTags.PARACHUTES);
        this.tag(GCItemTags.FREQUENCY_MODULES)
                .add(GCItems.FREQUENCY_MODULE);
        this.tag(GCItemTags.SHIELD_CONTROLLERS)
                .add(GCItems.SHIELD_CONTROLLER);
        this.tag(GCItemTags.ACCESSORIES)
                .addTag(GCItemTags.FREQUENCY_MODULES)
                .addTag(GCItemTags.PARACHUTES)
                .addTag(GCItemTags.SHIELD_CONTROLLERS);

        this.tag(GCItemTags.WRENCHES)
                .add(GCItems.STANDARD_WRENCH);

        this.tag(GCItemTags.BATTERIES)
                .add(GCItems.BATTERY)
                .add(GCItems.INFINITE_BATTERY);

        this.tag(GCItemTags.ROCKET_STORAGE_UPGRADE_ITEMS)
                .forceAddTag(ConventionalItemTags.WOODEN_CHESTS)
                .forceAddTag(ConventionalItemTags.WOODEN_BARRELS);

        this.tag(ConventionalItemTags.MUSIC_DISCS)
                .add(GCItems.LEGACY_MUSIC_DISC_MARS)
                .add(GCItems.LEGACY_MUSIC_DISC_MIMAS)
                .add(GCItems.LEGACY_MUSIC_DISC_ORBIT)
                .add(GCItems.LEGACY_MUSIC_DISC_SPACERACE);
        this.tag(GCItemTags.EVOLVED_CREEPER_DROP_MUSIC_DISCS)
                .add(GCItems.LEGACY_MUSIC_DISC_MARS)
                .add(GCItems.LEGACY_MUSIC_DISC_MIMAS)
                .add(GCItems.LEGACY_MUSIC_DISC_ORBIT)
                .add(GCItems.LEGACY_MUSIC_DISC_SPACERACE);

        // ORE TAGS
        this.tag(ItemTags.IRON_ORES)
                .add(GCBlocks.MARS_IRON_ORE.asItem())
                .add(GCBlocks.ASTEROID_IRON_ORE.asItem());
        this.tag(ItemTags.COPPER_ORES)
                .add(GCBlocks.MOON_COPPER_ORE.asItem())
                .add(GCBlocks.LUNASLATE_COPPER_ORE.asItem())
                .add(GCBlocks.MARS_COPPER_ORE.asItem())
                .add(GCBlocks.VENUS_COPPER_ORE.asItem());
        this.copy(GCBlockTags.SILICON_ORES, GCItemTags.SILICON_ORES);
        this.copy(GCBlockTags.TIN_ORES, GCItemTags.TIN_ORES);
        this.copy(GCBlockTags.ALUMINUM_ORES, GCItemTags.ALUMINUM_ORES);
        this.copy(GCBlockTags.CHEESE_ORES, GCItemTags.CHEESE_ORES);
        this.copy(GCBlockTags.LUNAR_SAPPHIRE_ORES, GCItemTags.LUNAR_SAPPHIRE_ORES);
        this.copy(GCBlockTags.OLIVINE_ORES, GCItemTags.OLIVINE_ORES);
        this.copy(GCBlockTags.METEORIC_IRON_ORES, GCItemTags.METEORIC_IRON_ORES);
        this.copy(GCBlockTags.DESH_ORES, GCItemTags.DESH_ORES);
        this.copy(GCBlockTags.TITANIUM_ORES, GCItemTags.TITANIUM_ORES);
        this.copy(GCBlockTags.LEAD_ORES, GCItemTags.LEAD_ORES);
        this.copy(GCBlockTags.SOLAR_ORES, GCItemTags.SOLAR_ORES);

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
                .addTag(GCItemTags.LEAD_ORES)
                .addTag(GCItemTags.SOLAR_ORES);

        // STORAGE BLOCK TAGS
        this.copy(GCBlockTags.TIN_BLOCKS, GCItemTags.TIN_BLOCKS);
        this.copy(GCBlockTags.ALUMINUM_BLOCKS, GCItemTags.ALUMINUM_BLOCKS);
        this.copy(GCBlockTags.METEORIC_IRON_BLOCKS, GCItemTags.METEORIC_IRON_BLOCKS);
        this.copy(GCBlockTags.DESH_BLOCKS, GCItemTags.DESH_BLOCKS);
        this.copy(GCBlockTags.TITANIUM_BLOCKS, GCItemTags.TITANIUM_BLOCKS);
        this.copy(GCBlockTags.LEAD_BLOCKS, GCItemTags.LEAD_BLOCKS);

        this.copy(GCBlockTags.SILICON_BLOCKS, GCItemTags.SILICON_BLOCKS);
        this.copy(GCBlockTags.CHEESE_BLOCKS, GCItemTags.CHEESE_BLOCKS);
        this.copy(GCBlockTags.LUNAR_SAPPHIRE_BLOCKS, GCItemTags.LUNAR_SAPPHIRE_BLOCKS);
        this.copy(GCBlockTags.OLIVINE_BLOCKS, GCItemTags.OLIVINE_BLOCKS);

        this.copy(GCBlockTags.RAW_TIN_BLOCKS, GCItemTags.RAW_TIN_BLOCKS);
        this.copy(GCBlockTags.RAW_ALUMINUM_BLOCKS, GCItemTags.RAW_ALUMINUM_BLOCKS);
        this.copy(GCBlockTags.RAW_METEORIC_IRON_BLOCKS, GCItemTags.RAW_METEORIC_IRON_BLOCKS);
        this.copy(GCBlockTags.RAW_DESH_BLOCKS, GCItemTags.RAW_DESH_BLOCKS);
        this.copy(GCBlockTags.RAW_TITANIUM_BLOCKS, GCItemTags.RAW_TITANIUM_BLOCKS);
        this.copy(GCBlockTags.RAW_LEAD_BLOCKS, GCItemTags.RAW_LEAD_BLOCKS);

        this.tag(ConventionalItemTags.STORAGE_BLOCKS)
                .addTag(GCItemTags.TIN_BLOCKS)
                .addTag(GCItemTags.ALUMINUM_BLOCKS)
                .addTag(GCItemTags.METEORIC_IRON_BLOCKS)
                .addTag(GCItemTags.DESH_BLOCKS)
                .addTag(GCItemTags.TITANIUM_BLOCKS)
                .addTag(GCItemTags.LEAD_BLOCKS)
                .addTag(GCItemTags.SILICON_BLOCKS)
                .addTag(GCItemTags.CHEESE_BLOCKS)
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
                .addTag(GCItemTags.CHEESE_FOODS)
                .addTag(GCItemTags.CANNED_FOODS);

        this.tag(GCItemTags.CANNED_FOODS)
                .add(GCItems.CANNED_FOOD);
        this.tag(GCItemTags.UNCANNABLE_FOODS)
                .forceAddTag(ConventionalItemTags.DRINKS);

        this.tag(ItemTags.WOLF_FOOD)
                .add(GCItems.MOON_CHEESE_CURD)
                .add(GCItems.MOON_CHEESE_SLICE);

        this.tag(GCItemTags.OIL_BUCKETS).add(GCItems.CRUDE_OIL_BUCKET);
        this.tag(GCItemTags.FUEL_BUCKETS).add(GCItems.FUEL_BUCKET);
        this.tag(GCItemTags.SULFURIC_ACID_BUCKETS).add(GCItems.SULFURIC_ACID_BUCKET);
        this.tag(ConventionalItemTags.BUCKETS)
                .addTag(GCItemTags.OIL_BUCKETS)
                .addTag(GCItemTags.FUEL_BUCKETS)
                .addTag(GCItemTags.SULFURIC_ACID_BUCKETS);

        this.copy(GCBlockTags.STAIRS, GCItemTags.STAIRS);
        this.copy(GCBlockTags.SLABS, GCItemTags.SLABS);
        this.copy(GCBlockTags.WALLS, GCItemTags.WALLS);
        this.tag(ItemTags.STAIRS).addTag(GCItemTags.STAIRS);
        this.tag(ItemTags.SLABS).addTag(GCItemTags.SLABS);
        this.tag(ItemTags.WALLS).addTag(GCItemTags.WALLS);

        this.tag(ConventionalItemTags.STONES)
                .add(GCBlocks.MOON_SURFACE_ROCK.asItem())
                .add(GCBlocks.MOON_ROCK.asItem())
                .add(GCBlocks.LUNASLATE.asItem())
                .add(GCBlocks.MARS_SURFACE_ROCK.asItem())
                .add(GCBlocks.MARS_SUB_SURFACE_ROCK.asItem())
                .add(GCBlocks.MARS_STONE.asItem())
                .add(GCBlocks.SOFT_VENUS_ROCK.asItem())
                .add(GCBlocks.HARD_VENUS_ROCK.asItem());

        this.copy(GCBlockTags.MOON_COBBLESTONES, GCItemTags.MOON_COBBLESTONES);
        this.copy(GCBlockTags.LUNASLATE_COBBLESTONES, GCItemTags.LUNASLATE_COBBLESTONES);
        this.copy(GCBlockTags.MARS_COBBLESTONES, GCItemTags.MARS_COBBLESTONES);
        this.tag(ConventionalItemTags.COBBLESTONES)
                .addTag(GCItemTags.MOON_COBBLESTONES)
                .addTag(GCItemTags.LUNASLATE_COBBLESTONES)
                .addTag(GCItemTags.MARS_COBBLESTONES);

        this.tag(ItemTags.STONE_TOOL_MATERIALS)
                .add(GCBlocks.COBBLED_MOON_ROCK.asItem())
                .add(GCBlocks.COBBLED_LUNASLATE.asItem())
                .add(GCBlocks.MARS_COBBLESTONE.asItem())
                .add(GCBlocks.ASTEROID_ROCK.asItem())
                .add(GCBlocks.ASTEROID_ROCK_1.asItem())
                .add(GCBlocks.ASTEROID_ROCK_2.asItem());
        this.tag(ItemTags.STONE_CRAFTING_MATERIALS)
                .add(GCBlocks.COBBLED_MOON_ROCK.asItem())
                .add(GCBlocks.COBBLED_LUNASLATE.asItem())
                .add(GCBlocks.MARS_COBBLESTONE.asItem())
                .add(GCBlocks.ASTEROID_ROCK.asItem())
                .add(GCBlocks.ASTEROID_ROCK_1.asItem())
                .add(GCBlocks.ASTEROID_ROCK_2.asItem());

        this.tag(ConventionalItemTags.VILLAGER_JOB_SITES)
                .add(GCBlocks.LUNAR_CARTOGRAPHY_TABLE.asItem());
    }

    protected FabricTagProvider<Item>.@NotNull FabricTagBuilder tag(TagKey<Item> tag) {
        return this.getOrCreateTagBuilder(tag);
    }
}
