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

package dev.galacticraft.mod.client.render.dimension.star.display;

import dev.galacticraft.mod.client.render.dimension.star.data.CelestialBody;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

import java.util.List;

/**
 * Interface for celestial body renderers.
 * Implements the Strategy pattern.
 */
public interface CelestialBodyRenderer {

    void setupBufferPositions(List<CelestialBody> bodies);

    /**
     * Renders a single celestial body.
     */
    void render(CelestialBody body, WorldRenderContext worldRenderContext);

    /**
     * Renders a list of celestial bodies.
     */
    default void renderAll(List<CelestialBody> bodies, WorldRenderContext worldRenderContext) {
        // Setup buffer positions once for all bodies
        this.setupBufferPositions(bodies);

        // Render each body
        for (CelestialBody body : bodies) {
            this.render(body, worldRenderContext);
        }
    }
}
