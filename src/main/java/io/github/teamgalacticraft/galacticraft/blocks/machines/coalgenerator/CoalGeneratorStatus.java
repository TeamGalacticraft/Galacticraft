package io.github.teamgalacticraft.galacticraft.blocks.machines.coalgenerator;

import net.minecraft.client.resource.language.I18n;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public enum CoalGeneratorStatus {
    /**
     * Generator is active and is generating energy.
     */
    ACTIVE("\u00A7a" + I18n.translate("ui.galacticraft-rewoven.machinestatus.active") + "\u00A7r"),
    /**
     * Generator has fuel but buffer is full.
     */
    IDLE("\u00A77" + I18n.translate("ui.galacticraft-rewoven.machinestatus.idle") + "\u00A7r"),
    /**
     * The generator has no fuel.
     */
    INACTIVE("\u00A77" + I18n.translate("ui.galacticraft-rewoven.machinestatus.inactive") + "\u00A7r"),
    /**
     * The generator is warming up.
     */
    WARMING("\u00A7e" + I18n.translate("ui.galacticraft-rewoven.machinestatus.warming") + "\u00A7r");

    private String name;

    CoalGeneratorStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
