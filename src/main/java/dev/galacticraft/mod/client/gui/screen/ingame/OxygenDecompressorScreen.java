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
import dev.galacticraft.machinelib.api.screen.SimpleMachineScreenHandler;
import dev.galacticraft.machinelib.client.api.screen.MachineHandledScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.block.entity.OxygenDecompressorBlockEntity;
import dev.galacticraft.mod.util.DrawableUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class OxygenDecompressorScreen extends MachineHandledScreen<OxygenDecompressorBlockEntity, SimpleMachineScreenHandler<OxygenDecompressorBlockEntity>> {
    public OxygenDecompressorScreen(SimpleMachineScreenHandler<OxygenDecompressorBlockEntity> handler, Inventory inv, Component title) {
        super(handler, inv, title, Constant.ScreenTexture.OXYGEN_COMPRESSOR_SCREEN);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX += 20;
    }

    @Override
    protected void renderBackground(PoseStack matrices, int mouseX, int mouseY, float delta) {
        super.renderBackground(matrices, mouseX, mouseY, delta);
        if (this.machine.getStatus().type().isActive()) {
            double height = (int) (System.currentTimeMillis() % 2250);
            if (height == 0) return; //prevent dividing by zero
            height /= 125.0;
            DrawableUtil.drawProgressTexture(matrices, this.leftPos + 82, this.topPos + 46, 176, 0, 11, (float)height);
        }
    }
}
