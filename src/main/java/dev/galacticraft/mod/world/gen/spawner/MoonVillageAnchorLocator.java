package dev.galacticraft.mod.world.gen.spawner;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

final class MoonVillageAnchorLocator {
    private MoonVillageAnchorLocator() {
    }

    static VillageAnchor findVillageAnchor(ServerLevel world, LevelChunk chunk, Structure villageStructure) {
        StructureStart structureStart = world.structureManager().startsForStructure(chunk.getPos(), structure -> structure == villageStructure).stream()
                .filter(StructureStart::isValid)
                .findFirst()
                .orElse(StructureStart.INVALID_START);
        if (!structureStart.isValid()) {
            return null;
        }

        ChunkPos startChunk = structureStart.getChunkPos();
        BlockPos center = structureStart.getBoundingBox().getCenter();
        BlockPos surfacePos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(center.getX(), world.getMinBuildHeight(), center.getZ()));
        return new VillageAnchor(ChunkPos.asLong(startChunk.x, startChunk.z), surfacePos);
    }

    record VillageAnchor(long key, BlockPos position) {
    }
}