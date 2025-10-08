package dev.galacticraft.mod.world.gen.dungeon;

import net.minecraft.core.BlockPos;

public record Port(BlockPos localPos, net.minecraft.core.Direction facing, int aperture) {
    public BlockPos worldPos() {
        return localPos;
    } // after rotatePortsToWorld we treat it as world

    public boolean axisMatches(Port other) {
        return this.facing.getAxis().isHorizontal() && other.facing.getAxis() == this.facing.getAxis();
    }

    public boolean apertureMatches(Port other) {
        return this.aperture == other.aperture;
    }
}