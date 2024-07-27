package dev.galacticraft.mod.world.ships;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.swing.*;

public class ParticleHelper {
    /**
     * Spawns particles at the corners of the given bounding box.
     *
     * @param level       the world where the particles should be spawned
     * @param boundingBox the bounding box
     */
    public static void spawnBoundingBoxParticles(ClientLevel level, RotatableBoundingBox boundingBox, SimpleParticleType type) {
        for (Vec3d[] cornerSet : boundingBox.getCorners()) {
            for (Vec3d corner : cornerSet) {
                level.addParticle(type, corner.x, corner.y, corner.z, 0.0, 0.0, 0.0);
            }
        }
    }

    /**
     * Gets the corners of the given bounding box.
     *
     * @param boundingBox the bounding box
     * @return an array of the corners
     */
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

    public static Vec3d[] getBoundingBoxCorners(Vec3d blockPos) {
        return new Vec3d[]{
                new Vec3d(blockPos.x, blockPos.y, blockPos.z),
                new Vec3d(blockPos.x + 1, blockPos.y, blockPos.z),
                new Vec3d(blockPos.x, blockPos.y + 1, blockPos.z),
                new Vec3d(blockPos.x, blockPos.y, blockPos.z + 1),
                new Vec3d(blockPos.x + 1, blockPos.y + 1, blockPos.z),
                new Vec3d(blockPos.x, blockPos.y + 1, blockPos.z + 1),
                new Vec3d(blockPos.x + 1, blockPos.y, blockPos.z + 1),
                new Vec3d(blockPos.x + 1, blockPos.y + 1, blockPos.z + 1)
        };
    }
}
