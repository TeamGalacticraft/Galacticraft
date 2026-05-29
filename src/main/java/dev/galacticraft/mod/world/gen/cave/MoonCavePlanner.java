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

/**
 * Deterministically creates Moon cave plans from large cells.
 */
public final class MoonCavePlanner {
    public static final MoonCavePlanner INSTANCE = new MoonCavePlanner();

    private static final int MIN_Y = -46;
    private static final int MAX_Y = 34;
    private static final int CELL_SEARCH_RADIUS = 1;

    /**
     * Chance that a cave network will generate inside a cave cell.
     */
    public static final float CAVE_CHANCE = 0.35F;

    /**
     * Width/length of a cave cell in chunks.
     *
     * Larger values:
     * - fewer cave systems
     * - larger spacing
     *
     * Smaller values:
     * - more cave systems
     * - denser cave distribution
     */
    public static final int CELL_SIZE_CHUNKS = 8;

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

        if (random.nextFloat() > CAVE_CHANCE) {
            return null;
        }

        BlockPos anchor = randomAnchor(cell, random);
        MoonCaveStyle style = MoonCaveStyle.fromBiome(biomeLookup.apply(anchor));

        if (style == null) {
            return null;
        }

        MoonCavePlan plan = new MoonCavePlan(cell, random.nextDouble(), style);
        int roomCount = roomCount(style, random);
        BlockPos previous = null;
        BlockPos current = anchor;

        for (int i = 0; i < roomCount; i++) {
            if (i > 0) {
                current = offsetRoom(current, style, random);
            }

            addRoom(plan, current, style, random);

            if (previous != null) {
                addTunnel(plan, previous, current, style, random);
            }

            previous = current;
        }

        return plan;
    }

    private static BlockPos randomAnchor(MoonCaveCellPos cell, RandomSource random) {
        int margin = 40;
        int x = cell.minBlockX() + margin + random.nextInt(MoonCaveCellPos.sizeBlocks() - margin * 2);
        int z = cell.minBlockZ() + margin + random.nextInt(MoonCaveCellPos.sizeBlocks() - margin * 2);
        int y = MIN_Y + random.nextInt(MAX_Y - MIN_Y + 1);

        return new BlockPos(x, y, z);
    }

    private static int roomCount(MoonCaveStyle style, RandomSource random) {
        return switch (style) {
            case GLACIAL -> 2 + random.nextInt(2);
            case OLIVINE -> 3 + random.nextInt(3);
            case CHEESE -> 4 + random.nextInt(4);
        };
    }

    private static void addRoom(MoonCavePlan plan, BlockPos center, MoonCaveStyle style, RandomSource random) {
        double rx = switch (style) {
            case GLACIAL -> 14.0D + random.nextDouble() * 8.0D;
            case OLIVINE -> 7.0D + random.nextDouble() * 6.0D;
            case CHEESE -> 5.0D + random.nextDouble() * 5.0D;
        };

        double ry = switch (style) {
            case GLACIAL -> 5.0D + random.nextDouble() * 5.0D;
            case OLIVINE -> 4.0D + random.nextDouble() * 4.0D;
            case CHEESE -> 3.0D + random.nextDouble() * 3.0D;
        };

        double rz = switch (style) {
            case GLACIAL -> 14.0D + random.nextDouble() * 8.0D;
            case OLIVINE -> 7.0D + random.nextDouble() * 6.0D;
            case CHEESE -> 5.0D + random.nextDouble() * 5.0D;
        };

        plan.addRoom(new MoonCaveRoom(center, rx, ry, rz, random.nextInt()));
    }

    private static void addTunnel(MoonCavePlan plan, BlockPos start, BlockPos end, MoonCaveStyle style, RandomSource random) {
        double radius = switch (style) {
            case GLACIAL -> 1.2D + random.nextDouble() * 0.8D;
            case OLIVINE -> 1.7D + random.nextDouble() * 1.2D;
            case CHEESE -> 2.0D + random.nextDouble() * 1.5D;
        };

        double curve = switch (style) {
            case GLACIAL -> 5.0D + random.nextDouble() * 7.0D;
            case OLIVINE -> 3.0D + random.nextDouble() * 5.0D;
            case CHEESE -> 2.0D + random.nextDouble() * 4.0D;
        };

        plan.addTunnel(new MoonCaveTunnel(start, end, radius, curve, random.nextInt()));
    }

    private static BlockPos offsetRoom(BlockPos origin, MoonCaveStyle style, RandomSource random) {
        int distance = switch (style) {
            case GLACIAL -> 26 + random.nextInt(28);
            case OLIVINE -> 14 + random.nextInt(22);
            case CHEESE -> 10 + random.nextInt(18);
        };

        double angle = random.nextDouble() * Math.PI * 2.0D;
        int x = origin.getX() + (int) Math.round(Math.cos(angle) * distance);
        int z = origin.getZ() + (int) Math.round(Math.sin(angle) * distance);
        int y = clamp(origin.getY() - 10 + random.nextInt(21), MIN_Y, MAX_Y);

        return new BlockPos(x, y, z);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}