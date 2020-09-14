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
 *
 */

package com.hrznstudio.galacticraft.client.gui.screen.ingame;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.screen.MachineHandledScreen;
import com.hrznstudio.galacticraft.block.entity.OxygenStorageModuleBlockEntity;
import com.hrznstudio.galacticraft.screen.OxygenStorageModuleScreenHandler;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class OxygenStorageModuleScreen extends MachineHandledScreen<OxygenStorageModuleScreenHandler> {
    private static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OXYGEN_STORAGE_MODULE_SCREEN));

    public OxygenStorageModuleScreen(OxygenStorageModuleScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, inv.player.world, handler.blockEntity.getPos(), title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    @Override
    protected void drawBackground(MatrixStack stack, float v, int mouseX, int mouseY) {
        this.renderBackground(stack);
        this.client.getTextureManager().bindTexture(BACKGROUND);
        this.drawTexture(stack, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        this.drawOxygenBufferBar(stack);

        DrawableUtils.drawCenteredString(stack, textRenderer, I18n.translate("ui.galacticraft-rewoven.machine.current_oxygen", this.handler.oxygen.get()), width / 2, y + 33, Formatting.DARK_GRAY.getColorValue());
        DrawableUtils.drawCenteredString(stack, textRenderer, I18n.translate("ui.galacticraft-rewoven.machine.max_oxygen", (int)(this.handler.blockEntity.getOxygenTank().getMaxCapacity(0).doubleValue() * 100.0D)), width / 2, y + 45, Formatting.DARK_GRAY.getColorValue());
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float v) {
        super.render(stack, mouseX, mouseY, v);
        DrawableUtils.drawCenteredString(stack, this.client.textRenderer, new TranslatableText("block.galacticraft-rewoven.oxygen_storage_module").getString(), (this.width / 2), this.y + 5, Formatting.DARK_GRAY.getColorValue());
        this.drawMouseoverTooltip(stack, mouseX, mouseY);
    }

    private void drawOxygenBufferBar(MatrixStack stack) {
        double currentOxygen = this.handler.oxygen.get();
        double maxOxygen = handler.blockEntity.getOxygenTank().getMaxCapacity(0).doubleValue() * 100.D;
        double oxygenScale = (currentOxygen / maxOxygen);

        this.client.getTextureManager().bindTexture(BACKGROUND);
        this.drawTexture(stack, this.x + 52, this.y + 57, 176, 0, (int) (72.0D * oxygenScale), 3);
    }

    @Override
    public void drawMouseoverTooltip(MatrixStack stack, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(stack, mouseX, mouseY);
        this.drawEnergyTooltip(stack, mouseX, mouseY, this.x + 11, this.y + 18);
    }
}
