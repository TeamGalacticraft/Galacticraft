package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.ArrayList;
import java.util.List;

public final class MoonCavePlanner {
    public static final MoonCavePlanner INSTANCE = new MoonCavePlanner();

    public static final int CELL_SIZE_CHUNKS = 8;
    public static final int CELL_SIZE_BLOCKS = CELL_SIZE_CHUNKS * 16;

    private static final int CELL_SEARCH_RADIUS = 1;

    private MoonCavePlanner() {
    }

    public List<MoonCavePlan> plansForChunk(RandomState randomState, ChunkPos chunk, BiomeSource biomeSource) {
        MoonCaveCellPos center = MoonCaveCellPos.fromChunk(chunk);
        List<MoonCavePlan> result = new ArrayList<>();

        for (int dx = -CELL_SEARCH_RADIUS; dx <= CELL_SEARCH_RADIUS; dx++) {
            for (int dz = -CELL_SEARCH_RADIUS; dz <= CELL_SEARCH_RADIUS; dz++) {
                MoonCaveCellPos cell = new MoonCaveCellPos(center.x() + dx, center.z() + dz);
                MoonCavePlan plan = this.rawPlan(randomState, cell, biomeSource);

                if (plan != null && plan.bounds().intersectsChunk(chunk)) {
                    result.add(plan);
                }
            }
        }

        return result;
    }

    private MoonCavePlan rawPlan(RandomState randomState, MoonCaveCellPos cell, BiomeSource biomeSource) {
        RandomSource random = randomState.aquiferRandom().at(
                cell.centerBlockX() + 91821,
                -7137,
                cell.centerBlockZ() - 44291
        );

        for (int attempt = 0; attempt < 8; attempt++) {
            BlockPos probe = randomProbeAnchor(cell, random);
            Holder<Biome> biome = biomeSource.getNoiseBiome(
                    QuartPos.fromBlock(probe.getX()),
                    QuartPos.fromBlock(probe.getY()),
                    QuartPos.fromBlock(probe.getZ()),
                    randomState.sampler()
            );

            PlanetCave cave = MoonCaveRegistry.pickForBiome(biome, random);

            if (cave == null) {
                continue;
            }

            BlockPos anchor = find3DBiomeAnchor(cell, cave, biomeSource, randomState, random);

            if (anchor == null) {
                continue;
            }

            Holder<Biome> anchorBiome = biomeSource.getNoiseBiome(
                    QuartPos.fromBlock(anchor.getX()),
                    QuartPos.fromBlock(anchor.getY()),
                    QuartPos.fromBlock(anchor.getZ()),
                    randomState.sampler()
            );

            if (!cave.matchesBiome(anchorBiome)) {
                continue;
            }

            MoonCaveContext context = new MoonCaveContext(
                    cell,
                    anchor,
                    cave,
                    random,
                    cave.minY(),
                    cave.maxY()
            );

            return cave.shape().createPlan(context);
        }

        return null;
    }

    private static BlockPos randomProbeAnchor(MoonCaveCellPos cell, RandomSource random) {
        int margin = Math.min(40, Math.max(8, CELL_SIZE_BLOCKS / 4));
        int usable = Math.max(1, CELL_SIZE_BLOCKS - margin * 2);

        int x = cell.minBlockX() + margin + random.nextInt(usable);
        int z = cell.minBlockZ() + margin + random.nextInt(usable);
        int y = -16 + random.nextInt(80);

        return new BlockPos(x, y, z);
    }

    private static BlockPos find3DBiomeAnchor(
            MoonCaveCellPos cell,
            PlanetCave cave,
            BiomeSource biomeSource,
            RandomState randomState,
            RandomSource random
    ) {
        int margin = Math.min(40, Math.max(8, CELL_SIZE_BLOCKS / 4));
        int usable = Math.max(1, CELL_SIZE_BLOCKS - margin * 2);

        for (int attempt = 0; attempt < 32; attempt++) {
            int x = cell.minBlockX() + margin + random.nextInt(usable);
            int z = cell.minBlockZ() + margin + random.nextInt(usable);

            int yRange = cave.maxBiomeSearchY() - cave.minBiomeSearchY() + 1;
            int y = cave.minBiomeSearchY() + random.nextInt(yRange);

            Holder<Biome> biome = biomeSource.getNoiseBiome(
                    QuartPos.fromBlock(x),
                    QuartPos.fromBlock(y),
                    QuartPos.fromBlock(z),
                    randomState.sampler()
            );

            if (cave.matchesBiome(biome)) {
                return new BlockPos(x, y, z);
            }
        }

        return null;
    }
}