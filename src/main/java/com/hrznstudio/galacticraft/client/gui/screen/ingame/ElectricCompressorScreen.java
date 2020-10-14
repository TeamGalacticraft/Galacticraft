/*
 * Copyright (c) 2020 HRZN LTD
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
import com.hrznstudio.galacticraft.api.screen.MachineHandledScreen;
import com.hrznstudio.galacticraft.screen.ElectricCompressorScreenHandler;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class ElectricCompressorScreen extends MachineHandledScreen<ElectricCompressorScreenHandler> {

    private static final int PROGRESS_X = 204;
    private static final int PROGRESS_Y = 0;
    private static final int PROGRESS_WIDTH = 52;
    private static final int PROGRESS_HEIGHT = 25;
    private final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, getBackgroundLocation());

    private int progressDisplayX;
    private int progressDisplayY;

    public ElectricCompressorScreen(ElectricCompressorScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, inv.player.world, handler.blockEntity.getPos(), title);
        this.backgroundHeight = 199;
    }

    protected void updateProgressDisplay() {
        progressDisplayX = this.x + 77;
        progressDisplayY = this.y + 28;
//        progressDisplayX = left + 105;
        progressDisplayY = this.y + 29;
    }

    protected String getBackgroundLocation() {
        return Constants.ScreenTextures.getRaw(Constants.ScreenTextures.ELECTRIC_COMPRESSOR_SCREEN);
    }

    private String getContainerDisplayName() {
        return new TranslatableText("block.galacticraft-rewoven.electric_compressor").getString();
    }

    @Override
    protected void drawBackground(MatrixStack stack, float delta, int mouseX, int mouseY) {
        this.renderBackground(stack);
        this.client.getTextureManager().bindTexture(BACKGROUND);

        updateProgressDisplay();

        this.drawTexture(stack, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        this.drawCraftProgressBar(stack);
        this.drawConfigTabs(stack, mouseX, mouseY);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float v) {
        super.render(stack, mouseX, mouseY, v);
        DrawableUtils.drawCenteredString(stack, this.client.textRenderer, getContainerDisplayName(), (this.width / 2), this.y + 6, Formatting.DARK_GRAY.getColorValue());
        this.drawMouseoverTooltip(stack, mouseX, mouseY);
    }

    protected void drawCraftProgressBar(MatrixStack stack) {
        float progress = this.handler.blockEntity.getProgress();
        float maxProgress = this.handler.blockEntity.getMaxProgress();
        float progressScale = (progress / maxProgress);
        // Progress confirmed to be working properly, below code is the problem.

        this.client.getTextureManager().bindTexture(BACKGROUND);
        this.drawTexture(stack, progressDisplayX, progressDisplayY, PROGRESS_X, PROGRESS_Y, (int) (PROGRESS_WIDTH * progressScale), PROGRESS_HEIGHT);
    }

    @Override
    public void drawMouseoverTooltip(MatrixStack stack, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(stack, mouseX, mouseY);
    }
}