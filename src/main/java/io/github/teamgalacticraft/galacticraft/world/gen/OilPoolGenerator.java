package io.github.teamgalacticraft.galacticraft.world.gen;

import io.github.teamgalacticraft.galacticraft.blocks.GalacticraftBlocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.LakeDecoratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.LakeFeatureConfig;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class OilPoolGenerator {

    public static void registerOilLake() {
        for (Biome biome : Biome.BIOMES) {
            if (!biome.getCategory().equals(Biomes.NETHER.getCategory()) && !biome.getCategory().equals(Biomes.THE_END.getCategory())) {

                if (biome.getCategory() == Biome.Category.DESERT) {
                    biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Biome.configureFeature(Feature.LAKE, new LakeFeatureConfig(GalacticraftBlocks.CRUDE_OIL_BLOCK.getDefaultState()), Decorator.WATER_LAKE, new LakeDecoratorConfig(2)));
                }
                else {
                    biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, Biome.configureFeature(Feature.LAKE, new LakeFeatureConfig(GalacticraftBlocks.CRUDE_OIL_BLOCK.getDefaultState()), Decorator.WATER_LAKE, new LakeDecoratorConfig(1)));
                }
            }
        }
    }
}
