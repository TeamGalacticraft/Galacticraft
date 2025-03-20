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

package dev.galacticraft.mod.content.item;

import net.minecraft.world.food.FoodProperties;

public class GCFoodComponent {
    public static final FoodProperties MOON_CHEESE_CURD = new FoodProperties.Builder().nutrition(1).saturationModifier(0.1F).build();
    public static final FoodProperties MOON_CHEESE_SLICE = new FoodProperties.Builder().nutrition(2).saturationModifier(0.1F).build();
    public static final FoodProperties BURGER_BUN = new FoodProperties.Builder().nutrition(2).saturationModifier(0.3F).build();
    public static final FoodProperties GROUND_BEEF = new FoodProperties.Builder().nutrition(3).saturationModifier(0.6F).build();
    public static final FoodProperties BEEF_PATTY = new FoodProperties.Builder().nutrition(4).saturationModifier(0.8F).build();
    public static final FoodProperties CHEESEBURGER = new FoodProperties.Builder().nutrition(14).saturationModifier(0.8F).build();

    public static final FoodProperties DEHYDRATED_APPLE = new FoodProperties.Builder().nutrition(8).saturationModifier(0.3F).build();
    public static final FoodProperties DEHYDRATED_CARROT = new FoodProperties.Builder().nutrition(8).saturationModifier(0.6F).build();
    public static final FoodProperties DEHYDRATED_MELON = new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build();
    public static final FoodProperties DEHYDRATED_POTATO = new FoodProperties.Builder().nutrition(2).saturationModifier(0.3F).build();
    public static final FoodProperties CANNED_BEEF = new FoodProperties.Builder().nutrition(8).saturationModifier(0.6F).build();
}
