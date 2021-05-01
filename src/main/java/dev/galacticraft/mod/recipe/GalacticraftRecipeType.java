package dev.galacticraft.mod.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GalacticraftRecipeType<C extends Inventory, T extends Recipe<C>> implements RecipeType<T> {
    @Override
    public String toString() {
        Identifier id = Registry.RECIPE_TYPE.getId(this);
        return id == null ? "Unregistered RecipeType" : id.toString();
    }
}
