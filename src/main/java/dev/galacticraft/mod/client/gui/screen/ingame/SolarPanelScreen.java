/*
 * Copyright (c) 2019-2025 Team Galacticraft
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
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.machine.MachineStatus;
import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.machinelib.client.api.util.GraphicsUtil;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.SolarPanel;
import dev.galacticraft.mod.api.block.entity.SolarPanel.SolarPanelSource;
import dev.galacticraft.mod.api.solarpanel.LightSource;
import dev.galacticraft.mod.api.solarpanel.SolarPanelRegistry;
import dev.galacticraft.mod.screen.SolarPanelMenu;
import dev.galacticraft.mod.util.DrawableUtil;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SolarPanelScreen<Machine extends MachineBlockEntity & SolarPanel, Menu extends SolarPanelMenu<Machine>> extends MachineScreen<Machine, Menu> {
    private static final int LIGHT_SOURCE_X = 120;
    private static final int LIGHT_SOURCE_Y = 30;
    private static final int LIGHT_SOURCE_WIDTH = 32;
    private static final int LIGHT_SOURCE_HEIGHT = 32;

    private static final int SOLAR_PANEL_X = 46;
    private static final int SOLAR_PANEL_Y = 22;
    private static final int SOLAR_PANEL_WIDTH = 16;
    private static final int SOLAR_PANEL_HEIGHT = 16;

    private static final Component DAY = Component.translatable(Translations.SolarPanel.DAY).setStyle(Constant.Text.YELLOW_STYLE);
    private static final Component NIGHT = Component.translatable(Translations.SolarPanel.NIGHT).setStyle(Constant.Text.GRAY_STYLE);
    private static final Component OVERCAST = Component.translatable(Translations.SolarPanel.OVERCAST).setStyle(Constant.Text.BLUE_STYLE);
    private static final Component STORMY = Component.translatable(Translations.SolarPanel.STORMY).setStyle(Constant.Text.DARK_GRAY_STYLE);
    private static final Component BLOCKED = Component.translatable(Translations.SolarPanel.BLOCKED).setStyle(Constant.Text.DARK_RED_STYLE);
    private static final Component MISSING_SOURCE = Component.translatable(Translations.SolarPanel.MISSING_SOURCE).setStyle(Constant.Text.WHITE_STYLE);

    private final Map<SolarPanelSource, ResourceLocation> solarPanelTextures;
    private final Map<SolarPanelSource, LightSource> lightSources;

    public SolarPanelScreen(Menu menu, Inventory inv, Component title) {
        super(menu, title, Constant.ScreenTexture.SOLAR_PANEL_SCREEN);
        this.solarPanelTextures = SolarPanelRegistry.getSolarPanelTextures(menu.be.getType());
        this.lightSources = SolarPanelRegistry.getLightSources(this.menu.playerInventory.player.level().dimension());
    }

    @Override
    protected void renderMachineBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderMachineBackground(graphics, mouseX, mouseY, delta);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                drawNormal(graphics, this.solarPanelTextures.get(this.menu.getSource()), x, y);
                if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + SOLAR_PANEL_X + x * SOLAR_PANEL_WIDTH, this.topPos + SOLAR_PANEL_Y + y * SOLAR_PANEL_HEIGHT, SOLAR_PANEL_WIDTH, SOLAR_PANEL_HEIGHT)) {
                    RenderSystem.disableDepthTest();
                    RenderSystem.colorMask(true, true, true, false);
                    graphics.fillGradient(this.leftPos + SOLAR_PANEL_X + x * SOLAR_PANEL_WIDTH, this.topPos + SOLAR_PANEL_Y + y * SOLAR_PANEL_HEIGHT, this.leftPos + SOLAR_PANEL_X + x * SOLAR_PANEL_WIDTH + SOLAR_PANEL_WIDTH, this.topPos + SOLAR_PANEL_Y + y * SOLAR_PANEL_HEIGHT + SOLAR_PANEL_HEIGHT, 0x80ffffff, 0x80ffffff, 1);
                    RenderSystem.colorMask(true, true, true, true);
                    RenderSystem.enableDepthTest();
                }
            }
        }
        graphics.blit(this.lightSources.get(this.menu.getSource()).location(), this.leftPos + LIGHT_SOURCE_X, this.topPos + LIGHT_SOURCE_Y, 0, 0, LIGHT_SOURCE_WIDTH, LIGHT_SOURCE_HEIGHT, 32, 32);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + SOLAR_PANEL_X + x * SOLAR_PANEL_WIDTH, this.topPos + SOLAR_PANEL_Y + y * SOLAR_PANEL_HEIGHT, SOLAR_PANEL_WIDTH, SOLAR_PANEL_HEIGHT)) {
                    GraphicsUtil.highlightElement(graphics, this.leftPos, this.topPos, SOLAR_PANEL_X + x * SOLAR_PANEL_WIDTH, SOLAR_PANEL_Y + y * SOLAR_PANEL_HEIGHT, SOLAR_PANEL_WIDTH, SOLAR_PANEL_HEIGHT, 0x80ffffff);

                    if (this.menu.getBlockage()[y * 3 + x]) {
                        graphics.renderTooltip(this.font, Component.translatable(Translations.SolarPanel.STATUS).setStyle(Constant.Text.GRAY_STYLE).append(BLOCKED), mouseX, mouseY);
                    } else {
                        switch (this.menu.getSource()){
                            case DAY -> graphics.renderTooltip(this.font, Component.translatable(Translations.SolarPanel.STATUS).setStyle(Constant.Text.GRAY_STYLE).append(DAY), mouseX, mouseY);
                            case OVERCAST -> graphics.renderTooltip(this.font, Component.translatable(Translations.SolarPanel.STATUS).setStyle(Constant.Text.GRAY_STYLE).append(OVERCAST), mouseX, mouseY);
                            case STORMY -> graphics.renderTooltip(this.font, Component.translatable(Translations.SolarPanel.STATUS).setStyle(Constant.Text.GRAY_STYLE).append(STORMY), mouseX, mouseY);
                            case NIGHT -> graphics.renderTooltip(this.font, Component.translatable(Translations.SolarPanel.STATUS).setStyle(Constant.Text.GRAY_STYLE).append(NIGHT), mouseX, mouseY);
                            case NO_LIGHT_SOURCE -> graphics.renderTooltip(this.font, Component.translatable(Translations.SolarPanel.STATUS).setStyle(Constant.Text.GRAY_STYLE).append(MISSING_SOURCE), mouseX, mouseY);
                        }
                    }
                    return;
                }
            }
        }

        if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + LIGHT_SOURCE_X, this.topPos + LIGHT_SOURCE_Y, LIGHT_SOURCE_WIDTH, LIGHT_SOURCE_HEIGHT)) {
            List<Component> tooltip = new LinkedList<>();
            LightSource source = this.lightSources.get(this.menu.getSource());
            tooltip.add(Component.translatable(Translations.SolarPanel.LIGHT_SOURCE).setStyle(Constant.Text.AQUA_STYLE).append(source.name()));
            tooltip.add(Component.translatable(Translations.SolarPanel.STRENGTH, source.strength()).setStyle(Constant.Text.GREEN_STYLE));
            tooltip.add(Component.translatable(Translations.SolarPanel.ATMOSPHERIC_INTERFERENCE, source.atmosphericInterference()).setStyle(Constant.Text.LIGHT_PURPLE_STYLE));
            graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
        }
    }

    private void drawNormal(GuiGraphics graphics, ResourceLocation normal, int x, int y) {
        if (this.menu.getBlockage()[y * 3 + x]) {
            normal = this.solarPanelTextures.get(SolarPanelSource.NO_LIGHT_SOURCE);
        }
        graphics.blit(normal, this.leftPos + SOLAR_PANEL_X + x * SOLAR_PANEL_WIDTH, this.topPos + SOLAR_PANEL_Y + y * SOLAR_PANEL_HEIGHT, 0, 0, 0, SOLAR_PANEL_WIDTH, SOLAR_PANEL_HEIGHT, 16, 16);
    }

    @Override
    public void appendEnergyTooltip(List<Component> list) {
        super.appendEnergyTooltip(list);
        if (this.menu.state.isActive()) {
            list.add(Component.translatable(Translations.Ui.GJT, this.menu.getCurrentEnergyGeneration()).setStyle(Constant.Text.LIGHT_PURPLE_STYLE));
        }
    }
}
