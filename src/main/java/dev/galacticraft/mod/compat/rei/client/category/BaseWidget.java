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

import com.mojang.blaze3d.platform.Lighting;
import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static dev.galacticraft.mod.Constant.RecipeViewer.*;

public class BaseWidget extends Widget {

    private final Point startPoint;

    BaseWidget(Point startPoint) {
        this.startPoint = startPoint;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        Lighting.setupForFlatItems();
        graphics.blit(RECIPE_VIEWER_DISPLAY_TEXTURE, this.startPoint.x, this.startPoint.y, COMPRESSOR_U, COMPRESSOR_V, COMPRESSOR_WIDTH, COMPRESSOR_HEIGHT);

        int height = Mth.ceil((double) (System.currentTimeMillis() / 250L) % 14.0D);
        graphics.blit(RECIPE_VIEWER_DISPLAY_TEXTURE, this.startPoint.x + 2, this.startPoint.y + 21 + (14 - height), 82, 77 + (14 - height), 14, height);
        int width = Mth.ceil((double) (System.currentTimeMillis() / 250L) % 24.0D);
        graphics.blit(RECIPE_VIEWER_DISPLAY_TEXTURE, this.startPoint.x + 24, this.startPoint.y + 18, 82, 91, width, 17);
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
}