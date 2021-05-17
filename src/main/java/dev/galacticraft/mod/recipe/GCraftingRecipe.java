package dev.galacticraft.mod.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;

public interface GCraftingRecipe extends CraftingRecipe {
    @Override
    default RecipeType<? extends Recipe<?>> getType() {
        return RecipeType.CRAFTING;
    }
}
