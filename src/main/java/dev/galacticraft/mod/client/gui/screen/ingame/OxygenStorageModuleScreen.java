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
import dev.galacticraft.mod.api.client.screen.MachineHandledScreen;
import dev.galacticraft.mod.block.entity.OxygenStorageModuleBlockEntity;
import dev.galacticraft.mod.screen.SimpleMachineScreenHandler;
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
public class OxygenStorageModuleScreen extends MachineHandledScreen<OxygenStorageModuleBlockEntity, SimpleMachineScreenHandler<OxygenStorageModuleBlockEntity>> {
    public OxygenStorageModuleScreen(SimpleMachineScreenHandler<OxygenStorageModuleBlockEntity> handler, PlayerInventory inv, Text title) {
        super(handler, inv, title, Constant.ScreenTexture.OXYGEN_STORAGE_MODULE_SCREEN);
    }

    @Override
    protected void renderBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        super.renderBackground(matrices, delta, mouseX, mouseY);
        this.drawOxygenBufferBar(matrices);

        drawCenteredText(matrices, textRenderer, I18n.translate("ui.galacticraft.machine.current_oxygen", this.machine.fluidInv().getInvFluid(0).amount().asInt(1000, RoundingMode.HALF_DOWN)), width / 2, y + 33, Formatting.DARK_GRAY.getColorValue());
        drawCenteredText(matrices, textRenderer, I18n.translate("ui.galacticraft.machine.max_oxygen", this.machine.fluidInv().getMaxAmount_F(0).asInt(1000, RoundingMode.HALF_DOWN)), width / 2, y + 45, Formatting.DARK_GRAY.getColorValue());
    }

    private void drawOxygenBufferBar(MatrixStack matrices) {
        double oxygenScale = this.machine.fluidInv().getInvFluid(0).amount().div(this.machine.fluidInv().getMaxAmount_F(0)).asInexactDouble();

        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.OXYGEN_STORAGE_MODULE_SCREEN);
        this.drawTexture(matrices, this.x + 52, this.y + 57, 176, 0, (int) (72.0D * oxygenScale), 3);
    }
}
