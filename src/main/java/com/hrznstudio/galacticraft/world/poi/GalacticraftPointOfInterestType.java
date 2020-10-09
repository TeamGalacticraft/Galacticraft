package com.hrznstudio.galacticraft.world.poi;

import com.google.common.collect.ImmutableSet;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.poi.PointOfInterestType;

public class GalacticraftPointOfInterestType {
    public static final PointOfInterestType LUNAR_CARTOGRAPHER = PointOfInterestHelper.register(new Identifier(Constants.MOD_ID, "lunar_cartographer"), 1, 1, ImmutableSet.copyOf(GalacticraftBlocks.LUNAR_CARTOGRAPHY_TABLE.getStateManager().getStates()));

    public static void register() {
    }
}
