package dev.galacticraft.mod.client.render.dimension.star.data;

/**
 * Planet implementation of CelestialBody.
 */
public class PlanetData extends CelestialBody {
    
    // TODO: we should retrieve some information about the planet (ex: orbit path)
    //  we could feed the apoapsis, periapsis, orbit degree, world tick, etc, and render from there

    public PlanetData(int x, int y, int z, double size, double rotation) {
        super(x, y, z, size, rotation, CelestialBodyType.PLANET);
    }
}