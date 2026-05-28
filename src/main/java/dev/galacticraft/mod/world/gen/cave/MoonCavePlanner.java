package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.carver.CarvingContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MoonCavePlanner {
    public static final MoonCavePlanner INSTANCE = new MoonCavePlanner();

    private static final int MIN_Y = -46;
    private static final int MAX_Y = 34;
    private static final int NEIGHBOR_RADIUS = 2;

    private MoonCavePlanner() {
    }

    public List<MoonCavePlan> plansForChunk(CarvingContext context, ChunkPos chunk, Function<BlockPos, Holder<Biome>> posToBiome) {
        MoonCaveRegionPos center = MoonCaveRegionPos.fromChunk(chunk);
        List<MoonCavePlan> result = new ArrayList<>();

        for (int dx = -NEIGHBOR_RADIUS; dx <= NEIGHBOR_RADIUS; dx++) {
            for (int dz = -NEIGHBOR_RADIUS; dz <= NEIGHBOR_RADIUS; dz++) {
                MoonCaveRegionPos region = new MoonCaveRegionPos(center.x() + dx, center.z() + dz);
                MoonCavePlan plan = this.acceptedPlan(context, region, posToBiome);

                if (plan != null && plan.bounds().intersectsChunk(chunk)) {
                    result.add(plan);
                }
            }
        }

        return result;
    }

    private MoonCavePlan acceptedPlan(CarvingContext context, MoonCaveRegionPos region, Function<BlockPos, Holder<Biome>> posToBiome) {
        MoonCavePlan candidate = this.rawPlan(context, region, posToBiome);

        if (candidate == null) {
            return null;
        }

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }

                MoonCaveRegionPos otherRegion = new MoonCaveRegionPos(region.x() + dx, region.z() + dz);
                MoonCavePlan other = this.rawPlan(context, otherRegion, posToBiome);

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

    private MoonCavePlan rawPlan(CarvingContext context, MoonCaveRegionPos region, Function<BlockPos, Holder<Biome>> posToBiome) {
        RandomSource random = context.randomState().aquiferRandom().at(region.centerBlockX() + 91821, -7137, region.centerBlockZ() - 44291);

        if (random.nextFloat() > 0.22F) {
            return null;
        }

        int anchorX = region.minBlockX() + 32 + random.nextInt(MoonCaveRegionPos.REGION_SIZE_CHUNKS * 16 - 64);
        int anchorZ = region.minBlockZ() + 32 + random.nextInt(MoonCaveRegionPos.REGION_SIZE_CHUNKS * 16 - 64);
        int anchorY = MIN_Y + random.nextInt(MAX_Y - MIN_Y + 1);

        BlockPos anchor = new BlockPos(anchorX, anchorY, anchorZ);
        MoonCaveStyle style = MoonCaveStyle.fromBiome(posToBiome.apply(anchor));

        if (style == null) {
            return null;
        }

        MoonCavePlan plan = new MoonCavePlan(region, random.nextDouble(), style);

        int rooms = switch (style) {
            case GLACIAL -> 2 + random.nextInt(2);
            case OLIVINE -> 3 + random.nextInt(3);
            case CHEESE -> 4 + random.nextInt(4);
        };

        BlockPos previous = null;
        BlockPos current = anchor;

        for (int i = 0; i < rooms; i++) {
            current = this.offsetRoom(current, random, style, i == 0);

            double rx = switch (style) {
                case GLACIAL -> 13.0D + random.nextDouble() * 10.0D;
                case OLIVINE -> 7.0D + random.nextDouble() * 6.0D;
                case CHEESE -> 5.0D + random.nextDouble() * 5.0D;
            };

            double ry = switch (style) {
                case GLACIAL -> 5.0D + random.nextDouble() * 5.0D;
                case OLIVINE -> 4.0D + random.nextDouble() * 4.0D;
                case CHEESE -> 4.0D + random.nextDouble() * 3.0D;
            };

            double rz = switch (style) {
                case GLACIAL -> 13.0D + random.nextDouble() * 10.0D;
                case OLIVINE -> 7.0D + random.nextDouble() * 6.0D;
                case CHEESE -> 5.0D + random.nextDouble() * 5.0D;
            };

            plan.addRoom(new MoonCaveRoom(current, rx, ry, rz, random.nextInt()));

            if (previous != null) {
                double tunnelRadius = switch (style) {
                    case GLACIAL -> 1.3D + random.nextDouble() * 1.0D;
                    case OLIVINE -> 1.8D + random.nextDouble() * 1.6D;
                    case CHEESE -> 2.0D + random.nextDouble() * 1.8D;
                };

                double curve = switch (style) {
                    case GLACIAL -> 5.0D + random.nextDouble() * 7.0D;
                    case OLIVINE -> 3.0D + random.nextDouble() * 5.0D;
                    case CHEESE -> 2.0D + random.nextDouble() * 4.0D;
                };

                plan.addTunnel(new MoonCaveTunnel(previous, current, tunnelRadius, curve, random.nextInt()));
            }

            previous = current;
        }

        return plan;
    }

    private BlockPos offsetRoom(BlockPos origin, RandomSource random, MoonCaveStyle style, boolean first) {
        if (first) {
            return origin;
        }

        int horizontal = switch (style) {
            case GLACIAL -> 24 + random.nextInt(32);
            case OLIVINE -> 14 + random.nextInt(24);
            case CHEESE -> 10 + random.nextInt(20);
        };

        double angle = random.nextDouble() * Math.PI * 2.0D;

        int dx = (int) Math.round(Math.cos(angle) * horizontal);
        int dz = (int) Math.round(Math.sin(angle) * horizontal);
        int dy = -10 + random.nextInt(21);

        return new BlockPos(origin.getX() + dx, clamp(origin.getY() + dy, MIN_Y, MAX_Y), origin.getZ() + dz);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}