package dev.galacticraft.mod.world.gen.cave.shape;

import dev.galacticraft.mod.world.gen.cave.MoonCaveContext;
import dev.galacticraft.mod.world.gen.cave.MoonCavePlan;
import dev.galacticraft.mod.world.gen.cave.MoonCaveRoom;
import dev.galacticraft.mod.world.gen.cave.MoonCaveTunnel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public class PathSolvedBranchingCaveShape implements dev.galacticraft.mod.world.gen.cave.MoonCaveShape {
    private static final int MAX_ROOM_LINK_HORIZONTAL_DISTANCE = 22;

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

    public PathSolvedBranchingCaveShape(PathSolvedBranchingCaveConfig config) {
        this(
                config.minMainAnchors(),
                config.maxMainAnchors(),
                config.minRoomsBetweenAnchors(),
                config.maxRoomsBetweenAnchors(),
                config.minSideBranches(),
                config.maxSideBranches(),
                config.minRadius(),
                config.maxRadius(),
                config.minHeightRadius(),
                config.maxHeightRadius(),
                config.minTunnelRadius(),
                config.maxTunnelRadius(),
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

        List<BlockPos> rooms = new ArrayList<>();
        BlockPos current = context.anchor();
        rooms.add(current);
        this.addRoom(plan, current, random);

        int anchorCount = this.minMainAnchors + random.nextInt(this.maxMainAnchors - this.minMainAnchors + 1);
        double heading = random.nextDouble() * Math.PI * 2.0D;

        for (int i = 1; i < anchorCount; i++) {
            heading += randomBetween(random, -0.9D, 0.9D);

            int distance = this.minAnchorDistance + random.nextInt(this.maxAnchorDistance - this.minAnchorDistance + 1);
            double progress = i / (double) Math.max(1, anchorCount - 1);
            int targetY = lerp(context.anchor().getY(), randomBetween(random, this.minTargetY, this.maxTargetY), progress);

            BlockPos target = new BlockPos(
                    current.getX() + (int) Math.round(Math.cos(heading) * distance),
                    context.clampY(targetY + random.nextInt(13) - 6),
                    current.getZ() + (int) Math.round(Math.sin(heading) * distance)
            );

            current = this.solveRoomPath(plan, current, target, rooms, context, random);
        }

        int sideBranches = this.minSideBranches + random.nextInt(this.maxSideBranches - this.minSideBranches + 1);

        for (int i = 0; i < sideBranches && !rooms.isEmpty(); i++) {
            BlockPos start = rooms.get(random.nextInt(rooms.size()));
            BlockPos target = this.randomSideTarget(start, context, random);
            this.solveRoomPath(plan, start, target, rooms, context, random);
        }

        return plan;
    }

    private BlockPos solveRoomPath(
            MoonCavePlan plan,
            BlockPos start,
            BlockPos end,
            List<BlockPos> rooms,
            MoonCaveContext context,
            RandomSource random
    ) {
        int baseRooms = this.minRoomsBetweenAnchors + random.nextInt(this.maxRoomsBetweenAnchors - this.minRoomsBetweenAnchors + 1);
        int forcedRooms = Math.max(1, (int) Math.ceil(horizontalDistance(start, end) / MAX_ROOM_LINK_HORIZONTAL_DISTANCE));
        int roomsBetween = Math.max(baseRooms, forcedRooms);

        BlockPos previous = start;
        double heading = Math.atan2(end.getZ() - start.getZ(), end.getX() - start.getX());

        for (int i = 1; i <= roomsBetween; i++) {
            double t = i / (double) roomsBetween;
            BlockPos next = i == roomsBetween
                    ? end
                    : this.noisyRoomNode(start, end, t, heading, context, random);

            rooms.add(next);
            this.addRoom(plan, next, random);
            this.addTunnel(plan, previous, next, random);
            previous = next;
        }

        return previous;
    }

    private BlockPos noisyRoomNode(
            BlockPos start,
            BlockPos end,
            double t,
            double heading,
            MoonCaveContext context,
            RandomSource random
    ) {
        double fade = Math.sin(t * Math.PI);
        double sideAngle = heading + Math.PI / 2.0D;

        double x = start.getX() + (end.getX() - start.getX()) * t;
        double y = start.getY() + (end.getY() - start.getY()) * t;
        double z = start.getZ() + (end.getZ() - start.getZ()) * t;

        double sideWarp = randomBetween(random, -11.0D, 11.0D) * fade;
        double forwardWarp = randomBetween(random, -5.0D, 5.0D) * fade;
        double verticalWarp = randomBetween(random, -8.0D, 8.0D) * fade;

        x += Math.cos(sideAngle) * sideWarp + Math.cos(heading) * forwardWarp;
        z += Math.sin(sideAngle) * sideWarp + Math.sin(heading) * forwardWarp;
        y = context.clampY((int) Math.round(y + verticalWarp));

        return new BlockPos((int) Math.round(x), (int) Math.round(y), (int) Math.round(z));
    }

    private BlockPos randomSideTarget(BlockPos start, MoonCaveContext context, RandomSource random) {
        double angle = random.nextDouble() * Math.PI * 2.0D;
        int distance = Math.min(
                this.minAnchorDistance + random.nextInt(this.maxAnchorDistance - this.minAnchorDistance + 1),
                58
        );

        return new BlockPos(
                start.getX() + (int) Math.round(Math.cos(angle) * distance),
                context.clampY(start.getY() - random.nextInt(20) + random.nextInt(12)),
                start.getZ() + (int) Math.round(Math.sin(angle) * distance)
        );
    }

    private void addRoom(MoonCavePlan plan, BlockPos center, RandomSource random) {
        double radiusX = randomBetween(random, this.minRadius, this.maxRadius);
        double radiusY = randomBetween(random, this.minHeightRadius, this.maxHeightRadius);
        double radiusZ = randomBetween(random, this.minRadius, this.maxRadius);

        plan.addRoom(new MoonCaveRoom(center, radiusX, radiusY, radiusZ, random.nextInt()));
    }

    private void addTunnel(MoonCavePlan plan, BlockPos start, BlockPos end, RandomSource random) {
        double radius = randomBetween(random, this.minTunnelRadius, this.maxTunnelRadius);
        double curve = 8.0D + random.nextDouble() * 14.0D;

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