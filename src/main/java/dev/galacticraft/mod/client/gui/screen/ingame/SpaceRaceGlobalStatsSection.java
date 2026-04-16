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
import dev.galacticraft.mod.client.gui.screen.ingame.spacerace.GlobalItemsColumn;
import dev.galacticraft.mod.client.gui.screen.ingame.spacerace.GlobalStatsSection;
import dev.galacticraft.mod.client.gui.screen.ingame.spacerace.ScrollbarAxis;
import dev.galacticraft.mod.client.gui.screen.ingame.spacerace.ScrollbarInfo;
import dev.galacticraft.mod.client.gui.screen.ingame.spacerace.ScrollbarType;
import dev.galacticraft.mod.client.gui.widget.SpaceRaceButton;
import dev.galacticraft.mod.client.spacerace.SpaceRaceClientStats;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.List;

final class SpaceRaceGlobalStatsSection {
    private SpaceRaceGlobalStatsSection() {
    }

    static void initializeMenu(SpaceRaceScreen screen) {
        SpaceRaceScreenMenus.clearSectionButtons(screen);
        SpaceRaceScreenMenus.addBackButton(screen);
        screen.globalGeneralScroll = 0;
        screen.globalItemsScroll = 0;
        screen.globalMobsScroll = 0;
        screen.globalStatsSection = GlobalStatsSection.GENERAL;

        ClientPlayNetworking.send(RequestSpaceRaceStatsPayload.INSTANCE);
        screen.globalVisibilityButton = screen.addScreenWidget(new SpaceRaceButton(
                getVisibilityButtonText(),
                screen.getRight() - SpaceRaceScreen.STATS_TOGGLE_BUTTON_WIDTH - 10,
                screen.getTop() + 5,
                SpaceRaceScreen.STATS_TOGGLE_BUTTON_WIDTH,
                SpaceRaceScreen.STATS_TOGGLE_BUTTON_HEIGHT,
                button -> toggleVisibility()
        ));

        int panelX = screen.getLeft() + 10;
        int panelWidth = screen.backgroundWidth - 20;
        int contentX = panelX + SpaceRaceScreen.STATS_PANEL_PADDING;
        int contentWidth = panelWidth - SpaceRaceScreen.STATS_PANEL_PADDING * 2;
        int tabWidth = Math.max(60, (contentWidth - SpaceRaceScreen.GLOBAL_TAB_SPACING * 2) / 3);
        int tabY = screen.getBottom() - SpaceRaceScreen.STATS_PANEL_PADDING - SpaceRaceScreen.GLOBAL_TAB_HEIGHT - SpaceRaceScreen.GLOBAL_TAB_BOTTOM_PADDING;
        screen.globalGeneralTabButton = screen.addButton(Component.translatable(Translations.SpaceRace.GLOBAL_TAB_GENERAL), contentX, tabY, tabWidth, SpaceRaceScreen.GLOBAL_TAB_HEIGHT, button -> setStatsSection(screen, GlobalStatsSection.GENERAL));
        screen.globalItemsTabButton = screen.addButton(Component.translatable(Translations.SpaceRace.GLOBAL_TAB_ITEMS), contentX + tabWidth + SpaceRaceScreen.GLOBAL_TAB_SPACING, tabY, tabWidth, SpaceRaceScreen.GLOBAL_TAB_HEIGHT, button -> setStatsSection(screen, GlobalStatsSection.ITEMS));
        screen.globalMobsTabButton = screen.addButton(Component.translatable(Translations.SpaceRace.GLOBAL_TAB_MOBS), contentX + (tabWidth + SpaceRaceScreen.GLOBAL_TAB_SPACING) * 2, tabY, tabWidth, SpaceRaceScreen.GLOBAL_TAB_HEIGHT, button -> setStatsSection(screen, GlobalStatsSection.MOBS));
        updateTabButtonState(screen);

        screen.addScreenRenderable((graphics, mouseX, mouseY, delta) -> renderPanel(screen, graphics, mouseX, mouseY));
    }

