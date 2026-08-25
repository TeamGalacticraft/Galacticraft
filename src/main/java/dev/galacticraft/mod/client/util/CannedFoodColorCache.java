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

import dev.galacticraft.api.component.GCDataComponents;
import dev.galacticraft.mod.content.item.CannedFoodItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CannedFoodColorCache {
    private static final int FALLBACK_COLOR = 0xFFFFFF;

    private static final int MIN_ALPHA = 64;
    private static final int MIN_BRIGHTNESS = 35;
    private static final int MIN_SATURATION = 10;
    private static final int WHITE_THRESHOLD = 245;
    private static final int WHITE_MAX_SATURATION = 20;
    private static final int COLOR_BUCKET_SIZE = 16;

    private static final Map<Item, Integer> FOOD_COLORS = new ConcurrentHashMap<>();
    private static final Map<CannedFoodColorKey, Integer> COLOR_CACHE = new ConcurrentHashMap<>();

    private CannedFoodColorCache() {
    }

    /**
     * Clears cached generated food and can colours.
     *
     * <p>Call this after resource reloads if item textures may have changed.</p>
     */
    public static void clear() {
        FOOD_COLORS.clear();
        COLOR_CACHE.clear();
    }

    public static int getCanColor(ItemStack stack) {
        Integer override = stack.get(GCDataComponents.COLOR);
        if (override != null) {
            return override;
        }

        List<ItemStack> contents = CannedFoodItem.getContents(stack);
        if (contents.isEmpty()) {
            return FALLBACK_COLOR;
        }

        CannedFoodColorKey key = CannedFoodColorKey.of(contents);
        return COLOR_CACHE.computeIfAbsent(key, CannedFoodColorCache::calculateCanColor);
    }

    private static int calculateCanColor(CannedFoodColorKey key) {
        if (key.entries().isEmpty()) {
            return FALLBACK_COLOR;
        }

        long sumRed = 0L;
        long sumGreen = 0L;
        long sumBlue = 0L;
        int totalCount = 0;

        for (CannedFoodColorKey.Entry entry : key.entries()) {
            int color = getFoodColor(entry.item());
            int count = entry.count();

            sumRed += (long) ((color >> 16) & 0xFF) * count;
            sumGreen += (long) ((color >> 8) & 0xFF) * count;
            sumBlue += (long) (color & 0xFF) * count;
            totalCount += count;
        }

        if (totalCount <= 0) {
            return FALLBACK_COLOR;
        }

        int red = (int) (sumRed / totalCount);
        int green = (int) (sumGreen / totalCount);
        int blue = (int) (sumBlue / totalCount);

        return red << 16 | green << 8 | blue;
    }

    private static int getFoodColor(Item item) {
        if (!CannedFoodItem.canAddToCan(item)) {
            return FALLBACK_COLOR;
        }

        return FOOD_COLORS.computeIfAbsent(item, CannedFoodColorCache::calculateItemColor);
    }

    private static int calculateItemColor(Item item) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        if (itemRenderer == null) {
            return FALLBACK_COLOR;
        }

        ItemStack stack = item.getDefaultInstance();

        TextureAtlasSprite sprite = itemRenderer
                .getModel(stack, null, null, 0)
                .getParticleIcon();

        if (sprite == null) {
            return FALLBACK_COLOR;
        }

        return calculateDominantColor(sprite);
    }

    private static int calculateDominantColor(TextureAtlasSprite sprite) {
        Map<Integer, ColorBucket> buckets = new HashMap<>();

        var image = sprite.contents().ori   ginalImage;
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgba = image.getPixelRGBA(x, y);

                int red = rgba & 0xFF;
                int green = rgba >> 8 & 0xFF;
                int blue = rgba >> 16 & 0xFF;
                int alpha = rgba >> 24 & 0xFF;

                if (alpha < MIN_ALPHA) {
                    continue;
                }

                int max = Math.max(red, Math.max(green, blue));
                int min = Math.min(red, Math.min(green, blue));
                int saturation = max - min;

                if (max < MIN_BRIGHTNESS || saturation < MIN_SATURATION) {
                    continue;
                }

                if (max > WHITE_THRESHOLD && saturation < WHITE_MAX_SATURATION) {
                    continue;
                }

                int bucketRed = red / COLOR_BUCKET_SIZE;
                int bucketGreen = green / COLOR_BUCKET_SIZE;
                int bucketBlue = blue / COLOR_BUCKET_SIZE;

                int bucketKey = bucketRed << 8 | bucketGreen << 4 | bucketBlue;

                buckets.computeIfAbsent(bucketKey, key -> new ColorBucket())
                        .add(red, green, blue, alpha);
            }
        }

        if (buckets.isEmpty()) {
            return FALLBACK_COLOR;
        }

        ColorBucket dominantBucket = buckets.values().stream()
                .max(Comparator.comparingLong(ColorBucket::weight))
                .orElse(null);

        if (dominantBucket == null) {
            return FALLBACK_COLOR;
        }

        return dominantBucket.color();
    }

    private record CannedFoodColorKey(List<Entry> entries) {
        private static CannedFoodColorKey of(List<ItemStack> stacks) {
            Map<Item, Integer> counts = new HashMap<>();

            for (ItemStack stack : stacks) {
                if (!stack.isEmpty()) {
                    counts.merge(stack.getItem(), stack.getCount(), Integer::sum);
                }
            }

            List<Entry> entries = counts.entrySet().stream()
                    .map(entry -> new Entry(entry.getKey(), entry.getValue()))
                    .sorted(Comparator.comparing(
                            entry -> BuiltInRegistries.ITEM.getKey(entry.item()).toString()))
                    .toList();

            return new CannedFoodColorKey(entries);
        }

        private record Entry(Item item, int count) {
        }
    }

    private static final class ColorBucket {
        private long weightedRed;
        private long weightedGreen;
        private long weightedBlue;
        private long weight;

        private void add(int red, int green, int blue, int alpha) {
            weightedRed += (long) red * alpha;
            weightedGreen += (long) green * alpha;
            weightedBlue += (long) blue * alpha;
            weight += alpha;
        }

        private long weight() {
            return weight;
        }

        private int color() {
            if (weight <= 0) {
                return FALLBACK_COLOR;
            }

            int red = (int) (weightedRed / weight);
            int green = (int) (weightedGreen / weight);
            int blue = (int) (weightedBlue / weight);

            return red << 16 | green << 8 | blue;
        }
    }
}