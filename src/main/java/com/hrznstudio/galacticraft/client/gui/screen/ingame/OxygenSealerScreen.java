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
import com.hrznstudio.galacticraft.api.screen.MachineHandledScreen;
import com.hrznstudio.galacticraft.client.gui.widget.machine.CapacitorWidget;
import com.hrznstudio.galacticraft.client.gui.widget.machine.OxygenTankWidget;
import com.hrznstudio.galacticraft.screen.OxygenSealerScreenHandler;
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
public class OxygenSealerScreen extends MachineHandledScreen<OxygenSealerScreenHandler> {
    private static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OXYGEN_SEALER_SCREEN));

    public OxygenSealerScreen(OxygenSealerScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, inv.player.world, handler.machine.getPos(), title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
        this.addWidget(new CapacitorWidget(handler.machine.getCapacitor(), 8, 8, 48, this::getEnergyTooltipLines, handler.machine::getStatus));
        this.addWidget(new OxygenTankWidget(handler.machine.getFluidTank().getTank(0), 30, 8, 48));
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        this.client.getTextureManager().bindTexture(BACKGROUND);
        this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float v) {
        super.render(matrices, mouseX, mouseY, v);
        DrawableUtils.drawCenteredString(matrices, this.client.textRenderer, new TranslatableText("block.galacticraft-rewoven.oxygen_sealer").getString(), (this.width / 2) + 20, this.y + 5, Formatting.DARK_GRAY.getColorValue());
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}
