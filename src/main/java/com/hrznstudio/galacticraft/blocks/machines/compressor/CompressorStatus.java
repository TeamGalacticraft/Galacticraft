package com.hrznstudio.galacticraft.blocks.machines.compressor;

import net.minecraft.text.Style;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;

public enum CompressorStatus {

    /**
     * Generator is active and is generating energy.
     */
    PROCESSING(new TranslatableTextComponent("ui.galacticraft-rewoven.machinestatus.active").setStyle(new Style().setColor(TextFormat.GREEN)).getFormattedText()),
    /**
     * Generator has fuel but buffer is full.
     */
    IDLE(new TranslatableTextComponent("ui.galacticraft-rewoven.machinestatus.idle").setStyle(new Style().setColor(TextFormat.GOLD)).getFormattedText()),
    /**
     * The generator has no fuel.
     */
    INACTIVE(new TranslatableTextComponent("ui.galacticraft-rewoven.machinestatus.inactive").setStyle(new Style().setColor(TextFormat.GRAY)).getFormattedText());

    private String name;

    CompressorStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
