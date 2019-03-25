package io.github.teamgalacticraft.galacticraft.items;

import net.minecraft.item.FoodItemSetting;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GCFoodSettings {
    public static final FoodItemSetting CHEESE_CURD = new FoodItemSetting.Builder().hunger(1).saturationModifier(0.1F).build();
    public static final FoodItemSetting CHEESE_SLICE = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.1F).build();
    public static final FoodItemSetting BURGER_BUN = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.3F).build();
    public static final FoodItemSetting GROUND_BEEF = new FoodItemSetting.Builder().hunger(3).saturationModifier(0.6F).wolfFood().build();
    public static final FoodItemSetting BEEF_PATTY = new FoodItemSetting.Builder().hunger(4).saturationModifier(0.8F).wolfFood().build();
    public static final FoodItemSetting CHEESEBURGER = new FoodItemSetting.Builder().hunger(14).saturationModifier(4.0F).build();

    public static final FoodItemSetting DEHYDRATED_APPLE = new FoodItemSetting.Builder().hunger(8).saturationModifier(0.3F).build();
    public static final FoodItemSetting DEHYDRATED_CARROT = new FoodItemSetting.Builder().hunger(8).saturationModifier(0.6F).build();
    public static final FoodItemSetting DEHYDRATED_MELON = new FoodItemSetting.Builder().hunger(4).saturationModifier(0.3F).build();
    public static final FoodItemSetting DEHYDRATED_POTATO = new FoodItemSetting.Builder().hunger(2).saturationModifier(0.3F).build();
    public static final FoodItemSetting CANNED_BEEF = new FoodItemSetting.Builder().hunger(8).saturationModifier(0.6F).build();

}
