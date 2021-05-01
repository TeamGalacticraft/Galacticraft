package dev.galacticraft.mod.util;

import com.google.gson.JsonArray;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.collection.DefaultedList;

public class RecipeUtil {
    private RecipeUtil() {}

    public static DefaultedList<Ingredient> getIngredients(JsonArray jsonArray) {
        DefaultedList<Ingredient> list = DefaultedList.of();

        for (int i = 0; i < jsonArray.size(); ++i) {
            Ingredient ingredient = Ingredient.fromJson(jsonArray.get(i));
            if (!ingredient.isEmpty()) {
                list.add(ingredient);
            }
        }
        return list;
    }
}
