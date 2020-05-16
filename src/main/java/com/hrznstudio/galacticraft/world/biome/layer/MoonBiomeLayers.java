package com.hrznstudio.galacticraft.world.biome.layer;

import com.hrznstudio.galacticraft.world.biome.GalacticraftBiomes;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.IncreaseEdgeCurvatureLayer;
import net.minecraft.world.biome.layer.ScaleLayer;
import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.*;
import net.minecraft.world.biome.source.BiomeLayerSampler;

import java.util.function.LongFunction;

public class MoonBiomeLayers {
    public static final int MOON_VALLEY_ID = Registry.BIOME.getRawId(GalacticraftBiomes.MOON_VALLEY);
    public static final int MOON_CHEESE_FOREST_ID = Registry.BIOME.getRawId(GalacticraftBiomes.MOON_CHEESE_FOREST);
    public static final int MOON_HIGHLANDS_ROCKS_ID = Registry.BIOME.getRawId(GalacticraftBiomes.MOON_HIGHLANDS_ROCKS);
    public static final int MOON_MARE_ROCKS_ID = Registry.BIOME.getRawId(GalacticraftBiomes.MOON_MARE_ROCKS);
    public static final int MOON_HIGHLANDS_PLAINS_ID = Registry.BIOME.getRawId(GalacticraftBiomes.MOON_HIGHLANDS_PLAINS);
    public static final int MOON_MARE_PLAINS_ID = Registry.BIOME.getRawId(GalacticraftBiomes.MOON_MARE_PLAINS);

