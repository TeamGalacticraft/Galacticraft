/*
 * Copyright (c) 2020 HRZN LTD
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
import com.hrznstudio.galacticraft.component.GalacticraftComponents;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.screen.PlayerInventoryGCScreenHandler;
import com.hrznstudio.galacticraft.util.OxygenUtils;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class PlayerInventoryGCScreen extends HandledScreen<PlayerInventoryGCScreenHandler> {
    public static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.PLAYER_INVENTORY_SCREEN));
    private static final Identifier OVERLAY = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OVERLAY));

    public PlayerInventoryGCScreen(PlayerInventoryGCScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, Constants.Misc.EMPTY);
    }

    public static boolean isCoordinateBetween(int coordinate, int min, int max) {
        int newMin = Math.min(min, max);
        int newMax = Math.max(min, max);
        return coordinate >= newMin && coordinate <= newMax;
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack stack, int x, int y) {
        if (PlayerInventoryGCScreen.isCoordinateBetween(x, this.x + 138, this.x + 138 + 12)
                && PlayerInventoryGCScreen.isCoordinateBetween(y, this.y + 8, this.y + 8 + 40)) {
            ItemStack invStack = this.handler.inventory.getStack(PlayerInventoryGCScreenHandler.OXYGEN_TANK_1_SLOT);
            int storedOxy = invStack.isEmpty() ? 0 : (int) (OxygenUtils.getOxygen(invStack).doubleValue() * 100);
            int maxOxy = invStack.isEmpty() ? 0 : (int) (OxygenUtils.getMaxOxygen(invStack).doubleValue() * 100.0D);
            this.renderTooltip(stack, new LiteralText("Tank 1 Oxygen: " + storedOxy + "/" + maxOxy), x, y);
        } else if (PlayerInventoryGCScreen.isCoordinateBetween(x, this.x + 156, this.x + 156 + 12)
                && PlayerInventoryGCScreen.isCoordinateBetween(y, this.y + 8, this.y + 8 + 40)) {
            ItemStack invStack = this.handler.inventory.getStack(PlayerInventoryGCScreenHandler.OXYGEN_TANK_2_SLOT);
            int storedOxy = invStack.isEmpty() ? 0 : (int) (OxygenUtils.getOxygen(invStack).doubleValue() * 100);
            int maxOxy = invStack.isEmpty() ? 0 : (int) (OxygenUtils.getMaxOxygen(invStack).doubleValue() * 100.0D);
            this.renderTooltip(stack, new LiteralText("Tank 2 Oxygen: " + storedOxy + "/" + maxOxy), x, y);
        }
        super.drawMouseoverTooltip(stack, x, y);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(stack, mouseX, mouseY);

        DiffuseLighting.enableGuiDepthLighting();
        this.itemRenderer.renderInGuiWithOverrides(Items.CRAFTING_TABLE.getDefaultStack(), this.x + 6, this.y - 20);
        this.itemRenderer.renderInGuiWithOverrides(GalacticraftItems.OXYGEN_MASK.getDefaultStack(), this.x + 35, this.y - 20);
    }

    public void drawOxygenBufferBar(MatrixStack stack, float currentOxygen, float maxOxygen, int oxygenDisplayX, int oxygenDisplayY) {
        float oxygenScale = (currentOxygen / maxOxygen);

        this.client.getTextureManager().bindTexture(OVERLAY);
        this.drawTexture(stack, oxygenDisplayX, oxygenDisplayY, Constants.TextureCoordinates.OXYGEN_DARK_X, Constants.TextureCoordinates.OXYGEN_DARK_Y, Constants.TextureCoordinates.OVERLAY_WIDTH, Constants.TextureCoordinates.OVERLAY_HEIGHT);
        this.drawTexture(stack, oxygenDisplayX, (oxygenDisplayY - (int) (Constants.TextureCoordinates.OVERLAY_HEIGHT * oxygenScale)) + Constants.TextureCoordinates.OVERLAY_HEIGHT, Constants.TextureCoordinates.OXYGEN_LIGHT_X, Constants.TextureCoordinates.OXYGEN_LIGHT_Y, Constants.TextureCoordinates.OVERLAY_WIDTH, (int) (Constants.TextureCoordinates.OVERLAY_HEIGHT * oxygenScale));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (PlayerInventoryGCScreen.isCoordinateBetween((int) Math.floor(mouseX), this.x, this.x + 29)
                && PlayerInventoryGCScreen.isCoordinateBetween((int) Math.floor(mouseY), this.y - 26, this.y)) {
            this.client.openScreen(new InventoryScreen(playerInventory.player));
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void drawBackground(MatrixStack stack, float v, int mouseX, int mouseY) {
        this.client.getTextureManager().bindTexture(BACKGROUND);
        this.drawTexture(stack, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        ItemStack tank1 = GalacticraftComponents.GEAR_INVENTORY_COMPONENT.get(this.playerInventory.player).getStack(6);
        ItemStack tank2 = GalacticraftComponents.GEAR_INVENTORY_COMPONENT.get(this.playerInventory.player).getStack(7);

        this.drawOxygenBufferBar(stack, OxygenUtils.getOxygen(tank1).floatValue() * 100.0F, OxygenUtils.getMaxOxygen(tank1).floatValue() * 100.0F, this.x + 138, this.y + 8);
        this.drawOxygenBufferBar(stack, OxygenUtils.getOxygen(tank2).floatValue() * 100.0F, OxygenUtils.getMaxOxygen(tank2).floatValue() * 100.0F, this.x + 156, this.y + 8);

        InventoryScreen.drawEntity(this.x + 51, this.y + 75, 30, (float) (this.x + 51) - mouseX, (float) (this.y + 75 - 50) - mouseY, this.client.player);
        this.client.getTextureManager().bindTexture(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.PLAYER_INVENTORY_TABS)));
        this.drawTexture(stack, this.x, this.y - 28, 0, 32, 57, 62);
    }
}