package com.hrznstudio.galacticraft.blocks.machines.refinery;

import net.minecraft.ChatFormat;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;

public enum RefineryStatus {

    /**
     * Generator is active and is generating energy.
     */
    REFINING(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.refining").setStyle(new Style().setColor(ChatFormat.GREEN)).getFormattedText()),
    /**
     * Generator has fuel but buffer is full.
     */
    IDLE(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.idle").setStyle(new Style().setColor(ChatFormat.GOLD)).getFormattedText()),
    /**
     * The generator has no fuel.
     */
    INACTIVE(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.inactive").setStyle(new Style().setColor(ChatFormat.GRAY)).getFormattedText());

    private String name;

    RefineryStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

