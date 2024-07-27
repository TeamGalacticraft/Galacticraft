package dev.galacticraft.mod.world.ships;

import net.minecraft.world.phys.AABB;

public class AABBHelper {
    // Rotates an AABB around a specified center of mass by a given quaternion
    public static AABB rotateAABB(AABB box, Vec3d centerOfMass, Quaternion rotation) {
        // Define the corners of the AABB
        Vec3d[] corners = getBoundingBoxCorners(box);

        // Translate corners to the origin (center of mass)
        for (int i = 0; i < corners.length; i++) {
            corners[i] = corners[i].subtract(centerOfMass);
        }

        // Rotate each corner
        for (int i = 0; i < corners.length; i++) {
            corners[i] = applyRotation(corners[i], rotation);
        }

        // Translate corners back to the original position
        for (int i = 0; i < corners.length; i++) {
            corners[i] = corners[i].add(centerOfMass);
        }

        // Find the new min and max coordinates
        Vec3d min = corners[0];
        Vec3d max = corners[0];

        for (Vec3d corner : corners) {
            min = new Vec3d(
                    Math.min(min.x, corner.x),
                    Math.min(min.y, corner.y),
                    Math.min(min.z, corner.z)
            );
            max = new Vec3d(
                    Math.max(max.x, corner.x),
                    Math.max(max.y, corner.y),
                    Math.max(max.z, corner.z)
            );
        }

        return new AABB(min.toVec3(), max.toVec3());
    }

    // Applies quaternion rotation to a vector
    public static Vec3d applyRotation(Vec3d vec, Quaternion rotation) {
        Quaternion pointQuat = new Quaternion((float) vec.x, (float) vec.y, (float) vec.z, 0);
        Quaternion rotatedQuat = rotation.multiply(pointQuat).multiply(rotation.conjugate());
        return new Vec3d(rotatedQuat.x, rotatedQuat.y, rotatedQuat.z);
    }

    // Retrieves the corners of the bounding box
    public static Vec3d[] getBoundingBoxCorners(AABB boundingBox) {
        return new Vec3d[]{
                new Vec3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ),
                new Vec3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ),
                new Vec3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ),
                new Vec3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ),
                new Vec3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ),
                new Vec3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ),
                new Vec3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ),
                new Vec3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ)
        };
    }

}
