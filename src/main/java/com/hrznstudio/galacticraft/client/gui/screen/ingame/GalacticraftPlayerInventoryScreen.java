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
import com.hrznstudio.galacticraft.item.GalacticraftItems;
import com.hrznstudio.galacticraft.screen.GalacticraftPlayerInventoryScreenHandler;
import com.hrznstudio.galacticraft.util.OxygenTankUtils;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftPlayerInventoryScreen extends HandledScreen<GalacticraftPlayerInventoryScreenHandler> {
    public static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTexture.getRaw(Constants.ScreenTexture.PLAYER_INVENTORY_SCREEN));
    private static final Identifier OVERLAY = new Identifier(Constants.MOD_ID, Constants.ScreenTexture.getRaw(Constants.ScreenTexture.OVERLAY));

    public GalacticraftPlayerInventoryScreen(GalacticraftPlayerInventoryScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, LiteralText.EMPTY);
    }

    public static boolean isCoordinateBetween(int coordinate, int min, int max) {
        int newMin = Math.min(min, max);
        int newMax = Math.max(min, max);
        return coordinate >= newMin && coordinate <= newMax;
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack matrices, int x, int y) {
        if (GalacticraftPlayerInventoryScreen.isCoordinateBetween(x, this.x + 138, this.x + 138 + 12)
                && GalacticraftPlayerInventoryScreen.isCoordinateBetween(y, this.y + 8, this.y + 8 + 40)) {
            OxygenTank tank = OxygenTankUtils.getOxygenTank(this.handler.inventory.getSlot(GalacticraftPlayerInventoryScreenHandler.OXYGEN_TANK_1_SLOT));
            this.renderTooltip(matrices, new TranslatableText("ui.galacticraft-rewoven.player_inv_screen.oxygen_tank_level", 1, tank.getAmount(), tank.getCapacity()), x, y);
        } else if (GalacticraftPlayerInventoryScreen.isCoordinateBetween(x, this.x + 156, this.x + 156 + 12)
                && GalacticraftPlayerInventoryScreen.isCoordinateBetween(y, this.y + 8, this.y + 8 + 40)) {
            OxygenTank tank = OxygenTankUtils.getOxygenTank(this.handler.inventory.getSlot(GalacticraftPlayerInventoryScreenHandler.OXYGEN_TANK_2_SLOT));
            this.renderTooltip(matrices, new TranslatableText("ui.galacticraft-rewoven.player_inv_screen.oxygen_tank_level", 2, tank.getAmount(), tank.getCapacity()), x, y);
        }
        super.drawMouseoverTooltip(matrices, x, y);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);

        DiffuseLighting.enableGuiDepthLighting();
        this.itemRenderer.renderInGuiWithOverrides(Items.CRAFTING_TABLE.getDefaultStack(), this.x + 6, this.y - 20);
        this.itemRenderer.renderInGuiWithOverrides(GalacticraftItems.OXYGEN_MASK.getDefaultStack(), this.x + 35, this.y - 20);
    }

    public void drawOxygenBufferBar(MatrixStack matrices, double currentOxygen, double maxOxygen, int oxygenDisplayX, int oxygenDisplayY) {
        double oxygenScale = (currentOxygen / maxOxygen);

        this.client.getTextureManager().bindTexture(OVERLAY);
        this.drawTexture(matrices, oxygenDisplayX, oxygenDisplayY, Constants.TextureCoordinate.OXYGEN_DARK_X, Constants.TextureCoordinate.OXYGEN_DARK_Y, Constants.TextureCoordinate.OVERLAY_WIDTH, Constants.TextureCoordinate.OVERLAY_HEIGHT);
        this.drawTexture(matrices, oxygenDisplayX, (oxygenDisplayY - (int) (Constants.TextureCoordinate.OVERLAY_HEIGHT * oxygenScale)) + Constants.TextureCoordinate.OVERLAY_HEIGHT, Constants.TextureCoordinate.OXYGEN_LIGHT_X, Constants.TextureCoordinate.OXYGEN_LIGHT_Y, Constants.TextureCoordinate.OVERLAY_WIDTH, (int) (Constants.TextureCoordinate.OVERLAY_HEIGHT * oxygenScale));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (GalacticraftPlayerInventoryScreen.isCoordinateBetween((int) Math.floor(mouseX), this.x, this.x + 29)
                && GalacticraftPlayerInventoryScreen.isCoordinateBetween((int) Math.floor(mouseY), this.y - 26, this.y)) {
            this.client.openScreen(new InventoryScreen(playerInventory.player));
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void drawBackground(MatrixStack matrices, float v, int mouseX, int mouseY) {
        this.client.getTextureManager().bindTexture(BACKGROUND);
        this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        OxygenTank tank1 = OxygenTankUtils.getOxygenTank(this.handler.inventory.getSlot(GalacticraftPlayerInventoryScreenHandler.OXYGEN_TANK_1_SLOT));
        OxygenTank tank2 = OxygenTankUtils.getOxygenTank(this.handler.inventory.getSlot(GalacticraftPlayerInventoryScreenHandler.OXYGEN_TANK_2_SLOT));

        this.drawOxygenBufferBar(matrices, tank1.getAmount(), tank1.getCapacity(), this.x + 138, this.y + 8);
        this.drawOxygenBufferBar(matrices, tank2.getAmount(), tank2.getCapacity(), this.x + 156, this.y + 8);

        InventoryScreen.drawEntity(this.x + 51, this.y + 75, 30, (float) (this.x + 51) - mouseX, (float) (this.y + 75 - 50) - mouseY, this.client.player);
        this.client.getTextureManager().bindTexture(new Identifier(Constants.MOD_ID, Constants.ScreenTexture.getRaw(Constants.ScreenTexture.PLAYER_INVENTORY_TABS)));
        this.drawTexture(matrices, this.x, this.y - 28, 0, 32, 57, 62);
    }
}