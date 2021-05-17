package dev.galacticraft.mod.recipe.rei;

import me.shedaniel.rei.api.BuiltinPlugin;
import me.shedaniel.rei.api.RecipeDisplay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public interface GCDefaultCraftingDisplay extends RecipeDisplay {

    default @NotNull Identifier getRecipeCategory() {
        return BuiltinPlugin.CRAFTING;
    }

    default int getWidth() {
        return 3;
    }

    default int getHeight() {
        return 3;
    }

}
