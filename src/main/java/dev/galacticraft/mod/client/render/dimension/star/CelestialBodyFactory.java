/*
 * Copyright (c) 2019-2026 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.client.render.dimension.star;

import dev.galacticraft.mod.client.render.dimension.star.data.CelestialBody;
import dev.galacticraft.mod.client.render.dimension.star.data.CelestialBodyType;
import dev.galacticraft.mod.client.render.dimension.star.data.Planet3DData;
import dev.galacticraft.mod.client.render.dimension.star.data.PlanetData;
import dev.galacticraft.mod.client.render.dimension.star.data.StarData;
import net.minecraft.resources.ResourceLocation;

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
            case PLANET2D:
                return new PlanetData(x, y, z, size, rotation);
            case PLANET3D:
                return new Planet3DData(x, y, z, size, rotation);
            default:
                throw new IllegalArgumentException("Unknown celestial body type: " + type);
        }
    }

    /**
     * Creates a planet with a specific texture.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param size Size of the planet
     * @param rotation Rotation of the planet
     * @param texture Texture to use for the planet
     * @return The created planet
     */
    public PlanetData createPlanet(int x, int y, int z, double size, double rotation, ResourceLocation texture) {
        return new PlanetData(x, y, z, size, rotation, texture);
    }

    /**
     * Creates a 3D planet with the same texture for all faces and specified opacity.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param size Size of the planet
     * @param rotation Rotation of the planet
     * @param texture Texture to use for all faces of the planet
     * @param opacity Opacity of the planet (0.0f to 1.0f)
     * @return The created 3D planet
     */
    public Planet3DData create3DPlanet(int x, int y, int z, double size, double rotation, ResourceLocation texture, float opacity) {
        return new Planet3DData(x, y, z, size, rotation, texture, opacity);
    }

    /**
     * Creates a 3D planet with different textures for each face and specified opacity.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param size Size of the planet
     * @param rotation Rotation of the planet
     * @param textures Map of textures for each face of the planet
     * @param opacity Opacity of the planet (0.0f to 1.0f)
     * @return The created 3D planet
     */
    public Planet3DData create3DPlanet(int x, int y, int z, double size, double rotation,
                                      java.util.Map<Planet3DData.Face, ResourceLocation> textures, float opacity) {
        return new Planet3DData(x, y, z, size, rotation, textures, opacity);
    }
}
