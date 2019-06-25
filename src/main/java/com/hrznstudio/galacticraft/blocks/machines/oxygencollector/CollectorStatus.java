package com.hrznstudio.galacticraft.blocks.machines.oxygencollector;

import net.minecraft.util.Formatting;

public enum CollectorStatus {
    INACTIVE(Formatting.RED.getColorValue()),
    NOT_ENOUGH_LEAVES(Formatting.RED.getColorValue()),
    COLLECTING(Formatting.GREEN.getColorValue());

    private int textColor;

    CollectorStatus(int textColor) {
        this.textColor = textColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public static CollectorStatus get(int index) {
        if (index < 0) return values()[0];
        return values()[index % values().length];
    }
}