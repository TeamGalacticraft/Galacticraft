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

package com.hrznstudio.galacticraft.world.gen.feature;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.world.biome.GalacticraftBiomes;
import com.hrznstudio.galacticraft.world.gen.stateprovider.MoonFloraBlockStateProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.AcaciaFoliagePlacer;
import net.minecraft.world.gen.placer.SimpleBlockPlacer;
import net.minecraft.world.gen.stateprovider.PillarBlockStateProvider;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.trunk.ForkingTrunkPlacer;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftFeatures {
    public static final TreeFeatureConfig CHEESE_TREE_CONFIG = (new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(GalacticraftBlocks.MOON_CHEESE_LOG.getDefaultState()), new SimpleBlockStateProvider(GalacticraftBlocks.MOON_CHEESE_LEAVES.getDefaultState()), new AcaciaFoliagePlacer(2, 0, 0, 0), new ForkingTrunkPlacer(5, 2, 2), new TwoLayersFeatureSize(1, 0, 2))).ignoreVines().build();

    public static final StructureFeature<StructurePoolFeatureConfig> MOON_VILLAGE = Registry.register(Registry.STRUCTURE_FEATURE, new Identifier(Constants.MOD_ID, "moon_village"), new MoonVillageFeature(StructurePoolFeatureConfig.CODEC));
    public static final RandomPatchFeatureConfig MOON_FLOWER_CONFIG = (new RandomPatchFeatureConfig.Builder(new MoonFloraBlockStateProvider(), new SimpleBlockPlacer())).tries(64).build();
    public static final BlockPileFeatureConfig CHEESE_LOG_PILE_CONFIG = new BlockPileFeatureConfig(new PillarBlockStateProvider(GalacticraftBlocks.MOON_CHEESE_LOG));

    public static final GCOreFeature GC_ORE_FEATURE = new GCOreFeature(GCOreFeatureConfig.CODEC);

    public static void register() {
        StructureFeature.STRUCTURES.put("Moon_Village", MOON_VILLAGE);

        for (Biome biome : Biome.BIOMES) {
            if (!biome.getCategory().equals(Biome.Category.NETHER) && !biome.getCategory().equals(Biome.Category.THEEND)) {
                biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, GalacticraftBlocks.ALUMINUM_ORE.getDefaultState(), 8)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(18, 0, 0, 45))));
                biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, GalacticraftBlocks.COPPER_ORE.getDefaultState(), 8)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(24, 0, 0, 75))));
                biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, GalacticraftBlocks.TIN_ORE.getDefaultState(), 8)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(22, 0, 0, 60))));
                biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Feature.ORE.configure(new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, GalacticraftBlocks.SILICON_ORE.getDefaultState(), 5)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(3, 0, 0, 25))));
            }
        }

        for (Biome biome : GalacticraftBiomes.MOON_BIOMES) {
            biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, GC_ORE_FEATURE.configure(new GCOreFeatureConfig(GCOreFeatureConfig.Target.MOON, GalacticraftBlocks.MOON_TIN_ORE.getDefaultState(), 8)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(12, 0, 0, 60))));
            biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, GC_ORE_FEATURE.configure(new GCOreFeatureConfig(GCOreFeatureConfig.Target.MOON, GalacticraftBlocks.MOON_COPPER_ORE.getDefaultState(), 8)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(26, 0, 0, 60))));
            biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, GC_ORE_FEATURE.configure(new GCOreFeatureConfig(GCOreFeatureConfig.Target.MOON, GalacticraftBlocks.MOON_CHEESE_ORE.getDefaultState(), 4)).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(12, 0, 0, 128))));
        }
    }
}
