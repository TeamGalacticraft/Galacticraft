package dev.galacticraft.mod.world.gen.dungeon;

import net.minecraft.core.Vec3i;

import java.util.List;

public record ScannedTemplate(
        Vec3i size,                // template size from NBT
        List<Port> entrances,      // local-space ports from entrance markers
        List<Port> exits           // local-space ports from exit markers
) {
}