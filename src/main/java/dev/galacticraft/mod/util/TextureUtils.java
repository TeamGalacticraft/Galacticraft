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

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class TextureUtils {
    public static int getAverageColor(ResourceLocation textureLocation) {
        if (Minecraft.getInstance().level == null) {
            System.out.println("NO LEVEL FOUND");
            return 0x00FFFF; // Default white color when the game is not fully loaded
        }

        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        if (resourceManager == null) {
            System.out.println("NO RESOURCE MANAGER FOUND");
            return 0xFF00FF; // Return default if ResourceManager isn't ready
        }

        Optional<Resource> resourceOptional = resourceManager.getResource(textureLocation);
        if (resourceOptional.isEmpty()) {
            System.out.println("RESOURCE OPTIONAL IS EMPTY");
            return 0xFFFF00; // Avoid throwing an exception, return default instead
        }

        try (InputStream is = resourceOptional.get().open()) {
            BufferedImage image = ImageIO.read(is);
            return calculateAverageColor(image);
        } catch (IOException e) {
            return 0xFFFFFF; // Return default if the texture couldn't be read
        }
    }

    private static int calculateAverageColor(BufferedImage image) {
        long sumRed = 0;
        long sumGreen = 0;
        long sumBlue = 0;
        int pixelCount = 0;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                int rgb = image.getRGB(x, y);
                if (rgb != 0) {
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;

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

