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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Style;
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
@Environment(EnvType.CLIENT)
public class BasicSolarPanelScreen extends MachineContainerScreen<BasicSolarPanelContainer> {

    public static final ContainerFactory<ContainerScreen> FACTORY = createFactory(BasicSolarPanelBlockEntity.class, BasicSolarPanelScreen::new);

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
    protected void drawBackground(float v, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.renderBackground();
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);

        int leftPos = this.x;
        int topPos = this.y;

        energyDisplayX = leftPos + 10;
        energyDisplayY = topPos + 9;

        this.blit(leftPos, topPos, 0, 0, this.containerWidth, this.containerHeight);
        this.drawEnergyBufferBar();
        this.drawConfigTabs();
    }

    @Override
    public void render(int mouseX, int mouseY, float v) {
        super.render(mouseX, mouseY, v);
        DrawableUtils.drawCenteredString(this.minecraft.textRenderer, new TranslatableText("block.galacticraft-rewoven.basic_solar_panel").asFormattedString(), (this.width / 2), this.y + 5, Formatting.DARK_GRAY.getColorValue());
        this.drawMouseoverTooltip(mouseX, mouseY);
    }

    private void drawEnergyBufferBar() {
        float currentEnergy = container.energy.get();
        float maxEnergy = container.getMaxEnergy();
        float energyScale = (currentEnergy / maxEnergy);

        this.minecraft.getTextureManager().bindTexture(OVERLAY);
        this.blit(energyDisplayX, energyDisplayY, ENERGY_DIMMED_X, ENERGY_DIMMED_Y, ENERGY_DIMMED_WIDTH, ENERGY_DIMMED_HEIGHT);
        this.blit(energyDisplayX, (energyDisplayY - (int) (ENERGY_HEIGHT * energyScale)) + ENERGY_HEIGHT, ENERGY_X, ENERGY_Y, ENERGY_WIDTH, (int) (ENERGY_HEIGHT * energyScale));
    }

    @Override
    public void drawMouseoverTooltip(int mouseX, int mouseY) {
        super.drawMouseoverTooltip(mouseX, mouseY);
        if (mouseX >= energyDisplayX && mouseX <= energyDisplayX + ENERGY_WIDTH && mouseY >= energyDisplayY && mouseY <= energyDisplayY + ENERGY_HEIGHT) {
            List<String> toolTipLines = new ArrayList<>();
            toolTipLines.add(new TranslatableText("ui.galacticraft-rewoven.machine.status", container.blockEntity.status.toString()).setStyle(new Style().setColor(Formatting.GRAY)).asFormattedString());
            if (container.blockEntity.status == BasicSolarPanelStatus.COLLECTING) {
                long time = world.getTimeOfDay() % 24000;
                if (time > 6000) {
                    toolTipLines.add(new TranslatableText("ui.galacticraft-rewoven.machine.gj_per_t", (int) (((6000D - (time - 6000D)) / 705.882353D) + 0.5D) * (container.blockEntity.visiblePanels / 9)).setStyle(new Style().setColor(Formatting.LIGHT_PURPLE)).asFormattedString());
                } else {
                    toolTipLines.add(new TranslatableText("ui.galacticraft-rewoven.machine.gj_per_t", (int) ((time / 705.882353D) + 0.5D) * (container.blockEntity.visiblePanels / 9)).setStyle(new Style().setColor(Formatting.LIGHT_PURPLE)).asFormattedString());
                }
            } else if (container.blockEntity.status == BasicSolarPanelStatus.RAINING) {
                long time = world.getTimeOfDay() % 24000;
                if (time > 6000) {
                    toolTipLines.add(new TranslatableText("ui.galacticraft-rewoven.machine.gj_per_t", (int) ((((6000D - (time - 6000D)) / 705.882353D) / 3.0D) + 0.5D)* (container.blockEntity.visiblePanels / 9)).setStyle(new Style().setColor(Formatting.LIGHT_PURPLE)).asFormattedString());
                } else {
                    toolTipLines.add(new TranslatableText("ui.galacticraft-rewoven.machine.gj_per_t", (int) (((time / 705.882353D) / 3.0D) + 0.5D) * (container.blockEntity.visiblePanels / 9)).setStyle(new Style().setColor(Formatting.LIGHT_PURPLE)).asFormattedString());
                }
            }
            toolTipLines.add("\u00A76" + new TranslatableText("ui.galacticraft-rewoven.machine.current_energy", new GalacticraftEnergyType().getDisplayAmount(container.energy.get()).setStyle(new Style().setColor(Formatting.BLUE))).asFormattedString() + "\u00A7r");
            toolTipLines.add("\u00A7c" + new TranslatableText("ui.galacticraft-rewoven.machine.max_energy", new GalacticraftEnergyType().getDisplayAmount(container.getMaxEnergy())).asFormattedString() + "\u00A7r");
            toolTipLines.add(new TranslatableText("ui.galacticraft-rewoven.machine.blocked_panels", container.blockEntity.visiblePanels).setStyle(new Style().setColor(Formatting.YELLOW)).asFormattedString());

            this.renderTooltip(toolTipLines, mouseX, mouseY);
        }
        if (mouseX >= this.x - 22 && mouseX <= this.x && mouseY >= this.y + 3 && mouseY <= this.y + (22 + 3)) {
            this.renderTooltip("\u00A77" + new TranslatableText("ui.galacticraft-rewoven.tabs.side_config").asFormattedString(), mouseX, mouseY);
        }
    }

}
