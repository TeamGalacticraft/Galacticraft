package dev.galacticraft.mod.compat.jei.category;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.block.GCBlocks;
import dev.galacticraft.mod.compat.jei.GCJEIRecipeTypes;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class JEICompressingCategory implements IRecipeCategory<CompressingRecipe> {
    private final IDrawable icon, background;

    public JEICompressingCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableItemStack(new ItemStack(GCBlocks.COMPRESSOR));
        this.background = helper.createDrawable(Constant.ScreenTexture.RECIPE_VEIWER_DISPLAY_TEXTURE, 0, 83, 137, 157);
    }

    @Override
    public RecipeType<CompressingRecipe> getRecipeType() {
        return GCJEIRecipeTypes.COMPRESSING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("category.recipe_viewer.compressing");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CompressingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 21)
                .addItemStack(recipe.getResultItem());
    }
}
