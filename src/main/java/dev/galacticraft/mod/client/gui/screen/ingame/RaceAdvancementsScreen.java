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
import java.util.UUID;

public class RaceAdvancementsScreen extends AbstractSpaceRaceScreen {
    private final Screen parent;
    private final List<PlayerClickArea> playerClickAreas = new ArrayList<>();
    private final List<TeamClickArea> teamClickAreas = new ArrayList<>();
    private ServerStatsMode statsMode = ServerStatsMode.PLAYER;
    private Button playerTabButton;
    private Button teamTabButton;
    private Button treeTabButton;
    private SpaceRaceButton hideAdvancementsButton;
    private SpaceRaceButton progressionButton;
    private UUID selectedPlayerId;
    private String selectedTeamId;
    private boolean importantProgressionOnly;
    private int playerScroll;
    private int playerDetailScroll;
    private int teamScroll;
    private int teamDetailScroll;
    private int treeScrollX;
    private int treeScrollY;
    private boolean centerTreeVerticalOnNextRender = true;
    private ScrollbarInfo listScrollbar;
    private ScrollbarInfo treeVerticalScrollbar;
    private ScrollbarInfo treeHorizontalScrollbar;
    private List<Component> mouseoverTooltip;

    public RaceAdvancementsScreen(Screen parent) {
        super(Component.translatable(Translations.SpaceRace.SERVER_STATS));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(Component.translatable(Translations.SpaceRace.BACK), this.getLeft() + 5, this.getTop() + 5, 40, 14, button -> this.handleBackButton());

        ClientPlayNetworking.send(RequestSpaceRaceStatsPayload.INSTANCE);
        int panelX = this.getLeft() + 10;
        int panelWidth = this.backgroundWidth - 20;
        int panelRight = panelX + panelWidth;
        int contentX = panelX + STATS_PANEL_PADDING;
        int modeStartX = Math.max(contentX, this.getLeft() + 5 + 40 + SERVER_MODE_BUTTON_SPACING);
        int modeButtonAreaWidth = Math.max(0, panelRight - STATS_PANEL_PADDING - modeStartX);
        int modeButtonWidth = Math.max(1, (modeButtonAreaWidth - SERVER_MODE_BUTTON_SPACING * 2) / 3);
        int modeButtonY = this.getTop() + 5;
        this.playerTabButton = this.addButton(Component.translatable(Translations.SpaceRace.TAB_PLAYER), modeStartX, modeButtonY, modeButtonWidth, SERVER_MODE_BUTTON_HEIGHT, button -> this.setStatsMode(ServerStatsMode.PLAYER));
        this.teamTabButton = this.addButton(Component.translatable(Translations.SpaceRace.TAB_TEAM), modeStartX + modeButtonWidth + SERVER_MODE_BUTTON_SPACING, modeButtonY, modeButtonWidth, SERVER_MODE_BUTTON_HEIGHT, button -> this.setStatsMode(ServerStatsMode.TEAM));
        this.treeTabButton = this.addButton(Component.translatable(Translations.SpaceRace.TAB_SERVER), modeStartX + (modeButtonWidth + SERVER_MODE_BUTTON_SPACING) * 2, modeButtonY, modeButtonWidth, SERVER_MODE_BUTTON_HEIGHT, button -> this.setStatsMode(ServerStatsMode.SERVER));
        int hideButtonWidth = this.getCompactButtonWidth(Component.translatable(Translations.SpaceRace.HIDE_MY_ADVANCEMENTS), Component.translatable(Translations.SpaceRace.SHOW_MY_ADVANCEMENTS));
        int progressionButtonWidth = this.getCompactButtonWidth(Component.translatable(Translations.SpaceRace.SHOW_SIMPLE_PROGRESSION), Component.translatable(Translations.SpaceRace.SHOW_FULL_PROGRESSION));
        this.hideAdvancementsButton = this.addRenderableWidget(new SpaceRaceButton(this.getVisibilityButtonText(), 0, 0, hideButtonWidth, COMPACT_TOGGLE_BUTTON_HEIGHT, button -> {
            this.toggleServerAdvancementVisibility();
            button.setMessage(this.getVisibilityButtonText());
        }));
        this.progressionButton = this.addRenderableWidget(new SpaceRaceButton(this.getProgressionButtonText(), 0, 0, progressionButtonWidth, COMPACT_TOGGLE_BUTTON_HEIGHT, button -> {
            this.toggleImportantProgression();
            button.setMessage(this.getProgressionButtonText());
        }));
        this.updateModeButtons();
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }

