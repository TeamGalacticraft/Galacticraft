package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic Moon cave planner.
 *
 * <p>This class deliberately does not query biomes. Biome lookup during planning can
 * force generation of unloaded chunks and cause worldgen dependency chains. The planner
 * only creates deterministic cave plans from cell coordinates and the world seed.</p>
 *
 * <p>Actual cave wall material/style is resolved later by {@link MoonCaveStyleResolver}
 * during chunk-local carving.</p>
 */
public final class MoonCavePlanner {
    public static final MoonCavePlanner INSTANCE = new MoonCavePlanner();

    public static final int CELL_SIZE_CHUNKS = 8;
    public static final int CELL_SIZE_BLOCKS = CELL_SIZE_CHUNKS * 16;

    private static final int CELL_SEARCH_RADIUS = 1;
    private static final int MIN_Y = -46;
    private static final int MAX_Y = 34;

    private MoonCavePlanner() {
    }

    /**
     * Returns all deterministic cave plans that may intersect the supplied chunk.
     *
     * @param randomState random state for deterministic cell-local random creation.
     * @param chunk current chunk position.
     * @return cave plans intersecting this chunk.
     */
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

        BlockPos anchor = randomAnchor(cell, random);

        MoonCaveStyle style = MoonCaveStyle.fromBiome(biomeSource.getNoiseBiome(
                QuartPos.fromBlock(anchor.getX()),
                QuartPos.fromBlock(anchor.getY()),
                QuartPos.fromBlock(anchor.getZ()),
                randomState.sampler()
        ));

        if (style == null) {
            return null;
        }

        MoonCaveDefinition definition = MoonCaveRegistry.pick(style, random);

        if (definition == null) {
            return null;
        }

        anchor = randomAnchor(cell, definition, random);

        MoonCaveContext context = new MoonCaveContext(
                cell,
                anchor,
                style,
                definition,
                random,
                definition.minY(),
                definition.maxY()
        );

        return definition.shape().createPlan(context);
    }

    private static BlockPos randomAnchor(MoonCaveCellPos cell, MoonCaveDefinition definition, RandomSource random) {
        int margin = Math.min(40, Math.max(8, CELL_SIZE_BLOCKS / 4));
        int usable = Math.max(1, CELL_SIZE_BLOCKS - margin * 2);

        int x = cell.minBlockX() + margin + random.nextInt(usable);
        int z = cell.minBlockZ() + margin + random.nextInt(usable);
        int y = definition.minAnchorY() + random.nextInt(definition.maxAnchorY() - definition.minAnchorY() + 1);

        return new BlockPos(x, y, z);
    }

    /**
     * Picks a deterministic fallback style for cave shape selection.
     *
     * <p>Final cave block materials are resolved from actual generated biomes during
     * carving. This value only controls which registered cave definition is used.</p>
     */
    private static MoonCaveStyle pickFallbackStyle(RandomSource random) {
        MoonCaveStyle[] styles = MoonCaveStyle.values();
        return styles[random.nextInt(styles.length)];
    }

    private static BlockPos randomAnchor(MoonCaveCellPos cell, RandomSource random) {
        int margin = Math.min(40, Math.max(8, CELL_SIZE_BLOCKS / 4));
        int usable = Math.max(1, CELL_SIZE_BLOCKS - margin * 2);

        int x = cell.minBlockX() + margin + random.nextInt(usable);
        int z = cell.minBlockZ() + margin + random.nextInt(usable);
        int y = MIN_Y + random.nextInt(MAX_Y - MIN_Y + 1);

        return new BlockPos(x, y, z);
    }
}