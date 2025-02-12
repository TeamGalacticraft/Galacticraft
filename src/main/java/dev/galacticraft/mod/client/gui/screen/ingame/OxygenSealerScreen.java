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

import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.api.menu.MachineMenu;
import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.machine.OxygenSealerBlockEntity;
import dev.galacticraft.mod.machine.GCMachineStatuses;
import dev.galacticraft.mod.screen.OxygenSealerMenu;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class OxygenSealerScreen extends MachineScreen<OxygenSealerBlockEntity, OxygenSealerMenu> {
    public OxygenSealerScreen(OxygenSealerMenu handler, Inventory inv, Component title) {
        super(handler, title, Constant.ScreenTexture.OXYGEN_SEALER_SCREEN);
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderForeground(graphics, mouseX, mouseY, delta);

        MachineStatus status = this.menu.state.getStatus();
        graphics.drawString(this.font, Component.translatable(Translations.Ui.MACHINE_STATUS, status != null ? status.getText() : Component.empty()), this.leftPos + 50, this.topPos + 30, ChatFormatting.DARK_GRAY.getColor(), false);
        if (status != null)
        {
            if (!status.equals(GCMachineStatuses.BLOCKED)) {
                int sealCheckTime = this.menu.sealTickTime;
                //TODO: make this a bar that goes down over time to match gc4's sealer screen?
                graphics.drawString(this.font, Component.literal("Sealer Recheck: ").append(String.valueOf(sealCheckTime)), this.leftPos + 50, this.topPos + 50, ChatFormatting.YELLOW.getColor(), false);
            }
        }
    }
}
