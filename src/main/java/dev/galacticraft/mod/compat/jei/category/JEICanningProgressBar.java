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

import dev.galacticraft.mod.client.gui.FoodCannerProgressAnimation;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

import static dev.galacticraft.mod.Constant.FoodCanner.*;

public class JEICanningProgressBar implements IDrawable {
    private long previousTime;
    private float progress;
    private boolean[] showRow = {false, false, false, false};

    public void setProcessingTime() {
        this.previousTime = System.currentTimeMillis();
        this.progress = 0;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    public int getProgress() {
        return (int) this.progress;
    }

    public void setSlotsUsed(int numIngredients) {
        this.showRow = new boolean[4];
        for (int i = 0; i < 4; i++) {
            this.showRow[i] = numIngredients > 4 * i;
        }
    }

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        long currentTime = System.currentTimeMillis();
        long delta = currentTime - this.previousTime;
        this.previousTime = currentTime;

        this.progress = FoodCannerProgressAnimation.renderForRecipeViewer(graphics, xOffset, yOffset, this.progress + delta / 50.0F, this.showRow);
    }

    public CurrentCanIngredientRenderer getIngredientRenderer(ItemStack fallback) {
        return new CurrentCanIngredientRenderer(fallback);
    }

    public class CurrentCanIngredientRenderer implements IIngredientRenderer<ItemStack>, IRecipeSlotRichTooltipCallback {
        private final Minecraft minecraft = Minecraft.getInstance();
        private final ItemStack fallback;

        protected CurrentCanIngredientRenderer(ItemStack fallback) {
            this.fallback = fallback;
        }

        private ItemStack getItemStack(ItemStack itemStack) {
            int progress = JEICanningProgressBar.this.getProgress();
            if (progress <= TRANSFER_INPUT) {
                return ItemStack.EMPTY;
            } else if (progress >= TRANSFER_OUTPUT) {
                return itemStack.copy();
            }
            return fallback;
        }

        @Override
        public void render(GuiGraphics graphics, ItemStack itemStack) {
            ItemStack itemStack2 = this.getItemStack(itemStack);
            if (itemStack2.isEmpty()) return;

            graphics.renderItem(itemStack2, 0, 0);
            graphics.renderItemDecorations(this.minecraft.font, itemStack2, 0, 0);
        }

        @Override
        @SuppressWarnings("removal")
        public List<Component> getTooltip(ItemStack itemStack, TooltipFlag tooltipFlag) {
            return itemStack.getTooltipLines(TooltipContext.of(this.minecraft.level), this.minecraft.player, tooltipFlag);
        }

        @Override
        public void onRichTooltip(IRecipeSlotView recipeSlotView, ITooltipBuilder tooltip) {
            int progress = JEICanningProgressBar.this.getProgress();
            if (progress <= TRANSFER_INPUT) {
                tooltip.clear();
            } else if (progress < TRANSFER_OUTPUT) {
                tooltip.clear();
                tooltip.addAll(this.fallback.getTooltipLines(
                        TooltipContext.of(this.minecraft.level),
                        this.minecraft.player,
                        this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL
                ));
            }
        }
    }
}
