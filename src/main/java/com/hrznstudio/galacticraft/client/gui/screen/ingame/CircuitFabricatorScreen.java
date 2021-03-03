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
import com.hrznstudio.galacticraft.screen.CircuitFabricatorScreenHandler;
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
public class CircuitFabricatorScreen extends MachineHandledScreen<CircuitFabricatorScreenHandler> {

    private static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.CIRCUIT_FABRICATOR_SCREEN));

    private static final int PROGRESS_SIZE = 4;
    private static final int INITIAL_PROGRESS_X = 0;
    private static final int INITIAL_PROGRESS_Y = 186;
    private static final int INITIAL_PROGRESS_U = 48;
    private static final int INITIAL_PROGRESS_V = 21;
    private static final int INITIAL_PROGRESS_WIDTH = 24;
    private static final int INITIAL_PROGRESS_HEIGHT = 24;
    private static final int SECONDARY_PROGRESS_X = 31;
    private static final int SECONDARY_PROGRESS_Y = 216;
    private static final int SECONDARY_PROGRESS_U = 79;
    private static final int SECONDARY_PROGRESS_V = 51;
    private static final int SECONDARY_PROGRESS_WIDTH = 18;
    private static final int SECONDARY_CONCURRENT_PROGRESS_X = 31;
    private static final int SECONDARY_CONCURRENT_PROGRESS_Y = 237;
    private static final int SECONDARY_CONCURRENT_PROGRESS_U = 79;
    private static final int SECONDARY_CONCURRENT_PROGRESS_V = 72;
    private static final int SECONDARY_CONCURRENT_PROGRESS_WIDTH = 24;
    private static final int SECONDARY_CONCURRENT_PROGRESS_INITIAL_HEIGHT = -4;
    private static final int SECONDARY_CONCURRENT_PROGRESS_HEIGHT = -22;
    private static final int TERTIARY_PROGRESS_X = 48;
    private static final int TERTIARY_PROGRESS_Y = 216;
    private static final int TERTIARY_PROGRESS_U = 97;
    private static final int TERTIARY_PROGRESS_V = 72;
    private static final int TERTIARY_PROGRESS_WIDTH = 65;
    private static final int QUATERNARY_PROGRESS_X = 65;
    private static final int QUATERNARY_PROGRESS_Y = 220;
    private static final int QUATERNARY_PROGRESS_U = 113;
    private static final int QUATERNARY_PROGRESS_V = 56;
    private static final int QUATERNARY_PROGRESS_HEIGHT = 65;
    private static final int QUINARY_PROGRESS_X = 92;
    private static final int QUINARY_PROGRESS_Y = 215;
    private static final int QUINARY_PROGRESS_U = 140;
    private static final int QUINARY_PROGRESS_V = 50;
    private static final int QUINARY_PROGRESS_HEIGHT = -19;
    private static final int SENARY_PROGRESS_X = 110;
    private static final int SENARY_PROGRESS_Y = 220;
    private static final int SENARY_PROGRESS_U = 158;
    private static final int SENARY_PROGRESS_V = 55;
    private static final int SENARY_PROGRESS_HEIGHT = 14;

    public CircuitFabricatorScreen(CircuitFabricatorScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, inv.player.world, handler.machine.getPos(), title);
        this.backgroundHeight = 176;
        this.addWidget(new CapacitorWidget(handler.machine.getCapacitor(), 8, 15, 48, this::getEnergyTooltipLines, handler.machine::getStatus));
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        this.client.getTextureManager().bindTexture(BACKGROUND);

        this.drawTexture(matrices, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.drawProgressBar(matrices, delta);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float v) {
        super.render(matrices, mouseX, mouseY, v);
        DrawableUtils.drawCenteredString(matrices, this.client.textRenderer, new TranslatableText("block.galacticraft-rewoven.circuit_fabricator").getString(), (this.width / 2), this.y + 5, Formatting.DARK_GRAY.getColorValue());
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    //24+20+18+65+14
    private void drawProgressBar(MatrixStack stack, float delta) {
        double progressScale = (((double)this.handler.machine.getProgress()) / ((double)this.handler.machine.getMaxProgress()));

    }
}
