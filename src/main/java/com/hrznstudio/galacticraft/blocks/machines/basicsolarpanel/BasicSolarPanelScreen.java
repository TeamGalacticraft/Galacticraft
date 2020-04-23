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

package com.hrznstudio.galacticraft.blocks.machines.basicsolarpanel;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class BasicSolarPanelScreen extends MachineContainerScreen<BasicSolarPanelContainer> {

    public static final ContainerFactory<HandledScreen> FACTORY = createFactory(BasicSolarPanelBlockEntity.class, BasicSolarPanelScreen::new);

    private static final Identifier BACKGROUND = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.BASIC_SOLAR_PANEL_SCREEN));
    private static final Identifier OVERLAY = new Identifier(Constants.MOD_ID, Constants.ScreenTextures.getRaw(Constants.ScreenTextures.OVERLAY));

    private static final int ENERGY_X = Constants.TextureCoordinates.ENERGY_LIGHT_X;
    private static final int ENERGY_Y = Constants.TextureCoordinates.ENERGY_LIGHT_Y;
    private static final int ENERGY_WIDTH = Constants.TextureCoordinates.OVERLAY_WIDTH;
    private static final int ENERGY_HEIGHT = Constants.TextureCoordinates.OVERLAY_HEIGHT;
    private static final int ENERGY_DIMMED_X = Constants.TextureCoordinates.ENERGY_DARK_X;
    private static final int ENERGY_DIMMED_Y = Constants.TextureCoordinates.ENERGY_DARK_Y;
    private static final int ENERGY_DIMMED_WIDTH = Constants.TextureCoordinates.OVERLAY_WIDTH;
    private static final int ENERGY_DIMMED_HEIGHT = Constants.TextureCoordinates.OVERLAY_HEIGHT;
    private int energyDisplayX = 0;
    private int energyDisplayY = 0;
    private World world;
    private BlockPos blockPos;


    public BasicSolarPanelScreen(int syncId, PlayerEntity playerEntity, BasicSolarPanelBlockEntity blockEntity) {
        super(new BasicSolarPanelContainer(syncId, playerEntity, blockEntity), playerEntity.inventory, playerEntity.world, blockEntity.getPos(), new TranslatableText("ui.galacticraft-rewoven.basic_solar_panel.name"));
        this.world = playerEntity.world;
        this.blockPos = blockEntity.getPos();
    }

    @Override
    protected void drawBackground(MatrixStack stack, float v, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.renderBackground(stack);
        this.client.getTextureManager().bindTexture(BACKGROUND);

        int leftPos = this.x;
        int topPos = this.y;

        energyDisplayX = leftPos + 10;
        energyDisplayY = topPos + 9;

        this.drawTexture(stack, leftPos, topPos, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.drawEnergyBufferBar(stack);
        this.drawConfigTabs(stack);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float v) {
        super.render(stack, mouseX, mouseY, v);
        DrawableUtils.drawCenteredString(stack, this.client.textRenderer, new TranslatableText("block.galacticraft-rewoven.basic_solar_panel").getKey(), (this.width / 2), this.y + 5, Formatting.DARK_GRAY.getColorValue());
        this.drawMouseoverTooltip(stack, mouseX, mouseY);
    }

    private void drawEnergyBufferBar(MatrixStack stack) {
        float currentEnergy = handler.energy.get();
        float maxEnergy = handler.getMaxEnergy();
        float energyScale = (currentEnergy / maxEnergy);

        this.client.getTextureManager().bindTexture(OVERLAY);
        this.drawTexture(stack, energyDisplayX, energyDisplayY, ENERGY_DIMMED_X, ENERGY_DIMMED_Y, ENERGY_DIMMED_WIDTH, ENERGY_DIMMED_HEIGHT);
        this.drawTexture(stack, energyDisplayX, (energyDisplayY - (int) (ENERGY_HEIGHT * energyScale)) + ENERGY_HEIGHT, ENERGY_X, ENERGY_Y, ENERGY_WIDTH, (int) (ENERGY_HEIGHT * energyScale));
    }

    @Override
    public void drawMouseoverTooltip(MatrixStack stack, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(stack, mouseX, mouseY);
        if (mouseX >= energyDisplayX && mouseX <= energyDisplayX + ENERGY_WIDTH && mouseY >= energyDisplayY && mouseY <= energyDisplayY + ENERGY_HEIGHT) {
            List<Text> toolTipLines = new ArrayList<>();
            toolTipLines.add(new TranslatableText("ui.galacticraft-rewoven.machine.status", this.handler.blockEntity.status.toString()).setStyle(Style.field_24360.setColor(Formatting.GRAY)));
            if (this.handler.blockEntity.status == BasicSolarPanelStatus.COLLECTING) {
                long time = world.getTimeOfDay() % 24000;
                if (time > 6000) {
                    toolTipLines.add(new TranslatableText("ui.galacticraft-rewoven.machine.gj_per_t", (int) (((6000D - (time - 6000D)) / 705.882353D) + 0.5D) * (this.handler.blockEntity.visiblePanels / 9)).setStyle(Style.field_24360.setColor(Formatting.LIGHT_PURPLE)));
                } else {
                    toolTipLines.add(new TranslatableText("ui.galacticraft-rewoven.machine.gj_per_t", (int) ((time / 705.882353D) + 0.5D) * (this.handler.blockEntity.visiblePanels / 9)).setStyle(Style.field_24360.setColor(Formatting.LIGHT_PURPLE)));
                }
            } else if (this.handler.blockEntity.status == BasicSolarPanelStatus.RAINING) {
                long time = world.getTimeOfDay() % 24000;
                if (time > 6000) {
                    toolTipLines.add(new TranslatableText("ui.galacticraft-rewoven.machine.gj_per_t", (int) ((((6000D - (time - 6000D)) / 705.882353D) / 3.0D) + 0.5D)* (this.handler.blockEntity.visiblePanels / 9)).setStyle(Style.field_24360.setColor(Formatting.LIGHT_PURPLE)));
                } else {
                    toolTipLines.add(new TranslatableText("ui.galacticraft-rewoven.machine.gj_per_t", (int) (((time / 705.882353D) / 3.0D) + 0.5D) * (this.handler.blockEntity.visiblePanels / 9)).setStyle(Style.field_24360.setColor(Formatting.LIGHT_PURPLE)));
                }
            }
            toolTipLines.add(new TranslatableText("ui.galacticraft-rewoven.machine.current_energy", new GalacticraftEnergyType().getDisplayAmount(this.handler.energy.get()).setStyle(Style.field_24360.setColor(Formatting.BLUE))).setStyle(Style.field_24360.setColor(Formatting.GOLD)));
            toolTipLines.add(new TranslatableText("ui.galacticraft-rewoven.machine.max_energy", new GalacticraftEnergyType().getDisplayAmount(this.handler.getMaxEnergy())).setStyle(Style.field_24360.setColor(Formatting.RED)));
            toolTipLines.add(new TranslatableText("ui.galacticraft-rewoven.machine.blocked_panels", this.handler.blockEntity.visiblePanels).setStyle(Style.field_24360.setColor(Formatting.YELLOW)));

            this.renderTooltip(stack, toolTipLines, mouseX, mouseY);
        }
        if (mouseX >= this.x - 22 && mouseX <= this.x && mouseY >= this.y + 3 && mouseY <= this.y + (22 + 3)) {
            this.renderTooltip(stack, new TranslatableText("ui.galacticraft-rewoven.tabs.side_config").setStyle(Style.field_24360.setColor(Formatting.GRAY)), mouseX, mouseY);
        }
    }

}
