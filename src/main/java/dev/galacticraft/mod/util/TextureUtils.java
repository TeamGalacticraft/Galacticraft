package dev.galacticraft.mod.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

public class TextureUtils {
    public static int getAverageColor(ResourceLocation textureLocation) {
        Optional<Resource> resourceOptional = Minecraft.getInstance().getResourceManager().getResource(textureLocation);
        if (resourceOptional.isEmpty()) {
            throw new RuntimeException("Failed to find resource: " + textureLocation);
        }

        try (InputStream is = resourceOptional.get().open()) {
            BufferedImage image = ImageIO.read(is);
            return calculateAverageColor(image);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image: " + textureLocation, e);
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
                if (rgb != 0)
                {
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

