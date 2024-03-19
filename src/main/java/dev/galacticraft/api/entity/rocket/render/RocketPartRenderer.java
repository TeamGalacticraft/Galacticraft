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

package dev.galacticraft.api.entity.rocket.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.api.rocket.entity.Rocket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface RocketPartRenderer {
    /**
     * Called when this rocket part is being rendered inside a gui/screen.
     *
     * @param graphics the gui graphics which has various methods for gui rendering; it also contains the matrix stack
     * @param x
     * @param y
     * @param mouseX   the x position of the mouse
     * @param mouseY   the y position of the mouse
     * @param delta    time in-between ticks
     */
    default void renderGUI(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, float delta) {
    }

    void render(ClientLevel world, PoseStack matrices, Rocket rocket, MultiBufferSource vertices, float partialTick, int light, int overlay);
}
