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
import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity;
import dev.galacticraft.machinelib.api.screen.MachineScreenHandler;
import dev.galacticraft.machinelib.client.api.screen.MachineHandledScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.SolarPanel;
import dev.galacticraft.mod.api.solarpanel.LightSource;
import dev.galacticraft.mod.api.solarpanel.SolarPanelRegistry;
import dev.galacticraft.mod.api.solarpanel.WorldLightSources;
import dev.galacticraft.mod.util.DrawableUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.LinkedList;
import java.util.List;

public class SolarPanelScreen<M extends MachineBlockEntity & SolarPanel, S extends MachineScreenHandler<M>> extends MachineHandledScreen<M, S> {
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

    private static final Component DAY = Component.translatable("ui.galacticraft.machine.solar_panel.day").setStyle(Constant.Text.Color.YELLOW_STYLE);
    private static final Component OVERCAST = Component.translatable("ui.galacticraft.machine.solar_panel.overcast").setStyle(Constant.Text.Color.GRAY_STYLE);
    private static final Component NIGHT = Component.translatable("ui.galacticraft.machine.solar_panel.night").setStyle(Constant.Text.Color.BLUE_STYLE);
    private static final Component BLOCKED = Component.translatable("ui.galacticraft.machine.solar_panel.blocked").setStyle(Constant.Text.Color.DARK_RED_STYLE);
    private static final Component MISSING_SOURCE = Component.translatable("ui.galacticraft.machine.solar_panel.missing_source").setStyle(Constant.Text.Color.WHITE_STYLE);

    private final ResourceLocation solarPanelTexture;
    private final WorldLightSources lightSource;

    public SolarPanelScreen(S handler, Inventory inv, Component title) {
        super(handler, inv, title, Constant.ScreenTexture.SOLAR_PANEL_SCREEN);
        this.solarPanelTexture = SolarPanelRegistry.getSolarPanelTexture(handler.machine.getType());
        this.lightSource = SolarPanelRegistry.getLightSource(handler.machine.getLevel().dimension());
    }

