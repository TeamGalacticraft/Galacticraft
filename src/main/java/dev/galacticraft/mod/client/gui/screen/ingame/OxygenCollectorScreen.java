/*
 * Copyright (c) 2019-2022 Team Galacticraft
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
import dev.galacticraft.api.client.screen.MachineHandledScreen;
import dev.galacticraft.mod.block.entity.OxygenCollectorBlockEntity;
import dev.galacticraft.mod.screen.OxygenCollectorScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class OxygenCollectorScreen extends MachineHandledScreen<OxygenCollectorBlockEntity, OxygenCollectorScreenHandler> {
    public OxygenCollectorScreen(OxygenCollectorScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, title, Constant.ScreenTexture.OXYGEN_COLLECTOR_SCREEN);
        this.backgroundHeight = 181;
    }

    @Override
    protected void init() {
        super.init();
        this.titleX += 15;
    }

    @Override
    protected void renderBackground(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderBackground(matrices, mouseX, mouseY, delta);
        this.textRenderer.draw(matrices, Text.translatable("ui.galacticraft.machine.collecting", this.machine.collectionAmount).getString(), this.x + 55, this.y + 56, Formatting.DARK_GRAY.getColorValue());
        this.textRenderer.draw(matrices, Text.translatable("ui.galacticraft.machine.status").append(this.machine.getStatus().name()), this.x + 32, this.y + 66, Formatting.DARK_GRAY.getColorValue());
    }
}
