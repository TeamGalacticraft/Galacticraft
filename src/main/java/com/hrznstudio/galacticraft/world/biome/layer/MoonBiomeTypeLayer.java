package com.hrznstudio.galacticraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.IdentitySamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum MoonBiomeTypeLayer implements IdentitySamplingLayer {
   INSTANCE;


   @Override
   public int sample(LayerRandomnessSource context, int value) {
      if (context.nextInt(6) == 2) {
         if (value == MoonBiomeLayers.MOON_MARE_PLAINS_ID) {
            return MoonBiomeLayers.MOON_MARE_ROCKS_ID;
         }
         if (value == MoonBiomeLayers.MOON_HIGHLANDS_PLAINS_ID) {
            return MoonBiomeLayers.MOON_HIGHLANDS_ROCKS_ID;
         }
      }
      return value;
   }
}
