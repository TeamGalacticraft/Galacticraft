package dev.galacticraft.mod.world.gen.dungeon.records;

import net.minecraft.core.BlockPos;

public record DungeonInput(BlockPos surface, int minBuildHeight, int criticalPathRooms,
                           java.util.List<Integer> criticalPaths, int targetRooms, int maxBuildHeight) {
}
