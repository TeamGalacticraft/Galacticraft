package dev.galacticraft.mod.world.ships;

import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;

public class VirtualBlock {
    private BlockState type;
    private Vec3d relativePosition;
    private double mass;
    private double restitution;
    private double friction;

    public VirtualBlock(BlockState type, Vec3d relativePosition)
    {
        this.type = type;
        this.relativePosition = relativePosition;
        this.mass = Masses.get(type);
        this.restitution = Restitutions.get(type);
        this.friction = Frictions.get(type);
    }


    public Vec3d getRelativePos() {
        return relativePosition;
    }
    public Vec3d getCenterPos() {
        return relativePosition.add(0.5);
    }
    public double getMass() {
        return this.mass;
    }

    public double getRestitution() {
        return this.restitution;
    }

    public double getFriction() {
        return this.friction;
    }
    public BlockState getState()
    {
        return type;
    }

    public double getHeight() {
        return 1;
    }

    public double getWidth() {
        return 1;
    }

    public double getDepth() {
        return 1;
    }

    public RotatableBoundingBox getBoundingBox(BlockGroup ship) {
        return new RotatableBoundingBox(
                this.getRelativePos().add(ship.getPosition()),
                this.getRelativePos().add(1).add(ship.getPosition()),
                ship.getRotation(),
                ship.getCenterOfMass().add(ship.getPosition())
        );
    }
}
