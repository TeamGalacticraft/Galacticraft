package com.hrznstudio.galacticraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.IdentitySamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum MoonBiomeCraterLayer implements IdentitySamplingLayer {
   INSTANCE;


   @Override
   public int sample(LayerRandomnessSource context, int value) {
      if (context.nextInt(6) == 2) {
         if (value == MoonBiomeLayers.MOON_MARE_PLAINS_ID) {
            return MoonBiomeLayers.MOON_MARE_CRATERS_ID;
         }
         if (value == MoonBiomeLayers.MOON_HIGHLANDS_PLAINS_ID) {
            return MoonBiomeLayers.MOON_HIGHLANDS_CRATERS_ID;
         }
      }
      return value;
   }
}
