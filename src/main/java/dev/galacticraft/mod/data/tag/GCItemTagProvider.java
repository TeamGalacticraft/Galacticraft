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
                .add(GCItems.THERMAL_PADDING_HELMET);

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

        this.tag(ItemTags.CREEPER_DROP_MUSIC_DISCS); //fixme
        //        "galacticraft:legacy_music_disc_mars",
        //        "galacticraft:legacy_music_disc_mimas",
        //        "galacticraft:legacy_music_disc_orbit",
        //        "galacticraft:legacy_music_disc_spacerace"

        // Ore Tags
        tag(ConventionalItemTags.ORES).add(
                GCBlocks.SILICON_ORE.asItem(), GCBlocks.DEEPSLATE_SILICON_ORE.asItem(),
                GCBlocks.MOON_COPPER_ORE.asItem(), GCBlocks.LUNASLATE_COPPER_ORE.asItem(),
                GCBlocks.OLIVINE_BASALT.asItem(), GCBlocks.RICH_OLIVINE_BASALT.asItem(),
                GCBlocks.TIN_ORE.asItem(), GCBlocks.DEEPSLATE_TIN_ORE.asItem(), GCBlocks.MOON_TIN_ORE.asItem(), GCBlocks.LUNASLATE_TIN_ORE.asItem(),
                GCBlocks.ALUMINUM_ORE.asItem(), GCBlocks.DEEPSLATE_ALUMINUM_ORE.asItem(),
                GCBlocks.DESH_ORE.asItem(),
                GCBlocks.ILMENITE_ORE.asItem(),
                GCBlocks.GALENA_ORE.asItem()
        );

        this.tag(GCItemTags.LUNAR_SAPPHIRES)
                .add(GCItems.LUNAR_SAPPHIRE);
        this.tag(GCItemTags.OLIVINE_SHARDS)
                .add(GCItems.OLIVINE_SHARD);
        this.tag(ConventionalItemTags.GEMS)
                .addTag(GCItemTags.LUNAR_SAPPHIRES)
                .addTag(GCItemTags.OLIVINE_SHARDS);

        this.tag(ConventionalItemTags.CLUSTERS)
                .add(GCBlocks.OLIVINE_CLUSTER.asItem());

        this.tag(GCItemTags.SOLAR_DUSTS)
                .add(GCItems.SOLAR_DUST);
        this.tag(ConventionalItemTags.DUSTS)
                .addTag(GCItemTags.SOLAR_DUSTS);

        this.tag(ConventionalItemTags.FRUIT_FOODS)
                .add(GCItems.CANNED_DEHYDRATED_APPLE)
                .add(GCItems.CANNED_DEHYDRATED_MELON);
        this.tag(ConventionalItemTags.VEGETABLE_FOODS)
                .add(GCItems.CANNED_DEHYDRATED_CARROT)
                .add(GCItems.CANNED_DEHYDRATED_POTATO);
        this.tag(ConventionalItemTags.BREAD_FOODS)
                .add(GCItems.BURGER_BUN);
        this.tag(ItemTags.MEAT)
                .add(GCItems.GROUND_BEEF)
                .add(GCItems.BEEF_PATTY);
        this.tag(ConventionalItemTags.RAW_MEAT_FOODS)
                .add(GCItems.GROUND_BEEF)
                .add(GCItems.CANNED_BEEF);
        this.tag(ConventionalItemTags.COOKED_MEAT_FOODS)
                .add(GCItems.BEEF_PATTY);
        this.tag(ConventionalItemTags.EDIBLE_WHEN_PLACED_FOODS)
                .add(GCBlocks.MOON_CHEESE_WHEEL.asItem());
        this.tag(GCItemTags.CHEESE_FOODS)
                .add(GCItems.MOON_CHEESE_CURD)
                .add(GCItems.MOON_CHEESE_SLICE)
                .add(GCBlocks.MOON_CHEESE_WHEEL.asItem())
                .add(GCItems.CHEESEBURGER);
        this.tag(GCItemTags.CANNED_FOODS)
                .add(GCItems.CANNED_DEHYDRATED_APPLE)
                .add(GCItems.CANNED_DEHYDRATED_CARROT)
                .add(GCItems.CANNED_DEHYDRATED_MELON)
                .add(GCItems.CANNED_DEHYDRATED_POTATO)
                .add(GCItems.CANNED_BEEF);
        this.tag(ConventionalItemTags.FOODS)
                .addTag(GCItemTags.CHEESE_FOODS)
                .addTag(GCItemTags.CANNED_FOODS);

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
