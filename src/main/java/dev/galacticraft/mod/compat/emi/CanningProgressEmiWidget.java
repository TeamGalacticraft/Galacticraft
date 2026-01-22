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

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.Widget;
import dev.galacticraft.mod.client.gui.FoodCannerProgressAnimation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.crafting.Ingredient;

import static dev.galacticraft.mod.Constant.FoodCanner.*;

public class CanningProgressEmiWidget extends Widget {
    private final Bounds bounds;
    private long previousTime;
    private float progress;
    private boolean[] showRow;

    public CanningProgressEmiWidget(int x, int y, int numIngredients) {
        this.bounds = new Bounds(x, y, PROGRESS_WIDTH, PROGRESS_HEIGHT);
        this.previousTime = System.currentTimeMillis();
        this.progress = 0;
        this.showRow = new boolean[4];
        for (int i = 0; i < 4; i++) {
            this.showRow[i] = numIngredients > 4 * i;
        }
    }

    @Override
    public Bounds getBounds() {
        return this.bounds;
    }

    public int getProgress() {
        return (int) this.progress;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        long currentTime = System.currentTimeMillis();
        long diff = currentTime - this.previousTime;
        this.previousTime = currentTime;

        this.progress = FoodCannerProgressAnimation.renderForRecipeViewer(graphics, this.bounds.x(), this.bounds.y(), this.progress + diff / 50.0F, this.showRow);
    }

    public SlotWidget getSlotWidget(EmiIngredient stack, EmiIngredient fallback, int x, int y) {
        return new CurrentCanWidget(stack, fallback, x, y);
    }

    private class CurrentCanWidget extends SlotWidget {
        private final EmiIngredient fallback;
        private static final EmiIngredient EMPTY = EmiIngredient.of(Ingredient.EMPTY);

        protected CurrentCanWidget(EmiIngredient stack, EmiIngredient fallback, int x, int y) {
            super(stack, x, y);
            this.fallback = fallback;
        }

        @Override
        public EmiIngredient getStack() {
            int progress = CanningProgressEmiWidget.this.getProgress();
            if (progress <= TRANSFER_INPUT) {
                return EMPTY;
            } else if (progress >= TRANSFER_OUTPUT) {
                return super.getStack();
            }
            return fallback;
        }
    }
}