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
import dev.galacticraft.mod.content.entity.ScalableFuelLevel;
import dev.galacticraft.mod.screen.ParachestMenu;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class ParachestScreen extends AbstractContainerScreen<ParachestMenu> {
    private static ResourceLocation[] parachestTexture = new ResourceLocation[4];

    private static boolean mouseIn(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
    }

    static {
        for (int i = 0; i < 4; i++) {
            parachestTexture[i] = Constant.id("textures/gui/chest_" + i * 18 + ".png");
        }
    }

    private int inventorySlots = 0;

    public ParachestScreen(ParachestMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
        this.inventorySlots = abstractContainerMenu.getContainer().getContainerSize();
        this.imageHeight = 146 + this.inventorySlots * 2;
        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 103 + (this.inventorySlots == 3 ? 2 : 4);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float f, int mouseX, int mouseY) {
        ResourceLocation texture = parachestTexture[(this.inventorySlots - 3) / 18];
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        graphics.blit(texture, i, j, 0, 0, this.imageWidth, this.imageHeight);

        var container = getMenu().getContainer();

        if (container instanceof ScalableFuelLevel scalable) {
            final int width = 34;
            final int height = 28;
            final int X = i + 17;
            final int Y = j + (this.inventorySlots == 3 ? 40 : 42) - height + this.inventorySlots * 2;
            final long capacity = FluidConstants.BUCKET * 5;
            final long amount = (long) scalable.getScaledFuelLevel(capacity);

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

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}