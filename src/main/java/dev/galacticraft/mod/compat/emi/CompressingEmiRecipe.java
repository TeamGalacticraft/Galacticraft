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
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.recipe.ShapedCompressingRecipe;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

import static dev.galacticraft.mod.Constant.RecipeViewer.*;
import static dev.galacticraft.mod.util.Translations.RecipeCategory.EMI_TIME;

public class CompressingEmiRecipe extends BasicEmiRecipe {
    public final boolean shapeless;
    public final int width;
    public final int height;
    public final int time;

    public CompressingEmiRecipe(RecipeHolder<CompressingRecipe> holder) {
        super(GalacticraftEmiPlugin.COMPRESSING, holder.id(), COMPRESSOR_WIDTH, COMPRESSOR_HEIGHT);
        CompressingRecipe recipe = holder.value();
        for (Ingredient ingredient : recipe.getIngredients()) {
            this.inputs.add(EmiIngredient.of(ingredient));
        }
        this.outputs.add(EmiStack.of(recipe.getResultItem(null)));
        if (recipe instanceof ShapedCompressingRecipe shapedRecipe) {
            this.shapeless = false;
            this.width = shapedRecipe.getWidth();
            this.height = shapedRecipe.getHeight();
        } else {
            this.shapeless = true;
            this.width = 3;
            this.height = 3;
        }
        this.time = recipe.getTime();
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // Add the background texture
        widgets.addTexture(RECIPE_VIEWER_DISPLAY_TEXTURE, 0, 0, COMPRESSOR_WIDTH, COMPRESSOR_HEIGHT, COMPRESSOR_U, COMPRESSOR_V);

        if (shapeless) {
            widgets.addTexture(EmiTexture.SHAPELESS, COMPRESSED_X, 0);
        }

        widgets.addAnimatedTexture(EmiTexture.FULL_FLAME, FIRE_X, FIRE_Y, 4000, false, true, true);
        widgets.addAnimatedTexture(RECIPE_VIEWER_DISPLAY_TEXTURE, 66, 10, 52, 24, 204, 74, this.time * 50, true, false, false).tooltip((mx, my) -> {
            return List.of(ClientTooltipComponent.create(Component.translatable(EMI_TIME, this.time / 20.0F).getVisualOrderText()));
        });

        for (int i = 0; i < this.inputs.size(); i++) {
            widgets.addSlot(this.inputs.get(i), (i % this.width) * 18, (i / this.width) * 18);
        }

        // Adds an output slot on the right
        // Note that output slots need to call `recipeContext` to inform EMI about their recipe context
        // This includes being able to resolve recipe trees, favorite stacks with recipe context, and more
        widgets.addSlot(this.outputs.get(0), COMPRESSED_X - 5, COMPRESSED_Y - 5).large(true).recipeContext(this);
    }
}