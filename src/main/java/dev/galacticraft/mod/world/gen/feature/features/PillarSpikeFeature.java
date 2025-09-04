/*
 * Copyright (c) 2019-2025 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.world.gen.feature.features;

import com.mojang.serialization.Codec;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.world.dimension.MoonConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.phys.Vec3;

public class PillarSpikeFeature extends Feature<NoneFeatureConfiguration> {
    private final int attempts;

    public PillarSpikeFeature(Codec<NoneFeatureConfiguration> codec, int attempts) {
        super(codec);
        this.attempts = attempts;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        boolean placed = false;

        for (int i = 0; i < attempts; i++) {
            BlockPos start = findAirInCave(level, origin.offset(random.nextInt(16), 0, random.nextInt(16)), random);
            if (start == null) continue;

            Vec3 direction = randomUnitDirection(random);
            BlockPos end = raycastUntilSolid(level, Vec3.atCenterOf(start), direction, 100);

            if (end == null || start.distSqr(end) < 25 || !isAirLine(level, start, end)) continue;

            placed |= queueOrPlace(level, start, GCBlocks.MOON_BASALT.defaultBlockState());
            placed |= queueOrPlace(level, end, GCBlocks.MOON_BASALT.defaultBlockState());

            Vec3 beamDir = Vec3.atCenterOf(end).subtract(Vec3.atCenterOf(start));

            //smartMergeBeamIntoTerrain(level, start, beamDir, GCBlocks.OLIVINE_BLOCK.defaultBlockState(), 8, 4f);
            //smartMergeBeamIntoTerrain(level, end, beamDir.scale(-1), GCBlocks.OLIVINE_BLOCK.defaultBlockState(), 8, 4f);

            placed |= drawLine(level, start, end, GCBlocks.MOON_BASALT.defaultBlockState());
        }

        return placed;
    }

    private Vec3 randomUnitDirection(RandomSource random) {
        double theta = random.nextDouble() * 2 * Math.PI;
        double phi = Math.acos(2 * random.nextDouble() - 1);
        double x = Math.sin(phi) * Math.cos(theta);
        double y = Math.sin(phi) * Math.sin(theta);
        double z = Math.cos(phi);
        return new Vec3(x, y, z).normalize();
    }

    private BlockPos raycastUntilSolid(WorldGenLevel level, Vec3 start, Vec3 dir, int maxSteps) {
        Vec3 pos = start;
        Vec3 step = dir.normalize().scale(0.8);
        for (int i = 0; i < maxSteps; i++) {
            pos = pos.add(step);
            BlockPos blockPos = BlockPos.containing(pos);
            if (level.getBlockState(blockPos).isSolidRender(level, blockPos)) {
                // Step back slightly to get air position near the solid surface
                return blockPos.relative(directionToClosestDirection(dir).getOpposite());
            }
        }
        return null;
    }

    private BlockPos findAirInCave(WorldGenLevel level, BlockPos center, RandomSource random) {
        for (int tries = 0; tries < 30; tries++) {
            int y = MoonConstants.OLIVINE_CAVE_MIN_HEIGHT + random.nextInt(MoonConstants.MAX_FEATURE_SPAWN - MoonConstants.MIN_FEATURE_SPAWN);
            BlockPos pos = new BlockPos(center.getX(), y, center.getZ());
            if (!level.getBlockState(pos).isSolidRender(level, pos)) {
                for (Direction direction : Direction.values()) {
                    if (level.getBlockState(pos.relative(direction)).getBlock().defaultBlockState().isSolidRender(level, pos)) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }

    private boolean drawLine(WorldGenLevel level, BlockPos start, BlockPos end, BlockState state) {
        boolean placed = false;

        Vec3 from = Vec3.atCenterOf(start);
        Vec3 to = Vec3.atCenterOf(end);
        Vec3 dir = to.subtract(from);
        float length = (float) dir.length();
        Vec3 step = dir.normalize().scale(1.0 / 2.5); // 0.4 blocks per step
        int steps = (int) (length * 2.5);

        // Determine width from length
        int width = 1 + Mth.floor((length - 10f) / 10f);
        width = Math.min(Math.max(width, 1), 5);

        // Build two perpendicular basis vectors (u, v) for triangle cross-section
        Vec3 forward = dir.normalize();
        Vec3 up = new Vec3(0, 1, 0);
        if (Math.abs(forward.dot(up)) > 0.98) {
            up = new Vec3(1, 0, 0); // avoid near-parallel
        }
        Vec3 right = forward.cross(up).normalize();
        Vec3 normal = forward.cross(right).normalize();

        // Triangle vertices in local space
        Vec3 a = right.scale(width);
        Vec3 b = right.scale(-width / 2.0).add(normal.scale(width * Math.sqrt(3) / 2.0));
        Vec3 c = right.scale(-width / 2.0).add(normal.scale(-width * Math.sqrt(3) / 2.0));

        Vec3 pos = from;
        for (int i = 0; i <= steps; i++) {
            Vec3 center = pos;
            placed |= fillTriangleWithBudding(level, center, a, b, c, state);
            pos = pos.add(step);
        }

        return placed;
    }

    private boolean fillTriangleWithBudding(WorldGenLevel level, Vec3 origin, Vec3 a, Vec3 b, Vec3 c, BlockState defaultState) {
        boolean placed = false;

        int resolution = 2 * Mth.ceil(a.length());
        RandomSource random = level.getRandom();

        for (int i = 0; i <= resolution; i++) {
            for (int j = 0; i + j <= resolution; j++) {
                float u = i / (float) resolution;
                float v = j / (float) resolution;
                float w = 1.0f - u - v;

                Vec3 p = a.scale(u).add(b.scale(v)).add(c.scale(w)).add(origin);
                BlockPos pos = BlockPos.containing(p);

                // 5% chance to place budding olivine
                if (random.nextFloat() < 0.05f) {
                    placed |= queueOrPlace(level, pos, GCBlocks.BUDDING_OLIVINE.defaultBlockState());

                    for (Direction dir : Direction.values()) {
                        BlockPos clusterPos = pos.relative(dir);
                        if (level.getBlockState(clusterPos).isAir() && random.nextFloat() < 0.75f) {
                            BlockState clusterState = GCBlocks.OLIVINE_CLUSTER.defaultBlockState()
                                    .setValue(AmethystClusterBlock.FACING, dir);
                            placed |= queueOrPlace(level, clusterPos, clusterState);
                        }
                    }
                } else {
                    placed |= queueOrPlace(level, pos, defaultState);
                }
            }
        }

        return placed;
    }

    private boolean isAirLine(WorldGenLevel level, BlockPos start, BlockPos end) {
        Vec3 from = Vec3.atCenterOf(start);
        Vec3 to = Vec3.atCenterOf(end);
        Vec3 dir = to.subtract(from);
        int steps = (int) (dir.length() * 3); // finer steps for accuracy
        Vec3 step = dir.normalize().scale(1.0 / 3.0); // 0.33 blocks per step

        Vec3 pos = from;
        for (int i = 0; i <= steps; i++) {
            BlockPos blockPos = BlockPos.containing(pos);
            if (!blockPos.equals(start) && !blockPos.equals(end)) {
                if (level.getBlockState(blockPos).isSolidRender(level, blockPos)) return false;
            }
            pos = pos.add(step);
        }

        return true;
    }

    private void smartMergeBeamIntoTerrain(WorldGenLevel level, BlockPos tip, Vec3 beamDir, BlockState state, int curveLength, float maxRadius) {
        Vec3 direction = beamDir.normalize();
        Vec3 start = Vec3.atCenterOf(tip).subtract(direction.scale(3)); // start ~3 blocks inside the beam

        for (int i = 0; i < curveLength; i++) {
            float t = i / (float)curveLength;

            // Arc blend path: out from the beam in a gentle arc
            float radius = maxRadius * Mth.sin(t * Mth.HALF_PI);  // Smooth ramp
            Vec3 center = start.add(direction.scale(i));

            // Flatten the blending into a capsule shape along the beam axis
            int rX = Mth.ceil(radius);
            int rY = Mth.ceil(radius * 0.5); // Flatten vertically
            int rZ = Mth.ceil(radius);

            BlockPos centerPos = BlockPos.containing(center);

            for (int dx = -rX; dx <= rX; dx++) {
                for (int dy = -rY; dy <= rY; dy++) {
                    for (int dz = -rZ; dz <= rZ; dz++) {
                        BlockPos pos = centerPos.offset(dx, dy, dz);
                        Vec3 delta = Vec3.atCenterOf(pos).subtract(center);
                        if (delta.length() > radius) continue;

                        // Only blend where solid terrain is adjacent *in the direction of the beam*
                        BlockPos terrainCheck = pos.relative(directionToClosestDirection(direction));
                        if (level.getBlockState(terrainCheck).isSolidRender(level, terrainCheck) &&
                                !level.getBlockState(pos).isSolidRender(level, pos)) {
                            queueOrPlace(level, pos, state);
                        }
                    }
                }
            }
        }
    }

    private static Direction directionToClosestDirection(Vec3 vec) {
        double maxDot = -Double.MAX_VALUE;
        Direction closest = Direction.NORTH;

        for (Direction dir : Direction.values()) {
            Vec3 dirVec = Vec3.atLowerCornerOf(dir.getNormal());
            double dot = vec.dot(dirVec);
            if (dot > maxDot) {
                maxDot = dot;
                closest = dir;
            }
        }

        return closest;
    }

    private boolean queueOrPlace(WorldGenLevel level, BlockPos pos, BlockState state) {
        try {
            if (!level.getBlockState(pos).isSolidRender(level, pos) || level.getBlockState(pos).is(GCBlocks.MOON_BASALT)) {
                level.setBlock(pos, state, 2);
                return true;
            }
        } catch (IllegalStateException e) {
            DeferredBlockPlacement.queue(pos.immutable(), state);
        }
        return false;
    }
}