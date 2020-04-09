package com.hrznstudio.galacticraft.accessor;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

import java.util.Map;

public interface GCRecipeAccessor {
    <C extends Inventory, T extends Recipe<C>> Map<Identifier, Recipe<C>> getAllForTypeGC(RecipeType<T> type);
}
