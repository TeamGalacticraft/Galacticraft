package com.hrznstudio.galacticraft.recipes.rei;

import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.recipes.FabricationRecipe;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class DefaultFabricationDisplay implements RecipeDisplay {
    private FabricationRecipe display;
    private List<List<ItemStack>> input;
    private List<ItemStack> output;

    public DefaultFabricationDisplay(FabricationRecipe recipe) {
        this.display = recipe;
        this.input = Lists.newArrayList();
        recipe.getPreviewInputs().forEach((ingredient) -> {
            this.input.add(Arrays.asList(ingredient.getStackArray()));
        });
        this.output = Arrays.asList(recipe.getOutput());
    }

    @Override
    public List<List<ItemStack>> getInput() {
        return this.input;
    }

    @Override
    public List<ItemStack> getOutput() {
        return this.output;
    }

    @Override
    public List<List<ItemStack>> getRequiredItems() {
        return this.input;
    }

    @Override
    public Identifier getRecipeCategory() {
        return GalacticraftREIPlugin.CIRCUIT_FABRICATION;
    }
}