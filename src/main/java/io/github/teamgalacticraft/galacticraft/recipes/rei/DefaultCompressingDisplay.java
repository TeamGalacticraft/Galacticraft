package io.github.teamgalacticraft.galacticraft.recipes.rei;

import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

public interface DefaultCompressingDisplay<T> extends RecipeDisplay<Recipe> {
    default Identifier getRecipeCategory() {
        return GalacticraftREIPlugin.COMPRESSING;
    }

    default int getWidth() {
        return 3;
    }

    default int getHeight() {
        return 3;
    }
}