    private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> build(int biomeSize, int riverSize, LongFunction<C> contextProvider) {
        LayerFactory<T> layerFactory = MoonBaseBiomeLayer.INSTANCE.create(contextProvider.apply(1L));
        layerFactory = MoonBiomeTypeLayer.INSTANCE.create(contextProvider.apply(1999L), layerFactory);
        layerFactory = ScaleLayer.FUZZY.create(contextProvider.apply(2000L), layerFactory);
        layerFactory = MoonRavineLayer.INSTANCE.create(contextProvider.apply(2100L), layerFactory);
        layerFactory = ScaleLayer.NORMAL.create(contextProvider.apply(2001L), layerFactory);
//        layerFactory = AddIslandLayer.INSTANCE.create(contextProvider.apply(2L), layerFactory);

//        LayerFactory<T> layerFactory2 = OceanTemperatureLayer.INSTANCE.create(contextProvider.apply(2L));
//        layerFactory2 = stack(2001L, ScaleLayer.NORMAL, layerFactory2, 6, contextProvider);
//        layerFactory = AddColdClimatesLayer.INSTANCE.create(contextProvider.apply(2L), layerFactory);
//        layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(3L), layerFactory);
//        layerFactory = AddClimateLayers.AddTemperateBiomesLayer.INSTANCE.create(contextProvider.apply(2L), layerFactory);
//        layerFactory = AddClimateLayers.AddCoolBiomesLayer.INSTANCE.create(contextProvider.apply(2L), layerFactory);
//        layerFactory = AddClimateLayers.AddSpecialBiomesLayer.INSTANCE.create(contextProvider.apply(3L), layerFactory);
//        layerFactory = ScaleLayer.NORMAL.create(contextProvider.apply(2002L), layerFactory);
//        layerFactory = ScaleLayer.NORMAL.create(contextProvider.apply(2003L), layerFactory);
//        layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(4L), layerFactory);
//        layerFactory = AddMushroomIslandLayer.INSTANCE.create(contextProvider.apply(5L), layerFactory);
//        layerFactory = AddDeepOceanLayer.INSTANCE.create(contextProvider.apply(4L), layerFactory);
//        layerFactory = stack(1000L, ScaleLayer.NORMAL, layerFactory, 0, contextProvider);
//        LayerFactory<T> layerFactory3 = stack(1000L, ScaleLayer.NORMAL, layerFactory, 0, contextProvider);
//        layerFactory3 = SimpleLandNoiseLayer.INSTANCE.create(contextProvider.apply(100L), layerFactory3);
//        LayerFactory<T> layerFactory4 = (new SetBaseBiomesLayer(old)).create(contextProvider.apply(200L), layerFactory);
//        layerFactory4 = AddBambooJungleLayer.INSTANCE.create(contextProvider.apply(1001L), layerFactory4);
//        layerFactory4 = stack(1000L, ScaleLayer.NORMAL, layerFactory4, 2, contextProvider);
//        layerFactory4 = EaseBiomeEdgeLayer.INSTANCE.create(contextProvider.apply(1000L), layerFactory4);
//        LayerFactory<T> layerFactory5 = stack(1000L, ScaleLayer.NORMAL, layerFactory3, 2, contextProvider);
//        layerFactory4 = AddHillsLayer.INSTANCE.create(contextProvider.apply(1000L), layerFactory4, layerFactory5);
//        layerFactory3 = stack(1000L, ScaleLayer.NORMAL, layerFactory3, 2, contextProvider);
//        layerFactory3 = stack(1000L, ScaleLayer.NORMAL, layerFactory3, riverSize, contextProvider);
//        layerFactory3 = NoiseToRiverLayer.INSTANCE.create(contextProvider.apply(1L), layerFactory3);
//        layerFactory3 = SmoothenShorelineLayer.INSTANCE.create(contextProvider.apply(1000L), layerFactory3);
//        layerFactory4 = AddSunflowerPlainsLayer.INSTANCE.create(contextProvider.apply(1001L), layerFactory4);
//
        for(int i = 0; i < biomeSize; ++i) {
            layerFactory = ScaleLayer.NORMAL.create(contextProvider.apply(1000L + i), layerFactory);
            if (i == 0) {
                layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(3L), layerFactory);
            }

//            if (i == 1 || biomeSize == 1) {
//                layerFactory = AddEdgeBiomesLayer.INSTANCE.create(contextProvider.apply(1000L), layerFactory4);
//            }
        }
//
//        layerFactory4 = SmoothenShorelineLayer.INSTANCE.create(contextProvider.apply(1000L), layerFactory4);
////        layerFactory4 = MoonRavineLayer.INSTANCE.create(contextProvider.apply(100L), layerFactory4, layerFactory3);
//        layerFactory4 = ApplyOceanTemperatureLayer.INSTANCE.create(contextProvider.apply(100L), layerFactory4, layerFactory2);
        layerFactory = stack(2913L, ScaleLayer.NORMAL, layerFactory, 0, contextProvider);
        return layerFactory;
    }

    private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> stack(long seed, ParentedLayer layer, LayerFactory<T> parent, int count, LongFunction<C> contextProvider) {
        LayerFactory<T> layerFactory = parent;

        for(int i = 0; i < count; ++i) {
            layerFactory = layer.create(contextProvider.apply(seed + (long)i), layerFactory);
        }

        return layerFactory;
    }
    
    public static BiomeLayerSampler build(long seed, int biomeSize, int riverSize) {
        LayerFactory<CachingLayerSampler> layerFactory = build(biomeSize, riverSize, (salt) -> new CachingLayerContext(25, seed, salt));
        return new BiomeLayerSampler(layerFactory);
    }

    public static boolean areSimilar(int id1, int id2) {
        if (id1 == id2) {
            return true;
        } else {
            Biome biome = (Biome) Registry.BIOME.get(id1);
            Biome biome2 = (Biome)Registry.BIOME.get(id2);
            if (biome != null && biome2 != null) {
                if (biome != Biomes.WOODED_BADLANDS_PLATEAU && biome != Biomes.BADLANDS_PLATEAU) {
                    if (biome.getCategory() != Biome.Category.NONE && biome2.getCategory() != Biome.Category.NONE && biome.getCategory() == biome2.getCategory()) {
                        return true;
                    } else {
                        return biome == biome2;
                    }
                } else {
                    return biome2 == Biomes.WOODED_BADLANDS_PLATEAU || biome2 == Biomes.BADLANDS_PLATEAU;
                }
            } else {
                return false;
            }
        }
    }
}
