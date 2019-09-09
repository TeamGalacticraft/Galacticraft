package com.hrznstudio.galacticraft.blocks.machines.compressor;

import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public enum CompressorStatus {

    /**
     * Generator is active and is generating energy.
     */
    PROCESSING(new TranslatableText("ui.galacticraft-rewoven.machinestatus.active").setStyle(new Style().setColor(Formatting.GREEN)).asFormattedString()),
    /**
     * Generator has fuel but buffer is full.
     */
    IDLE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.idle").setStyle(new Style().setColor(Formatting.GOLD)).asFormattedString()),
    /**
     * The generator has no fuel.
     */
    INACTIVE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.inactive").setStyle(new Style().setColor(Formatting.GRAY)).asFormattedString());

    private String name;

    CompressorStatus(String name) {
        this.name = name;
    }

    public static CompressorStatus get(int index) {
        switch (index) {
            case 0:
                return PROCESSING;
            case 1:
                return IDLE;
            default:
                return INACTIVE;
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
