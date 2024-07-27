package dev.galacticraft.mod.world.ships;

import dev.galacticraft.mod.Galacticraft;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import org.intellij.lang.annotations.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @Inject(method = "apply", at = @At("RETURN"))
    private void onRecipesLoaded(Map<Identifier, Recipe<?>> recipes, CallbackInfo ci) {
        Masses.processRecipes((RecipeManager) (Object) this);
    }
}