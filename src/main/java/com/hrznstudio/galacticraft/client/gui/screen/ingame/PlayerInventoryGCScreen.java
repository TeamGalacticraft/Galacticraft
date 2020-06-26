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

package com.hrznstudio.galacticraft.client.gui.screen.ingame;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.items.OxygenTankItem;
import com.hrznstudio.galacticraft.screen.PlayerInventoryGCScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class PlayerInventoryGCScreen extends HandledScreen<PlayerInventoryGCScreenHandler> {
    public static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.PLAYER_INVENTORY_SCREEN));

    private float mouseX;
    private float mouseY;

    public PlayerInventoryGCScreen(PlayerEntity player) {
//        super(((GCPlayerAccessor) player).getGCContainer(), player.inventory, new TranslatableText(Constants.MOD_ID + ".player_inv_screen"));
        super(new PlayerInventoryGCScreenHandler(player.inventory, player), player.inventory, new TranslatableText("ui." + Constants.MOD_ID + ".player_inv_screen"));
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
            int storedOxy = invStack.isEmpty() ? 0 : OxygenTankItem.getOxygenCount(invStack);
            int maxOxy = invStack.isEmpty() ? 0 : OxygenTankItem.getMaxOxygen(invStack);
            this.renderTooltip(stack, new LiteralText("Tank 1 Oxygen: " + storedOxy + "/" + maxOxy), x, y);
        } else if (PlayerInventoryGCScreen.isCoordinateBetween(x, this.x + 156, this.x + 156 + 12)
                && PlayerInventoryGCScreen.isCoordinateBetween(y, this.y + 8, this.y + 8 + 40)) {
            ItemStack invStack = this.handler.inventory.getStack(PlayerInventoryGCScreenHandler.OXYGEN_TANK_2_SLOT);
            int storedOxy = invStack.isEmpty() ? 0 : OxygenTankItem.getOxygenCount(invStack);
            int maxOxy = invStack.isEmpty() ? 0 : OxygenTankItem.getMaxOxygen(invStack);
            this.renderTooltip(stack, new LiteralText("Tank 2 Oxygen: " + storedOxy + "/" + maxOxy), x, y);
        }
        super.drawMouseoverTooltip(stack, x, y);
    }

    @Override
    public void render(MatrixStack stack, int x, int y, float lastFrameDuration) {
        this.renderBackground(stack);
        super.render(stack, x, y, lastFrameDuration);
        this.drawMouseoverTooltip(stack, x, y);

        this.mouseX = (float) x;
        this.mouseY = (float)/*y*/ this.client.getWindow().getScaledHeight() / 2;

        DiffuseLighting.enableGuiDepthLighting();
        this.itemRenderer.renderInGuiWithOverrides(Items.CRAFTING_TABLE.getStackForRender(), this.x + 6, this.y - 20);
        this.itemRenderer.renderInGuiWithOverrides(GalacticraftItems.OXYGEN_MASK.getStackForRender(), this.x + 35, this.y - 20);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
//        System.out.println("X: " + mouseX);
//        System.out.println("Y: " + mouseY);
//        System.out.println("b: " + button);
        boolean b = super.mouseClicked(mouseX, mouseY, button);

        if (PlayerInventoryGCScreen.isCoordinateBetween((int) Math.floor(mouseX), this.x, this.x + 29)
                && PlayerInventoryGCScreen.isCoordinateBetween((int) Math.floor(mouseY), this.y - 26, this.y)) {
            System.out.println("Clicked on vanilla tab!");
            this.client.openScreen(new InventoryScreen(playerInventory.player));
        }

        return b;
    }

    @Override
    public void drawBackground(MatrixStack stack, float v, int mouseX, int mouseY) {
//        this.drawTexturedReact(...)
        this.client.getTextureManager().bindTexture(BACKGROUND);
        this.drawTexture(stack, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        int int_3 = this.x;
        int int_4 = this.y;
        InventoryScreen.drawEntity(int_3 + 51, int_4 + 75, 30, (float) (int_3 + 51) - this.mouseX, (float) (int_4 + 75 - 50) - this.mouseY, this.client.player);

        this.client.getTextureManager().bindTexture(BACKGROUND);

        //X,Y,blitOffset,u,v,width,height
        this.drawTexture(stack, this.x + 138, this.y + 8, 244, 0, 12, 40);
        this.drawTexture(stack, this.x + 156, this.y + 8, 244, 0, 12, 40);

        this.client.getTextureManager().bindTexture(new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.PLAYER_INVENTORY_TABS)));
        this.drawTexture(stack, this.x, this.y - 28, 0, 32, 57, 62);
    }
}