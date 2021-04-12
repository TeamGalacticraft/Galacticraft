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
import com.hrznstudio.galacticraft.screen.ElectricArcFurnaceScreenHandler;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ElectricArcFurnaceScreen extends MachineHandledScreen<ElectricArcFurnaceScreenHandler> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.ELECTRIC_ARC_FURNACE_SCREEN));

    private static final int ARROW_X = 78;
    private static final int ARROW_Y = 24;

    private static final int LIT_ARROW_X = 176;
    private static final int LIT_ARROW_Y = 0;

    private static final int ARROW_WIDTH = 22;
    private static final int ARROW_HEIGHT = 15;

    public ElectricArcFurnaceScreen(ElectricArcFurnaceScreenHandler screenHandler, Inventory playerInventory, Component title) {
        super(screenHandler, playerInventory, screenHandler.machine.getLevel(), screenHandler.machine.getBlockPos(), title);
        addWidget(new CapacitorWidget(screenHandler.machine.getCapacitor(), 8, 29, 48, this::getEnergyTooltipLines, screenHandler.machine::getStatus));
    }

    @Override
    protected void renderBg(PoseStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        this.minecraft.getTextureManager().bind(BACKGROUND);
        this.blit(matrices, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        if (menu.machine.cookLength != 0 && menu.machine.cookTime != 0) {
            double scale = ((double)menu.machine.cookTime) / ((double)menu.machine.cookLength);

            this.blit(matrices, this.leftPos + ARROW_X, this.topPos + ARROW_Y, LIT_ARROW_X, LIT_ARROW_Y, (int) (((double)ARROW_WIDTH) * scale), ARROW_HEIGHT);
        }

        DrawableUtils.drawCenteredString(matrices, font, new TranslatableComponent("block.galacticraft-rewoven.electric_arc_furnace"), this.width / 2, this.topPos + 5, ChatFormatting.DARK_GRAY.getColor());
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        this.renderTooltip(matrices, mouseX, mouseY);
    }
}
