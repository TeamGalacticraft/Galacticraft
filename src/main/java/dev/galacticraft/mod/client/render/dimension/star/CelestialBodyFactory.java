/*
 * Copyright (c) 2019-2025 Team Galacticraft
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