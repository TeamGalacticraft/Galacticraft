package dev.galacticraft.mod.client.render.dimension.star;

/**
 * Represents the three-dimensional position of a celestial body in space.
 * Used for rendering other astronomical objects in the space environment.
 */
public class GeographicalSolarPosition {
    private double x;
    private double y;
    private double z;

    public GeographicalSolarPosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void setCameraPositions(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    private static final GeographicalSolarPosition instance = new GeographicalSolarPosition(0, 0, 0);

    public static GeographicalSolarPosition getInstance() {
        return instance;
    }

}

