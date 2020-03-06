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

package com.hrznstudio.galacticraft.world.biome.moon.mare;

import com.hrznstudio.galacticraft.api.biome.SpaceBiome;
import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import com.hrznstudio.galacticraft.world.gen.surfacebuilder.BlockStateWithChance;
import com.hrznstudio.galacticraft.world.gen.surfacebuilder.GalacticraftSurfaceBuilders;
import com.hrznstudio.galacticraft.world.gen.surfacebuilder.MultiBlockSurfaceConfig;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public final class MoonMarePlainsBiome extends Biome implements SpaceBiome {

    public static final MultiBlockSurfaceConfig MOON_MARE_BIOME_CONFIG = new MultiBlockSurfaceConfig(
            new BlockStateWithChance[]{
                    new BlockStateWithChance(GalacticraftBlocks.MOON_BASALT.getDefaultState(), -1),
                    new BlockStateWithChance(GalacticraftBlocks.MOON_TURF.getDefaultState(), 100)}, //DISABLED for now. Need to find a good ratio
            new BlockStateWithChance[]{
                    new BlockStateWithChance(GalacticraftBlocks.MOON_BASALT.getDefaultState(), -1),
                    new BlockStateWithChance(GalacticraftBlocks.MOON_DIRT.getDefaultState(), 100)},
            new BlockStateWithChance[]{
                    new BlockStateWithChance(GalacticraftBlocks.MOON_BASALT.getDefaultState(), -1),
                    new BlockStateWithChance(GalacticraftBlocks.MOON_ROCK.getDefaultState(), 100)}
    );

    public MoonMarePlainsBiome() {
        super((new Settings())
                .configureSurfaceBuilder(GalacticraftSurfaceBuilders.MULTI_BLOCK_SURFACE_BUILDER, MOON_MARE_BIOME_CONFIG)
                .precipitation(Precipitation.NONE)
                .category(Category.NONE)
                .depth(0.03F)
                .scale(0.03F)
                .temperature(-100F)
                .downfall(0.005F)
                .waterColor(9937330)
                .waterFogColor(11253183)
                .parent(null));
        this.flowerFeatures.clear();
    }

    @Override
    protected float computeTemperature(BlockPos blockPos) {
        return -100F;
    }

    @Override
    public String getTranslationKey() {
        return "biome.galacticraft-rewoven.moon_mare_plains";
    }

    @Override
    public int getSkyColor() {
        return 0;
    }

    @Override
    public int getFoliageColor() {
        return waterFogColor;
    }

    @Override
    public int getGrassColorAt(double x, double z) {
        return waterColor;
    }

    @Override
    public TemperatureGroup getTemperatureGroup() {
        return TemperatureGroup.COLD;
    }

    @Override
    public Text getName() {
        return new TranslatableText(this.getTranslationKey());
    }
}
