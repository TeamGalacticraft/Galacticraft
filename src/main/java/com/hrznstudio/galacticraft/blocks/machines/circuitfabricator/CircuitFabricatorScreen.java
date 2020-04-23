/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.blocks.machines.circuitfabricator;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.api.screen.MachineContainerScreen;
import com.hrznstudio.galacticraft.energy.GalacticraftEnergyType;
import com.hrznstudio.galacticraft.util.DrawableUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
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
public class CircuitFabricatorScreen extends MachineContainerScreen<CircuitFabricatorContainer> {

    public static final ContainerFactory<HandledScreen> FACTORY = createFactory(CircuitFabricatorBlockEntity.class, CircuitFabricatorScreen::new);

    private static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.CIRCUIT_FABRICATOR_SCREEN));
    private static final Identifier OVERLAY = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OVERLAY));

    private static final int ENERGY_X = Constants.TextureCoordinates.ENERGY_LIGHT_X;
    private static final int ENERGY_Y = Constants.TextureCoordinates.ENERGY_LIGHT_Y;
    private static final int ENERGY_WIDTH = Constants.TextureCoordinates.OVERLAY_WIDTH;
    private static final int ENERGY_HEIGHT = Constants.TextureCoordinates.OVERLAY_HEIGHT;
    private static final int ENERGY_DIMMED_X = Constants.TextureCoordinates.ENERGY_DARK_X;
    private static final int ENERGY_DIMMED_Y = Constants.TextureCoordinates.ENERGY_DARK_Y;
    private static final int ENERGY_DIMMED_WIDTH = Constants.TextureCoordinates.OVERLAY_WIDTH;
    private static final int ENERGY_DIMMED_HEIGHT = Constants.TextureCoordinates.OVERLAY_HEIGHT;

    private static final int PROGRESS_X = 206;
    private static final int PROGRESS_Y = 0;
    private static final int PROGRESS_WIDTH = 50;
    private static final int PROGRESS_HEIGHT = 10;

    private int energyDisplayX;
    private int energyDisplayY;
    private int progressDisplayX;
    private int progressDisplayY;

    public CircuitFabricatorScreen(int syncId, PlayerEntity playerEntity, CircuitFabricatorBlockEntity blockEntity) {
        super(new CircuitFabricatorContainer(syncId, playerEntity, blockEntity), playerEntity.inventory, playerEntity.world, blockEntity.getPos(), new TranslatableText("ui.galacticraft-rewoven.circuit_fabricator.name"));
        this.backgroundHeight = 192;
    }

    @Override
    protected void drawBackground(MatrixStack stack, float v, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.renderBackground(stack);
        this.client.getTextureManager().bindTexture(BACKGROUND);

        int leftPos = this.x;
        int topPos = this.y;

        energyDisplayX = leftPos + 10;
        energyDisplayY = topPos + 35;
        progressDisplayX = leftPos + 90;
        progressDisplayY = topPos + 82;

        //this.drawTexturedRect(...)
        this.drawTexture(stack, leftPos, topPos, 0, 0, this.backgroundWidth, this.backgroundHeight + 26);
        this.drawEnergyBufferBar(stack);

        this.drawProgressBar(stack);
        this.drawConfigTabs(stack);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float v) {
        super.render(stack, mouseX, mouseY, v);
        DrawableUtils.drawCenteredString(stack, this.client.textRenderer, new TranslatableText("block.galacticraft-rewoven.circuit_fabricator").getString(), (this.width / 2), this.y + 5, Formatting.DARK_GRAY.getColorValue());
        this.drawMouseoverTooltip(stack, mouseX, mouseY);
    }

    private void drawEnergyBufferBar(MatrixStack stack) {
        float currentEnergy = this.handler.energy.get();
        float maxEnergy = this.handler.getMaxEnergy();
        float energyScale = (currentEnergy / maxEnergy);

        //this.drawTexturedReact(...)
        this.client.getTextureManager().bindTexture(OVERLAY);
        this.drawTexture(stack, energyDisplayX, energyDisplayY, ENERGY_DIMMED_X, ENERGY_DIMMED_Y, ENERGY_DIMMED_WIDTH, ENERGY_DIMMED_HEIGHT);
        this.drawTexture(stack, energyDisplayX, (energyDisplayY - (int) (ENERGY_HEIGHT * energyScale)) + ENERGY_HEIGHT, ENERGY_X, ENERGY_Y, ENERGY_WIDTH, (int) (ENERGY_HEIGHT * energyScale));
    }

    private void drawProgressBar(MatrixStack stack) {
        float progress = this.handler.progress.get();
        float maxProgress = this.handler.blockEntity.getMaxProgress();
        float progressScale = (progress / maxProgress);

        this.client.getTextureManager().bindTexture(BACKGROUND);
        this.drawTexture(stack, progressDisplayX, progressDisplayY, PROGRESS_X, PROGRESS_Y, (int) (PROGRESS_WIDTH * progressScale), PROGRESS_HEIGHT);
    }

    @Override
    public void drawMouseoverTooltip(MatrixStack stack, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(stack, mouseX, mouseY);
        if (mouseX >= energyDisplayX && mouseX <= energyDisplayX + ENERGY_WIDTH && mouseY >= energyDisplayY && mouseY <= energyDisplayY + ENERGY_HEIGHT) {
            List<Text> toolTipLines = new ArrayList<>();
            toolTipLines.add(new TranslatableText("ui.galacticraft-rewoven.machine.status", this.handler.blockEntity.status.toString()).setStyle(Style.field_24360.setColor(Formatting.GRAY)));
            toolTipLines.add(new TranslatableText("ui.galacticraft-rewoven.machine.current_energy", new GalacticraftEnergyType().getDisplayAmount(this.handler.energy.get()).setStyle(Style.field_24360.setColor(Formatting.BLUE))).setStyle(Style.field_24360.setColor(Formatting.GOLD)));
            toolTipLines.add(new TranslatableText("ui.galacticraft-rewoven.machine.max_energy", new GalacticraftEnergyType().getDisplayAmount(this.handler.getMaxEnergy())).setStyle(Style.field_24360.setColor(Formatting.RED)));

            this.renderTooltip(stack, toolTipLines, mouseX, mouseY);
        }
    }
}
