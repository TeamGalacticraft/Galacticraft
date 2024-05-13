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

import dev.galacticraft.machinelib.api.menu.RecipeMachineMenu;
import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.machine.ElectricArcFurnaceBlockEntity;
import dev.galacticraft.mod.util.DrawableUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.BlastingRecipe;

public class ElectricArcFurnaceScreen extends MachineScreen<ElectricArcFurnaceBlockEntity, RecipeMachineMenu<Container, BlastingRecipe, ElectricArcFurnaceBlockEntity>> {
    private static final int ARROW_X = 68;
    private static final int ARROW_Y = 35;
    private static final int ARROW_U = 176;
    private static final int ARROW_V = 0;
    private static final int ARROW_WIDTH = 26;
    private static final int ARROW_HEIGHT = 16;

    public ElectricArcFurnaceScreen(RecipeMachineMenu<Container, BlastingRecipe, ElectricArcFurnaceBlockEntity> handler, Inventory inv, Component title) {
        super(handler, title, Constant.ScreenTexture.ELECTRIC_ARC_FURNACE_SCREEN);
    }

    @Override
    protected void renderMachineBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderMachineBackground(graphics, mouseX, mouseY, delta);
        if (this.menu.getMaxProgress() != 0 && this.menu.getProgress() != 0) {
            double scale = ((double)this.menu.getProgress()) / ((double)this.menu.getMaxProgress());

            DrawableUtil.drawProgressTexture(graphics.pose(), this.leftPos + ARROW_X, this.topPos + ARROW_Y, ARROW_U, ARROW_V, (float) (ARROW_WIDTH * scale), ARROW_HEIGHT);
        }
    }
}