    @Override
    protected void clearTransientRenderState() {
        this.playerClickAreas.clear();
        this.teamClickAreas.clear();
        this.listScrollbar = null;
        this.treeVerticalScrollbar = null;
        this.treeHorizontalScrollbar = null;
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
        if (this.statsMode == ServerStatsMode.SERVER) {
            boolean horizontalScroll = Screen.hasShiftDown() || Math.abs(horizontalAmount) > Math.abs(verticalAmount);
            if (horizontalScroll) {
                this.treeScrollX = Math.max(0, this.treeScrollX - delta * 18);
            } else {
                this.treeScrollY = Math.max(0, this.treeScrollY - delta * 18);
            }
        } else {
            this.setActiveListScroll(Math.max(0, this.getActiveListScroll() - delta));
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.animationCompleted && this.handleScrollbarClick(mouseX, mouseY)) {
            return true;
        }
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return button == 0 && this.animationCompleted && this.handleOverviewClick(mouseX, mouseY);
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

    private void toggleImportantProgression() {
        this.importantProgressionOnly = !this.importantProgressionOnly;
    }

    private void toggleServerAdvancementVisibility() {
        boolean hideServerAdvancements = !SpaceRaceClientStats.isHideServerAdvancements();
        boolean hideGlobalLeaderboard = SpaceRaceClientStats.isHideGlobalLeaderboard();
        SpaceRaceClientStats.setVisibility(hideServerAdvancements, hideGlobalLeaderboard);
        ClientPlayNetworking.send(new UpdateSpaceRaceVisibilityPayload(hideServerAdvancements, hideGlobalLeaderboard));
    }

    private Component getProgressionButtonText() {
        if (this.importantProgressionOnly) {
            return Component.translatable(Translations.SpaceRace.SHOW_FULL_PROGRESSION);
        }
        return Component.translatable(Translations.SpaceRace.SHOW_SIMPLE_PROGRESSION);
    }

    private Component getVisibilityButtonText() {
        if (SpaceRaceClientStats.isHideServerAdvancements()) {
            return Component.translatable(Translations.SpaceRace.SHOW_MY_ADVANCEMENTS);
        }
        return Component.translatable(Translations.SpaceRace.HIDE_MY_ADVANCEMENTS);
    }

    private void setStatsMode(ServerStatsMode mode) {
        if (this.statsMode == mode) {
            return;
        }
        this.statsMode = mode;
        this.selectedPlayerId = null;
        this.selectedTeamId = null;
        this.listScrollbar = null;
        this.treeVerticalScrollbar = null;
        this.treeHorizontalScrollbar = null;
        this.activeScrollbar = ScrollbarType.NONE;
        if (mode == ServerStatsMode.SERVER) {
            this.centerTreeVerticalOnNextRender = true;
        }
        this.updateModeButtons();
    }

    private void updateModeButtons() {
        if (this.playerTabButton != null) {
            this.playerTabButton.active = this.statsMode != ServerStatsMode.PLAYER;
        }
        if (this.teamTabButton != null) {
            this.teamTabButton.active = this.statsMode != ServerStatsMode.TEAM;
        }
        if (this.treeTabButton != null) {
            this.treeTabButton.active = this.statsMode != ServerStatsMode.SERVER;
        }
        this.layoutBottomButtons();
    }

    private void layoutBottomButtons() {
        int panelX = this.getLeft() + 10;
        int panelWidth = this.backgroundWidth - 20;
        int panelRight = panelX + panelWidth;
        int buttonRight = panelRight - STATS_PANEL_PADDING;
        int buttonY = this.getBottom() - COMPACT_TOGGLE_BUTTON_HEIGHT - 4;

        int hideButtonWidth = this.getCompactButtonWidth(
                Component.translatable(Translations.SpaceRace.HIDE_MY_ADVANCEMENTS),
                Component.translatable(Translations.SpaceRace.SHOW_MY_ADVANCEMENTS)
        );
        if (this.hideAdvancementsButton != null) {
            this.hideAdvancementsButton.setX(buttonRight - hideButtonWidth);
            this.hideAdvancementsButton.setY(buttonY);
            this.hideAdvancementsButton.visible = true;
            this.hideAdvancementsButton.active = true;
        }

        boolean showProgressionButton = this.statsMode != ServerStatsMode.SERVER;
        if (this.progressionButton != null) {
            int progressionButtonWidth = this.getCompactButtonWidth(
                    Component.translatable(Translations.SpaceRace.SHOW_SIMPLE_PROGRESSION),
                    Component.translatable(Translations.SpaceRace.SHOW_FULL_PROGRESSION)
            );
            this.progressionButton.setX(buttonRight - hideButtonWidth - COMPACT_TOGGLE_BUTTON_SPACING - progressionButtonWidth);
            this.progressionButton.setY(buttonY);
            this.progressionButton.visible = showProgressionButton;
            this.progressionButton.active = showProgressionButton;
        }
    }

    private int getBottomControlsReserved() {
        if (this.statsMode == ServerStatsMode.SERVER) {
            return SERVER_BOTTOM_CONTROL_RESERVED_SERVER_TAB;
        }
        return SERVER_BOTTOM_CONTROL_RESERVED;
    }

    private void handleBackButton() {
        if (this.statsMode == ServerStatsMode.PLAYER && this.selectedPlayerId != null) {
            this.selectedPlayerId = null;
            this.playerDetailScroll = 0;
            this.listScrollbar = null;
            return;
        }
        if (this.statsMode == ServerStatsMode.TEAM && this.selectedTeamId != null) {
            this.selectedTeamId = null;
            this.teamDetailScroll = 0;
            this.listScrollbar = null;
            return;
        }
        this.onClose();
    }

    private int getActiveListScroll() {
        if (this.statsMode == ServerStatsMode.PLAYER) {
            return this.selectedPlayerId == null ? this.playerScroll : this.playerDetailScroll;
        }
        if (this.statsMode == ServerStatsMode.TEAM) {
            return this.selectedTeamId == null ? this.teamScroll : this.teamDetailScroll;
        }
        return this.treeScrollY;
    }

    private void setActiveListScroll(int value) {
        if (this.statsMode == ServerStatsMode.PLAYER) {
            if (this.selectedPlayerId == null) {
                this.playerScroll = value;
            } else {
                this.playerDetailScroll = value;
            }
            return;
        }
        if (this.statsMode == ServerStatsMode.TEAM) {
            if (this.selectedTeamId == null) {
                this.teamScroll = value;
            } else {
                this.teamDetailScroll = value;
            }
            return;
        }
        this.treeScrollY = value;
    }

    private void renderPanel(GuiGraphics graphics, int mouseX, int mouseY) {
        int panelX = this.getLeft() + 10;
        int panelY = this.getTop() + 24;
        int panelWidth = this.backgroundWidth - 20;
        int panelHeight = this.backgroundHeight - 30 - this.getBottomControlsReserved();

        graphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0x66000000);
        graphics.renderOutline(panelX, panelY, panelWidth, panelHeight, 0xAA2D2D2D);

        int contentX = panelX + STATS_PANEL_PADDING;
        int contentY = panelY + STATS_PANEL_PADDING;
        int contentWidth = panelWidth - STATS_PANEL_PADDING * 2;
        Component header = switch (this.statsMode) {
            case PLAYER -> this.selectedPlayerId == null ? Component.translatable(Translations.SpaceRace.HEADER_PLAYER) : Component.translatable(Translations.SpaceRace.HEADER_PLAYER_PROGRESS);
            case TEAM -> this.selectedTeamId == null ? Component.translatable(Translations.SpaceRace.HEADER_TEAM) : Component.translatable(Translations.SpaceRace.HEADER_TEAM_MEMBERS);
            case SERVER -> Component.translatable(Translations.SpaceRace.HEADER_SERVER);
        };
        graphics.drawString(this.font, header, contentX, contentY, 0xFFFFFFFF, false);

        int headerSpacing = this.statsMode == ServerStatsMode.SERVER ? 5 : STATS_PANEL_PADDING;
        int bodyTop = contentY + this.font.lineHeight + headerSpacing;
        int bodyHeight = Math.max(0, panelHeight - (bodyTop - panelY) - STATS_PANEL_PADDING);
        if (bodyHeight <= 0) {
            return;
        }

        switch (this.statsMode) {
            case PLAYER -> {
                if (this.selectedPlayerId == null) {
                    this.renderPlayerOverviewPanel(graphics, mouseX, mouseY, contentX, bodyTop, contentWidth, bodyHeight);
                } else {
                    this.renderPlayerDetailPanel(graphics, mouseX, mouseY, contentX, bodyTop, contentWidth, bodyHeight);
                }
            }
            case TEAM -> {
                if (this.selectedTeamId == null) {
                    this.renderTeamOverviewPanel(graphics, mouseX, mouseY, contentX, bodyTop, contentWidth, bodyHeight);
                } else {
                    this.renderTeamDetailPanel(graphics, mouseX, mouseY, contentX, bodyTop, contentWidth, bodyHeight);
                }
            }
            case SERVER -> this.renderServerTreePanel(graphics, mouseX, mouseY, contentX, bodyTop, contentWidth, bodyHeight);
        }
    }

