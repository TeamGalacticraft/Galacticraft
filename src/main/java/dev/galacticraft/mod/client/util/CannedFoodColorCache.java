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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class CannedFoodColorCache {
    private static final int FALLBACK_COLOR = 0xFFFFFF;
    private static final Map<Item, Integer> FOOD_COLORS = new ConcurrentHashMap<>();
    private static final Map<CannedFoodColorKey, Integer> COLOR_CACHE = new ConcurrentHashMap<>();

    private CannedFoodColorCache() {
    }

    /**
     * Clears cached generated food/can colours.
     *
     * <p>Call this after resource reloads if item texture colours may have changed.</p>
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

        int avgRed = (int) (sumRed / totalCount);
        int avgGreen = (int) (sumGreen / totalCount);
        int avgBlue = (int) (sumBlue / totalCount);

        return avgRed << 16 | avgGreen << 8 | avgBlue;
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
        TextureAtlasSprite sprite = itemRenderer.getModel(stack, null, null, 0).getParticleIcon();
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

    private record CannedFoodColorKey(Set<Entry> entries) {
        private static CannedFoodColorKey of(List<ItemStack> stacks) {
            Map<Item, Integer> counts = new HashMap<>();

            for (ItemStack stack : stacks) {
                if (!stack.isEmpty()) {
                    counts.merge(stack.getItem(), stack.getCount(), Integer::sum);
                }
            }

            Set<Entry> entries = counts.entrySet().stream()
                    .map(entry -> new Entry(entry.getKey(), entry.getValue()))
                    .sorted(Comparator.comparing(entry -> BuiltInRegistries.ITEM.getKey(entry.item()).toString()))
                    .collect(Collectors.toSet());

            return new CannedFoodColorKey(entries);
        }

        private record Entry(Item item, int count) {
        }
    }
}