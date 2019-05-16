package com.hrznstudio.galacticraft.blocks.machines.basicsolarpanel;

import net.minecraft.network.chat.Style;
import net.minecraft.ChatFormat;
import net.minecraft.network.chat.TranslatableComponent;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public enum BasicSolarPanelStatus {
    /**
     * Solar panel is active and is generating energy.
     */
    COLLECTING(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.collecting").setStyle(new Style().setColor(ChatFormat.GREEN)).getFormattedText()),
    /**
     * Solar Panel is generating energy, but the buffer is full.
     */
    FULL(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.full").setStyle(new Style().setColor(ChatFormat.GOLD)).getFormattedText()),
    /**
     * Solar Panel is generating energy, but less efficiently.
     */
    RAINING(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.raining").setStyle(new Style().setColor(ChatFormat.DARK_AQUA)).getFormattedText()),
    /**
     * Solar Panel is not generating energy.
     */
    NIGHT(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.night").setStyle(new Style().setColor(ChatFormat.BLUE)).getFormattedText()),
    /**
     * The sun is not visible.
     */
    BLOCKED(new TranslatableComponent("ui.galacticraft-rewoven.machinestatus.blocked").setStyle(new Style().setColor(ChatFormat.DARK_GRAY)).getFormattedText());

    private String name;

    BasicSolarPanelStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
