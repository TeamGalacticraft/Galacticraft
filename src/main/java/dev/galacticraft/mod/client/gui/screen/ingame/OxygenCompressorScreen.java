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
import dev.galacticraft.mod.block.entity.OxygenCompressorBlockEntity;
import dev.galacticraft.mod.screen.SimpleMachineScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class OxygenCompressorScreen extends MachineHandledScreen<OxygenCompressorBlockEntity, SimpleMachineScreenHandler<OxygenCompressorBlockEntity>> {
    public OxygenCompressorScreen(SimpleMachineScreenHandler<OxygenCompressorBlockEntity> handler, PlayerInventory inv, Text title) {
        super(handler, inv, title, Constant.ScreenTexture.OXYGEN_COMPRESSOR_SCREEN);
        this.addWidget(this.createCapacitorWidget(8, 8, 48));
    }

    @Override
    protected void init() {
        super.init();
        this.titleX += 18;
    }

    @Override
    protected void renderBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        super.renderBackground(matrices, delta, mouseX, mouseY);
        if (this.machine.getStatus().getType().isActive()) {
            double height = (System.currentTimeMillis() % 2250);
            if (height == 0) height = 1; //prevent dividing by zero
            height /= -125D;
            this.drawTexture(matrices, this.x + 93, this.y + 64, 187, 18, -11, (int) height);
        }
    }
}
