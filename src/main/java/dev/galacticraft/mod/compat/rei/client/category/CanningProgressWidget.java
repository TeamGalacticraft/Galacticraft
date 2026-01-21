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

package dev.galacticraft.mod.compat.rei.client.category;

import dev.galacticraft.mod.client.gui.FoodCannerProgressAnimation;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static dev.galacticraft.mod.Constant.FoodCanner.*;

public class CanningProgressWidget extends Widget {
    private final int x;
    private final int y;
    private float progress;
    private boolean[] showRow;

    public CanningProgressWidget(int x, int y, int numIngredients) {
        this.x = x;
        this.y = y;
        this.progress = 0;
        this.showRow = new boolean[4];
        for (int i = 0; i < 4; i++) {
            this.showRow[i] = numIngredients > 4 * i;
        }
    }

    public EntryRenderer<ItemStack> getEntryRenderer(ItemStack fallback) {
        return new CurrentCanEntryRenderer(fallback);
    }

    public int getProgress() {
        return (int) this.progress;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.progress = FoodCannerProgressAnimation.renderForRecipeViewer(graphics, this.x, this.y, this.progress + delta, this.showRow);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return Collections.emptyList();
    }

    @Override
    public boolean isDragging() {
        return false;
    }

    @Override
    public void setDragging(boolean dragging) {

    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
        return null;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {

    }

    private class CurrentCanEntryRenderer implements EntryRenderer<ItemStack> {
        private final Minecraft minecraft = Minecraft.getInstance();
        private final ItemStack fallback;

        protected CurrentCanEntryRenderer(ItemStack fallback) {
            this.fallback = fallback;
        }

        private ItemStack getItemStack(EntryStack<ItemStack> entryStack) {
            int progress = CanningProgressWidget.this.getProgress();
            if (progress <= TRANSFER_INPUT) {
                return ItemStack.EMPTY;
            } else if (progress >= TRANSFER_OUTPUT) {
                return entryStack.getValue();
            }
            return fallback;
        }

        @Override
        public void render(EntryStack<ItemStack> entryStack, GuiGraphics graphics, Rectangle bounds, int mouseX, int mouseY, float delta) {
            ItemStack itemStack = this.getItemStack(entryStack);
            if (itemStack.isEmpty()) return;

            graphics.renderItem(itemStack, bounds.x, bounds.y);
            graphics.renderItemDecorations(this.minecraft.font, itemStack, bounds.x, bounds.y);
        }

        @Override
        public Tooltip getTooltip(EntryStack<ItemStack> entryStack, TooltipContext context) {
            int progress = CanningProgressWidget.this.getProgress();
            if (progress >= TRANSFER_OUTPUT) {
                return entryStack.getDefinition().getRenderer().getTooltip(entryStack.cast(), context);
            }

            ItemStack itemStack = this.getItemStack(entryStack);
            if (itemStack.isEmpty()) return null;

            return Tooltip.create(itemStack.getTooltipLines(
                    Item.TooltipContext.of(this.minecraft.level), this.minecraft.player, context.getFlag()
            ));
        }
    }
}