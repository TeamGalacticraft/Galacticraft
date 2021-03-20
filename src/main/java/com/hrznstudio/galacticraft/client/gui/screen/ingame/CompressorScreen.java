/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.client.gui.screen.ingame;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.machine.MachineStatus;
import com.hrznstudio.galacticraft.screen.CompressorScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class CompressorScreen extends HandledScreen<CompressorScreenHandler> {
    private static final int PROGRESS_X = 204;
    private static final int PROGRESS_Y = 0;
    private static final int PROGRESS_WIDTH = 52;
    private static final int PROGRESS_HEIGHT = 25;

    protected final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTexture.getRaw(Constants.ScreenTexture.COMPRESSOR_SCREEN));

    public CompressorScreen(CompressorScreenHandler electricCompressorContainer, PlayerInventory inv, Text title) {
        super(electricCompressorContainer, inv, title);
        this.backgroundHeight = 192;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        this.client.getTextureManager().bindTexture(BACKGROUND);

        this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        this.drawFuelProgressBar(matrices);
        this.drawCraftProgressBar(matrices);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        drawCenteredString(matrices, this.textRenderer, getContainerDisplayName(), (this.width / 2), this.y + 6, Formatting.DARK_GRAY.getColorValue());
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    protected String getContainerDisplayName() {
        return I18n.translate("block.galacticraft-rewoven.compressor");
    }

    protected void drawFuelProgressBar(MatrixStack matrices) {
        this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        int fuelUsageScale;

        if (this.handler.machine.getStatus().getType() != MachineStatus.StatusType.MISSING_ENERGY) {
            fuelUsageScale = getFuelProgress();
            this.drawTexture(matrices, this.x + 80, this.y + 29 + 12 - fuelUsageScale, 203, 39 - fuelUsageScale, 14, fuelUsageScale + 1);
        }
    }

    protected void drawCraftProgressBar(MatrixStack matrices) {
        float progressScale = (((float)this.handler.machine.getProgress()) / ((float)this.handler.machine.getMaxProgress()));

        this.client.getTextureManager().bindTexture(BACKGROUND);
        this.drawTexture(matrices, this.x + 77, this.x + 28, PROGRESS_X, PROGRESS_Y, (int) (PROGRESS_WIDTH * progressScale), PROGRESS_HEIGHT);
    }

    private int getFuelProgress() {
        int maxFuelTime = this.handler.machine.fuelLength;
        if (maxFuelTime == 0) {
            maxFuelTime = 200;
        }

        // 0 = CompressorBlockEntity#fuelTime
        return this.handler.fuelTime.get() * 13 / maxFuelTime;
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
    }
}