package com.hrznstudio.galacticraft.world.poi;

import com.google.common.collect.ImmutableSet;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.poi.PointOfInterestType;

public class GalacticraftPointOfInterestType {
    public static final PointOfInterestType LUNAR_CARTOGRAPHER = PointOfInterestType.setup(Registry.register(Registry.POINT_OF_INTEREST_TYPE, new Identifier(Constants.MOD_ID, "lunar_cartographer"), new PointOfInterestType(new Identifier(Constants.MOD_ID, "lunar_cartographer").toString(), ImmutableSet.copyOf(GalacticraftBlocks.LUNAR_CARTOGRAPHY_TABLE.getStateManager().getStates()),1, 1)));

    public static void register() {

    }
}
