/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

        this.tag(ItemTags.CREEPER_DROP_MUSIC_DISCS); //fixme
        //        "galacticraft:legacy_music_disc_mars",
        //        "galacticraft:legacy_music_disc_mimas",
        //        "galacticraft:legacy_music_disc_orbit",
        //        "galacticraft:legacy_music_disc_spacerace"

        // Ore Tags
        this.tag(ItemTags.COPPER_ORES)
                .add(GCItems.MOON_COPPER_ORE)
                .add(GCItems.LUNASLATE_COPPER_ORE);
        this.tag(GCItemTags.SILICON_ORES)
                .add(GCItems.SILICON_ORE)
                .add(GCItems.DEEPSLATE_SILICON_ORE);
        this.tag(GCItemTags.TIN_ORES)
                .add(GCItems.TIN_ORE)
                .add(GCItems.DEEPSLATE_TIN_ORE)
                .add(GCItems.MOON_TIN_ORE)
                .add(GCItems.LUNASLATE_TIN_ORE);
        this.tag(GCItemTags.ALUMINUM_ORES)
                .add(GCItems.ALUMINUM_ORE)
                .add(GCItems.DEEPSLATE_ALUMINUM_ORE);
        this.tag(GCItemTags.METEORIC_IRON_ORES)
                .add(GCItems.FALLEN_METEOR);
        this.tag(GCItemTags.OLIVINE_ORES)
                .add(GCItems.OLIVINE_BASALT, GCItems.RICH_OLIVINE_BASALT);
        this.tag(GCItemTags.DESH_ORES)
                .add(GCItems.DESH_ORE);
        this.tag(GCItemTags.LEAD_ORES)
                .add(GCItems.GALENA_ORE);
        this.tag(GCItemTags.TITANIUM_ORES)
                .add(GCItems.ILMENITE_ORE);
        this.tag(ConventionalItemTags.ORES)
                .addTag(GCItemTags.SILICON_ORES)
                .addTag(GCItemTags.TIN_ORES)
                .addTag(GCItemTags.ALUMINUM_ORES)
                .addTag(GCItemTags.METEORIC_IRON_ORES)
                .addTag(GCItemTags.OLIVINE_ORES)
                .addTag(GCItemTags.DESH_ORES)
                .addTag(GCItemTags.LEAD_ORES)
                .addTag(GCItemTags.TITANIUM_ORES);

        // Storage Block Tags
        this.tag(GCItemTags.SILICON_BLOCKS)
                .add(GCItems.SILICON_BLOCK);
        this.tag(GCItemTags.TIN_BLOCKS)
                .add(GCItems.TIN_BLOCK);
        this.tag(GCItemTags.ALUMINUM_BLOCKS)
                .add(GCItems.ALUMINUM_BLOCK);
        this.tag(GCItemTags.METEORIC_IRON_BLOCKS)
                .add(GCItems.METEORIC_IRON_BLOCK);
        this.tag(GCItemTags.LUNAR_SAPPHIRE_BLOCKS)
                .add(GCItems.LUNAR_SAPPHIRE_BLOCK);
        this.tag(GCItemTags.OLIVINE_BLOCKS)
                .add(GCItems.OLIVINE_BLOCK);
        this.tag(GCItemTags.DESH_BLOCKS)
                .add(GCItems.DESH_BLOCK);
        this.tag(GCItemTags.LEAD_BLOCKS)
                .add(GCItems.LEAD_BLOCK);
        this.tag(GCItemTags.TITANIUM_BLOCKS)
                .add(GCItems.TITANIUM_BLOCK);
        this.tag(GCItemTags.RAW_TIN_BLOCKS)
                .add(GCItems.RAW_TIN_BLOCK);
        this.tag(GCItemTags.RAW_ALUMINUM_BLOCKS)
                .add(GCItems.RAW_ALUMINUM_BLOCK);
        this.tag(GCItemTags.RAW_METEORIC_IRON_BLOCKS)
                .add(GCItems.RAW_METEORIC_IRON_BLOCK);
        this.tag(GCItemTags.RAW_DESH_BLOCKS)
                .add(GCItems.RAW_DESH_BLOCK);
        this.tag(GCItemTags.RAW_LEAD_BLOCKS)
                .add(GCItems.RAW_LEAD_BLOCK);
        this.tag(GCItemTags.RAW_TITANIUM_BLOCKS)
                .add(GCItems.RAW_TITANIUM_BLOCK);
        this.tag(ConventionalItemTags.STORAGE_BLOCKS)
                .addTag(GCItemTags.SILICON_BLOCKS)
                .addTag(GCItemTags.TIN_BLOCKS)
                .addTag(GCItemTags.ALUMINUM_BLOCKS)
                .addTag(GCItemTags.METEORIC_IRON_BLOCKS)
                .addTag(GCItemTags.OLIVINE_BLOCKS)
                .addTag(GCItemTags.LUNAR_SAPPHIRE_BLOCKS)
                .addTag(GCItemTags.DESH_BLOCKS)
                .addTag(GCItemTags.LEAD_BLOCKS)
                .addTag(GCItemTags.TITANIUM_BLOCKS)
                .addTag(GCItemTags.RAW_TIN_BLOCKS)
                .addTag(GCItemTags.RAW_ALUMINUM_BLOCKS)
                .addTag(GCItemTags.RAW_METEORIC_IRON_BLOCKS)
                .addTag(GCItemTags.RAW_DESH_BLOCKS)
                .addTag(GCItemTags.RAW_LEAD_BLOCKS)
                .addTag(GCItemTags.RAW_TITANIUM_BLOCKS);

        // Raw Material Tags
        this.tag(GCItemTags.RAW_TIN)
                .add(GCItems.RAW_TIN);
        this.tag(GCItemTags.RAW_ALUMINUM)
                .add(GCItems.RAW_ALUMINUM);
        this.tag(GCItemTags.RAW_METEORIC_IRON)
                .add(GCItems.RAW_METEORIC_IRON);
        this.tag(GCItemTags.RAW_DESH)
                .add(GCItems.RAW_DESH);
        this.tag(GCItemTags.RAW_LEAD)
                .add(GCItems.RAW_LEAD);
        this.tag(GCItemTags.RAW_TITANIUM)
                .add(GCItems.RAW_TITANIUM);
        this.tag(ConventionalItemTags.RAW_MATERIALS)
                .addTag(GCItemTags.RAW_TIN)
                .addTag(GCItemTags.RAW_ALUMINUM)
                .addTag(GCItemTags.RAW_METEORIC_IRON)
                .addTag(GCItemTags.RAW_DESH)
                .addTag(GCItemTags.RAW_LEAD)
                .addTag(GCItemTags.RAW_TITANIUM);

        // Ingot Tags
        this.tag(GCItemTags.TIN_INGOTS)
                .add(GCItems.TIN_INGOT);
        this.tag(GCItemTags.ALUMINUM_INGOTS)
                .add(GCItems.ALUMINUM_INGOT);
        this.tag(GCItemTags.METEORIC_IRON_INGOTS)
                .add(GCItems.METEORIC_IRON_INGOT);
        this.tag(GCItemTags.DESH_INGOTS)
                .add(GCItems.DESH_INGOT);
        this.tag(GCItemTags.LEAD_INGOTS)
                .add(GCItems.LEAD_INGOT);
        this.tag(GCItemTags.TITANIUM_INGOTS)
                .add(GCItems.TITANIUM_INGOT);
        // this.tag(GCItemTags.STEEL_INGOTS)
        //         .add(GCItems.STEEL_INGOT);
        this.tag(ConventionalItemTags.INGOTS)
                .addTag(GCItemTags.TIN_INGOTS)
                .addTag(GCItemTags.ALUMINUM_INGOTS)
                .addTag(GCItemTags.METEORIC_IRON_INGOTS)
                .addTag(GCItemTags.DESH_INGOTS)
                .addTag(GCItemTags.LEAD_INGOTS)
                .addTag(GCItemTags.TITANIUM_INGOTS);
        
        // Nugget Tags
        this.tag(GCItemTags.TIN_NUGGETS)
                .add(GCItems.TIN_NUGGET);
        this.tag(GCItemTags.ALUMINUM_NUGGETS)
                .add(GCItems.ALUMINUM_NUGGET);
        this.tag(GCItemTags.METEORIC_IRON_NUGGETS)
                .add(GCItems.METEORIC_IRON_NUGGET);
        this.tag(GCItemTags.DESH_NUGGETS)
                .add(GCItems.DESH_NUGGET);
        this.tag(GCItemTags.LEAD_NUGGETS)
                .add(GCItems.LEAD_NUGGET);
        this.tag(GCItemTags.TITANIUM_NUGGETS)
                .add(GCItems.TITANIUM_NUGGET);
        this.tag(ConventionalItemTags.NUGGETS)
                .addTag(GCItemTags.TIN_NUGGETS)
                .addTag(GCItemTags.ALUMINUM_NUGGETS)
                .addTag(GCItemTags.METEORIC_IRON_NUGGETS)
                .addTag(GCItemTags.DESH_NUGGETS)
                .addTag(GCItemTags.LEAD_NUGGETS)
                .addTag(GCItemTags.TITANIUM_NUGGETS);

        // Metal Plates/Compressed Metal Tags
        this.tag(GCItemTags.COMPRESSED_COPPER)
                .add(GCItems.COMPRESSED_COPPER);
        this.tag(GCItemTags.COMPRESSED_BRONZE)
                .add(GCItems.COMPRESSED_BRONZE);
        this.tag(GCItemTags.COMPRESSED_IRON)
                .add(GCItems.COMPRESSED_IRON);
        this.tag(GCItemTags.COMPRESSED_STEEL)
                .add(GCItems.COMPRESSED_STEEL);
        this.tag(GCItemTags.COMPRESSED_TIN)
                .add(GCItems.COMPRESSED_TIN);
        this.tag(GCItemTags.COMPRESSED_ALUMINUM)
                .add(GCItems.COMPRESSED_ALUMINUM);
        this.tag(GCItemTags.COMPRESSED_METEORIC_IRON)
                .add(GCItems.COMPRESSED_METEORIC_IRON);
        this.tag(GCItemTags.COMPRESSED_DESH)
                .add(GCItems.COMPRESSED_DESH);
        this.tag(GCItemTags.COMPRESSED_TITANIUM)
                .add(GCItems.COMPRESSED_TITANIUM);
        this.tag(GCItemTags.PLATES)
                .addTag(GCItemTags.COMPRESSED_COPPER)
                .addTag(GCItemTags.COMPRESSED_BRONZE)
                .addTag(GCItemTags.COMPRESSED_IRON)
                .addTag(GCItemTags.COMPRESSED_STEEL)
                .addTag(GCItemTags.COMPRESSED_TIN)
                .addTag(GCItemTags.COMPRESSED_ALUMINUM)
                .addTag(GCItemTags.COMPRESSED_METEORIC_IRON)
                .addTag(GCItemTags.COMPRESSED_DESH)
                .addTag(GCItemTags.COMPRESSED_TITANIUM);

        this.tag(ConventionalItemTags.GEMS)
                .add(GCItems.OLIVINE_SHARD);

        this.tag(ConventionalItemTags.CLUSTERS)
                .add(GCBlocks.OLIVINE_CLUSTER.asItem());

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
                .add(GCItems.MOON_CHEESE_WHEEL);
        this.tag(GCItemTags.CHEESE_FOODS)
                .add(GCItems.CHEESE_CURD)
                .add(GCItems.CHEESE_SLICE)
                .add(GCItems.MOON_CHEESE_WHEEL)
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
                .add(GCItems.CHEESE_CURD)
                .add(GCItems.CHEESE_SLICE);

        this.tag(ConventionalItemTags.WHITE_DYED).add(GCItems.PARACHUTE.get(DyeColor.WHITE));
        this.tag(ConventionalItemTags.ORANGE_DYED).add(GCItems.PARACHUTE.get(DyeColor.ORANGE));
        this.tag(ConventionalItemTags.MAGENTA_DYED).add(GCItems.PARACHUTE.get(DyeColor.MAGENTA));
        this.tag(ConventionalItemTags.LIGHT_BLUE_DYED).add(GCItems.PARACHUTE.get(DyeColor.LIGHT_BLUE));
        this.tag(ConventionalItemTags.YELLOW_DYED).add(GCItems.PARACHUTE.get(DyeColor.YELLOW));
        this.tag(ConventionalItemTags.LIME_DYED).add(GCItems.PARACHUTE.get(DyeColor.LIME));
        this.tag(ConventionalItemTags.PINK_DYED).add(GCItems.PARACHUTE.get(DyeColor.PINK));
        this.tag(ConventionalItemTags.GRAY_DYED).add(GCItems.PARACHUTE.get(DyeColor.GRAY));
        this.tag(ConventionalItemTags.LIGHT_GRAY_DYED).add(GCItems.PARACHUTE.get(DyeColor.LIGHT_GRAY));
        this.tag(ConventionalItemTags.PURPLE_DYED).add(GCItems.PARACHUTE.get(DyeColor.PURPLE));
        this.tag(ConventionalItemTags.BLUE_DYED).add(GCItems.PARACHUTE.get(DyeColor.BLUE));
        this.tag(ConventionalItemTags.BROWN_DYED).add(GCItems.PARACHUTE.get(DyeColor.BROWN));
        this.tag(ConventionalItemTags.GREEN_DYED).add(GCItems.PARACHUTE.get(DyeColor.GREEN));
        this.tag(ConventionalItemTags.RED_DYED).add(GCItems.PARACHUTE.get(DyeColor.RED));
        this.tag(ConventionalItemTags.BLACK_DYED).add(GCItems.PARACHUTE.get(DyeColor.BLACK));
    }

    protected FabricTagProvider<Item>.FabricTagBuilder tag(TagKey<Item> tag) {
        return this.getOrCreateTagBuilder(tag);
    }
}
