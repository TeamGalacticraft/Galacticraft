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

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Collections;
import java.util.List;

public class CustomArrowWidget extends WidgetWithBounds {
    private final ResourceLocation texture;
    private Rectangle bounds;
    private int progressU;
    private int progressV;
    private double animationDuration = -1;

    public CustomArrowWidget(ResourceLocation texture, Rectangle bounds, int progressU, int progressV, double animationDurationMS) {
        this.texture = texture;
        this.bounds = bounds;
        this.progressU = progressU;
        this.progressV = progressV;
        this.animationDuration = animationDurationMS;
    }

    public double getAnimationDuration() {
        return this.animationDuration;
    }

    public void setAnimationDuration(double animationDurationMS) {
        this.animationDuration = animationDurationMS;
        if (this.animationDuration <= 0)
            this.animationDuration = -1;
    }

    @Override
    public Rectangle getBounds() {
        return this.bounds;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (this.animationDuration > 0) {
            int quotient = this.bounds.getWidth() + 2;
            int width = Mth.ceil((System.currentTimeMillis() / (this.animationDuration / quotient) % (double) quotient));
            width = Mth.clamp(width - 1, 0, this.bounds.getWidth());
            graphics.blit(this.texture, this.bounds.getX(), this.bounds.getY(), this.progressU, this.progressV, width, this.bounds.getHeight());
        }
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return Collections.emptyList();
    }
}