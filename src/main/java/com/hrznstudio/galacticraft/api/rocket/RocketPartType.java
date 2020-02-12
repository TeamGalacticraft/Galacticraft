package com.hrznstudio.galacticraft.api.rocket;

import net.minecraft.util.StringIdentifiable;

public enum RocketPartType implements StringIdentifiable {
    CONE,
    BODY,
    FIN,
    BOOSTER,
    BOTTOM;


    @Override
    public String asString() {
        return this.toString().toLowerCase();
    }
}