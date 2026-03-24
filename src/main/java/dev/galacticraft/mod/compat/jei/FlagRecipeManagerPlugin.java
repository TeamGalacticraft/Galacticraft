package dev.galacticraft.mod.compat.jei;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.item.FlagItem;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.recipe.FlagRecipe;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.advanced.ISimpleRecipeManagerPlugin;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FlagRecipeManagerPlugin implements ISimpleRecipeManagerPlugin<RecipeHolder<CraftingRecipe>> {
    @Override
    public boolean isHandledInput(ITypedIngredient<?> input) {
        if (input.getIngredient() instanceof ItemStack stack) {
            return stack.is(GCItems.STEEL_POLE) || !FlagRecipe.invalidBanner(stack);
        }
        return false;
    }

    @Override
    public boolean isHandledOutput(ITypedIngredient<?> output) {
        if (output.getIngredient() instanceof ItemStack stack) {
            return stack.getItem() instanceof FlagItem;
        }
        return false;
    }

    @Override
    public @NotNull List<RecipeHolder<CraftingRecipe>> getRecipesForInput(ITypedIngredient<?> input) {
        if (input.getIngredient() instanceof ItemStack stack) {
            if (stack.getItem() instanceof BannerItem) {
                return List.of(createRecipe(stack, FlagItem.fromBanner(stack)));
            } else if (stack.is(GCItems.STEEL_POLE)) {
                return List.of(defaultRecipe());
            }
        }
        return List.of();
    }

    @Override
    public @NotNull List<RecipeHolder<CraftingRecipe>> getRecipesForOutput(ITypedIngredient<?> output) {
        if (output.getIngredient() instanceof ItemStack stack) {
            if (stack.getItem() instanceof FlagItem) {
                return List.of(createRecipe(FlagItem.toBanner(stack), stack));
            }
        }
        return List.of();
    }

    @Override
    public @NotNull List<RecipeHolder<CraftingRecipe>> getAllRecipes() {
        return List.of(defaultRecipe());
    }

    public static RecipeHolder<CraftingRecipe> createRecipe(ItemStack banner, ItemStack flag) {
        if (banner.getCount() > 1) {
            banner = banner.copyWithCount(1);
        }

        Map<Character, Ingredient> key = Map.of(
                '|', Ingredient.of(GCItems.STEEL_POLE),
                'B', Ingredient.of(banner)
        );
        List<String> pattern = List.of(
                "|B",
                "| ",
                "| "
        );
        ShapedRecipe recipe = new ShapedRecipe(Constant.Recipe.FLAG, CraftingBookCategory.BUILDING, ShapedRecipePattern.of(key, pattern), flag);
        return new RecipeHolder<>(Constant.id(Constant.Recipe.FLAG), recipe);
    }

    public static RecipeHolder<CraftingRecipe> defaultRecipe() {
        return createRecipe(new ItemStack(Items.WHITE_BANNER), new ItemStack(GCItems.FLAGS.get(DyeColor.WHITE)));
    }
}
