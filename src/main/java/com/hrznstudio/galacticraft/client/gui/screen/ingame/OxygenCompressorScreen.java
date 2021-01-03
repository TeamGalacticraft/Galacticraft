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
import com.hrznstudio.galacticraft.api.screen.MachineHandledScreen;
import com.hrznstudio.galacticraft.client.gui.widget.machine.CapacitorWidget;
import com.hrznstudio.galacticraft.client.gui.widget.machine.OxygenTankWidget;
import com.hrznstudio.galacticraft.screen.OxygenCompressorScreenHandler;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class OxygenCompressorScreen extends MachineHandledScreen<OxygenCompressorScreenHandler> {
    private static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OXYGEN_COMPRESSOR_SCREEN));

    public OxygenCompressorScreen(OxygenCompressorScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, inv.player.world, handler.blockEntity.getPos(), title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
        this.addWidget(new CapacitorWidget(handler.blockEntity.getCapacitor(), 8, 8, 48, this::getEnergyTooltipLines, handler.blockEntity::getStatus));
        this.addWidget(new OxygenTankWidget(handler.blockEntity.getFluidTank(), 0, 30, 8, 48));
    }

    @Override
    protected void drawBackground(MatrixStack stack, float v, int mouseX, int mouseY) {
        this.renderBackground(stack);
        this.client.getTextureManager().bindTexture(BACKGROUND);
        this.drawTexture(stack, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        if (handler.blockEntity.getStatus().getType().isActive()) {
            double height = (System.currentTimeMillis() % 2250);
            if (height == 0) height = 1; //prevent dividing by zero
            height /= -125D;
            this.drawTexture(stack, this.x + 93, this.y + 64, 187, 18, -11, (int) height);
        }

    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float v) {
        super.render(stack, mouseX, mouseY, v);
        DrawableUtils.drawCenteredString(stack, this.client.textRenderer, new TranslatableText("block.galacticraft-rewoven.oxygen_compressor").getString(), (this.width / 2) + 20, this.y + 5, Formatting.DARK_GRAY.getColorValue());
        this.drawMouseoverTooltip(stack, mouseX, mouseY);
    }
}
