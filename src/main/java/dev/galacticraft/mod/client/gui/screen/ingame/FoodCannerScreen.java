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
import dev.galacticraft.mod.client.gui.FoodCannerProgressAnimation;
import dev.galacticraft.mod.content.block.entity.machine.FoodCannerBlockEntity;
import dev.galacticraft.mod.network.c2s.EjectCanPayload;
import dev.galacticraft.mod.screen.FoodCannerMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import static dev.galacticraft.mod.Constant.FoodCanner.*;

@Environment(EnvType.CLIENT)
public class FoodCannerScreen extends MachineScreen<FoodCannerBlockEntity, FoodCannerMenu> {
    public FoodCannerScreen(FoodCannerMenu handler, Inventory inv, Component title) {
        super(handler, title, SCREEN_TEXTURE);
        this.imageHeight = 171;
        this.capacitorX = 8;
        this.capacitorY = 13;
        this.titleLabelY = 4;
    }

    @Override
    protected void renderMachineBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderMachineBackground(graphics, mouseX, mouseY, delta);
        int progress = this.menu.getProgress();
        if (progress > 0) {
            FoodCannerProgressAnimation.render(graphics, this.leftPos + PROGRESS_X, this.topPos + PROGRESS_Y, progress, this.menu.getRowsConsumed());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseIn(mouseX, mouseY, this.leftPos + CURRENT_X, this.topPos + CURRENT_Y, 18, 18)) {
            ClientPlayNetworking.send(new EjectCanPayload());
            this.playButtonSound();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
