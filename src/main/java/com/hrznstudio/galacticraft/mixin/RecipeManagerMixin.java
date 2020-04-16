package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.accessor.GCRecipeAccessor;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin implements GCRecipeAccessor {
    @Shadow
    protected abstract <C extends Inventory, T extends Recipe<C>> Map<Identifier, Recipe<C>> getAllOfType(RecipeType<T> type);

    @Override
    public <C extends Inventory, T extends Recipe<C>> Map<Identifier, Recipe<C>> getAllOfTypeGC(RecipeType<T> type) {
        return getAllOfType(type);
    }
}
