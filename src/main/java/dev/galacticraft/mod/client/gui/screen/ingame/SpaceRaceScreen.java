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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.gui.screen.ingame.spacerace.GlobalStatsSection;
import dev.galacticraft.mod.client.gui.screen.ingame.spacerace.PlayerClickArea;
import dev.galacticraft.mod.client.gui.screen.ingame.spacerace.ScrollbarAxis;
import dev.galacticraft.mod.client.gui.screen.ingame.spacerace.ScrollbarInfo;
import dev.galacticraft.mod.client.gui.screen.ingame.spacerace.ScrollbarType;
import dev.galacticraft.mod.client.gui.screen.ingame.spacerace.ServerStatsMode;
import dev.galacticraft.mod.client.gui.screen.ingame.spacerace.TeamClickArea;
import dev.galacticraft.mod.client.gui.widget.SpaceRaceButton;
import dev.galacticraft.mod.util.Translations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpaceRaceScreen extends Screen {
    static final int STATS_PANEL_PADDING = 8;
    static final int STATS_ENTRY_HEIGHT = 22;
    static final int STATS_ENTRY_SPACING = 2;
    static final int STATS_ENTRY_CONTENT_PADDING = 6;
    static final int OVERVIEW_ENTRY_HEIGHT = 20;
    static final int OVERVIEW_ENTRY_SPACING = 2;
    static final int PLAYER_HEAD_SIZE = 16;
    static final int PLAYER_HEAD_SPACING = 8;
    static final int ADVANCEMENT_ICON_SIZE = 16;
    static final int ADVANCEMENT_ICON_SPACING = 2;
    static final int ADVANCEMENT_TEXT_ICON_SPACING = 6;
    static final int STATS_TOGGLE_BUTTON_WIDTH = 170;
    static final int STATS_TOGGLE_BUTTON_HEIGHT = 16;
    static final int COMPACT_TOGGLE_BUTTON_HEIGHT = 16;
    static final int COMPACT_TOGGLE_BUTTON_SPACING = 4;
    static final int SERVER_BOTTOM_CONTROL_RESERVED = COMPACT_TOGGLE_BUTTON_HEIGHT + 8;
    static final int SERVER_BOTTOM_CONTROL_RESERVED_SERVER_TAB = COMPACT_TOGGLE_BUTTON_HEIGHT + 5;
    static final int SERVER_MODE_BUTTON_HEIGHT = 16;
    static final int SERVER_MODE_BUTTON_SPACING = 4;
    static final int ADVANCEMENT_ROW_HEIGHT = 20;
    static final int ADVANCEMENT_ROW_SPACING = 3;
    static final int TREE_NODE_SIZE = 20;
    static final int TREE_NODE_SPACING_X = 30;
    static final int TREE_NODE_SPACING_Y = 26;
    static final int TREE_CANVAS_PADDING = 24;
    static final int TREE_LINE_COLOR = 0xAA5A5A5A;
    static final int TREE_NODE_INCOMPLETE_FILL = 0x55272727;
    static final int TREE_NODE_COMPLETE_FILL = 0x553E6231;
    static final int TREE_NODE_BORDER_INCOMPLETE = 0xAA666666;
    static final int TREE_NODE_BORDER_COMPLETE = 0xAA96C56B;
    static final int GLOBAL_TAB_SPACING = 6;
    static final int GLOBAL_TAB_HEIGHT = 20;
    static final int GLOBAL_TAB_BOTTOM_PADDING = 6;
    static final int GLOBAL_CONTENT_SPACING = 6;
    static final int GLOBAL_GENERAL_ROW_HEIGHT = 18;
    static final int GLOBAL_GENERAL_TOTAL_WIDTH = 54;
    static final int GLOBAL_MOBS_ROW_HEIGHT = 18;
    static final int GLOBAL_ITEMS_HEADER_HEIGHT = 20;
    static final int GLOBAL_ITEMS_ROW_HEIGHT = 24;
    static final int GLOBAL_ITEMS_ICON_COLUMN_WIDTH = 22;
    static final int GLOBAL_ITEMS_MIN_CELL_WIDTH = 48;
    static final int GLOBAL_ITEMS_MAX_CELL_WIDTH = 72;
    static final float GLOBAL_TEXT_SCALE = 1.0F;
    static final int GLOBAL_HEAD_SIZE = 12;
    static final int GLOBAL_ITEM_ICON_SIZE = 16;
    static final int SCROLLBAR_WIDTH = 6;
    static final int SCROLLBAR_SPACING = 2;
    static final int SCROLLBAR_MIN_THUMB_HEIGHT = 14;

    int backgroundWidth = 0;
    int backgroundHeight = 0;
    SpaceRaceMenu menu = SpaceRaceMenu.MAIN;
    EditBox teamNameInput;
    boolean animationCompleted = false;
    int teamColor = 0xFF000000;
    final ResourceLocation teamFlag = Constant.id("team_name/id_here");
    Button serverVisibilityButton;
    Button serverHideAdvancementsButton;
    Button serverPlayerTabButton;
    Button serverTeamTabButton;
    Button serverTreeTabButton;
    Button globalVisibilityButton;
    Button globalGeneralTabButton;
    Button globalItemsTabButton;
    Button globalMobsTabButton;
    ServerStatsMode serverStatsMode = ServerStatsMode.PLAYER;
    UUID selectedServerPlayerId;
    String selectedServerTeamId;
    boolean importantProgressionOnly;
    final List<PlayerClickArea> playerClickAreas = new ArrayList<>();
    final List<TeamClickArea> teamClickAreas = new ArrayList<>();
    GlobalStatsSection globalStatsSection = GlobalStatsSection.GENERAL;
    int serverPlayerScroll = 0;
    int serverPlayerDetailScroll = 0;
    int serverTeamScroll = 0;
    int serverTeamDetailScroll = 0;
    int serverTreeScrollX = 0;
    int serverTreeScrollY = 0;
    boolean centerServerTreeVerticalOnNextRender = true;
    int globalGeneralScroll = 0;
    int globalItemsScroll = 0;
    int globalMobsScroll = 0;
    ScrollbarInfo serverScrollbar;
    ScrollbarInfo serverTreeVerticalScrollbar;
    ScrollbarInfo serverTreeHorizontalScrollbar;
    ScrollbarInfo globalGeneralScrollbar;
    ScrollbarInfo globalItemsScrollbar;
    ScrollbarInfo globalMobsScrollbar;
    ScrollbarType activeScrollbar = ScrollbarType.NONE;
    int activeScrollbarThumbOffset = 0;

    public SpaceRaceScreen() {
        super(Component.translatable(Translations.SpaceRace.SPACE_RACE_MANAGER));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.serverVisibilityButton != null) {
            this.serverVisibilityButton.setMessage(SpaceRaceServerStatsSection.getProgressionButtonText(this));
        }
        if (this.serverHideAdvancementsButton != null) {
            this.serverHideAdvancementsButton.setMessage(SpaceRaceServerStatsSection.getVisibilityButtonText());
        }
        if (this.globalVisibilityButton != null) {
            this.globalVisibilityButton.setMessage(SpaceRaceGlobalStatsSection.getVisibilityButtonText());
        }
        SpaceRaceServerStatsSection.updateModeButtons(this);
        SpaceRaceGlobalStatsSection.updateTabButtonState(this);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!this.animationCompleted || (this.menu != SpaceRaceMenu.GLOBAL_STATS && this.menu != SpaceRaceMenu.SERVER_STATS)) {
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
        if (this.menu == SpaceRaceMenu.SERVER_STATS) {
            if (this.serverStatsMode == ServerStatsMode.SERVER) {
                boolean horizontalScroll = Screen.hasShiftDown() || Math.abs(horizontalAmount) > Math.abs(verticalAmount);
                if (horizontalScroll) {
                    this.serverTreeScrollX = Math.max(0, this.serverTreeScrollX - delta * 18);
                } else {
                    this.serverTreeScrollY = Math.max(0, this.serverTreeScrollY - delta * 18);
                }
            } else {
                SpaceRaceServerStatsSection.setActiveListScroll(this, Math.max(0, SpaceRaceServerStatsSection.getActiveListScroll(this) - delta));
            }
            return true;
        }

        switch (this.globalStatsSection) {
            case GENERAL -> this.globalGeneralScroll = Math.max(0, this.globalGeneralScroll - delta);
            case ITEMS -> this.globalItemsScroll = Math.max(0, this.globalItemsScroll - delta);
            case MOBS -> this.globalMobsScroll = Math.max(0, this.globalMobsScroll - delta);
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.animationCompleted) {
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

            if (super.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }

            if (this.menu == SpaceRaceMenu.SERVER_STATS && SpaceRaceServerStatsSection.handleClick(this, mouseX, mouseY)) {
                return true;
            }
            return false;
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
            case SERVER_PLAYER_LIST -> this.serverPlayerScroll = Math.max(0, value);
            case SERVER_PLAYER_DETAIL -> this.serverPlayerDetailScroll = Math.max(0, value);
            case SERVER_TEAM_LIST -> this.serverTeamScroll = Math.max(0, value);
            case SERVER_TEAM_DETAIL -> this.serverTeamDetailScroll = Math.max(0, value);
            case SERVER_TREE_HORIZONTAL -> this.serverTreeScrollX = Math.max(0, value);
            case SERVER_TREE_VERTICAL -> this.serverTreeScrollY = Math.max(0, value);
            case GLOBAL_GENERAL -> this.globalGeneralScroll = Math.max(0, value);
            case GLOBAL_ITEMS -> this.globalItemsScroll = Math.max(0, value);
            case GLOBAL_MOBS -> this.globalMobsScroll = Math.max(0, value);
            case NONE -> {
            }
        }
    }

    private List<ScrollbarInfo> getVisibleScrollbars() {
        List<ScrollbarInfo> scrollbars = new ArrayList<>(2);
        if (this.menu == SpaceRaceMenu.SERVER_STATS) {
            if (this.serverScrollbar != null) {
                scrollbars.add(this.serverScrollbar);
            }
            if (this.serverTreeVerticalScrollbar != null) {
                scrollbars.add(this.serverTreeVerticalScrollbar);
            }
            if (this.serverTreeHorizontalScrollbar != null) {
                scrollbars.add(this.serverTreeHorizontalScrollbar);
            }
            return scrollbars;
        }
        if (this.menu != SpaceRaceMenu.GLOBAL_STATS) {
            return scrollbars;
        }

        ScrollbarInfo globalScrollbar = switch (this.globalStatsSection) {
            case GENERAL -> this.globalGeneralScrollbar;
            case ITEMS -> this.globalItemsScrollbar;
            case MOBS -> this.globalMobsScrollbar;
        };
        if (globalScrollbar != null) {
            scrollbars.add(globalScrollbar);
        }
        return scrollbars;
    }

    private ScrollbarInfo getScrollbarForType(ScrollbarType type) {
        return switch (type) {
            case SERVER_PLAYER_LIST, SERVER_PLAYER_DETAIL, SERVER_TEAM_LIST, SERVER_TEAM_DETAIL -> this.serverScrollbar;
            case SERVER_TREE_HORIZONTAL -> this.serverTreeHorizontalScrollbar;
            case SERVER_TREE_VERTICAL -> this.serverTreeVerticalScrollbar;
            case GLOBAL_GENERAL -> this.globalGeneralScrollbar;
            case GLOBAL_ITEMS -> this.globalItemsScrollbar;
            case GLOBAL_MOBS -> this.globalMobsScrollbar;
            case NONE -> null;
        };
    }

    @Override
    protected void init() {
        super.init();
        this.createMenu(this.menu);
    }

    @Override
    public void resize(Minecraft client, int width, int height) {
        this.backgroundWidth = (int) (width - ((this.getMarginPercent() * width) * 1.5D));
        this.backgroundHeight = (int) (height - ((this.getMarginPercent() * height) * 1.5D));
        super.resize(client, width, height);
    }

    SpaceRaceButton addButton(Component text, int x, int y, int width, int height, Button.OnPress onPress) {
        return this.addRenderableWidget(new SpaceRaceButton(text, x, y, width, height, onPress));
    }

    <T extends GuiEventListener & Renderable & NarratableEntry> T addScreenWidget(T widget) {
        return this.addRenderableWidget(widget);
    }

    <T extends Renderable> T addScreenRenderable(T renderable) {
        return this.addRenderableOnly(renderable);
    }

    Font fontRenderer() {
        return this.font;
    }

    Minecraft minecraftClient() {
        return this.minecraft;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int maxWidth = (int) (this.width - (this.getXMargins() * 1.5D));
        if (this.backgroundWidth < maxWidth) {
            this.backgroundWidth += (int) Math.min(60 * delta, maxWidth - this.backgroundWidth);
        }

        int maxHeight = (int) (this.height - (this.getYMargins() * 1.5D));
        if (this.backgroundHeight < maxHeight) {
            this.backgroundHeight += (int) Math.min(40 * delta, maxHeight - this.backgroundHeight);
        }

        if (!this.animationCompleted && this.isAnimationComplete()) {
            this.repositionElements();
            this.animationCompleted = true;
        }

        graphics.fill(this.getLeft(), this.getTop(), this.getLeft() + this.backgroundWidth, this.getTop() + this.backgroundHeight, 0x80000000);
    }

    private void renderForeground(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawCenteredString(this.font, Component.translatable(Translations.SpaceRace.SPACE_RACE_MANAGER), this.width / 2, this.getTop() - 20, 0xFFFFFF);
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, float delta) {
        if (this.animationCompleted) {
            this.serverScrollbar = null;
            this.serverTreeVerticalScrollbar = null;
            this.serverTreeHorizontalScrollbar = null;
            this.globalGeneralScrollbar = null;
            this.globalItemsScrollbar = null;
            this.globalMobsScrollbar = null;
            super.render(graphics, x, y, delta);
            this.renderForeground(graphics, x, y);
            this.drawMouseoverTooltip(graphics, x, y);
        } else {
            this.renderBackground(graphics, x, y, delta);
        }
    }

    private void drawMouseoverTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
    }

    int getBottom() {
        return this.getTop() + this.backgroundHeight;
    }

    int getLeft() {
        return (this.width / 2) - (this.backgroundWidth / 2);
    }

    int getTop() {
        return (this.height / 2) - (this.backgroundHeight / 2);
    }

    int getRight() {
        return this.getLeft() + this.backgroundWidth;
    }

    private float getMarginPercent() {
        return 0.17F;
    }

    void setMenu(SpaceRaceMenu menu) {
        this.menu = menu;
        this.createMenu(menu);
    }

    private void createMenu(SpaceRaceMenu menu) {
        this.clearWidgets();
        this.clearFocus();
        this.activeScrollbar = ScrollbarType.NONE;
        this.serverScrollbar = null;
        this.serverTreeVerticalScrollbar = null;
        this.serverTreeHorizontalScrollbar = null;
        this.globalGeneralScrollbar = null;
        this.globalItemsScrollbar = null;
        this.globalMobsScrollbar = null;
        this.playerClickAreas.clear();
        this.teamClickAreas.clear();
        switch (menu) {
            case MAIN -> SpaceRaceScreenMenus.showMainMenu(this);
            case ADD_PLAYERS -> SpaceRaceScreenMenus.showAddPlayersMenu(this);
            case REMOVE_PLAYERS -> SpaceRaceScreenMenus.showRemovePlayersMenu(this);
            case SERVER_STATS -> SpaceRaceServerStatsSection.initializeMenu(this);
            case GLOBAL_STATS -> SpaceRaceGlobalStatsSection.initializeMenu(this);
            case TEAM_FLAG -> SpaceRaceScreenMenus.showTeamFlagMenu(this);
            case TEAM_COLOR -> SpaceRaceScreenMenus.showTeamColorMenu(this);
        }
    }

    private boolean isAnimationComplete() {
        int maxWidth = (int) (this.width - (this.getXMargins() * 1.5D));
        int maxHeight = (int) (this.height - (this.getYMargins() * 1.5D));

        return this.backgroundWidth >= maxWidth && this.backgroundHeight >= maxHeight;
    }

    private int getYMargins() {
        return (int) (this.height * this.getMarginPercent());
    }

    private int getXMargins() {
        return (int) (this.width * this.getMarginPercent());
    }

    @Override
    public void onFilesDrop(List<Path> paths) {
        SpaceRaceFlagUploadHandler.handleFilesDrop(this, paths);
    }
}
