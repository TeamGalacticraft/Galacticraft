package dev.galacticraft.mod.world.gen.cave;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

/**
 * Organic worm tunnel made from overlapping blobs plus guaranteed capsule bridges.
 *
 * <p>The blobs create organic cave shape, while the capsule bridge between each
 * node prevents disconnected rooms/tunnel gaps.</p>
 */
public final class MoonCaveTunnel implements MoonCaveElement {
    private static final int MAX_STEP_HORIZONTAL_DISTANCE = 5;
    private static final double INNER_EXTRA = 1.15D;
    private static final double OUTER_EXTRA = 2.35D;
    private static final double MIN_CONNECT_RADIUS = 1.65D;

    private final BlockPos start;
    private final BlockPos end;
    private final double radius;
    private final double curve;
    private final int seed;
    private final Node[] nodes;
    private final Bridge[] bridges;
    private final MoonCaveBounds bounds;

    public MoonCaveTunnel(BlockPos start, BlockPos end, double radius, double curve, int seed) {
        this.start = start;
        this.end = end;
        this.radius = radius;
        this.curve = curve;
        this.seed = seed;
        this.nodes = this.createNodes();
        this.bridges = this.createBridges();
        this.bounds = this.createBounds();
    }

    @Override
    public MoonCaveBounds bounds() {
        return this.bounds;
    }

    @Override
    public void stamp(ChunkPos chunkPos, int minY, int maxY, CaveCarvingMask mask, MoonCavePlan owner) {
        for (Bridge bridge : this.bridges) {
            this.stampBridge(chunkPos, minY, maxY, mask, owner, bridge);
        }

        for (Node node : this.nodes) {
            this.stampNode(chunkPos, minY, maxY, mask, owner, node);
        }
    }

    private void stampNode(
            ChunkPos chunkPos,
            int minY,
            int maxY,
            CaveCarvingMask mask,
            MoonCavePlan owner,
            Node node
    ) {
        int padding = (int) Math.ceil(node.radius + OUTER_EXTRA + 4.0D);

        int minX = Math.max(chunkPos.getMinBlockX(), (int) Math.floor(node.x - padding));
        int maxX = Math.min(chunkPos.getMaxBlockX(), (int) Math.ceil(node.x + padding));
        int minZ = Math.max(chunkPos.getMinBlockZ(), (int) Math.floor(node.z - padding));
        int maxZ = Math.min(chunkPos.getMaxBlockZ(), (int) Math.ceil(node.z + padding));
        int lowY = Math.max(minY, (int) Math.floor(node.y - padding));
        int highY = Math.min(maxY, (int) Math.ceil(node.y + padding));

        for (int x = minX; x <= maxX; x++) {
            int localX = x - chunkPos.getMinBlockX();

            for (int z = minZ; z <= maxZ; z++) {
                int localZ = z - chunkPos.getMinBlockZ();

                for (int y = lowY; y <= highY; y++) {
                    CaveZone zone = this.nodeZone(node, x, y, z);

                    if (zone != CaveZone.NONE) {
                        mask.set(localX, y, localZ, zone, owner);
                    }
                }
            }
        }
    }

    private void stampBridge(
            ChunkPos chunkPos,
            int minY,
            int maxY,
            CaveCarvingMask mask,
            MoonCavePlan owner,
            Bridge bridge
    ) {
        int padding = (int) Math.ceil(bridge.radius + OUTER_EXTRA + 3.0D);

        int minX = Math.max(chunkPos.getMinBlockX(), (int) Math.floor(Math.min(bridge.a.x, bridge.b.x) - padding));
        int maxX = Math.min(chunkPos.getMaxBlockX(), (int) Math.ceil(Math.max(bridge.a.x, bridge.b.x) + padding));
        int minZ = Math.max(chunkPos.getMinBlockZ(), (int) Math.floor(Math.min(bridge.a.z, bridge.b.z) - padding));
        int maxZ = Math.min(chunkPos.getMaxBlockZ(), (int) Math.ceil(Math.max(bridge.a.z, bridge.b.z) + padding));
        int lowY = Math.max(minY, (int) Math.floor(Math.min(bridge.a.y, bridge.b.y) - padding));
        int highY = Math.min(maxY, (int) Math.ceil(Math.max(bridge.a.y, bridge.b.y) + padding));

        for (int x = minX; x <= maxX; x++) {
            int localX = x - chunkPos.getMinBlockX();

            for (int z = minZ; z <= maxZ; z++) {
                int localZ = z - chunkPos.getMinBlockZ();

                for (int y = lowY; y <= highY; y++) {
                    CaveZone zone = this.bridgeZone(bridge, x, y, z);

                    if (zone != CaveZone.NONE) {
                        mask.set(localX, y, localZ, zone, owner);
                    }
                }
            }
        }
    }

    private CaveZone nodeZone(Node node, int x, int y, int z) {
        double warpAmount = node.radius * 0.55D;

        double px = x + 0.5D + CaveNoise.fbm(this.seed + node.index * 17L, x, y, z, 0.075D, 2, 0.5D) * warpAmount;
        double py = y + 0.5D + CaveNoise.fbm(this.seed + node.index * 23L, x, y, z, 0.075D, 2, 0.5D) * warpAmount * 0.7D;
        double pz = z + 0.5D + CaveNoise.fbm(this.seed + node.index * 31L, x, y, z, 0.075D, 2, 0.5D) * warpAmount;

        double dx = px - node.x;
        double dy = py - node.y;
        double dz = pz - node.z;

        double large = CaveNoise.warpedFbm(this.seed + node.index * 43L, x, y, z, 0.07D, 6.0D, 2);
        double detail = CaveNoise.fbm(this.seed + node.index * 47L, x, y, z, 0.20D, 2, 0.45D);

        double localRadius = Math.max(MIN_CONNECT_RADIUS, node.radius + large * 1.05D + detail * 0.35D);
        return zoneFromDistance(dx * dx + dy * dy + dz * dz, localRadius);
    }

