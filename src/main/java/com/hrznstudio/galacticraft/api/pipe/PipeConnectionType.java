package com.hrznstudio.galacticraft.api.pipe;

import net.minecraft.util.StringIdentifiable;

public enum PipeConnectionType implements StringIdentifiable {
    NONE,
    PIPE,
    FLUID_INPUT,
    FLUID_OUTPUT,
    FLUID_IO;

    @Override
    public String asString() {
        return name().toLowerCase();
    }
}
