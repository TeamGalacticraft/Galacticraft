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
        tag(ItemTags.TRIMMABLE_ARMOR)
                .add(GCItems.HEAVY_DUTY_HELMET)
                .add(GCItems.HEAVY_DUTY_CHESTPLATE)
                .add(GCItems.HEAVY_DUTY_LEGGINGS)
                .add(GCItems.HEAVY_DUTY_BOOTS)
                .add(GCItems.DESH_HELMET)
                .add(GCItems.DESH_CHESTPLATE)
                .add(GCItems.DESH_LEGGINGS)
                .add(GCItems.DESH_BOOTS)
                .add(GCItems.TITANIUM_HELMET)
                .add(GCItems.TITANIUM_CHESTPLATE)
                .add(GCItems.TITANIUM_LEGGINGS)
                .add(GCItems.TITANIUM_BOOTS);

        tag(GCTags.ALUMINUM_INGOTS)
                .add(GCItems.ALUMINUM_INGOT);
        tag(GCTags.RAW_ALUMINUM_ORES)
                .add(GCItems.RAW_ALUMINUM);
        tag(GCTags.LEAD_INGOTS)
                .add(GCItems.LEAD_INGOT);
        tag(GCTags.RAW_LEAD_ORES)
                .add(GCItems.RAW_LEAD);
        tag(GCTags.SILICONS)
                .add(GCItems.SILICON);
        tag(GCTags.TIN_INGOTS)
                .add(GCItems.TIN_INGOT);
        tag(GCTags.RAW_TIN_ORES)
                .add(GCItems.RAW_TIN);
        tag(GCTags.COMPRESSED_STEEL)
                .add(GCItems.COMPRESSED_STEEL);
        tag(GCTags.COMPRESSED_IRON)
                .add(GCItems.COMPRESSED_IRON);
        tag(GCTags.COMPRESSED_TIN)
                .add(GCItems.COMPRESSED_TIN);

        tag(ItemTags.CREEPER_DROP_MUSIC_DISCS)
                .add(GCItems.LEGACY_MUSIC_DISC_MARS)
                .add(GCItems.LEGACY_MUSIC_DISC_MIMAS)
                .add(GCItems.LEGACY_MUSIC_DISC_ORBIT)
                .add(GCItems.LEGACY_MUSIC_DISC_SPACERACE);

        // Ore Tags
        tag(ConventionalItemTags.ORES).add(
                GCBlocks.MARS_IRON_ORE.asItem(), GCBlocks.ASTEROID_IRON_ORE.asItem(),
                GCBlocks.MOON_COPPER_ORE.asItem(), GCBlocks.LUNASLATE_COPPER_ORE.asItem(), GCBlocks.MARS_COPPER_ORE.asItem(), GCBlocks.VENUS_COPPER_ORE.asItem(),
                GCBlocks.SILICON_ORE.asItem(), GCBlocks.DEEPSLATE_SILICON_ORE.asItem(),
                GCBlocks.FALLEN_METEOR.asItem(),
                GCBlocks.MOON_CHEESE_ORE.asItem(),
                GCBlocks.LUNAR_SAPPHIRE_ORE.asItem(),
                GCBlocks.OLIVINE_BASALT.asItem(), GCBlocks.RICH_OLIVINE_BASALT.asItem(),
                GCBlocks.TIN_ORE.asItem(), GCBlocks.DEEPSLATE_TIN_ORE.asItem(), GCBlocks.MOON_TIN_ORE.asItem(), GCBlocks.LUNASLATE_TIN_ORE.asItem(), GCBlocks.MARS_TIN_ORE.asItem(), GCBlocks.VENUS_TIN_ORE.asItem(),
                GCBlocks.ALUMINUM_ORE.asItem(), GCBlocks.DEEPSLATE_ALUMINUM_ORE.asItem(), GCBlocks.ASTEROID_ALUMINUM_ORE.asItem(), GCBlocks.VENUS_ALUMINUM_ORE.asItem(),
                GCBlocks.DESH_ORE.asItem(),
                GCBlocks.ILMENITE_ORE.asItem(),
                GCBlocks.GALENA_ORE.asItem(),
                GCBlocks.SOLAR_ORE.asItem()
        );

        tag(ConventionalItemTags.GEMS)
                .add(GCItems.OLIVINE_SHARD);

        tag(ConventionalItemTags.CLUSTERS)
                .add(GCBlocks.OLIVINE_CLUSTER.asItem());

        tag(ItemTags.MEAT)
                .add(GCItems.GROUND_BEEF)
                .add(GCItems.BEEF_PATTY);
    }

    protected FabricTagProvider<Item>.FabricTagBuilder tag(TagKey<Item> tag) {
        return getOrCreateTagBuilder(tag);
    }
}
