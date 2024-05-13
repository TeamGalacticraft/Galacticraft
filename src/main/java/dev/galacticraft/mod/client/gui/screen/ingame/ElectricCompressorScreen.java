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
import dev.galacticraft.machinelib.api.menu.RecipeMachineMenu;
import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.machine.ElectricCompressorBlockEntity;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.util.DrawableUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingContainer;

@Environment(EnvType.CLIENT)
public class ElectricCompressorScreen extends MachineScreen<ElectricCompressorBlockEntity, RecipeMachineMenu<CraftingContainer, CompressingRecipe, ElectricCompressorBlockEntity>> {
    private static final int PROGRESS_U = 177;
    private static final int PROGRESS_V = 0;
    private static final int PROGRESS_X = 87;
    private static final int PROGRESS_Y = 28;
    private static final int PROGRESS_WIDTH = 52;
    private static final int PROGRESS_HEIGHT = 25;

    public ElectricCompressorScreen(RecipeMachineMenu<CraftingContainer, CompressingRecipe, ElectricCompressorBlockEntity> handler, Inventory inv, Component title) {
        super(handler, title, Constant.ScreenTexture.ELECTRIC_COMPRESSOR_SCREEN);
    }

    @Override
    protected void renderMachineBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderMachineBackground(graphics, mouseX, mouseY, delta);
        this.drawCraftProgressBar(graphics);
    }

    protected void drawCraftProgressBar(GuiGraphics graphics) {
        float progressScale = (((float)this.menu.getProgress()) / ((float)this.menu.getMaxProgress()));

        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.ELECTRIC_COMPRESSOR_SCREEN);
        DrawableUtil.drawProgressTexture(graphics.pose(), this.leftPos + PROGRESS_X, this.topPos + PROGRESS_Y, PROGRESS_U, PROGRESS_V, (PROGRESS_WIDTH * progressScale), PROGRESS_HEIGHT);
    }
}