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
