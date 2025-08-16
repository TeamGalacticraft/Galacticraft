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

import dev.galacticraft.mod.Constant;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * 3D Planet implementation of CelestialBody.
 * Supports textures for all 6 faces and opacity.
 */
public class Planet3DData extends CelestialBody {

    public enum Face {
        TOP, BOTTOM, NORTH, SOUTH, EAST, WEST
    }

    private final Map<Face, ResourceLocation> textures;
    private float opacity;

    /**
     * Creates a 3D planet with default textures and full opacity.
     */
    public Planet3DData(int x, int y, int z, double size, double rotation) {
        super(x, y, z, size, rotation, CelestialBodyType.PLANET3D);
        this.textures = new HashMap<>();
        // Set default texture for all faces
        for (Face face : Face.values()) {
            this.textures.put(face, Constant.CelestialBody.EARTH);
        }
        this.opacity = 1.0f; // Full opacity by default
    }

    /**
     * Creates a 3D planet with the same texture for all faces and specified opacity.
     */
    public Planet3DData(int x, int y, int z, double size, double rotation, ResourceLocation texture, float opacity) {
        super(x, y, z, size, rotation, CelestialBodyType.PLANET3D);
        this.textures = new HashMap<>();
        // Set the same texture for all faces
        for (Face face : Face.values()) {
            this.textures.put(face, texture);
        }
        this.opacity = opacity;
    }

    /**
     * Creates a 3D planet with different textures for each face and specified opacity.
     */
    public Planet3DData(int x, int y, int z, double size, double rotation, Map<Face, ResourceLocation> textures, float opacity) {
        super(x, y, z, size, rotation, CelestialBodyType.PLANET3D);
        this.textures = new HashMap<>(textures);
        // Set default texture for any missing faces
        for (Face face : Face.values()) {
            if (!this.textures.containsKey(face)) {
                this.textures.put(face, Constant.CelestialBody.EARTH);
            }
        }
        this.opacity = opacity;
    }

    /**
     * Gets the texture for a specific face.
     */
    public ResourceLocation getTexture(Face face) {
        return textures.getOrDefault(face, Constant.CelestialBody.EARTH);
    }

    /**
     * Sets the texture for a specific face.
     */
    public void setTexture(Face face, ResourceLocation texture) {
        textures.put(face, texture);
    }

    /**
     * Sets the same texture for all faces.
     */
    public void setTextureAll(ResourceLocation texture) {
        for (Face face : Face.values()) {
            textures.put(face, texture);
        }
    }

    /**
     * Gets the opacity of the planet.
     */
    public float getOpacity() {
        return opacity;
    }

    /**
     * Sets the opacity of the planet.
     * @param opacity Value between 0.0 (fully transparent) and 1.0 (fully opaque)
     */
    public void setOpacity(float opacity) {
        this.opacity = Math.max(0.0f, Math.min(1.0f, opacity)); // Clamp between 0 and 1
    }
}