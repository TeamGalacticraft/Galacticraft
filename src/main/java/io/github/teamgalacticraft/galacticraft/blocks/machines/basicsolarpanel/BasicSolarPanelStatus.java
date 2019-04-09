package io.github.teamgalacticraft.galacticraft.blocks.machines.basicsolarpanel;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public enum BasicSolarPanelStatus {
    /**
     * Solar panel is active and is generating energy.
     */
    COLLECTING("\u00A7a" + "Collecting" + "\u00A7r"),
    /**
     * Solar Panel is generating energy, but the buffer is full.
     */
    FULL("\u00A77" + "Full" + "\u00A7r"),
    /**
     * Solar Panel is generating energy, but less efficiently.
     */
    RAIN("\u00A7e" + "Raining" + "\u00A7r"),
    /**
     * Solar Panel is not generating energy.
     */
    NIGHT("\u00A70" + "Night" + "\u00A7r"),
    /**
     * The sun is not visible.
     */
    BLOCKED("\u00A70" + "Blocked" + "\u00A7r");

    private String name;

    BasicSolarPanelStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
