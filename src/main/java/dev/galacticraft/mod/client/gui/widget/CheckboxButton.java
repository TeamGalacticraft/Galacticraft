/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.mod.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.mod.Constant;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class CheckboxButton extends AbstractButton {

    private boolean checked = false;

    public CheckboxButton(int x, int y) {
        super(x, y, Constant.TextureCoordinate.BUTTON_WIDTH, Constant.TextureCoordinate.BUTTON_HEIGHT, Component.empty());
    }

    @Override
    public void renderButton(PoseStack poseStack, int i, int j, float f) {
        poseStack.pushPose();
        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.OVERLAY);
        blit(poseStack, this.getX(), this.getY(), checked ? Constant.TextureCoordinate.BUTTON_GREEN_X : Constant.TextureCoordinate.BUTTON_RED_X, isHoveredOrFocused() ? 115 : 102, getWidth(), getHeight());
        poseStack.popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public void onPress() {
        checked = !checked;
    }
}
