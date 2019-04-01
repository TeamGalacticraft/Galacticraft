package io.github.teamgalacticraft.galacticraft.blocks.machines.coalgenerator;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public enum CoalGeneratorStatus {
    /**
     * Generator is active and is generating energy.
     */
    ACTIVE("\u00A7a" + "Active" + "\u00A7r"),
    /**
     * Generator has fuel but buffer is full.
     */
    IDLE("\u00A77" + "Idle" + "\u00A7r"),
    /**
     * The generator has no fuel.
     */
    INACTIVE("\u00A77" + "Inactive" + "\u00A7r"),
    /**
     * The generator is warming up.
     */
    WARMING("\u00A7e" + "Warming" + "\u00A7r");

    private String name;

    CoalGeneratorStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
