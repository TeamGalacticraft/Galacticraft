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

import dev.galacticraft.mod.client.gui.screen.ingame.RocketWorkbenchScreen;
import dev.galacticraft.mod.content.entity.orbital.RocketEntity;
import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static dev.galacticraft.mod.Constant.RecipeViewer.*;

public class RocketPreviewWidget extends Widget {
    private final Point startPoint;
    private final RocketEntity entity;

    public RocketPreviewWidget(Point startPoint, RocketEntity entity) {
        this.startPoint = startPoint;
        this.entity = entity;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.entity.setYRot(this.entity.getYRot() + delta);
        RocketWorkbenchScreen.renderEntityInInventory(graphics, this.startPoint.x + ROCKET_PREVIEW_X, this.startPoint.y + ROCKET_PREVIEW_Y, 15, SmithingScreen.ARMOR_STAND_ANGLE, null, this.entity);
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