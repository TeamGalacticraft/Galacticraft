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
import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
import com.hrznstudio.galacticraft.screen.CompressorScreenHandler;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class CompressorScreen extends AbstractContainerScreen<CompressorScreenHandler> {
    private static final int PROGRESS_X = 204;
    private static final int PROGRESS_Y = 0;
    private static final int PROGRESS_WIDTH = 52;
    private static final int PROGRESS_HEIGHT = 25;

    protected final ResourceLocation BACKGROUND = new ResourceLocation(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.COMPRESSOR_SCREEN));

    public CompressorScreen(CompressorScreenHandler electricCompressorContainer, Inventory inv, Component title) {
        super(electricCompressorContainer, inv, title);
        this.imageHeight = 192;
    }

    @Override
    protected void renderBg(PoseStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        this.minecraft.getTextureManager().bind(BACKGROUND);

        this.blit(matrices, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        this.drawFuelProgressBar(matrices);
        this.drawCraftProgressBar(matrices);
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        DrawableUtils.drawCenteredString(matrices, this.font, getContainerDisplayName(), (this.width / 2), this.topPos + 6, ChatFormatting.DARK_GRAY.getColor());
        this.renderTooltip(matrices, mouseX, mouseY);
    }

    protected String getContainerDisplayName() {
        return I18n.get("block.galacticraft-rewoven.compressor");
    }

    protected void drawFuelProgressBar(PoseStack matrices) {
        this.blit(matrices, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        int fuelUsageScale;

        if (this.menu.machine.getStatus().getType() != ConfigurableMachineBlockEntity.MachineStatus.StatusType.MISSING_ENERGY) {
            fuelUsageScale = getFuelProgress();
            this.blit(matrices, this.leftPos + 80, this.topPos + 29 + 12 - fuelUsageScale, 203, 39 - fuelUsageScale, 14, fuelUsageScale + 1);
        }
    }

    protected void drawCraftProgressBar(PoseStack matrices) {
        float progressScale = (((float)this.menu.machine.getProgress()) / ((float)this.menu.machine.getMaxProgress()));

        this.minecraft.getTextureManager().bind(BACKGROUND);
        this.blit(matrices, this.leftPos + 77, this.leftPos + 28, PROGRESS_X, PROGRESS_Y, (int) (PROGRESS_WIDTH * progressScale), PROGRESS_HEIGHT);
    }

    private int getFuelProgress() {
        int maxFuelTime = this.menu.machine.fuelLength;
        if (maxFuelTime == 0) {
            maxFuelTime = 200;
        }

        // 0 = CompressorBlockEntity#fuelTime
        return this.menu.fuelTime.get() * 13 / maxFuelTime;
    }

    @Override
    protected void renderLabels(PoseStack matrices, int mouseX, int mouseY) {
    }
}