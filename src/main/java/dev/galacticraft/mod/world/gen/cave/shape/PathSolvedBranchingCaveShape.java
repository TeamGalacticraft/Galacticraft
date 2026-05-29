package dev.galacticraft.mod.world.gen.cave.shape;

import dev.galacticraft.mod.world.gen.cave.MoonCaveContext;
import dev.galacticraft.mod.world.gen.cave.MoonCavePlan;
import dev.galacticraft.mod.world.gen.cave.MoonCaveRoom;
import dev.galacticraft.mod.world.gen.cave.MoonCaveTunnel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Goal-directed branching cave shape.
 *
 * <p>This shape creates room-based cave systems where several required anchors
 * must be reached. It produces large-room/small-tunnel/large-room style caves,
 * but unlike a purely random branching generator it guarantees that the cave
 * reaches deeper or farther target areas.</p>
 */
public class PathSolvedBranchingCaveShape implements dev.galacticraft.mod.world.gen.cave.MoonCaveShape {
    private final int minMainAnchors;
    private final int maxMainAnchors;
    private final int minRoomsBetweenAnchors;
    private final int maxRoomsBetweenAnchors;
    private final int minSideBranches;
    private final int maxSideBranches;
    private final double minRadius;
    private final double maxRadius;
    private final double minHeightRadius;
    private final double maxHeightRadius;
    private final double minTunnelRadius;
    private final double maxTunnelRadius;
    private final int minAnchorDistance;
    private final int maxAnchorDistance;
    private final int minTargetY;
    private final int maxTargetY;

    public PathSolvedBranchingCaveShape(
            int minMainAnchors,
            int maxMainAnchors,
            int minRoomsBetweenAnchors,
            int maxRoomsBetweenAnchors,
            int minSideBranches,
            int maxSideBranches,
            double minRadius,
            double maxRadius,
            double minHeightRadius,
            double maxHeightRadius,
            double minTunnelRadius,
            double maxTunnelRadius,
            int minAnchorDistance,
            int maxAnchorDistance,
            int minTargetY,
            int maxTargetY
    ) {
        this.minMainAnchors = minMainAnchors;
        this.maxMainAnchors = maxMainAnchors;
        this.minRoomsBetweenAnchors = minRoomsBetweenAnchors;
        this.maxRoomsBetweenAnchors = maxRoomsBetweenAnchors;
        this.minSideBranches = minSideBranches;
        this.maxSideBranches = maxSideBranches;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.minHeightRadius = minHeightRadius;
        this.maxHeightRadius = maxHeightRadius;
        this.minTunnelRadius = minTunnelRadius;
        this.maxTunnelRadius = maxTunnelRadius;
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

        List<BlockPos> allRooms = new ArrayList<>();
        List<BlockPos> anchors = this.createAnchors(context, random);

        for (int i = 0; i < anchors.size(); i++) {
            BlockPos anchor = anchors.get(i);
            allRooms.add(anchor);
            this.addRoom(plan, anchor, random);

            if (i > 0) {
                this.solveRoomPath(plan, anchors.get(i - 1), anchor, allRooms, context, random);
            }
        }

        int sideBranches = this.minSideBranches + random.nextInt(this.maxSideBranches - this.minSideBranches + 1);

        for (int i = 0; i < sideBranches && !allRooms.isEmpty(); i++) {
            BlockPos start = allRooms.get(random.nextInt(allRooms.size()));
            BlockPos end = this.randomSideTarget(start, context, random);
            this.solveRoomPath(plan, start, end, allRooms, context, random);
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
            int y = context.clampY(targetY + random.nextInt(15) - 7);

            anchors.add(new BlockPos(x, y, z));
        }

        return anchors;
    }

    private void solveRoomPath(
            MoonCavePlan plan,
            BlockPos start,
            BlockPos end,
            List<BlockPos> allRooms,
            MoonCaveContext context,
            RandomSource random
    ) {
        int roomsBetween = this.minRoomsBetweenAnchors + random.nextInt(this.maxRoomsBetweenAnchors - this.minRoomsBetweenAnchors + 1);
        BlockPos previous = start;

        for (int i = 1; i <= roomsBetween; i++) {
            double t = i / (double) (roomsBetween + 1);
            BlockPos next = this.interpolateRoom(start, end, t, context, random);

            allRooms.add(next);
            this.addRoom(plan, next, random);
            this.addTunnel(plan, previous, next, random);

            previous = next;
        }

        this.addTunnel(plan, previous, end, random);
    }

    private BlockPos interpolateRoom(BlockPos start, BlockPos end, double t, MoonCaveContext context, RandomSource random) {
        int x = (int) Math.round(start.getX() + (end.getX() - start.getX()) * t) + random.nextInt(17) - 8;
        int y = context.clampY((int) Math.round(start.getY() + (end.getY() - start.getY()) * t) + random.nextInt(9) - 4);
        int z = (int) Math.round(start.getZ() + (end.getZ() - start.getZ()) * t) + random.nextInt(17) - 8;

        return new BlockPos(x, y, z);
    }

    private BlockPos randomSideTarget(BlockPos start, MoonCaveContext context, RandomSource random) {
        double angle = random.nextDouble() * Math.PI * 2.0D;
        int distance = this.minAnchorDistance + random.nextInt(this.maxAnchorDistance - this.minAnchorDistance + 1);

        int x = start.getX() + (int) Math.round(Math.cos(angle) * distance);
        int z = start.getZ() + (int) Math.round(Math.sin(angle) * distance);
        int y = context.clampY(start.getY() - random.nextInt(28) + random.nextInt(12));

        return new BlockPos(x, y, z);
    }

    private void addRoom(MoonCavePlan plan, BlockPos center, RandomSource random) {
        double radiusX = randomBetween(random, this.minRadius, this.maxRadius);
        double radiusY = randomBetween(random, this.minHeightRadius, this.maxHeightRadius);
        double radiusZ = randomBetween(random, this.minRadius, this.maxRadius);

        plan.addRoom(new MoonCaveRoom(center, radiusX, radiusY, radiusZ, random.nextInt()));
    }

    private void addTunnel(MoonCavePlan plan, BlockPos start, BlockPos end, RandomSource random) {
        double radius = randomBetween(random, this.minTunnelRadius, this.maxTunnelRadius);
        double curve = 3.0D + random.nextDouble() * 10.0D;

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