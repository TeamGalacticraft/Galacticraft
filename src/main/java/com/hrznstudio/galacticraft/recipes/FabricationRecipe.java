package com.hrznstudio.galacticraft.recipes;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class FabricationRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final String group;
    private final Ingredient input;
    private final ItemStack output;

    public FabricationRecipe(Identifier id, String group, Ingredient input, ItemStack output) {
        this.id = id;
        this.group = group;
        this.input = input;
        this.output = output;
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return this.output.copy();
    }

    @Override
    public boolean fits(int var1, int var2) {
        return true;
    }

    @Override
    public DefaultedList<Ingredient> getPreviewInputs() {
        DefaultedList<Ingredient> list = DefaultedList.create();
        list.add(this.input);
        return list;
    }

    @Override
    public boolean matches(Inventory inventory, World world_1) {
        // TODO PR yarn for this.
        return this.input.method_8093(inventory.getInvStack(0));
    }

    @Override
    public RecipeType<?> getType() {
        return GalacticraftRecipes.FABRICATION_TYPE;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GalacticraftRecipes.FABRICATION_SERIALIZER;
    }

    public Ingredient getInput() {
        return input;
    }

    @Override
    public ItemStack getOutput() {
        return output;
    }

    @Override
    public String getGroup() {
        return group;
    }
}