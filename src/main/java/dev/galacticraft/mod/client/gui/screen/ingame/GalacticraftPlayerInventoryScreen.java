/*
 * Copyright (c) 2019-2021 Team Galacticraft
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
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.attribute.oxygen.OxygenTank;
import dev.galacticraft.mod.item.GalacticraftItem;
import dev.galacticraft.mod.screen.GalacticraftPlayerInventoryScreenHandler;
import dev.galacticraft.mod.util.DrawableUtil;
import dev.galacticraft.mod.util.OxygenTankUtil;
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
            OxygenTank tank = OxygenTankUtil.getOxygenTank(this.handler.inventory.getSlot(GalacticraftPlayerInventoryScreenHandler.OXYGEN_TANK_1_SLOT));
            this.renderTooltip(matrices, new TranslatableText("ui.galacticraft.player_inv_screen.oxygen_tank_level", 1, tank.getAmount(), tank.getCapacity()), mouseX, mouseY);
        } else if (DrawableUtil.isWithin(mouseX, mouseY, this.x + 152, this.y + 8, Constant.TextureCoordinate.OVERLAY_WIDTH, Constant.TextureCoordinate.OVERLAY_HEIGHT)) {
            OxygenTank tank = OxygenTankUtil.getOxygenTank(this.handler.inventory.getSlot(GalacticraftPlayerInventoryScreenHandler.OXYGEN_TANK_2_SLOT));
            this.renderTooltip(matrices, new TranslatableText("ui.galacticraft.player_inv_screen.oxygen_tank_level", 2, tank.getAmount(), tank.getCapacity()), mouseX, mouseY);
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
            this.client.openScreen(new InventoryScreen(this.handler.player));
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
        OxygenTank tank1 = OxygenTankUtil.getOxygenTank(this.handler.inventory.getSlot(GalacticraftPlayerInventoryScreenHandler.OXYGEN_TANK_1_SLOT));
        OxygenTank tank2 = OxygenTankUtil.getOxygenTank(this.handler.inventory.getSlot(GalacticraftPlayerInventoryScreenHandler.OXYGEN_TANK_2_SLOT));

        DrawableUtil.drawOxygenBuffer(matrices, this.x + 129, this.y + 8, tank1.getAmount(), tank1.getCapacity());
        DrawableUtil.drawOxygenBuffer(matrices, this.x + 152, this.y + 8, tank2.getAmount(), tank2.getCapacity());

        InventoryScreen.drawEntity(this.x + 51, this.y + 75, 30, (float) (this.x + 51) - mouseX, (float) (this.y + 75 - 50) - mouseY, this.client.player);
        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.PLAYER_INVENTORY_TABS);
        this.drawTexture(matrices, this.x, this.y - 28, 0, 32, 57, 62);
    }
}