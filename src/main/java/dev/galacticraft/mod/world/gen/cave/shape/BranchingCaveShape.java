package dev.galacticraft.mod.world.gen.cave.shape;

import dev.galacticraft.mod.world.gen.cave.MoonCaveContext;
import dev.galacticraft.mod.world.gen.cave.MoonCavePlan;
import dev.galacticraft.mod.world.gen.cave.MoonCaveRoom;
import dev.galacticraft.mod.world.gen.cave.MoonCaveTunnel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

public class BranchingCaveShape implements dev.galacticraft.mod.world.gen.cave.MoonCaveShape {
    private final int minRooms;
    private final int maxRooms;
    private final double minRadius;
    private final double maxRadius;
    private final double minHeightRadius;
    private final double maxHeightRadius;
    private final double minTunnelRadius;
    private final double maxTunnelRadius;
    private final int minRoomDistance;
    private final int maxRoomDistance;

    public BranchingCaveShape(
            int minRooms,
            int maxRooms,
            double minRadius,
            double maxRadius,
            double minHeightRadius,
            double maxHeightRadius,
            double minTunnelRadius,
            double maxTunnelRadius,
            int minRoomDistance,
            int maxRoomDistance
    ) {
        this.minRooms = minRooms;
        this.maxRooms = maxRooms;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.minHeightRadius = minHeightRadius;
        this.maxHeightRadius = maxHeightRadius;
        this.minTunnelRadius = minTunnelRadius;
        this.maxTunnelRadius = maxTunnelRadius;
        this.minRoomDistance = minRoomDistance;
        this.maxRoomDistance = maxRoomDistance;
    }

    @Override
    public MoonCavePlan createPlan(MoonCaveContext context) {
        RandomSource random = context.random();
        MoonCavePlan plan = new MoonCavePlan(context.definition().id(), context.cell(), random.nextDouble(), context.style());
        List<BlockPos> rooms = new ArrayList<>();

        int roomCount = this.minRooms + random.nextInt(this.maxRooms - this.minRooms + 1);
        rooms.add(context.anchor());
        this.addRoom(plan, context.anchor(), random);

        for (int i = 1; i < roomCount; i++) {
            BlockPos parent = rooms.get(random.nextInt(rooms.size()));
            BlockPos next = this.branchFrom(parent, context, random);

            rooms.add(next);
            this.addRoom(plan, next, random);

            double tunnelRadius = randomBetween(random, this.minTunnelRadius, this.maxTunnelRadius);
            double curve = 2.0D + random.nextDouble() * 6.0D;
            plan.addTunnel(new MoonCaveTunnel(parent, next, tunnelRadius, curve, random.nextInt()));
        }

        return plan;
    }

    private void addRoom(MoonCavePlan plan, BlockPos center, RandomSource random) {
        double radiusX = randomBetween(random, this.minRadius, this.maxRadius);
        double radiusY = randomBetween(random, this.minHeightRadius, this.maxHeightRadius);
        double radiusZ = randomBetween(random, this.minRadius, this.maxRadius);

        plan.addRoom(new MoonCaveRoom(center, radiusX, radiusY, radiusZ, random.nextInt()));
    }

    private BlockPos branchFrom(BlockPos parent, MoonCaveContext context, RandomSource random) {
        int distance = this.minRoomDistance + random.nextInt(this.maxRoomDistance - this.minRoomDistance + 1);
        double angle = random.nextDouble() * Math.PI * 2.0D;

        int x = parent.getX() + (int) Math.round(Math.cos(angle) * distance);
        int z = parent.getZ() + (int) Math.round(Math.sin(angle) * distance);
        int y = context.clampY(parent.getY() - 8 + random.nextInt(17));

        return new BlockPos(x, y, z);
    }

    private static double randomBetween(RandomSource random, double min, double max) {
        return min + random.nextDouble() * (max - min);
    }
}