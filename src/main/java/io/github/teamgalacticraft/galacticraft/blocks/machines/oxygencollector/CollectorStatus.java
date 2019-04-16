package io.github.teamgalacticraft.galacticraft.blocks.machines.oxygencollector;

import net.minecraft.text.TextFormat;

public enum CollectorStatus {
    INACTIVE(TextFormat.RED.getColor()), NOT_ENOUGH_LEAVES(TextFormat.RED.getColor()), COLLECTING(TextFormat.GREEN.getColor());

    private int textColor;

    CollectorStatus(int textColor) {
        this.textColor = textColor;
    }

    public int getTextColor() {
        return textColor;
    }
}