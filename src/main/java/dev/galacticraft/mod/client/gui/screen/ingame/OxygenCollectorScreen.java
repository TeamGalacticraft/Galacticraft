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
import dev.galacticraft.mod.api.client.screen.MachineHandledScreen;
import dev.galacticraft.mod.block.entity.OxygenCollectorBlockEntity;
import dev.galacticraft.mod.client.gui.widget.machine.CapacitorWidget;
import dev.galacticraft.mod.screen.OxygenCollectorScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class OxygenCollectorScreen extends MachineHandledScreen<OxygenCollectorBlockEntity, OxygenCollectorScreenHandler> {
    public OxygenCollectorScreen(OxygenCollectorScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, title, Constant.ScreenTexture.OXYGEN_COLLECTOR_SCREEN);
        this.backgroundHeight = 181;

        this.addWidget(new CapacitorWidget(this.machine.capacitor(), 13, 13, 48, this::getEnergyTooltipLines, this.machine::getStatus));
    }

    @Override
    protected void renderBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        super.renderBackground(matrices, delta, mouseX, mouseY);
        drawCenteredText(matrices, textRenderer, I18n.translate("block.galacticraft.oxygen_collector"), (this.width / 2), this.y + 5, Formatting.DARK_GRAY.getColorValue());
        drawCenteredText(matrices, this.textRenderer, new TranslatableText("ui.galacticraft.machine.collecting", this.machine.collectionAmount).getString(), (this.width / 2) + 10, this.y + 64 + 12, Formatting.DARK_GRAY.getColorValue());
        this.textRenderer.draw(matrices, new TranslatableText("ui.galacticraft.machine.status").append(this.machine.getStatus().getName()), this.x + 38, this.y + 64, Formatting.DARK_GRAY.getColorValue());
    }
}
