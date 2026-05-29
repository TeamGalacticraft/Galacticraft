package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class MoonCavePlanner {
    public static final MoonCavePlanner INSTANCE = new MoonCavePlanner();

    public static final int CELL_SIZE_CHUNKS = 8;
    public static final int CELL_SIZE_BLOCKS = CELL_SIZE_CHUNKS * 16;
    public static final float CAVE_CELL_CHANCE = 0.45F;

    private static final int CELL_SEARCH_RADIUS = 1;
    private static final int MIN_Y = -46;
    private static final int MAX_Y = 34;

    private MoonCavePlanner() {
    }

    public List<MoonCavePlan> plansForChunk(RandomState randomState, ChunkPos chunk, Function<BlockPos, Holder<Biome>> biomeLookup) {
        MoonCaveCellPos center = MoonCaveCellPos.fromChunk(chunk);
        List<MoonCavePlan> result = new ArrayList<>();

        for (int dx = -CELL_SEARCH_RADIUS; dx <= CELL_SEARCH_RADIUS; dx++) {
            for (int dz = -CELL_SEARCH_RADIUS; dz <= CELL_SEARCH_RADIUS; dz++) {
                MoonCaveCellPos cell = new MoonCaveCellPos(center.x() + dx, center.z() + dz);
                MoonCavePlan plan = this.acceptedPlan(randomState, cell, biomeLookup);

                if (plan != null && plan.bounds().intersectsChunk(chunk)) {
                    result.add(plan);
                }
            }
        }

        return result;
    }

    private MoonCavePlan acceptedPlan(RandomState randomState, MoonCaveCellPos cell, Function<BlockPos, Holder<Biome>> biomeLookup) {
        MoonCavePlan candidate = this.rawPlan(randomState, cell, biomeLookup);

        if (candidate == null) {
            return null;
        }

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }

                MoonCavePlan other = this.rawPlan(randomState, new MoonCaveCellPos(cell.x() + dx, cell.z() + dz), biomeLookup);

                if (other == null || !candidate.bounds().intersects(other.bounds())) {
                    continue;
                }

                if (other.priority() > candidate.priority()) {
                    return null;
                }

                if (other.primaryStyle() != candidate.primaryStyle()) {
                    candidate.mergeFrom(other);
                }
            }
        }

        return candidate;
    }

    private MoonCavePlan rawPlan(RandomState randomState, MoonCaveCellPos cell, Function<BlockPos, Holder<Biome>> biomeLookup) {
        RandomSource random = randomState.aquiferRandom().at(
                cell.centerBlockX() + 91821,
                -7137,
                cell.centerBlockZ() - 44291
        );

        if (random.nextFloat() > CAVE_CELL_CHANCE) {
            return null;
        }

        BlockPos anchor = randomAnchor(cell, random);
        MoonCaveStyle style = MoonCaveStyle.fromBiome(biomeLookup.apply(anchor));

        if (style == null) {
            return null;
        }

        MoonCaveDefinition definition = MoonCaveRegistry.pick(style, random);

        if (definition == null || random.nextFloat() > definition.chance()) {
            return null;
        }

        MoonCaveContext context = new MoonCaveContext(
                cell,
                anchor,
                style,
                definition,
                random,
                MIN_Y,
                MAX_Y
        );

        return definition.shape().createPlan(context);
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