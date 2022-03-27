/*
 * Copyright (c) 2019-2022 Team Galacticraft
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
import dev.galacticraft.api.gas.GasVariant;
import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.api.transfer.v1.gas.GasStorage;
import dev.galacticraft.api.gas.Gas;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.item.GalacticraftItem;
import dev.galacticraft.mod.screen.GalacticraftPlayerInventoryScreenHandler;
import dev.galacticraft.mod.util.DrawableUtil;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftPlayerInventoryScreen extends HandledScreen<GalacticraftPlayerInventoryScreenHandler> {
    public GalacticraftPlayerInventoryScreen(GalacticraftPlayerInventoryScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, LiteralText.EMPTY);
    }

    public static boolean isCoordinateBetween(int coordinate, int min, int max) {
        int newMin = Math.min(min, max);
        int newMax = Math.max(min, max);
        return coordinate >= newMin && coordinate <= newMax;
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        if (DrawableUtil.isWithin(mouseX, mouseY, this.x + 129, this.y + 8, Constant.TextureCoordinate.OVERLAY_WIDTH, Constant.TextureCoordinate.OVERLAY_HEIGHT)) {
            Storage<GasVariant> storage = ContainerItemContext.withInitial(this.handler.inventory.getStack(GalacticraftPlayerInventoryScreenHandler.OXYGEN_TANK_1_SLOT)).find(GasStorage.ITEM);
            if (storage != null) {
                try (Transaction transaction = Transaction.openOuter()) {
                    StorageView<GasVariant> exact = storage.exactView(transaction, GasVariant.of(Gases.OXYGEN));
                    if (exact != null) {
                        this.renderTooltip(matrices, new TranslatableText("ui.galacticraft.player_inv_screen.oxygen_tank_level", 1, exact.getAmount(), exact.getCapacity()), mouseX, mouseY);
                    } else {
                        long l = storage.extract(GasVariant.of(Gases.OXYGEN), Long.MAX_VALUE, transaction);
                        this.renderTooltip(matrices, new TranslatableText("ui.galacticraft.player_inv_screen.oxygen_tank_level", 1, l, "???"), mouseX, mouseY);
                    }
                }
            }
        } else if (DrawableUtil.isWithin(mouseX, mouseY, this.x + 152, this.y + 8, Constant.TextureCoordinate.OVERLAY_WIDTH, Constant.TextureCoordinate.OVERLAY_HEIGHT)) {
            Storage<GasVariant> storage = ContainerItemContext.withInitial(this.handler.inventory.getStack(GalacticraftPlayerInventoryScreenHandler.OXYGEN_TANK_2_SLOT)).find(GasStorage.ITEM);
            if (storage != null) {
                try (Transaction transaction = Transaction.openOuter()) {
                    StorageView<GasVariant> exact = storage.exactView(transaction, GasVariant.of(Gases.OXYGEN));
                    if (exact != null) {
                        this.renderTooltip(matrices, new TranslatableText("ui.galacticraft.player_inv_screen.oxygen_tank_level", 2, exact.getAmount(), exact.getCapacity()), mouseX, mouseY);
                    } else {
                        long l = storage.extract(GasVariant.of(Gases.OXYGEN), Long.MAX_VALUE, transaction);
                        this.renderTooltip(matrices, new TranslatableText("ui.galacticraft.player_inv_screen.oxygen_tank_level", 2, l, "???"), mouseX, mouseY);
                    }
                }
            }
        }
        super.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);

        DiffuseLighting.enableGuiDepthLighting();
        this.itemRenderer.renderInGuiWithOverrides(Items.CRAFTING_TABLE.getDefaultStack(), this.x + 6, this.y - 20);
        this.itemRenderer.renderInGuiWithOverrides(GalacticraftItem.OXYGEN_MASK.getDefaultStack(), this.x + 35, this.y - 20);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (GalacticraftPlayerInventoryScreen.isCoordinateBetween((int) Math.floor(mouseX), this.x, this.x + 29)
                && GalacticraftPlayerInventoryScreen.isCoordinateBetween((int) Math.floor(mouseY), this.y - 26, this.y)) {
            this.client.setScreen(new InventoryScreen(this.handler.player));
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void drawBackground(MatrixStack matrices, float v, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.PLAYER_INVENTORY_SCREEN);
        this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        Storage<GasVariant> storage1 = ContainerItemContext.withInitial(this.handler.inventory.getStack(GalacticraftPlayerInventoryScreenHandler.OXYGEN_TANK_1_SLOT)).find(GasStorage.ITEM);
        if (storage1 != null) {
            try (Transaction transaction = Transaction.openOuter()) {
                StorageView<GasVariant> exact = storage1.exactView(transaction, GasVariant.of(Gases.OXYGEN));
                if (exact != null) {
                    DrawableUtil.drawOxygenBuffer(matrices, this.x + 129, this.y + 8, exact.getAmount(), exact.getCapacity());
                }
            }
        }
        Storage<GasVariant> storage2 = ContainerItemContext.withInitial(this.handler.inventory.getStack(GalacticraftPlayerInventoryScreenHandler.OXYGEN_TANK_2_SLOT)).find(GasStorage.ITEM);
        if (storage2 != null) {
            try (Transaction transaction = Transaction.openOuter()) {
                StorageView<GasVariant> exact = storage2.exactView(transaction, GasVariant.of(Gases.OXYGEN));
                if (exact != null) {
                    DrawableUtil.drawOxygenBuffer(matrices, this.x + 152, this.y + 8, exact.getAmount(), exact.getCapacity());
                }
            }
        }

        InventoryScreen.drawEntity(this.x + 51, this.y + 75, 30, (float) (this.x + 51) - mouseX, (float) (this.y + 75 - 50) - mouseY, this.client.player);
        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.PLAYER_INVENTORY_TABS);
        this.drawTexture(matrices, this.x, this.y - 28, 0, 32, 57, 62);
    }
}