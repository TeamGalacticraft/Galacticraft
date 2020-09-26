package com.hrznstudio.galacticraft.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;

public interface CompressingRecipe extends Recipe<Inventory> {
    @Override
    default RecipeType<? extends CompressingRecipe> getType() {
        return GalacticraftRecipes.COMPRESSING_TYPE;
    }
}
