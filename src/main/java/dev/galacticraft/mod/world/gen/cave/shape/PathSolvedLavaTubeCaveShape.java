package dev.galacticraft.mod.world.gen.cave.shape;

import dev.galacticraft.mod.world.gen.cave.MoonCaveContext;
import dev.galacticraft.mod.world.gen.cave.MoonCavePlan;
import dev.galacticraft.mod.world.gen.cave.MoonCaveTunnel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Goal-directed spaghetti/lava-tube cave shape.
 *
 * <p>This shape creates cave systems made only from tunnel segments. It has no
 * rooms. Required target anchors force the tunnel network to reach far lateral
 * and vertical locations, while optional branches create a sparse natural
 * spaghetti-cave structure.</p>
 */
public class PathSolvedLavaTubeCaveShape implements dev.galacticraft.mod.world.gen.cave.MoonCaveShape {
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

    @Override
    public MoonCavePlan createPlan(MoonCaveContext context) {
        RandomSource random = context.random();
        MoonCavePlan plan = new MoonCavePlan(
                context.cave(),
                context.cell(),
                random.nextDouble()
        );

        List<BlockPos> tunnelNodes = new ArrayList<>();
        List<BlockPos> anchors = this.createAnchors(context, random);

        tunnelNodes.add(anchors.get(0));

        for (int i = 1; i < anchors.size(); i++) {
            this.solveTubePath(plan, anchors.get(i - 1), anchors.get(i), tunnelNodes, context, random);
        }

        int sideBranches = this.minSideBranches + random.nextInt(this.maxSideBranches - this.minSideBranches + 1);

        for (int i = 0; i < sideBranches && !tunnelNodes.isEmpty(); i++) {
            BlockPos start = tunnelNodes.get(random.nextInt(tunnelNodes.size()));
            BlockPos end = this.randomSideTarget(start, context, random);

            this.solveTubePath(plan, start, end, tunnelNodes, context, random);
        }

        return plan;
    }

    private List<BlockPos> createAnchors(MoonCaveContext context, RandomSource random) {
        List<BlockPos> anchors = new ArrayList<>();
        anchors.add(context.anchor());

        int anchorCount = this.minMainAnchors + random.nextInt(this.maxMainAnchors - this.minMainAnchors + 1);

        for (int i = 1; i < anchorCount; i++) {
            double progress = i / (double) (anchorCount - 1);
            double angle = random.nextDouble() * Math.PI * 2.0D;
            int distance = this.minAnchorDistance + random.nextInt(this.maxAnchorDistance - this.minAnchorDistance + 1);

            int x = context.anchor().getX() + (int) Math.round(Math.cos(angle) * distance * i);
            int z = context.anchor().getZ() + (int) Math.round(Math.sin(angle) * distance * i);

            int targetY = lerp(context.anchor().getY(), randomBetween(random, this.minTargetY, this.maxTargetY), progress);
            int y = context.clampY(targetY + random.nextInt(11) - 5);

            anchors.add(new BlockPos(x, y, z));
        }

        return anchors;
    }

    private void solveTubePath(
            MoonCavePlan plan,
            BlockPos start,
            BlockPos end,
            List<BlockPos> tunnelNodes,
            MoonCaveContext context,
            RandomSource random
    ) {
        int segments = this.minSegmentsBetweenAnchors + random.nextInt(this.maxSegmentsBetweenAnchors - this.minSegmentsBetweenAnchors + 1);
        BlockPos previous = start;

        for (int i = 1; i <= segments; i++) {
            double t = i / (double) (segments + 1);
            BlockPos next = this.interpolateTubeNode(start, end, t, context, random);

            this.addTunnel(plan, previous, next, random);
            tunnelNodes.add(next);

            previous = next;
        }

        this.addTunnel(plan, previous, end, random);
        tunnelNodes.add(end);
    }

    private BlockPos interpolateTubeNode(BlockPos start, BlockPos end, double t, MoonCaveContext context, RandomSource random) {
        int x = (int) Math.round(start.getX() + (end.getX() - start.getX()) * t);
        int y = (int) Math.round(start.getY() + (end.getY() - start.getY()) * t);
        int z = (int) Math.round(start.getZ() + (end.getZ() - start.getZ()) * t);

        int horizontalWarp = this.minSegmentLength + random.nextInt(this.maxSegmentLength - this.minSegmentLength + 1);
        double angle = random.nextDouble() * Math.PI * 2.0D;

        x += (int) Math.round(Math.cos(angle) * horizontalWarp * 0.45D);
        z += (int) Math.round(Math.sin(angle) * horizontalWarp * 0.45D);
        y = context.clampY(y + random.nextInt(13) - 6);

        return new BlockPos(x, y, z);
    }

    private BlockPos randomSideTarget(BlockPos start, MoonCaveContext context, RandomSource random) {
        double angle = random.nextDouble() * Math.PI * 2.0D;
        int distance = this.minAnchorDistance + random.nextInt(this.maxAnchorDistance - this.minAnchorDistance + 1);

        int x = start.getX() + (int) Math.round(Math.cos(angle) * distance);
        int z = start.getZ() + (int) Math.round(Math.sin(angle) * distance);
        int y = context.clampY(start.getY() - random.nextInt(32) + random.nextInt(16));

        return new BlockPos(x, y, z);
    }

    private void addTunnel(MoonCavePlan plan, BlockPos start, BlockPos end, RandomSource random) {
        double radius = randomBetween(random, this.minRadius, this.maxRadius);
        double curve = randomBetween(random, this.minCurve, this.maxCurve);

        plan.addTunnel(new MoonCaveTunnel(start, end, radius, curve, random.nextInt()));
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