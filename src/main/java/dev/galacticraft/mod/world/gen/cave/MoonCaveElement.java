package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.world.level.ChunkPos;

public interface MoonCaveElement {
    MoonCaveBounds bounds();

    void stamp(
            ChunkPos chunkPos,
            int minY,
            int maxY,
            CaveCarvingMask mask,
            MoonCavePlan owner
    );
}