/*
 * Copyright (c) 2020 HRZN LTD
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
 *
 */

package com.hrznstudio.galacticraft.client.gui.screen.ingame;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.screen.MachineHandledScreen;
import com.hrznstudio.galacticraft.block.entity.OxygenCollectorBlockEntity;
import com.hrznstudio.galacticraft.screen.OxygenCollectorScreenHandler;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
@Environment(EnvType.CLIENT)
public class OxygenCollectorScreen extends MachineHandledScreen<OxygenCollectorScreenHandler> {

    private static final Identifier OVERLAY = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OVERLAY));
    private static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OXYGEN_COLLECTOR_SCREEN));
    private static final int OVERLAY_WIDTH = Constants.TextureCoordinates.OVERLAY_WIDTH;
    private static final int OVERLAY_HEIGHT = Constants.TextureCoordinates.OVERLAY_HEIGHT;
    private static final int ENERGY_X = Constants.TextureCoordinates.ENERGY_LIGHT_X;
    private static final int ENERGY_Y = Constants.TextureCoordinates.ENERGY_LIGHT_Y;
    private static final int ENERGY_DIMMED_X = Constants.TextureCoordinates.ENERGY_DARK_X;
    private static final int ENERGY_DIMMED_Y = Constants.TextureCoordinates.ENERGY_DARK_Y;
    private static final int OXYGEN_X = Constants.TextureCoordinates.OXYGEN_LIGHT_X;
    private static final int OXYGEN_Y = Constants.TextureCoordinates.OXYGEN_LIGHT_Y;
    private static final int OXYGEN_DIMMED_X = Constants.TextureCoordinates.OXYGEN_DARK_X;
    private static final int OXYGEN_DIMMED_Y = Constants.TextureCoordinates.OXYGEN_DARK_Y;

    private int oxygenDisplayX = 0;
    private int oxygenDisplayY = 0;

    public OxygenCollectorScreen(OxygenCollectorScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, inv.player.world, handler.blockEntity.getPos(), title);
        this.backgroundHeight = 181;
    }

    @Override
    protected void drawBackground(MatrixStack stack, float v, int mouseX, int mouseY) {
        this.renderBackground(stack);
        this.client.getTextureManager().bindTexture(BACKGROUND);

        int leftPos = this.x;
        int topPos = this.y;

        oxygenDisplayX = leftPos + 33;
        oxygenDisplayY = topPos + 18;

        this.drawTexture(stack, leftPos, topPos, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.drawEnergyBufferBar(stack, this.x + 11, this.y + 18);
        this.drawOxygenBufferBar(stack);
        this.drawConfigTabs(stack);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float v) {
        super.render(stack, mouseX, mouseY, v);
        DrawableUtils.drawCenteredString(stack, this.client.textRenderer, new TranslatableText("block.galacticraft-rewoven.oxygen_collector").getString(), (this.width / 2), this.y + 5, Formatting.DARK_GRAY.getColorValue());
        String statusText = new TranslatableText("ui.galacticraft-rewoven.machine.status").getString();


        int statusX = this.x + 38;
        int statusY = this.y + 64;

        this.client.textRenderer.draw(stack, statusText, statusX, statusY, Formatting.DARK_GRAY.getColorValue());

        this.client.textRenderer.draw(stack, OxygenCollectorBlockEntity.OxygenCollectorStatus.get(handler.status.get()).getText(), statusX + this.client.textRenderer.getWidth(statusText), statusY, OxygenCollectorBlockEntity.OxygenCollectorStatus.get(handler.status.get()).getText().getStyle().getColor().getRgb());

        DrawableUtils.drawCenteredString(stack, this.client.textRenderer, new TranslatableText("ui.galacticraft-rewoven.machine.collecting", this.handler.lastCollectAmount.get()).getString(), (this.width / 2) + 10, statusY + 12, Formatting.DARK_GRAY.getColorValue());
        this.drawMouseoverTooltip(stack, mouseX, mouseY);
    }

    private void drawOxygenBufferBar(MatrixStack stack) {
        float currentOxygen = ((float) this.handler.oxygen.get());
        float maxOxygen = OxygenCollectorBlockEntity.MAX_OXYGEN.floatValue() * 100.0F;
        float oxygenScale = (currentOxygen / maxOxygen);

        this.client.getTextureManager().bindTexture(OVERLAY);
        this.drawTexture(stack, oxygenDisplayX, oxygenDisplayY, OXYGEN_DIMMED_X, OXYGEN_DIMMED_Y, OVERLAY_WIDTH, OVERLAY_HEIGHT);
        this.drawTexture(stack, oxygenDisplayX, (oxygenDisplayY - (int) (OVERLAY_HEIGHT * oxygenScale)) + OVERLAY_HEIGHT, OXYGEN_X, OXYGEN_Y, OVERLAY_WIDTH, (int) (OVERLAY_HEIGHT * oxygenScale));
    }

    @Override
    public void drawMouseoverTooltip(MatrixStack stack, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(stack, mouseX, mouseY);
        this.drawEnergyTooltip(stack, mouseX, mouseY, this.x + 11, this.y + 18);
        if (mouseX >= oxygenDisplayX && mouseX <= oxygenDisplayX + OVERLAY_WIDTH && mouseY >= oxygenDisplayY && mouseY <= oxygenDisplayY + OVERLAY_HEIGHT) {
            List<Text> toolTipLines = new ArrayList<>();
            toolTipLines.add(new TranslatableText("ui.galacticraft-rewoven.machine.current_oxygen", new LiteralText(String.valueOf((float) this.handler.oxygen.get())).setStyle(Style.EMPTY.withColor(Formatting.BLUE))).setStyle(Style.EMPTY.withColor(Formatting.GOLD)));
            toolTipLines.add(new TranslatableText("ui.galacticraft-rewoven.machine.max_oxygen", String.valueOf(OxygenCollectorBlockEntity.MAX_OXYGEN.floatValue() * 100.0F)).setStyle(Style.EMPTY.withColor(Formatting.RED)));
            this.renderTooltip(stack, toolTipLines, mouseX, mouseY);
        }
    }
}
