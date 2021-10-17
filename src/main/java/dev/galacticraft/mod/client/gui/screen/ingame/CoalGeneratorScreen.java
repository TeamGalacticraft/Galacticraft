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
import dev.galacticraft.mod.block.entity.CoalGeneratorBlockEntity;
import dev.galacticraft.mod.client.gui.widget.machine.CapacitorWidget;
import dev.galacticraft.mod.screen.CoalGeneratorScreenHandler;
import dev.galacticraft.mod.util.DrawableUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class CoalGeneratorScreen extends MachineHandledScreen<CoalGeneratorBlockEntity, CoalGeneratorScreenHandler> {
    private static final int FIRE_X = 72;
    private static final int FIRE_Y = 37;
    private static final int FIRE_U = 176;
    private static final int FIRE_V = 0;
    private static final int FIRE_WIDTH = 14;
    private static final int FIRE_HEIGHT = 14;

    public CoalGeneratorScreen(CoalGeneratorScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, title, Constant.ScreenTexture.COAL_GENERATOR_SCREEN);
        this.backgroundHeight = 176;
    }

    @Override
    protected void init() {
        super.init();
        this.addDrawableChild(new CapacitorWidget(this, this.x + 8, this.y + 8, 48));
    }

    @Override
    protected void renderBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        super.renderBackground(matrices, delta, mouseX, mouseY);
        if (this.machine.fuelLength > 0) {
            double scale = ((double)(this.machine.fuelLength - this.machine.fuelTime)) / (double)this.machine.fuelLength;
            DrawableUtil.drawProgressTexture(matrices, this.x + FIRE_X, this.y + FIRE_Y, this.getZOffset(), FIRE_U, FIRE_V, FIRE_WIDTH, (float) (FIRE_HEIGHT * scale));
        }
    }
}
