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

public class TextureUtils {
    public static int getAverageColor(ResourceLocation textureLocation) {
        TextureAtlasSprite textureAtlasSprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(textureLocation);

        return calculateAverageColor(textureAtlasSprite);
    }

    private static int calculateAverageColor(TextureAtlasSprite image) {
        long sumRed = 0;
        long sumGreen = 0;
        long sumBlue = 0;
        int pixelCount = 0;

        SpriteContents contents = image.contents();

        for (int y = 0; y < contents.height(); y++) {
            for (int x = 0; x < contents.width(); x++) {

                int rgb = ((SpriteContentAccessor) contents).getOriginalImage().getPixelRGBA(x, y);
                if (rgb != 0) {
                    int red = rgb & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = (rgb >> 16) & 0xFF;

                    sumRed += red;
                    sumGreen += green;
                    sumBlue += blue;
                    pixelCount++;
                }
            }
        }

        int avgRed = (int) (sumRed / pixelCount);
        int avgGreen = (int) (sumGreen / pixelCount);
        int avgBlue = (int) (sumBlue / pixelCount);

        return (avgRed << 16) | (avgGreen << 8) | avgBlue;
    }
}

