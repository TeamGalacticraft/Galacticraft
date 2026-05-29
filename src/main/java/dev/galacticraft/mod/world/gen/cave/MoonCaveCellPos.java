package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.world.level.ChunkPos;

public record MoonCaveCellPos(int x, int z) {
    public static MoonCaveCellPos fromChunk(ChunkPos chunk) {
        return new MoonCaveCellPos(
                Math.floorDiv(chunk.x, MoonCavePlanner.CELL_SIZE_CHUNKS),
                Math.floorDiv(chunk.z, MoonCavePlanner.CELL_SIZE_CHUNKS)
        );
    }

    public int minBlockX() {
        return this.x * MoonCavePlanner.CELL_SIZE_BLOCKS;
    }

    public int minBlockZ() {
        return this.z * MoonCavePlanner.CELL_SIZE_BLOCKS;
    }

    public int maxBlockX() {
        return this.minBlockX() + MoonCavePlanner.CELL_SIZE_BLOCKS - 1;
    }

    public int maxBlockZ() {
        return this.minBlockZ() + MoonCavePlanner.CELL_SIZE_BLOCKS - 1;
    }

    public int centerBlockX() {
        return this.minBlockX() + MoonCavePlanner.CELL_SIZE_BLOCKS / 2;
    }

    public int centerBlockZ() {
        return this.minBlockZ() + MoonCavePlanner.CELL_SIZE_BLOCKS / 2;
    }
}