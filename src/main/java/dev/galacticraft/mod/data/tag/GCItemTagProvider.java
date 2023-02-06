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

import dev.galacticraft.mod.content.item.GCItem;
import dev.galacticraft.mod.tag.GCTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class GCItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public GCItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture, new GCBlockTagProvider(output, completableFuture));
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.tag(ConventionalItemTags.AXES)
                .add(this.reverseLookup(GCItem.HEAVY_DUTY_AXE))
                .add(this.reverseLookup(GCItem.DESH_AXE))
                .add(this.reverseLookup(GCItem.TITANIUM_AXE));
        this.tag(ConventionalItemTags.HOES)
                .add(this.reverseLookup(GCItem.HEAVY_DUTY_HOE))
                .add(this.reverseLookup(GCItem.DESH_HOE))
                .add(this.reverseLookup(GCItem.TITANIUM_HOE));
        this.tag(ConventionalItemTags.PICKAXES)
                .add(this.reverseLookup(GCItem.HEAVY_DUTY_PICKAXE))
                .add(this.reverseLookup(GCItem.DESH_PICKAXE))
                .add(this.reverseLookup(GCItem.TITANIUM_PICKAXE));
        this.tag(ConventionalItemTags.SHOVELS)
                .add(this.reverseLookup(GCItem.HEAVY_DUTY_SHOVEL))
                .add(this.reverseLookup(GCItem.DESH_SHOVEL))
                .add(this.reverseLookup(GCItem.TITANIUM_SHOVEL));
        this.tag(ConventionalItemTags.SWORDS)
                .add(this.reverseLookup(GCItem.HEAVY_DUTY_SWORD))
                .add(this.reverseLookup(GCItem.DESH_SWORD))
                .add(this.reverseLookup(GCItem.TITANIUM_SWORD));

        this.tag(GCTags.ALUMINUM_INGOTS)
                .add(this.reverseLookup(GCItem.ALUMINUM_INGOT));
        this.tag(GCTags.RAW_ALUMINUM_ORES)
                .add(this.reverseLookup(GCItem.RAW_ALUMINUM));
        this.tag(GCTags.LEAD_INGOTS)
                .add(this.reverseLookup(GCItem.LEAD_INGOT));
        this.tag(GCTags.RAW_LEAD_ORES)
                .add(this.reverseLookup(GCItem.RAW_LEAD));
        this.tag(GCTags.SILICONS)
                .add(this.reverseLookup(GCItem.RAW_SILICON));
        this.tag(GCTags.TIN_INGOTS)
                .add(this.reverseLookup(GCItem.TIN_INGOT));
        this.tag(GCTags.RAW_TIN_ORES)
                .add(this.reverseLookup(GCItem.RAW_TIN));
        this.tag(ItemTags.CREEPER_DROP_MUSIC_DISCS); //fixme
        //        "galacticraft:legacy_music_disc_mars",
        //        "galacticraft:legacy_music_disc_mimas",
        //        "galacticraft:legacy_music_disc_orbit",
        //        "galacticraft:legacy_music_disc_spacerace"
    }
}
