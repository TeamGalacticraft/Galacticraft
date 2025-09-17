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
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.entity.vehicle.RocketEntity;
import dev.galacticraft.mod.recipe.RocketRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import static dev.galacticraft.mod.Constant.RecipeViewer.*;

public class RocketEmiRecipe extends BasicEmiRecipe {
    private final int bodyHeight;
    public static RocketEntity ROCKET_ENTITY = new RocketEntity(GCEntityTypes.ROCKET, Minecraft.getInstance().level);

    static {
        ROCKET_ENTITY.setYRot(90.0F);
    }

    public RocketEmiRecipe(RecipeHolder<RocketRecipe> holder) {
        super(GalacticraftEmiPlugin.ROCKET, holder.id(), ROCKET_WORKBENCH_WIDTH, ROCKET_WORKBENCH_HEIGHT);
        RocketRecipe recipe = holder.value();
        for (Ingredient ingredient : recipe.getIngredients()) {
            this.inputs.add(EmiIngredient.of(ingredient));
        }
        this.outputs.add(EmiStack.of(recipe.getResultItem(null)));
        this.bodyHeight = recipe.bodyHeight();
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // Add the background texture
        widgets.addTexture(ROCKET_WORKBENCH_DISPLAY_TEXTURE, 0, 0, ROCKET_WORKBENCH_WIDTH, ROCKET_WORKBENCH_HEIGHT, ROCKET_WORKBENCH_U, ROCKET_WORKBENCH_V);

        final int rocketHeight = 18 + this.bodyHeight * 18 + 18;
        int y = 59 - rocketHeight / 2;
        final int centerX = 45;

        // Cone
        int slot = 0;
        widgets.addSlot(this.inputs.get(slot++), centerX - 8, y);

        // Body
        for (int i = 0; i < this.bodyHeight; i++) {
            y += 18;
            widgets.addSlot(this.inputs.get(slot++), centerX - 17, y);
            widgets.addSlot(this.inputs.get(slot++), centerX + 1, y);
        }

        // Fins
        for (int i = 0; i < 2; i++) {
            widgets.addSlot(this.inputs.get(slot++), centerX - 35, y + 18 * i);
            widgets.add(new MirroredSlotWidget(this.inputs.get(slot++), centerX + 19, y + 18 * i));
        }
        y += 18;

        // Engine
        widgets.addSlot(this.inputs.get(slot++), centerX - 8, y);

        // Storage
        if (slot < this.inputs.size()) {
            widgets.addSlot(this.inputs.get(slot++), centerX - 8, y + 24);
        } else {
            widgets.addTexture(ROCKET_WORKBENCH_DISPLAY_TEXTURE, centerX - 8, y + 24, 16, 16, CHEST_SLOT_U, CHEST_SLOT_V);
        }

        // Adds an output slot on the right
        // Note that output slots need to call `recipeContext` to inform EMI about their recipe context
        // This includes being able to resolve recipe trees, favorite stacks with recipe context, and more
        widgets.addSlot(this.outputs.get(0), ROCKET_OUTPUT_X - 1, ROCKET_OUTPUT_Y - 1).drawBack(false).recipeContext(this);

        widgets.add(new RocketPreviewEmiWidget(new Bounds(ROCKET_PREVIEW_X - 1, ROCKET_PREVIEW_Y - 1, ROCKET_PREVIEW_WIDTH, ROCKET_PREVIEW_HEIGHT), ROCKET_ENTITY));
    }
}