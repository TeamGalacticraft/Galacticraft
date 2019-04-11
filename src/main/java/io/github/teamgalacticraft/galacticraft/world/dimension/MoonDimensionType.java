package io.github.teamgalacticraft.galacticraft.world.dimension;

import net.minecraft.world.dimension.DimensionType;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class MoonDimensionType extends DimensionType {
    public MoonDimensionType() {
        super(30, "moon", "DIM30", MoonDimension::new, true);
    }

}
