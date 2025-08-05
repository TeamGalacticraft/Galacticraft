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

package dev.galacticraft.mod.client.render.dimension.star.data;

import net.minecraft.resources.ResourceLocation;
import dev.galacticraft.mod.client.render.dimension.CelestialBodyTextures;

/**
 * Planet implementation of CelestialBody.
 */
public class PlanetData extends CelestialBody {

    private ResourceLocation texture;

    // TODO: we should retrieve some information about the planet (ex: orbit path)
    //  we could feed the apoapsis, periapsis, orbit degree, world tick, etc, and render from there

    public PlanetData(int x, int y, int z, double size, double rotation) {
        super(x, y, z, size, rotation, CelestialBodyType.PLANET2D);
        this.texture = CelestialBodyTextures.EARTH; // Default texture
    }

    public PlanetData(int x, int y, int z, double size, double rotation, ResourceLocation texture) {
        super(x, y, z, size, rotation, CelestialBodyType.PLANET2D);
        this.texture = texture;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
    }
}
