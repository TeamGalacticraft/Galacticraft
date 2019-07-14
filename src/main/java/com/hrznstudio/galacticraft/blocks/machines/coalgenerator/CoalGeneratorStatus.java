package com.hrznstudio.galacticraft.blocks.machines.coalgenerator;

import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public enum CoalGeneratorStatus {
    /**
     * Generator is active and is generating energy.
     */
    ACTIVE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.active").setStyle(new Style().setColor(Formatting.GREEN)).asFormattedString()),
    /**
     * Generator has fuel but buffer is full.
     */
    IDLE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.idle").setStyle(new Style().setColor(Formatting.GOLD)).asFormattedString()),
    /**
     * The generator has no fuel.
     */
    INACTIVE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.inactive").setStyle(new Style().setColor(Formatting.GRAY)).asFormattedString()),
    /**
     * The generator is warming up.
     */
    WARMING(new TranslatableText("ui.galacticraft-rewoven.machinestatus.warming").setStyle(new Style().setColor(Formatting.GREEN)).asFormattedString());

    private String name;

    CoalGeneratorStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static CoalGeneratorStatus get(int index) {
        if (index < 0) return ACTIVE;
        return values()[index % values().length];
    }
}
