package com.hrznstudio.galacticraft.blocks.machines.basicsolarpanel;

import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public enum BasicSolarPanelStatus {
    /**
     * Solar panel is active and is generating energy.
     */
    COLLECTING(new TranslatableText("ui.galacticraft-rewoven.machinestatus.collecting").setStyle(new Style().setColor(Formatting.GREEN)).asFormattedString()),
    /**
     * Solar Panel is generating energy, but the buffer is full.
     */
    FULL(new TranslatableText("ui.galacticraft-rewoven.machinestatus.full").setStyle(new Style().setColor(Formatting.GOLD)).asFormattedString()),
    /**
     * Solar Panel is generating energy, but less efficiently.
     */
    RAINING(new TranslatableText("ui.galacticraft-rewoven.machinestatus.raining").setStyle(new Style().setColor(Formatting.DARK_AQUA)).asFormattedString()),
    /**
     * Solar Panel is not generating energy.
     */
    NIGHT(new TranslatableText("ui.galacticraft-rewoven.machinestatus.night").setStyle(new Style().setColor(Formatting.BLUE)).asFormattedString()),
    /**
     * The sun is not visible.
     */
    BLOCKED(new TranslatableText("ui.galacticraft-rewoven.machinestatus.blocked").setStyle(new Style().setColor(Formatting.DARK_GRAY)).asFormattedString());
    private String name;

    BasicSolarPanelStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static BasicSolarPanelStatus get(int index) {
        if (index < 0) index=0;
        return values()[index % values().length];
    }
}
