package com.hrznstudio.galacticraft.api.rocket;

import net.minecraft.util.StringIdentifiable;

public enum RocketPartType implements StringIdentifiable {
    CONE,
    BODY,
    FIN,
    BOOSTER,
    BOTTOM,
    MISC_UPGRADE;


    @Override
    public String asString() {
        return this.toString().toLowerCase();
    }

    //temporary workaround
    public static RocketPartType[] values_noUpgrade() {
        return new RocketPartType[]{CONE, BODY, FIN, BOOSTER, BOTTOM};
    }
}