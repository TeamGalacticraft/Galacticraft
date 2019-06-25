package com.hrznstudio.galacticraft.blocks.machines.circuitfabricator;

import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public enum CircuitFabricatorStatus {
    /**
     * Fabricator is active and is processing.
     */
    PROCESSING(new TranslatableText("ui.galacticraft-rewoven.machinestatus.processing").setStyle(new Style().setColor(Formatting.GREEN)).asFormattedString()),
    /**
     * Fabricator is not processing.
     */
    IDLE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.idle").setStyle(new Style().setColor(Formatting.GOLD)).asFormattedString()),
    /**
     * The fabricator has been switched off.
     */
    INACTIVE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.inactive").setStyle(new Style().setColor(Formatting.GRAY)).asFormattedString());


    private String name;

    CircuitFabricatorStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static CircuitFabricatorStatus get(int index) {
        switch (index) {
            case 0: return PROCESSING;
            case 1: return IDLE;
            default: return INACTIVE;
        }
    }
}