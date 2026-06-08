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
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class ServerStatisticsScreen extends AbstractSpaceRaceScreen {
    private final Screen parent;
    private GlobalStatsSection statsSection = GlobalStatsSection.GENERAL;
    private SpaceRaceButton visibilityButton;
    private Button generalTabButton;
    private Button itemsTabButton;
    private Button mobsTabButton;
    private int generalScroll;
    private int itemsScroll;
    private int mobsScroll;
    private ScrollbarInfo generalScrollbar;
    private ScrollbarInfo itemsScrollbar;
    private ScrollbarInfo mobsScrollbar;
    private List<Component> mouseoverTooltip;

    public ServerStatisticsScreen(Screen parent) {
        super(Component.translatable(Translations.SpaceRace.GLOBAL_STATS));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(Component.translatable(Translations.SpaceRace.BACK), this.getLeft() + 5, this.getTop() + 5, 40, 14, button -> this.onClose());
        ClientPlayNetworking.send(RequestSpaceRaceStatsPayload.INSTANCE);
        this.visibilityButton = this.addRenderableWidget(new SpaceRaceButton(this.getVisibilityButtonText(), this.getRight() - STATS_TOGGLE_BUTTON_WIDTH - 10, this.getTop() + 5, STATS_TOGGLE_BUTTON_WIDTH, STATS_TOGGLE_BUTTON_HEIGHT, button -> {
            this.toggleVisibility();
            button.setMessage(this.getVisibilityButtonText());
        }));

        int panelX = this.getLeft() + 10;
        int panelWidth = this.backgroundWidth - 20;
        int contentX = panelX + STATS_PANEL_PADDING;
        int contentWidth = panelWidth - STATS_PANEL_PADDING * 2;
        int tabWidth = Math.max(60, (contentWidth - GLOBAL_TAB_SPACING * 2) / 3);
        int tabY = this.getBottom() - STATS_PANEL_PADDING - GLOBAL_TAB_HEIGHT - GLOBAL_TAB_BOTTOM_PADDING;
        this.generalTabButton = this.addButton(Component.translatable(Translations.SpaceRace.GLOBAL_TAB_GENERAL), contentX, tabY, tabWidth, GLOBAL_TAB_HEIGHT, button -> this.setStatsSection(GlobalStatsSection.GENERAL));
        this.itemsTabButton = this.addButton(Component.translatable(Translations.SpaceRace.GLOBAL_TAB_ITEMS), contentX + tabWidth + GLOBAL_TAB_SPACING, tabY, tabWidth, GLOBAL_TAB_HEIGHT, button -> this.setStatsSection(GlobalStatsSection.ITEMS));
        this.mobsTabButton = this.addButton(Component.translatable(Translations.SpaceRace.GLOBAL_TAB_MOBS), contentX + (tabWidth + GLOBAL_TAB_SPACING) * 2, tabY, tabWidth, GLOBAL_TAB_HEIGHT, button -> this.setStatsSection(GlobalStatsSection.MOBS));
        this.updateTabButtonState();
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }

    @Override
    protected void clearTransientRenderState() {
        this.generalScrollbar = null;
        this.itemsScrollbar = null;
        this.mobsScrollbar = null;
        this.mouseoverTooltip = null;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderBackground(graphics, mouseX, mouseY, delta);
        if (this.animationCompleted) {
            this.renderPanel(graphics, mouseX, mouseY);
        }
    }

    @Override
    protected void drawMouseoverTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (this.mouseoverTooltip != null) {
            graphics.renderComponentTooltip(this.font, this.mouseoverTooltip, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!this.animationCompleted) {
            return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        }

        double amount = verticalAmount;
        if (amount == 0.0D) {
            amount = horizontalAmount;
        }
        if (amount == 0.0D) {
            return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        }

        int delta = (int) Math.signum(amount);
        switch (this.statsSection) {
            case GENERAL -> this.generalScroll = Math.max(0, this.generalScroll - delta);
            case ITEMS -> this.itemsScroll = Math.max(0, this.itemsScroll - delta);
            case MOBS -> this.mobsScroll = Math.max(0, this.mobsScroll - delta);
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.animationCompleted && this.handleScrollbarClick(mouseX, mouseY)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && this.activeScrollbar != ScrollbarType.NONE) {
            ScrollbarInfo scrollbar = this.getScrollbarForType(this.activeScrollbar);
            if (scrollbar == null) {
                return true;
            }

            int thumbLength = scrollbar.thumbLength();
            int trackRange = Math.max(0, scrollbar.length() - thumbLength);
            int mouseAxis = scrollbar.axis() == ScrollbarAxis.VERTICAL ? (int) mouseY : (int) mouseX;
            int trackStart = scrollbar.trackStart();
            int targetThumbPosition = Mth.clamp(mouseAxis - this.activeScrollbarThumbOffset, trackStart, trackStart + trackRange);
            this.setScrollFromThumbPosition(scrollbar, targetThumbPosition);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.activeScrollbar = ScrollbarType.NONE;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void toggleVisibility() {
        boolean hideServerAdvancements = SpaceRaceClientStats.isHideServerAdvancements();
        boolean hideGlobalLeaderboard = !SpaceRaceClientStats.isHideGlobalLeaderboard();
        SpaceRaceClientStats.setVisibility(hideServerAdvancements, hideGlobalLeaderboard);
        ClientPlayNetworking.send(new UpdateSpaceRaceVisibilityPayload(hideServerAdvancements, hideGlobalLeaderboard));
    }

    private Component getVisibilityButtonText() {
        if (SpaceRaceClientStats.isHideGlobalLeaderboard()) {
            return Component.translatable(Translations.SpaceRace.SHOW_ME_ON_LEADERBOARD);
        }
        return Component.translatable(Translations.SpaceRace.HIDE_ME_FROM_LEADERBOARD);
    }

    private void setStatsSection(GlobalStatsSection section) {
        this.statsSection = section;
        this.activeScrollbar = ScrollbarType.NONE;
        this.updateTabButtonState();
    }

    private void updateTabButtonState() {
        if (this.generalTabButton != null) {
            this.generalTabButton.active = this.statsSection != GlobalStatsSection.GENERAL;
        }
        if (this.itemsTabButton != null) {
            this.itemsTabButton.active = this.statsSection != GlobalStatsSection.ITEMS;
        }
        if (this.mobsTabButton != null) {
            this.mobsTabButton.active = this.statsSection != GlobalStatsSection.MOBS;
        }
    }

    private void renderPanel(GuiGraphics graphics, int mouseX, int mouseY) {
        int panelX = this.getLeft() + 10;
        int panelY = this.getTop() + 26;
        int panelWidth = this.backgroundWidth - 20;
        int panelHeight = this.backgroundHeight - 36;

        graphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0x66000000);
        graphics.renderOutline(panelX, panelY, panelWidth, panelHeight, 0xAA2D2D2D);

        int contentX = panelX + STATS_PANEL_PADDING;
        int contentY = panelY + STATS_PANEL_PADDING;
        int contentWidth = panelWidth - STATS_PANEL_PADDING * 2;
        graphics.drawString(this.font, Component.translatable(Translations.SpaceRace.GLOBAL_STATS), contentX, contentY, 0xFFFFFFFF, false);
        int bodyTop = contentY + this.font.lineHeight + GLOBAL_CONTENT_SPACING;
        int tabY = this.getBottom() - STATS_PANEL_PADDING - GLOBAL_TAB_HEIGHT - GLOBAL_TAB_BOTTOM_PADDING;
        int bodyBottom = tabY - GLOBAL_CONTENT_SPACING;
        int bodyHeight = bodyBottom - bodyTop;
        if (bodyHeight <= 0) {
            return;
        }

        switch (this.statsSection) {
            case GENERAL -> this.renderGeneralPanel(graphics, mouseX, mouseY, contentX, bodyTop, contentWidth, bodyHeight);
            case ITEMS -> this.renderItemsPanel(graphics, mouseX, mouseY, contentX, bodyTop, contentWidth, bodyHeight);
            case MOBS -> this.renderMobsPanel(graphics, mouseX, mouseY, contentX, bodyTop, contentWidth, bodyHeight);
        }
    }

    private void renderGeneralPanel(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        List<SpaceRaceClientStats.GeneralStatRow> rows = SpaceRaceClientStats.getGeneralStatRows();
        if (rows.isEmpty()) {
            graphics.drawCenteredString(this.font, Component.translatable(Translations.SpaceRace.GLOBAL_EMPTY_GENERAL), x + width / 2, y + height / 2 - this.font.lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        int listWidth = Math.max(80, width - SCROLLBAR_WIDTH - SCROLLBAR_SPACING);
        int scrollbarX = x + listWidth + SCROLLBAR_SPACING;
        int visibleRows = Math.max(1, height / GLOBAL_GENERAL_ROW_HEIGHT);
        int rowAreaHeight = visibleRows * GLOBAL_GENERAL_ROW_HEIGHT;
        this.generalScroll = Mth.clamp(this.generalScroll, 0, Math.max(0, rows.size() - visibleRows));

        int totalRightEdge = x + listWidth - GLOBAL_HEAD_SIZE - 8;
        int totalLeftEdge = totalRightEdge - GLOBAL_GENERAL_TOTAL_WIDTH;
        int labelWidth = Math.max(40, totalLeftEdge - x - 6);
        List<Component> hoveredTooltip = null;

        for (int rowIndex = 0; rowIndex < visibleRows; rowIndex++) {
            int sourceRowIndex = this.generalScroll + rowIndex;
            if (sourceRowIndex >= rows.size()) {
                break;
            }

            SpaceRaceClientStats.GeneralStatRow row = rows.get(sourceRowIndex);
            int rowY = y + rowIndex * GLOBAL_GENERAL_ROW_HEIGHT;
            graphics.fill(x, rowY, x + listWidth, rowY + GLOBAL_GENERAL_ROW_HEIGHT, 0x441A1A1A);
            graphics.renderOutline(x, rowY, listWidth, GLOBAL_GENERAL_ROW_HEIGHT, 0x553C3C3C);

            int textY = rowY + (GLOBAL_GENERAL_ROW_HEIGHT - this.font.lineHeight) / 2;
            String label = this.font.plainSubstrByWidth(row.label(), labelWidth);
            graphics.drawString(this.font, label, x + 4, textY, 0xFFFFFFFF, false);

            String totalText = Integer.toString(row.total());
            int totalX = totalRightEdge - this.font.width(totalText);
            graphics.drawString(this.font, totalText, totalX, textY, 0xFFFFFFFF, false);

            int headX = x + listWidth - GLOBAL_HEAD_SIZE - 4;
            int headY = rowY + (GLOBAL_GENERAL_ROW_HEIGHT - GLOBAL_HEAD_SIZE) / 2;
            SpaceRaceClientStats.LeaderCell leader = row.leader();
            if (leader != null) {
                this.renderPlayerHead(graphics, leader.playerId(), headX, headY, GLOBAL_HEAD_SIZE);
                if (mouseX >= headX && mouseX < headX + GLOBAL_HEAD_SIZE && mouseY >= headY && mouseY < headY + GLOBAL_HEAD_SIZE) {
                    hoveredTooltip = List.of(Component.translatable(Translations.SpaceRace.LEADER_VALUE, leader.playerName(), leader.value()));
                }
            } else {
                graphics.drawCenteredString(this.font, "-", headX + GLOBAL_HEAD_SIZE / 2, textY, 0xFF9E9E9E);
            }
        }

        this.generalScrollbar = new ScrollbarInfo(ScrollbarType.GLOBAL_GENERAL, ScrollbarAxis.VERTICAL, scrollbarX, y, rowAreaHeight, rows.size(), visibleRows, this.generalScroll);
        this.renderScrollbar(graphics, this.generalScrollbar, mouseX, mouseY);
        this.mouseoverTooltip = hoveredTooltip;
    }

    private void renderItemsPanel(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        List<SpaceRaceClientStats.ItemStatRow> rows = SpaceRaceClientStats.getItemStatRows();
        if (rows.isEmpty()) {
            graphics.drawCenteredString(this.font, Component.translatable(Translations.SpaceRace.GLOBAL_EMPTY_ITEMS), x + width / 2, y + height / 2 - this.font.lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        GlobalItemsColumn[] columns = GlobalItemsColumn.values();
        int listWidth = Math.max(80, width - SCROLLBAR_WIDTH - SCROLLBAR_SPACING);
        int scrollbarX = x + listWidth + SCROLLBAR_SPACING;
        int maxTableWidth = Math.max(1, listWidth - 2);
        int availableCellWidth = Math.max(1, maxTableWidth - GLOBAL_ITEMS_ICON_COLUMN_WIDTH);
        int cellWidth = Mth.clamp(availableCellWidth / columns.length, GLOBAL_ITEMS_MIN_CELL_WIDTH, GLOBAL_ITEMS_MAX_CELL_WIDTH);
        int tableWidth = GLOBAL_ITEMS_ICON_COLUMN_WIDTH + cellWidth * columns.length;
        if (tableWidth > maxTableWidth) {
            cellWidth = Math.max(36, availableCellWidth / columns.length);
            tableWidth = GLOBAL_ITEMS_ICON_COLUMN_WIDTH + cellWidth * columns.length;
        }

        int tableX = x + Math.max(0, (listWidth - tableWidth) / 2);
        int headerY = y;
        List<Component> hoveredTooltip = null;
        for (int i = 0; i < columns.length; i++) {
            int cellX = tableX + GLOBAL_ITEMS_ICON_COLUMN_WIDTH + i * cellWidth;
            graphics.fill(cellX, headerY, cellX + cellWidth, headerY + GLOBAL_ITEMS_HEADER_HEIGHT, 0x55202020);
            graphics.renderOutline(cellX, headerY, cellWidth, GLOBAL_ITEMS_HEADER_HEIGHT, 0x773D3D3D);
            int iconX = cellX + (cellWidth - GLOBAL_ITEM_ICON_SIZE) / 2;
            int iconY = headerY + (GLOBAL_ITEMS_HEADER_HEIGHT - GLOBAL_ITEM_ICON_SIZE) / 2;
            graphics.renderItem(columns[i].icon(), iconX, iconY);
            if (mouseX >= iconX && mouseX < iconX + GLOBAL_ITEM_ICON_SIZE && mouseY >= iconY && mouseY < iconY + GLOBAL_ITEM_ICON_SIZE) {
                hoveredTooltip = List.of(columns[i].tooltip());
            }
        }

        int rowsTop = headerY + GLOBAL_ITEMS_HEADER_HEIGHT;
        int rowsHeight = Math.max(0, height - GLOBAL_ITEMS_HEADER_HEIGHT);
        int visibleRows = Math.max(1, rowsHeight / GLOBAL_ITEMS_ROW_HEIGHT);
        int rowAreaHeight = visibleRows * GLOBAL_ITEMS_ROW_HEIGHT;
        this.itemsScroll = Mth.clamp(this.itemsScroll, 0, Math.max(0, rows.size() - visibleRows));

        for (int rowIndex = 0; rowIndex < visibleRows; rowIndex++) {
            int sourceRowIndex = this.itemsScroll + rowIndex;
            if (sourceRowIndex >= rows.size()) {
                break;
            }

            SpaceRaceClientStats.ItemStatRow row = rows.get(sourceRowIndex);
            int rowY = rowsTop + rowIndex * GLOBAL_ITEMS_ROW_HEIGHT;
            graphics.fill(tableX, rowY, tableX + tableWidth, rowY + GLOBAL_ITEMS_ROW_HEIGHT, 0x441A1A1A);
            graphics.renderOutline(tableX, rowY, tableWidth, GLOBAL_ITEMS_ROW_HEIGHT, 0x553C3C3C);
            int rowIconX = tableX + (GLOBAL_ITEMS_ICON_COLUMN_WIDTH - GLOBAL_ITEM_ICON_SIZE) / 2;
            int rowIconY = rowY + (GLOBAL_ITEMS_ROW_HEIGHT - GLOBAL_ITEM_ICON_SIZE) / 2;
            graphics.renderItem(row.icon(), rowIconX, rowIconY);

            for (int i = 0; i < columns.length; i++) {
                int cellX = tableX + GLOBAL_ITEMS_ICON_COLUMN_WIDTH + i * cellWidth;
                graphics.fill(cellX, rowY, cellX + cellWidth, rowY + GLOBAL_ITEMS_ROW_HEIGHT, 0x33262626);
                graphics.renderOutline(cellX, rowY, cellWidth, GLOBAL_ITEMS_ROW_HEIGHT, 0x553C3C3C);

                if (i >= row.cells().size()) {
                    int emptyY = rowY + (GLOBAL_ITEMS_ROW_HEIGHT - this.font.lineHeight) / 2;
                    graphics.drawCenteredString(this.font, "-", cellX + cellWidth / 2, emptyY, 0xFF9E9E9E);
                    continue;
                }

                SpaceRaceClientStats.ItemStatCell cell = row.cells().get(i);
                if (cell.total() <= 0) {
                    int emptyY = rowY + (GLOBAL_ITEMS_ROW_HEIGHT - this.font.lineHeight) / 2;
                    graphics.drawCenteredString(this.font, "-", cellX + cellWidth / 2, emptyY, 0xFF9E9E9E);
                    continue;
                }

                SpaceRaceClientStats.LeaderCell leader = cell.leader();
                if (leader != null) {
                    int headX = cellX + (cellWidth - GLOBAL_HEAD_SIZE) / 2;
                    int headY = rowY + 2;
                    this.renderPlayerHead(graphics, leader.playerId(), headX, headY, GLOBAL_HEAD_SIZE);
                    if (mouseX >= headX && mouseX < headX + GLOBAL_HEAD_SIZE && mouseY >= headY && mouseY < headY + GLOBAL_HEAD_SIZE) {
                        hoveredTooltip = List.of(Component.translatable(Translations.SpaceRace.LEADER_VALUE, leader.playerName(), leader.value()));
                    }
                } else {
                    graphics.drawCenteredString(this.font, "-", cellX + cellWidth / 2, rowY + 2, 0xFF9E9E9E);
                }

                int totalY = rowY + GLOBAL_HEAD_SIZE + 3;
                graphics.drawCenteredString(this.font, Integer.toString(cell.total()), cellX + cellWidth / 2, totalY, 0xFFFFFFFF);
            }
        }

        this.itemsScrollbar = new ScrollbarInfo(ScrollbarType.GLOBAL_ITEMS, ScrollbarAxis.VERTICAL, scrollbarX, headerY, GLOBAL_ITEMS_HEADER_HEIGHT + rowAreaHeight, rows.size(), visibleRows, this.itemsScroll);
        this.renderScrollbar(graphics, this.itemsScrollbar, mouseX, mouseY);
        this.mouseoverTooltip = hoveredTooltip;
    }

    private void renderMobsPanel(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        List<SpaceRaceClientStats.MobStatRow> rows = SpaceRaceClientStats.getMobStatRows();
        if (rows.isEmpty()) {
            graphics.drawCenteredString(this.font, Component.translatable(Translations.SpaceRace.GLOBAL_EMPTY_MOBS), x + width / 2, y + height / 2 - this.font.lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        int listWidth = Math.max(120, width - SCROLLBAR_WIDTH - SCROLLBAR_SPACING);
        int scrollbarX = x + listWidth + SCROLLBAR_SPACING;
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

        graphics.fill(x, y, x + listWidth, y + GLOBAL_MOBS_ROW_HEIGHT, 0x55202020);
        graphics.renderOutline(x, y, listWidth, GLOBAL_MOBS_ROW_HEIGHT, 0x773D3D3D);
        graphics.fill(dividerOneX, y, dividerOneX + 1, y + GLOBAL_MOBS_ROW_HEIGHT, 0x773D3D3D);
        graphics.fill(dividerTwoX, y, dividerTwoX + 1, y + GLOBAL_MOBS_ROW_HEIGHT, 0x773D3D3D);
        int headerTextY = y + (GLOBAL_MOBS_ROW_HEIGHT - this.font.lineHeight) / 2;
        graphics.drawString(this.font, Component.translatable(Translations.SpaceRace.GLOBAL_MOBS_COLUMN_MOB), x + 4, headerTextY, 0xFFFFFFFF, false);
        graphics.drawCenteredString(this.font, Component.translatable(Translations.SpaceRace.GLOBAL_MOBS_COLUMN_KILLED), killedCenterX, headerTextY, 0xFFFFFFFF);
        graphics.drawCenteredString(this.font, Component.translatable(Translations.SpaceRace.GLOBAL_MOBS_COLUMN_KILL_LEADER), leaderLeftX + leaderWidth / 2, headerTextY, 0xFFFFFFFF);

        int rowsTop = y + GLOBAL_MOBS_ROW_HEIGHT;
        int rowsHeight = Math.max(0, height - GLOBAL_MOBS_ROW_HEIGHT);
        int visibleRows = Math.max(1, rowsHeight / GLOBAL_MOBS_ROW_HEIGHT);
        int rowAreaHeight = visibleRows * GLOBAL_MOBS_ROW_HEIGHT;
        this.mobsScroll = Mth.clamp(this.mobsScroll, 0, Math.max(0, rows.size() - visibleRows));

        List<Component> hoveredTooltip = null;
        for (int rowIndex = 0; rowIndex < visibleRows; rowIndex++) {
            int sourceRowIndex = this.mobsScroll + rowIndex;
            if (sourceRowIndex >= rows.size()) {
                break;
            }

            SpaceRaceClientStats.MobStatRow row = rows.get(sourceRowIndex);
            int rowY = rowsTop + rowIndex * GLOBAL_MOBS_ROW_HEIGHT;
            graphics.fill(x, rowY, x + listWidth, rowY + GLOBAL_MOBS_ROW_HEIGHT, 0x441A1A1A);
            graphics.renderOutline(x, rowY, listWidth, GLOBAL_MOBS_ROW_HEIGHT, 0x553C3C3C);
            graphics.fill(dividerOneX, rowY, dividerOneX + 1, rowY + GLOBAL_MOBS_ROW_HEIGHT, 0x553C3C3C);
            graphics.fill(dividerTwoX, rowY, dividerTwoX + 1, rowY + GLOBAL_MOBS_ROW_HEIGHT, 0x553C3C3C);

            int textY = rowY + (GLOBAL_MOBS_ROW_HEIGHT - this.font.lineHeight) / 2;
            String mobLabel = this.font.plainSubstrByWidth(row.mobName(), nameWidth - 6);
            graphics.drawString(this.font, mobLabel, x + 4, textY, 0xFFFFFFFF, false);
            graphics.drawCenteredString(this.font, Integer.toString(row.totalKilled()), killedCenterX, textY, 0xFFFFFFFF);

            SpaceRaceClientStats.LeaderCell leader = row.leader();
            int headX = leaderRightX - GLOBAL_HEAD_SIZE - 4;
            int headY = rowY + (GLOBAL_MOBS_ROW_HEIGHT - GLOBAL_HEAD_SIZE) / 2;
            int leaderNameWidth = Math.max(12, headX - leaderLeftX - 4);
            if (leader != null) {
                String leaderName = this.font.plainSubstrByWidth(leader.playerName(), leaderNameWidth);
                graphics.drawString(this.font, leaderName, leaderLeftX + 2, textY, 0xFFFFFFFF, false);
                this.renderPlayerHead(graphics, leader.playerId(), headX, headY, GLOBAL_HEAD_SIZE);
                if (mouseX >= headX && mouseX < headX + GLOBAL_HEAD_SIZE && mouseY >= headY && mouseY < headY + GLOBAL_HEAD_SIZE) {
                    hoveredTooltip = List.of(Component.translatable(Translations.SpaceRace.LEADER_VALUE, leader.playerName(), leader.value()));
                }
            } else {
                graphics.drawCenteredString(this.font, "-", leaderLeftX + leaderWidth / 2, textY, 0xFF9E9E9E);
            }
        }

        this.mobsScrollbar = new ScrollbarInfo(ScrollbarType.GLOBAL_MOBS, ScrollbarAxis.VERTICAL, scrollbarX, y, GLOBAL_MOBS_ROW_HEIGHT + rowAreaHeight, rows.size(), visibleRows, this.mobsScroll);
        this.renderScrollbar(graphics, this.mobsScrollbar, mouseX, mouseY);
        this.mouseoverTooltip = hoveredTooltip;
    }

    private boolean handleScrollbarClick(double mouseX, double mouseY) {
        for (ScrollbarInfo scrollbar : this.getVisibleScrollbars()) {
            if (!scrollbar.isInteractive() || !scrollbar.contains(mouseX, mouseY)) {
                continue;
            }

            int thumbLength = scrollbar.thumbLength();
            int thumbStart = scrollbar.thumbPosition();
            int trackRange = Math.max(0, scrollbar.length() - thumbLength);
            int mouseAxis = scrollbar.axis() == ScrollbarAxis.VERTICAL ? (int) mouseY : (int) mouseX;
            int trackStart = scrollbar.trackStart();

            if (mouseAxis >= thumbStart && mouseAxis < thumbStart + thumbLength) {
                this.activeScrollbar = scrollbar.type();
                this.activeScrollbarThumbOffset = mouseAxis - thumbStart;
                return true;
            }

            int targetThumbStart = Mth.clamp(mouseAxis - thumbLength / 2, trackStart, trackStart + trackRange);
            this.setScrollFromThumbPosition(scrollbar, targetThumbStart);
            this.activeScrollbar = scrollbar.type();
            this.activeScrollbarThumbOffset = mouseAxis - targetThumbStart;
            return true;
        }
        return false;
    }

    private void setScrollFromThumbPosition(ScrollbarInfo scrollbar, int thumbPosition) {
        int maxScroll = scrollbar.maxScroll();
        if (maxScroll <= 0) {
            this.setScrollValue(scrollbar.type(), 0);
            return;
        }

        int thumbLength = scrollbar.thumbLength();
        int trackRange = Math.max(1, scrollbar.length() - thumbLength);
        double progress = (thumbPosition - scrollbar.trackStart()) / (double) trackRange;
        int newScroll = Mth.clamp((int) Math.round(progress * maxScroll), 0, maxScroll);
        this.setScrollValue(scrollbar.type(), newScroll);
    }

    private void setScrollValue(ScrollbarType type, int value) {
        switch (type) {
            case GLOBAL_GENERAL -> this.generalScroll = value;
            case GLOBAL_ITEMS -> this.itemsScroll = value;
            case GLOBAL_MOBS -> this.mobsScroll = value;
            case NONE, SERVER_PLAYER_LIST, SERVER_PLAYER_DETAIL, SERVER_TEAM_LIST, SERVER_TEAM_DETAIL, SERVER_TREE_VERTICAL, SERVER_TREE_HORIZONTAL -> {
            }
        }
    }

    private List<ScrollbarInfo> getVisibleScrollbars() {
        List<ScrollbarInfo> scrollbars = new ArrayList<>(1);
        ScrollbarInfo currentScrollbar = switch (this.statsSection) {
            case GENERAL -> this.generalScrollbar;
            case ITEMS -> this.itemsScrollbar;
            case MOBS -> this.mobsScrollbar;
        };
        if (currentScrollbar != null) {
            scrollbars.add(currentScrollbar);
        }
        return scrollbars;
    }

    private ScrollbarInfo getScrollbarForType(ScrollbarType type) {
        return switch (type) {
            case GLOBAL_GENERAL -> this.generalScrollbar;
            case GLOBAL_ITEMS -> this.itemsScrollbar;
            case GLOBAL_MOBS -> this.mobsScrollbar;
            case NONE, SERVER_PLAYER_LIST, SERVER_PLAYER_DETAIL, SERVER_TEAM_LIST, SERVER_TEAM_DETAIL, SERVER_TREE_VERTICAL, SERVER_TREE_HORIZONTAL -> null;
        };
    }
}
