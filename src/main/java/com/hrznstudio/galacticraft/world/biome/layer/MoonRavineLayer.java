package com.hrznstudio.galacticraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum MoonRavineLayer implements CrossSamplingLayer {
   INSTANCE;

   @Override
   public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
      int i = context.nextInt(3);
      if (n != s && e != w) {
         return MoonBiomeLayers.MOON_VALLEY_ID;
      }

      if (n == MoonBiomeLayers.MOON_VALLEY_ID && context.nextInt(1) == 0) {
         return n;
      }
      if (s == MoonBiomeLayers.MOON_VALLEY_ID && context.nextInt(1) == 0) {
         return s;
      }
      if (e == MoonBiomeLayers.MOON_VALLEY_ID && context.nextInt(1) == 0) {
         return e;
      }
      if (w == MoonBiomeLayers.MOON_VALLEY_ID && context.nextInt(1) == 0) {
         return w;
      }

      switch (i) {
         case 0:
            return n;
         case 1:
            return e;
         case 2:
            return s;
         case 3:
            return w;
      }
      return center;
   }
}
