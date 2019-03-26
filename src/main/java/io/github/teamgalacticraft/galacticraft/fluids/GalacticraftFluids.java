package io.github.teamgalacticraft.galacticraft.fluids;

import io.github.teamgalacticraft.galacticraft.Constants;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftFluids {

    public static final BaseFluid FLOWING_CRUDE_OIL = new CrudeOilFluid.Flowing();
    public static final BaseFluid STILL_CRUDE_OIL = new CrudeOilFluid.Still();

    public static void register() {
        Registry.register(Registry.FLUID, new Identifier(Constants.MOD_ID, Constants.Fluids.CRUDE_OIL_FLUID_FLOWING), FLOWING_CRUDE_OIL);
        Registry.register(Registry.FLUID, new Identifier(Constants.MOD_ID, Constants.Fluids.CRUDE_OIL_FLUID_STILL), STILL_CRUDE_OIL);
    }
}
