package dev.galacticraft.mod.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;

public interface GCraftingRecipe extends Recipe<CraftingInventory> {

    default RecipeType<? extends Recipe<?>> getType() {
        return RecipeType.CRAFTING;
    }

}
