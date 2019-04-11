package io.github.teamgalacticraft.galacticraft.recipes.rei;

import com.google.common.collect.Lists;
import io.github.teamgalacticraft.galacticraft.recipes.CompressingRecipe;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DefaultCompressingDisplay implements RecipeDisplay {
    private CompressingRecipe display;
    private List<List<ItemStack>> input;
    private List<ItemStack> output;

    public DefaultCompressingDisplay(CompressingRecipe recipe) {
        this.display = recipe;
        this.input = Lists.newArrayList();
        recipe.getPreviewInputs().forEach((ingredient) -> {
            this.input.add(Arrays.asList(ingredient.getStackArray()));
        });
        this.output = Arrays.asList(recipe.getOutput());
    }

    @Override
    public Optional getRecipe() {
        return Optional.ofNullable(this.display);
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
        return GalacticraftREIPlugin.COMPRESSING;
    }

    public int getHeight() {
        return this.display.getHeight();
    }

    public int getWidth() {
        return this.display.getWidth();
    }
}