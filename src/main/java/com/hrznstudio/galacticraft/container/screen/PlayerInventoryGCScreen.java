/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.container.screen;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.container.PlayerInventoryGCContainer;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.items.OxygenTankItem;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class PlayerInventoryGCScreen extends ContainerScreen<PlayerInventoryGCContainer> {
    public static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.PLAYER_INVENTORY_SCREEN));

    public PlayerInventoryGCScreen(PlayerEntity player) {
        super(new PlayerInventoryGCContainer(player.inventory, player), player.inventory, new TranslatableText(Constants.MOD_ID + ".player_inv_screen"));
    }

    public static boolean isCoordinateBetween(int coordinate, int min, int max) {
        int newMin = Math.min(min, max);
        int newMax = Math.max(min, max);
        return coordinate >= newMin && coordinate <= newMax;
    }

    @Override
    protected void drawMouseoverTooltip(int mouseX, int mouseY) {
        if (PlayerInventoryGCScreen.isCoordinateBetween(mouseX, this.x + 138, this.x + 138 + 12)
                && PlayerInventoryGCScreen.isCoordinateBetween(mouseX, this.y + 8, this.y + 8 + 40)) {
            ItemStack invStack = container.inventory.getInvStack(PlayerInventoryGCContainer.OXYGEN_TANK_1_SLOT);
            int storedOxy = invStack.isEmpty() ? 0 : OxygenTankItem.getOxygenCount(invStack);
            int maxOxy = invStack.isEmpty() ? 0 : OxygenTankItem.getMaxOxygen(invStack);
            this.renderTooltip("Tank 1 Oxygen: " + storedOxy + "/" + maxOxy, mouseX, mouseY);
        } else if (PlayerInventoryGCScreen.isCoordinateBetween(mouseX, x + 156, x + 156 + 12)
                && PlayerInventoryGCScreen.isCoordinateBetween(mouseY, y + 8, y + 8 + 40)) {
            ItemStack invStack = container.inventory.getInvStack(PlayerInventoryGCContainer.OXYGEN_TANK_2_SLOT);
            int storedOxy = invStack.isEmpty() ? 0 : OxygenTankItem.getOxygenCount(invStack);
            int maxOxy = invStack.isEmpty() ? 0 : OxygenTankItem.getMaxOxygen(invStack);
            this.renderTooltip("Tank 2 Oxygen: " + storedOxy + "/" + maxOxy, mouseX, mouseY);
        }
        super.drawMouseoverTooltip(mouseX, mouseY);
    }

    @Override
    public void render(int mouseX, int mouseY, float lastFrameDuration) {
        this.renderBackground();
        super.render(mouseX, mouseY, lastFrameDuration);
        this.drawMouseoverTooltip(mouseX, mouseY);

//        this.mouseX = (float) x;
//        this.mouseY = (float) minecraft.getWindow().getScaledHeight() / 2;

        DiffuseLighting.enable();
        this.itemRenderer.renderGuiItem(Items.CRAFTING_TABLE.getStackForRender(), this.x + 6, this.y - 20);
        this.itemRenderer.renderGuiItem(GalacticraftItems.OXYGEN_MASK.getStackForRender(), this.x + 35, this.y - 20);
        DiffuseLighting.disable();

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean b = super.mouseClicked(mouseX, mouseY, button);

        if (PlayerInventoryGCScreen.isCoordinateBetween((int) Math.floor(mouseX), x, x + 29)
                && PlayerInventoryGCScreen.isCoordinateBetween((int) Math.floor(mouseY), y - 26, y)) {

            minecraft.openScreen(new InventoryScreen(playerInventory.player));
        }

        return b;
    }

    @Override
    public void drawBackground(float v, int mouseX, int mouseY) {
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);
        this.blit(this.x, this.y, 0, 0, this.containerWidth, this.containerHeight);

        InventoryScreen.drawEntity(this.x + 51, this.y + 75, 30, (float) (this.x + 51) - mouseX, (float) (this.y + 75 - 50) - mouseY, this.minecraft.player);

        this.minecraft.getTextureManager().bindTexture(BACKGROUND);

        blit(this.x + 138, this.y + 8, 244, 0, 12, 40);
        blit(this.x + 156, this.y + 8, 244, 0, 12, 40);

        this.minecraft.getTextureManager().bindTexture(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.PLAYER_INVENTORY_TABS)));
        this.blit(this.x, this.y - 28, 0, 32, 57, 62);
    }
}