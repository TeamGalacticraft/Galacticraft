/*
 * Copyright (c) 2019-2026 Team Galacticraft
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
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.entity.vehicle.RocketEntity;
import dev.galacticraft.mod.recipe.RocketRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import static dev.galacticraft.mod.Constant.RocketWorkbench.*;

public class RocketEmiRecipe extends BasicEmiRecipe {
    private static final int CHEST_SLOT_X = CHEST_X - RECIPE_VIEWER_X - 1;
    private static final int CHEST_SLOT_Y = CHEST_Y - RECIPE_VIEWER_Y - 1;
    private static final int OUTPUT_SLOT_X = OUTPUT_X - RECIPE_VIEWER_X - 1;
    private static final int OUTPUT_SLOT_Y = OUTPUT_Y - RECIPE_VIEWER_Y - 1;
    private static final int OUTPUT_BORDER_X = OUTPUT_X - OUTPUT_X_OFFSET - RECIPE_VIEWER_X;
    private static final int OUTPUT_BORDER_Y = OUTPUT_Y - OUTPUT_Y_OFFSET - RECIPE_VIEWER_Y;
    private static final int PREVIEW_BACKGROUND_X = PREVIEW_X - RECIPE_VIEWER_X;
    private static final int PREVIEW_BACKGROUND_Y = PREVIEW_Y - RECIPE_VIEWER_Y;
    private static final int PREVIEW_ROCKET_X = ROCKET_X - RECIPE_VIEWER_X;
    private static final int PREVIEW_ROCKET_Y = ROCKET_Y - RECIPE_VIEWER_Y;

    private final int bodyHeight;
    private final boolean hasBoosters;
    private static final EmiIngredient EMPTY = EmiIngredient.of(Ingredient.EMPTY);
    public static RocketEntity ROCKET_ENTITY = new RocketEntity(GCEntityTypes.ROCKET, Minecraft.getInstance().level);

    static {
        ROCKET_ENTITY.setYRot(90.0F);
    }

    public RocketEmiRecipe(RecipeHolder<RocketRecipe> holder) {
        super(GalacticraftEmiPlugin.ROCKET, holder.id(), RECIPE_VIEWER_WIDTH, RECIPE_VIEWER_HEIGHT);
        RocketRecipe recipe = holder.value();
        for (Ingredient ingredient : recipe.getIngredients()) {
            this.inputs.add(EmiIngredient.of(ingredient));
        }
        this.outputs.add(EmiStack.of(recipe.getResultItem(null)));
        this.bodyHeight = recipe.bodyHeight();
        this.hasBoosters = !recipe.boosters().isEmpty();
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int slot = 0;
        for (RocketRecipe.RocketSlotData data : RocketRecipe.slotData(this.bodyHeight, this.hasBoosters)) {
            if (data.mirror()) {
                widgets.add(new MirroredSlotWidget(this.inputs.get(slot++), data.x() - RECIPE_VIEWER_X - 1, data.y() - RECIPE_VIEWER_Y - 1));
            } else {
                widgets.addSlot(this.inputs.get(slot++), data.x() - RECIPE_VIEWER_X - 1, data.y() - RECIPE_VIEWER_Y - 1);
            }
        }

        // Storage
        widgets.addTexture(SCREEN_TEXTURE, CHEST_SLOT_X - 1, CHEST_SLOT_Y - 1, CHEST_WIDTH, CHEST_HEIGHT, CHEST_U, CHEST_V);
        if (slot < this.inputs.size()) {
            widgets.addSlot(this.inputs.get(slot++), CHEST_SLOT_X, CHEST_SLOT_Y);
        } else {
            widgets.addSlot(EMPTY, CHEST_SLOT_X, CHEST_SLOT_Y).backgroundTexture(SCREEN_TEXTURE, CHEST_U + 1, CHEST_V + 1);
        }

        // Adds an output slot on the right
        // Note that output slots need to call `recipeContext` to inform EMI about their recipe context
        // This includes being able to resolve recipe trees, favorite stacks with recipe context, and more
        widgets.addTexture(SCREEN_TEXTURE, OUTPUT_BORDER_X, OUTPUT_BORDER_Y, OUTPUT_WIDTH, OUTPUT_HEIGHT, OUTPUT_U, OUTPUT_V);
        widgets.addSlot(this.outputs.get(0), OUTPUT_SLOT_X, OUTPUT_SLOT_Y).drawBack(false).recipeContext(this);

        widgets.addTexture(SCREEN_TEXTURE, PREVIEW_BACKGROUND_X, PREVIEW_BACKGROUND_Y, PREVIEW_WIDTH, PREVIEW_HEIGHT, PREVIEW_U, PREVIEW_V);
        widgets.add(new RocketPreviewEmiWidget(new Bounds(PREVIEW_ROCKET_X, PREVIEW_ROCKET_Y, PREVIEW_WIDTH, PREVIEW_HEIGHT), ROCKET_ENTITY));
    }
}