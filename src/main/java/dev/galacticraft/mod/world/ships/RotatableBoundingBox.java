package dev.galacticraft.mod.world.ships;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RotatableBoundingBox {
    private List<Vec3d[]> corners;
    private Quaternion rotation;
    private Vec3d centerOfMass;

    public RotatableBoundingBox(Vec3d min, Vec3d max, Quaternion rotation, Vec3d centerOfMass) {
        this.rotation = rotation;
        this.centerOfMass = centerOfMass;
        this.corners = new ArrayList<>();
        this.corners.add(computeCorners(min, max));
        rotate();
    }

    public RotatableBoundingBox(AABB aabb)
    {
        this.rotation = Quaternion.fromEuler(0, 0, 0);
        this.centerOfMass = new Vec3d(0, 0, 0);
        this.corners = new ArrayList<>();
        this.corners.add(computeCorners(new Vec3d(aabb.minX, aabb.minY, aabb.minZ), new Vec3d(aabb.maxX, aabb.maxY, aabb.maxZ)));
    }

    public RotatableBoundingBox()
    {
        this.rotation = Quaternion.fromEuler(0, 0, 0);
        this.centerOfMass = new Vec3d(0, 0, 0);
        this.corners = new ArrayList<>();
    }

    private Vec3d[] computeCorners(Vec3d min, Vec3d max) {
        return new Vec3d[]{
                new Vec3d(min.x, min.y, min.z),
                new Vec3d(min.x, min.y, max.z),
                new Vec3d(min.x, max.y, min.z),
                new Vec3d(min.x, max.y, max.z),
                new Vec3d(max.x, min.y, min.z),
                new Vec3d(max.x, min.y, max.z),
                new Vec3d(max.x, max.y, min.z),
                new Vec3d(max.x, max.y, max.z)
        };
    }

    public void rotate() {
        for (Vec3d[] cornerSet : corners) {
            // Translate corners to origin
            for (int i = 0; i < cornerSet.length; i++) {
                cornerSet[i] = cornerSet[i].subtract(centerOfMass);
            }

            // Apply rotation
            for (int i = 0; i < cornerSet.length; i++) {
                cornerSet[i] = applyRotation(cornerSet[i], rotation);
            }

            // Translate corners back to original position
            for (int i = 0; i < cornerSet.length; i++) {
                cornerSet[i] = cornerSet[i].add(centerOfMass);
            }
        }
    }

    private Vec3d applyRotation(Vec3d vec, Quaternion rotation) {
        Quaternion pointQuat = new Quaternion((float) vec.x, (float) vec.y, (float) vec.z, 0);
        Quaternion resultQuat = rotation.multiply(pointQuat).multiply(rotation.conjugate());
        return new Vec3d(resultQuat.x, resultQuat.y, resultQuat.z);
    }

    // Getters
    public List<Vec3d[]> getCorners() {
        return this.corners;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public Vec3d getCenterOfMass() {
        return centerOfMass;
    }

    // Setters
    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
        rotate(); // Reapply rotation when the rotation is changed
    }

    public void setCenterOfMass(Vec3d centerOfMass) {
        this.centerOfMass = centerOfMass;
        rotate(); // Reapply rotation when the center of mass is changed
    }

    public void transform(Vec3d translation) {
        for (Vec3d[] cornerSet : corners) {
            for (int i = 0; i < cornerSet.length; i++) {
                cornerSet[i] = cornerSet[i].add(translation);
            }
        }
        centerOfMass = centerOfMass.add(translation);
    }

    private Vec2d projectOntoAxis(Vec3d axis) {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        for (Vec3d[] cornerSet : corners) {
            for (Vec3d corner : cornerSet) {
                double projection = corner.dot(axis);
                if (projection < min) {
                    min = projection;
                }
                if (projection > max) {
                    max = projection;
                }
            }
        }

        return new Vec2d(min, max);
    }

    private boolean overlaps(Vec2d projection1, Vec2d projection2) {
        return projection1.x < projection2.y && projection2.x < projection1.y;
    }

    public CollisionResult intersects(RotatableBoundingBox other, Vec3d velocity) {
        return null;
    }

    public double distanceToPointAlongNormal(Vec3d point, Vec3d normal) {
        Vec3d closestPoint = this.getClosestPointOnBounds(point);
        return closestPoint.subtract(point).dot(normal);
    }

    public Vec3d getClosestPointOnBounds(Vec3d point) {
        Vec3d[] minmax = findMinMax();
        Vec3d min = minmax[0];
        Vec3d max = minmax[1];

        double x = Math.max(min.x, Math.min(point.x, max.x));
        double y = Math.max(min.y, Math.min(point.y, max.y));
        double z = Math.max(min.z, Math.min(point.z, max.z));

        return new Vec3d(x, y, z);
    }

    public void visualiseParticleBox(ClientLevel level, SimpleParticleType type) {
        for (Vec3d[] cornerSet : this.getCorners()) {
            for (Vec3d corner : cornerSet) {
                level.addParticle(type, corner.x, corner.y, corner.z, 0.0, 0.0, 0.0);
            }
        }
    }

    public String toString() {
        Vec3d[] minmax = findMinMax();
        return "\nMinimum: " + minmax[0] + "\nMaximum: " + minmax[1] + "\nRotation: " + this.getRotation();
    }

    public Vec3d[] findMinMax() {
        double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;

        for (Vec3d[] cornerSet : corners) {
            for (Vec3d corner : cornerSet) {
                if (corner.x < minX) minX = corner.x;
                if (corner.x > maxX) maxX = corner.x;
                if (corner.y < minY) minY = corner.y;
                if (corner.y > maxY) maxY = corner.y;
                if (corner.z < minZ) minZ = corner.z;
                if (corner.z > maxZ) maxZ = corner.z;
            }
        }

        return new Vec3d[]{
                new Vec3d(minX, minY, minZ),
                new Vec3d(maxX, maxY, maxZ)
        };
    }

    private double calculatePenetrationDepth(RotatableBoundingBox other, Vec3d direction) {
        Vec2d thisProjection = this.projectOntoAxis(direction);
        Vec2d otherProjection = other.projectOntoAxis(direction);

        return Math.min(thisProjection.y, otherProjection.y) - Math.max(thisProjection.x, otherProjection.x);
    }

    public void add(Vec3d min, Vec3d max) {
        Vec3d[] newCorners = computeCorners(min, max);

        // Translate new corners to origin
        for (int i = 0; i < newCorners.length; i++) {
            newCorners[i] = newCorners[i].subtract(centerOfMass);
        }

        // Apply rotation
        for (int i = 0; i < newCorners.length; i++) {
            newCorners[i] = applyRotation(newCorners[i], rotation);
        }

        // Translate new corners back to original position
        for (int i = 0; i < newCorners.length; i++) {
            newCorners[i] = newCorners[i].add(centerOfMass);
        }

        corners.add(newCorners);
    }

}