    static void toggleVisibility() {
        boolean hideServerAdvancements = SpaceRaceClientStats.isHideServerAdvancements();
        boolean hideGlobalLeaderboard = !SpaceRaceClientStats.isHideGlobalLeaderboard();
        SpaceRaceClientStats.setVisibility(hideServerAdvancements, hideGlobalLeaderboard);
        ClientPlayNetworking.send(new UpdateSpaceRaceVisibilityPayload(hideServerAdvancements, hideGlobalLeaderboard));
    }

    static Component getVisibilityButtonText() {
        if (SpaceRaceClientStats.isHideGlobalLeaderboard()) {
            return Component.translatable(Translations.SpaceRace.SHOW_ME_ON_LEADERBOARD);
        }
        return Component.translatable(Translations.SpaceRace.HIDE_ME_FROM_LEADERBOARD);
    }

    static void setStatsSection(SpaceRaceScreen screen, GlobalStatsSection section) {
        screen.globalStatsSection = section;
        updateTabButtonState(screen);
    }

    static void updateTabButtonState(SpaceRaceScreen screen) {
        if (screen.globalGeneralTabButton != null) {
            screen.globalGeneralTabButton.active = screen.globalStatsSection != GlobalStatsSection.GENERAL;
        }
        if (screen.globalItemsTabButton != null) {
            screen.globalItemsTabButton.active = screen.globalStatsSection != GlobalStatsSection.ITEMS;
        }
        if (screen.globalMobsTabButton != null) {
            screen.globalMobsTabButton.active = screen.globalStatsSection != GlobalStatsSection.MOBS;
        }
    }

    static void renderPanel(SpaceRaceScreen screen, GuiGraphics graphics, int mouseX, int mouseY) {
        int panelX = screen.getLeft() + 10;
        int panelY = screen.getTop() + 26;
        int panelWidth = screen.backgroundWidth - 20;
        int panelHeight = screen.backgroundHeight - 36;

        graphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0x66000000);
        graphics.renderOutline(panelX, panelY, panelWidth, panelHeight, 0xAA2D2D2D);

        int contentX = panelX + SpaceRaceScreen.STATS_PANEL_PADDING;
        int contentY = panelY + SpaceRaceScreen.STATS_PANEL_PADDING;
        int contentWidth = panelWidth - SpaceRaceScreen.STATS_PANEL_PADDING * 2;

        graphics.drawString(screen.fontRenderer(), Component.translatable(Translations.SpaceRace.GLOBAL_STATS), contentX, contentY, 0xFFFFFFFF, false);
        int bodyTop = contentY + screen.fontRenderer().lineHeight + SpaceRaceScreen.GLOBAL_CONTENT_SPACING;
        int tabY = screen.getBottom() - SpaceRaceScreen.STATS_PANEL_PADDING - SpaceRaceScreen.GLOBAL_TAB_HEIGHT - SpaceRaceScreen.GLOBAL_TAB_BOTTOM_PADDING;
        int bodyBottom = tabY - SpaceRaceScreen.GLOBAL_CONTENT_SPACING;
        int bodyHeight = bodyBottom - bodyTop;
        if (bodyHeight <= 0) {
            return;
        }

