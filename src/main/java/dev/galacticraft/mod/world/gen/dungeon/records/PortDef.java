package dev.galacticraft.mod.world.gen.dungeon.records;

import net.minecraft.core.Direction;

public record PortDef(
        String name,            // optional label, e.g. "north_exit_1"
        boolean entrance,       // true = entrance port
        boolean exit,           // true = exit port
        Direction facing        // Direction eg NORTH, WEST, ...
) {}