package com.hrznstudio.galacticraft.recipes.rei;

import me.shedaniel.rei.api.RecipeDisplay;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public interface DefaultCompressingDisplay<T> extends RecipeDisplay {
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
