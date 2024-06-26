package dev.galacticraft.mod.compat;

import net.minecraft.world.food.FoodProperties;
import squeek.appleskin.api.AppleSkinApi;
import squeek.appleskin.api.event.FoodValuesEvent;
import squeek.appleskin.api.food.FoodValues;

import static dev.galacticraft.mod.content.item.CannedFoodItem.getCanFoodProperties;
import static dev.galacticraft.mod.content.item.CannedFoodItem.isCannedFoodItem;

public class AppleSkinEventHandler implements AppleSkinApi
{

    @Override
    public void registerEvents() {
        FoodValuesEvent.EVENT.register(foodValuesEvent -> {
            if (isCannedFoodItem(foodValuesEvent.itemStack))
            {
                FoodProperties foodProperties = getCanFoodProperties(foodValuesEvent.itemStack);
                if (foodProperties != null)
                {
                    foodValuesEvent.modifiedFoodValues = new FoodValues(foodProperties.getNutrition(), foodProperties.getSaturationModifier());
                }
            }
        });
    }
}
