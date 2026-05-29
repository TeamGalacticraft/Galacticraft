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

package dev.galacticraft.mod.client.util;

import dev.galacticraft.mod.content.item.CannedFoodItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public final class CannedFoodColorRegistry {
    private static final int FALLBACK_COLOR = 0xFFFFFF;
    private static final Map<Item, Integer> FOOD_COLORS = new HashMap<>();

    private CannedFoodColorRegistry() {
    }

    public static void init() {
        FOOD_COLORS.clear();

        for (Item item : BuiltInRegistries.ITEM) {
            if (CannedFoodItem.canAddToCan(item)) {
                FOOD_COLORS.put(item, calculateItemColor(item));
            }
        }

        CannedFoodItem.clearColorCache();
    }

    public static int getColor(Item item) {
        if (!CannedFoodItem.canAddToCan(item)) {
            return FALLBACK_COLOR;
        }

        return FOOD_COLORS.computeIfAbsent(item, CannedFoodColorRegistry::calculateItemColor);
    }

    public static boolean hasColor(Item item) {
        return FOOD_COLORS.containsKey(item);
    }

    public static void clear() {
        FOOD_COLORS.clear();
    }

    private static int calculateItemColor(Item item) {
        ItemStack stack = item.getDefaultInstance();
        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getItemRenderer()
                .getModel(stack, null, null, 0)
                .getParticleIcon();

        if (sprite == null) {
            return FALLBACK_COLOR;
        }

        return calculateDominantColor(sprite);
    }

    private static int calculateDominantColor(TextureAtlasSprite sprite) {
        Map<Integer, Integer> buckets = new HashMap<>();

        var image = sprite.contents().originalImage;
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgba = image.getPixelRGBA(x, y);

                int red = rgba & 0xFF;
                int green = rgba >> 8 & 0xFF;
                int blue = rgba >> 16 & 0xFF;
                int alpha = rgba >> 24 & 0xFF;

                if (alpha < 32) {
                    continue;
                }

                int max = Math.max(red, Math.max(green, blue));
                int min = Math.min(red, Math.min(green, blue));
                int saturation = max - min;

                if (max < 35 || saturation < 10) {
                    continue;
                }

                if (max > 245 && saturation < 20) {
                    continue;
                }

                int bucketRed = red / 16;
                int bucketGreen = green / 16;
                int bucketBlue = blue / 16;
                int bucket = bucketRed << 8 | bucketGreen << 4 | bucketBlue;

                buckets.merge(bucket, alpha, Integer::sum);
            }
        }

        if (buckets.isEmpty()) {
            return FALLBACK_COLOR;
        }

        int bestBucket = buckets.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0xFFFFFF);

        int red = ((bestBucket >> 8) & 0xF) * 16 + 8;
        int green = ((bestBucket >> 4) & 0xF) * 16 + 8;
        int blue = (bestBucket & 0xF) * 16 + 8;

        return red << 16 | green << 8 | blue;
    }
}