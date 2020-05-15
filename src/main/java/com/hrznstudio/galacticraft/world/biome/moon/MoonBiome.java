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

package com.hrznstudio.galacticraft.world.biome.moon;

import com.hrznstudio.galacticraft.api.biome.SpaceBiome;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.world.gen.feature.GalacticraftFeatures;
import com.hrznstudio.galacticraft.world.gen.surfacebuilder.GalacticraftSurfaceBuilders;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

import java.util.Set;

/**
 * Base moon biome.
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public abstract class MoonBiome extends Biome implements SpaceBiome {

    public static final TernarySurfaceConfig MOON_HIGHLANDS_BIOME_CONFIG = new TernarySurfaceConfig(GalacticraftBlocks.MOON_TURF.getDefaultState(), GalacticraftBlocks.MOON_DIRT.getDefaultState(), GalacticraftBlocks.MOON_TURF.getDefaultState());

    public MoonBiome(Settings settings) {
        super(settings);
    }

    protected void addMoonVillages() {
        this.addFeature(GenerationStep.Feature.RAW_GENERATION, GalacticraftFeatures.MOON_VILLAGE.configure(new DefaultFeatureConfig()).createDecoratedFeature(new ConfiguredDecorator<>(Decorator.NOPE, DecoratorConfig.DEFAULT)));
        this.addStructureFeature(GalacticraftFeatures.MOON_VILLAGE.configure(FeatureConfig.DEFAULT));
    }

    @Override
    protected float computeTemperature(BlockPos blockPos) {
        return -1000.0F;
    }

    @Override
    public int getSkyColor() {
        return 0;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getFoliageColor() {
        return getWaterFogColor();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getGrassColorAt(double x, double z) {
        return getWaterColor();
    }

    @Override
    public TemperatureGroup getTemperatureGroup() {
        return TemperatureGroup.COLD;
    }
}
