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

import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.registries.block.entity.CoalGeneratorBlockEntity;
import dev.galacticraft.mod.screen.CoalGeneratorScreenHandler;
import dev.galacticraft.mod.util.DrawableUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class CoalGeneratorScreen extends MachineScreen<CoalGeneratorBlockEntity, CoalGeneratorScreenHandler> {
    private static final int FIRE_X = 72;
    private static final int FIRE_Y = 37;
    private static final int FIRE_U = 176;
    private static final int FIRE_V = 0;
    private static final int FIRE_WIDTH = 14;
    private static final int FIRE_HEIGHT = 14;

    public CoalGeneratorScreen(CoalGeneratorScreenHandler handler, Inventory inv, Component title) {
        super(handler, inv, title, Constant.ScreenTexture.COAL_GENERATOR_SCREEN);
        this.imageHeight = 176;
    }

    @Override
    protected void renderBackground(PoseStack matrices, int mouseX, int mouseY, float delta) {
        super.renderBackground(matrices, mouseX, mouseY, delta);
        if (this.machine.getFuelLength() > 0) {
            double scale = 1.0 - ((double)this.machine.getFuelTime()) / (double)this.machine.getFuelLength();
            DrawableUtil.drawProgressTexture(matrices, this.leftPos + FIRE_X, this.topPos + FIRE_Y + FIRE_HEIGHT - (float) (FIRE_HEIGHT * scale), this.getBlitOffset(), FIRE_U, FIRE_V + FIRE_HEIGHT - (float) (FIRE_HEIGHT * scale), FIRE_WIDTH, (float) (FIRE_HEIGHT * scale));
        }
    }
}
