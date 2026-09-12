/*
 * Copyright (c) 2019-2026 Team Galacticraft
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
import dev.galacticraft.mod.content.block.entity.machine.OxygenDetectorBlockEntity;
import dev.galacticraft.mod.network.c2s.OxygenDetectorControlPayload;
import dev.galacticraft.mod.screen.OxygenDetectorMenu;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class OxygenDetectorScreen extends MachineScreen<OxygenDetectorBlockEntity, OxygenDetectorMenu> {
    private Button switchButton;

    public OxygenDetectorScreen(OxygenDetectorMenu menu, Inventory inv, Component title) {
        super(menu, title, Constant.ScreenTexture.OXYGEN_DETECTOR_SCREEN);
    }

    private void invertMode() {
        boolean mode = !menu.be.getMode();
        ClientPlayNetworking.send(new OxygenDetectorControlPayload(mode));
    }

    @Override
    protected void init() {
        super.init();

        switchButton = new Button(this.leftPos + 76, this.topPos + 24, 24, 33, Component.empty(), btn -> {invertMode();}, s -> s.get()) {
            @Override
            protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {};
        };
        addRenderableWidget(switchButton);
    }

    @Override
    protected void renderMachineBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int target_y = menu.be.isAnd() ? 0 : 34;
        graphics.blit(Constant.ScreenTexture.OXYGEN_DETECTOR_SCREEN,
            switchButton.getX(), switchButton.getY(),
            176, target_y,
            switchButton.getWidth(), switchButton.getHeight()
        );

        if (mouseIn(mouseX, mouseY, switchButton.getX(), switchButton.getY(), switchButton.getWidth(), switchButton.getHeight())) {
            String mode_str = Component.translatable(menu.be.isAnd() ? Translations.Ui.DETECTOR_AND : Translations.Ui.DETECTOR_OR).getString();
            Component mode = Component.translatable(Translations.Ui.DETECTOR_MODE, mode_str);

            graphics.renderTooltip(font, mode, mouseX, mouseY);
        }
    }
}
