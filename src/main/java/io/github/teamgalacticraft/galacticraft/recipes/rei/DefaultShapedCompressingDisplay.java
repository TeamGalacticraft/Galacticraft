package io.github.teamgalacticraft.galacticraft.recipes.rei;

import com.google.common.collect.Lists;
import io.github.teamgalacticraft.galacticraft.recipes.ShapedCompressingRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class DefaultShapedCompressingDisplay implements DefaultCompressingDisplay {
    private ShapedCompressingRecipe display;
    private List<List<ItemStack>> input;
    private List<ItemStack> output;

    public DefaultShapedCompressingDisplay(ShapedCompressingRecipe recipe) {
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
}