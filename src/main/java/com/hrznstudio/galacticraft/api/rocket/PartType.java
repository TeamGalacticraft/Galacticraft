package com.hrznstudio.galacticraft.api.rocket;

import net.minecraft.util.StringIdentifiable;

public enum PartType implements StringIdentifiable {
    CONE,
    BODY,
    FINS,
    BOOSTER,
    BOTTOM;


    @Override
    public String asString() {
        return this.toString().toLowerCase();
    }
}