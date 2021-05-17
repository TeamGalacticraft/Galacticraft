package dev.galacticraft.mod.recipe.rei;

import dev.galacticraft.mod.recipe.ShapedCompressingRecipe;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GCDefaultShapedDisplay implements GCDefaultCraftingDisplay {

    private final List<List<EntryStack>> input;
    private final List<EntryStack> output;

    public GCDefaultShapedDisplay(ShapedCompressingRecipe recipe) {
        this.input = new ArrayList<>();
        recipe.getPreviewInputs().forEach((ingredient) -> {
            List<EntryStack> stacks = new ArrayList<>();
            for (ItemStack stack : ingredient.getMatchingStacksClient()) {
                stacks.add(EntryStack.create(stack));
            }
            input.add(stacks);
        });
        this.output = Collections.singletonList(EntryStack.create(recipe.getOutput()));
    }


    @Override
    public @NotNull List<List<EntryStack>> getInputEntries() {
        return null;
    }


}