    private boolean handleOverviewClick(double mouseX, double mouseY) {
        if (this.statsMode == ServerStatsMode.PLAYER && this.selectedPlayerId == null) {
            for (PlayerClickArea clickArea : this.playerClickAreas) {
                if (!clickArea.contains(mouseX, mouseY)) {
                    continue;
                }
                this.selectedPlayerId = clickArea.playerId();
                this.playerDetailScroll = 0;
                this.activeScrollbar = ScrollbarType.NONE;
                return true;
            }
            return false;
        }

        if (this.statsMode == ServerStatsMode.TEAM && this.selectedTeamId == null) {
            for (TeamClickArea clickArea : this.teamClickAreas) {
                if (!clickArea.contains(mouseX, mouseY)) {
                    continue;
                }
                this.selectedTeamId = clickArea.teamId();
                this.teamDetailScroll = 0;
                this.activeScrollbar = ScrollbarType.NONE;
                return true;
            }
        }
        return false;
    }

    private void renderPlayerOverviewPanel(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        List<SpaceRaceClientStats.PlayerStatsEntry> entries = SpaceRaceClientStats.getServerStatsEntries();
        if (entries.isEmpty()) {
            graphics.drawCenteredString(this.font, Component.translatable(Translations.SpaceRace.EMPTY_PLAYER_DATA), x + width / 2, y + height / 2 - this.font.lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        int listWidth = Math.max(80, width - SCROLLBAR_WIDTH - SCROLLBAR_SPACING);
        int scrollbarX = x + listWidth + SCROLLBAR_SPACING;
        int visibleRows = Math.max(0, (height + OVERVIEW_ENTRY_SPACING) / (OVERVIEW_ENTRY_HEIGHT + OVERVIEW_ENTRY_SPACING));
        int rowsTrackHeight = Math.max(0, visibleRows * (OVERVIEW_ENTRY_HEIGHT + OVERVIEW_ENTRY_SPACING) - OVERVIEW_ENTRY_SPACING);
        this.playerScroll = Mth.clamp(this.playerScroll, 0, Math.max(0, entries.size() - visibleRows));

        int rowsToRender = Math.min(visibleRows, entries.size());
        List<Component> hoveredTooltip = null;
        for (int rowIndex = 0; rowIndex < rowsToRender; rowIndex++) {
            int sourceRowIndex = this.playerScroll + rowIndex;
            if (sourceRowIndex >= entries.size()) {
                break;
            }

            SpaceRaceClientStats.PlayerStatsEntry entry = entries.get(sourceRowIndex);
            int rowY = y + rowIndex * (OVERVIEW_ENTRY_HEIGHT + OVERVIEW_ENTRY_SPACING);
            List<Component> hovered = this.renderPlayerOverviewRow(graphics, mouseX, mouseY, x, rowY, listWidth, entry);
            this.playerClickAreas.add(new PlayerClickArea(x, rowY, x + listWidth, rowY + OVERVIEW_ENTRY_HEIGHT, entry.playerId()));
            if (hovered != null) {
                hoveredTooltip = hovered;
            }
        }

        this.listScrollbar = new ScrollbarInfo(
                ScrollbarType.SERVER_PLAYER_LIST,
                ScrollbarAxis.VERTICAL,
                scrollbarX,
                y,
                rowsTrackHeight,
                entries.size(),
                Math.max(0, visibleRows),
                this.playerScroll
        );
        this.renderScrollbar(graphics, this.listScrollbar, mouseX, mouseY);

        if (rowsToRender == 0) {
            graphics.drawCenteredString(this.font, Component.translatable(Translations.SpaceRace.EMPTY_PLAYER_DATA), x + listWidth / 2, y + height / 2 - this.font.lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        this.mouseoverTooltip = hoveredTooltip;
    }

    private List<Component> renderPlayerOverviewRow(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, SpaceRaceClientStats.PlayerStatsEntry statsEntry) {
        graphics.fill(x, y, x + width, y + OVERVIEW_ENTRY_HEIGHT, 0x441A1A1A);
        graphics.renderOutline(x, y, width, OVERVIEW_ENTRY_HEIGHT, 0x553C3C3C);

        int headX = x + STATS_ENTRY_CONTENT_PADDING;
        int headY = y + (OVERVIEW_ENTRY_HEIGHT - PLAYER_HEAD_SIZE) / 2;
        this.renderPlayerHead(graphics, statsEntry.playerId(), headX, headY, PLAYER_HEAD_SIZE);

        List<SpaceRaceClientStats.AdvancementIconData> visibleIcons = this.getFilteredAdvancements(statsEntry.advancementIcons());
        int textX = headX + PLAYER_HEAD_SIZE + PLAYER_HEAD_SPACING;
        int rightEdge = x + width - STATS_ENTRY_CONTENT_PADDING;
        int iconCount = this.getRenderableIconCount(textX, rightEdge, visibleIcons.size());
        int iconWidth = iconCount == 0 ? 0 : iconCount * ADVANCEMENT_ICON_SIZE + (iconCount - 1) * ADVANCEMENT_ICON_SPACING;
        int textWidth = rightEdge - textX - (iconWidth > 0 ? ADVANCEMENT_TEXT_ICON_SPACING + iconWidth : 0);
        if (textWidth <= 0) {
            return null;
        }

        Component nameComponent = Component.literal(statsEntry.playerName());
        if (this.minecraft != null && this.minecraft.player != null && this.minecraft.player.connection != null) {
            PlayerInfo playerInfo = this.minecraft.player.connection.getPlayerInfo(statsEntry.playerId());
            if (playerInfo != null && playerInfo.getTabListDisplayName() != null) {
                nameComponent = playerInfo.getTabListDisplayName();
            }
        }

        List<FormattedCharSequence> nameLines = this.font.split(nameComponent, textWidth);
        if (!nameLines.isEmpty()) {
            int textY = y + (OVERVIEW_ENTRY_HEIGHT - this.font.lineHeight) / 2;
            graphics.drawString(this.font, nameLines.get(0), textX, textY, 0xFFFFFFFF, false);
        }

        if (iconCount == 0) {
            return null;
        }

        int iconStartX = rightEdge - iconWidth;
        int iconY = y + (OVERVIEW_ENTRY_HEIGHT - ADVANCEMENT_ICON_SIZE) / 2;
        for (int i = 0; i < iconCount; i++) {
            SpaceRaceClientStats.AdvancementIconData advancementIcon = visibleIcons.get(i);
            ItemStack icon = advancementIcon.icon();
            if (icon.isEmpty()) {
                continue;
            }
            int iconX = iconStartX + i * (ADVANCEMENT_ICON_SIZE + ADVANCEMENT_ICON_SPACING);
            graphics.renderItem(icon, iconX, iconY);
            if (mouseX >= iconX && mouseX < iconX + ADVANCEMENT_ICON_SIZE && mouseY >= iconY && mouseY < iconY + ADVANCEMENT_ICON_SIZE) {
                return List.of(Component.literal(advancementIcon.title()).withStyle(style -> style.withColor(advancementIcon.color())));
            }
        }
        return null;
    }

    private void renderPlayerDetailPanel(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        SpaceRaceClientStats.PlayerStatsEntry entry = SpaceRaceClientStats.getServerStatsEntry(this.selectedPlayerId);
        if (entry == null) {
            graphics.drawCenteredString(this.font, Component.translatable(Translations.SpaceRace.EMPTY_SELECTED_PLAYER), x + width / 2, y + height / 2 - this.font.lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        int headerHeight = STATS_ENTRY_HEIGHT;
        int listTop = y + headerHeight + STATS_ENTRY_SPACING;
        int listHeight = Math.max(0, height - headerHeight - STATS_ENTRY_SPACING);
        int listWidth = Math.max(80, width - SCROLLBAR_WIDTH - SCROLLBAR_SPACING);
        int scrollbarX = x + listWidth + SCROLLBAR_SPACING;

        graphics.fill(x, y, x + listWidth, y + headerHeight, 0x55202020);
        graphics.renderOutline(x, y, listWidth, headerHeight, 0x773D3D3D);
        int headX = x + STATS_ENTRY_CONTENT_PADDING;
        int headY = y + (headerHeight - PLAYER_HEAD_SIZE) / 2;
        this.renderPlayerHead(graphics, entry.playerId(), headX, headY, PLAYER_HEAD_SIZE);
        graphics.drawString(this.font, Component.literal(entry.playerName()), headX + PLAYER_HEAD_SIZE + PLAYER_HEAD_SPACING, y + (headerHeight - this.font.lineHeight) / 2, 0xFFFFFFFF, false);

        List<SpaceRaceClientStats.AdvancementIconData> advancements = this.getFilteredAdvancements(entry.advancementIcons());
        if (advancements.isEmpty()) {
            graphics.drawCenteredString(this.font, Component.translatable(Translations.SpaceRace.EMPTY_MATCHING_ADVANCEMENTS), x + listWidth / 2, listTop + listHeight / 2 - this.font.lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        int visibleRows = Math.max(0, (listHeight + ADVANCEMENT_ROW_SPACING) / (ADVANCEMENT_ROW_HEIGHT + ADVANCEMENT_ROW_SPACING));
        int rowsTrackHeight = Math.max(0, visibleRows * (ADVANCEMENT_ROW_HEIGHT + ADVANCEMENT_ROW_SPACING) - ADVANCEMENT_ROW_SPACING);
        this.playerDetailScroll = Mth.clamp(this.playerDetailScroll, 0, Math.max(0, advancements.size() - visibleRows));

        for (int rowIndex = 0; rowIndex < visibleRows; rowIndex++) {
            int sourceRowIndex = this.playerDetailScroll + rowIndex;
            if (sourceRowIndex >= advancements.size()) {
                break;
            }

            SpaceRaceClientStats.AdvancementIconData advancement = advancements.get(sourceRowIndex);
            int rowY = listTop + rowIndex * (ADVANCEMENT_ROW_HEIGHT + ADVANCEMENT_ROW_SPACING);
            graphics.fill(x, rowY, x + listWidth, rowY + ADVANCEMENT_ROW_HEIGHT, 0x441A1A1A);
            graphics.renderOutline(x, rowY, listWidth, ADVANCEMENT_ROW_HEIGHT, 0x553C3C3C);

            int iconY = rowY + (ADVANCEMENT_ROW_HEIGHT - ADVANCEMENT_ICON_SIZE) / 2;
            int iconX = x + STATS_ENTRY_CONTENT_PADDING;
            graphics.renderItem(advancement.icon(), iconX, iconY);

            int textX = iconX + ADVANCEMENT_ICON_SIZE + ADVANCEMENT_TEXT_ICON_SPACING;
            int textWidth = Math.max(10, listWidth - (textX - x) - STATS_ENTRY_CONTENT_PADDING);
            String title = this.font.plainSubstrByWidth(advancement.title(), textWidth);
            graphics.drawString(this.font, Component.literal(title), textX, rowY + (ADVANCEMENT_ROW_HEIGHT - this.font.lineHeight) / 2, advancement.color(), false);
        }

        this.listScrollbar = new ScrollbarInfo(
                ScrollbarType.SERVER_PLAYER_DETAIL,
                ScrollbarAxis.VERTICAL,
                scrollbarX,
                listTop,
                rowsTrackHeight,
                advancements.size(),
                Math.max(0, visibleRows),
                this.playerDetailScroll
        );
        this.renderScrollbar(graphics, this.listScrollbar, mouseX, mouseY);
    }

    private void renderTeamOverviewPanel(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        List<SpaceRaceClientStats.TeamStatsEntry> entries = SpaceRaceClientStats.getTeamStatsEntries();
        if (entries.isEmpty()) {
            graphics.drawCenteredString(this.font, Component.translatable(Translations.SpaceRace.EMPTY_TEAM_DATA), x + width / 2, y + height / 2 - this.font.lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        int listWidth = Math.max(80, width - SCROLLBAR_WIDTH - SCROLLBAR_SPACING);
        int scrollbarX = x + listWidth + SCROLLBAR_SPACING;
        int visibleRows = Math.max(0, (height + OVERVIEW_ENTRY_SPACING) / (OVERVIEW_ENTRY_HEIGHT + OVERVIEW_ENTRY_SPACING));
        int rowsTrackHeight = Math.max(0, visibleRows * (OVERVIEW_ENTRY_HEIGHT + OVERVIEW_ENTRY_SPACING) - OVERVIEW_ENTRY_SPACING);
        this.teamScroll = Mth.clamp(this.teamScroll, 0, Math.max(0, entries.size() - visibleRows));

        int rowsToRender = Math.min(visibleRows, entries.size());
        List<Component> hoveredTooltip = null;
        for (int rowIndex = 0; rowIndex < rowsToRender; rowIndex++) {
            int sourceRowIndex = this.teamScroll + rowIndex;
            if (sourceRowIndex >= entries.size()) {
                break;
            }

            SpaceRaceClientStats.TeamStatsEntry entry = entries.get(sourceRowIndex);
            int rowY = y + rowIndex * (OVERVIEW_ENTRY_HEIGHT + OVERVIEW_ENTRY_SPACING);
            List<Component> hovered = this.renderTeamOverviewRow(graphics, mouseX, mouseY, x, rowY, listWidth, entry);
            this.teamClickAreas.add(new TeamClickArea(x, rowY, x + listWidth, rowY + OVERVIEW_ENTRY_HEIGHT, entry.teamId()));
            if (hovered != null) {
                hoveredTooltip = hovered;
            }
        }

        this.listScrollbar = new ScrollbarInfo(
                ScrollbarType.SERVER_TEAM_LIST,
                ScrollbarAxis.VERTICAL,
                scrollbarX,
                y,
                rowsTrackHeight,
                entries.size(),
                Math.max(0, visibleRows),
                this.teamScroll
        );
        this.renderScrollbar(graphics, this.listScrollbar, mouseX, mouseY);

        if (rowsToRender == 0) {
            graphics.drawCenteredString(this.font, Component.translatable(Translations.SpaceRace.EMPTY_TEAM_DATA), x + listWidth / 2, y + height / 2 - this.font.lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        this.mouseoverTooltip = hoveredTooltip;
    }

    private List<Component> renderTeamOverviewRow(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, SpaceRaceClientStats.TeamStatsEntry statsEntry) {
        graphics.fill(x, y, x + width, y + OVERVIEW_ENTRY_HEIGHT, 0x441A1A1A);
        graphics.renderOutline(x, y, width, OVERVIEW_ENTRY_HEIGHT, 0x553C3C3C);

        int iconX = x + STATS_ENTRY_CONTENT_PADDING;
        int iconY = y + (OVERVIEW_ENTRY_HEIGHT - ADVANCEMENT_ICON_SIZE) / 2;
        graphics.renderItem(statsEntry.teamFlag(), iconX, iconY);

        List<SpaceRaceClientStats.AdvancementIconData> visibleIcons = this.getFilteredAdvancements(statsEntry.advancementIcons());
        int textX = iconX + ADVANCEMENT_ICON_SIZE + PLAYER_HEAD_SPACING;
        int rightEdge = x + width - STATS_ENTRY_CONTENT_PADDING;
        int iconCount = this.getRenderableIconCount(textX, rightEdge, visibleIcons.size());
        int iconWidth = iconCount == 0 ? 0 : iconCount * ADVANCEMENT_ICON_SIZE + (iconCount - 1) * ADVANCEMENT_ICON_SPACING;
        int textWidth = rightEdge - textX - (iconWidth > 0 ? ADVANCEMENT_TEXT_ICON_SPACING + iconWidth : 0);
        if (textWidth <= 0) {
            return null;
        }

        String teamName = this.font.plainSubstrByWidth(statsEntry.teamName(), textWidth);
        graphics.drawString(this.font, teamName, textX, y + (OVERVIEW_ENTRY_HEIGHT - this.font.lineHeight) / 2, 0xFFFFFFFF, false);

        if (iconCount == 0) {
            return null;
        }

        int iconStartX = rightEdge - iconWidth;
        int rowIconY = y + (OVERVIEW_ENTRY_HEIGHT - ADVANCEMENT_ICON_SIZE) / 2;
        for (int i = 0; i < iconCount; i++) {
            SpaceRaceClientStats.AdvancementIconData advancementIcon = visibleIcons.get(i);
            ItemStack icon = advancementIcon.icon();
            if (icon.isEmpty()) {
                continue;
            }
            int entryIconX = iconStartX + i * (ADVANCEMENT_ICON_SIZE + ADVANCEMENT_ICON_SPACING);
            graphics.renderItem(icon, entryIconX, rowIconY);
            if (mouseX >= entryIconX && mouseX < entryIconX + ADVANCEMENT_ICON_SIZE && mouseY >= rowIconY && mouseY < rowIconY + ADVANCEMENT_ICON_SIZE) {
                return this.buildAdvancementTooltip(advancementIcon, true);
            }
        }
        return null;
    }

    private void renderTeamDetailPanel(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        SpaceRaceClientStats.TeamStatsEntry entry = SpaceRaceClientStats.getTeamStatsEntry(this.selectedTeamId);
        if (entry == null) {
            graphics.drawCenteredString(this.font, Component.translatable(Translations.SpaceRace.EMPTY_SELECTED_TEAM), x + width / 2, y + height / 2 - this.font.lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        int headerHeight = STATS_ENTRY_HEIGHT;
        int listTop = y + headerHeight + STATS_ENTRY_SPACING;
        int listHeight = Math.max(0, height - headerHeight - STATS_ENTRY_SPACING);
        int listWidth = Math.max(80, width - SCROLLBAR_WIDTH - SCROLLBAR_SPACING);
        int scrollbarX = x + listWidth + SCROLLBAR_SPACING;

        graphics.fill(x, y, x + listWidth, y + headerHeight, 0x55202020);
        graphics.renderOutline(x, y, listWidth, headerHeight, 0x773D3D3D);
        int flagX = x + STATS_ENTRY_CONTENT_PADDING;
        int flagY = y + (headerHeight - ADVANCEMENT_ICON_SIZE) / 2;
        graphics.renderItem(entry.teamFlag(), flagX, flagY);
        graphics.drawString(this.font, Component.literal(entry.teamName()), flagX + ADVANCEMENT_ICON_SIZE + PLAYER_HEAD_SPACING, y + (headerHeight - this.font.lineHeight) / 2, 0xFFFFFFFF, false);

        List<SpaceRaceClientStats.TeamMemberEntry> members = entry.members();
        if (members.isEmpty()) {
            graphics.drawCenteredString(this.font, Component.translatable(Translations.SpaceRace.EMPTY_TEAM_MEMBERS), x + listWidth / 2, listTop + listHeight / 2 - this.font.lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        int visibleRows = Math.max(0, (listHeight + OVERVIEW_ENTRY_SPACING) / (OVERVIEW_ENTRY_HEIGHT + OVERVIEW_ENTRY_SPACING));
        int rowsTrackHeight = Math.max(0, visibleRows * (OVERVIEW_ENTRY_HEIGHT + OVERVIEW_ENTRY_SPACING) - OVERVIEW_ENTRY_SPACING);
        this.teamDetailScroll = Mth.clamp(this.teamDetailScroll, 0, Math.max(0, members.size() - visibleRows));

        List<Component> hoveredTooltip = null;
        for (int rowIndex = 0; rowIndex < visibleRows; rowIndex++) {
            int sourceRowIndex = this.teamDetailScroll + rowIndex;
            if (sourceRowIndex >= members.size()) {
                break;
            }

            SpaceRaceClientStats.TeamMemberEntry member = members.get(sourceRowIndex);
            int rowY = listTop + rowIndex * (OVERVIEW_ENTRY_HEIGHT + OVERVIEW_ENTRY_SPACING);
            List<Component> hovered = this.renderTeamMemberRow(graphics, mouseX, mouseY, x, rowY, listWidth, member);
            if (hovered != null) {
                hoveredTooltip = hovered;
            }
        }

        this.listScrollbar = new ScrollbarInfo(
                ScrollbarType.SERVER_TEAM_DETAIL,
                ScrollbarAxis.VERTICAL,
                scrollbarX,
                listTop,
                rowsTrackHeight,
                members.size(),
                Math.max(0, visibleRows),
                this.teamDetailScroll
        );
        this.renderScrollbar(graphics, this.listScrollbar, mouseX, mouseY);
        this.mouseoverTooltip = hoveredTooltip;
    }

    private List<Component> renderTeamMemberRow(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, SpaceRaceClientStats.TeamMemberEntry member) {
        graphics.fill(x, y, x + width, y + OVERVIEW_ENTRY_HEIGHT, 0x441A1A1A);
        graphics.renderOutline(x, y, width, OVERVIEW_ENTRY_HEIGHT, 0x553C3C3C);

        int headX = x + STATS_ENTRY_CONTENT_PADDING;
        int headY = y + (OVERVIEW_ENTRY_HEIGHT - PLAYER_HEAD_SIZE) / 2;
        this.renderPlayerHead(graphics, member.playerId(), headX, headY, PLAYER_HEAD_SIZE);

        List<SpaceRaceClientStats.AdvancementIconData> visibleIcons = this.getFilteredAdvancements(member.advancementIcons());
        int textX = headX + PLAYER_HEAD_SIZE + PLAYER_HEAD_SPACING;
        int rightEdge = x + width - STATS_ENTRY_CONTENT_PADDING;
        int iconCount = this.getRenderableIconCount(textX, rightEdge, visibleIcons.size());
        int iconWidth = iconCount == 0 ? 0 : iconCount * ADVANCEMENT_ICON_SIZE + (iconCount - 1) * ADVANCEMENT_ICON_SPACING;
        int textWidth = rightEdge - textX - (iconWidth > 0 ? ADVANCEMENT_TEXT_ICON_SPACING + iconWidth : 0);
        if (textWidth <= 0) {
            return null;
        }

        String playerName = this.font.plainSubstrByWidth(member.playerName(), textWidth);
        graphics.drawString(this.font, playerName, textX, y + (OVERVIEW_ENTRY_HEIGHT - this.font.lineHeight) / 2, 0xFFFFFFFF, false);

        if (iconCount == 0) {
            return null;
        }

        int iconStartX = rightEdge - iconWidth;
        int iconY = y + (OVERVIEW_ENTRY_HEIGHT - ADVANCEMENT_ICON_SIZE) / 2;
        for (int i = 0; i < iconCount; i++) {
            SpaceRaceClientStats.AdvancementIconData advancementIcon = visibleIcons.get(i);
            ItemStack icon = advancementIcon.icon();
            if (icon.isEmpty()) {
                continue;
            }
            int iconX = iconStartX + i * (ADVANCEMENT_ICON_SIZE + ADVANCEMENT_ICON_SPACING);
            graphics.renderItem(icon, iconX, iconY);
            if (mouseX >= iconX && mouseX < iconX + ADVANCEMENT_ICON_SIZE && mouseY >= iconY && mouseY < iconY + ADVANCEMENT_ICON_SIZE) {
                return List.of(Component.literal(advancementIcon.title()).withStyle(style -> style.withColor(advancementIcon.color())));
            }
        }
        return null;
    }

    private void renderServerTreePanel(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        List<SpaceRaceClientStats.ServerAdvancementNode> nodes = SpaceRaceClientStats.getServerAdvancementNodes();
        if (nodes.isEmpty()) {
            graphics.drawCenteredString(this.font, Component.translatable(Translations.SpaceRace.EMPTY_SERVER_TREE), x + width / 2, y + height / 2 - this.font.lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        int canvasWidth = Math.max(1, width - SCROLLBAR_WIDTH - SCROLLBAR_SPACING);
        int canvasHeight = Math.max(1, height - SCROLLBAR_WIDTH - SCROLLBAR_SPACING);
        int verticalScrollbarX = x + canvasWidth + SCROLLBAR_SPACING;
        int horizontalScrollbarY = y + canvasHeight + SCROLLBAR_SPACING;

        graphics.fill(x, y, x + canvasWidth, y + canvasHeight, 0x33101010);
        graphics.renderOutline(x, y, canvasWidth, canvasHeight, 0x553F3F3F);

        Map<ResourceLocation, TreeNodeRenderInfo> nodePositions = new HashMap<>();
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (SpaceRaceClientStats.ServerAdvancementNode node : nodes) {
            int nodeX = Math.round(node.x() * TREE_NODE_SPACING_X);
            int nodeY = Math.round(node.y() * TREE_NODE_SPACING_Y);
            nodePositions.put(node.advancementId(), new TreeNodeRenderInfo(node, nodeX, nodeY));
            minX = Math.min(minX, nodeX);
            minY = Math.min(minY, nodeY);
            maxX = Math.max(maxX, nodeX);
            maxY = Math.max(maxY, nodeY);
        }

        if (nodePositions.isEmpty()) {
            return;
        }

        int contentWidth = Math.max(TREE_NODE_SIZE + TREE_CANVAS_PADDING * 2, (maxX - minX) + TREE_NODE_SIZE + TREE_CANVAS_PADDING * 2);
        int contentHeight = Math.max(TREE_NODE_SIZE + TREE_CANVAS_PADDING * 2, (maxY - minY) + TREE_NODE_SIZE + TREE_CANVAS_PADDING * 2);
        int maxScrollX = Math.max(0, contentWidth - canvasWidth);
        int maxScrollY = Math.max(0, contentHeight - canvasHeight);
        if (this.centerTreeVerticalOnNextRender) {
            this.treeScrollY = maxScrollY / 2;
            this.centerTreeVerticalOnNextRender = false;
        }
        this.treeScrollX = Mth.clamp(this.treeScrollX, 0, maxScrollX);
        this.treeScrollY = Mth.clamp(this.treeScrollY, 0, maxScrollY);

        int originX = x + TREE_CANVAS_PADDING - this.treeScrollX - minX;
        int originY = y + TREE_CANVAS_PADDING - this.treeScrollY - minY;

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

            int parentCenterX = originX + parentInfo.nodeX() + TREE_NODE_SIZE / 2;
            int parentCenterY = originY + parentInfo.nodeY() + TREE_NODE_SIZE / 2;
            int childCenterX = originX + info.nodeX() + TREE_NODE_SIZE / 2;
            int childCenterY = originY + info.nodeY() + TREE_NODE_SIZE / 2;

            this.drawClampedHorizontalLine(graphics, parentCenterX, childCenterX, parentCenterY, x, y, canvasWidth, canvasHeight, TREE_LINE_COLOR);
            this.drawClampedVerticalLine(graphics, childCenterX, parentCenterY, childCenterY, x, y, canvasWidth, canvasHeight, TREE_LINE_COLOR);
        }

        List<Component> hoveredTooltip = null;
        for (TreeNodeRenderInfo info : nodePositions.values()) {
            SpaceRaceClientStats.ServerAdvancementNode node = info.node();
            int nodeX = originX + info.nodeX();
            int nodeY = originY + info.nodeY();
            if (nodeX + TREE_NODE_SIZE < x || nodeX > x + canvasWidth || nodeY + TREE_NODE_SIZE < y || nodeY > y + canvasHeight) {
                continue;
            }

            int fillColor = node.complete() ? TREE_NODE_COMPLETE_FILL : TREE_NODE_INCOMPLETE_FILL;
            int outlineColor = node.complete() ? TREE_NODE_BORDER_COMPLETE : TREE_NODE_BORDER_INCOMPLETE;
            graphics.fill(nodeX, nodeY, nodeX + TREE_NODE_SIZE, nodeY + TREE_NODE_SIZE, fillColor);
            graphics.renderOutline(nodeX, nodeY, TREE_NODE_SIZE, TREE_NODE_SIZE, outlineColor);
            graphics.renderItem(node.icon(), nodeX + 2, nodeY + 2);

            if (mouseX >= nodeX && mouseX < nodeX + TREE_NODE_SIZE && mouseY >= nodeY && mouseY < nodeY + TREE_NODE_SIZE) {
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
                hoveredTooltip = nodeTooltip;
            }
        }
        graphics.disableScissor();

        this.treeVerticalScrollbar = new ScrollbarInfo(ScrollbarType.SERVER_TREE_VERTICAL, ScrollbarAxis.VERTICAL, verticalScrollbarX, y, canvasHeight, contentHeight, canvasHeight, this.treeScrollY);
        this.treeHorizontalScrollbar = new ScrollbarInfo(ScrollbarType.SERVER_TREE_HORIZONTAL, ScrollbarAxis.HORIZONTAL, x, horizontalScrollbarY, canvasWidth, contentWidth, canvasWidth, this.treeScrollX);
        this.renderScrollbar(graphics, this.treeVerticalScrollbar, mouseX, mouseY);
        this.renderScrollbar(graphics, this.treeHorizontalScrollbar, mouseX, mouseY);
        this.mouseoverTooltip = hoveredTooltip;
    }

    private void drawClampedHorizontalLine(GuiGraphics graphics, int startX, int endX, int y, int clipX, int clipY, int clipWidth, int clipHeight, int color) {
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

    private void drawClampedVerticalLine(GuiGraphics graphics, int x, int startY, int endY, int clipX, int clipY, int clipWidth, int clipHeight, int color) {
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

    private List<SpaceRaceClientStats.AdvancementIconData> getFilteredAdvancements(List<SpaceRaceClientStats.AdvancementIconData> icons) {
        if (!this.importantProgressionOnly) {
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

    private int getRenderableIconCount(int textStartX, int rightEdgeX, int totalIcons) {
        if (totalIcons <= 0) {
            return 0;
        }

        int minimumNameWidth = 48;
        int availableWidth = rightEdgeX - textStartX - minimumNameWidth - ADVANCEMENT_TEXT_ICON_SPACING;
        if (availableWidth < ADVANCEMENT_ICON_SIZE) {
            return 0;
        }

        int maxIconsByWidth = 1 + (availableWidth - ADVANCEMENT_ICON_SIZE) / (ADVANCEMENT_ICON_SIZE + ADVANCEMENT_ICON_SPACING);
        return Math.max(0, Math.min(totalIcons, maxIconsByWidth));
    }

    private List<Component> buildAdvancementTooltip(SpaceRaceClientStats.AdvancementIconData advancementIcon, boolean includeUnlockers) {
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

    private int getCompactButtonWidth(Component... labels) {
        int width = 0;
        for (Component label : labels) {
            width = Math.max(width, this.font.width(label) + 10);
        }
        return Math.max(64, width);
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
            case SERVER_PLAYER_LIST -> this.playerScroll = value;
            case SERVER_PLAYER_DETAIL -> this.playerDetailScroll = value;
            case SERVER_TEAM_LIST -> this.teamScroll = value;
            case SERVER_TEAM_DETAIL -> this.teamDetailScroll = value;
            case SERVER_TREE_VERTICAL -> this.treeScrollY = value;
            case SERVER_TREE_HORIZONTAL -> this.treeScrollX = value;
            case NONE, GLOBAL_GENERAL, GLOBAL_ITEMS, GLOBAL_MOBS -> {
            }
        }
    }

    private List<ScrollbarInfo> getVisibleScrollbars() {
        List<ScrollbarInfo> scrollbars = new ArrayList<>(3);
        if (this.listScrollbar != null) {
            scrollbars.add(this.listScrollbar);
        }
        if (this.treeVerticalScrollbar != null) {
            scrollbars.add(this.treeVerticalScrollbar);
        }
        if (this.treeHorizontalScrollbar != null) {
            scrollbars.add(this.treeHorizontalScrollbar);
        }
        return scrollbars;
    }

    private ScrollbarInfo getScrollbarForType(ScrollbarType type) {
        return switch (type) {
            case SERVER_PLAYER_LIST, SERVER_PLAYER_DETAIL, SERVER_TEAM_LIST, SERVER_TEAM_DETAIL -> this.listScrollbar;
            case SERVER_TREE_HORIZONTAL -> this.treeHorizontalScrollbar;
            case SERVER_TREE_VERTICAL -> this.treeVerticalScrollbar;
            case NONE, GLOBAL_GENERAL, GLOBAL_ITEMS, GLOBAL_MOBS -> null;
        };
    }
}
