package com.hrznstudio.galacticraft.world.biome.layer;

import com.hrznstudio.galacticraft.world.biome.moon.mare.MoonMareBiome;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum MoonValleyLayer implements CrossSamplingLayer {
   INSTANCE;

   @Override
   public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
      if (n != s && e != w) {
         int mare = 0;
         if (Registry.BIOME.get(n) instanceof MoonMareBiome) {
            mare++;
         }
         if (Registry.BIOME.get(s) instanceof MoonMareBiome) {
            mare++;
         }
         if (Registry.BIOME.get(e) instanceof MoonMareBiome) {
            mare++;
         }
         if (Registry.BIOME.get(w) instanceof MoonMareBiome) {
            mare++;
         }
         if (mare == 2) return context.nextInt(1) == 0 ? MoonBiomeLayers.MOON_MARE_VALLEY_ID : MoonBiomeLayers.MOON_HIGHLANDS_VALLEY_ID;
         return mare < 2 ? MoonBiomeLayers.MOON_HIGHLANDS_VALLEY_ID : MoonBiomeLayers.MOON_MARE_VALLEY_ID;
      }

      if (n == MoonBiomeLayers.MOON_HIGHLANDS_VALLEY_ID || n == MoonBiomeLayers.MOON_MARE_VALLEY_ID && context.nextInt(2) == 0) {
         return n;
      }

      if (s == MoonBiomeLayers.MOON_HIGHLANDS_VALLEY_ID || s == MoonBiomeLayers.MOON_MARE_VALLEY_ID && context.nextInt(2) == 0) {
         return s;
      }

      if (e == MoonBiomeLayers.MOON_HIGHLANDS_VALLEY_ID || e == MoonBiomeLayers.MOON_MARE_VALLEY_ID && context.nextInt(2) == 0) {
         return e;
      }

      if (w == MoonBiomeLayers.MOON_HIGHLANDS_VALLEY_ID || w == MoonBiomeLayers.MOON_MARE_VALLEY_ID && context.nextInt(2) == 0) {
         return w;
      }

      switch (context.nextInt(3)) {
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