        switch (screen.globalStatsSection) {
            case GENERAL -> renderGeneralPanel(screen, graphics, mouseX, mouseY, contentX, bodyTop, contentWidth, bodyHeight);
            case ITEMS -> renderItemsPanel(screen, graphics, mouseX, mouseY, contentX, bodyTop, contentWidth, bodyHeight);
            case MOBS -> renderMobsPanel(screen, graphics, mouseX, mouseY, contentX, bodyTop, contentWidth, bodyHeight);
        }
    }

    private static void renderGeneralPanel(SpaceRaceScreen screen, GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        List<SpaceRaceClientStats.GeneralStatRow> rows = SpaceRaceClientStats.getGeneralStatRows();
        if (rows.isEmpty()) {
            screen.globalGeneralScrollbar = null;
            graphics.drawCenteredString(screen.fontRenderer(), Component.translatable(Translations.SpaceRace.GLOBAL_EMPTY_GENERAL), x + width / 2, y + height / 2 - screen.fontRenderer().lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        int listWidth = Math.max(80, width - SpaceRaceScreen.SCROLLBAR_WIDTH - SpaceRaceScreen.SCROLLBAR_SPACING);
        int scrollbarX = x + listWidth + SpaceRaceScreen.SCROLLBAR_SPACING;
        int scaledLineHeight = SpaceRaceScreenRenderers.getScaledLineHeight(screen, SpaceRaceScreen.GLOBAL_TEXT_SCALE);
        int visibleRows = Math.max(1, height / SpaceRaceScreen.GLOBAL_GENERAL_ROW_HEIGHT);
        int rowAreaHeight = visibleRows * SpaceRaceScreen.GLOBAL_GENERAL_ROW_HEIGHT;
        screen.globalGeneralScroll = Mth.clamp(screen.globalGeneralScroll, 0, Math.max(0, rows.size() - visibleRows));

        int totalRightEdge = x + listWidth - SpaceRaceScreen.GLOBAL_HEAD_SIZE - 8;
        int totalLeftEdge = totalRightEdge - SpaceRaceScreen.GLOBAL_GENERAL_TOTAL_WIDTH;
        int labelWidth = Math.max(40, totalLeftEdge - x - 6);
        Component tooltip = null;

        for (int rowIndex = 0; rowIndex < visibleRows; rowIndex++) {
            int sourceRowIndex = screen.globalGeneralScroll + rowIndex;
            if (sourceRowIndex >= rows.size()) {
                break;
            }
            SpaceRaceClientStats.GeneralStatRow row = rows.get(sourceRowIndex);
            int rowY = y + rowIndex * SpaceRaceScreen.GLOBAL_GENERAL_ROW_HEIGHT;

            graphics.fill(x, rowY, x + listWidth, rowY + SpaceRaceScreen.GLOBAL_GENERAL_ROW_HEIGHT, 0x441A1A1A);
            graphics.renderOutline(x, rowY, listWidth, SpaceRaceScreen.GLOBAL_GENERAL_ROW_HEIGHT, 0x553C3C3C);

            int textY = rowY + (SpaceRaceScreen.GLOBAL_GENERAL_ROW_HEIGHT - scaledLineHeight) / 2;
            String label = SpaceRaceScreenRenderers.truncateForScaledText(screen, row.label(), labelWidth, SpaceRaceScreen.GLOBAL_TEXT_SCALE);
            SpaceRaceScreenRenderers.drawScaledString(screen, graphics, label, x + 4, textY, 0xFFFFFFFF, SpaceRaceScreen.GLOBAL_TEXT_SCALE);

            String totalText = Integer.toString(row.total());
            int totalX = totalRightEdge - SpaceRaceScreenRenderers.scaledTextWidth(screen, totalText, SpaceRaceScreen.GLOBAL_TEXT_SCALE);
            SpaceRaceScreenRenderers.drawScaledString(screen, graphics, totalText, totalX, textY, 0xFFFFFFFF, SpaceRaceScreen.GLOBAL_TEXT_SCALE);

            int headX = x + listWidth - SpaceRaceScreen.GLOBAL_HEAD_SIZE - 4;
            int headY = rowY + (SpaceRaceScreen.GLOBAL_GENERAL_ROW_HEIGHT - SpaceRaceScreen.GLOBAL_HEAD_SIZE) / 2;
            SpaceRaceClientStats.LeaderCell leader = row.leader();
            if (leader != null) {
                SpaceRaceScreenRenderers.renderPlayerHead(screen, graphics, leader.playerId(), headX, headY, SpaceRaceScreen.GLOBAL_HEAD_SIZE);
                if (mouseX >= headX && mouseX < headX + SpaceRaceScreen.GLOBAL_HEAD_SIZE && mouseY >= headY && mouseY < headY + SpaceRaceScreen.GLOBAL_HEAD_SIZE) {
                    tooltip = Component.translatable(Translations.SpaceRace.LEADER_VALUE, leader.playerName(), leader.value());
                }
            } else {
                SpaceRaceScreenRenderers.drawScaledCenteredString(screen, graphics, "-", headX + SpaceRaceScreen.GLOBAL_HEAD_SIZE / 2, textY, 0xFF9E9E9E, SpaceRaceScreen.GLOBAL_TEXT_SCALE);
            }
        }

        screen.globalGeneralScrollbar = new ScrollbarInfo(
                ScrollbarType.GLOBAL_GENERAL,
                ScrollbarAxis.VERTICAL,
                scrollbarX,
                y,
                rowAreaHeight,
                rows.size(),
                visibleRows,
                screen.globalGeneralScroll
        );
        SpaceRaceScreenRenderers.renderScrollbar(screen, graphics, screen.globalGeneralScrollbar, mouseX, mouseY);

        if (tooltip != null) {
            graphics.renderTooltip(screen.fontRenderer(), tooltip, mouseX, mouseY);
        }
    }

    private static void renderItemsPanel(SpaceRaceScreen screen, GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        List<SpaceRaceClientStats.ItemStatRow> rows = SpaceRaceClientStats.getItemStatRows();
        if (rows.isEmpty()) {
            screen.globalItemsScrollbar = null;
            graphics.drawCenteredString(screen.fontRenderer(), Component.translatable(Translations.SpaceRace.GLOBAL_EMPTY_ITEMS), x + width / 2, y + height / 2 - screen.fontRenderer().lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        GlobalItemsColumn[] columns = GlobalItemsColumn.values();
        int listWidth = Math.max(80, width - SpaceRaceScreen.SCROLLBAR_WIDTH - SpaceRaceScreen.SCROLLBAR_SPACING);
        int scrollbarX = x + listWidth + SpaceRaceScreen.SCROLLBAR_SPACING;
        int maxTableWidth = Math.max(1, listWidth - 2);
        int availableCellWidth = Math.max(1, maxTableWidth - SpaceRaceScreen.GLOBAL_ITEMS_ICON_COLUMN_WIDTH);
        int cellWidth = Mth.clamp(availableCellWidth / columns.length, SpaceRaceScreen.GLOBAL_ITEMS_MIN_CELL_WIDTH, SpaceRaceScreen.GLOBAL_ITEMS_MAX_CELL_WIDTH);

        int tableWidth = SpaceRaceScreen.GLOBAL_ITEMS_ICON_COLUMN_WIDTH + cellWidth * columns.length;
        if (tableWidth > maxTableWidth) {
            cellWidth = Math.max(36, availableCellWidth / columns.length);
            tableWidth = SpaceRaceScreen.GLOBAL_ITEMS_ICON_COLUMN_WIDTH + cellWidth * columns.length;
        }

        int tableX = x + Math.max(0, (listWidth - tableWidth) / 2);
        int headerY = y;
        int scaledLineHeight = SpaceRaceScreenRenderers.getScaledLineHeight(screen, SpaceRaceScreen.GLOBAL_TEXT_SCALE);
        float iconScale = SpaceRaceScreen.GLOBAL_ITEM_ICON_SIZE / 16.0F;

        Component tooltip = null;
        for (int i = 0; i < columns.length; i++) {
            int cellX = tableX + SpaceRaceScreen.GLOBAL_ITEMS_ICON_COLUMN_WIDTH + i * cellWidth;
            graphics.fill(cellX, headerY, cellX + cellWidth, headerY + SpaceRaceScreen.GLOBAL_ITEMS_HEADER_HEIGHT, 0x55202020);
            graphics.renderOutline(cellX, headerY, cellWidth, SpaceRaceScreen.GLOBAL_ITEMS_HEADER_HEIGHT, 0x773D3D3D);

            int iconX = cellX + (cellWidth - SpaceRaceScreen.GLOBAL_ITEM_ICON_SIZE) / 2;
            int iconY = headerY + (SpaceRaceScreen.GLOBAL_ITEMS_HEADER_HEIGHT - SpaceRaceScreen.GLOBAL_ITEM_ICON_SIZE) / 2;
            SpaceRaceScreenRenderers.renderScaledItem(graphics, columns[i].icon(), iconX, iconY, iconScale);
            if (mouseX >= iconX && mouseX < iconX + SpaceRaceScreen.GLOBAL_ITEM_ICON_SIZE && mouseY >= iconY && mouseY < iconY + SpaceRaceScreen.GLOBAL_ITEM_ICON_SIZE) {
                tooltip = columns[i].tooltip();
            }
        }

        int rowsTop = headerY + SpaceRaceScreen.GLOBAL_ITEMS_HEADER_HEIGHT;
        int rowsHeight = Math.max(0, height - SpaceRaceScreen.GLOBAL_ITEMS_HEADER_HEIGHT);
        int visibleRows = Math.max(1, rowsHeight / SpaceRaceScreen.GLOBAL_ITEMS_ROW_HEIGHT);
        int rowAreaHeight = visibleRows * SpaceRaceScreen.GLOBAL_ITEMS_ROW_HEIGHT;
        screen.globalItemsScroll = Mth.clamp(screen.globalItemsScroll, 0, Math.max(0, rows.size() - visibleRows));

        for (int rowIndex = 0; rowIndex < visibleRows; rowIndex++) {
            int sourceRowIndex = screen.globalItemsScroll + rowIndex;
            if (sourceRowIndex >= rows.size()) {
                break;
            }
            SpaceRaceClientStats.ItemStatRow row = rows.get(sourceRowIndex);
            int rowY = rowsTop + rowIndex * SpaceRaceScreen.GLOBAL_ITEMS_ROW_HEIGHT;

            graphics.fill(tableX, rowY, tableX + tableWidth, rowY + SpaceRaceScreen.GLOBAL_ITEMS_ROW_HEIGHT, 0x441A1A1A);
            graphics.renderOutline(tableX, rowY, tableWidth, SpaceRaceScreen.GLOBAL_ITEMS_ROW_HEIGHT, 0x553C3C3C);

            int rowIconX = tableX + (SpaceRaceScreen.GLOBAL_ITEMS_ICON_COLUMN_WIDTH - SpaceRaceScreen.GLOBAL_ITEM_ICON_SIZE) / 2;
            int rowIconY = rowY + (SpaceRaceScreen.GLOBAL_ITEMS_ROW_HEIGHT - SpaceRaceScreen.GLOBAL_ITEM_ICON_SIZE) / 2;
            SpaceRaceScreenRenderers.renderScaledItem(graphics, row.icon(), rowIconX, rowIconY, iconScale);

            for (int i = 0; i < columns.length; i++) {
                int cellX = tableX + SpaceRaceScreen.GLOBAL_ITEMS_ICON_COLUMN_WIDTH + i * cellWidth;
                graphics.fill(cellX, rowY, cellX + cellWidth, rowY + SpaceRaceScreen.GLOBAL_ITEMS_ROW_HEIGHT, 0x33262626);
                graphics.renderOutline(cellX, rowY, cellWidth, SpaceRaceScreen.GLOBAL_ITEMS_ROW_HEIGHT, 0x553C3C3C);

                if (i >= row.cells().size()) {
                    int emptyY = rowY + (SpaceRaceScreen.GLOBAL_ITEMS_ROW_HEIGHT - scaledLineHeight) / 2;
                    SpaceRaceScreenRenderers.drawScaledCenteredString(screen, graphics, "-", cellX + cellWidth / 2, emptyY, 0xFF9E9E9E, SpaceRaceScreen.GLOBAL_TEXT_SCALE);
                    continue;
                }

                SpaceRaceClientStats.ItemStatCell cell = row.cells().get(i);
                if (cell.total() <= 0) {
                    int emptyY = rowY + (SpaceRaceScreen.GLOBAL_ITEMS_ROW_HEIGHT - scaledLineHeight) / 2;
                    SpaceRaceScreenRenderers.drawScaledCenteredString(screen, graphics, "-", cellX + cellWidth / 2, emptyY, 0xFF9E9E9E, SpaceRaceScreen.GLOBAL_TEXT_SCALE);
                    continue;
                }

                SpaceRaceClientStats.LeaderCell leader = cell.leader();
                if (leader != null) {
                    int headX = cellX + (cellWidth - SpaceRaceScreen.GLOBAL_HEAD_SIZE) / 2;
                    int headY = rowY + 2;
                    SpaceRaceScreenRenderers.renderPlayerHead(screen, graphics, leader.playerId(), headX, headY, SpaceRaceScreen.GLOBAL_HEAD_SIZE);
                    if (mouseX >= headX && mouseX < headX + SpaceRaceScreen.GLOBAL_HEAD_SIZE && mouseY >= headY && mouseY < headY + SpaceRaceScreen.GLOBAL_HEAD_SIZE) {
                        tooltip = Component.translatable(Translations.SpaceRace.LEADER_VALUE, leader.playerName(), leader.value());
                    }
                } else {
                    SpaceRaceScreenRenderers.drawScaledCenteredString(screen, graphics, "-", cellX + cellWidth / 2, rowY + 2, 0xFF9E9E9E, SpaceRaceScreen.GLOBAL_TEXT_SCALE);
                }

                int totalY = rowY + SpaceRaceScreen.GLOBAL_HEAD_SIZE + 3;
                SpaceRaceScreenRenderers.drawScaledCenteredString(screen, graphics, Integer.toString(cell.total()), cellX + cellWidth / 2, totalY, 0xFFFFFFFF, SpaceRaceScreen.GLOBAL_TEXT_SCALE);
            }
        }

        if (tooltip != null) {
            graphics.renderTooltip(screen.fontRenderer(), tooltip, mouseX, mouseY);
        }

        screen.globalItemsScrollbar = new ScrollbarInfo(
                ScrollbarType.GLOBAL_ITEMS,
                ScrollbarAxis.VERTICAL,
                scrollbarX,
                headerY,
                SpaceRaceScreen.GLOBAL_ITEMS_HEADER_HEIGHT + rowAreaHeight,
                rows.size(),
                visibleRows,
                screen.globalItemsScroll
        );
        SpaceRaceScreenRenderers.renderScrollbar(screen, graphics, screen.globalItemsScrollbar, mouseX, mouseY);
    }

    private static void renderMobsPanel(SpaceRaceScreen screen, GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        List<SpaceRaceClientStats.MobStatRow> rows = SpaceRaceClientStats.getMobStatRows();
        if (rows.isEmpty()) {
            screen.globalMobsScrollbar = null;
            graphics.drawCenteredString(screen.fontRenderer(), Component.translatable(Translations.SpaceRace.GLOBAL_EMPTY_MOBS), x + width / 2, y + height / 2 - screen.fontRenderer().lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        int listWidth = Math.max(120, width - SpaceRaceScreen.SCROLLBAR_WIDTH - SpaceRaceScreen.SCROLLBAR_SPACING);
        int scrollbarX = x + listWidth + SpaceRaceScreen.SCROLLBAR_SPACING;
        int totalWidth = 62;
        int leaderWidth = Mth.clamp(listWidth / 3, 110, 190);
        int nameWidth = Math.max(70, listWidth - totalWidth - leaderWidth);
        int usedWidth = nameWidth + totalWidth + leaderWidth;
        if (usedWidth > listWidth) {
            int overflow = usedWidth - listWidth;
            nameWidth = Math.max(40, nameWidth - overflow);
        }
        int dividerOneX = x + nameWidth;
        int dividerTwoX = dividerOneX + totalWidth;
        int killedCenterX = dividerOneX + totalWidth / 2;
        int leaderLeftX = dividerTwoX + 1;
        int leaderRightX = x + listWidth;
        int scaledLineHeight = SpaceRaceScreenRenderers.getScaledLineHeight(screen, SpaceRaceScreen.GLOBAL_TEXT_SCALE);

        graphics.fill(x, y, x + listWidth, y + SpaceRaceScreen.GLOBAL_MOBS_ROW_HEIGHT, 0x55202020);
        graphics.renderOutline(x, y, listWidth, SpaceRaceScreen.GLOBAL_MOBS_ROW_HEIGHT, 0x773D3D3D);
        graphics.fill(dividerOneX, y, dividerOneX + 1, y + SpaceRaceScreen.GLOBAL_MOBS_ROW_HEIGHT, 0x773D3D3D);
        graphics.fill(dividerTwoX, y, dividerTwoX + 1, y + SpaceRaceScreen.GLOBAL_MOBS_ROW_HEIGHT, 0x773D3D3D);
        int headerTextY = y + (SpaceRaceScreen.GLOBAL_MOBS_ROW_HEIGHT - scaledLineHeight) / 2;
        SpaceRaceScreenRenderers.drawScaledString(screen, graphics, Component.translatable(Translations.SpaceRace.GLOBAL_MOBS_COLUMN_MOB).getString(), x + 4, headerTextY, 0xFFFFFFFF, SpaceRaceScreen.GLOBAL_TEXT_SCALE);
        SpaceRaceScreenRenderers.drawScaledCenteredString(screen, graphics, Component.translatable(Translations.SpaceRace.GLOBAL_MOBS_COLUMN_KILLED).getString(), killedCenterX, headerTextY, 0xFFFFFFFF, SpaceRaceScreen.GLOBAL_TEXT_SCALE);
        SpaceRaceScreenRenderers.drawScaledCenteredString(screen, graphics, Component.translatable(Translations.SpaceRace.GLOBAL_MOBS_COLUMN_KILL_LEADER).getString(), leaderLeftX + leaderWidth / 2, headerTextY, 0xFFFFFFFF, SpaceRaceScreen.GLOBAL_TEXT_SCALE);

        int rowsTop = y + SpaceRaceScreen.GLOBAL_MOBS_ROW_HEIGHT;
        int rowsHeight = Math.max(0, height - SpaceRaceScreen.GLOBAL_MOBS_ROW_HEIGHT);
        int visibleRows = Math.max(1, rowsHeight / SpaceRaceScreen.GLOBAL_MOBS_ROW_HEIGHT);
        int rowAreaHeight = visibleRows * SpaceRaceScreen.GLOBAL_MOBS_ROW_HEIGHT;
        screen.globalMobsScroll = Mth.clamp(screen.globalMobsScroll, 0, Math.max(0, rows.size() - visibleRows));

        Component tooltip = null;
        for (int rowIndex = 0; rowIndex < visibleRows; rowIndex++) {
            int sourceRowIndex = screen.globalMobsScroll + rowIndex;
            if (sourceRowIndex >= rows.size()) {
                break;
            }
            SpaceRaceClientStats.MobStatRow row = rows.get(sourceRowIndex);
            int rowY = rowsTop + rowIndex * SpaceRaceScreen.GLOBAL_MOBS_ROW_HEIGHT;

            graphics.fill(x, rowY, x + listWidth, rowY + SpaceRaceScreen.GLOBAL_MOBS_ROW_HEIGHT, 0x441A1A1A);
            graphics.renderOutline(x, rowY, listWidth, SpaceRaceScreen.GLOBAL_MOBS_ROW_HEIGHT, 0x553C3C3C);
            graphics.fill(dividerOneX, rowY, dividerOneX + 1, rowY + SpaceRaceScreen.GLOBAL_MOBS_ROW_HEIGHT, 0x553C3C3C);
            graphics.fill(dividerTwoX, rowY, dividerTwoX + 1, rowY + SpaceRaceScreen.GLOBAL_MOBS_ROW_HEIGHT, 0x553C3C3C);

            int textY = rowY + (SpaceRaceScreen.GLOBAL_MOBS_ROW_HEIGHT - scaledLineHeight) / 2;
            String mobLabel = SpaceRaceScreenRenderers.truncateForScaledText(screen, row.mobName(), nameWidth - 6, SpaceRaceScreen.GLOBAL_TEXT_SCALE);
            SpaceRaceScreenRenderers.drawScaledString(screen, graphics, mobLabel, x + 4, textY, 0xFFFFFFFF, SpaceRaceScreen.GLOBAL_TEXT_SCALE);
            SpaceRaceScreenRenderers.drawScaledCenteredString(screen, graphics, Integer.toString(row.totalKilled()), killedCenterX, textY, 0xFFFFFFFF, SpaceRaceScreen.GLOBAL_TEXT_SCALE);

            SpaceRaceClientStats.LeaderCell leader = row.leader();
            int headX = leaderRightX - SpaceRaceScreen.GLOBAL_HEAD_SIZE - 4;
            int headY = rowY + (SpaceRaceScreen.GLOBAL_MOBS_ROW_HEIGHT - SpaceRaceScreen.GLOBAL_HEAD_SIZE) / 2;
            int leaderNameWidth = Math.max(12, headX - leaderLeftX - 4);
            if (leader != null) {
                String leaderName = SpaceRaceScreenRenderers.truncateForScaledText(screen, leader.playerName(), leaderNameWidth, SpaceRaceScreen.GLOBAL_TEXT_SCALE);
                SpaceRaceScreenRenderers.drawScaledString(screen, graphics, leaderName, leaderLeftX + 2, textY, 0xFFFFFFFF, SpaceRaceScreen.GLOBAL_TEXT_SCALE);
                SpaceRaceScreenRenderers.renderPlayerHead(screen, graphics, leader.playerId(), headX, headY, SpaceRaceScreen.GLOBAL_HEAD_SIZE);
                if (mouseX >= headX && mouseX < headX + SpaceRaceScreen.GLOBAL_HEAD_SIZE && mouseY >= headY && mouseY < headY + SpaceRaceScreen.GLOBAL_HEAD_SIZE) {
                    tooltip = Component.translatable(Translations.SpaceRace.LEADER_VALUE, leader.playerName(), leader.value());
                }
            } else {
                SpaceRaceScreenRenderers.drawScaledCenteredString(screen, graphics, "-", leaderLeftX + leaderWidth / 2, textY, 0xFF9E9E9E, SpaceRaceScreen.GLOBAL_TEXT_SCALE);
            }
        }

        screen.globalMobsScrollbar = new ScrollbarInfo(
                ScrollbarType.GLOBAL_MOBS,
                ScrollbarAxis.VERTICAL,
                scrollbarX,
                y,
                SpaceRaceScreen.GLOBAL_MOBS_ROW_HEIGHT + rowAreaHeight,
                rows.size(),
                visibleRows,
                screen.globalMobsScroll
        );
        SpaceRaceScreenRenderers.renderScrollbar(screen, graphics, screen.globalMobsScrollbar, mouseX, mouseY);

        if (tooltip != null) {
            graphics.renderTooltip(screen.fontRenderer(), tooltip, mouseX, mouseY);
        }
    }
}