    private CaveZone bridgeZone(Bridge bridge, int x, int y, int z) {
        double distanceSqr = bridge.distanceSqr(x + 0.5D, y + 0.5D, z + 0.5D);

        double large = CaveNoise.warpedFbm(this.seed + bridge.index * 101L, x, y, z, 0.065D, 5.0D, 2);
        double detail = CaveNoise.fbm(this.seed + bridge.index * 103L, x, y, z, 0.18D, 2, 0.45D);

        double localRadius = Math.max(MIN_CONNECT_RADIUS, bridge.radius + large * 0.65D + detail * 0.25D);
        return zoneFromDistance(distanceSqr, localRadius);
    }

    private static CaveZone zoneFromDistance(double distanceSqr, double radius) {
        if (distanceSqr <= radius * radius) {
            return CaveZone.AIR;
        }

        double inner = radius + INNER_EXTRA;
        if (distanceSqr <= inner * inner) {
            return CaveZone.INNER_SHELL;
        }

        double outer = radius + OUTER_EXTRA;
        if (distanceSqr <= outer * outer) {
            return CaveZone.OUTER_SHELL;
        }

        return CaveZone.NONE;
    }

    private Node[] createNodes() {
        int steps = Math.max(2, (int) Math.ceil(horizontalDistance(this.start, this.end) / MAX_STEP_HORIZONTAL_DISTANCE));
        steps = Math.max(steps, Math.abs(this.start.getY() - this.end.getY()) / 5);

        Node[] result = new Node[steps + 1];
        double heading = Math.atan2(this.end.getZ() - this.start.getZ(), this.end.getX() - this.start.getX());

        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            double fade = Math.sin(t * Math.PI);

            double x = this.start.getX() + (this.end.getX() - this.start.getX()) * t;
            double y = this.start.getY() + (this.end.getY() - this.start.getY()) * t;
            double z = this.start.getZ() + (this.end.getZ() - this.start.getZ()) * t;

            double sideAngle = heading + Math.PI / 2.0D;

            double side = CaveNoise.fbm(this.seed + 100L, i, x, z, 0.31D, 2, 0.5D) * this.curve * fade;
            double vertical = CaveNoise.fbm(this.seed + 200L, i, x, y, 0.28D, 2, 0.5D) * this.curve * 0.75D * fade;
            double forward = CaveNoise.fbm(this.seed + 300L, i, y, z, 0.25D, 2, 0.5D) * this.curve * 0.35D * fade;

            x += Math.cos(sideAngle) * side + Math.cos(heading) * forward;
            z += Math.sin(sideAngle) * side + Math.sin(heading) * forward;
            y += vertical;

            double nodeRadius = Math.max(
                    MIN_CONNECT_RADIUS,
                    this.radius + CaveNoise.fbm(this.seed + 400L, i, x, z, 0.45D, 2, 0.5D) * 0.75D
            );

            result[i] = new Node(i, x + 0.5D, y + 0.5D, z + 0.5D, nodeRadius);
        }

        return result;
    }

    private Bridge[] createBridges() {
        Bridge[] result = new Bridge[Math.max(0, this.nodes.length - 1)];

        for (int i = 0; i < result.length; i++) {
            Node a = this.nodes[i];
            Node b = this.nodes[i + 1];
            double radius = Math.max(MIN_CONNECT_RADIUS, Math.min(a.radius, b.radius) * 0.92D);
            result[i] = new Bridge(i, a, b, radius);
        }

        return result;
    }

    private MoonCaveBounds createBounds() {
        MoonCaveBounds bounds = new MoonCaveBounds();

        for (Node node : this.nodes) {
            int p = (int) Math.ceil(node.radius + OUTER_EXTRA + 6.0D);
            bounds.include(
                    (int) Math.floor(node.x - p),
                    (int) Math.floor(node.y - p),
                    (int) Math.floor(node.z - p),
                    (int) Math.ceil(node.x + p),
                    (int) Math.ceil(node.y + p),
                    (int) Math.ceil(node.z + p)
            );
        }

        return bounds;
    }

    private static double horizontalDistance(BlockPos a, BlockPos b) {
        int dx = a.getX() - b.getX();
        int dz = a.getZ() - b.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    private record Node(int index, double x, double y, double z, double radius) {
    }

    private static final class Bridge {
        private final int index;
        private final Node a;
        private final Node b;
        private final double radius;

        private Bridge(int index, Node a, Node b, double radius) {
            this.index = index;
            this.a = a;
            this.b = b;
            this.radius = radius;
        }

        private double distanceSqr(double px, double py, double pz) {
            double abx = this.b.x - this.a.x;
            double aby = this.b.y - this.a.y;
            double abz = this.b.z - this.a.z;

            double apx = px - this.a.x;
            double apy = py - this.a.y;
            double apz = pz - this.a.z;

            double lengthSqr = abx * abx + aby * aby + abz * abz;

            if (lengthSqr <= 0.0001D) {
                double dx = px - this.a.x;
                double dy = py - this.a.y;
                double dz = pz - this.a.z;
                return dx * dx + dy * dy + dz * dz;
            }

            double t = (apx * abx + apy * aby + apz * abz) / lengthSqr;
            t = Math.max(0.0D, Math.min(1.0D, t));

            double closestX = this.a.x + abx * t;
            double closestY = this.a.y + aby * t;
            double closestZ = this.a.z + abz * t;

            double dx = px - closestX;
            double dy = py - closestY;
            double dz = pz - closestZ;

            return dx * dx + dy * dy + dz * dz;
        }
    }
}