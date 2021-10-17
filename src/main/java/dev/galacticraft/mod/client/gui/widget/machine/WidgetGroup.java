/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.client.gui.widget.machine;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public abstract class WidgetGroup<T extends AbstractWidget> extends AbstractWidget {
    protected WidgetGroup(int x, int y) {
        super(x, y, 0, 0);
    }

    public abstract List<T> getWidgets();

    @Override
    public int getWidth() {
        int minX = 0;
        int maxX = 0;
        for (T widget : this.getWidgets()) {
            minX = Math.min(widget.getX(), minX);
            maxX = Math.max(widget.getX() + widget.getWidth(), minX);
        }
        return minX - maxX;
    }

    @Override
    public int getHeight() {
        int minY = 0;
        int maxY = 0;
        for (T widget : this.getWidgets()) {
            minY = Math.min(widget.getY(), minY);
            maxY = Math.max(widget.getY() + widget.getHeight(), minY);
        }
        return minY - maxY;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        for (T widget : this.getWidgets()) {
            if (widget.isMouseOver(mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }
}
