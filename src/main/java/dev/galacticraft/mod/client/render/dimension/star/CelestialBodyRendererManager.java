package dev.galacticraft.mod.client.render.dimension.star;

import dev.galacticraft.mod.client.render.dimension.star.data.CelestialBody;
import dev.galacticraft.mod.client.render.dimension.star.data.CelestialBodyType;
import dev.galacticraft.mod.client.render.dimension.star.display.CelestialBodyRenderer;
import dev.galacticraft.mod.client.render.dimension.star.display.PlanetRenderer;
import dev.galacticraft.mod.client.render.dimension.star.display.StarRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import org.joml.SimplexNoise;
import org.joml.Vector3d;

import java.util.*;

/**
 * Manager class for rendering multiple celestial bodies (stars and planets) in a galaxy.
 * Uses the Composite and Factory design patterns to manage and render different types of celestial bodies.
 */
public class CelestialBodyRendererManager {
    // Factory for creating different types of celestial bodies
    private final CelestialBodyFactory factory;

    // Map to store celestial bodies by type
    private final Map<CelestialBodyType, List<CelestialBody>> celestialBodies;

    // Renderers for different types of celestial bodies
    private final Map<CelestialBodyType, CelestialBodyRenderer> renderers;

    // Geographical solar position for relative rendering of celestial bodies
    private GeographicalSolarPosition solarPosition;

    private CelestialBodyRendererManager() {

        this.factory = new CelestialBodyFactory();
        this.celestialBodies = new HashMap<>();
        this.renderers = new HashMap<>();
        this.solarPosition = GeographicalSolarPosition.getInstance();

        // Initialize collections for each celestial body type
        for (CelestialBodyType type : CelestialBodyType.values()) {
            celestialBodies.put(type, new ArrayList<>());
        }

        // Register default renderers
        renderers.put(CelestialBodyType.STAR, new StarRenderer());
        renderers.put(CelestialBodyType.PLANET, new PlanetRenderer());

        // FIXME: VERY TEMPORARY, we should setup from a generated map of stars or something
        this.setStarPositions();
    }

    // TODO: temp
    public void setStarPositions() {
        final Random random = new Random(27893L);
        final int starCount = 20000;
        final int size = 850;

        // Worley noise parameters
        int numPoints = 32;
        Vector3d[] points = new Vector3d[numPoints];
        for (int i = 0; i < numPoints; i++) {
            points[i] = new Vector3d(
                    random.nextInt(size * 2) - size,
                    random.nextInt(size * 2) - size,
                    random.nextInt(size * 2) - size
            );
        }

        for (int i = 0; i < starCount; i++) {
            // Generate base position
            int x = random.nextInt((size * 2) + 1) - size;
            int y = random.nextInt((size * 2) + 1) - size;
            int z = random.nextInt((size * 2) + 1) - size;

            // Perlin noise influence
            double noise = (SimplexNoise.noise(x * 0.005F, y * 0.005F, z * 0.005F) + 1) * 0.5;

            // Find closest Worley point
            double minDist = Double.MAX_VALUE;
            for (Vector3d p : points) {
                double dx = x - p.x;
                double dy = y - p.y;
                double dz = z - p.z;
                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                minDist = Math.min(minDist, dist);
            }

            // Only place stars where Worley noise is high and Perlin noise is above threshold
            if (minDist > 100 && noise > 0.4) {
                this.addCelestialBody(
                        CelestialBodyType.STAR,
                        x, y, z,
                        random.nextFloat(0.3f) + 1,
                        random.nextDouble(360) + 1
                );
            }
        }
    }
    /**
     * Adds a celestial body to the manager.
     * 
     * @param type The type of celestial body
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @param size Size of the celestial body
     * @param rotation Rotation of the celestial body
     * @return The created celestial body
     */
    public CelestialBody addCelestialBody(CelestialBodyType type, int x, int y, int z, double size, double rotation) {
        CelestialBody body = factory.createCelestialBody(type, x, y, z, size, rotation);
        celestialBodies.get(type).add(body);
        return body;
    }

    /**
     * Removes a celestial body from the manager.
     * 
     * @param body The celestial body to remove
     * @return true if the body was removed, false otherwise
     */
    public boolean removeCelestialBody(CelestialBody body) {
        return celestialBodies.get(body.getType()).remove(body);
    }

    /**
     * Registers a custom renderer for a specific celestial body type.
     * 
     * @param type The type of celestial body
     * @param renderer The renderer to use
     */
    public void registerRenderer(CelestialBodyType type, CelestialBodyRenderer renderer) {
        renderers.put(type, renderer);
    }

    /**
     * Gets all celestial bodies of a specific type.
     * 
     * @param type The type of celestial body
     * @return List of celestial bodies of the specified type
     */
    public List<CelestialBody> getCelestialBodiesByType(CelestialBodyType type) {
        return new ArrayList<>(celestialBodies.get(type));
    }

    /**
     * Gets all celestial bodies.
     * 
     * @return List of all celestial bodies
     */
    public List<CelestialBody> getAllCelestialBodies() {
        List<CelestialBody> allBodies = new ArrayList<>();
        for (List<CelestialBody> bodies : celestialBodies.values()) {
            allBodies.addAll(bodies);
        }
        return allBodies;
    }

    /**
     * Renders all celestial bodies.
     */
    public void render(WorldRenderContext worldRenderContext) {
        // Render each type of celestial body with its corresponding renderer
        for (CelestialBodyType type : CelestialBodyType.values()) {
            CelestialBodyRenderer renderer = renderers.get(type);
            if (renderer != null) {
                List<CelestialBody> bodies = celestialBodies.get(type);
                renderer.renderAll(bodies, worldRenderContext);
            }
        }
    }

    /**
     * Gets the geographical solar position used for relative rendering.
     * 
     * @return The geographical solar position
     */
    public GeographicalSolarPosition getSolarPosition() {
        return solarPosition;
    }

    /**
     * Sets the geographical solar position used for relative rendering.
     * 
     * @param solarPosition The new geographical solar position
     */
    public void setSolarPosition(GeographicalSolarPosition solarPosition) {
        this.solarPosition = solarPosition;
    }

    /**
     * Updates the geographical solar position with new coordinates.
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     * @param z The z coordinate
     */
    public void updateSolarPosition(double x, double y, double z) {
        this.solarPosition.setCameraPositions(x, y, z);
    }

    private static final CelestialBodyRendererManager INSTANCE = new CelestialBodyRendererManager();

    // TODO: we should ideally have multiple world spaces
    //  maybe we want to render a far off solar system in a addon?
    public static CelestialBodyRendererManager getInstance() {
        return INSTANCE;
    }
}
