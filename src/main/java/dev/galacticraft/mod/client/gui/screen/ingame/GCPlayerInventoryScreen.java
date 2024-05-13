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
import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.screen.GCPlayerInventoryMenu;
import dev.galacticraft.mod.util.DrawableUtil;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GCPlayerInventoryScreen extends AbstractContainerScreen<GCPlayerInventoryMenu> {
    public GCPlayerInventoryScreen(GCPlayerInventoryMenu handler, Inventory inv, Component title) {
        super(handler, inv, Component.empty());
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 129, this.topPos + 8, Constant.TextureCoordinate.OVERLAY_WIDTH, Constant.TextureCoordinate.OVERLAY_HEIGHT)) {
            Storage<FluidVariant> storage = ContainerItemContext.withConstant(this.menu.inventory.getItem(GCPlayerInventoryMenu.OXYGEN_TANK_1_SLOT)).find(FluidStorage.ITEM);
            if (storage != null) {
                long capacity = 0;
                long amount = 0;
                for (StorageView<FluidVariant> view : storage) {
                    if (view.isResourceBlank() || view.getResource().getFluid() == Gases.OXYGEN) {
                        capacity += view.getCapacity();
                        amount += view.getAmount();
                    }
                }
                graphics.renderTooltip(this.font, Component.translatable(Translations.Ui.OXYGEN_TANK_LEVEL, 1, amount, capacity), mouseX, mouseY);
            }
        } else if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + 152, this.topPos + 8, Constant.TextureCoordinate.OVERLAY_WIDTH, Constant.TextureCoordinate.OVERLAY_HEIGHT)) {
            Storage<FluidVariant> storage = ContainerItemContext.withConstant(this.menu.inventory.getItem(GCPlayerInventoryMenu.OXYGEN_TANK_2_SLOT)).find(FluidStorage.ITEM);
            if (storage != null) {
                long capacity = 0;
                long amount = 0;
                for (StorageView<FluidVariant> view : storage) {
                    if (view.isResourceBlank() || view.getResource().getFluid() == Gases.OXYGEN) {
                        capacity += view.getCapacity();
                        amount += view.getAmount();
                    }
                }
                graphics.renderTooltip(this.font, Component.translatable(Translations.Ui.OXYGEN_TANK_LEVEL, 2, amount, capacity), mouseX, mouseY);
            }
        }
        super.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public void renderBg(GuiGraphics graphics, float v, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(Constant.ScreenTexture.PLAYER_INVENTORY_SCREEN, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        Storage<FluidVariant> storage1 = ContainerItemContext.withConstant(this.menu.inventory.getItem(GCPlayerInventoryMenu.OXYGEN_TANK_1_SLOT)).find(FluidStorage.ITEM);
        if (storage1 != null) {
            long capacity = 0;
            long amount = 0;
            for (StorageView<FluidVariant> view : storage1) {
                if (view.isResourceBlank() || view.getResource().getFluid() == Gases.OXYGEN) {
                    capacity += view.getCapacity();
                    amount += view.getAmount();
                }
            }

            if (capacity > 0) {
                DrawableUtil.drawOxygenBuffer(graphics.pose(), this.leftPos + 129, this.topPos + 8, amount, capacity);
            }
        }
        Storage<FluidVariant> storage2 = ContainerItemContext.withConstant(this.menu.inventory.getItem(GCPlayerInventoryMenu.OXYGEN_TANK_2_SLOT)).find(FluidStorage.ITEM);
        if (storage2 != null) {
            long capacity = 0;
            long amount = 0;
            for (StorageView<FluidVariant> view : storage2) {
                if (view.isResourceBlank() || view.getResource().getFluid() == Gases.OXYGEN) {
                    capacity += view.getCapacity();
                    amount += view.getAmount();
                }
            }

            if (capacity > 0) {
                DrawableUtil.drawOxygenBuffer(graphics.pose(), this.leftPos + 152, this.topPos + 8, amount, capacity);
            }
        }

        InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, this.leftPos + 26, this.topPos + 8, this.leftPos + 75, this.topPos + 78, 30, 0.0625F, mouseX, mouseY, this.minecraft.player);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int i, int j) {}
}