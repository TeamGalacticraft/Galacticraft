package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.world.level.ChunkPos;

public interface MoonCaveElement {
    MoonCaveBounds bounds();

    CaveZone zone(int x, int y, int z);

    default void forEachAffectedBlock(
            ChunkPos chunkPos,
            int minY,
            int maxY,
            CaveBlockConsumer consumer
    ) {
        MoonCaveBounds bounds = this.bounds();

        int minX = Math.max(chunkPos.getMinBlockX(), bounds.minX());
        int maxX = Math.min(chunkPos.getMaxBlockX(), bounds.maxX());
        int minZ = Math.max(chunkPos.getMinBlockZ(), bounds.minZ());
        int maxZ = Math.min(chunkPos.getMaxBlockZ(), bounds.maxZ());
        int lowY = Math.max(minY, bounds.minY());
        int highY = Math.min(maxY, bounds.maxY());

        if (minX > maxX || minZ > maxZ || lowY > highY) {
            return;
        }

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = lowY; y <= highY; y++) {
                    CaveZone zone = this.zone(x, y, z);

                    if (zone != CaveZone.NONE) {
                        consumer.accept(x, y, z, zone);
                    }
                }
            }
        }
    }
}