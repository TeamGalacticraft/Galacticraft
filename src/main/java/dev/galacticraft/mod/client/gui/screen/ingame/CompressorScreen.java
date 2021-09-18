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

package dev.galacticraft.mod.client.gui.screen.ingame;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.client.screen.MachineHandledScreen;
import dev.galacticraft.mod.api.machine.MachineStatus;
import dev.galacticraft.mod.block.entity.CompressorBlockEntity;
import dev.galacticraft.mod.screen.CompressorScreenHandler;
import dev.galacticraft.mod.util.DrawableUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class CompressorScreen extends MachineHandledScreen<CompressorBlockEntity, CompressorScreenHandler> {
    private static final int PROGRESS_X = 204;
    private static final int PROGRESS_Y = 0;
    private static final int PROGRESS_WIDTH = 52;
    private static final int PROGRESS_HEIGHT = 25;

    public CompressorScreen(CompressorScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, title, Constant.ScreenTexture.COMPRESSOR_SCREEN);
        this.backgroundWidth = 176;
        this.backgroundHeight = 167;
    }

    @Override
    protected void renderBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        super.renderBackground(matrices, delta, mouseX, mouseY);
        this.drawFuelProgressBar(matrices);
        this.drawCraftProgressBar(matrices);
    }

    protected void drawFuelProgressBar(MatrixStack matrices) {
        if (this.handler.machine.getStatus().getType() != MachineStatus.StatusType.MISSING_ENERGY) {
            float fuelUsageScale = getFuelProgress();
            DrawableUtil.drawProgressTexture(matrices, this.x + 80, (int) (this.y + 29 + 12 - fuelUsageScale), 203, 39 - fuelUsageScale, 14, fuelUsageScale + 1);
        }
    }

    protected void drawCraftProgressBar(MatrixStack matrices) {
        float progressScale = (((float)this.handler.machine.progress()) / ((float)this.handler.machine.maxProgress()));

        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.COMPRESSOR_SCREEN);
        DrawableUtil.drawProgressTexture(matrices, this.x + 77, this.y + 28, PROGRESS_X, PROGRESS_Y, PROGRESS_WIDTH * progressScale, PROGRESS_HEIGHT);
    }

    private float getFuelProgress() {
        float maxFuelTime = this.handler.machine.fuelLength;
        if (maxFuelTime == 0) {
            maxFuelTime = 200;
        }

        return (this.handler.machine.fuelTime * 13f) / maxFuelTime;
    }
}