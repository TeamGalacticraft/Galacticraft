package io.github.teamgalacticraft.galacticraft.world.gen;

import io.github.teamgalacticraft.galacticraft.blocks.GalacticraftBlocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class OreGenerator {

    public static void register() {
        registerOverworld();
        registerMoon();
        registerMars();
    }

    private static void registerOverworld() {
        for (Biome biome : Biome.BIOMES) {
            if (!biome.getCategory().equals(Biomes.NETHER.getCategory()) && !biome.getCategory().equals(Biomes.THE_END.getCategory())) {

                biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Biome.configureFeature(Feature.ORE, new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, GalacticraftBlocks.ALUMINUM_ORE_BLOCK.getDefaultState(), 8), Decorator.COUNT_RANGE, new RangeDecoratorConfig(10, 0, 0, 45)));
                biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Biome.configureFeature(Feature.ORE, new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, GalacticraftBlocks.COPPER_ORE_BLOCK.getDefaultState(), 8), Decorator.COUNT_RANGE, new RangeDecoratorConfig(10, 0, 0, 45)));
                biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Biome.configureFeature(Feature.ORE, new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, GalacticraftBlocks.TIN_ORE_BLOCK.getDefaultState(), 8), Decorator.COUNT_RANGE, new RangeDecoratorConfig(10, 0, 0, 45)));
                biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, Biome.configureFeature(Feature.ORE, new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, GalacticraftBlocks.SILICON_ORE_BLOCK.getDefaultState(), 4), Decorator.COUNT_RANGE, new RangeDecoratorConfig(3, 0, 0, 25)));
            }
        }
    }

    private static void registerMoon() {

    }

    private static void registerMars() {

    }
}
