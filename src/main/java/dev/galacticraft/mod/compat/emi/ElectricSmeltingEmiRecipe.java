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

package dev.galacticraft.mod.compat.emi;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.api.widget.TextWidget.Alignment;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmeltingRecipe;

import java.util.List;

import static dev.galacticraft.mod.Constant.ElectricFurnace.*;
import static dev.galacticraft.mod.util.Translations.RecipeCategory.EMI_TIME;
import static dev.galacticraft.mod.util.Translations.RecipeCategory.EMI_XP;

public class ElectricSmeltingEmiRecipe extends BasicEmiRecipe {
    private static final int PROGRESS_BAR_X = PROGRESS_X - EMI_X;
    private static final int PROGRESS_BAR_Y = PROGRESS_Y - EMI_Y;

    public final int time;
    public final float experience;

    public ElectricSmeltingEmiRecipe(RecipeHolder<SmeltingRecipe> holder) {
        super(GalacticraftEmiPlugin.ELECTRIC_SMELTING, holder.id().withPrefix("/electric_"), EMI_WIDTH, EMI_HEIGHT);
        SmeltingRecipe recipe = holder.value();
        for (Ingredient ingredient : recipe.getIngredients()) {
            this.inputs.add(EmiIngredient.of(ingredient));
        }
        this.outputs.add(EmiStack.of(recipe.getResultItem(null)));
        this.time = (int) (recipe.getCookingTime() / 1.5F);
        this.experience = recipe.getExperience();
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // Add the background texture
        widgets.addTexture(SCREEN_TEXTURE, PROGRESS_BAR_X, PROGRESS_BAR_Y, PROGRESS_WIDTH, PROGRESS_HEIGHT, PROGRESS_BACKGROUND_U, PROGRESS_BACKGROUND_V);

        widgets.addAnimatedTexture(SCREEN_TEXTURE, PROGRESS_BAR_X - 1, PROGRESS_BAR_Y, PROGRESS_WIDTH + 2, PROGRESS_HEIGHT, PROGRESS_U - 1, PROGRESS_V, this.time * 50, true, false, false).tooltip((mx, my) -> {
            return List.of(ClientTooltipComponent.create(Component.translatable(EMI_TIME, this.time / 20.0F).getVisualOrderText()));
        });

        widgets.addText(Component.translatable(EMI_XP, this.experience), PROGRESS_BAR_X + PROGRESS_WIDTH / 2, 28, -1, true).horizontalAlign(Alignment.CENTER);

        widgets.addSlot(this.inputs.get(0), INPUT_X - EMI_X - 1, INPUT_Y - EMI_Y - 1);

        // Adds an output slot on the right
        // Note that output slots need to call `recipeContext` to inform EMI about their recipe context
        // This includes being able to resolve recipe trees, favorite stacks with recipe context, and more
        widgets.addSlot(this.outputs.get(0), OUTPUT_X - EMI_X - 5, OUTPUT_Y - EMI_Y - 5).large(true).recipeContext(this);
    }
}