    @Override
    protected void renderBackground(PoseStack matrices, int mouseX, int mouseY, float delta) {
        super.renderBackground(matrices, mouseX, mouseY, delta);
        RenderSystem.setShaderTexture(0, this.solarPanelTexture);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                switch (this.machine.getSource()) {
                    case DAY -> drawNormal(matrices, x, y, SOLAR_PANEL_U, SOLAR_PANEL_V);
                    case NIGHT, OVERCAST -> drawNormal(matrices, x, y, SOLAR_PANEL_NIGHT_U, SOLAR_PANEL_NIGHT_V);
                    case NO_LIGHT_SOURCE -> blit(matrices, this.leftPos + SOLAR_PANEL_X + x * SOLAR_PANEL_WIDTH, this.topPos + SOLAR_PANEL_Y + y * SOLAR_PANEL_HEIGHT, this.getBlitOffset(), SOLAR_PANEL_BLOCKED_U, SOLAR_PANEL_BLOCKED_V, SOLAR_PANEL_WIDTH, SOLAR_PANEL_HEIGHT, 32, 32);
                }
                if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + SOLAR_PANEL_X + x * SOLAR_PANEL_WIDTH, this.topPos + SOLAR_PANEL_Y + y * SOLAR_PANEL_HEIGHT, SOLAR_PANEL_WIDTH, SOLAR_PANEL_HEIGHT)) {
                    RenderSystem.disableDepthTest();
                    RenderSystem.colorMask(true, true, true, false);
                    fillGradient(matrices, this.leftPos + SOLAR_PANEL_X + x * SOLAR_PANEL_WIDTH, this.topPos + SOLAR_PANEL_Y + y * SOLAR_PANEL_HEIGHT, this.leftPos + SOLAR_PANEL_X + x * SOLAR_PANEL_WIDTH + SOLAR_PANEL_WIDTH, this.topPos + SOLAR_PANEL_Y + y * SOLAR_PANEL_HEIGHT + SOLAR_PANEL_HEIGHT, 0x80ffffff, 0x80ffffff, this.getBlitOffset() + 1);
                    RenderSystem.colorMask(true, true, true, true);
                    RenderSystem.enableDepthTest();
                }
            }
        }
        RenderSystem.setShaderTexture(0, this.lightSource.texture());
        switch (this.machine.getSource()){
            case DAY -> blit(matrices, this.leftPos + LIGHT_SOURCE_X, this.topPos + LIGHT_SOURCE_Y, DAY_SOURCE_U, DAY_SOURCE_V, LIGHT_SOURCE_WIDTH, LIGHT_SOURCE_HEIGHT, 64, 64);
            case NIGHT -> blit(matrices, this.leftPos + LIGHT_SOURCE_X, this.topPos + LIGHT_SOURCE_Y, NIGHT_SOURCE_U, NIGHT_SOURCE_V, LIGHT_SOURCE_WIDTH, LIGHT_SOURCE_HEIGHT, 64, 64);
            case OVERCAST -> blit(matrices, this.leftPos + LIGHT_SOURCE_X, this.topPos + LIGHT_SOURCE_Y, OVERCAST_SOURCE_U, OVERCAST_SOURCE_V, LIGHT_SOURCE_WIDTH, LIGHT_SOURCE_HEIGHT, 64, 64);
            case NO_LIGHT_SOURCE -> blit(matrices, this.leftPos + LIGHT_SOURCE_X, this.topPos + LIGHT_SOURCE_Y, MISSING_SOURCE_U, MISSING_SOURCE_V, LIGHT_SOURCE_WIDTH, LIGHT_SOURCE_HEIGHT, 64, 64);
        }
    }

    @Override
    protected void renderTooltip(PoseStack matrices, int mouseX, int mouseY) {
        super.renderTooltip(matrices, mouseX, mouseY);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + SOLAR_PANEL_X + x * SOLAR_PANEL_WIDTH, this.topPos + SOLAR_PANEL_Y + y * SOLAR_PANEL_HEIGHT, SOLAR_PANEL_WIDTH, SOLAR_PANEL_HEIGHT)) {
                    if (this.machine.getBlockage()[y * 3 + x]) {
                        this.renderTooltip(matrices, Component.translatable("ui.galacticraft.machine.solar_panel.status").setStyle(Constant.Text.Color.GRAY_STYLE).append(BLOCKED), mouseX, mouseY);
                    } else {
                        switch (this.machine.getSource()){
                            case DAY -> this.renderTooltip(matrices, Component.translatable("ui.galacticraft.machine.solar_panel.status").setStyle(Constant.Text.Color.GRAY_STYLE).append(DAY), mouseX, mouseY);
                            case OVERCAST -> this.renderTooltip(matrices, Component.translatable("ui.galacticraft.machine.solar_panel.status").setStyle(Constant.Text.Color.GRAY_STYLE).append(OVERCAST), mouseX, mouseY);
                            case NIGHT -> this.renderTooltip(matrices, Component.translatable("ui.galacticraft.machine.solar_panel.status").setStyle(Constant.Text.Color.GRAY_STYLE).append(NIGHT), mouseX, mouseY);
                            case NO_LIGHT_SOURCE -> this.renderTooltip(matrices, Component.translatable("ui.galacticraft.machine.solar_panel.status").setStyle(Constant.Text.Color.GRAY_STYLE).append(MISSING_SOURCE), mouseX, mouseY);
                        }
                    }
                    return;
                }
            }
        }

        if (DrawableUtil.isWithin(mouseX, mouseY, this.leftPos + LIGHT_SOURCE_X, this.topPos + LIGHT_SOURCE_Y, LIGHT_SOURCE_WIDTH, LIGHT_SOURCE_HEIGHT)) {
            List<Component> tooltip = new LinkedList<>();
            LightSource source = switch (this.machine.getSource()) {
                case DAY -> this.lightSource.day();
                case OVERCAST -> this.lightSource.overcast();
                case NIGHT -> this.lightSource.night();
                case NO_LIGHT_SOURCE -> this.lightSource.missing();
            };
            tooltip.add(Component.translatable("ui.galacticraft.machine.solar_panel.source").setStyle(Constant.Text.Color.AQUA_STYLE).append(source.name()));
            tooltip.add(Component.translatable("ui.galacticraft.machine.solar_panel.strength", source.strength()).setStyle(Constant.Text.Color.GREEN_STYLE));
            tooltip.add(Component.translatable("ui.galacticraft.machine.solar_panel.atmospheric_interference", source.atmosphericInterference()).setStyle(Constant.Text.Color.LIGHT_PURPLE_STYLE));
            this.renderComponentTooltip(matrices, tooltip, mouseX, mouseY);
        }
    }

    private void drawNormal(PoseStack matrices, int x, int y, int normalU, int normalV) {
        if (this.machine.getBlockage()[y * 3 + x]) {
            blit(matrices, this.leftPos + SOLAR_PANEL_X + x * SOLAR_PANEL_WIDTH, this.topPos + SOLAR_PANEL_Y + y * SOLAR_PANEL_HEIGHT, this.getBlitOffset(), SOLAR_PANEL_BLOCKED_U, SOLAR_PANEL_BLOCKED_V, SOLAR_PANEL_WIDTH, SOLAR_PANEL_HEIGHT, 32, 32);
        } else {
            blit(matrices, this.leftPos + SOLAR_PANEL_X + x * SOLAR_PANEL_WIDTH, this.topPos + SOLAR_PANEL_Y + y * SOLAR_PANEL_HEIGHT, this.getBlitOffset(), normalU, normalV, SOLAR_PANEL_WIDTH, SOLAR_PANEL_HEIGHT, 32, 32);
        }
    }

    @Override
    public void appendEnergyTooltip(List<Component> list) {
        if (this.machine.getStatus().type().isActive()) {
            list.add(Component.translatable("ui.galacticraft.machine.gj_per_t", this.machine.getCurrentEnergyGeneration()).setStyle(Constant.Text.Color.LIGHT_PURPLE_STYLE));
        }
    }
}
