package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.block.state.BlockState;

public final class MoonCaveSurfacePainter {
    private MoonCaveSurfacePainter() {
    }

    public static void paintSurface(
            ChunkAccess chunk,
            RandomState randomState,
            BiomeSource biomeSource
    ) {
        ChunkPos chunkPos = chunk.getPos();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int localX = 0; localX < 16; localX++) {
            int x = chunkPos.getMinBlockX() + localX;

            for (int localZ = 0; localZ < 16; localZ++) {
                int z = chunkPos.getMinBlockZ() + localZ;
                int y = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, localX, localZ) - 1;

                if (y <= chunk.getMinBuildHeight()) {
                    continue;
                }

                Holder<Biome> biome = biomeSource.getNoiseBiome(
                        QuartPos.fromBlock(x),
                        QuartPos.fromBlock(y),
                        QuartPos.fromBlock(z),
                        randomState.sampler()
                );

                PlanetCave cave = MoonCaveRegistry.firstForBiome(biome);

                if (cave == null || !cave.paintsSurface()) {
                    continue;
                }

                pos.set(x, y, z);
                BlockState current = chunk.getBlockState(pos);
                BlockState replacement = cave.surfaceBlock(x, y, z, current);

                if (replacement != current) {
                    chunk.setBlockState(pos, replacement, false);
                }
            }
        }
    }
}