package dev.galacticraft.mod.client.render.dimension.star;

import dev.galacticraft.mod.client.render.dimension.star.data.CelestialBody;
import dev.galacticraft.mod.client.render.dimension.star.data.CelestialBodyType;
import dev.galacticraft.mod.client.render.dimension.star.data.PlanetData;
import dev.galacticraft.mod.client.render.dimension.star.data.StarData;

/**
 * Factory for creating different types of celestial bodies.
 * Implements the Factory Method pattern.
 */
public class CelestialBodyFactory {
    public CelestialBody createCelestialBody(CelestialBodyType type, int x, int y, int z, double size, double rotation) {
        switch (type) {
            case STAR:
                float brightness = 1.0F;
                return new StarData(x, y, z, size, rotation, brightness);
            case PLANET:
                return new PlanetData(x, y, z, size, rotation);
            default:
                throw new IllegalArgumentException("Unknown celestial body type: " + type);
        }
    }
}