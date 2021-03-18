/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.item;

import net.minecraft.item.FoodComponent;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftFoodComponents {
    public static final FoodComponent MOON_BERRIES = new FoodComponent.Builder().hunger(1).saturationModifier(0.0F).build();
    public static final FoodComponent CHEESE_CURD = new FoodComponent.Builder().hunger(1).saturationModifier(0.1F).build();
    public static final FoodComponent CHEESE_SLICE = new FoodComponent.Builder().hunger(2).saturationModifier(0.1F).build();
    public static final FoodComponent BURGER_BUN = new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build();
    public static final FoodComponent GROUND_BEEF = new FoodComponent.Builder().hunger(3).saturationModifier(0.6F).meat().build();
    public static final FoodComponent BEEF_PATTY = new FoodComponent.Builder().hunger(4).saturationModifier(0.8F).meat().build();
    public static final FoodComponent CHEESEBURGER = new FoodComponent.Builder().hunger(14).saturationModifier(4.0F).build();

    public static final FoodComponent DEHYDRATED_APPLE = new FoodComponent.Builder().hunger(8).saturationModifier(0.3F).build();
    public static final FoodComponent DEHYDRATED_CARROT = new FoodComponent.Builder().hunger(8).saturationModifier(0.6F).build();
    public static final FoodComponent DEHYDRATED_MELON = new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build();
    public static final FoodComponent DEHYDRATED_POTATO = new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build();
    public static final FoodComponent CANNED_BEEF = new FoodComponent.Builder().hunger(8).saturationModifier(0.6F).build();
}
