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
import dev.galacticraft.mod.tag.GCTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class GCItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public GCItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture, new GCBlockTagProvider(output, completableFuture));
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ItemTags.AXES)
                .add(GCItems.HEAVY_DUTY_AXE)
                .add(GCItems.DESH_AXE)
                .add(GCItems.TITANIUM_AXE);
        tag(ItemTags.HOES)
                .add(GCItems.HEAVY_DUTY_HOE)
                .add(GCItems.DESH_HOE)
                .add(GCItems.TITANIUM_HOE);
        tag(ItemTags.PICKAXES)
                .add(GCItems.HEAVY_DUTY_PICKAXE)
                .add(GCItems.DESH_PICKAXE)
                .add(GCItems.TITANIUM_PICKAXE);
        tag(ItemTags.SHOVELS)
                .add(GCItems.HEAVY_DUTY_SHOVEL)
                .add(GCItems.DESH_SHOVEL)
                .add(GCItems.TITANIUM_SHOVEL);
        tag(ItemTags.SWORDS)
                .add(GCItems.HEAVY_DUTY_SWORD)
                .add(GCItems.DESH_SWORD)
                .add(GCItems.TITANIUM_SWORD);
        tag(ItemTags.CLUSTER_MAX_HARVESTABLES)
                .add(GCItems.HEAVY_DUTY_PICKAXE)
                .add(GCItems.DESH_PICKAXE)
                .add(GCItems.TITANIUM_PICKAXE);
        tag(ItemTags.HEAD_ARMOR)
                .add(GCItems.HEAVY_DUTY_HELMET)
                .add(GCItems.DESH_HELMET)
                .add(GCItems.TITANIUM_HELMET);
        tag(ItemTags.CHEST_ARMOR)
                .add(GCItems.HEAVY_DUTY_CHESTPLATE)
                .add(GCItems.DESH_CHESTPLATE)
                .add(GCItems.TITANIUM_CHESTPLATE);
        tag(ItemTags.LEG_ARMOR)
                .add(GCItems.HEAVY_DUTY_LEGGINGS)
                .add(GCItems.DESH_LEGGINGS)
                .add(GCItems.TITANIUM_LEGGINGS);
        tag(ItemTags.FOOT_ARMOR)
                .add(GCItems.HEAVY_DUTY_BOOTS)
                .add(GCItems.DESH_BOOTS)
                .add(GCItems.TITANIUM_BOOTS);

        tag(ItemTags.CREEPER_DROP_MUSIC_DISCS); //fixme
        //        "galacticraft:legacy_music_disc_mars",
        //        "galacticraft:legacy_music_disc_mimas",
        //        "galacticraft:legacy_music_disc_orbit",
        //        "galacticraft:legacy_music_disc_spacerace"

        // Ore Tags
        tag(GCTags.SILICON_ORES)
                .add(GCItems.SILICON_ORE)
                .add(GCItems.DEEPSLATE_SILICON_ORE);
        tag(ItemTags.COPPER_ORES)
                .add(GCItems.MOON_COPPER_ORE)
                .add(GCItems.LUNASLATE_COPPER_ORE);
        // tag(GCTags.OLIVINE_ORES)
        //         .add(GCBlocks.OLIVINE_BASALT.asItem())
        //         .add(GCBlocks.RICH_OLIVINE_BASALT.asItem());
        tag(GCTags.TIN_ORES)
                .add(GCItems.TIN_ORE)
                .add(GCItems.DEEPSLATE_TIN_ORE)
                .add(GCItems.MOON_TIN_ORE)
                .add(GCItems.LUNASLATE_TIN_ORE);
        tag(GCTags.ALUMINUM_ORES)
                .add(GCItems.ALUMINUM_ORE)
                .add(GCItems.DEEPSLATE_ALUMINUM_ORE);
        tag(GCTags.DESH_ORES)
                .add(GCItems.DESH_ORE);
        tag(GCTags.LEAD_ORES)
                .add(GCItems.GALENA_ORE);
        tag(GCTags.TITANIUM_ORES)
                .add(GCItems.ILMENITE_ORE);
        tag(ConventionalItemTags.ORES)
                .addOptionalTag(GCTags.SILICON_ORES)
                .addOptionalTag(GCTags.DESH_ORES)
                .addOptionalTag(GCTags.LEAD_ORES)
                .addOptionalTag(GCTags.ALUMINUM_ORES)
                .addOptionalTag(GCTags.TIN_ORES)
                .addOptionalTag(GCTags.TITANIUM_ORES);

        // Storage Block Tags
        tag(GCTags.SILICON_BLOCKS)
                .add(GCItems.SILICON_BLOCK);
        tag(GCTags.METEORIC_IRON_BLOCKS)
                .add(GCItems.METEORIC_IRON_BLOCK);
        tag(GCTags.DESH_BLOCKS)
                .add(GCItems.DESH_BLOCK);
        tag(GCTags.LEAD_BLOCKS)
                .add(GCItems.LEAD_BLOCK);
        tag(GCTags.ALUMINUM_BLOCKS)
                .add(GCItems.ALUMINUM_BLOCK);
        tag(GCTags.TIN_BLOCKS)
                .add(GCItems.TIN_BLOCK);
        tag(GCTags.TITANIUM_BLOCKS)
                .add(GCItems.TITANIUM_BLOCK);
        tag(GCTags.RAW_METEORIC_IRON_BLOCKS)
                .add(GCItems.RAW_METEORIC_IRON_BLOCK);
        tag(GCTags.RAW_DESH_BLOCKS)
                .add(GCItems.RAW_DESH_BLOCK);
        tag(GCTags.RAW_LEAD_BLOCKS)
                .add(GCItems.RAW_LEAD_BLOCK);
        tag(GCTags.RAW_ALUMINUM_BLOCKS)
                .add(GCItems.RAW_ALUMINUM_BLOCK);
        tag(GCTags.RAW_TIN_BLOCKS)
                .add(GCItems.RAW_TIN_BLOCK);
        tag(GCTags.RAW_TITANIUM_BLOCKS)
                .add(GCItems.RAW_TITANIUM_BLOCK);
        tag(ConventionalItemTags.STORAGE_BLOCKS)
                .addOptionalTag(GCTags.SILICON_BLOCKS)
                .addOptionalTag(GCTags.METEORIC_IRON_BLOCKS)
                .addOptionalTag(GCTags.DESH_BLOCKS)
                .addOptionalTag(GCTags.LEAD_BLOCKS)
                .addOptionalTag(GCTags.ALUMINUM_BLOCKS)
                .addOptionalTag(GCTags.TIN_BLOCKS)
                .addOptionalTag(GCTags.TITANIUM_BLOCKS)
                .addOptionalTag(GCTags.RAW_METEORIC_IRON_BLOCKS)
                .addOptionalTag(GCTags.RAW_DESH_BLOCKS)
                .addOptionalTag(GCTags.RAW_LEAD_BLOCKS)
                .addOptionalTag(GCTags.RAW_ALUMINUM_BLOCKS)
                .addOptionalTag(GCTags.RAW_TIN_BLOCKS)
                .addOptionalTag(GCTags.RAW_TITANIUM_BLOCKS);

        // Raw Material Tags
        tag(GCTags.RAW_METEORIC_IRON)
                .add(GCItems.RAW_METEORIC_IRON);
        tag(GCTags.RAW_DESH)
                .add(GCItems.RAW_DESH);
        tag(GCTags.RAW_LEAD)
                .add(GCItems.RAW_LEAD);
        tag(GCTags.RAW_ALUMINUM)
                .add(GCItems.RAW_ALUMINUM);
        tag(GCTags.RAW_TIN)
                .add(GCItems.RAW_TIN);
        tag(GCTags.RAW_TITANIUM)
                .add(GCItems.RAW_TITANIUM);
        tag(ConventionalItemTags.RAW_MATERIALS)
                .addOptionalTag(GCTags.RAW_METEORIC_IRON)
                .addOptionalTag(GCTags.RAW_DESH)
                .addOptionalTag(GCTags.RAW_LEAD)
                .addOptionalTag(GCTags.RAW_ALUMINUM)
                .addOptionalTag(GCTags.RAW_TIN)
                .addOptionalTag(GCTags.RAW_TITANIUM);

        // Ingot Tags
        tag(GCTags.METEORIC_IRON_INGOTS)
                .add(GCItems.METEORIC_IRON_INGOT);
        tag(GCTags.DESH_INGOTS)
                .add(GCItems.DESH_INGOT);
        tag(GCTags.LEAD_INGOTS)
                .add(GCItems.LEAD_INGOT);
        tag(GCTags.ALUMINUM_INGOTS)
                .add(GCItems.ALUMINUM_INGOT);
        tag(GCTags.TIN_INGOTS)
                .add(GCItems.TIN_INGOT);
        tag(GCTags.TITANIUM_INGOTS)
                .add(GCItems.TITANIUM_INGOT);
        // tag(GCTags.STEEL_INGOTS)
        //         .add(GCItems.STEEL_INGOT);
        tag(ConventionalItemTags.INGOTS)
                .addOptionalTag(GCTags.METEORIC_IRON_INGOTS)
                .addOptionalTag(GCTags.DESH_INGOTS)
                .addOptionalTag(GCTags.LEAD_INGOTS)
                .addOptionalTag(GCTags.ALUMINUM_INGOTS)
                .addOptionalTag(GCTags.TIN_INGOTS)
                .addOptionalTag(GCTags.TITANIUM_INGOTS);
        
        // Nugget Tags
        tag(GCTags.METEORIC_IRON_NUGGETS)
                .add(GCItems.METEORIC_IRON_NUGGET);
        tag(GCTags.DESH_NUGGETS)
                .add(GCItems.DESH_NUGGET);
        tag(GCTags.LEAD_NUGGETS)
                .add(GCItems.LEAD_NUGGET);
        tag(GCTags.ALUMINUM_NUGGETS)
                .add(GCItems.ALUMINUM_NUGGET);
        tag(GCTags.TIN_NUGGETS)
                .add(GCItems.TIN_NUGGET);
        tag(GCTags.TITANIUM_NUGGETS)
                .add(GCItems.TITANIUM_NUGGET);
        tag(ConventionalItemTags.NUGGETS)
                .addOptionalTag(GCTags.METEORIC_IRON_NUGGETS)
                .addOptionalTag(GCTags.DESH_NUGGETS)
                .addOptionalTag(GCTags.LEAD_NUGGETS)
                .addOptionalTag(GCTags.ALUMINUM_NUGGETS)
                .addOptionalTag(GCTags.TIN_NUGGETS)
                .addOptionalTag(GCTags.TITANIUM_NUGGETS);

        // Metal Plates/Compressed Metal Tags
        tag(GCTags.COMPRESSED_METEORIC_IRON)
                .add(GCItems.COMPRESSED_METEORIC_IRON);
        tag(GCTags.COMPRESSED_DESH)
                .add(GCItems.COMPRESSED_DESH);
        tag(GCTags.COMPRESSED_ALUMINUM)
                .add(GCItems.COMPRESSED_ALUMINUM);
        tag(GCTags.COMPRESSED_TIN)
                .add(GCItems.COMPRESSED_TIN);
        tag(GCTags.COMPRESSED_TITANIUM)
                .add(GCItems.COMPRESSED_TITANIUM);
        tag(GCTags.COMPRESSED_BRONZE)
                .add(GCItems.COMPRESSED_BRONZE);
        tag(GCTags.COMPRESSED_COPPER)
                .add(GCItems.COMPRESSED_COPPER);
        tag(GCTags.COMPRESSED_STEEL)
                .add(GCItems.COMPRESSED_STEEL);
        tag(GCTags.COMPRESSED_IRON)
                .add(GCItems.COMPRESSED_IRON);
        tag(GCTags.PLATES)
                .addOptionalTag(GCTags.COMPRESSED_METEORIC_IRON)
                .addOptionalTag(GCTags.COMPRESSED_DESH)
                .addOptionalTag(GCTags.COMPRESSED_ALUMINUM)
                .addOptionalTag(GCTags.COMPRESSED_TIN)
                .addOptionalTag(GCTags.COMPRESSED_TITANIUM)
                .addOptionalTag(GCTags.COMPRESSED_BRONZE)
                .addOptionalTag(GCTags.COMPRESSED_COPPER)
                .addOptionalTag(GCTags.COMPRESSED_STEEL)
                .addOptionalTag(GCTags.COMPRESSED_IRON);

        tag(ConventionalItemTags.GEMS)
                .add(GCItems.OLIVINE_SHARD);

        tag(ConventionalItemTags.CLUSTERS)
                .add(GCBlocks.OLIVINE_CLUSTER.asItem());

        tag(ConventionalItemTags.FRUIT_FOODS)
                .add(GCItems.CANNED_DEHYDRATED_APPLE)
                .add(GCItems.CANNED_DEHYDRATED_MELON);
        tag(ConventionalItemTags.VEGETABLE_FOODS)
                .add(GCItems.CANNED_DEHYDRATED_CARROT)
                .add(GCItems.CANNED_DEHYDRATED_POTATO);
        tag(ConventionalItemTags.BREAD_FOODS)
                .add(GCItems.BURGER_BUN);
        tag(ItemTags.MEAT)
                .add(GCItems.GROUND_BEEF)
                .add(GCItems.BEEF_PATTY);
        tag(ConventionalItemTags.RAW_MEAT_FOODS)
                .add(GCItems.GROUND_BEEF)
                .add(GCItems.CANNED_BEEF);
        tag(ConventionalItemTags.COOKED_MEAT_FOODS)
                .add(GCItems.BEEF_PATTY);
        tag(ConventionalItemTags.EDIBLE_WHEN_PLACED_FOODS)
                .add(GCItems.MOON_CHEESE_WHEEL);
        tag(GCTags.CHEESE_FOODS)
                .add(GCItems.CHEESE_CURD)
                .add(GCItems.CHEESE_SLICE)
                .add(GCItems.MOON_CHEESE_WHEEL)
                .add(GCItems.CHEESEBURGER);
        tag(GCTags.CANNED_FOODS)
                .add(GCItems.CANNED_DEHYDRATED_APPLE)
                .add(GCItems.CANNED_DEHYDRATED_CARROT)
                .add(GCItems.CANNED_DEHYDRATED_MELON)
                .add(GCItems.CANNED_DEHYDRATED_POTATO)
                .add(GCItems.CANNED_BEEF);
        tag(ConventionalItemTags.FOODS)
                .addOptionalTag(GCTags.CHEESE_FOODS)
                .addOptionalTag(GCTags.CANNED_FOODS);

        tag(ItemTags.WOLF_FOOD)
                .add(GCItems.CHEESE_CURD)
                .add(GCItems.CHEESE_SLICE);
    }

    protected FabricTagProvider<Item>.FabricTagBuilder tag(TagKey<Item> tag) {
        return getOrCreateTagBuilder(tag);
    }
}
