package dev.galacticraft.mod.world.gen.cave.shape;

import dev.galacticraft.mod.world.gen.cave.MoonCaveContext;
import dev.galacticraft.mod.world.gen.cave.MoonCavePlan;
import dev.galacticraft.mod.world.gen.cave.MoonCaveTunnel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public class PathSolvedLavaTubeCaveShape implements dev.galacticraft.mod.world.gen.cave.MoonCaveShape {
    private static final int MAX_HORIZONTAL_TUNNEL_LENGTH = 30;

    private final int minMainAnchors;
    private final int maxMainAnchors;
    private final int minSegmentsBetweenAnchors;
    private final int maxSegmentsBetweenAnchors;
    private final int minSideBranches;
    private final int maxSideBranches;
    private final int minSegmentLength;
    private final int maxSegmentLength;
    private final double minRadius;
    private final double maxRadius;
    private final double minCurve;
    private final double maxCurve;
    private final int minAnchorDistance;
    private final int maxAnchorDistance;
    private final int minTargetY;
    private final int maxTargetY;

    public PathSolvedLavaTubeCaveShape(
            int minMainAnchors,
            int maxMainAnchors,
            int minSegmentsBetweenAnchors,
            int maxSegmentsBetweenAnchors,
            int minSideBranches,
            int maxSideBranches,
            int minSegmentLength,
            int maxSegmentLength,
            double minRadius,
            double maxRadius,
            double minCurve,
            double maxCurve,
            int minAnchorDistance,
            int maxAnchorDistance,
            int minTargetY,
            int maxTargetY
    ) {
        this.minMainAnchors = minMainAnchors;
        this.maxMainAnchors = maxMainAnchors;
        this.minSegmentsBetweenAnchors = minSegmentsBetweenAnchors;
        this.maxSegmentsBetweenAnchors = maxSegmentsBetweenAnchors;
        this.minSideBranches = minSideBranches;
        this.maxSideBranches = maxSideBranches;
        this.minSegmentLength = minSegmentLength;
        this.maxSegmentLength = maxSegmentLength;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.minCurve = minCurve;
        this.maxCurve = maxCurve;
        this.minAnchorDistance = minAnchorDistance;
        this.maxAnchorDistance = maxAnchorDistance;
        this.minTargetY = minTargetY;
        this.maxTargetY = maxTargetY;
    }

    public PathSolvedLavaTubeCaveShape(PathSolvedLavaTubeCaveConfig config) {
        this(
                config.minMainAnchors(),
                config.maxMainAnchors(),
                config.minSegmentsBetweenAnchors(),
                config.maxSegmentsBetweenAnchors(),
                config.minSideBranches(),
                config.maxSideBranches(),
                config.minSegmentLength(),
                config.maxSegmentLength(),
                config.minRadius(),
                config.maxRadius(),
                config.minCurve(),
                config.maxCurve(),
                config.minAnchorDistance(),
                config.maxAnchorDistance(),
                config.minTargetY(),
                config.maxTargetY()
        );
    }

    @Override
    public MoonCavePlan createPlan(MoonCaveContext context) {
        RandomSource random = context.random();
        MoonCavePlan plan = new MoonCavePlan(context.cave(), context.cell(), random.nextDouble());

        List<BlockPos> nodes = new ArrayList<>();
        List<BlockPos> anchors = this.createAnchors(context, random);
        nodes.add(anchors.get(0));

        for (int i = 1; i < anchors.size(); i++) {
            this.solveTubePath(plan, anchors.get(i - 1), anchors.get(i), nodes, context, random);
        }

        int sideBranches = this.minSideBranches + random.nextInt(this.maxSideBranches - this.minSideBranches + 1);

        for (int i = 0; i < sideBranches && !nodes.isEmpty(); i++) {
            BlockPos start = nodes.get(random.nextInt(nodes.size()));
            BlockPos end = this.randomSideTarget(start, context, random);
            this.solveTubePath(plan, start, end, nodes, context, random);
        }

        return plan;
    }

    private List<BlockPos> createAnchors(MoonCaveContext context, RandomSource random) {
        List<BlockPos> anchors = new ArrayList<>();
        anchors.add(context.anchor());

        int anchorCount = this.minMainAnchors + random.nextInt(this.maxMainAnchors - this.minMainAnchors + 1);
        double heading = random.nextDouble() * Math.PI * 2.0D;
        BlockPos previous = context.anchor();

        for (int i = 1; i < anchorCount; i++) {
            heading += randomBetween(random, -0.85D, 0.85D);

            int distance = this.minAnchorDistance + random.nextInt(this.maxAnchorDistance - this.minAnchorDistance + 1);
            int x = previous.getX() + (int) Math.round(Math.cos(heading) * distance);
            int z = previous.getZ() + (int) Math.round(Math.sin(heading) * distance);

            double progress = i / (double) Math.max(1, anchorCount - 1);
            int targetY = lerp(context.anchor().getY(), randomBetween(random, this.minTargetY, this.maxTargetY), progress);
            int y = context.clampY(targetY + random.nextInt(13) - 6);

            previous = new BlockPos(x, y, z);
            anchors.add(previous);
        }

        return anchors;
    }

    private void solveTubePath(
            MoonCavePlan plan,
            BlockPos start,
            BlockPos end,
            List<BlockPos> nodes,
            MoonCaveContext context,
            RandomSource random
    ) {
        int baseSegments = this.minSegmentsBetweenAnchors + random.nextInt(this.maxSegmentsBetweenAnchors - this.minSegmentsBetweenAnchors + 1);
        int forcedSegments = Math.max(1, (int) Math.ceil(horizontalDistance(start, end) / MAX_HORIZONTAL_TUNNEL_LENGTH));
        int segments = Math.max(baseSegments, forcedSegments);

        BlockPos previous = start;
        double heading = Math.atan2(end.getZ() - start.getZ(), end.getX() - start.getX());

        for (int i = 1; i <= segments; i++) {
            double t = i / (double) segments;
            boolean last = i == segments;

            BlockPos next = last ? end : this.noisyIntermediateNode(start, end, t, heading, context, random);

            this.addTunnel(plan, previous, next, random);
            nodes.add(next);
            previous = next;
        }
    }

    private BlockPos noisyIntermediateNode(
            BlockPos start,
            BlockPos end,
            double t,
            double heading,
            MoonCaveContext context,
            RandomSource random
    ) {
        int x = (int) Math.round(start.getX() + (end.getX() - start.getX()) * t);
        int y = (int) Math.round(start.getY() + (end.getY() - start.getY()) * t);
        int z = (int) Math.round(start.getZ() + (end.getZ() - start.getZ()) * t);

        double sideAngle = heading + Math.PI / 2.0D;
        double fade = Math.sin(t * Math.PI);
        double sideWarp = randomBetween(random, -10.0D, 10.0D) * fade;
        double forwardWarp = randomBetween(random, -5.0D, 5.0D) * fade;

        x += (int) Math.round(Math.cos(sideAngle) * sideWarp + Math.cos(heading) * forwardWarp);
        z += (int) Math.round(Math.sin(sideAngle) * sideWarp + Math.sin(heading) * forwardWarp);
        y = context.clampY(y + (int) Math.round(randomBetween(random, -8.0D, 8.0D) * fade));

        return new BlockPos(x, y, z);
    }

    private BlockPos randomSideTarget(BlockPos start, MoonCaveContext context, RandomSource random) {
        double angle = random.nextDouble() * Math.PI * 2.0D;
        int distance = this.minAnchorDistance + random.nextInt(this.maxAnchorDistance - this.minAnchorDistance + 1);

        int x = start.getX() + (int) Math.round(Math.cos(angle) * distance);
        int z = start.getZ() + (int) Math.round(Math.sin(angle) * distance);
        int y = context.clampY(start.getY() - random.nextInt(24) + random.nextInt(14));

        return new BlockPos(x, y, z);
    }

    private void addTunnel(MoonCavePlan plan, BlockPos start, BlockPos end, RandomSource random) {
        double radius = randomBetween(random, this.minRadius, this.maxRadius);
        double curve = randomBetween(random, this.minCurve, this.maxCurve);

        plan.addTunnel(new MoonCaveTunnel(start, end, radius, curve, random.nextInt()));
    }

    private static double horizontalDistance(BlockPos a, BlockPos b) {
        int dx = a.getX() - b.getX();
        int dz = a.getZ() - b.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    private static int lerp(int start, int end, double t) {
        return (int) Math.round(start + (end - start) * t);
    }

    private static int randomBetween(RandomSource random, int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    private static double randomBetween(RandomSource random, double min, double max) {
        return min + random.nextDouble() * (max - min);
    }
}