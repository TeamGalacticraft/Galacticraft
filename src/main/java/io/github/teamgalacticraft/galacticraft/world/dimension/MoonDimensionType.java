package io.github.teamgalacticraft.galacticraft.world.dimension;

import net.minecraft.world.dimension.DimensionType;

public class MoonDimensionType extends DimensionType {
   public MoonDimensionType() {
      super(30, "moon", "DIM30", MoonDimension::new, true);
   }

}
