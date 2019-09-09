package com.hrznstudio.galacticraft.blocks.machines.refinery;

import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public enum RefineryStatus {

    /**
     * Refinery is active and is converting oil into fuel.
     */
    ACTIVE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.refining").setStyle(new Style().setColor(Formatting.GREEN)).asFormattedString()),
    /**
     * Refinery has oil but the fuel tank is full.
     */
    FULL(new TranslatableText("ui.galacticraft-rewoven.machinestatus.idle").setStyle(new Style().setColor(Formatting.GOLD)).asFormattedString()),
    /**
     * The refinery has no oil.
     */
    IDLE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.inactive").setStyle(new Style().setColor(Formatting.BLACK)).asFormattedString()),
    /**
     * The refinery has no energy.
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

