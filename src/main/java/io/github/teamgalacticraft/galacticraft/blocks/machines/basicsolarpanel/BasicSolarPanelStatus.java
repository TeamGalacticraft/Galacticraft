package io.github.teamgalacticraft.galacticraft.blocks.machines.basicsolarpanel;

import net.minecraft.text.Style;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public enum BasicSolarPanelStatus {
    /**
     * Solar panel is active and is generating energy.
     */
    COLLECTING(new TranslatableTextComponent("ui.galacticraft-rewoven.machinestatus.collecting").setStyle(new Style().setColor(TextFormat.GREEN)).getFormattedText()),
    /**
     * Solar Panel is generating energy, but the buffer is full.
     */
    FULL(new TranslatableTextComponent("ui.galacticraft-rewoven.machinestatus.full").setStyle(new Style().setColor(TextFormat.GOLD)).getFormattedText()),
    /**
     * Solar Panel is generating energy, but less efficiently.
     */
    RAINING(new TranslatableTextComponent("ui.galacticraft-rewoven.machinestatus.raining").setStyle(new Style().setColor(TextFormat.DARK_AQUA)).getFormattedText()),
    /**
     * Solar Panel is not generating energy.
     */
    NIGHT(new TranslatableTextComponent("ui.galacticraft-rewoven.machinestatus.night").setStyle(new Style().setColor(TextFormat.BLUE)).getFormattedText()),
    /**
     * The sun is not visible.
     */
    BLOCKED(new TranslatableTextComponent("ui.galacticraft-rewoven.machinestatus.blocked").setStyle(new Style().setColor(TextFormat.DARK_GRAY)).getFormattedText());

    private String name;

    BasicSolarPanelStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
