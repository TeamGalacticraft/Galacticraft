/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

import dev.galacticraft.mod.util.DrawableUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public abstract class AbstractWidget extends DrawableHelper implements Drawable, Element, Positioned, Selectable {
    protected final MinecraftClient client = MinecraftClient.getInstance();
    protected final TextRenderer textRenderer = client.textRenderer;
    protected final ItemRenderer itemRenderer = client.getItemRenderer();
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected boolean hovered = false;

    protected AbstractWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public final void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.hovered = this.isMouseOver(mouseX, mouseY);
        this.setZOffset(0);
        this.renderBackground(matrices, mouseX, mouseY);
        this.setZOffset(1);
        this.renderForeground(matrices, mouseX, mouseY);
        if (this.isMouseOver(mouseX, mouseY)) {
            this.setZOffset(2);
            this.renderTooltips(matrices, mouseX, mouseY);
        }
    }

    protected void renderTooltips(MatrixStack matrices, int mouseX, int mouseY) {
    }

    protected void renderForeground(MatrixStack matrices, int mouseX, int mouseY) {
    }

    protected void renderBackground(MatrixStack matrices, int mouseX, int mouseY) {
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return check(mouseX, mouseY, this.x, this.y, this.width, this.height);
    }

    @Override
    public SelectionType getType() {
        return this.hovered ? SelectionType.HOVERED : SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setPos(int x, int y) {
        this.setX(x);
        this.setY(y);
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    protected static boolean check(double mouseX, double mouseY, int x, int y, int width, int height) {
        return DrawableUtil.isWithin(mouseX, mouseY, x, y, width, height);
    }
}
