/*
 * Copyright (c) 2019-2024 Team Galacticraft
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
import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.SolarPanel;
import dev.galacticraft.mod.api.solarpanel.LightSource;
import dev.galacticraft.mod.api.solarpanel.SolarPanelRegistry;
import dev.galacticraft.mod.api.solarpanel.WorldLightSources;
import dev.galacticraft.mod.screen.SolarPanelMenu;
import dev.galacticraft.mod.util.DrawableUtil;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.LinkedList;
import java.util.List;

public class SolarPanelScreen<M extends MachineBlockEntity & SolarPanel, S extends SolarPanelMenu<M>> extends MachineScreen<M, S> {
    private static final int DAY_SOURCE_U = 0;
    private static final int DAY_SOURCE_V = 0;
    private static final int OVERCAST_SOURCE_U = 32;
    private static final int OVERCAST_SOURCE_V = 32;
    private static final int NIGHT_SOURCE_U = 32;
    private static final int NIGHT_SOURCE_V = 0;
    private static final int MISSING_SOURCE_U = 0;
    private static final int MISSING_SOURCE_V = 32;

    private static final int SOLAR_PANEL_U = 0;
    private static final int SOLAR_PANEL_V = 0;
    private static final int SOLAR_PANEL_NIGHT_U = 0;
    private static final int SOLAR_PANEL_NIGHT_V = 16;
    private static final int SOLAR_PANEL_BLOCKED_U = 16;
    private static final int SOLAR_PANEL_BLOCKED_V = 0;

    private static final int LIGHT_SOURCE_X = 128;
    private static final int LIGHT_SOURCE_Y = 25;
    private static final int LIGHT_SOURCE_WIDTH = 32;
    private static final int LIGHT_SOURCE_HEIGHT = 32;

    private static final int SOLAR_PANEL_X = 38;
    private static final int SOLAR_PANEL_Y = 17;
    private static final int SOLAR_PANEL_WIDTH = 16;
    private static final int SOLAR_PANEL_HEIGHT = 16;

    private static final Component DAY = Component.translatable(Translations.SolarPanel.DAY).setStyle(Constant.Text.Color.YELLOW_STYLE);
    private static final Component OVERCAST = Component.translatable(Translations.SolarPanel.OVERCAST).setStyle(Constant.Text.Color.GRAY_STYLE);
    private static final Component NIGHT = Component.translatable(Translations.SolarPanel.NIGHT).setStyle(Constant.Text.Color.BLUE_STYLE);
    private static final Component BLOCKED = Component.translatable(Translations.SolarPanel.BLOCKED).setStyle(Constant.Text.Color.DARK_RED_STYLE);
    private static final Component MISSING_SOURCE = Component.translatable(Translations.SolarPanel.MISSING_SOURCE).setStyle(Constant.Text.Color.WHITE_STYLE);

    private final ResourceLocation solarPanelTexture;
    private final WorldLightSources lightSource;

    public SolarPanelScreen(S handler, Inventory inv, Component title) {
        super(handler, title, Constant.ScreenTexture.SOLAR_PANEL_SCREEN);
        this.solarPanelTexture = SolarPanelRegistry.getSolarPanelTexture(handler.machine.getType());
        this.lightSource = SolarPanelRegistry.getLightSource(this.menu.playerInventory.player.level().dimension());
    }

    @Override
    protected void renderMachineBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderMachineBackground(graphics, mouseX, mouseY, delta);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                switch (this.menu.getSource()) {
                    case DAY -> drawNormal(graphics, x, y, SOLAR_PANEL_U, SOLAR_PANEL_V);
                    case NIGHT, OVERCAST -> drawNormal(graphics, x, y, SOLAR_PANEL_NIGHT_U, SOLAR_PANEL_NIGHT_V);
                    case NO_LIGHT_SOURCE -> graphics.blit(this.solarPanelTexture, this.leftPos + SOLAR_PANEL_X + x * SOLAR_PANEL_WIDTH, this.topPos + SOLAR_PANEL_Y + y * SOLAR_PANEL_HEIGHT, 0, SOLAR_PANEL_BLOCKED_U, SOLAR_PANEL_BLOCKED_V, SOLAR_PANEL_WIDTH, SOLAR_PANEL_HEIGHT, 32, 32);
                }
                if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + SOLAR_PANEL_X + x * SOLAR_PANEL_WIDTH, this.topPos + SOLAR_PANEL_Y + y * SOLAR_PANEL_HEIGHT, SOLAR_PANEL_WIDTH, SOLAR_PANEL_HEIGHT)) {
                    RenderSystem.disableDepthTest();
                    RenderSystem.colorMask(true, true, true, false);
                    graphics.fillGradient(this.leftPos + SOLAR_PANEL_X + x * SOLAR_PANEL_WIDTH, this.topPos + SOLAR_PANEL_Y + y * SOLAR_PANEL_HEIGHT, this.leftPos + SOLAR_PANEL_X + x * SOLAR_PANEL_WIDTH + SOLAR_PANEL_WIDTH, this.topPos + SOLAR_PANEL_Y + y * SOLAR_PANEL_HEIGHT + SOLAR_PANEL_HEIGHT, 0x80ffffff, 0x80ffffff, 1);
                    RenderSystem.colorMask(true, true, true, true);
                    RenderSystem.enableDepthTest();
                }
            }
        }
        switch (this.menu.getSource()) {
            case DAY -> graphics.blit(this.lightSource.texture(), this.leftPos + LIGHT_SOURCE_X, this.topPos + LIGHT_SOURCE_Y, DAY_SOURCE_U, DAY_SOURCE_V, LIGHT_SOURCE_WIDTH, LIGHT_SOURCE_HEIGHT, 64, 64);
            case NIGHT -> graphics.blit(this.lightSource.texture(), this.leftPos + LIGHT_SOURCE_X, this.topPos + LIGHT_SOURCE_Y, NIGHT_SOURCE_U, NIGHT_SOURCE_V, LIGHT_SOURCE_WIDTH, LIGHT_SOURCE_HEIGHT, 64, 64);
            case OVERCAST -> graphics.blit(this.lightSource.texture(), this.leftPos + LIGHT_SOURCE_X, this.topPos + LIGHT_SOURCE_Y, OVERCAST_SOURCE_U, OVERCAST_SOURCE_V, LIGHT_SOURCE_WIDTH, LIGHT_SOURCE_HEIGHT, 64, 64);
            case NO_LIGHT_SOURCE -> graphics.blit(this.lightSource.texture(), this.leftPos + LIGHT_SOURCE_X, this.topPos + LIGHT_SOURCE_Y, MISSING_SOURCE_U, MISSING_SOURCE_V, LIGHT_SOURCE_WIDTH, LIGHT_SOURCE_HEIGHT, 64, 64);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + SOLAR_PANEL_X + x * SOLAR_PANEL_WIDTH, this.topPos + SOLAR_PANEL_Y + y * SOLAR_PANEL_HEIGHT, SOLAR_PANEL_WIDTH, SOLAR_PANEL_HEIGHT)) {
                    if (this.menu.getBlockage()[y * 3 + x]) {
                        graphics.renderTooltip(this.font, Component.translatable(Translations.SolarPanel.STATUS).setStyle(Constant.Text.Color.GRAY_STYLE).append(BLOCKED), mouseX, mouseY);
                    } else {
                        switch (this.menu.getSource()){
                            case DAY -> graphics.renderTooltip(this.font, Component.translatable(Translations.SolarPanel.STATUS).setStyle(Constant.Text.Color.GRAY_STYLE).append(DAY), mouseX, mouseY);
                            case OVERCAST -> graphics.renderTooltip(this.font, Component.translatable(Translations.SolarPanel.STATUS).setStyle(Constant.Text.Color.GRAY_STYLE).append(OVERCAST), mouseX, mouseY);
                            case NIGHT -> graphics.renderTooltip(this.font, Component.translatable(Translations.SolarPanel.STATUS).setStyle(Constant.Text.Color.GRAY_STYLE).append(NIGHT), mouseX, mouseY);
                            case NO_LIGHT_SOURCE -> graphics.renderTooltip(this.font, Component.translatable(Translations.SolarPanel.STATUS).setStyle(Constant.Text.Color.GRAY_STYLE).append(MISSING_SOURCE), mouseX, mouseY);
                        }
                    }
                    return;
                }
            }
        }

        if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + LIGHT_SOURCE_X, this.topPos + LIGHT_SOURCE_Y, LIGHT_SOURCE_WIDTH, LIGHT_SOURCE_HEIGHT)) {
            List<Component> tooltip = new LinkedList<>();
            LightSource source = switch (this.menu.getSource()) {
                case DAY -> this.lightSource.day();
                case OVERCAST -> this.lightSource.overcast();
                case NIGHT -> this.lightSource.night();
                case NO_LIGHT_SOURCE -> this.lightSource.missing();
            };
            tooltip.add(Component.translatable(Translations.SolarPanel.LIGHT_SOURCE).setStyle(Constant.Text.Color.AQUA_STYLE).append(source.name()));
            tooltip.add(Component.translatable(Translations.SolarPanel.STRENGTH, source.strength()).setStyle(Constant.Text.Color.GREEN_STYLE));
            tooltip.add(Component.translatable(Translations.SolarPanel.ATMOSPHERIC_INTERFERENCE, source.atmosphericInterference()).setStyle(Constant.Text.Color.LIGHT_PURPLE_STYLE));
            graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
        }
    }

    private void drawNormal(GuiGraphics graphics, int x, int y, int normalU, int normalV) {
        if (this.menu.getBlockage()[y * 3 + x]) {
            graphics.blit(this.solarPanelTexture, this.leftPos + SOLAR_PANEL_X + x * SOLAR_PANEL_WIDTH, this.topPos + SOLAR_PANEL_Y + y * SOLAR_PANEL_HEIGHT, 0, SOLAR_PANEL_BLOCKED_U, SOLAR_PANEL_BLOCKED_V, SOLAR_PANEL_WIDTH, SOLAR_PANEL_HEIGHT, 32, 32);
        } else {
            graphics.blit(this.solarPanelTexture, this.leftPos + SOLAR_PANEL_X + x * SOLAR_PANEL_WIDTH, this.topPos + SOLAR_PANEL_Y + y * SOLAR_PANEL_HEIGHT, 0, normalU, normalV, SOLAR_PANEL_WIDTH, SOLAR_PANEL_HEIGHT, 32, 32);
        }
    }

    @Override
    public void appendEnergyTooltip(List<Component> list) {
        if (this.menu.state.isActive()) {
            list.add(Component.translatable(Translations.Ui.GJT, this.menu.getCurrentEnergyGeneration()).setStyle(Constant.Text.Color.LIGHT_PURPLE_STYLE));
        }
    }
}
