package io.github.teamgalacticraft.galacticraft.blocks.machines.circuitfabricator;

import net.minecraft.text.Style;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public enum CircuitFabricatorStatus {
    /**
     * Fabricator is active and is processing.
     */
    ACTIVE(new TranslatableTextComponent("ui.galacticraft-rewoven.machinestatus.active").setStyle(new Style().setColor(TextFormat.GREEN)).getFormattedText()),
    /**
     * Fabricator is not processing.
     */
    IDLE(new TranslatableTextComponent("ui.galacticraft-rewoven.machinestatus.idle").setStyle(new Style().setColor(TextFormat.GOLD)).getFormattedText()),
    /**
     * The fabricator has been switched off.
     */
    INACTIVE(new TranslatableTextComponent("ui.galacticraft-rewoven.machinestatus.inactive").setStyle(new Style().setColor(TextFormat.GRAY)).getFormattedText());


    private String name;

    CircuitFabricatorStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}