package com.hrznstudio.galacticraft.blocks.machines.coalgenerator;

import net.minecraft.text.Style;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public enum CoalGeneratorStatus {
    /**
     * Generator is active and is generating energy.
     */
    ACTIVE(new TranslatableTextComponent("ui.galacticraft-rewoven.machinestatus.active").setStyle(new Style().setColor(TextFormat.GREEN)).getFormattedText()),
    /**
     * Generator has fuel but buffer is full.
     */
    IDLE(new TranslatableTextComponent("ui.galacticraft-rewoven.machinestatus.idle").setStyle(new Style().setColor(TextFormat.GOLD)).getFormattedText()),
    /**
     * The generator has no fuel.
     */
    INACTIVE(new TranslatableTextComponent("ui.galacticraft-rewoven.machinestatus.inactive").setStyle(new Style().setColor(TextFormat.GRAY)).getFormattedText()),
    /**
     * The generator is warming up.
     */
    WARMING(new TranslatableTextComponent("ui.galacticraft-rewoven.machinestatus.warming").setStyle(new Style().setColor(TextFormat.GREEN)).getFormattedText());

    private String name;

    CoalGeneratorStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
