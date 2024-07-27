package dev.galacticraft.mod.world.ships;

public class CollisionResult {
    private final boolean intersects;
    private final Vec3d collisionNormal;
    private final double penetrationDepth;

    public CollisionResult(boolean intersects, Vec3d collisionNormal, double penetrationDepth) {
        this.intersects = intersects;
        this.collisionNormal = collisionNormal;
        this.penetrationDepth = penetrationDepth;
    }

    public boolean intersects() {
        return intersects;
    }

    public Vec3d getCollisionNormal() {
        return collisionNormal;
    }

    public double getPenetrationDepth() {
        return penetrationDepth;
    }
}