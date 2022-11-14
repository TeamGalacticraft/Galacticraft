/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

import dev.galacticraft.mod.world.biome.GCBiomeKey;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class GCBiomeTagProvider extends FabricTagProvider.DynamicRegistryTagProvider<Biome> {
    protected GCBiomeTagProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator, Registry.BIOME_REGISTRY);
    }

    @Override
    protected void generateTags() {
        tag(GCTags.MOON_HIGHLANDS)
                .add(GCBiomeKey.Moon.HIGHLANDS)
                .add(GCBiomeKey.Moon.HIGHLANDS_HILLS)
                .add(GCBiomeKey.Moon.HIGHLANDS_VALLEY)
                .add(GCBiomeKey.Moon.HIGHLANDS_FLAT)
                .add(GCBiomeKey.Moon.HIGHLANDS_EDGE);

        tag(GCTags.MOON_MARE)
                .add(GCBiomeKey.Moon.MARE)
                .add(GCBiomeKey.Moon.MARE_HILLS)
                .add(GCBiomeKey.Moon.MARE_VALLEY)
                .add(GCBiomeKey.Moon.MARE_FLAT)
                .add(GCBiomeKey.Moon.MARE_EDGE);
    }

    public FabricTagBuilder<Biome> tag(TagKey<Biome> tag) {
        return getOrCreateTagBuilder(tag);
    }
}
