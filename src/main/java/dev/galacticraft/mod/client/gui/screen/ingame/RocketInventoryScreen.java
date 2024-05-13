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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.screen.RocketMenu;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class RocketInventoryScreen extends AbstractContainerScreen<RocketMenu> {
    public RocketInventoryScreen(RocketMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelY = 0;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, Component.translatable(Translations.Ui.ROCKET_FUEL), 125, 15 + 3, 4210752, false);

        final double percentage = this.menu.rocket.getScaledFuelLevel(100);
        final ChatFormatting color = percentage > 80.0D ? ChatFormatting.GREEN : percentage > 40.0D ? ChatFormatting.GOLD : ChatFormatting.RED;
        final String str = percentage + I18n.get(Translations.Ui.ROCKET_FULL);
        graphics.drawString(this.font, Component.literal(str).withStyle(color), 117 - str.length() / 2, 20 + 8, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float v, int mouseX, int mouseY) {
        graphics.blit(Constant.ScreenTexture.ROCKET_INVENTORY, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        final int fuelLevel = this.menu.rocket.getScaledFuelLevel(38);
        graphics.blit(Constant.ScreenTexture.ROCKET_INVENTORY, this.leftPos + 71, this.topPos + 45 - fuelLevel, 176, 38 - fuelLevel, 42, fuelLevel);
    }
}