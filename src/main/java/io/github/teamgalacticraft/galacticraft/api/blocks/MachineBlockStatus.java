package io.github.teamgalacticraft.galacticraft.api.blocks;

import net.minecraft.util.StringRepresentable;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public enum MachineBlockStatus implements StringRepresentable {
    /**
     * The machine block has power but isn't running
     */
    INACTIVE("inactive"),

    /**
     * The machine block has power and is running.
     */
    ACTIVE("active"),

    /**
     *  The machine block has no power.
     */
    UNPOWERED("unpowered"),
    ;

    String name;
    MachineBlockStatus(String name) {
        this.name = name;
    }
    @Override
    public String asString() {
        return name;
    }}
