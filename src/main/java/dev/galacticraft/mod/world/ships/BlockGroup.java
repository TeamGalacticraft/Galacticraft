package dev.galacticraft.mod.world.ships;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;


public class BlockGroup {
    private Set<VirtualBlock> blocks;
    private Vec3d velocity;
    private Vec3d thrust;
    private Vec3d position;
    private Vec3d previousPosition;
    private double mass;
    private Vec3d centerOfMass;
    private double momentOfInertia;
    private Vec3d angularVelocity;
    private boolean needsRecalculation;

    private Quaternion rotation;
    private static final double RESTITUTION = 0.8;


    public BlockGroup(BlockState initialBlock, Vec3d initialPosition)
    {
        this.blocks = new HashSet<>();
        this.velocity = new Vec3d(0, 0, 0);
        this.angularVelocity = new Vec3d(0, 0, 0);
        this.thrust = new Vec3d(0, 0, 0);
        this.position = initialPosition;
        this.previousPosition = this.position;
        this.mass = 0;
        this.centerOfMass = new Vec3d().zero().add(0.5);
        this.rotation = Quaternion.fromEuler(0, 0, 0);

        addBlock(initialBlock, initialPosition);
        this.needsRecalculation = true;
        calculateProperties();
//        addBlock(initialBlock, initialPosition.add(1, 0, 0));
//        addBlock(initialBlock, initialPosition.add(2, 0, 0));
//        addBlock(initialBlock, initialPosition.add(2, 0, 1));
//        addBlock(initialBlock, initialPosition.add(2, 0, 2));
    }

    private void calculateProperties() {
        calculateMomentOfInertia();
        this.needsRecalculation = false;
    }

    private void calculateMomentOfInertia() {
        this.momentOfInertia = 0;
        for (VirtualBlock block : blocks) {
            double blockMass = block.getMass();
            Vec3d blockPos = block.getRelativePos();
            Vec3d r = blockPos.subtract(this.centerOfMass);

            // Assuming block dimensions are width, height, and depth
            double I_x = (1.0 / 12.0) * blockMass * (block.getHeight() * block.getHeight() + block.getDepth() * block.getDepth());
            double I_y = (1.0 / 12.0) * blockMass * (block.getWidth() * block.getWidth() + block.getDepth() * block.getDepth());
            double I_z = (1.0 / 12.0) * blockMass * (block.getWidth() * block.getWidth() + block.getHeight() * block.getHeight());

            // Calculate distance from the block to the center of mass
            double distanceSquared = r.lengthSquared();

            // Apply the parallel axis theorem
            this.momentOfInertia += (I_x + I_y + I_z) + blockMass * distanceSquared;
        }
    }

    public void addBlock(BlockState type, Vec3d blockPosition) {
        double blockMass = Masses.get(type);

        centerOfMass = centerOfMass.multiply(mass).add(blockPosition.subtract(this.position).add(0.5).multiply(blockMass)).multiply(1.0 / (mass + blockMass));

        mass += blockMass;

        blocks.add(new VirtualBlock(type, blockPosition.subtract(this.position)));
        needsRecalculation = true;
    }

    public void applyThrust(Vec3d force, Vec3d relativePosition) {
        Vec3d acceleration = force.multiply(1.0 / this.mass);
        this.velocity = this.velocity.add(acceleration);

        Vec3d r = relativePosition.subtract(this.centerOfMass);
        Vec3d torque = r.crossProduct(force);

        Vec3d angularAcceleration = torque.multiply(1.0 / this.momentOfInertia);
        this.angularVelocity = this.angularVelocity.add(angularAcceleration);
    }

    public void updatePhysics(double deltaTime, Minecraft context) {
        if (needsRecalculation) {
            calculateProperties();
        }

        this.previousPosition = this.position;

        // Apply gravity
        applyGravity(deltaTime);

        // Update linear position
        this.position = this.position.add(this.velocity.multiply(deltaTime));

        // Update rotation
//        Quaternion angularVelocityQuat = new Quaternion(this.angularVelocity.x * deltaTime, this.angularVelocity.y * deltaTime, this.angularVelocity.z * deltaTime, 0);
//        this.rotation = this.rotation.add(angularVelocityQuat.multiply(this.rotation).multiply(0.5)).normalize();



        handleCollisions(deltaTime, context);
    }

    private void applyGravity(double deltaTime)
    {
        Vec3d gravity = new Vec3d(0, -9.81, 0);
        this.velocity = this.velocity.add(gravity.multiply(deltaTime));
    }


    private void handleCollisions(double deltaTime, Minecraft context) {
        for (VirtualBlock block : this.getBlocks())
        {
            Vec3d realBlockPos = block.getRelativePos().add(this.getPosition());
            RotatableBoundingBox boundingBox = block.getBoundingBox(this);
            RotatableBoundingBox collisionBoxes = CollisionHelper.getCollisionBoxes(context.level, realBlockPos.subtract(2).toBlockPos(), realBlockPos.add(1).toBlockPos());

            boundingBox.visualiseParticleBox(context.level, ParticleTypes.ELECTRIC_SPARK);
            collisionBoxes.visualiseParticleBox(context.level, ParticleTypes.FLAME);

            System.out.println(collisionBoxes.getCorners().size());
        }
    }

    private void setPosition(Vec3d newPosition) {
        this.position = newPosition;
    }

    private void setVelocity(Vec3d newVelocity) {
        this.velocity = newVelocity;
    }

    private ConcurrentHashMap<String, BlockGroup> getNearbyGroups() {
        // Implement logic to get nearby PhysicalBlockGroup objects
        return BlockGroupRenderer.getGroups();
    }

    public Vec3d interpolatePosition(float tickDelta) {
        return this.previousPosition.add(this.position.subtract(this.previousPosition).scale(tickDelta));
    }

    public Set<VirtualBlock> getBlocks() {
        return this.blocks;
    }

    public Vec3d getPosition() {
        return position;
    }

    public Vec3d getVelocity() {
        return velocity;
    }

    public double getMass() {
        return mass;
    }

    public Vec3d getCenterOfMass() {
        return centerOfMass;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public void rotate(Vec3d eulerRotation) {
        System.out.println("old rotation: " + this.rotation);
        this.rotation = Quaternion.fromEuler(eulerRotation);
        System.out.println("new rotation: " + this.rotation);
    }


    public void translate(Vec3d translation)
    {
        System.out.println("translated block group by: " + translation);
        this.position = this.position.add(translation);
    }
}
