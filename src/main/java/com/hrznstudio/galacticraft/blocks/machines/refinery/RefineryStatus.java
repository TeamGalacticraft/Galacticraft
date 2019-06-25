package com.hrznstudio.galacticraft.blocks.machines.refinery;

import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public enum RefineryStatus {

    /**
     * Generator is active and is generating energy.
     */
    REFINING(new TranslatableText("ui.galacticraft-rewoven.machinestatus.refining").setStyle(new Style().setColor(Formatting.GREEN)).asFormattedString()),
    /**
     * Generator has fuel but buffer is full.
     */
    IDLE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.idle").setStyle(new Style().setColor(Formatting.GOLD)).asFormattedString()),
    /**
     * The generator has no fuel.
     */
    INACTIVE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.inactive").setStyle(new Style().setColor(Formatting.GRAY)).asFormattedString());

    private String name;

    RefineryStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

