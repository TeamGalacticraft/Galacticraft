/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.tag.GCTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;

import java.util.concurrent.CompletableFuture;

public class GCItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public GCItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture, new GCBlockTagProvider(output, completableFuture));
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.getOrCreateTagBuilder(ItemTags.AXES)
                .add(GCItems.HEAVY_DUTY_AXE)
                .add(GCItems.DESH_AXE)
                .add(GCItems.TITANIUM_AXE);
        this.getOrCreateTagBuilder(ItemTags.HOES)
                .add(GCItems.HEAVY_DUTY_HOE)
                .add(GCItems.DESH_HOE)
                .add(GCItems.TITANIUM_HOE);
        this.getOrCreateTagBuilder(ItemTags.PICKAXES)
                .add(GCItems.HEAVY_DUTY_PICKAXE)
                .add(GCItems.DESH_PICKAXE)
                .add(GCItems.TITANIUM_PICKAXE);
        this.getOrCreateTagBuilder(ItemTags.SHOVELS)
                .add(GCItems.HEAVY_DUTY_SHOVEL)
                .add(GCItems.DESH_SHOVEL)
                .add(GCItems.TITANIUM_SHOVEL);
        this.getOrCreateTagBuilder(ItemTags.SWORDS)
                .add(GCItems.HEAVY_DUTY_SWORD)
                .add(GCItems.DESH_SWORD)
                .add(GCItems.TITANIUM_SWORD);
        this.getOrCreateTagBuilder(ItemTags.CLUSTER_MAX_HARVESTABLES)
                .add(GCItems.HEAVY_DUTY_PICKAXE)
                .add(GCItems.DESH_PICKAXE)
                .add(GCItems.TITANIUM_PICKAXE);
        this.getOrCreateTagBuilder(ItemTags.TRIMMABLE_ARMOR)
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

        this.getOrCreateTagBuilder(GCTags.ALUMINUM_INGOTS)
                .add(GCItems.ALUMINUM_INGOT);
        this.getOrCreateTagBuilder(GCTags.RAW_ALUMINUM_ORES)
                .add(GCItems.RAW_ALUMINUM);
        this.getOrCreateTagBuilder(GCTags.LEAD_INGOTS)
                .add(GCItems.LEAD_INGOT);
        this.getOrCreateTagBuilder(GCTags.RAW_LEAD_ORES)
                .add(GCItems.RAW_LEAD);
        this.getOrCreateTagBuilder(GCTags.SILICONS)
                .add(GCItems.RAW_SILICON);
        this.getOrCreateTagBuilder(GCTags.TIN_INGOTS)
                .add(GCItems.TIN_INGOT);
        this.getOrCreateTagBuilder(GCTags.RAW_TIN_ORES)
                .add(GCItems.RAW_TIN);
        this.getOrCreateTagBuilder(ItemTags.CREEPER_DROP_MUSIC_DISCS); //fixme
        //        "galacticraft:legacy_music_disc_mars",
        //        "galacticraft:legacy_music_disc_mimas",
        //        "galacticraft:legacy_music_disc_orbit",
        //        "galacticraft:legacy_music_disc_spacerace"

        // Ore Tags
        this.getOrCreateTagBuilder(ConventionalItemTags.ORES).add(
                GCItems.SILICON_ORE, GCItems.DEEPSLATE_SILICON_ORE,
                GCItems.MOON_COPPER_ORE, GCItems.LUNASLATE_COPPER_ORE,
                GCItems.TIN_ORE, GCItems.DEEPSLATE_TIN_ORE, GCItems.MOON_TIN_ORE, GCItems.LUNASLATE_TIN_ORE,
                GCItems.ALUMINUM_ORE, GCItems.DEEPSLATE_ALUMINUM_ORE,
                GCItems.DESH_ORE,
                GCItems.ILMENITE_ORE,
                GCItems.GALENA_ORE
        );
    }
}
