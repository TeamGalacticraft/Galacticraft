package com.hrznstudio.galacticraft.blocks.machines.circuitfabricator;

import net.minecraft.network.chat.Style;
import net.minecraft.ChatFormat;
import net.minecraft.network.chat.TranslatableComponent;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public enum CircuitFabricatorStatus {
    /**
     * Fabricator is active and is processing.
     */
    PROCESSING(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.processing").setStyle(new Style().setColor(ChatFormat.GREEN)).getFormattedText()),
    /**
     * Fabricator is not processing.
     */
    IDLE(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.idle").setStyle(new Style().setColor(ChatFormat.GOLD)).getFormattedText()),
    /**
     * The fabricator has been switched off.
     */
    INACTIVE(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.inactive").setStyle(new Style().setColor(ChatFormat.GRAY)).getFormattedText());


    private String name;

    CircuitFabricatorStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}