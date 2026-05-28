package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.world.level.ChunkPos;

public record MoonCaveRegionPos(int x, int z) {
    public static final int REGION_SIZE_CHUNKS = 10;

    public static MoonCaveRegionPos fromChunk(ChunkPos chunk) {
        return new MoonCaveRegionPos(
                Math.floorDiv(chunk.x, REGION_SIZE_CHUNKS),
                Math.floorDiv(chunk.z, REGION_SIZE_CHUNKS)
        );
    }

    public int minBlockX() {
        return this.x * REGION_SIZE_CHUNKS * 16;
    }

    public int minBlockZ() {
        return this.z * REGION_SIZE_CHUNKS * 16;
    }

    public int maxBlockX() {
        return this.minBlockX() + REGION_SIZE_CHUNKS * 16 - 1;
    }

    public int maxBlockZ() {
        return this.minBlockZ() + REGION_SIZE_CHUNKS * 16 - 1;
    }

    public int centerBlockX() {
        return this.minBlockX() + REGION_SIZE_CHUNKS * 8;
    }

    public int centerBlockZ() {
        return this.minBlockZ() + REGION_SIZE_CHUNKS * 8;
    }
}