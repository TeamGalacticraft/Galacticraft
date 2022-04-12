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

import com.mojang.blaze3d.systems.RenderSystem;
import dev.galacticraft.api.screen.RecipeMachineScreenHandler;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.api.client.screen.MachineHandledScreen;
import dev.galacticraft.mod.block.entity.ElectricCompressorBlockEntity;
import dev.galacticraft.mod.recipe.CompressingRecipe;
import dev.galacticraft.mod.util.DrawableUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.text.Text;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class ElectricCompressorScreen extends MachineHandledScreen<ElectricCompressorBlockEntity, RecipeMachineScreenHandler<Inventory, CompressingRecipe, ElectricCompressorBlockEntity>> {
    private static final int PROGRESS_U = 177;
    private static final int PROGRESS_V = 0;
    private static final int PROGRESS_X = 87;
    private static final int PROGRESS_Y = 28;
    private static final int PROGRESS_WIDTH = 52;
    private static final int PROGRESS_HEIGHT = 25;

    public ElectricCompressorScreen(RecipeMachineScreenHandler<Inventory, CompressingRecipe, ElectricCompressorBlockEntity> handler, PlayerInventory inv, Text title) {
        super(handler, inv, title, Constant.ScreenTexture.ELECTRIC_COMPRESSOR_SCREEN);
    }

    @Override
    protected void renderBackground(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderBackground(matrices, mouseX, mouseY, delta);
        this.drawCraftProgressBar(matrices);
    }

    protected void drawCraftProgressBar(MatrixStack matrices) {
        float progressScale = (((float)this.machine.getProgress()) / ((float)this.machine.getMaxProgress()));

        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.ELECTRIC_COMPRESSOR_SCREEN);
        DrawableUtil.drawProgressTexture(matrices, this.x + PROGRESS_X, this.y + PROGRESS_Y, PROGRESS_U, PROGRESS_V, (PROGRESS_WIDTH * progressScale), PROGRESS_HEIGHT);
    }
}