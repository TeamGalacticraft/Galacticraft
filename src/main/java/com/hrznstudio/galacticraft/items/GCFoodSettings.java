package com.hrznstudio.galacticraft.items;

import net.minecraft.item.FoodComponent;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GCFoodSettings {
    public static final FoodComponent MOON_BERRIES = new FoodComponent.Builder().hunger(1).saturationModifier(0.0F).build();
    public static final FoodComponent CHEESE_CURD = new FoodComponent.Builder().hunger(1).saturationModifier(0.1F).build();
    public static final FoodComponent CHEESE_SLICE = new FoodComponent.Builder().hunger(2).saturationModifier(0.1F).build();
    public static final FoodComponent BURGER_BUN = new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build();
    public static final FoodComponent GROUND_BEEF = new FoodComponent.Builder().hunger(3).saturationModifier(0.6F).wolfFood().build();
    public static final FoodComponent BEEF_PATTY = new FoodComponent.Builder().hunger(4).saturationModifier(0.8F).wolfFood().build();
    public static final FoodComponent CHEESEBURGER = new FoodComponent.Builder().hunger(14).saturationModifier(4.0F).build();

    public static final FoodComponent DEHYDRATED_APPLE = new FoodComponent.Builder().hunger(8).saturationModifier(0.3F).build();
    public static final FoodComponent DEHYDRATED_CARROT = new FoodComponent.Builder().hunger(8).saturationModifier(0.6F).build();
    public static final FoodComponent DEHYDRATED_MELON = new FoodComponent.Builder().hunger(4).saturationModifier(0.3F).build();
    public static final FoodComponent DEHYDRATED_POTATO = new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).build();
    public static final FoodComponent CANNED_BEEF = new FoodComponent.Builder().hunger(8).saturationModifier(0.6F).build();

}
