/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

import dev.galacticraft.impl.network.c2s.RequestSpaceRaceStatsPayload;
import dev.galacticraft.impl.network.c2s.UpdateSpaceRaceVisibilityPayload;
import dev.galacticraft.mod.client.gui.screen.ingame.spacerace.PlayerClickArea;
import dev.galacticraft.mod.client.gui.screen.ingame.spacerace.ScrollbarAxis;
import dev.galacticraft.mod.client.gui.screen.ingame.spacerace.ScrollbarInfo;
import dev.galacticraft.mod.client.gui.screen.ingame.spacerace.ScrollbarType;
import dev.galacticraft.mod.client.gui.screen.ingame.spacerace.ServerStatsMode;
import dev.galacticraft.mod.client.gui.screen.ingame.spacerace.TeamClickArea;
import dev.galacticraft.mod.client.gui.screen.ingame.spacerace.TreeNodeRenderInfo;
import dev.galacticraft.mod.client.gui.widget.SpaceRaceButton;
import dev.galacticraft.mod.client.spacerace.SpaceRaceClientStats;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class SpaceRaceServerStatsSection {
    private SpaceRaceServerStatsSection() {
    }

    static void initializeMenu(SpaceRaceScreen screen) {
        SpaceRaceScreenMenus.clearSectionButtons(screen);
        screen.serverStatsMode = ServerStatsMode.PLAYER;
        screen.selectedServerPlayerId = null;
        screen.selectedServerTeamId = null;
        screen.serverPlayerScroll = 0;
        screen.serverPlayerDetailScroll = 0;
        screen.serverTeamScroll = 0;
        screen.serverTeamDetailScroll = 0;
        screen.serverTreeScrollX = 0;
        screen.serverTreeScrollY = 0;
        screen.centerServerTreeVerticalOnNextRender = true;
        screen.addButton(Component.translatable(Translations.SpaceRace.BACK), screen.getLeft() + 5, screen.getTop() + 5, 40, 14, button -> handleBack(screen));

        ClientPlayNetworking.send(RequestSpaceRaceStatsPayload.INSTANCE);
        int panelX = screen.getLeft() + 10;
        int panelWidth = screen.backgroundWidth - 20;
        int panelRight = panelX + panelWidth;
        int contentX = panelX + SpaceRaceScreen.STATS_PANEL_PADDING;
        int modeStartX = Math.max(contentX, screen.getLeft() + 5 + 40 + SpaceRaceScreen.SERVER_MODE_BUTTON_SPACING);
        int modeButtonAreaWidth = Math.max(0, panelRight - SpaceRaceScreen.STATS_PANEL_PADDING - modeStartX);
        int modeButtonWidth = Math.max(1, (modeButtonAreaWidth - SpaceRaceScreen.SERVER_MODE_BUTTON_SPACING * 2) / 3);
        int modeButtonY = screen.getTop() + 5;
        screen.serverPlayerTabButton = screen.addButton(Component.translatable(Translations.SpaceRace.TAB_PLAYER), modeStartX, modeButtonY, modeButtonWidth, SpaceRaceScreen.SERVER_MODE_BUTTON_HEIGHT, button -> setMode(screen, ServerStatsMode.PLAYER));
        screen.serverTeamTabButton = screen.addButton(Component.translatable(Translations.SpaceRace.TAB_TEAM), modeStartX + modeButtonWidth + SpaceRaceScreen.SERVER_MODE_BUTTON_SPACING, modeButtonY, modeButtonWidth, SpaceRaceScreen.SERVER_MODE_BUTTON_HEIGHT, button -> setMode(screen, ServerStatsMode.TEAM));
        screen.serverTreeTabButton = screen.addButton(Component.translatable(Translations.SpaceRace.TAB_SERVER), modeStartX + (modeButtonWidth + SpaceRaceScreen.SERVER_MODE_BUTTON_SPACING) * 2, modeButtonY, modeButtonWidth, SpaceRaceScreen.SERVER_MODE_BUTTON_HEIGHT, button -> setMode(screen, ServerStatsMode.SERVER));
        updateModeButtons(screen);

        int hideButtonWidth = getCompactButtonWidth(screen,
                Component.translatable(Translations.SpaceRace.HIDE_MY_ADVANCEMENTS),
                Component.translatable(Translations.SpaceRace.SHOW_MY_ADVANCEMENTS)
        );
        int progressionButtonWidth = getCompactButtonWidth(screen,
                Component.translatable(Translations.SpaceRace.SHOW_SIMPLE_PROGRESSION),
                Component.translatable(Translations.SpaceRace.SHOW_FULL_PROGRESSION)
        );

        screen.serverHideAdvancementsButton = screen.addScreenWidget(new SpaceRaceButton(
                getVisibilityButtonText(),
                0,
                0,
                hideButtonWidth,
                SpaceRaceScreen.COMPACT_TOGGLE_BUTTON_HEIGHT,
                button -> toggleServerAdvancementVisibility()
        ));
        screen.serverVisibilityButton = screen.addScreenWidget(new SpaceRaceButton(
                getProgressionButtonText(screen),
                0,
                0,
                progressionButtonWidth,
                SpaceRaceScreen.COMPACT_TOGGLE_BUTTON_HEIGHT,
                button -> toggleImportantProgression(screen)
        ));
        layoutBottomButtons(screen);

        screen.addScreenRenderable((graphics, mouseX, mouseY, delta) -> renderPanel(screen, graphics, mouseX, mouseY));
    }

    static void toggleImportantProgression(SpaceRaceScreen screen) {
        screen.importantProgressionOnly = !screen.importantProgressionOnly;
    }

    static void toggleServerAdvancementVisibility() {
        boolean hideServerAdvancements = !SpaceRaceClientStats.isHideServerAdvancements();
        boolean hideGlobalLeaderboard = SpaceRaceClientStats.isHideGlobalLeaderboard();
        SpaceRaceClientStats.setVisibility(hideServerAdvancements, hideGlobalLeaderboard);
        ClientPlayNetworking.send(new UpdateSpaceRaceVisibilityPayload(hideServerAdvancements, hideGlobalLeaderboard));
    }

    static Component getProgressionButtonText(SpaceRaceScreen screen) {
        if (screen.importantProgressionOnly) {
            return Component.translatable(Translations.SpaceRace.SHOW_FULL_PROGRESSION);
        }
        return Component.translatable(Translations.SpaceRace.SHOW_SIMPLE_PROGRESSION);
    }

    static Component getVisibilityButtonText() {
        if (SpaceRaceClientStats.isHideServerAdvancements()) {
            return Component.translatable(Translations.SpaceRace.SHOW_MY_ADVANCEMENTS);
        }
        return Component.translatable(Translations.SpaceRace.HIDE_MY_ADVANCEMENTS);
    }

    static void setMode(SpaceRaceScreen screen, ServerStatsMode mode) {
        if (screen.serverStatsMode == mode) {
            return;
        }
        screen.serverStatsMode = mode;
        screen.selectedServerPlayerId = null;
        screen.selectedServerTeamId = null;
        screen.serverScrollbar = null;
        screen.serverTreeVerticalScrollbar = null;
        screen.serverTreeHorizontalScrollbar = null;
        screen.activeScrollbar = ScrollbarType.NONE;
        if (mode == ServerStatsMode.SERVER) {
            screen.centerServerTreeVerticalOnNextRender = true;
        }
        updateModeButtons(screen);
    }

    static void updateModeButtons(SpaceRaceScreen screen) {
        if (screen.serverPlayerTabButton != null) {
            screen.serverPlayerTabButton.active = screen.serverStatsMode != ServerStatsMode.PLAYER;
        }
        if (screen.serverTeamTabButton != null) {
            screen.serverTeamTabButton.active = screen.serverStatsMode != ServerStatsMode.TEAM;
        }
        if (screen.serverTreeTabButton != null) {
            screen.serverTreeTabButton.active = screen.serverStatsMode != ServerStatsMode.SERVER;
        }
        layoutBottomButtons(screen);
    }

    static void layoutBottomButtons(SpaceRaceScreen screen) {
        if (screen.menu != SpaceRaceMenu.SERVER_STATS) {
            return;
        }

        int panelX = screen.getLeft() + 10;
        int panelWidth = screen.backgroundWidth - 20;
        int panelRight = panelX + panelWidth;
        int buttonRight = panelRight - SpaceRaceScreen.STATS_PANEL_PADDING;
        int buttonY = screen.getBottom() - SpaceRaceScreen.COMPACT_TOGGLE_BUTTON_HEIGHT - 4;

        int hideButtonWidth = getCompactButtonWidth(screen,
                Component.translatable(Translations.SpaceRace.HIDE_MY_ADVANCEMENTS),
                Component.translatable(Translations.SpaceRace.SHOW_MY_ADVANCEMENTS)
        );
        if (screen.serverHideAdvancementsButton != null) {
            screen.serverHideAdvancementsButton.setX(buttonRight - hideButtonWidth);
            screen.serverHideAdvancementsButton.setY(buttonY);
            screen.serverHideAdvancementsButton.visible = true;
            screen.serverHideAdvancementsButton.active = true;
        }

        boolean showProgressionButton = screen.serverStatsMode != ServerStatsMode.SERVER;
        if (screen.serverVisibilityButton != null) {
            int progressionButtonWidth = getCompactButtonWidth(screen,
                    Component.translatable(Translations.SpaceRace.SHOW_SIMPLE_PROGRESSION),
                    Component.translatable(Translations.SpaceRace.SHOW_FULL_PROGRESSION)
            );
            screen.serverVisibilityButton.setX(buttonRight - hideButtonWidth - SpaceRaceScreen.COMPACT_TOGGLE_BUTTON_SPACING - progressionButtonWidth);
            screen.serverVisibilityButton.setY(buttonY);
            screen.serverVisibilityButton.visible = showProgressionButton;
            screen.serverVisibilityButton.active = showProgressionButton;
        }
    }

    static int getBottomControlsReserved(SpaceRaceScreen screen) {
        if (screen.serverStatsMode == ServerStatsMode.SERVER) {
            return SpaceRaceScreen.SERVER_BOTTOM_CONTROL_RESERVED_SERVER_TAB;
        }
        return SpaceRaceScreen.SERVER_BOTTOM_CONTROL_RESERVED;
    }

    static void handleBack(SpaceRaceScreen screen) {
        if (screen.serverStatsMode == ServerStatsMode.PLAYER && screen.selectedServerPlayerId != null) {
            screen.selectedServerPlayerId = null;
            screen.serverPlayerDetailScroll = 0;
            screen.serverScrollbar = null;
            return;
        }
        if (screen.serverStatsMode == ServerStatsMode.TEAM && screen.selectedServerTeamId != null) {
            screen.selectedServerTeamId = null;
            screen.serverTeamDetailScroll = 0;
            screen.serverScrollbar = null;
            return;
        }
        screen.setMenu(SpaceRaceMenu.MAIN);
    }

    static int getActiveListScroll(SpaceRaceScreen screen) {
        if (screen.serverStatsMode == ServerStatsMode.PLAYER) {
            return screen.selectedServerPlayerId == null ? screen.serverPlayerScroll : screen.serverPlayerDetailScroll;
        }
        if (screen.serverStatsMode == ServerStatsMode.TEAM) {
            return screen.selectedServerTeamId == null ? screen.serverTeamScroll : screen.serverTeamDetailScroll;
        }
        return screen.serverTreeScrollY;
    }

    static void setActiveListScroll(SpaceRaceScreen screen, int value) {
        if (screen.serverStatsMode == ServerStatsMode.PLAYER) {
            if (screen.selectedServerPlayerId == null) {
                screen.serverPlayerScroll = value;
            } else {
                screen.serverPlayerDetailScroll = value;
            }
            return;
        }
        if (screen.serverStatsMode == ServerStatsMode.TEAM) {
            if (screen.selectedServerTeamId == null) {
                screen.serverTeamScroll = value;
            } else {
                screen.serverTeamDetailScroll = value;
            }
            return;
        }
        screen.serverTreeScrollY = value;
    }

    static void renderPanel(SpaceRaceScreen screen, GuiGraphics graphics, int mouseX, int mouseY) {
        int panelX = screen.getLeft() + 10;
        int panelY = screen.getTop() + 24;
        int panelWidth = screen.backgroundWidth - 20;
        int panelHeight = screen.backgroundHeight - 30 - getBottomControlsReserved(screen);

        graphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0x66000000);
        graphics.renderOutline(panelX, panelY, panelWidth, panelHeight, 0xAA2D2D2D);

        screen.playerClickAreas.clear();
        screen.teamClickAreas.clear();
        screen.serverScrollbar = null;
        screen.serverTreeVerticalScrollbar = null;
        screen.serverTreeHorizontalScrollbar = null;

        int contentX = panelX + SpaceRaceScreen.STATS_PANEL_PADDING;
        int contentY = panelY + SpaceRaceScreen.STATS_PANEL_PADDING;
        int contentWidth = panelWidth - SpaceRaceScreen.STATS_PANEL_PADDING * 2;
        Component header = switch (screen.serverStatsMode) {
            case PLAYER -> screen.selectedServerPlayerId == null ? Component.translatable(Translations.SpaceRace.HEADER_PLAYER) : Component.translatable(Translations.SpaceRace.HEADER_PLAYER_PROGRESS);
            case TEAM -> screen.selectedServerTeamId == null ? Component.translatable(Translations.SpaceRace.HEADER_TEAM) : Component.translatable(Translations.SpaceRace.HEADER_TEAM_MEMBERS);
            case SERVER -> Component.translatable(Translations.SpaceRace.HEADER_SERVER);
        };
        graphics.drawString(screen.fontRenderer(), header, contentX, contentY, 0xFFFFFFFF, false);

        int headerSpacing = screen.serverStatsMode == ServerStatsMode.SERVER ? 5 : SpaceRaceScreen.STATS_PANEL_PADDING;
        int bodyTop = contentY + screen.fontRenderer().lineHeight + headerSpacing;
        int bodyHeight = Math.max(0, panelHeight - (bodyTop - panelY) - SpaceRaceScreen.STATS_PANEL_PADDING);
        if (bodyHeight <= 0) {
            return;
        }

        switch (screen.serverStatsMode) {
            case PLAYER -> {
                if (screen.selectedServerPlayerId == null) {
                    renderPlayerOverviewPanel(screen, graphics, mouseX, mouseY, contentX, bodyTop, contentWidth, bodyHeight);
                } else {
                    renderPlayerDetailPanel(screen, graphics, mouseX, mouseY, contentX, bodyTop, contentWidth, bodyHeight);
                }
            }
            case TEAM -> {
                if (screen.selectedServerTeamId == null) {
                    renderTeamOverviewPanel(screen, graphics, mouseX, mouseY, contentX, bodyTop, contentWidth, bodyHeight);
                } else {
                    renderTeamDetailPanel(screen, graphics, mouseX, mouseY, contentX, bodyTop, contentWidth, bodyHeight);
                }
            }
            case SERVER -> renderServerTreePanel(screen, graphics, mouseX, mouseY, contentX, bodyTop, contentWidth, bodyHeight);
        }
    }

    static boolean handleClick(SpaceRaceScreen screen, double mouseX, double mouseY) {
        if (screen.serverStatsMode == ServerStatsMode.PLAYER && screen.selectedServerPlayerId == null) {
            for (PlayerClickArea clickArea : screen.playerClickAreas) {
                if (!clickArea.contains(mouseX, mouseY)) {
                    continue;
                }
                screen.selectedServerPlayerId = clickArea.playerId();
                screen.serverPlayerDetailScroll = 0;
                screen.activeScrollbar = ScrollbarType.NONE;
                return true;
            }
            return false;
        }

        if (screen.serverStatsMode == ServerStatsMode.TEAM && screen.selectedServerTeamId == null) {
            for (TeamClickArea clickArea : screen.teamClickAreas) {
                if (!clickArea.contains(mouseX, mouseY)) {
                    continue;
                }
                screen.selectedServerTeamId = clickArea.teamId();
                screen.serverTeamDetailScroll = 0;
                screen.activeScrollbar = ScrollbarType.NONE;
                return true;
            }
        }
        return false;
    }

    private static void renderPlayerOverviewPanel(SpaceRaceScreen screen, GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        List<SpaceRaceClientStats.PlayerStatsEntry> entries = SpaceRaceClientStats.getServerStatsEntries();
        if (entries.isEmpty()) {
            graphics.drawCenteredString(screen.fontRenderer(), Component.translatable(Translations.SpaceRace.EMPTY_PLAYER_DATA), x + width / 2, y + height / 2 - screen.fontRenderer().lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        int listWidth = Math.max(80, width - SpaceRaceScreen.SCROLLBAR_WIDTH - SpaceRaceScreen.SCROLLBAR_SPACING);
        int scrollbarX = x + listWidth + SpaceRaceScreen.SCROLLBAR_SPACING;
        int visibleRows = Math.max(0, (height + SpaceRaceScreen.OVERVIEW_ENTRY_SPACING) / (SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT + SpaceRaceScreen.OVERVIEW_ENTRY_SPACING));
        int rowsTrackHeight = Math.max(0, visibleRows * (SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT + SpaceRaceScreen.OVERVIEW_ENTRY_SPACING) - SpaceRaceScreen.OVERVIEW_ENTRY_SPACING);
        screen.serverPlayerScroll = Mth.clamp(screen.serverPlayerScroll, 0, Math.max(0, entries.size() - visibleRows));

        int rowsToRender = Math.min(visibleRows, entries.size());
        Component tooltip = null;
        for (int rowIndex = 0; rowIndex < rowsToRender; rowIndex++) {
            int sourceRowIndex = screen.serverPlayerScroll + rowIndex;
            if (sourceRowIndex >= entries.size()) {
                break;
            }

            SpaceRaceClientStats.PlayerStatsEntry entry = entries.get(sourceRowIndex);
            int rowY = y + rowIndex * (SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT + SpaceRaceScreen.OVERVIEW_ENTRY_SPACING);
            Component hovered = renderPlayerOverviewRow(screen, graphics, mouseX, mouseY, x, rowY, listWidth, entry);
            screen.playerClickAreas.add(new PlayerClickArea(x, rowY, x + listWidth, rowY + SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT, entry.playerId()));
            if (hovered != null) {
                tooltip = hovered;
            }
        }

        screen.serverScrollbar = new ScrollbarInfo(
                ScrollbarType.SERVER_PLAYER_LIST,
                ScrollbarAxis.VERTICAL,
                scrollbarX,
                y,
                rowsTrackHeight,
                entries.size(),
                Math.max(0, visibleRows),
                screen.serverPlayerScroll
        );
        SpaceRaceScreenRenderers.renderScrollbar(screen, graphics, screen.serverScrollbar, mouseX, mouseY);

        if (rowsToRender == 0) {
            graphics.drawCenteredString(screen.fontRenderer(), Component.translatable(Translations.SpaceRace.EMPTY_PLAYER_DATA), x + listWidth / 2, y + height / 2 - screen.fontRenderer().lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        if (tooltip != null) {
            graphics.renderTooltip(screen.fontRenderer(), tooltip, mouseX, mouseY);
        }
    }

    private static Component renderPlayerOverviewRow(SpaceRaceScreen screen, GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, SpaceRaceClientStats.PlayerStatsEntry statsEntry) {
        graphics.fill(x, y, x + width, y + SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT, 0x441A1A1A);
        graphics.renderOutline(x, y, width, SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT, 0x553C3C3C);

        int headX = x + SpaceRaceScreen.STATS_ENTRY_CONTENT_PADDING;
        int headY = y + (SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT - SpaceRaceScreen.PLAYER_HEAD_SIZE) / 2;
        SpaceRaceScreenRenderers.renderPlayerHead(screen, graphics, statsEntry.playerId(), headX, headY, SpaceRaceScreen.PLAYER_HEAD_SIZE);

        List<SpaceRaceClientStats.AdvancementIconData> visibleIcons = getFilteredAdvancements(screen, statsEntry.advancementIcons());
        int textX = headX + SpaceRaceScreen.PLAYER_HEAD_SIZE + SpaceRaceScreen.PLAYER_HEAD_SPACING;
        int rightEdge = x + width - SpaceRaceScreen.STATS_ENTRY_CONTENT_PADDING;
        int iconCount = getRenderableIconCount(textX, rightEdge, visibleIcons.size());
        int iconWidth = iconCount == 0 ? 0 : iconCount * SpaceRaceScreen.ADVANCEMENT_ICON_SIZE + (iconCount - 1) * SpaceRaceScreen.ADVANCEMENT_ICON_SPACING;
        int textWidth = rightEdge - textX - (iconWidth > 0 ? SpaceRaceScreen.ADVANCEMENT_TEXT_ICON_SPACING + iconWidth : 0);
        if (textWidth <= 0) {
            return null;
        }

        Component nameComponent = Component.literal(statsEntry.playerName());
        if (screen.minecraftClient() != null && screen.minecraftClient().player != null && screen.minecraftClient().player.connection != null) {
            PlayerInfo playerInfo = screen.minecraftClient().player.connection.getPlayerInfo(statsEntry.playerId());
            if (playerInfo != null && playerInfo.getTabListDisplayName() != null) {
                nameComponent = playerInfo.getTabListDisplayName();
            }
        }

        List<FormattedCharSequence> nameLines = screen.fontRenderer().split(nameComponent, textWidth);
        if (!nameLines.isEmpty()) {
            int textY = y + (SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT - screen.fontRenderer().lineHeight) / 2;
            graphics.drawString(screen.fontRenderer(), nameLines.get(0), textX, textY, 0xFFFFFFFF, false);
        }

        if (iconCount == 0) {
            return null;
        }

        int iconStartX = rightEdge - iconWidth;
        int iconY = y + (SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT - SpaceRaceScreen.ADVANCEMENT_ICON_SIZE) / 2;
        Component tooltip = null;
        for (int i = 0; i < iconCount; i++) {
            SpaceRaceClientStats.AdvancementIconData advancementIcon = visibleIcons.get(i);
            ItemStack icon = advancementIcon.icon();
            if (icon.isEmpty()) {
                continue;
            }
            int iconX = iconStartX + i * (SpaceRaceScreen.ADVANCEMENT_ICON_SIZE + SpaceRaceScreen.ADVANCEMENT_ICON_SPACING);
            graphics.renderItem(icon, iconX, iconY);
            if (mouseX >= iconX && mouseX < iconX + SpaceRaceScreen.ADVANCEMENT_ICON_SIZE && mouseY >= iconY && mouseY < iconY + SpaceRaceScreen.ADVANCEMENT_ICON_SIZE) {
                tooltip = Component.literal(advancementIcon.title()).withStyle(style -> style.withColor(advancementIcon.color()));
            }
        }
        return tooltip;
    }

    private static void renderPlayerDetailPanel(SpaceRaceScreen screen, GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        SpaceRaceClientStats.PlayerStatsEntry entry = SpaceRaceClientStats.getServerStatsEntry(screen.selectedServerPlayerId);
        if (entry == null) {
            graphics.drawCenteredString(screen.fontRenderer(), Component.translatable(Translations.SpaceRace.EMPTY_SELECTED_PLAYER), x + width / 2, y + height / 2 - screen.fontRenderer().lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        int headerHeight = SpaceRaceScreen.STATS_ENTRY_HEIGHT;
        int listTop = y + headerHeight + SpaceRaceScreen.STATS_ENTRY_SPACING;
        int listHeight = Math.max(0, height - headerHeight - SpaceRaceScreen.STATS_ENTRY_SPACING);
        int listWidth = Math.max(80, width - SpaceRaceScreen.SCROLLBAR_WIDTH - SpaceRaceScreen.SCROLLBAR_SPACING);
        int scrollbarX = x + listWidth + SpaceRaceScreen.SCROLLBAR_SPACING;

        graphics.fill(x, y, x + listWidth, y + headerHeight, 0x55202020);
        graphics.renderOutline(x, y, listWidth, headerHeight, 0x773D3D3D);
        int headX = x + SpaceRaceScreen.STATS_ENTRY_CONTENT_PADDING;
        int headY = y + (headerHeight - SpaceRaceScreen.PLAYER_HEAD_SIZE) / 2;
        SpaceRaceScreenRenderers.renderPlayerHead(screen, graphics, entry.playerId(), headX, headY, SpaceRaceScreen.PLAYER_HEAD_SIZE);
        graphics.drawString(screen.fontRenderer(), Component.literal(entry.playerName()), headX + SpaceRaceScreen.PLAYER_HEAD_SIZE + SpaceRaceScreen.PLAYER_HEAD_SPACING, y + (headerHeight - screen.fontRenderer().lineHeight) / 2, 0xFFFFFFFF, false);

        List<SpaceRaceClientStats.AdvancementIconData> advancements = getFilteredAdvancements(screen, entry.advancementIcons());
        if (advancements.isEmpty()) {
            graphics.drawCenteredString(screen.fontRenderer(), Component.translatable(Translations.SpaceRace.EMPTY_MATCHING_ADVANCEMENTS), x + listWidth / 2, listTop + listHeight / 2 - screen.fontRenderer().lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        int visibleRows = Math.max(0, (listHeight + SpaceRaceScreen.ADVANCEMENT_ROW_SPACING) / (SpaceRaceScreen.ADVANCEMENT_ROW_HEIGHT + SpaceRaceScreen.ADVANCEMENT_ROW_SPACING));
        int rowsTrackHeight = Math.max(0, visibleRows * (SpaceRaceScreen.ADVANCEMENT_ROW_HEIGHT + SpaceRaceScreen.ADVANCEMENT_ROW_SPACING) - SpaceRaceScreen.ADVANCEMENT_ROW_SPACING);
        screen.serverPlayerDetailScroll = Mth.clamp(screen.serverPlayerDetailScroll, 0, Math.max(0, advancements.size() - visibleRows));

        for (int rowIndex = 0; rowIndex < visibleRows; rowIndex++) {
            int sourceRowIndex = screen.serverPlayerDetailScroll + rowIndex;
            if (sourceRowIndex >= advancements.size()) {
                break;
            }

            SpaceRaceClientStats.AdvancementIconData advancement = advancements.get(sourceRowIndex);
            int rowY = listTop + rowIndex * (SpaceRaceScreen.ADVANCEMENT_ROW_HEIGHT + SpaceRaceScreen.ADVANCEMENT_ROW_SPACING);
            graphics.fill(x, rowY, x + listWidth, rowY + SpaceRaceScreen.ADVANCEMENT_ROW_HEIGHT, 0x441A1A1A);
            graphics.renderOutline(x, rowY, listWidth, SpaceRaceScreen.ADVANCEMENT_ROW_HEIGHT, 0x553C3C3C);

            int iconY = rowY + (SpaceRaceScreen.ADVANCEMENT_ROW_HEIGHT - SpaceRaceScreen.ADVANCEMENT_ICON_SIZE) / 2;
            int iconX = x + SpaceRaceScreen.STATS_ENTRY_CONTENT_PADDING;
            graphics.renderItem(advancement.icon(), iconX, iconY);

            int textX = iconX + SpaceRaceScreen.ADVANCEMENT_ICON_SIZE + SpaceRaceScreen.ADVANCEMENT_TEXT_ICON_SPACING;
            int textWidth = Math.max(10, listWidth - (textX - x) - SpaceRaceScreen.STATS_ENTRY_CONTENT_PADDING);
            String title = screen.fontRenderer().plainSubstrByWidth(advancement.title(), textWidth);
            graphics.drawString(screen.fontRenderer(), Component.literal(title), textX, rowY + (SpaceRaceScreen.ADVANCEMENT_ROW_HEIGHT - screen.fontRenderer().lineHeight) / 2, advancement.color(), false);
        }

        screen.serverScrollbar = new ScrollbarInfo(
                ScrollbarType.SERVER_PLAYER_DETAIL,
                ScrollbarAxis.VERTICAL,
                scrollbarX,
                listTop,
                rowsTrackHeight,
                advancements.size(),
                Math.max(0, visibleRows),
                screen.serverPlayerDetailScroll
        );
        SpaceRaceScreenRenderers.renderScrollbar(screen, graphics, screen.serverScrollbar, mouseX, mouseY);
    }

    private static void renderTeamOverviewPanel(SpaceRaceScreen screen, GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        List<SpaceRaceClientStats.TeamStatsEntry> entries = SpaceRaceClientStats.getTeamStatsEntries();
        if (entries.isEmpty()) {
            graphics.drawCenteredString(screen.fontRenderer(), Component.translatable(Translations.SpaceRace.EMPTY_TEAM_DATA), x + width / 2, y + height / 2 - screen.fontRenderer().lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        int listWidth = Math.max(80, width - SpaceRaceScreen.SCROLLBAR_WIDTH - SpaceRaceScreen.SCROLLBAR_SPACING);
        int scrollbarX = x + listWidth + SpaceRaceScreen.SCROLLBAR_SPACING;
        int visibleRows = Math.max(0, (height + SpaceRaceScreen.OVERVIEW_ENTRY_SPACING) / (SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT + SpaceRaceScreen.OVERVIEW_ENTRY_SPACING));
        int rowsTrackHeight = Math.max(0, visibleRows * (SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT + SpaceRaceScreen.OVERVIEW_ENTRY_SPACING) - SpaceRaceScreen.OVERVIEW_ENTRY_SPACING);
        screen.serverTeamScroll = Mth.clamp(screen.serverTeamScroll, 0, Math.max(0, entries.size() - visibleRows));

        int rowsToRender = Math.min(visibleRows, entries.size());
        List<Component> tooltip = null;
        for (int rowIndex = 0; rowIndex < rowsToRender; rowIndex++) {
            int sourceRowIndex = screen.serverTeamScroll + rowIndex;
            if (sourceRowIndex >= entries.size()) {
                break;
            }

            SpaceRaceClientStats.TeamStatsEntry entry = entries.get(sourceRowIndex);
            int rowY = y + rowIndex * (SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT + SpaceRaceScreen.OVERVIEW_ENTRY_SPACING);
            List<Component> hovered = renderTeamOverviewRow(screen, graphics, mouseX, mouseY, x, rowY, listWidth, entry);
            screen.teamClickAreas.add(new TeamClickArea(x, rowY, x + listWidth, rowY + SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT, entry.teamId()));
            if (hovered != null) {
                tooltip = hovered;
            }
        }

        screen.serverScrollbar = new ScrollbarInfo(
                ScrollbarType.SERVER_TEAM_LIST,
                ScrollbarAxis.VERTICAL,
                scrollbarX,
                y,
                rowsTrackHeight,
                entries.size(),
                Math.max(0, visibleRows),
                screen.serverTeamScroll
        );
        SpaceRaceScreenRenderers.renderScrollbar(screen, graphics, screen.serverScrollbar, mouseX, mouseY);

        if (rowsToRender == 0) {
            graphics.drawCenteredString(screen.fontRenderer(), Component.translatable(Translations.SpaceRace.EMPTY_TEAM_DATA), x + listWidth / 2, y + height / 2 - screen.fontRenderer().lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        if (tooltip != null) {
            graphics.renderComponentTooltip(screen.fontRenderer(), tooltip, mouseX, mouseY);
        }
    }

    private static List<Component> renderTeamOverviewRow(SpaceRaceScreen screen, GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, SpaceRaceClientStats.TeamStatsEntry statsEntry) {
        graphics.fill(x, y, x + width, y + SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT, 0x441A1A1A);
        graphics.renderOutline(x, y, width, SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT, 0x553C3C3C);

        int iconX = x + SpaceRaceScreen.STATS_ENTRY_CONTENT_PADDING;
        int iconY = y + (SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT - SpaceRaceScreen.ADVANCEMENT_ICON_SIZE) / 2;
        graphics.renderItem(statsEntry.teamFlag(), iconX, iconY);

        List<SpaceRaceClientStats.AdvancementIconData> visibleIcons = getFilteredAdvancements(screen, statsEntry.advancementIcons());
        int textX = iconX + SpaceRaceScreen.ADVANCEMENT_ICON_SIZE + SpaceRaceScreen.PLAYER_HEAD_SPACING;
        int rightEdge = x + width - SpaceRaceScreen.STATS_ENTRY_CONTENT_PADDING;
        int iconCount = getRenderableIconCount(textX, rightEdge, visibleIcons.size());
        int iconWidth = iconCount == 0 ? 0 : iconCount * SpaceRaceScreen.ADVANCEMENT_ICON_SIZE + (iconCount - 1) * SpaceRaceScreen.ADVANCEMENT_ICON_SPACING;
        int textWidth = rightEdge - textX - (iconWidth > 0 ? SpaceRaceScreen.ADVANCEMENT_TEXT_ICON_SPACING + iconWidth : 0);
        if (textWidth <= 0) {
            return null;
        }

        String teamName = screen.fontRenderer().plainSubstrByWidth(statsEntry.teamName(), textWidth);
        graphics.drawString(screen.fontRenderer(), teamName, textX, y + (SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT - screen.fontRenderer().lineHeight) / 2, 0xFFFFFFFF, false);

        if (iconCount == 0) {
            return null;
        }

        int iconStartX = rightEdge - iconWidth;
        int rowIconY = y + (SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT - SpaceRaceScreen.ADVANCEMENT_ICON_SIZE) / 2;
        List<Component> tooltip = null;
        for (int i = 0; i < iconCount; i++) {
            SpaceRaceClientStats.AdvancementIconData advancementIcon = visibleIcons.get(i);
            ItemStack icon = advancementIcon.icon();
            if (icon.isEmpty()) {
                continue;
            }
            int entryIconX = iconStartX + i * (SpaceRaceScreen.ADVANCEMENT_ICON_SIZE + SpaceRaceScreen.ADVANCEMENT_ICON_SPACING);
            graphics.renderItem(icon, entryIconX, rowIconY);
            if (mouseX >= entryIconX && mouseX < entryIconX + SpaceRaceScreen.ADVANCEMENT_ICON_SIZE && mouseY >= rowIconY && mouseY < rowIconY + SpaceRaceScreen.ADVANCEMENT_ICON_SIZE) {
                tooltip = buildAdvancementTooltip(advancementIcon, true);
            }
        }
        return tooltip;
    }

    private static void renderTeamDetailPanel(SpaceRaceScreen screen, GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        SpaceRaceClientStats.TeamStatsEntry entry = SpaceRaceClientStats.getTeamStatsEntry(screen.selectedServerTeamId);
        if (entry == null) {
            graphics.drawCenteredString(screen.fontRenderer(), Component.translatable(Translations.SpaceRace.EMPTY_SELECTED_TEAM), x + width / 2, y + height / 2 - screen.fontRenderer().lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        int headerHeight = SpaceRaceScreen.STATS_ENTRY_HEIGHT;
        int listTop = y + headerHeight + SpaceRaceScreen.STATS_ENTRY_SPACING;
        int listHeight = Math.max(0, height - headerHeight - SpaceRaceScreen.STATS_ENTRY_SPACING);
        int listWidth = Math.max(80, width - SpaceRaceScreen.SCROLLBAR_WIDTH - SpaceRaceScreen.SCROLLBAR_SPACING);
        int scrollbarX = x + listWidth + SpaceRaceScreen.SCROLLBAR_SPACING;

        graphics.fill(x, y, x + listWidth, y + headerHeight, 0x55202020);
        graphics.renderOutline(x, y, listWidth, headerHeight, 0x773D3D3D);
        int flagX = x + SpaceRaceScreen.STATS_ENTRY_CONTENT_PADDING;
        int flagY = y + (headerHeight - SpaceRaceScreen.ADVANCEMENT_ICON_SIZE) / 2;
        graphics.renderItem(entry.teamFlag(), flagX, flagY);
        graphics.drawString(screen.fontRenderer(), Component.literal(entry.teamName()), flagX + SpaceRaceScreen.ADVANCEMENT_ICON_SIZE + SpaceRaceScreen.PLAYER_HEAD_SPACING, y + (headerHeight - screen.fontRenderer().lineHeight) / 2, 0xFFFFFFFF, false);

        List<SpaceRaceClientStats.TeamMemberEntry> members = entry.members();
        if (members.isEmpty()) {
            graphics.drawCenteredString(screen.fontRenderer(), Component.translatable(Translations.SpaceRace.EMPTY_TEAM_MEMBERS), x + listWidth / 2, listTop + listHeight / 2 - screen.fontRenderer().lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        int visibleRows = Math.max(0, (listHeight + SpaceRaceScreen.OVERVIEW_ENTRY_SPACING) / (SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT + SpaceRaceScreen.OVERVIEW_ENTRY_SPACING));
        int rowsTrackHeight = Math.max(0, visibleRows * (SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT + SpaceRaceScreen.OVERVIEW_ENTRY_SPACING) - SpaceRaceScreen.OVERVIEW_ENTRY_SPACING);
        screen.serverTeamDetailScroll = Mth.clamp(screen.serverTeamDetailScroll, 0, Math.max(0, members.size() - visibleRows));

        Component tooltip = null;
        for (int rowIndex = 0; rowIndex < visibleRows; rowIndex++) {
            int sourceRowIndex = screen.serverTeamDetailScroll + rowIndex;
            if (sourceRowIndex >= members.size()) {
                break;
            }

            SpaceRaceClientStats.TeamMemberEntry member = members.get(sourceRowIndex);
            int rowY = listTop + rowIndex * (SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT + SpaceRaceScreen.OVERVIEW_ENTRY_SPACING);
            Component hovered = renderTeamMemberRow(screen, graphics, mouseX, mouseY, x, rowY, listWidth, member);
            if (hovered != null) {
                tooltip = hovered;
            }
        }

        screen.serverScrollbar = new ScrollbarInfo(
                ScrollbarType.SERVER_TEAM_DETAIL,
                ScrollbarAxis.VERTICAL,
                scrollbarX,
                listTop,
                rowsTrackHeight,
                members.size(),
                Math.max(0, visibleRows),
                screen.serverTeamDetailScroll
        );
        SpaceRaceScreenRenderers.renderScrollbar(screen, graphics, screen.serverScrollbar, mouseX, mouseY);

        if (tooltip != null) {
            graphics.renderTooltip(screen.fontRenderer(), tooltip, mouseX, mouseY);
        }
    }

    private static Component renderTeamMemberRow(SpaceRaceScreen screen, GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, SpaceRaceClientStats.TeamMemberEntry member) {
        graphics.fill(x, y, x + width, y + SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT, 0x441A1A1A);
        graphics.renderOutline(x, y, width, SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT, 0x553C3C3C);

        int headX = x + SpaceRaceScreen.STATS_ENTRY_CONTENT_PADDING;
        int headY = y + (SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT - SpaceRaceScreen.PLAYER_HEAD_SIZE) / 2;
        SpaceRaceScreenRenderers.renderPlayerHead(screen, graphics, member.playerId(), headX, headY, SpaceRaceScreen.PLAYER_HEAD_SIZE);

        List<SpaceRaceClientStats.AdvancementIconData> visibleIcons = getFilteredAdvancements(screen, member.advancementIcons());
        int textX = headX + SpaceRaceScreen.PLAYER_HEAD_SIZE + SpaceRaceScreen.PLAYER_HEAD_SPACING;
        int rightEdge = x + width - SpaceRaceScreen.STATS_ENTRY_CONTENT_PADDING;
        int iconCount = getRenderableIconCount(textX, rightEdge, visibleIcons.size());
        int iconWidth = iconCount == 0 ? 0 : iconCount * SpaceRaceScreen.ADVANCEMENT_ICON_SIZE + (iconCount - 1) * SpaceRaceScreen.ADVANCEMENT_ICON_SPACING;
        int textWidth = rightEdge - textX - (iconWidth > 0 ? SpaceRaceScreen.ADVANCEMENT_TEXT_ICON_SPACING + iconWidth : 0);
        if (textWidth <= 0) {
            return null;
        }

        String playerName = screen.fontRenderer().plainSubstrByWidth(member.playerName(), textWidth);
        graphics.drawString(screen.fontRenderer(), playerName, textX, y + (SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT - screen.fontRenderer().lineHeight) / 2, 0xFFFFFFFF, false);

        if (iconCount == 0) {
            return null;
        }

        int iconStartX = rightEdge - iconWidth;
        int iconY = y + (SpaceRaceScreen.OVERVIEW_ENTRY_HEIGHT - SpaceRaceScreen.ADVANCEMENT_ICON_SIZE) / 2;
        Component tooltip = null;
        for (int i = 0; i < iconCount; i++) {
            SpaceRaceClientStats.AdvancementIconData advancementIcon = visibleIcons.get(i);
            ItemStack icon = advancementIcon.icon();
            if (icon.isEmpty()) {
                continue;
            }
            int iconX = iconStartX + i * (SpaceRaceScreen.ADVANCEMENT_ICON_SIZE + SpaceRaceScreen.ADVANCEMENT_ICON_SPACING);
            graphics.renderItem(icon, iconX, iconY);
            if (mouseX >= iconX && mouseX < iconX + SpaceRaceScreen.ADVANCEMENT_ICON_SIZE && mouseY >= iconY && mouseY < iconY + SpaceRaceScreen.ADVANCEMENT_ICON_SIZE) {
                tooltip = Component.literal(advancementIcon.title()).withStyle(style -> style.withColor(advancementIcon.color()));
            }
        }
        return tooltip;
    }

    private static void renderServerTreePanel(SpaceRaceScreen screen, GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        List<SpaceRaceClientStats.ServerAdvancementNode> nodes = SpaceRaceClientStats.getServerAdvancementNodes();
        if (nodes.isEmpty()) {
            graphics.drawCenteredString(screen.fontRenderer(), Component.translatable(Translations.SpaceRace.EMPTY_SERVER_TREE), x + width / 2, y + height / 2 - screen.fontRenderer().lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        int canvasWidth = Math.max(1, width - SpaceRaceScreen.SCROLLBAR_WIDTH - SpaceRaceScreen.SCROLLBAR_SPACING);
        int canvasHeight = Math.max(1, height - SpaceRaceScreen.SCROLLBAR_WIDTH - SpaceRaceScreen.SCROLLBAR_SPACING);
        int verticalScrollbarX = x + canvasWidth + SpaceRaceScreen.SCROLLBAR_SPACING;
        int horizontalScrollbarY = y + canvasHeight + SpaceRaceScreen.SCROLLBAR_SPACING;

        graphics.fill(x, y, x + canvasWidth, y + canvasHeight, 0x33101010);
        graphics.renderOutline(x, y, canvasWidth, canvasHeight, 0x553F3F3F);

        Map<ResourceLocation, TreeNodeRenderInfo> nodePositions = new HashMap<>();
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (SpaceRaceClientStats.ServerAdvancementNode node : nodes) {
            int nodeX = Math.round(node.x() * SpaceRaceScreen.TREE_NODE_SPACING_X);
            int nodeY = Math.round(node.y() * SpaceRaceScreen.TREE_NODE_SPACING_Y);
            nodePositions.put(node.advancementId(), new TreeNodeRenderInfo(node, nodeX, nodeY));
            minX = Math.min(minX, nodeX);
            minY = Math.min(minY, nodeY);
            maxX = Math.max(maxX, nodeX);
            maxY = Math.max(maxY, nodeY);
        }

        if (nodePositions.isEmpty()) {
            return;
        }

        int contentWidth = Math.max(SpaceRaceScreen.TREE_NODE_SIZE + SpaceRaceScreen.TREE_CANVAS_PADDING * 2, (maxX - minX) + SpaceRaceScreen.TREE_NODE_SIZE + SpaceRaceScreen.TREE_CANVAS_PADDING * 2);
        int contentHeight = Math.max(SpaceRaceScreen.TREE_NODE_SIZE + SpaceRaceScreen.TREE_CANVAS_PADDING * 2, (maxY - minY) + SpaceRaceScreen.TREE_NODE_SIZE + SpaceRaceScreen.TREE_CANVAS_PADDING * 2);
        int maxScrollX = Math.max(0, contentWidth - canvasWidth);
        int maxScrollY = Math.max(0, contentHeight - canvasHeight);
        if (screen.centerServerTreeVerticalOnNextRender) {
            screen.serverTreeScrollY = maxScrollY / 2;
            screen.centerServerTreeVerticalOnNextRender = false;
        }
        screen.serverTreeScrollX = Mth.clamp(screen.serverTreeScrollX, 0, maxScrollX);
        screen.serverTreeScrollY = Mth.clamp(screen.serverTreeScrollY, 0, maxScrollY);

        int originX = x + SpaceRaceScreen.TREE_CANVAS_PADDING - screen.serverTreeScrollX - minX;
        int originY = y + SpaceRaceScreen.TREE_CANVAS_PADDING - screen.serverTreeScrollY - minY;

        graphics.enableScissor(x, y, x + canvasWidth, y + canvasHeight);
        for (TreeNodeRenderInfo info : nodePositions.values()) {
            ResourceLocation parentId = info.node().parentId();
            if (parentId == null) {
                continue;
            }
            TreeNodeRenderInfo parentInfo = nodePositions.get(parentId);
            if (parentInfo == null) {
                continue;
            }

            int parentCenterX = originX + parentInfo.nodeX() + SpaceRaceScreen.TREE_NODE_SIZE / 2;
            int parentCenterY = originY + parentInfo.nodeY() + SpaceRaceScreen.TREE_NODE_SIZE / 2;
            int childCenterX = originX + info.nodeX() + SpaceRaceScreen.TREE_NODE_SIZE / 2;
            int childCenterY = originY + info.nodeY() + SpaceRaceScreen.TREE_NODE_SIZE / 2;

            drawClampedHorizontalLine(graphics, parentCenterX, childCenterX, parentCenterY, x, y, canvasWidth, canvasHeight, SpaceRaceScreen.TREE_LINE_COLOR);
            drawClampedVerticalLine(graphics, childCenterX, parentCenterY, childCenterY, x, y, canvasWidth, canvasHeight, SpaceRaceScreen.TREE_LINE_COLOR);
        }

        List<Component> tooltip = null;
        for (TreeNodeRenderInfo info : nodePositions.values()) {
            SpaceRaceClientStats.ServerAdvancementNode node = info.node();
            int nodeX = originX + info.nodeX();
            int nodeY = originY + info.nodeY();
            if (nodeX + SpaceRaceScreen.TREE_NODE_SIZE < x || nodeX > x + canvasWidth || nodeY + SpaceRaceScreen.TREE_NODE_SIZE < y || nodeY > y + canvasHeight) {
                continue;
            }

            int fillColor = node.complete() ? SpaceRaceScreen.TREE_NODE_COMPLETE_FILL : SpaceRaceScreen.TREE_NODE_INCOMPLETE_FILL;
            int outlineColor = node.complete() ? SpaceRaceScreen.TREE_NODE_BORDER_COMPLETE : SpaceRaceScreen.TREE_NODE_BORDER_INCOMPLETE;
            graphics.fill(nodeX, nodeY, nodeX + SpaceRaceScreen.TREE_NODE_SIZE, nodeY + SpaceRaceScreen.TREE_NODE_SIZE, fillColor);
            graphics.renderOutline(nodeX, nodeY, SpaceRaceScreen.TREE_NODE_SIZE, SpaceRaceScreen.TREE_NODE_SIZE, outlineColor);
            graphics.renderItem(node.icon(), nodeX + 2, nodeY + 2);

            if (mouseX >= nodeX && mouseX < nodeX + SpaceRaceScreen.TREE_NODE_SIZE && mouseY >= nodeY && mouseY < nodeY + SpaceRaceScreen.TREE_NODE_SIZE) {
                Component status = Component.translatable(node.complete() ? Translations.SpaceRace.STATUS_COMPLETE : Translations.SpaceRace.STATUS_INCOMPLETE)
                        .withStyle(style -> style.withColor(node.complete() ? 0x8DDB6B : 0xC97B7B));
                List<Component> nodeTooltip = new ArrayList<>();
                nodeTooltip.add(Component.literal(node.title()).withStyle(style -> style.withColor(node.color())).append(Component.literal(" - ")).append(status));
                if (node.complete()) {
                    if (node.firstUnlockers().isEmpty()) {
                        nodeTooltip.add(Component.translatable(Translations.SpaceRace.FIRST_UNLOCKERS_UNAVAILABLE).withStyle(ChatFormatting.DARK_GRAY));
                    } else {
                        nodeTooltip.add(Component.translatable(Translations.SpaceRace.FIRST_UNLOCKED_BY).withStyle(ChatFormatting.GRAY));
                        for (String firstUnlocker : node.firstUnlockers()) {
                            nodeTooltip.add(Component.literal("- " + firstUnlocker));
                        }
                    }
                }
                tooltip = nodeTooltip;
            }
        }
        graphics.disableScissor();

        screen.serverTreeVerticalScrollbar = new ScrollbarInfo(
                ScrollbarType.SERVER_TREE_VERTICAL,
                ScrollbarAxis.VERTICAL,
                verticalScrollbarX,
                y,
                canvasHeight,
                contentHeight,
                canvasHeight,
                screen.serverTreeScrollY
        );
        screen.serverTreeHorizontalScrollbar = new ScrollbarInfo(
                ScrollbarType.SERVER_TREE_HORIZONTAL,
                ScrollbarAxis.HORIZONTAL,
                x,
                horizontalScrollbarY,
                canvasWidth,
                contentWidth,
                canvasWidth,
                screen.serverTreeScrollX
        );
        SpaceRaceScreenRenderers.renderScrollbar(screen, graphics, screen.serverTreeVerticalScrollbar, mouseX, mouseY);
        SpaceRaceScreenRenderers.renderScrollbar(screen, graphics, screen.serverTreeHorizontalScrollbar, mouseX, mouseY);

        if (tooltip != null) {
            graphics.renderComponentTooltip(screen.fontRenderer(), tooltip, mouseX, mouseY);
        }
    }

    private static void drawClampedHorizontalLine(GuiGraphics graphics, int startX, int endX, int y, int clipX, int clipY, int clipWidth, int clipHeight, int color) {
        if (y < clipY || y >= clipY + clipHeight) {
            return;
        }
        int min = Math.max(Math.min(startX, endX), clipX);
        int max = Math.min(Math.max(startX, endX), clipX + clipWidth - 1);
        if (max < min) {
            return;
        }
        graphics.fill(min, y, max + 1, y + 1, color);
    }

    private static void drawClampedVerticalLine(GuiGraphics graphics, int x, int startY, int endY, int clipX, int clipY, int clipWidth, int clipHeight, int color) {
        if (x < clipX || x >= clipX + clipWidth) {
            return;
        }
        int min = Math.max(Math.min(startY, endY), clipY);
        int max = Math.min(Math.max(startY, endY), clipY + clipHeight - 1);
        if (max < min) {
            return;
        }
        graphics.fill(x, min, x + 1, max + 1, color);
    }

    private static List<SpaceRaceClientStats.AdvancementIconData> getFilteredAdvancements(SpaceRaceScreen screen, List<SpaceRaceClientStats.AdvancementIconData> icons) {
        if (!screen.importantProgressionOnly) {
            return icons;
        }
        List<SpaceRaceClientStats.AdvancementIconData> filtered = new ArrayList<>(icons.size());
        for (SpaceRaceClientStats.AdvancementIconData icon : icons) {
            if (icon.challenge()) {
                filtered.add(icon);
            }
        }
        return filtered;
    }

    private static int getRenderableIconCount(int textStartX, int rightEdgeX, int totalIcons) {
        if (totalIcons <= 0) {
            return 0;
        }

        int minimumNameWidth = 48;
        int availableWidth = rightEdgeX - textStartX - minimumNameWidth - SpaceRaceScreen.ADVANCEMENT_TEXT_ICON_SPACING;
        if (availableWidth < SpaceRaceScreen.ADVANCEMENT_ICON_SIZE) {
            return 0;
        }

        int maxIconsByWidth = 1 + (availableWidth - SpaceRaceScreen.ADVANCEMENT_ICON_SIZE) / (SpaceRaceScreen.ADVANCEMENT_ICON_SIZE + SpaceRaceScreen.ADVANCEMENT_ICON_SPACING);
        return Math.max(0, Math.min(totalIcons, maxIconsByWidth));
    }

    private static List<Component> buildAdvancementTooltip(SpaceRaceClientStats.AdvancementIconData advancementIcon, boolean includeUnlockers) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.literal(advancementIcon.title()).withStyle(style -> style.withColor(advancementIcon.color())));
        if (!includeUnlockers || advancementIcon.firstUnlockers().isEmpty()) {
            return tooltip;
        }

        tooltip.add(Component.translatable(Translations.SpaceRace.FIRST_UNLOCKED_BY).withStyle(ChatFormatting.GRAY));
        for (String firstUnlocker : advancementIcon.firstUnlockers()) {
            tooltip.add(Component.literal("- " + firstUnlocker));
        }
        return tooltip;
    }

    private static int getCompactButtonWidth(SpaceRaceScreen screen, Component... labels) {
        int width = 0;
        for (Component label : labels) {
            width = Math.max(width, screen.fontRenderer().width(label) + 10);
        }
        return Math.max(64, width);
    }
}
