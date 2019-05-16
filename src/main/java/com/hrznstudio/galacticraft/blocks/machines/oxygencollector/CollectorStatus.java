package com.hrznstudio.galacticraft.blocks.machines.oxygencollector;

import net.minecraft.ChatFormat;

public enum CollectorStatus {
    INACTIVE(ChatFormat.RED.getColor()), NOT_ENOUGH_LEAVES(ChatFormat.RED.getColor()), COLLECTING(ChatFormat.GREEN.getColor());

    private int textColor;

    CollectorStatus(int textColor) {
        this.textColor = textColor;
    }

    public int getTextColor() {
        return textColor;
    }
}