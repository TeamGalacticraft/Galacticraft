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

package dev.galacticraft.mod.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import org.jetbrains.annotations.Nullable;

import dev.galacticraft.mod.content.item.GCItem;

public class GCItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public GCItemTagProvider(FabricDataGenerator dataGenerator, @Nullable BlockTagProvider blockTagProvider) {
        super(dataGenerator, blockTagProvider);
    }

    @Override
    protected void generateTags() {
        this.tag(GCTags.SMELTABLE_ALUMINUM).add(GCItem.ALUMINUM_ORE, GCItem.DEEPSLATE_ALUMINUM_ORE, GCItem.RAW_ALUMINUM);
        this.tag(GCTags.SMELTABLE_DESH).add(GCItem.DESH_ORE, GCItem.RAW_DESH);
        this.tag(GCTags.SMELTABLE_SILICON).add(GCItem.SILICON_ORE, GCItem.DEEPSLATE_SILICON_ORE);
        this.tag(GCTags.SMELTABLE_TIN).add(GCItem.TIN_ORE, GCItem.DEEPSLATE_TIN_ORE, GCItem.RAW_TIN, GCItem.MOON_TIN_ORE, GCItem.LUNASLATE_TIN_ORE);
        this.tag(GCTags.SMELTABLE_TITANIUM).add(GCItem.RAW_TITANIUM, GCItem.ILMENITE_ORE);
    }
}
