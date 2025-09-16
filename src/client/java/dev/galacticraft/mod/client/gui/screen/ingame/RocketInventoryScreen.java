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

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.galacticraft.machinelib.client.api.util.DisplayUtil;
import dev.galacticraft.machinelib.client.api.util.GraphicsUtil;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCFluids;
import dev.galacticraft.mod.screen.RocketMenu;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class RocketInventoryScreen extends AbstractContainerScreen<RocketMenu> {

    private static boolean mouseIn(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
    }

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
        final Component fuel = Component.translatable(Translations.Ui.ROCKET_FUEL);
        graphics.drawString(this.font, fuel, 37 - this.font.width(fuel) / 2, 15 + 3, 4210752, false);

        final float percentage = this.menu.rocket.getScaledFuelLevel(100);
        final Style style = Constant.Text.getStorageLevelStyle(1.0F - 0.01F * percentage);
        final MutableComponent text = Component.literal(String.format("%.1f", percentage)).append(Component.translatable(Translations.Ui.ROCKET_FULL));
        graphics.drawString(this.font, text.withStyle(style), 37 - this.font.width(text) / 2, 20 + 8, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float v, int mouseX, int mouseY) {
        graphics.blit(Constant.ScreenTexture.ROCKET_INVENTORY, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        final int X = this.leftPos + 71;
        final int Y = this.topPos + 7;
        final int width = 34;
        final int height = 38;
        final long amount = this.menu.rocket.getFuel();
        final long capacity = this.menu.rocket.getFuelTankCapacity();

        GraphicsUtil.drawFluid(graphics, X, Y, width, height, capacity, FluidVariant.of(GCFluids.FUEL), amount);

        boolean primary = true;
        for (int y = Y + height - 3; y > Y; y -= 3) {
            graphics.hLine(X, X + (primary ? 9 : 7), y, 0xFFB31212);
            primary = !primary;
        }

        if (mouseIn(mouseX, mouseY, X, Y, width, height)) {
            RenderSystem.disableDepthTest();
            graphics.fill(X, Y, X + width, Y + height, 0x80ffffff);
            RenderSystem.enableDepthTest();

            List<Component> list = new ArrayList<>();
            DisplayUtil.createFluidTooltip(list, GCFluids.FUEL, null, amount, capacity);
            this.setTooltipForNextRenderPass(Lists.transform(list, Component::getVisualOrderText));
        }
    }
}