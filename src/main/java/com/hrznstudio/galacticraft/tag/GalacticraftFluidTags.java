package com.hrznstudio.galacticraft.tag;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.fluids.GalacticraftFluids;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class GalacticraftFluidTags {
    public static final Tag<Fluid> OIL = Tag.Builder.<Fluid>create().add(GalacticraftFluids.STILL_CRUDE_OIL, GalacticraftFluids.FLOWING_CRUDE_OIL).build(new Identifier(Constants.MOD_ID, "oil"));
    public static final Tag<Fluid> FUEL = Tag.Builder.<Fluid>create().add(GalacticraftFluids.STILL_FUEL, GalacticraftFluids.FLOWING_FUEL).build(new Identifier(Constants.MOD_ID, "fuel"));

    public static void register() {
    }
}
