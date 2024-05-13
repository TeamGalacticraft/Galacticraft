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

package dev.galacticraft.mod.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.machine.CompressorBlockEntity;
import dev.galacticraft.mod.screen.CompressorMenu;
import dev.galacticraft.mod.util.DrawableUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class CompressorScreen extends MachineScreen<CompressorBlockEntity, CompressorMenu> {
    private static final int PROGRESS_U = 204;
    private static final int PROGRESS_V = 0;
    private static final int PROGRESS_X = 82;
    private static final int PROGRESS_Y = 26;
    private static final int PROGRESS_WIDTH = 52;
    private static final int PROGRESS_HEIGHT = 25;

    private static final int FIRE_U = 204;
    private static final int FIRE_V = 25;
    private static final int FIRE_WIDTH = 14;
    private static final int FIRE_HEIGHT = 14;
    private static final int FIRE_X = 84;
    private static final int FIRE_Y = 26;

    public CompressorScreen(CompressorMenu handler, Inventory inv, Component title) {
        super(handler, title, Constant.ScreenTexture.COMPRESSOR_SCREEN);
    }

    @Override
    protected void renderMachineBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderMachineBackground(graphics, mouseX, mouseY, delta);
        this.drawFuelProgressBar(graphics.pose());
        this.drawCraftProgressBar(graphics.pose());
    }

    protected void drawFuelProgressBar(PoseStack matrices) {
        if (this.menu.getFuelLength() > 0) {
            float fuelUsageScale = (float)(1.0 - (double)(this.menu.getFuelLength() - this.menu.getFuelTime()) / (double)this.menu.getFuelLength());
            RenderSystem.setShaderTexture(0, Constant.ScreenTexture.COMPRESSOR_SCREEN);
            DrawableUtil.drawProgressTexture(matrices, this.leftPos + FIRE_X, (this.topPos + FIRE_Y + FIRE_HEIGHT - (fuelUsageScale * FIRE_HEIGHT)), FIRE_U, FIRE_V + (FIRE_HEIGHT - (fuelUsageScale * FIRE_HEIGHT)), FIRE_WIDTH, (fuelUsageScale * FIRE_HEIGHT));
        }
    }

    protected void drawCraftProgressBar(PoseStack matrices) {
        float progressScale = (((float)this.menu.getProgress()) / ((float)this.menu.getMaxProgress()));

        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.COMPRESSOR_SCREEN);
        DrawableUtil.drawProgressTexture(matrices, this.leftPos + PROGRESS_X, this.topPos + PROGRESS_Y, PROGRESS_U, PROGRESS_V, PROGRESS_WIDTH * progressScale, PROGRESS_HEIGHT);
    }
}