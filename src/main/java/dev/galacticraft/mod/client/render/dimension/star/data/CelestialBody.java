package dev.galacticraft.mod.client.render.dimension.star.data;

/**
 * Abstract base class for celestial bodies.
 * Uses the Template Method pattern for common functionality.
 */
public abstract class CelestialBody {
    protected double size;
    protected double rotation;
    protected int x;
    protected int y;
    protected int z;
    protected CelestialBodyType type;

    public CelestialBody(int x, int y, int z, double size, double rotation, CelestialBodyType type) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
        this.rotation = rotation;
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public double getSize() {
        return size;
    }

    public double getRotation() {
        return rotation;
    }

    public CelestialBodyType getType() {
        return type;
    }

    public void setPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }
}