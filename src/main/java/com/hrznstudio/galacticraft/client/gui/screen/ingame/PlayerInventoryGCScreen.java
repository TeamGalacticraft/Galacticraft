/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.client.gui.screen.ingame;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.attribute.oxygen.OxygenTank;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.screen.PlayerInventoryGCScreenHandler;
import com.hrznstudio.galacticraft.util.OxygenTankUtils;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class PlayerInventoryGCScreen extends AbstractContainerScreen<PlayerInventoryGCScreenHandler> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.PLAYER_INVENTORY_SCREEN));
    private static final ResourceLocation OVERLAY = new ResourceLocation(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OVERLAY));

    public PlayerInventoryGCScreen(PlayerInventoryGCScreenHandler handler, Inventory inv, Component title) {
        super(handler, inv, Constants.Misc.EMPTY_TEXT);
    }

    public static boolean isCoordinateBetween(int coordinate, int min, int max) {
        int newMin = Math.min(min, max);
        int newMax = Math.max(min, max);
        return coordinate >= newMin && coordinate <= newMax;
    }

    @Override
    protected void renderTooltip(PoseStack matrices, int x, int y) {
        if (PlayerInventoryGCScreen.isCoordinateBetween(x, this.leftPos + 138, this.leftPos + 138 + 12)
                && PlayerInventoryGCScreen.isCoordinateBetween(y, this.topPos + 8, this.topPos + 8 + 40)) {
            OxygenTank tank = OxygenTankUtils.getOxygenTank(this.menu.inventory.getSlot(PlayerInventoryGCScreenHandler.OXYGEN_TANK_1_SLOT));
            this.renderTooltip(matrices, new TranslatableComponent("ui.galacticraft-rewoven.player_inv_screen.oxygen_tank_level", 1, tank.getAmount(), tank.getCapacity()), x, y);
        } else if (PlayerInventoryGCScreen.isCoordinateBetween(x, this.leftPos + 156, this.leftPos + 156 + 12)
                && PlayerInventoryGCScreen.isCoordinateBetween(y, this.topPos + 8, this.topPos + 8 + 40)) {
            OxygenTank tank = OxygenTankUtils.getOxygenTank(this.menu.inventory.getSlot(PlayerInventoryGCScreenHandler.OXYGEN_TANK_2_SLOT));
            this.renderTooltip(matrices, new TranslatableComponent("ui.galacticraft-rewoven.player_inv_screen.oxygen_tank_level", 2, tank.getAmount(), tank.getCapacity()), x, y);
        }
        super.renderTooltip(matrices, x, y);
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.renderTooltip(matrices, mouseX, mouseY);

        Lighting.setupFor3DItems();
        this.itemRenderer.renderAndDecorateItem(Items.CRAFTING_TABLE.getDefaultInstance(), this.leftPos + 6, this.topPos - 20);
        this.itemRenderer.renderAndDecorateItem(GalacticraftItems.OXYGEN_MASK.getDefaultInstance(), this.leftPos + 35, this.topPos - 20);
    }

    public void drawOxygenBufferBar(PoseStack matrices, double currentOxygen, double maxOxygen, int oxygenDisplayX, int oxygenDisplayY) {
        double oxygenScale = (currentOxygen / maxOxygen);

        this.minecraft.getTextureManager().bind(OVERLAY);
        this.blit(matrices, oxygenDisplayX, oxygenDisplayY, Constants.TextureCoordinates.OXYGEN_DARK_X, Constants.TextureCoordinates.OXYGEN_DARK_Y, Constants.TextureCoordinates.OVERLAY_WIDTH, Constants.TextureCoordinates.OVERLAY_HEIGHT);
        this.blit(matrices, oxygenDisplayX, (oxygenDisplayY - (int) (Constants.TextureCoordinates.OVERLAY_HEIGHT * oxygenScale)) + Constants.TextureCoordinates.OVERLAY_HEIGHT, Constants.TextureCoordinates.OXYGEN_LIGHT_X, Constants.TextureCoordinates.OXYGEN_LIGHT_Y, Constants.TextureCoordinates.OVERLAY_WIDTH, (int) (Constants.TextureCoordinates.OVERLAY_HEIGHT * oxygenScale));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (PlayerInventoryGCScreen.isCoordinateBetween((int) Math.floor(mouseX), this.leftPos, this.leftPos + 29)
                && PlayerInventoryGCScreen.isCoordinateBetween((int) Math.floor(mouseY), this.topPos - 26, this.topPos)) {
            this.minecraft.setScreen(new InventoryScreen(inventory.player));
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void renderBg(PoseStack matrices, float v, int mouseX, int mouseY) {
        this.minecraft.getTextureManager().bind(BACKGROUND);
        this.blit(matrices, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        OxygenTank tank1 = OxygenTankUtils.getOxygenTank(this.menu.inventory.getSlot(PlayerInventoryGCScreenHandler.OXYGEN_TANK_1_SLOT));
        OxygenTank tank2 = OxygenTankUtils.getOxygenTank(this.menu.inventory.getSlot(PlayerInventoryGCScreenHandler.OXYGEN_TANK_2_SLOT));

        this.drawOxygenBufferBar(matrices, tank1.getAmount(), tank1.getCapacity(), this.leftPos + 138, this.topPos + 8);
        this.drawOxygenBufferBar(matrices, tank2.getAmount(), tank2.getCapacity(), this.leftPos + 156, this.topPos + 8);

        InventoryScreen.renderEntityInInventory(this.leftPos + 51, this.topPos + 75, 30, (float) (this.leftPos + 51) - mouseX, (float) (this.topPos + 75 - 50) - mouseY, this.minecraft.player);
        this.minecraft.getTextureManager().bind(new ResourceLocation(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.PLAYER_INVENTORY_TABS)));
        this.blit(matrices, this.leftPos, this.topPos - 28, 0, 32, 57, 62);
    }
}