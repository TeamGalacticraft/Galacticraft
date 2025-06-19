/*
 * Copyright (c) 2019-2025 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.compat.jei.category;

import dev.galacticraft.mod.client.gui.screen.ingame.RocketWorkbenchScreen;
import dev.galacticraft.mod.content.entity.orbital.RocketEntity;
import dev.galacticraft.mod.compat.jei.GCJEIRecipeTypes;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.recipe.RocketRecipe;
import dev.galacticraft.mod.util.Translations;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import static dev.galacticraft.mod.Constant.RecipeViewer.*;

public class JEIRocketCategory implements IRecipeCategory<RocketRecipe> {
    private final IDrawable icon, chestSprite;

    public static RocketEntity ROCKET_ENTITY = new RocketEntity(GCEntityTypes.ROCKET, Minecraft.getInstance().level);

    public JEIRocketCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableItemStack(new ItemStack(GCBlocks.ROCKET_WORKBENCH));
        this.chestSprite = helper.createDrawable(ROCKET_WORKBENCH_DISPLAY_TEXTURE, CHEST_SLOT_U, CHEST_SLOT_V, 16, 16);
        ROCKET_ENTITY.setYRot(90.0F);
    }

    @Override
    public RecipeType<RocketRecipe> getRecipeType() {
        return GCJEIRecipeTypes.ROCKET;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(Translations.RecipeCategory.ROCKET_WORKBENCH);
    }

    @Override
    public int getWidth() {
        return ROCKET_WORKBENCH_WIDTH;
    }

    @Override
    public int getHeight() {
        return ROCKET_WORKBENCH_HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RocketRecipe recipe, IFocusGroup focuses) {
        final int rocketHeight = 18 + recipe.bodyHeight() * 18 + 18;
        int y = 60 - rocketHeight / 2;
        final int centerX = 46;

        // Cone
        builder.addInputSlot(centerX - 8, y)
                .addIngredients(recipe.cone())
                .setStandardSlotBackground();
        
        // Body
        for (int i = 0; i < recipe.bodyHeight(); i++) {
            y += 18;
            builder.addInputSlot(centerX - 17, y)
                    .addIngredients(recipe.body())
                    .setStandardSlotBackground();
            builder.addInputSlot(centerX + 1, y)
                    .addIngredients(recipe.body())
                    .setStandardSlotBackground();
        }

        // Fins
        for (int i = 0; i < 2; i++) {
            builder.addInputSlot(centerX - 35, y + 18 * i)
                    .addIngredients(recipe.fins())
                    .setStandardSlotBackground();
            builder.addInputSlot(centerX + 19, y + 18 * i)
                    .addIngredients(recipe.fins())
                    .setStandardSlotBackground();
        }
        y += 18;

        // Engine
        builder.addInputSlot(centerX - 8, y)
                .addIngredients(recipe.engine())
                .setStandardSlotBackground();

        // Storage
        IRecipeSlotBuilder storageSlot = builder.addInputSlot(centerX - 8, y + 24)
                    .addIngredients(recipe.storage());
        if (recipe.storage() == Ingredient.EMPTY) {
            storageSlot.setBackground(this.chestSprite, -1, -1);
        } else {
            storageSlot.setStandardSlotBackground();
        }

        // Output
        builder.addOutputSlot(ROCKET_OUTPUT_X, ROCKET_OUTPUT_Y)
                .addItemStack(recipe.getResultItem(null)); //fixme
    }

    @Override
    public void draw(RocketRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        graphics.blit(ROCKET_WORKBENCH_DISPLAY_TEXTURE, 0, 0, ROCKET_WORKBENCH_U, ROCKET_WORKBENCH_V, ROCKET_WORKBENCH_WIDTH, ROCKET_WORKBENCH_HEIGHT);

        ROCKET_ENTITY.setYRot(ROCKET_ENTITY.getYRot() + 0.2F);
        RocketWorkbenchScreen.renderEntityInInventory(graphics, ROCKET_PREVIEW_X, ROCKET_PREVIEW_Y, 15, SmithingScreen.ARMOR_STAND_ANGLE, null, ROCKET_ENTITY);
    }
}
