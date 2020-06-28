package com.hrznstudio.galacticraft.api.rocket.part;

import net.minecraft.util.StringIdentifiable;

public enum RocketPartType implements StringIdentifiable {
    CONE,
    BODY,
    FIN,
    BOOSTER,
    BOTTOM,
    UPGRADE;


    @Override
    public String asString() {
        return this.toString().toLowerCase();
    }
}