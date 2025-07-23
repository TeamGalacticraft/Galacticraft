package dev.galacticraft.mod.client.render.dimension.star.data;

/**
 * Star implementation of CelestialBody.
 */
public class StarData extends CelestialBody {
    private double brightness;

    public StarData(int x, int y, int z, double size, double rotation, double brightness) {
        super(x, y, z, size, rotation, CelestialBodyType.STAR);
        this.brightness = brightness;
    }

    public double getBrightness() {
        return brightness;
    }

    public void setBrightness(double brightness) {
        this.brightness = brightness;
    }
}