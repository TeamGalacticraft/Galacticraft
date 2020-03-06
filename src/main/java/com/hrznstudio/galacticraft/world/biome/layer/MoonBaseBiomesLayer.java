package com.hrznstudio.galacticraft.world.biome.layer;

import com.hrznstudio.galacticraft.world.biome.GalacticraftBiomes;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.type.IdentitySamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public class MoonBaseBiomesLayer implements IdentitySamplingLayer {
   private static final int MOON_HIGHLANDS_PLAINS_ID = Registry.BIOME.getRawId(GalacticraftBiomes.MOON_HIGHLANDS_PLAINS);
   private static final int MOON_HIGHLANDS_CRATERS_ID = Registry.BIOME.getRawId(GalacticraftBiomes.MOON_HIGHLANDS_CRATERS);
   private static final int MOON_HIGHLANDS_ROCKS_ID = Registry.BIOME.getRawId(GalacticraftBiomes.MOON_HIGHLANDS_ROCKS);

   private static final int MOON_MARE_PLAINS_ID = Registry.BIOME.getRawId(GalacticraftBiomes.MOON_MARE_PLAINS);
   private static final int MOON_MARE_CRATERS_ID = Registry.BIOME.getRawId(GalacticraftBiomes.MOON_MARE_CRATERS);
   private static final int MOON_MARE_ROCKS_ID = Registry.BIOME.getRawId(GalacticraftBiomes.MOON_MARE_ROCKS);
   private static final int MOON_CHEESE_FOREST_ID = Registry.BIOME.getRawId(GalacticraftBiomes.MOON_CHEESE_FOREST);

   private final int[] BIOMES = new int[]{MOON_HIGHLANDS_PLAINS_ID, MOON_HIGHLANDS_CRATERS_ID, MOON_HIGHLANDS_ROCKS_ID, MOON_MARE_PLAINS_ID, MOON_MARE_CRATERS_ID, MOON_MARE_ROCKS_ID};
   private final int[] MARE_BIOMES = new int[]{MOON_MARE_PLAINS_ID, MOON_MARE_CRATERS_ID, MOON_MARE_ROCKS_ID};
   private final int[] HIGHLANDS_BIOMES = new int[]{MOON_HIGHLANDS_PLAINS_ID, MOON_HIGHLANDS_CRATERS_ID, MOON_HIGHLANDS_ROCKS_ID};


   public MoonBaseBiomesLayer() {
   }

   @Override
   public int sample(LayerRandomnessSource context, int value) {
      value = value & -3841;
      if (value < 1 || value > 4) {
         System.out.println(value);
      }
      if (!isOcean(value) && value != MOON_CHEESE_FOREST_ID) {
         switch (value) {
            case 1:
            case 4:
               return BIOMES[context.nextInt(BIOMES.length)];
            case 2:
               return HIGHLANDS_BIOMES[context.nextInt(HIGHLANDS_BIOMES.length)];
            case 3:
               return MARE_BIOMES[context.nextInt(MARE_BIOMES.length)];
            default:
               return MOON_CHEESE_FOREST_ID;
         }
      } else {
         return value;
      }
   }

   private static final int WARM_OCEAN_ID = Registry.BIOME.getRawId(Biomes.WARM_OCEAN);
   private static final int LUKEWARM_OCEAN_ID = Registry.BIOME.getRawId(Biomes.LUKEWARM_OCEAN);
   private static final int OCEAN_ID = Registry.BIOME.getRawId(Biomes.OCEAN);
   private static final int COLD_OCEAN_ID = Registry.BIOME.getRawId(Biomes.COLD_OCEAN);
   private static final int FROZEN_OCEAN_ID = Registry.BIOME.getRawId(Biomes.FROZEN_OCEAN);
   private static final int DEEP_WARM_OCEAN_ID = Registry.BIOME.getRawId(Biomes.DEEP_WARM_OCEAN);
   private static final int DEEP_LUKEWARM_OCEAN_ID = Registry.BIOME.getRawId(Biomes.DEEP_LUKEWARM_OCEAN);
   private static final int DEEP_OCEAN_ID = Registry.BIOME.getRawId(Biomes.DEEP_OCEAN);
   private static final int DEEP_COLD_OCEAN_ID = Registry.BIOME.getRawId(Biomes.DEEP_COLD_OCEAN);
   private static final int DEEP_FROZEN_OCEAN_ID = Registry.BIOME.getRawId(Biomes.DEEP_FROZEN_OCEAN);

   protected static boolean isOcean(int id) {
      return id == WARM_OCEAN_ID || id == LUKEWARM_OCEAN_ID || id == OCEAN_ID || id == COLD_OCEAN_ID || id == FROZEN_OCEAN_ID || id == DEEP_WARM_OCEAN_ID || id == DEEP_LUKEWARM_OCEAN_ID || id == DEEP_OCEAN_ID || id == DEEP_COLD_OCEAN_ID || id == DEEP_FROZEN_OCEAN_ID;
   }

}
