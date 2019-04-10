package io.github.teamgalacticraft.galacticraft.recipes;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public class CompressingRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final String group;
    private final DefaultedList<Ingredient> input;
    private final ItemStack output;
    private int width;
    private int height;

    public CompressingRecipe(Identifier id, String group, int width, int height, DefaultedList<Ingredient> input, ItemStack output) {
        this.id = id;
        this.group = group;
        this.width = width;
        this.height = height;
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean matches(Inventory var1, World var2) {
        return false;
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return this.output.copy();
    }

    @Override
    public boolean fits(int var1, int var2) {
        return true;
    }

    public DefaultedList<Ingredient> getPreviewInputs() {
        DefaultedList<Ingredient> list = DefaultedList.create();
        list.addAll(this.input);
        return list;
    }

    @Override
    public RecipeType<?> getType() {
        return GalacticraftRecipes.COMPRESSING_TYPE;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GalacticraftRecipes.COMPRESSING_SERIALIZER;
    }

    public List<Ingredient> getInputs() {
        return input;
    }

    @Override
    public ItemStack getOutput() {
        return output;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public String getGroup() {
        return group;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}