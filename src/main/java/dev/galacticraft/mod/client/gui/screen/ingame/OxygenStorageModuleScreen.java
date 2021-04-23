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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.screen.MachineHandledScreen;
import dev.galacticraft.mod.screen.OxygenStorageModuleScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.math.RoundingMode;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class OxygenStorageModuleScreen extends MachineHandledScreen<OxygenStorageModuleScreenHandler> {
    public OxygenStorageModuleScreen(OxygenStorageModuleScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, inv.player.world, handler.machine.getPos(), title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        this.client.getTextureManager().bindTexture(Constant.ScreenTexture.OXYGEN_STORAGE_MODULE_SCREEN);
        this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        this.drawOxygenBufferBar(matrices);

        drawCenteredString(matrices, textRenderer, I18n.translate("ui.galacticraft.machine.current_oxygen", this.handler.machine.getFluidInv().getInvFluid(0).amount().asInt(1000, RoundingMode.HALF_DOWN)), width / 2, y + 33, Formatting.DARK_GRAY.getColorValue());
        drawCenteredString(matrices, textRenderer, I18n.translate("ui.galacticraft.machine.max_oxygen", this.handler.machine.getFluidInv().getMaxAmount_F(0).asInt(1000, RoundingMode.HALF_DOWN)), width / 2, y + 45, Formatting.DARK_GRAY.getColorValue());
        super.drawBackground(matrices, delta, mouseX, mouseY);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        drawCenteredString(matrices, textRenderer, I18n.translate("block.galacticraft.oxygen_storage_module"), (this.width / 2), this.y + 5, Formatting.DARK_GRAY.getColorValue());
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    private void drawOxygenBufferBar(MatrixStack matrices) {
        double oxygenScale = this.handler.machine.getFluidInv().getInvFluid(0).amount().div(this.handler.machine.getFluidInv().getMaxAmount_F(0)).asInexactDouble();

        this.client.getTextureManager().bindTexture(Constant.ScreenTexture.OXYGEN_STORAGE_MODULE_SCREEN);
        this.drawTexture(matrices, this.x + 52, this.y + 57, 176, 0, (int) (72.0D * oxygenScale), 3);
    }
}
