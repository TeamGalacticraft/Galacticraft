package io.github.teamgalacticraft.galacticraft.api.blocks;

import net.minecraft.util.StringRepresentable;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public enum MachineBlockStatus implements StringRepresentable {
    /**
     * The machine block has been turned off, making it use even less power
     * TODO: Make machines that are "off" have to warm up or start up again.
     */
    OFF("off"),
    /**
     * The machine block has power but isn't running
     */
    IDLE("idle"),

    /**
     * The machine block has power and is running.
     */
    ACTIVE("active"),

    /**
     * The machine block has no power.
     */
    UNPOWERED("unpowered"),

    /**
     * Something has gone wrong in the code
     */
    ERROR("error");

    String name;

    MachineBlockStatus(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return name;
    }
}
