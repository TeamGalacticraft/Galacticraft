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

package dev.galacticraft.mod.compat.rei.client.category;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.galacticraft.mod.Constant;
import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import net.minecraft.client.gui.Element;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import java.util.Collections;
import java.util.List;

public class BaseWidget extends Widget {

    private final Point startPoint;

    BaseWidget(Point startPoint) {
        this.startPoint = startPoint;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        DiffuseLighting.disableGuiDepthLighting();
        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.REI_DISPLAY_TEXTURE);
        this.drawTexture(matrices, this.startPoint.x, this.startPoint.y, 0, 83, 137, 157);

        int height = MathHelper.ceil((double) (System.currentTimeMillis() / 250L) % 14.0D);
        this.drawTexture(matrices, this.startPoint.x + 2, this.startPoint.y + 21 + (14 - height), 82, 77 + (14 - height), 14, height);
        int width = MathHelper.ceil((double) (System.currentTimeMillis() / 250L) % 24.0D);
        this.drawTexture(matrices, this.startPoint.x + 24, this.startPoint.y + 18, 82, 91, width, 17);
    }

    @Override
    public List<? extends Element> children() {
        return Collections.emptyList();
    }
}