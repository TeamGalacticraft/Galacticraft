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
import com.hrznstudio.galacticraft.screen.OxygenStorageModuleScreenHandler;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import java.math.RoundingMode;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class OxygenStorageModuleScreen extends MachineHandledScreen<OxygenStorageModuleScreenHandler> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OXYGEN_STORAGE_MODULE_SCREEN));

    public OxygenStorageModuleScreen(OxygenStorageModuleScreenHandler handler, Inventory inv, Component title) {
        super(handler, inv, inv.player.level, handler.machine.getBlockPos(), title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(PoseStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        this.minecraft.getTextureManager().bind(BACKGROUND);
        this.blit(matrices, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        this.drawOxygenBufferBar(matrices);

        DrawableUtils.drawCenteredString(matrices, font, I18n.get("ui.galacticraft-rewoven.machine.current_oxygen", (int)(this.menu.machine.getFluidTank().getInvFluid(0).getAmount_F().asInt(1000, RoundingMode.HALF_DOWN))), width / 2, topPos + 33, ChatFormatting.DARK_GRAY.getColor());
        DrawableUtils.drawCenteredString(matrices, font, I18n.get("ui.galacticraft-rewoven.machine.max_oxygen", (int)(this.menu.machine.getFluidTank().getMaxAmount_F(0).asInt(1000, RoundingMode.HALF_DOWN))), width / 2, topPos + 45, ChatFormatting.DARK_GRAY.getColor());
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        DrawableUtils.drawCenteredString(matrices, font, I18n.get("block.galacticraft-rewoven.oxygen_storage_module"), (this.width / 2), this.topPos + 5, ChatFormatting.DARK_GRAY.getColor());
        this.renderTooltip(matrices, mouseX, mouseY);
    }

    private void drawOxygenBufferBar(PoseStack matrices) {
        double oxygenScale = this.menu.machine.getFluidTank().getInvFluid(0).getAmount_F().div(this.menu.machine.getFluidTank().getMaxAmount_F(0)).asInexactDouble();

        this.minecraft.getTextureManager().bind(BACKGROUND);
        this.blit(matrices, this.leftPos + 52, this.topPos + 57, 176, 0, (int) (72.0D * oxygenScale), 3);
    }
}
