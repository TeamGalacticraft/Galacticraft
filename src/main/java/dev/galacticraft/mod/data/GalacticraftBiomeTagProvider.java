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

import dev.galacticraft.mod.tag.GalacticraftTag;
import dev.galacticraft.mod.world.biome.GalacticraftBiomeKey;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class GalacticraftBiomeTagProvider extends FabricTagProvider.DynamicRegistryTagProvider<Biome> {
    protected GalacticraftBiomeTagProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator, Registry.BIOME_REGISTRY);
    }

    @Override
    protected void generateTags() {
        tag(GalacticraftTag.MOON_HIGHLANDS)
                .add(GalacticraftBiomeKey.Moon.HIGHLANDS)
                .add(GalacticraftBiomeKey.Moon.HIGHLANDS_HILLS)
                .add(GalacticraftBiomeKey.Moon.HIGHLANDS_VALLEY)
                .add(GalacticraftBiomeKey.Moon.HIGHLANDS_FLAT)
                .add(GalacticraftBiomeKey.Moon.HIGHLANDS_EDGE);

        tag(GalacticraftTag.MOON_MARE)
                .add(GalacticraftBiomeKey.Moon.MARE)
                .add(GalacticraftBiomeKey.Moon.MARE_HILLS)
                .add(GalacticraftBiomeKey.Moon.MARE_VALLEY)
                .add(GalacticraftBiomeKey.Moon.MARE_FLAT)
                .add(GalacticraftBiomeKey.Moon.MARE_EDGE);
    }

    public FabricTagBuilder<Biome> tag(TagKey<Biome> tag) {
        return getOrCreateTagBuilder(tag);
    }
}
