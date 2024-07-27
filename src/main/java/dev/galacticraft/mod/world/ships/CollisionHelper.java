package dev.galacticraft.mod.world.ships;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static dev.galacticraft.mod.world.ships.ParticleHelper.getBoundingBoxCorners;
import static dev.galacticraft.mod.world.ships.Vec3d.applyRotation;

public class CollisionHelper {
    /**
     * Gets the collision shape of a block at a given position in the world.
     *
     * @param level the world where the block is located
     * @param pos the position of the block
     * @return the collision shape of the block
     */
    public static RotatableBoundingBox getBlockCollisionShape(ClientLevel level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        RotatableBoundingBox boundingBoxes = new RotatableBoundingBox();
        blockState.getCollisionShape(level, pos).toAabbs().forEach(aabb -> {
            AABB shape = aabb.move(pos);
            boundingBoxes.add(new Vec3d(shape.maxX, shape.maxY, shape.maxZ), new Vec3d(shape.maxX, shape.maxY, shape.maxZ));
        });
        return boundingBoxes;
    }

    public static List<RotatableBoundingBox> getBlockCollisionShapeTransformed(ClientLevel level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        List<RotatableBoundingBox> boundingBoxes = new ArrayList<>();
        blockState.getCollisionShape(level, pos).toAabbs().forEach(aabb -> {
            RotatableBoundingBox box = new RotatableBoundingBox(aabb);
            box.transform(new Vec3d(pos));
            boundingBoxes.add(box);
        });
        return boundingBoxes;
    }

    public static List<Vec3d> getPositionsInCube(double xSize, double ySize, double zSize)
    {
        List<Vec3d> positions = new ArrayList<>();
        for (int x = -Math.floorDiv((int) xSize, 2); x < xSize - Math.floorDiv((int) xSize, 2); x++) {
            for (int y = -Math.floorDiv((int) ySize, 2); y < ySize - Math.floorDiv((int) ySize, 2); y++) {
                for (int z = -Math.floorDiv((int) zSize, 2); z < zSize - Math.floorDiv((int) zSize, 2); z++)
                {
                    positions.add(new Vec3d(x, y, z));
                }
            }
        }
        return positions;
    }

    public static RotatableBoundingBox getCollisionBoxes(ClientLevel level, BlockPos minimum, BlockPos maximum) {
        RotatableBoundingBox collisionBoxes = new RotatableBoundingBox();
        for (int x = minimum.getX(); x <= maximum.getX(); x++) {
            for (int y = minimum.getY(); y <= maximum.getY(); y++) {
                for (int z = minimum.getZ(); z <= maximum.getZ(); z++) {
                    RotatableBoundingBox blockCollisionShape = getBlockCollisionShape(level, new BlockPos(x, y, z));

                    for (Vec3d[] corners : blockCollisionShape.getCorners()) {
                        double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
                        double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
                        double minZ = Double.POSITIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;
                        for (Vec3d corner : corners) {
                            if (corner.x < minX) minX = corner.x;
                            if (corner.x > maxX) maxX = corner.x;
                            if (corner.y < minY) minY = corner.y;
                            if (corner.y > maxY) maxY = corner.y;
                            if (corner.z < minZ) minZ = corner.z;
                            if (corner.z > maxZ) maxZ = corner.z;
                        }

                        collisionBoxes.add(new Vec3d(minX, minY, minZ), new Vec3d(maxX, maxY, maxZ));
                    }
                }
            }
        }

        return collisionBoxes;
    }
}
