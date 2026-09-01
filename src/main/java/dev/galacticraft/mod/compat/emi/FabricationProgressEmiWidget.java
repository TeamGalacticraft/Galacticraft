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

import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import dev.galacticraft.mod.client.gui.CircuitFabricatorProgressAnimation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.List;

import static dev.galacticraft.mod.Constant.CircuitFabricator.*;
import static dev.galacticraft.mod.util.Translations.RecipeCategory.EMI_TIME;

public class FabricationProgressEmiWidget extends Widget {
    private final Bounds bounds;
    private final int processingTime;

    public FabricationProgressEmiWidget(int x, int y, int processingTime) {
        this.bounds = new Bounds(x, y - 1, PROGRESS_WIDTH + 1, PROGRESS_HEIGHT + 2);
        this.processingTime = processingTime * 50;
    }

    @Override
    public Bounds getBounds() {
        return this.bounds;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        float progress = (System.currentTimeMillis() % this.processingTime) / (float) this.processingTime;
        CircuitFabricatorProgressAnimation.render(graphics, this.bounds.x(), this.bounds.y() + 1, progress);
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
        final int x = mouseX - this.bounds.x();
        final int y = mouseY - this.bounds.y();
        if (x < 19 && y > 4) {
            return Collections.emptyList();
        } else if (x < 31 && y > 23) {
            return Collections.emptyList();
        } else if (x > 23 && x < 91 && y < 30) {
            return Collections.emptyList();
        } else if (x >= 91 && x <= 95 && y < 11) {
            return Collections.emptyList();
        } else if (x > 95 && y < 30) {
            return Collections.emptyList();
        } else if (x < 44 && y > 34 && y < 48) {
            return Collections.emptyList();
        } else if (x > 48 && x < 64 && y > 34) {
            return Collections.emptyList();
        } else if (x > 68 && x < 109 && y > 34) {
            return Collections.emptyList();
        } else if (x > 48 && y >= 48) {
            return Collections.emptyList();
        }
        return List.of(ClientTooltipComponent.create(Component.translatable(EMI_TIME, this.processingTime / 1000.0F).getVisualOrderText()));
    }
}