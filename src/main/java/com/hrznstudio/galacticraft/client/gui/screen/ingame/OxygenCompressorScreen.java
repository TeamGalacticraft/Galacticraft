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
import com.hrznstudio.galacticraft.api.screen.MachineHandledScreen;
import com.hrznstudio.galacticraft.client.gui.widget.machine.CapacitorWidget;
import com.hrznstudio.galacticraft.client.gui.widget.machine.OxygenTankWidget;
import com.hrznstudio.galacticraft.screen.OxygenCompressorScreenHandler;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class OxygenCompressorScreen extends MachineHandledScreen<OxygenCompressorScreenHandler> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OXYGEN_COMPRESSOR_SCREEN));

    public OxygenCompressorScreen(OxygenCompressorScreenHandler handler, Inventory inv, Component title) {
        super(handler, inv, inv.player.level, handler.machine.getBlockPos(), title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.addWidget(new CapacitorWidget(handler.machine.getCapacitor(), 8, 8, 48, this::getEnergyTooltipLines, handler.machine::getStatus));
        this.addWidget(new OxygenTankWidget(handler.machine.getFluidTank().getTank(0), 30, 8, 48));
    }

    @Override
    protected void renderBg(PoseStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        this.minecraft.getTextureManager().bind(BACKGROUND);
        this.blit(matrices, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        if (menu.machine.getStatus().getType().isActive()) {
            double height = (System.currentTimeMillis() % 2250);
            if (height == 0) height = 1; //prevent dividing by zero
            height /= -125D;
            this.blit(matrices, this.leftPos + 93, this.topPos + 64, 187, 18, -11, (int) height);
        }

    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        DrawableUtils.drawCenteredString(matrices, font, I18n.get("block.galacticraft-rewoven.oxygen_compressor"), (this.width / 2) + 20, this.topPos + 5, ChatFormatting.DARK_GRAY.getColor());
        this.renderTooltip(matrices, mouseX, mouseY);
    }
}
