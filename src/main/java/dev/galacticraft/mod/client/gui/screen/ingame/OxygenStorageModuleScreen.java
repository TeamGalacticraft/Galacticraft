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

import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.api.storage.slot.FluidResourceSlot;
import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.machine.OxygenStorageModuleBlockEntity;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class OxygenStorageModuleScreen extends MachineScreen<OxygenStorageModuleBlockEntity, MachineMenu<OxygenStorageModuleBlockEntity>> {
    public OxygenStorageModuleScreen(MachineMenu<OxygenStorageModuleBlockEntity> handler, Inventory inv, Component title) {
        super(handler, title, Constant.ScreenTexture.OXYGEN_STORAGE_MODULE_SCREEN);
    }

    @Override
    protected void renderMachineBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderMachineBackground(graphics, mouseX, mouseY, delta);
        this.drawOxygenBufferBar(graphics);

        graphics.drawCenteredString(font, Component.translatable(Translations.Ui.CURRENT_OXYGEN, this.menu.fluidStorage.getSlot(OxygenStorageModuleBlockEntity.OXYGEN_TANK).getAmount()), width / 2, topPos + 33, ChatFormatting.DARK_GRAY.getColor());
        graphics.drawCenteredString(font, Component.translatable(Translations.Ui.MAX_OXYGEN, this.menu.fluidStorage.getSlot(OxygenStorageModuleBlockEntity.OXYGEN_TANK).getCapacity()), width / 2, topPos + 45, ChatFormatting.DARK_GRAY.getColor());
    }

    private void drawOxygenBufferBar(GuiGraphics graphics) {
        FluidResourceSlot slot = this.menu.fluidStorage.getSlot(OxygenStorageModuleBlockEntity.OXYGEN_TANK);
        double oxygenScale = (double)slot.getAmount() / (double)slot.getCapacity();

        graphics.blit(Constant.ScreenTexture.OXYGEN_STORAGE_MODULE_SCREEN, this.leftPos + 52, this.topPos + 57, 176, 0, (int) (72.0D * oxygenScale), 3);
    }
}
