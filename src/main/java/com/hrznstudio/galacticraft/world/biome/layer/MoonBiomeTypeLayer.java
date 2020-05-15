package com.hrznstudio.galacticraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.IdentitySamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum MoonBiomeTypeLayer implements IdentitySamplingLayer {
   INSTANCE;


   @Override
   public int sample(LayerRandomnessSource context, int value) {
      int i = context.nextInt(4);

      if (value == MoonBiomeLayers.MOON_MARE_PLAINS_ID) {
         if (i == 2) {
            return MoonBiomeLayers.MOON_MARE_ROCKS_ID;
         }
      }
      if (value == MoonBiomeLayers.MOON_HIGHLANDS_PLAINS_ID) {
         if (i == 2) {
            return MoonBiomeLayers.MOON_HIGHLANDS_ROCKS_ID;
         }
      }
      return value;
   }
}
