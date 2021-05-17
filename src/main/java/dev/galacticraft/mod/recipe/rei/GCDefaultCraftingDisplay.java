package dev.galacticraft.mod.recipe.rei;

import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public interface GCDefaultCraftingDisplay extends RecipeDisplay {

    default @NotNull Identifier getRecipeCategory() {
        return GalacticraftREIPlugin.CRAFTING;
    }

    default int getWidth() {
        return 3;
    }

    default int getHeight() {
        return 3;
    }

}
