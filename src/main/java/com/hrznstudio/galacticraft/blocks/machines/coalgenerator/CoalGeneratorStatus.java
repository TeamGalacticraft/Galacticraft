package com.hrznstudio.galacticraft.blocks.machines.coalgenerator;

import net.minecraft.ChatFormat;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public enum CoalGeneratorStatus {
    /**
     * Generator is active and is generating energy.
     */
    ACTIVE(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.active").setStyle(new Style().setColor(ChatFormat.GREEN)).getFormattedText()),
    /**
     * Generator has fuel but buffer is full.
     */
    IDLE(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.idle").setStyle(new Style().setColor(ChatFormat.GOLD)).getFormattedText()),
    /**
     * The generator has no fuel.
     */
    INACTIVE(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.inactive").setStyle(new Style().setColor(ChatFormat.GRAY)).getFormattedText()),
    /**
     * The generator is warming up.
     */
    WARMING(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.warming").setStyle(new Style().setColor(ChatFormat.GREEN)).getFormattedText());

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
