package io.github.teamgalacticraft.galacticraft.blocks.machines.circuitfabricator;

import net.minecraft.client.resource.language.I18n;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public enum CircuitFabricatorStatus {
    /**
     * Fabricator is active and is processing.
     */
    ACTIVE("\u00A7a" + I18n.translate("ui.galacticraft-rewoven.machinestatus.active") + "\u00A7r"),
    /**
     * Fabricator is not processing.
     */
    IDLE("\u00A77" + I18n.translate("ui.galacticraft-rewoven.machinestatus.idle") + "\u00A7r"),
    /**
     * The fabricator has been switched off.
     */
    INACTIVE("\u00A77" + I18n.translate("ui.galacticraft-rewoven.machinestatus.inactive") + "\u00A7r");


    private String name;

    CircuitFabricatorStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}