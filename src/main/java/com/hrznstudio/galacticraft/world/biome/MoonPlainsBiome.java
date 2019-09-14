/*
 * Copyright (c) 2018-2019 Horizon Studio
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

package com.hrznstudio.galacticraft.world.biome;

import com.hrznstudio.galacticraft.world.gen.decorator.GalacticraftDecorators;
import com.hrznstudio.galacticraft.world.gen.surfacebuilder.GalacticraftSurfaceBuilders;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public final class MoonPlainsBiome extends Biome {

    public MoonPlainsBiome() {
        super((new Settings())
                .configureSurfaceBuilder(SurfaceBuilder.DEFAULT, GalacticraftSurfaceBuilders.MOON_CONFIG)
                .precipitation(Precipitation.NONE)
                .category(Category.NONE)
                .depth(0.03F)
                .scale(0.03F)
                .temperature(0.0F)
                .downfall(0.005F)
                .waterColor(9937330)
                .waterFogColor(11253183)
                .parent(null));
        this.flowerFeatures.clear();
        this.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, GalacticraftDecorators.CRATER_CONF);
    }

    @Override
    public String getTranslationKey() {
        return "biome.galacticraft-rewoven.moon_plains";
    }

    @Override
    public int getSkyColor(float float_1) {
        return 0;
    }

    @Override
    public int getFoliageColorAt(BlockPos blockPos_1) {
        return waterFogColor;
    }

    @Override
    public int getGrassColorAt(BlockPos blockPos_1) {
        return waterColor;
    }
}
