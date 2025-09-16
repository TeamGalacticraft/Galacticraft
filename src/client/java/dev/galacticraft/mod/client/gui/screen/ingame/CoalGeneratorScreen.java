/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.machine.CoalGeneratorBlockEntity;
import dev.galacticraft.mod.screen.CoalGeneratorMenu;
import dev.galacticraft.mod.util.DrawableUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class CoalGeneratorScreen extends MachineScreen<CoalGeneratorBlockEntity, CoalGeneratorMenu> {
    private static final int FIRE_X = 80;
    private static final int FIRE_Y = 27;
    private static final int FIRE_U = 176;
    private static final int FIRE_V = 0;
    private static final int FIRE_WIDTH = 14;
    private static final int FIRE_HEIGHT = 14;

    public CoalGeneratorScreen(CoalGeneratorMenu handler, Inventory inv, Component title) {
        super(handler, title, Constant.ScreenTexture.COAL_GENERATOR_SCREEN);
    }

    @Override
    protected void renderMachineBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderMachineBackground(graphics, mouseX, mouseY, delta);
        if (this.menu.getFuelLength() > 0) {
            float scale = 1.0F - (float) this.menu.getFuelTime() / (float) this.menu.getFuelLength();
            int height = Mth.ceil((FIRE_HEIGHT - 1) * scale) + 1;
            DrawableUtil.drawProgressTexture(graphics.pose(), this.leftPos + FIRE_X, this.topPos + FIRE_Y + FIRE_HEIGHT - height, 0, FIRE_U, FIRE_V + FIRE_HEIGHT - height, FIRE_WIDTH, height);
        }
    }
}
