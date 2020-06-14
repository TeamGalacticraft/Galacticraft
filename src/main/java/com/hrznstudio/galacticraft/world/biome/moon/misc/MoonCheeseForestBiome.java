/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.world.biome.moon.misc;

import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.world.biome.moon.MoonBiome;
import com.hrznstudio.galacticraft.world.gen.feature.GalacticraftFeatures;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public final class MoonCheeseForestBiome extends MoonBiome {

    public MoonCheeseForestBiome() {
        super((new Settings())
                .configureSurfaceBuilder(SurfaceBuilder.DEFAULT, new TernarySurfaceConfig(GalacticraftBlocks.MOON_TURF.getDefaultState(), GalacticraftBlocks.MOON_DIRT.getDefaultState(), GalacticraftBlocks.MOON_ROCK.getDefaultState()))
                .precipitation(Precipitation.NONE)
                .category(Category.NONE)
                .depth(0.03F)
                .scale(0.03F)
                .temperature(-100F)
                .downfall(0.005F)
                .effects(new BiomeEffects.Builder()
                        .waterColor(9937330)
                        .waterFogColor(11243183)
                        .fogColor(0)
                        .build())
                .parent(null));
        this.flowerFeatures.clear();
        this.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, Feature.TREE.configure(GalacticraftFeatures.CHEESE_TREE_CONFIG));
    }

    @Override
    protected String getCategoryName() {
        return "misc";
    }

    @Override
    protected String getBiomeName() {
        return "cheese_forest";
    }
}
