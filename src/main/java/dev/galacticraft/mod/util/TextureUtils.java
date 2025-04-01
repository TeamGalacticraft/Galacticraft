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

package dev.galacticraft.mod.util;

import dev.galacticraft.mod.mixin.client.SpriteContentAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TextureUtils {
    public static int getAverageColor(ResourceLocation textureLocation) {
        TextureAtlasSprite textureAtlasSprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(textureLocation);

        return calculateAverageColor(textureAtlasSprite);
    }

    private static int calculateAverageColor(TextureAtlasSprite image) {
        SpriteContents contents = image.contents();
        List<Vector3f> pixels = new ArrayList<>();

        for (int y = 0; y < contents.height(); y++) {
            for (int x = 0; x < contents.width(); x++) {
                int rgba = ((SpriteContentAccessor) contents).getOriginalImage().getPixelRGBA(x, y);
                int alpha = (rgba >> 24) & 0xFF;
                if (alpha < 64) continue;

                int red = rgba & 0xFF;
                int green = (rgba >> 8) & 0xFF;
                int blue = (rgba >> 16) & 0xFF;

                pixels.add(new Vector3f(red, green, blue));
            }
        }

        if (pixels.isEmpty()) return 0xFFFFFF;

        Vector3f dominant = kMeans(pixels, 3);
        return ((int)dominant.x << 16) | ((int)dominant.y << 8) | (int)dominant.z;
    }

    private static Vector3f kMeans(List<Vector3f> points, int k) {
        List<Vector3f> centroids = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < k; i++) {
            centroids.add(points.get(rand.nextInt(points.size())));
        }

        for (int iteration = 0; iteration < 10; iteration++) {
            List<List<Vector3f>> clusters = new ArrayList<>();
            for (int i = 0; i < k; i++) clusters.add(new ArrayList<>());

            for (Vector3f p : points) {
                int closest = 0;
                float minDist = Float.MAX_VALUE;
                for (int i = 0; i < k; i++) {
                    float dist = distanceSquared(p, centroids.get(i));
                    if (dist < minDist) {
                        minDist = dist;
                        closest = i;
                    }
                }
                clusters.get(closest).add(p);
            }

            for (int i = 0; i < k; i++) {
                centroids.set(i, average(clusters.get(i)));
            }
        }

        int largest = 0;
        for (int i = 1; i < k; i++) {
            if (centroids.get(i).length() > centroids.get(largest).length()) {
                largest = i;
            }
        }
        return centroids.get(largest);
    }

    private static float distanceSquared(Vector3f a, Vector3f b) {
        float dx = a.x - b.x;
        float dy = a.y - b.y;
        float dz = a.z - b.z;
        return dx * dx + dy * dy + dz * dz;
    }

    private static Vector3f average(List<Vector3f> points) {
        float r = 0, g = 0, b = 0;
        for (Vector3f p : points) {
            r += p.x;
            g += p.y;
            b += p.z;
        }
        int size = points.size();
        return new Vector3f(r / size, g / size, b / size);
    }
}

