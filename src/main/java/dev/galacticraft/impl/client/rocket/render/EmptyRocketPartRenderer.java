/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.impl.client.rocket.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.api.entity.rocket.render.RocketPartRenderer;
import dev.galacticraft.api.rocket.entity.Rocket;
import dev.galacticraft.mod.Constant;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;

@Environment(EnvType.CLIENT)
public enum EmptyRocketPartRenderer implements RocketPartRenderer {
    INSTANCE;
    private static boolean hasWarned = false;

    @Override
    public void renderGUI(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float delta) {
        if (!hasWarned) {
            hasWarned = true;
            Constant.LOGGER.warn("EmptyRocketPartRenderer renderer is in use! RocketPartRenderer wasn't registered?");
        }
    }

    @Override
    public void render(ClientLevel world, PoseStack matrices, Rocket rocket, MultiBufferSource vertices, float partialTick, int light, int overlay) {
        if (!hasWarned) {
            hasWarned = true;
            Constant.LOGGER.warn("EmptyRocketPartRenderer renderer is in use! RocketPartRenderer wasn't registered?");
        }
    }
}
