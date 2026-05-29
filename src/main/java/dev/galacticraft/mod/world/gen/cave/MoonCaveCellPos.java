package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.world.level.ChunkPos;

/**
 * Region coordinate used for deterministic cave planning.
 */
public record MoonCaveCellPos(int x, int z) {
    public static int sizeChunks() {
        return MoonCavePlanner.CELL_SIZE_CHUNKS;
    }

    public static int sizeBlocks() {
        return sizeChunks() * 16;
    }

    /**
     * Converts a chunk position into the cave planning cell containing it.
     *
     * @param chunk Chunk position.
     * @return Cave cell position.
     */
    public static MoonCaveCellPos fromChunk(ChunkPos chunk) {
        return new MoonCaveCellPos(
                Math.floorDiv(chunk.x, MoonCaveCellPos.sizeChunks()),
                Math.floorDiv(chunk.z, MoonCaveCellPos.sizeChunks())
        );
    }

    public int minBlockX() {
        return this.x * MoonCaveCellPos.sizeBlocks();
    }

    public int minBlockZ() {
        return this.z * MoonCaveCellPos.sizeBlocks();
    }

    public int maxBlockX() {
        return this.minBlockX() + MoonCaveCellPos.sizeBlocks() - 1;
    }

    public int maxBlockZ() {
        return this.minBlockZ() + MoonCaveCellPos.sizeBlocks() - 1;
    }

    public int centerBlockX() {
        return this.minBlockX() + MoonCaveCellPos.sizeBlocks() / 2;
    }

    public int centerBlockZ() {
        return this.minBlockZ() + MoonCaveCellPos.sizeBlocks() / 2;
    }
}