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

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.impl.network.c2s.FlagDataPayload;
import dev.galacticraft.impl.network.c2s.RequestSpaceRaceStatsPayload;
import dev.galacticraft.impl.network.c2s.TeamNamePayload;
import dev.galacticraft.impl.network.c2s.UpdateSpaceRaceVisibilityPayload;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.spacerace.SpaceRaceClientStats;
import dev.galacticraft.mod.util.Translations;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class SpaceRaceScreen extends Screen {
    private static final int STATS_PANEL_PADDING = 8;
    private static final int STATS_ENTRY_HEIGHT = 22;
    private static final int STATS_ENTRY_SPACING = 2;
    private static final int STATS_ENTRY_CONTENT_PADDING = 6;
    private static final int OVERVIEW_ENTRY_HEIGHT = 20;
    private static final int OVERVIEW_ENTRY_SPACING = 2;
    private static final int PLAYER_HEAD_SIZE = 16;
    private static final int PLAYER_HEAD_SPACING = 8;
    private static final int ADVANCEMENT_ICON_SIZE = 16;
    private static final int ADVANCEMENT_ICON_SPACING = 2;
    private static final int ADVANCEMENT_TEXT_ICON_SPACING = 6;
    private static final int STATS_TOGGLE_BUTTON_WIDTH = 170;
    private static final int STATS_TOGGLE_BUTTON_HEIGHT = 16;
    private static final int COMPACT_TOGGLE_BUTTON_HEIGHT = 12;
    private static final int COMPACT_TOGGLE_BUTTON_SPACING = 4;
    private static final float COMPACT_BUTTON_TEXT_SCALE = 0.75F;
    private static final int SERVER_BOTTOM_CONTROL_RESERVED = COMPACT_TOGGLE_BUTTON_HEIGHT + 8;
    private static final int SERVER_BOTTOM_CONTROL_RESERVED_SERVER_TAB = COMPACT_TOGGLE_BUTTON_HEIGHT + 5;
    private static final int SERVER_MODE_BUTTON_HEIGHT = 16;
    private static final int SERVER_MODE_BUTTON_SPACING = 4;
    private static final int ADVANCEMENT_ROW_HEIGHT = 20;
    private static final int ADVANCEMENT_ROW_SPACING = 3;
    private static final int TREE_NODE_SIZE = 20;
    private static final int TREE_NODE_SPACING_X = 30;
    private static final int TREE_NODE_SPACING_Y = 26;
    private static final int TREE_CANVAS_PADDING = 24;
    private static final int TREE_LINE_COLOR = 0xAA5A5A5A;
    private static final int TREE_NODE_INCOMPLETE_FILL = 0x55272727;
    private static final int TREE_NODE_COMPLETE_FILL = 0x553E6231;
    private static final int TREE_NODE_BORDER_INCOMPLETE = 0xAA666666;
    private static final int TREE_NODE_BORDER_COMPLETE = 0xAA96C56B;
    private static final int GLOBAL_TAB_SPACING = 6;
    private static final int GLOBAL_TAB_HEIGHT = 20;
    private static final int GLOBAL_TAB_BOTTOM_PADDING = 6;
    private static final int GLOBAL_CONTENT_SPACING = 6;
    private static final int GLOBAL_GENERAL_ROW_HEIGHT = 18;
    private static final int GLOBAL_GENERAL_TOTAL_WIDTH = 54;
    private static final int GLOBAL_MOBS_ROW_HEIGHT = 18;
    private static final int GLOBAL_ITEMS_HEADER_HEIGHT = 20;
    private static final int GLOBAL_ITEMS_ROW_HEIGHT = 24;
    private static final int GLOBAL_ITEMS_ICON_COLUMN_WIDTH = 22;
    private static final int GLOBAL_ITEMS_MIN_CELL_WIDTH = 48;
    private static final int GLOBAL_ITEMS_MAX_CELL_WIDTH = 72;
    private static final float GLOBAL_TEXT_SCALE = 0.75F;
    private static final int GLOBAL_HEAD_SIZE = 12;
    private static final int GLOBAL_ITEM_ICON_SIZE = 12;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_SPACING = 2;
    private static final int SCROLLBAR_MIN_THUMB_HEIGHT = 14;

    private int backgroundWidth = 0;
    private int backgroundHeight = 0;
    private Menu menu = Menu.MAIN;
    private EditBox teamNameInput;
    private boolean animationCompleted = false;
    private int teamColor = 0xFF000000;
    private final ResourceLocation teamFlag = Constant.id("team_name/id_here");
    private Button serverVisibilityButton;
    private Button serverHideAdvancementsButton;
    private Button serverPlayerTabButton;
    private Button serverTeamTabButton;
    private Button serverTreeTabButton;
    private Button globalVisibilityButton;
    private Button globalGeneralTabButton;
    private Button globalItemsTabButton;
    private Button globalMobsTabButton;
    private ServerStatsMode serverStatsMode = ServerStatsMode.PLAYER;
    private UUID selectedServerPlayerId;
    private String selectedServerTeamId;
    private boolean importantProgressionOnly;
    private final List<PlayerClickArea> playerClickAreas = new ArrayList<>();
    private final List<TeamClickArea> teamClickAreas = new ArrayList<>();
    private GlobalStatsSection globalStatsSection = GlobalStatsSection.GENERAL;
    private int serverPlayerScroll = 0;
    private int serverPlayerDetailScroll = 0;
    private int serverTeamScroll = 0;
    private int serverTeamDetailScroll = 0;
    private int serverTreeScrollX = 0;
    private int serverTreeScrollY = 0;
    private boolean centerServerTreeVerticalOnNextRender = true;
    private int globalGeneralScroll = 0;
    private int globalItemsScroll = 0;
    private int globalMobsScroll = 0;
    private ScrollbarInfo serverScrollbar;
    private ScrollbarInfo serverTreeVerticalScrollbar;
    private ScrollbarInfo serverTreeHorizontalScrollbar;
    private ScrollbarInfo globalGeneralScrollbar;
    private ScrollbarInfo globalItemsScrollbar;
    private ScrollbarInfo globalMobsScrollbar;
    private ScrollbarType activeScrollbar = ScrollbarType.NONE;
    private int activeScrollbarThumbOffset = 0;

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
            this.serverVisibilityButton.setMessage(this.getServerProgressionButtonText());
        }
        if (this.serverHideAdvancementsButton != null) {
            this.serverHideAdvancementsButton.setMessage(this.getServerVisibilityButtonText());
        }
        if (this.globalVisibilityButton != null) {
            this.globalVisibilityButton.setMessage(this.getGlobalVisibilityButtonText());
        }
        this.updateServerModeButtons();
        this.updateGlobalTabButtonState();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!this.animationCompleted || (this.menu != Menu.GLOBAL_STATS && this.menu != Menu.SERVER_STATS)) {
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
        if (this.menu == Menu.SERVER_STATS) {
            if (this.serverStatsMode == ServerStatsMode.SERVER) {
                boolean horizontalScroll = Screen.hasShiftDown() || Math.abs(horizontalAmount) > Math.abs(verticalAmount);
                if (horizontalScroll) {
                    this.serverTreeScrollX = Math.max(0, this.serverTreeScrollX - delta * 18);
                } else {
                    this.serverTreeScrollY = Math.max(0, this.serverTreeScrollY - delta * 18);
                }
            } else {
                this.setActiveServerListScroll(Math.max(0, this.getActiveServerListScroll() - delta));
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

            if (this.menu == Menu.SERVER_STATS && this.handleServerStatsClick(mouseX, mouseY)) {
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
        if (this.menu == Menu.SERVER_STATS) {
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
        if (this.menu != Menu.GLOBAL_STATS) {
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

    protected void mainMenu() {
        this.serverVisibilityButton = null;
        this.serverHideAdvancementsButton = null;
        this.serverPlayerTabButton = null;
        this.serverTeamTabButton = null;
        this.serverTreeTabButton = null;
        this.globalVisibilityButton = null;
        this.globalGeneralTabButton = null;
        this.globalItemsTabButton = null;
        this.globalMobsTabButton = null;

        addButton(Component.translatable(Translations.SpaceRace.EXIT), this.getLeft() + 5, this.getTop() + 5, 40, 14, button -> this.onClose());
        addButton(Component.translatable(Translations.SpaceRace.ADD_PLAYERS), this.getLeft() + 10, this.getBottom() - 85, 100, 30, button -> setMenu(Menu.ADD_PLAYERS));
        addButton(Component.translatable(Translations.SpaceRace.REMOVE_PLAYERS), this.getLeft() + 10, this.getBottom() - 45, 100, 30, button -> setMenu(Menu.REMOVE_PLAYERS));
        addButton(Component.translatable(Translations.SpaceRace.SERVER_STATS), this.getRight() - 100 - 10, this.getBottom() - 85, 100, 30, button -> setMenu(Menu.SERVER_STATS));
        addButton(Component.translatable(Translations.SpaceRace.GLOBAL_STATS), this.getRight() - 100 - 10, this.getBottom() - 45, 100, 30, button -> setMenu(Menu.GLOBAL_STATS));

        int flagButtonWidth = 96;
        int flagButtonHeight = 64;
        int flagButtonX = this.width / 2 - flagButtonWidth / 2;
        int flagButtonY = this.getTop() + 10;
        this.addRenderableWidget(new CustomizeFlagButton(flagButtonX, flagButtonY, flagButtonWidth, flagButtonHeight, this.teamFlag, () -> setMenu(Menu.TEAM_FLAG)));
        this.addRenderableWidget(new TeamColorButton(flagButtonX + flagButtonWidth + 10, flagButtonY + flagButtonHeight / 2 - 45 / 2, 45, 45));
        this.addRenderableWidget(this.teamNameInput = new EditBox(this.font, this.getLeft() + (this.backgroundWidth / 2) - 64, flagButtonY + 70, 128, 15, this.teamNameInput, Component.empty()) {
            private String prevText;

            @Override
            public void setFocused(boolean focused) {
                if (this.isFocused() != focused) {
                    if (focused) {
                        this.prevText = this.getValue();
                    } else if (this.prevText == null || !this.prevText.equals(this.getValue())) {
                        ClientPlayNetworking.send(new TeamNamePayload(this.getValue()));
                    }
                }
                super.setFocused(focused);
            }
        });
    }

    protected void addPlayersMenu() {
        this.serverVisibilityButton = null;
        this.serverHideAdvancementsButton = null;
        this.serverPlayerTabButton = null;
        this.serverTeamTabButton = null;
        this.serverTreeTabButton = null;
        this.globalVisibilityButton = null;
        this.globalGeneralTabButton = null;
        this.globalItemsTabButton = null;
        this.globalMobsTabButton = null;
        addBackButton();
    }

    protected void removePlayersMenu() {
        this.serverVisibilityButton = null;
        this.serverHideAdvancementsButton = null;
        this.serverPlayerTabButton = null;
        this.serverTeamTabButton = null;
        this.serverTreeTabButton = null;
        this.globalVisibilityButton = null;
        this.globalGeneralTabButton = null;
        this.globalItemsTabButton = null;
        this.globalMobsTabButton = null;
        addBackButton();
    }

    protected void serverStatsMenu() {
        this.serverVisibilityButton = null;
        this.serverHideAdvancementsButton = null;
        this.serverPlayerTabButton = null;
        this.serverTeamTabButton = null;
        this.serverTreeTabButton = null;
        this.globalVisibilityButton = null;
        this.globalGeneralTabButton = null;
        this.globalItemsTabButton = null;
        this.globalMobsTabButton = null;
        this.serverStatsMode = ServerStatsMode.PLAYER;
        this.selectedServerPlayerId = null;
        this.selectedServerTeamId = null;
        this.serverPlayerScroll = 0;
        this.serverPlayerDetailScroll = 0;
        this.serverTeamScroll = 0;
        this.serverTeamDetailScroll = 0;
        this.serverTreeScrollX = 0;
        this.serverTreeScrollY = 0;
        this.centerServerTreeVerticalOnNextRender = true;
        addButton(Component.translatable(Translations.SpaceRace.BACK), this.getLeft() + 5, this.getTop() + 5, 40, 14, button -> this.handleServerStatsBack());

        ClientPlayNetworking.send(RequestSpaceRaceStatsPayload.INSTANCE);
        int panelX = this.getLeft() + 10;
        int panelWidth = this.backgroundWidth - 20;
        int panelRight = panelX + panelWidth;
        int contentX = panelX + STATS_PANEL_PADDING;
        int modeStartX = Math.max(contentX, this.getLeft() + 5 + 40 + SERVER_MODE_BUTTON_SPACING);
        int modeButtonAreaWidth = Math.max(0, panelRight - STATS_PANEL_PADDING - modeStartX);
        int modeButtonWidth = Math.max(1, (modeButtonAreaWidth - SERVER_MODE_BUTTON_SPACING * 2) / 3);
        int modeButtonY = this.getTop() + 5;
        this.serverPlayerTabButton = addButton(Component.literal("Player"), modeStartX, modeButtonY, modeButtonWidth, SERVER_MODE_BUTTON_HEIGHT, button -> this.setServerStatsMode(ServerStatsMode.PLAYER));
        this.serverTeamTabButton = addButton(Component.literal("Team"), modeStartX + modeButtonWidth + SERVER_MODE_BUTTON_SPACING, modeButtonY, modeButtonWidth, SERVER_MODE_BUTTON_HEIGHT, button -> this.setServerStatsMode(ServerStatsMode.TEAM));
        this.serverTreeTabButton = addButton(Component.literal("Server"), modeStartX + (modeButtonWidth + SERVER_MODE_BUTTON_SPACING) * 2, modeButtonY, modeButtonWidth, SERVER_MODE_BUTTON_HEIGHT, button -> this.setServerStatsMode(ServerStatsMode.SERVER));
        this.updateServerModeButtons();

        int hideButtonWidth = this.getCompactButtonWidth(
                Component.literal("Hide my advancements"),
                Component.literal("Show my advancements")
        );
        int progressionButtonWidth = this.getCompactButtonWidth(
                Component.literal("Show simple progression"),
                Component.literal("Show full progression")
        );

        this.serverHideAdvancementsButton = this.addRenderableWidget(new CompactTextButton(
                this.getServerVisibilityButtonText(),
                0,
                0,
                hideButtonWidth,
                COMPACT_TOGGLE_BUTTON_HEIGHT,
                button -> this.toggleServerAdvancementVisibility()
        ));
        this.serverVisibilityButton = this.addRenderableWidget(new CompactTextButton(
                this.getServerProgressionButtonText(),
                0,
                0,
                progressionButtonWidth,
                COMPACT_TOGGLE_BUTTON_HEIGHT,
                button -> this.toggleImportantProgression()
        ));
        this.layoutServerBottomButtons();

        this.addRenderableOnly((graphics, mouseX, mouseY, delta) -> this.renderServerStatsPanel(graphics, mouseX, mouseY));
    }

    protected void globalStatsMenu() {
        this.serverVisibilityButton = null;
        this.serverHideAdvancementsButton = null;
        this.serverPlayerTabButton = null;
        this.serverTeamTabButton = null;
        this.serverTreeTabButton = null;
        this.globalGeneralTabButton = null;
        this.globalItemsTabButton = null;
        this.globalMobsTabButton = null;
        addBackButton();
        this.globalGeneralScroll = 0;
        this.globalItemsScroll = 0;
        this.globalMobsScroll = 0;
        this.globalStatsSection = GlobalStatsSection.GENERAL;

        ClientPlayNetworking.send(RequestSpaceRaceStatsPayload.INSTANCE);
        this.globalVisibilityButton = this.addRenderableWidget(new SpaceRaceButton(
                this.getGlobalVisibilityButtonText(),
                this.getRight() - STATS_TOGGLE_BUTTON_WIDTH - 10,
                this.getTop() + 5,
                STATS_TOGGLE_BUTTON_WIDTH,
                STATS_TOGGLE_BUTTON_HEIGHT,
                button -> this.toggleGlobalLeaderboardVisibility()
        ));

        int panelX = this.getLeft() + 10;
        int panelWidth = this.backgroundWidth - 20;
        int contentX = panelX + STATS_PANEL_PADDING;
        int contentWidth = panelWidth - STATS_PANEL_PADDING * 2;
        int tabWidth = Math.max(60, (contentWidth - GLOBAL_TAB_SPACING * 2) / 3);
        int tabY = this.getBottom() - STATS_PANEL_PADDING - GLOBAL_TAB_HEIGHT - GLOBAL_TAB_BOTTOM_PADDING;
        this.globalGeneralTabButton = addButton(Component.literal("General"), contentX, tabY, tabWidth, GLOBAL_TAB_HEIGHT, button -> setGlobalStatsSection(GlobalStatsSection.GENERAL));
        this.globalItemsTabButton = addButton(Component.literal("Items"), contentX + tabWidth + GLOBAL_TAB_SPACING, tabY, tabWidth, GLOBAL_TAB_HEIGHT, button -> setGlobalStatsSection(GlobalStatsSection.ITEMS));
        this.globalMobsTabButton = addButton(Component.literal("Mobs"), contentX + (tabWidth + GLOBAL_TAB_SPACING) * 2, tabY, tabWidth, GLOBAL_TAB_HEIGHT, button -> setGlobalStatsSection(GlobalStatsSection.MOBS));
        this.updateGlobalTabButtonState();

        this.addRenderableOnly((graphics, mouseX, mouseY, delta) -> this.renderGlobalStatsPanel(graphics, mouseX, mouseY));
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

    private void toggleGlobalLeaderboardVisibility() {
        boolean hideServerAdvancements = SpaceRaceClientStats.isHideServerAdvancements();
        boolean hideGlobalLeaderboard = !SpaceRaceClientStats.isHideGlobalLeaderboard();
        SpaceRaceClientStats.setVisibility(hideServerAdvancements, hideGlobalLeaderboard);
        ClientPlayNetworking.send(new UpdateSpaceRaceVisibilityPayload(hideServerAdvancements, hideGlobalLeaderboard));
    }

    private Component getServerProgressionButtonText() {
        if (this.importantProgressionOnly) {
            return Component.literal("Show full progression");
        }
        return Component.literal("Show simple progression");
    }

    private Component getServerVisibilityButtonText() {
        if (SpaceRaceClientStats.isHideServerAdvancements()) {
            return Component.literal("Show my advancements");
        }
        return Component.literal("Hide my advancements");
    }

    private Component getGlobalVisibilityButtonText() {
        if (SpaceRaceClientStats.isHideGlobalLeaderboard()) {
            return Component.literal("Show me on leaderboard");
        }
        return Component.literal("Hide me from leaderboard");
    }

    private void setGlobalStatsSection(GlobalStatsSection section) {
        this.globalStatsSection = section;
        this.updateGlobalTabButtonState();
    }

    private void setServerStatsMode(ServerStatsMode mode) {
        if (this.serverStatsMode == mode) {
            return;
        }
        this.serverStatsMode = mode;
        this.selectedServerPlayerId = null;
        this.selectedServerTeamId = null;
        this.serverScrollbar = null;
        this.serverTreeVerticalScrollbar = null;
        this.serverTreeHorizontalScrollbar = null;
        this.activeScrollbar = ScrollbarType.NONE;
        if (mode == ServerStatsMode.SERVER) {
            this.centerServerTreeVerticalOnNextRender = true;
        }
        this.updateServerModeButtons();
    }

    private void updateServerModeButtons() {
        if (this.serverPlayerTabButton != null) {
            this.serverPlayerTabButton.active = this.serverStatsMode != ServerStatsMode.PLAYER;
        }
        if (this.serverTeamTabButton != null) {
            this.serverTeamTabButton.active = this.serverStatsMode != ServerStatsMode.TEAM;
        }
        if (this.serverTreeTabButton != null) {
            this.serverTreeTabButton.active = this.serverStatsMode != ServerStatsMode.SERVER;
        }
        this.layoutServerBottomButtons();
    }

    private void layoutServerBottomButtons() {
        if (this.menu != Menu.SERVER_STATS) {
            return;
        }

        int panelX = this.getLeft() + 10;
        int panelWidth = this.backgroundWidth - 20;
        int panelRight = panelX + panelWidth;
        int buttonRight = panelRight - STATS_PANEL_PADDING;
        int buttonY = this.getBottom() - COMPACT_TOGGLE_BUTTON_HEIGHT - 4;

        int hideButtonWidth = this.getCompactButtonWidth(
                Component.literal("Hide my advancements"),
                Component.literal("Show my advancements")
        );
        if (this.serverHideAdvancementsButton != null) {
            this.serverHideAdvancementsButton.setX(buttonRight - hideButtonWidth);
            this.serverHideAdvancementsButton.setY(buttonY);
            this.serverHideAdvancementsButton.visible = true;
            this.serverHideAdvancementsButton.active = true;
        }

        boolean showProgressionButton = this.serverStatsMode != ServerStatsMode.SERVER;
        if (this.serverVisibilityButton != null) {
            int progressionButtonWidth = this.getCompactButtonWidth(
                    Component.literal("Show simple progression"),
                    Component.literal("Show full progression")
            );
            this.serverVisibilityButton.setX(buttonRight - hideButtonWidth - COMPACT_TOGGLE_BUTTON_SPACING - progressionButtonWidth);
            this.serverVisibilityButton.setY(buttonY);
            this.serverVisibilityButton.visible = showProgressionButton;
            this.serverVisibilityButton.active = showProgressionButton;
        }
    }

    private int getServerBottomControlsReserved() {
        if (this.serverStatsMode == ServerStatsMode.SERVER) {
            return SERVER_BOTTOM_CONTROL_RESERVED_SERVER_TAB;
        }
        return SERVER_BOTTOM_CONTROL_RESERVED;
    }

    private void handleServerStatsBack() {
        if (this.serverStatsMode == ServerStatsMode.PLAYER && this.selectedServerPlayerId != null) {
            this.selectedServerPlayerId = null;
            this.serverPlayerDetailScroll = 0;
            this.serverScrollbar = null;
            return;
        }
        if (this.serverStatsMode == ServerStatsMode.TEAM && this.selectedServerTeamId != null) {
            this.selectedServerTeamId = null;
            this.serverTeamDetailScroll = 0;
            this.serverScrollbar = null;
            return;
        }
        setMenu(Menu.MAIN);
    }

    private int getActiveServerListScroll() {
        if (this.serverStatsMode == ServerStatsMode.PLAYER) {
            return this.selectedServerPlayerId == null ? this.serverPlayerScroll : this.serverPlayerDetailScroll;
        }
        if (this.serverStatsMode == ServerStatsMode.TEAM) {
            return this.selectedServerTeamId == null ? this.serverTeamScroll : this.serverTeamDetailScroll;
        }
        return this.serverTreeScrollY;
    }

    private void setActiveServerListScroll(int value) {
        if (this.serverStatsMode == ServerStatsMode.PLAYER) {
            if (this.selectedServerPlayerId == null) {
                this.serverPlayerScroll = value;
            } else {
                this.serverPlayerDetailScroll = value;
            }
            return;
        }
        if (this.serverStatsMode == ServerStatsMode.TEAM) {
            if (this.selectedServerTeamId == null) {
                this.serverTeamScroll = value;
            } else {
                this.serverTeamDetailScroll = value;
            }
            return;
        }
        this.serverTreeScrollY = value;
    }

    private void updateGlobalTabButtonState() {
        if (this.globalGeneralTabButton != null) {
            this.globalGeneralTabButton.active = this.globalStatsSection != GlobalStatsSection.GENERAL;
        }
        if (this.globalItemsTabButton != null) {
            this.globalItemsTabButton.active = this.globalStatsSection != GlobalStatsSection.ITEMS;
        }
        if (this.globalMobsTabButton != null) {
            this.globalMobsTabButton.active = this.globalStatsSection != GlobalStatsSection.MOBS;
        }
    }

    private void renderServerStatsPanel(GuiGraphics graphics, int mouseX, int mouseY) {
        int panelX = this.getLeft() + 10;
        int panelY = this.getTop() + 24;
        int panelWidth = this.backgroundWidth - 20;
        int panelHeight = this.backgroundHeight - 30 - this.getServerBottomControlsReserved();

        graphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0x66000000);
        graphics.renderOutline(panelX, panelY, panelWidth, panelHeight, 0xAA2D2D2D);

        this.playerClickAreas.clear();
        this.teamClickAreas.clear();
        this.serverScrollbar = null;
        this.serverTreeVerticalScrollbar = null;
        this.serverTreeHorizontalScrollbar = null;

        int contentX = panelX + STATS_PANEL_PADDING;
        int contentY = panelY + STATS_PANEL_PADDING;
        int contentWidth = panelWidth - STATS_PANEL_PADDING * 2;
        String header = switch (this.serverStatsMode) {
            case PLAYER -> this.selectedServerPlayerId == null ? "Race Advancements - Player" : "Race Advancements - Player Progress";
            case TEAM -> this.selectedServerTeamId == null ? "Race Advancements - Team" : "Race Advancements - Team Members";
            case SERVER -> "Race Advancements - Server";
        };
        graphics.drawString(this.font, Component.literal(header), contentX, contentY, 0xFFFFFFFF, false);

        int headerSpacing = this.serverStatsMode == ServerStatsMode.SERVER ? 5 : STATS_PANEL_PADDING;
        int bodyTop = contentY + this.font.lineHeight + headerSpacing;
        int bodyHeight = Math.max(0, panelHeight - (bodyTop - panelY) - STATS_PANEL_PADDING);
        if (bodyHeight <= 0) {
            return;
        }

        switch (this.serverStatsMode) {
            case PLAYER -> {
                if (this.selectedServerPlayerId == null) {
                    this.renderPlayerOverviewPanel(graphics, mouseX, mouseY, contentX, bodyTop, contentWidth, bodyHeight);
                } else {
                    this.renderPlayerDetailPanel(graphics, mouseX, mouseY, contentX, bodyTop, contentWidth, bodyHeight);
                }
            }
            case TEAM -> {
                if (this.selectedServerTeamId == null) {
                    this.renderTeamOverviewPanel(graphics, mouseX, mouseY, contentX, bodyTop, contentWidth, bodyHeight);
                } else {
                    this.renderTeamDetailPanel(graphics, mouseX, mouseY, contentX, bodyTop, contentWidth, bodyHeight);
                }
            }
            case SERVER -> this.renderServerTreePanel(graphics, mouseX, mouseY, contentX, bodyTop, contentWidth, bodyHeight);
        }
    }

    private void renderPlayerOverviewPanel(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        List<SpaceRaceClientStats.PlayerStatsEntry> entries = SpaceRaceClientStats.getServerStatsEntries();
        if (entries.isEmpty()) {
            graphics.drawCenteredString(this.font, Component.literal("No player progression data available."), x + width / 2, y + height / 2 - this.font.lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        int listWidth = Math.max(80, width - SCROLLBAR_WIDTH - SCROLLBAR_SPACING);
        int scrollbarX = x + listWidth + SCROLLBAR_SPACING;
        int visibleRows = Math.max(0, (height + OVERVIEW_ENTRY_SPACING) / (OVERVIEW_ENTRY_HEIGHT + OVERVIEW_ENTRY_SPACING));
        int rowsTrackHeight = Math.max(0, visibleRows * (OVERVIEW_ENTRY_HEIGHT + OVERVIEW_ENTRY_SPACING) - OVERVIEW_ENTRY_SPACING);
        this.serverPlayerScroll = Mth.clamp(this.serverPlayerScroll, 0, Math.max(0, entries.size() - visibleRows));

        int rowsToRender = Math.min(visibleRows, entries.size());
        Component tooltip = null;
        for (int rowIndex = 0; rowIndex < rowsToRender; rowIndex++) {
            int sourceRowIndex = this.serverPlayerScroll + rowIndex;
            if (sourceRowIndex >= entries.size()) {
                break;
            }

            SpaceRaceClientStats.PlayerStatsEntry entry = entries.get(sourceRowIndex);
            int rowY = y + rowIndex * (OVERVIEW_ENTRY_HEIGHT + OVERVIEW_ENTRY_SPACING);
            Component hovered = this.renderPlayerOverviewRow(graphics, mouseX, mouseY, x, rowY, listWidth, entry);
            this.playerClickAreas.add(new PlayerClickArea(x, rowY, x + listWidth, rowY + OVERVIEW_ENTRY_HEIGHT, entry.playerId()));
            if (hovered != null) {
                tooltip = hovered;
            }
        }

        this.serverScrollbar = new ScrollbarInfo(
                ScrollbarType.SERVER_PLAYER_LIST,
                ScrollbarAxis.VERTICAL,
                scrollbarX,
                y,
                rowsTrackHeight,
                entries.size(),
                Math.max(0, visibleRows),
                this.serverPlayerScroll
        );
        this.renderScrollbar(graphics, this.serverScrollbar, mouseX, mouseY);

        if (rowsToRender == 0) {
            graphics.drawCenteredString(this.font, Component.literal("No player progression data available."), x + listWidth / 2, y + height / 2 - this.font.lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        if (tooltip != null) {
            graphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
        }
    }

    private Component renderPlayerOverviewRow(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, SpaceRaceClientStats.PlayerStatsEntry statsEntry) {
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
        Component tooltip = null;
        for (int i = 0; i < iconCount; i++) {
            SpaceRaceClientStats.AdvancementIconData advancementIcon = visibleIcons.get(i);
            ItemStack icon = advancementIcon.icon();
            if (icon.isEmpty()) {
                continue;
            }
            int iconX = iconStartX + i * (ADVANCEMENT_ICON_SIZE + ADVANCEMENT_ICON_SPACING);
            graphics.renderItem(icon, iconX, iconY);
            if (mouseX >= iconX && mouseX < iconX + ADVANCEMENT_ICON_SIZE && mouseY >= iconY && mouseY < iconY + ADVANCEMENT_ICON_SIZE) {
                tooltip = Component.literal(advancementIcon.title()).withStyle(style -> style.withColor(advancementIcon.color()));
            }
        }
        return tooltip;
    }

    private void renderPlayerDetailPanel(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        SpaceRaceClientStats.PlayerStatsEntry entry = SpaceRaceClientStats.getServerStatsEntry(this.selectedServerPlayerId);
        if (entry == null) {
            graphics.drawCenteredString(this.font, Component.literal("Selected player data is no longer available."), x + width / 2, y + height / 2 - this.font.lineHeight / 2, 0xFF9E9E9E);
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
            graphics.drawCenteredString(this.font, Component.literal("No matching advancements."), x + listWidth / 2, listTop + listHeight / 2 - this.font.lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        int visibleRows = Math.max(0, (listHeight + ADVANCEMENT_ROW_SPACING) / (ADVANCEMENT_ROW_HEIGHT + ADVANCEMENT_ROW_SPACING));
        int rowsTrackHeight = Math.max(0, visibleRows * (ADVANCEMENT_ROW_HEIGHT + ADVANCEMENT_ROW_SPACING) - ADVANCEMENT_ROW_SPACING);
        this.serverPlayerDetailScroll = Mth.clamp(this.serverPlayerDetailScroll, 0, Math.max(0, advancements.size() - visibleRows));

        for (int rowIndex = 0; rowIndex < visibleRows; rowIndex++) {
            int sourceRowIndex = this.serverPlayerDetailScroll + rowIndex;
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

        this.serverScrollbar = new ScrollbarInfo(
                ScrollbarType.SERVER_PLAYER_DETAIL,
                ScrollbarAxis.VERTICAL,
                scrollbarX,
                listTop,
                rowsTrackHeight,
                advancements.size(),
                Math.max(0, visibleRows),
                this.serverPlayerDetailScroll
        );
        this.renderScrollbar(graphics, this.serverScrollbar, mouseX, mouseY);
    }

    private void renderTeamOverviewPanel(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        List<SpaceRaceClientStats.TeamStatsEntry> entries = SpaceRaceClientStats.getTeamStatsEntries();
        if (entries.isEmpty()) {
            graphics.drawCenteredString(this.font, Component.literal("No team progression data available."), x + width / 2, y + height / 2 - this.font.lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        int listWidth = Math.max(80, width - SCROLLBAR_WIDTH - SCROLLBAR_SPACING);
        int scrollbarX = x + listWidth + SCROLLBAR_SPACING;
        int visibleRows = Math.max(0, (height + OVERVIEW_ENTRY_SPACING) / (OVERVIEW_ENTRY_HEIGHT + OVERVIEW_ENTRY_SPACING));
        int rowsTrackHeight = Math.max(0, visibleRows * (OVERVIEW_ENTRY_HEIGHT + OVERVIEW_ENTRY_SPACING) - OVERVIEW_ENTRY_SPACING);
        this.serverTeamScroll = Mth.clamp(this.serverTeamScroll, 0, Math.max(0, entries.size() - visibleRows));

        int rowsToRender = Math.min(visibleRows, entries.size());
        List<Component> tooltip = null;
        for (int rowIndex = 0; rowIndex < rowsToRender; rowIndex++) {
            int sourceRowIndex = this.serverTeamScroll + rowIndex;
            if (sourceRowIndex >= entries.size()) {
                break;
            }

            SpaceRaceClientStats.TeamStatsEntry entry = entries.get(sourceRowIndex);
            int rowY = y + rowIndex * (OVERVIEW_ENTRY_HEIGHT + OVERVIEW_ENTRY_SPACING);
            List<Component> hovered = this.renderTeamOverviewRow(graphics, mouseX, mouseY, x, rowY, listWidth, entry);
            this.teamClickAreas.add(new TeamClickArea(x, rowY, x + listWidth, rowY + OVERVIEW_ENTRY_HEIGHT, entry.teamId()));
            if (hovered != null) {
                tooltip = hovered;
            }
        }

        this.serverScrollbar = new ScrollbarInfo(
                ScrollbarType.SERVER_TEAM_LIST,
                ScrollbarAxis.VERTICAL,
                scrollbarX,
                y,
                rowsTrackHeight,
                entries.size(),
                Math.max(0, visibleRows),
                this.serverTeamScroll
        );
        this.renderScrollbar(graphics, this.serverScrollbar, mouseX, mouseY);

        if (rowsToRender == 0) {
            graphics.drawCenteredString(this.font, Component.literal("No team progression data available."), x + listWidth / 2, y + height / 2 - this.font.lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        if (tooltip != null) {
            graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
        }
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
        List<Component> tooltip = null;
        for (int i = 0; i < iconCount; i++) {
            SpaceRaceClientStats.AdvancementIconData advancementIcon = visibleIcons.get(i);
            ItemStack icon = advancementIcon.icon();
            if (icon.isEmpty()) {
                continue;
            }
            int entryIconX = iconStartX + i * (ADVANCEMENT_ICON_SIZE + ADVANCEMENT_ICON_SPACING);
            graphics.renderItem(icon, entryIconX, rowIconY);
            if (mouseX >= entryIconX && mouseX < entryIconX + ADVANCEMENT_ICON_SIZE && mouseY >= rowIconY && mouseY < rowIconY + ADVANCEMENT_ICON_SIZE) {
                tooltip = this.buildAdvancementTooltip(advancementIcon, true);
            }
        }
        return tooltip;
    }

    private void renderTeamDetailPanel(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        SpaceRaceClientStats.TeamStatsEntry entry = SpaceRaceClientStats.getTeamStatsEntry(this.selectedServerTeamId);
        if (entry == null) {
            graphics.drawCenteredString(this.font, Component.literal("Selected team data is no longer available."), x + width / 2, y + height / 2 - this.font.lineHeight / 2, 0xFF9E9E9E);
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
            graphics.drawCenteredString(this.font, Component.literal("No visible team members."), x + listWidth / 2, listTop + listHeight / 2 - this.font.lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        int visibleRows = Math.max(0, (listHeight + OVERVIEW_ENTRY_SPACING) / (OVERVIEW_ENTRY_HEIGHT + OVERVIEW_ENTRY_SPACING));
        int rowsTrackHeight = Math.max(0, visibleRows * (OVERVIEW_ENTRY_HEIGHT + OVERVIEW_ENTRY_SPACING) - OVERVIEW_ENTRY_SPACING);
        this.serverTeamDetailScroll = Mth.clamp(this.serverTeamDetailScroll, 0, Math.max(0, members.size() - visibleRows));

        Component tooltip = null;

        for (int rowIndex = 0; rowIndex < visibleRows; rowIndex++) {
            int sourceRowIndex = this.serverTeamDetailScroll + rowIndex;
            if (sourceRowIndex >= members.size()) {
                break;
            }

            SpaceRaceClientStats.TeamMemberEntry member = members.get(sourceRowIndex);
            int rowY = listTop + rowIndex * (OVERVIEW_ENTRY_HEIGHT + OVERVIEW_ENTRY_SPACING);
            Component hovered = this.renderTeamMemberRow(graphics, mouseX, mouseY, x, rowY, listWidth, member);
            if (hovered != null) {
                tooltip = hovered;
            }
        }

        this.serverScrollbar = new ScrollbarInfo(
                ScrollbarType.SERVER_TEAM_DETAIL,
                ScrollbarAxis.VERTICAL,
                scrollbarX,
                listTop,
                rowsTrackHeight,
                members.size(),
                Math.max(0, visibleRows),
                this.serverTeamDetailScroll
        );
        this.renderScrollbar(graphics, this.serverScrollbar, mouseX, mouseY);

        if (tooltip != null) {
            graphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
        }
    }

    private Component renderTeamMemberRow(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            int x,
            int y,
            int width,
            SpaceRaceClientStats.TeamMemberEntry member
    ) {
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
        Component tooltip = null;
        for (int i = 0; i < iconCount; i++) {
            SpaceRaceClientStats.AdvancementIconData advancementIcon = visibleIcons.get(i);
            ItemStack icon = advancementIcon.icon();
            if (icon.isEmpty()) {
                continue;
            }
            int iconX = iconStartX + i * (ADVANCEMENT_ICON_SIZE + ADVANCEMENT_ICON_SPACING);
            graphics.renderItem(icon, iconX, iconY);
            if (mouseX >= iconX && mouseX < iconX + ADVANCEMENT_ICON_SIZE && mouseY >= iconY && mouseY < iconY + ADVANCEMENT_ICON_SIZE) {
                tooltip = Component.literal(advancementIcon.title()).withStyle(style -> style.withColor(advancementIcon.color()));
            }
        }
        return tooltip;
    }

    private void renderServerTreePanel(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        List<SpaceRaceClientStats.ServerAdvancementNode> nodes = SpaceRaceClientStats.getServerAdvancementNodes();
        if (nodes.isEmpty()) {
            graphics.drawCenteredString(this.font, Component.literal("No server advancement tree data available."), x + width / 2, y + height / 2 - this.font.lineHeight / 2, 0xFF9E9E9E);
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
        if (this.centerServerTreeVerticalOnNextRender) {
            this.serverTreeScrollY = maxScrollY / 2;
            this.centerServerTreeVerticalOnNextRender = false;
        }
        this.serverTreeScrollX = Mth.clamp(this.serverTreeScrollX, 0, maxScrollX);
        this.serverTreeScrollY = Mth.clamp(this.serverTreeScrollY, 0, maxScrollY);

        int originX = x + TREE_CANVAS_PADDING - this.serverTreeScrollX - minX;
        int originY = y + TREE_CANVAS_PADDING - this.serverTreeScrollY - minY;

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

        List<Component> tooltip = null;
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
                Component status = Component.literal(node.complete() ? "Complete" : "Incomplete").withStyle(style -> style.withColor(node.complete() ? 0x8DDB6B : 0xC97B7B));
                List<Component> nodeTooltip = new ArrayList<>();
                nodeTooltip.add(Component.literal(node.title()).withStyle(style -> style.withColor(node.color())).append(Component.literal(" - ")).append(status));
                if (node.complete()) {
                    if (node.firstUnlockers().isEmpty()) {
                        nodeTooltip.add(Component.literal("First unlockers hidden or unavailable.").withStyle(ChatFormatting.DARK_GRAY));
                    } else {
                        nodeTooltip.add(Component.literal("First unlocked by:").withStyle(ChatFormatting.GRAY));
                        for (String firstUnlocker : node.firstUnlockers()) {
                            nodeTooltip.add(Component.literal("- " + firstUnlocker));
                        }
                    }
                }
                tooltip = nodeTooltip;
            }
        }
        graphics.disableScissor();

        this.serverTreeVerticalScrollbar = new ScrollbarInfo(
                ScrollbarType.SERVER_TREE_VERTICAL,
                ScrollbarAxis.VERTICAL,
                verticalScrollbarX,
                y,
                canvasHeight,
                contentHeight,
                canvasHeight,
                this.serverTreeScrollY
        );
        this.serverTreeHorizontalScrollbar = new ScrollbarInfo(
                ScrollbarType.SERVER_TREE_HORIZONTAL,
                ScrollbarAxis.HORIZONTAL,
                x,
                horizontalScrollbarY,
                canvasWidth,
                contentWidth,
                canvasWidth,
                this.serverTreeScrollX
        );
        this.renderScrollbar(graphics, this.serverTreeVerticalScrollbar, mouseX, mouseY);
        this.renderScrollbar(graphics, this.serverTreeHorizontalScrollbar, mouseX, mouseY);

        if (tooltip != null) {
            graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
        }
    }

    private void drawClampedHorizontalLine(
            GuiGraphics graphics,
            int startX,
            int endX,
            int y,
            int clipX,
            int clipY,
            int clipWidth,
            int clipHeight,
            int color
    ) {
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

    private void drawClampedVerticalLine(
            GuiGraphics graphics,
            int x,
            int startY,
            int endY,
            int clipX,
            int clipY,
            int clipWidth,
            int clipHeight,
            int color
    ) {
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

    private boolean handleServerStatsClick(double mouseX, double mouseY) {
        if (this.serverStatsMode == ServerStatsMode.PLAYER && this.selectedServerPlayerId == null) {
            for (PlayerClickArea clickArea : this.playerClickAreas) {
                if (!clickArea.contains(mouseX, mouseY)) {
                    continue;
                }
                this.selectedServerPlayerId = clickArea.playerId();
                this.serverPlayerDetailScroll = 0;
                this.activeScrollbar = ScrollbarType.NONE;
                return true;
            }
            return false;
        }

        if (this.serverStatsMode == ServerStatsMode.TEAM && this.selectedServerTeamId == null) {
            for (TeamClickArea clickArea : this.teamClickAreas) {
                if (!clickArea.contains(mouseX, mouseY)) {
                    continue;
                }
                this.selectedServerTeamId = clickArea.teamId();
                this.serverTeamDetailScroll = 0;
                this.activeScrollbar = ScrollbarType.NONE;
                return true;
            }
        }
        return false;
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

        tooltip.add(Component.literal("First unlocked by:").withStyle(ChatFormatting.GRAY));
        for (String firstUnlocker : advancementIcon.firstUnlockers()) {
            tooltip.add(Component.literal("- " + firstUnlocker));
        }
        return tooltip;
    }

    private int getCompactButtonWidth(Component... labels) {
        int width = 0;
        for (Component label : labels) {
            width = Math.max(width, Mth.ceil(this.font.width(label) * COMPACT_BUTTON_TEXT_SCALE) + 10);
        }
        return Math.max(64, width);
    }

    private void renderGlobalStatsPanel(GuiGraphics graphics, int mouseX, int mouseY) {
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

        switch (this.globalStatsSection) {
            case GENERAL -> this.renderGlobalGeneralPanel(graphics, mouseX, mouseY, contentX, bodyTop, contentWidth, bodyHeight);
            case ITEMS -> this.renderGlobalItemsPanel(graphics, mouseX, mouseY, contentX, bodyTop, contentWidth, bodyHeight);
            case MOBS -> this.renderGlobalMobsPanel(graphics, mouseX, mouseY, contentX, bodyTop, contentWidth, bodyHeight);
        }
    }

    private void renderGlobalGeneralPanel(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        List<SpaceRaceClientStats.GeneralStatRow> rows = SpaceRaceClientStats.getGeneralStatRows();
        if (rows.isEmpty()) {
            this.globalGeneralScrollbar = null;
            graphics.drawCenteredString(this.font, Component.literal("No Galacticraft general stats available."), x + width / 2, y + height / 2 - this.font.lineHeight / 2, 0xFF9E9E9E);
            return;
        }

        int listWidth = Math.max(80, width - SCROLLBAR_WIDTH - SCROLLBAR_SPACING);
        int scrollbarX = x + listWidth + SCROLLBAR_SPACING;
        int scaledLineHeight = this.getScaledLineHeight(GLOBAL_TEXT_SCALE);
        int visibleRows = Math.max(1, height / GLOBAL_GENERAL_ROW_HEIGHT);
        int rowAreaHeight = visibleRows * GLOBAL_GENERAL_ROW_HEIGHT;
        this.globalGeneralScroll = Mth.clamp(this.globalGeneralScroll, 0, Math.max(0, rows.size() - visibleRows));

        int totalRightEdge = x + listWidth - GLOBAL_HEAD_SIZE - 8;
        int totalLeftEdge = totalRightEdge - GLOBAL_GENERAL_TOTAL_WIDTH;
        int labelWidth = Math.max(40, totalLeftEdge - x - 6);
        Component tooltip = null;

        for (int rowIndex = 0; rowIndex < visibleRows; rowIndex++) {
            int sourceRowIndex = this.globalGeneralScroll + rowIndex;
            if (sourceRowIndex >= rows.size()) {
                break;
            }
            SpaceRaceClientStats.GeneralStatRow row = rows.get(sourceRowIndex);
            int rowY = y + rowIndex * GLOBAL_GENERAL_ROW_HEIGHT;

            graphics.fill(x, rowY, x + listWidth, rowY + GLOBAL_GENERAL_ROW_HEIGHT, 0x441A1A1A);
            graphics.renderOutline(x, rowY, listWidth, GLOBAL_GENERAL_ROW_HEIGHT, 0x553C3C3C);

            int textY = rowY + (GLOBAL_GENERAL_ROW_HEIGHT - scaledLineHeight) / 2;
            String label = this.truncateForScaledText(row.label(), labelWidth, GLOBAL_TEXT_SCALE);
            this.drawScaledString(graphics, label, x + 4, textY, 0xFFFFFFFF, GLOBAL_TEXT_SCALE);

            String totalText = Integer.toString(row.total());
            int totalX = totalRightEdge - this.scaledTextWidth(totalText, GLOBAL_TEXT_SCALE);
            this.drawScaledString(graphics, totalText, totalX, textY, 0xFFFFFFFF, GLOBAL_TEXT_SCALE);

            int headX = x + listWidth - GLOBAL_HEAD_SIZE - 4;
            int headY = rowY + (GLOBAL_GENERAL_ROW_HEIGHT - GLOBAL_HEAD_SIZE) / 2;
            SpaceRaceClientStats.LeaderCell leader = row.leader();
            if (leader != null) {
                this.renderPlayerHead(graphics, leader.playerId(), headX, headY, GLOBAL_HEAD_SIZE);
                if (mouseX >= headX && mouseX < headX + GLOBAL_HEAD_SIZE && mouseY >= headY && mouseY < headY + GLOBAL_HEAD_SIZE) {
                    tooltip = Component.literal(leader.playerName() + ": " + leader.value());
                }
            } else {
                this.drawScaledCenteredString(graphics, "-", headX + GLOBAL_HEAD_SIZE / 2, textY, 0xFF9E9E9E, GLOBAL_TEXT_SCALE);
            }
        }

        this.globalGeneralScrollbar = new ScrollbarInfo(
                ScrollbarType.GLOBAL_GENERAL,
                ScrollbarAxis.VERTICAL,
                scrollbarX,
                y,
                rowAreaHeight,
                rows.size(),
                visibleRows,
                this.globalGeneralScroll
        );
        this.renderScrollbar(graphics, this.globalGeneralScrollbar, mouseX, mouseY);

        if (tooltip != null) {
            graphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
        }
    }

    private void renderGlobalItemsPanel(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        List<SpaceRaceClientStats.ItemStatRow> rows = SpaceRaceClientStats.getItemStatRows();
        if (rows.isEmpty()) {
            this.globalItemsScrollbar = null;
            graphics.drawCenteredString(this.font, Component.literal("No item stats recorded yet."), x + width / 2, y + height / 2 - this.font.lineHeight / 2, 0xFF9E9E9E);
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
        int scaledLineHeight = this.getScaledLineHeight(GLOBAL_TEXT_SCALE);
        float iconScale = GLOBAL_ITEM_ICON_SIZE / 16.0F;

        Component tooltip = null;
        for (int i = 0; i < columns.length; i++) {
            int cellX = tableX + GLOBAL_ITEMS_ICON_COLUMN_WIDTH + i * cellWidth;
            graphics.fill(cellX, headerY, cellX + cellWidth, headerY + GLOBAL_ITEMS_HEADER_HEIGHT, 0x55202020);
            graphics.renderOutline(cellX, headerY, cellWidth, GLOBAL_ITEMS_HEADER_HEIGHT, 0x773D3D3D);

            int iconX = cellX + (cellWidth - GLOBAL_ITEM_ICON_SIZE) / 2;
            int iconY = headerY + (GLOBAL_ITEMS_HEADER_HEIGHT - GLOBAL_ITEM_ICON_SIZE) / 2;
            this.renderScaledItem(graphics, columns[i].icon(), iconX, iconY, iconScale);
            if (mouseX >= iconX && mouseX < iconX + GLOBAL_ITEM_ICON_SIZE && mouseY >= iconY && mouseY < iconY + GLOBAL_ITEM_ICON_SIZE) {
                tooltip = columns[i].tooltip();
            }
        }

        int rowsTop = headerY + GLOBAL_ITEMS_HEADER_HEIGHT;
        int rowsHeight = Math.max(0, height - GLOBAL_ITEMS_HEADER_HEIGHT);
        int visibleRows = Math.max(1, rowsHeight / GLOBAL_ITEMS_ROW_HEIGHT);
        int rowAreaHeight = visibleRows * GLOBAL_ITEMS_ROW_HEIGHT;
        this.globalItemsScroll = Mth.clamp(this.globalItemsScroll, 0, Math.max(0, rows.size() - visibleRows));

        for (int rowIndex = 0; rowIndex < visibleRows; rowIndex++) {
            int sourceRowIndex = this.globalItemsScroll + rowIndex;
            if (sourceRowIndex >= rows.size()) {
                break;
            }
            SpaceRaceClientStats.ItemStatRow row = rows.get(sourceRowIndex);
            int rowY = rowsTop + rowIndex * GLOBAL_ITEMS_ROW_HEIGHT;

            graphics.fill(tableX, rowY, tableX + tableWidth, rowY + GLOBAL_ITEMS_ROW_HEIGHT, 0x441A1A1A);
            graphics.renderOutline(tableX, rowY, tableWidth, GLOBAL_ITEMS_ROW_HEIGHT, 0x553C3C3C);

            int rowIconX = tableX + (GLOBAL_ITEMS_ICON_COLUMN_WIDTH - GLOBAL_ITEM_ICON_SIZE) / 2;
            int rowIconY = rowY + (GLOBAL_ITEMS_ROW_HEIGHT - GLOBAL_ITEM_ICON_SIZE) / 2;
            this.renderScaledItem(graphics, row.icon(), rowIconX, rowIconY, iconScale);

            for (int i = 0; i < columns.length; i++) {
                int cellX = tableX + GLOBAL_ITEMS_ICON_COLUMN_WIDTH + i * cellWidth;
                graphics.fill(cellX, rowY, cellX + cellWidth, rowY + GLOBAL_ITEMS_ROW_HEIGHT, 0x33262626);
                graphics.renderOutline(cellX, rowY, cellWidth, GLOBAL_ITEMS_ROW_HEIGHT, 0x553C3C3C);

                if (i >= row.cells().size()) {
                    int emptyY = rowY + (GLOBAL_ITEMS_ROW_HEIGHT - scaledLineHeight) / 2;
                    this.drawScaledCenteredString(graphics, "-", cellX + cellWidth / 2, emptyY, 0xFF9E9E9E, GLOBAL_TEXT_SCALE);
                    continue;
                }

                SpaceRaceClientStats.ItemStatCell cell = row.cells().get(i);
                if (cell.total() <= 0) {
                    int emptyY = rowY + (GLOBAL_ITEMS_ROW_HEIGHT - scaledLineHeight) / 2;
                    this.drawScaledCenteredString(graphics, "-", cellX + cellWidth / 2, emptyY, 0xFF9E9E9E, GLOBAL_TEXT_SCALE);
                    continue;
                }

                SpaceRaceClientStats.LeaderCell leader = cell.leader();
                if (leader != null) {
                    int headX = cellX + (cellWidth - GLOBAL_HEAD_SIZE) / 2;
                    int headY = rowY + 2;
                    this.renderPlayerHead(graphics, leader.playerId(), headX, headY, GLOBAL_HEAD_SIZE);
                    if (mouseX >= headX && mouseX < headX + GLOBAL_HEAD_SIZE && mouseY >= headY && mouseY < headY + GLOBAL_HEAD_SIZE) {
                        tooltip = Component.literal(leader.playerName() + ": " + leader.value());
                    }
                } else {
                    this.drawScaledCenteredString(graphics, "-", cellX + cellWidth / 2, rowY + 2, 0xFF9E9E9E, GLOBAL_TEXT_SCALE);
                }

                int totalY = rowY + GLOBAL_HEAD_SIZE + 3;
                this.drawScaledCenteredString(graphics, Integer.toString(cell.total()), cellX + cellWidth / 2, totalY, 0xFFFFFFFF, GLOBAL_TEXT_SCALE);
            }
        }

        if (tooltip != null) {
            graphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
        }

        this.globalItemsScrollbar = new ScrollbarInfo(
                ScrollbarType.GLOBAL_ITEMS,
                ScrollbarAxis.VERTICAL,
                scrollbarX,
                headerY,
                GLOBAL_ITEMS_HEADER_HEIGHT + rowAreaHeight,
                rows.size(),
                visibleRows,
                this.globalItemsScroll
        );
        this.renderScrollbar(graphics, this.globalItemsScrollbar, mouseX, mouseY);
    }

    private void renderGlobalMobsPanel(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height) {
        List<SpaceRaceClientStats.MobStatRow> rows = SpaceRaceClientStats.getMobStatRows();
        if (rows.isEmpty()) {
            this.globalMobsScrollbar = null;
            graphics.drawCenteredString(this.font, Component.literal("No mob kills recorded yet."), x + width / 2, y + height / 2 - this.font.lineHeight / 2, 0xFF9E9E9E);
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
        int scaledLineHeight = this.getScaledLineHeight(GLOBAL_TEXT_SCALE);

        graphics.fill(x, y, x + listWidth, y + GLOBAL_MOBS_ROW_HEIGHT, 0x55202020);
        graphics.renderOutline(x, y, listWidth, GLOBAL_MOBS_ROW_HEIGHT, 0x773D3D3D);
        graphics.fill(dividerOneX, y, dividerOneX + 1, y + GLOBAL_MOBS_ROW_HEIGHT, 0x773D3D3D);
        graphics.fill(dividerTwoX, y, dividerTwoX + 1, y + GLOBAL_MOBS_ROW_HEIGHT, 0x773D3D3D);
        int headerTextY = y + (GLOBAL_MOBS_ROW_HEIGHT - scaledLineHeight) / 2;
        this.drawScaledString(graphics, "Mob", x + 4, headerTextY, 0xFFFFFFFF, GLOBAL_TEXT_SCALE);
        this.drawScaledCenteredString(graphics, "Killed", killedCenterX, headerTextY, 0xFFFFFFFF, GLOBAL_TEXT_SCALE);
        this.drawScaledCenteredString(graphics, "Kill Leader", leaderLeftX + leaderWidth / 2, headerTextY, 0xFFFFFFFF, GLOBAL_TEXT_SCALE);

        int rowsTop = y + GLOBAL_MOBS_ROW_HEIGHT;
        int rowsHeight = Math.max(0, height - GLOBAL_MOBS_ROW_HEIGHT);
        int visibleRows = Math.max(1, rowsHeight / GLOBAL_MOBS_ROW_HEIGHT);
        int rowAreaHeight = visibleRows * GLOBAL_MOBS_ROW_HEIGHT;
        this.globalMobsScroll = Mth.clamp(this.globalMobsScroll, 0, Math.max(0, rows.size() - visibleRows));

        Component tooltip = null;
        for (int rowIndex = 0; rowIndex < visibleRows; rowIndex++) {
            int sourceRowIndex = this.globalMobsScroll + rowIndex;
            if (sourceRowIndex >= rows.size()) {
                break;
            }
            SpaceRaceClientStats.MobStatRow row = rows.get(sourceRowIndex);
            int rowY = rowsTop + rowIndex * GLOBAL_MOBS_ROW_HEIGHT;

            graphics.fill(x, rowY, x + listWidth, rowY + GLOBAL_MOBS_ROW_HEIGHT, 0x441A1A1A);
            graphics.renderOutline(x, rowY, listWidth, GLOBAL_MOBS_ROW_HEIGHT, 0x553C3C3C);
            graphics.fill(dividerOneX, rowY, dividerOneX + 1, rowY + GLOBAL_MOBS_ROW_HEIGHT, 0x553C3C3C);
            graphics.fill(dividerTwoX, rowY, dividerTwoX + 1, rowY + GLOBAL_MOBS_ROW_HEIGHT, 0x553C3C3C);

            int textY = rowY + (GLOBAL_MOBS_ROW_HEIGHT - scaledLineHeight) / 2;
            String mobLabel = this.truncateForScaledText(row.mobName(), nameWidth - 6, GLOBAL_TEXT_SCALE);
            this.drawScaledString(graphics, mobLabel, x + 4, textY, 0xFFFFFFFF, GLOBAL_TEXT_SCALE);
            this.drawScaledCenteredString(graphics, Integer.toString(row.totalKilled()), killedCenterX, textY, 0xFFFFFFFF, GLOBAL_TEXT_SCALE);

            SpaceRaceClientStats.LeaderCell leader = row.leader();
            int headX = leaderRightX - GLOBAL_HEAD_SIZE - 4;
            int headY = rowY + (GLOBAL_MOBS_ROW_HEIGHT - GLOBAL_HEAD_SIZE) / 2;
            int leaderNameWidth = Math.max(12, headX - leaderLeftX - 4);
            if (leader != null) {
                String leaderName = this.truncateForScaledText(leader.playerName(), leaderNameWidth, GLOBAL_TEXT_SCALE);
                this.drawScaledString(graphics, leaderName, leaderLeftX + 2, textY, 0xFFFFFFFF, GLOBAL_TEXT_SCALE);
                this.renderPlayerHead(graphics, leader.playerId(), headX, headY, GLOBAL_HEAD_SIZE);
                if (mouseX >= headX && mouseX < headX + GLOBAL_HEAD_SIZE && mouseY >= headY && mouseY < headY + GLOBAL_HEAD_SIZE) {
                    tooltip = Component.literal(leader.playerName() + ": " + leader.value());
                }
            } else {
                this.drawScaledCenteredString(graphics, "-", leaderLeftX + leaderWidth / 2, textY, 0xFF9E9E9E, GLOBAL_TEXT_SCALE);
            }
        }

        this.globalMobsScrollbar = new ScrollbarInfo(
                ScrollbarType.GLOBAL_MOBS,
                ScrollbarAxis.VERTICAL,
                scrollbarX,
                y,
                GLOBAL_MOBS_ROW_HEIGHT + rowAreaHeight,
                rows.size(),
                visibleRows,
                this.globalMobsScroll
        );
        this.renderScrollbar(graphics, this.globalMobsScrollbar, mouseX, mouseY);

        if (tooltip != null) {
            graphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
        }
    }

    private int getScaledLineHeight(float scale) {
        return Math.max(1, Math.round(this.font.lineHeight * scale));
    }

    private int scaledTextWidth(String text, float scale) {
        return Math.round(this.font.width(text) * scale);
    }

    private String truncateForScaledText(String text, int maxWidth, float scale) {
        int unscaledWidth = Math.max(0, Mth.floor(maxWidth / scale));
        return this.font.plainSubstrByWidth(text, unscaledWidth);
    }

    private void drawScaledString(GuiGraphics graphics, String text, int x, int y, int color, float scale) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x, y, 0);
        pose.scale(scale, scale, 1.0F);
        graphics.drawString(this.font, text, 0, 0, color, false);
        pose.popPose();
    }

    private void drawScaledCenteredString(GuiGraphics graphics, String text, int centerX, int y, int color, float scale) {
        int drawX = centerX - this.scaledTextWidth(text, scale) / 2;
        this.drawScaledString(graphics, text, drawX, y, color, scale);
    }

    private void renderScaledItem(GuiGraphics graphics, ItemStack icon, int x, int y, float scale) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(x, y, 0);
        pose.scale(scale, scale, 1.0F);
        graphics.renderItem(icon, 0, 0);
        pose.popPose();
    }

    private void renderScrollbar(GuiGraphics graphics, ScrollbarInfo scrollbar, int mouseX, int mouseY) {
        if (scrollbar == null || scrollbar.length() <= 0 || !scrollbar.isInteractive()) {
            return;
        }

        int x = scrollbar.x();
        int y = scrollbar.y();
        int length = scrollbar.length();
        if (scrollbar.axis() == ScrollbarAxis.VERTICAL) {
            graphics.fill(x, y, x + SCROLLBAR_WIDTH, y + length, 0x44171717);
            graphics.renderOutline(x, y, SCROLLBAR_WIDTH, length, 0x66454545);
        } else {
            graphics.fill(x, y, x + length, y + SCROLLBAR_WIDTH, 0x44171717);
            graphics.renderOutline(x, y, length, SCROLLBAR_WIDTH, 0x66454545);
        }

        int thumbPosition = scrollbar.thumbPosition();
        int thumbLength = scrollbar.thumbLength();
        boolean active = this.activeScrollbar == scrollbar.type();
        boolean hovered = scrollbar.contains(mouseX, mouseY);
        int thumbColor = active ? 0xFFB9B9B9 : hovered ? 0xFFA3A3A3 : 0xFF8D8D8D;
        if (scrollbar.axis() == ScrollbarAxis.VERTICAL) {
            graphics.fill(x + 1, thumbPosition + 1, x + SCROLLBAR_WIDTH - 1, thumbPosition + thumbLength - 1, thumbColor);
            graphics.renderOutline(x, thumbPosition, SCROLLBAR_WIDTH, thumbLength, 0xFF5A5A5A);
        } else {
            graphics.fill(thumbPosition + 1, y + 1, thumbPosition + thumbLength - 1, y + SCROLLBAR_WIDTH - 1, thumbColor);
            graphics.renderOutline(thumbPosition, y, thumbLength, SCROLLBAR_WIDTH, 0xFF5A5A5A);
        }
    }

    private void renderPlayerHead(GuiGraphics graphics, UUID playerId, int x, int y, int size) {
        if (this.minecraft != null && this.minecraft.player != null && this.minecraft.player.connection != null) {
            PlayerInfo playerInfo = this.minecraft.player.connection.getPlayerInfo(playerId);
            if (playerInfo != null) {
                PlayerFaceRenderer.draw(graphics, playerInfo.getSkin(), x, y, size);
                return;
            }
        }

        PlayerFaceRenderer.draw(graphics, DefaultPlayerSkin.get(playerId), x, y, size);
    }

    protected void teamColorMenu() {
        this.serverVisibilityButton = null;
        this.serverHideAdvancementsButton = null;
        this.serverPlayerTabButton = null;
        this.serverTeamTabButton = null;
        this.serverTreeTabButton = null;
        this.globalVisibilityButton = null;
        this.globalGeneralTabButton = null;
        this.globalItemsTabButton = null;
        this.globalMobsTabButton = null;
        addBackButton();

        this.addRenderableOnly((graphics, mouseX, mouseY, delta) -> {
            graphics.fill(this.width / 2 - 50, this.getTop() + 10, this.width / 2 + 50, this.getTop() + 10 + 100, this.teamColor);
        });

        int sliderWidth = 200;
        int sliderX = this.width / 2 - sliderWidth / 2;
        this.addRenderableWidget(new ColorSlider(sliderX, this.getBottom() - 80, sliderWidth, 20, Component.translatable(Translations.SpaceRace.RED), FastColor.ARGB32.red(this.teamColor), value -> {
            this.teamColor = (this.teamColor & 0xFF00FFFF) + (value << 16);
        }));
        this.addRenderableWidget(new ColorSlider(sliderX, this.getBottom() - 55, sliderWidth, 20, Component.translatable(Translations.SpaceRace.GREEN), FastColor.ARGB32.green(this.teamColor), value -> {
            this.teamColor = (this.teamColor & 0xFFFF00FF) + (value << 8);
        }));
        this.addRenderableWidget(new ColorSlider(sliderX, this.getBottom() - 30, sliderWidth, 20, Component.translatable(Translations.SpaceRace.BLUE), FastColor.ARGB32.blue(this.teamColor), value -> {
            this.teamColor = (this.teamColor & 0xFFFFFF00) + value;
        }));
    }

    protected void teamFlagMenu() {
        this.serverVisibilityButton = null;
        this.serverHideAdvancementsButton = null;
        this.serverPlayerTabButton = null;
        this.serverTeamTabButton = null;
        this.serverTreeTabButton = null;
        this.globalVisibilityButton = null;
        this.globalGeneralTabButton = null;
        this.globalItemsTabButton = null;
        this.globalMobsTabButton = null;
        addBackButton();

        this.addRenderableOnly((graphics, mouseX, mouseY, delta) -> graphics.drawCenteredString(this.minecraft.font, Component.translatable(Translations.SpaceRace.DRAG_AND_DROP_FLAG), this.width / 2, this.height / 2 - this.minecraft.font.lineHeight / 2, 0xFFFFFFFF));
    }

    @Override
    protected void init() {
        super.init();
        createMenu(this.menu);
    }

    @Override
    public void resize(Minecraft client, int width, int height) {
        this.backgroundWidth = (int) (width - ((this.getMarginPercent() * width) * 1.5D));
        this.backgroundHeight = (int) (height - ((this.getMarginPercent() * height) * 1.5D));
        super.resize(client, width, height);
    }

    private SpaceRaceButton addButton(Component text, int x, int y, int width, int height, Button.OnPress onPress) {
        return this.addRenderableWidget(new SpaceRaceButton(text, x, y, width, height, onPress));
    }

    private void addBackButton() {
        addButton(Component.translatable(Translations.SpaceRace.BACK), this.getLeft() + 5, this.getTop() + 5, 40, 14, button -> setMenu(Menu.MAIN));
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int maxWidth = (int) (this.width - (getXMargins() * 1.5D));
        if (backgroundWidth < maxWidth) {
            backgroundWidth += (int) Math.min(60 * delta, maxWidth - backgroundWidth);
        }

        int maxHeight = (int) (this.height - (getYMargins() * 1.5D));
        if (backgroundHeight < maxHeight) {
            backgroundHeight += (int) Math.min(40 * delta, maxHeight - backgroundHeight);
        }

        if (!this.animationCompleted && this.isAnimationComplete()) {
            this.repositionElements();
            this.animationCompleted = true;
        }

        graphics.fill(getLeft(), getTop(), getLeft() + backgroundWidth, getTop() + backgroundHeight, 0x80000000);
    }

    private void renderForeground(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawCenteredString(this.font, Component.translatable(Translations.SpaceRace.SPACE_RACE_MANAGER), this.width / 2, getTop() - 20, 0xFFFFFF);
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

    private int getBottom() {
        return this.getTop() + this.backgroundHeight;
    }

    private int getLeft() {
        return (this.width / 2) - (this.backgroundWidth / 2);
    }

    private int getTop() {
        return (this.height / 2) - (this.backgroundHeight / 2);
    }

    private int getRight() {
        return this.getLeft() + this.backgroundWidth;
    }

    private float getMarginPercent() {
        return 0.17F;
    }

    private void setMenu(Menu menu) {
        this.menu = menu;
        createMenu(menu);
    }

    private void createMenu(Menu menu) {
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
            case MAIN -> mainMenu();
            case ADD_PLAYERS -> addPlayersMenu();
            case REMOVE_PLAYERS -> removePlayersMenu();
            case SERVER_STATS -> serverStatsMenu();
            case GLOBAL_STATS -> globalStatsMenu();
            case TEAM_FLAG -> teamFlagMenu();
            case TEAM_COLOR -> teamColorMenu();
        }
    }

    private boolean isAnimationComplete() {
        int maxWidth = (int) (this.width - (getXMargins() * 1.5D));
        int maxHeight = (int) (this.height - (getYMargins() * 1.5D));

        return backgroundWidth >= maxWidth && backgroundHeight >= maxHeight;
    }

    private int getYMargins() {
        return (int) (this.height * this.getMarginPercent());
    }

    private int getXMargins() {
        return (int) (this.width * this.getMarginPercent());
    }

    @Override
    public void onFilesDrop(List<Path> paths) {
        if (this.menu != Menu.TEAM_FLAG) {
            return;
        }
        if (paths.isEmpty()) {
            return;
        }
        File file = paths.get(0).toFile();
        NativeImage image;
        assert file.exists();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            image = NativeImage.read(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (image.getWidth() == 48 && image.getHeight() == 32) {
            final NativeImage finalImage = image;
            final DynamicTexture texture = new DynamicTexture(finalImage);
            ResourceLocation location = Constant.id("temp_flag");
            this.minecraft.getTextureManager().register(location, texture);
            this.minecraft.setScreen(new ConfirmFlagScreen(yes -> {
                if (yes) {
                    ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(48 * 32 * 3, 48 * 32 * 3);
                    for (int y = 0; y < 32; y++) {
                        for (int x = 0; x < 48; x++) {
                            int color = finalImage.getPixelRGBA(x, y);
                            buf.writeByte((color >> 16) & 0xFF)
                                    .writeByte((color >> 8) & 0xFF)
                                    .writeByte(color & 0xFF);
                        }
                    }

                    byte[] data;
                    if (buf.hasArray()) {
                        data = buf.array();
                    } else {
                        data = new byte[buf.readableBytes()];
                        buf.getBytes(buf.readerIndex(), data);
                    }

                    ClientPlayNetworking.send(new FlagDataPayload(data));
                    this.minecraft.getTextureManager().register(this.teamFlag, texture);
                } else {
                    finalImage.close();
                }
                this.minecraft.setScreen(SpaceRaceScreen.this);
            }, location, Component.translatable(Translations.SpaceRace.FLAG_CONFIRM), Component.translatable(Translations.SpaceRace.FLAG_CONFIRM_MESSAGE)));
        }
    }

    private enum ScrollbarType {
        NONE,
        SERVER_PLAYER_LIST,
        SERVER_PLAYER_DETAIL,
        SERVER_TEAM_LIST,
        SERVER_TEAM_DETAIL,
        SERVER_TREE_VERTICAL,
        SERVER_TREE_HORIZONTAL,
        GLOBAL_GENERAL,
        GLOBAL_ITEMS,
        GLOBAL_MOBS
    }

    private enum ScrollbarAxis {
        VERTICAL,
        HORIZONTAL
    }

    private record ScrollbarInfo(
            ScrollbarType type,
            ScrollbarAxis axis,
            int x,
            int y,
            int length,
            int totalEntries,
            int visibleEntries,
            int scroll
    ) {
        public int maxScroll() {
            return Math.max(0, this.totalEntries - this.visibleEntries);
        }

        public boolean isInteractive() {
            return this.maxScroll() > 0 && this.length > 0;
        }

        public int thumbLength() {
            if (!this.isInteractive()) {
                return Math.max(1, this.length);
            }
            int calculated = (int) Math.round((this.visibleEntries / (double) this.totalEntries) * this.length);
            return Mth.clamp(calculated, SCROLLBAR_MIN_THUMB_HEIGHT, this.length);
        }

        public int thumbPosition() {
            int maxScroll = this.maxScroll();
            if (maxScroll <= 0) {
                return this.trackStart();
            }
            int thumbLength = this.thumbLength();
            int travelRange = Math.max(0, this.length - thumbLength);
            return this.trackStart() + (int) Math.round((this.scroll / (double) maxScroll) * travelRange);
        }

        public int trackStart() {
            return this.axis == ScrollbarAxis.VERTICAL ? this.y : this.x;
        }

        public boolean contains(double mouseX, double mouseY) {
            if (this.axis == ScrollbarAxis.VERTICAL) {
                return mouseX >= this.x && mouseX < this.x + SCROLLBAR_WIDTH && mouseY >= this.y && mouseY < this.y + this.length;
            }
            return mouseX >= this.x && mouseX < this.x + this.length && mouseY >= this.y && mouseY < this.y + SCROLLBAR_WIDTH;
        }
    }

    private enum ServerStatsMode {
        PLAYER,
        TEAM,
        SERVER
    }

    private record PlayerClickArea(int x1, int y1, int x2, int y2, UUID playerId) {
        public boolean contains(double mouseX, double mouseY) {
            return mouseX >= this.x1 && mouseX < this.x2 && mouseY >= this.y1 && mouseY < this.y2;
        }
    }

    private record TeamClickArea(int x1, int y1, int x2, int y2, String teamId) {
        public boolean contains(double mouseX, double mouseY) {
            return mouseX >= this.x1 && mouseX < this.x2 && mouseY >= this.y1 && mouseY < this.y2;
        }
    }

    private record TreeNodeRenderInfo(SpaceRaceClientStats.ServerAdvancementNode node, int nodeX, int nodeY) {
    }

    private enum GlobalStatsSection {
        GENERAL,
        ITEMS,
        MOBS
    }

    private enum GlobalItemsColumn {
        MINED("Mined", new ItemStack(Items.DIAMOND_PICKAXE)),
        CRAFTED("Crafted", new ItemStack(Items.CRAFTING_TABLE)),
        USED("Used", new ItemStack(Items.IRON_SWORD)),
        BROKEN("Broken", new ItemStack(Items.CHIPPED_ANVIL)),
        PICKED_UP("Picked Up", new ItemStack(Items.CHEST)),
        DROPPED("Dropped", new ItemStack(Items.DROPPER));

        private final Component tooltip;
        private final ItemStack icon;

        GlobalItemsColumn(String tooltipText, ItemStack icon) {
            this.tooltip = Component.literal(tooltipText);
            this.icon = icon;
        }

        public Component tooltip() {
            return this.tooltip;
        }

        public ItemStack icon() {
            return this.icon;
        }
    }

    private enum Menu {
        MAIN,
        ADD_PLAYERS,
        REMOVE_PLAYERS,
        SERVER_STATS,
        GLOBAL_STATS,
        TEAM_COLOR,
        TEAM_FLAG
    }

    private static class SpaceRaceButton extends Button {

        public SpaceRaceButton(Component component, int x, int y, int width, int height, OnPress onPress) {
            super(x, y, width, height, component, onPress, DEFAULT_NARRATION);
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            int x = this.getX();
            int y = this.getY();
            int backgroundColor = this.isHoveredOrFocused() ? 0xAA1e1e1e : 0xAA000000;
            int lineColor = this.isHoveredOrFocused() ? 0xFF3c3c3c : 0xFF2d2d2d;
            graphics.fill(x, y, x + width, y + height, backgroundColor);
            graphics.renderOutline(x, y, width, height, lineColor);
            Font font = Minecraft.getInstance().font;
            int textPadding = 4;
            int maxLineWidth = Math.max(0, this.width - textPadding * 2);
            List<FormattedCharSequence> lines = font.split(this.getMessage(), maxLineWidth);
            if (lines.isEmpty()) {
                return;
            }

            int lineSpacing = 1;
            int maxLines = Math.max(1, (this.height - textPadding * 2 + lineSpacing) / (font.lineHeight + lineSpacing));
            int lineCount = Math.min(lines.size(), maxLines);
            int totalTextHeight = lineCount * font.lineHeight + (lineCount - 1) * lineSpacing;
            int textY = y + (this.height - totalTextHeight) / 2;
            int centerX = x + this.width / 2;

            for (int i = 0; i < lineCount; i++) {
                graphics.drawCenteredString(font, lines.get(i), centerX, textY + i * (font.lineHeight + lineSpacing), 0xFFFFFFFF);
            }
        }
    }

    private static class CompactTextButton extends SpaceRaceButton {
        public CompactTextButton(Component component, int x, int y, int width, int height, OnPress onPress) {
            super(component, x, y, width, height, onPress);
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            int x = this.getX();
            int y = this.getY();
            int width = this.getWidth();
            int height = this.getHeight();
            int backgroundColor = this.isHoveredOrFocused() ? 0xAA1E1E1E : 0xAA000000;
            int lineColor = this.isHoveredOrFocused() ? 0xFF3C3C3C : 0xFF2D2D2D;
            graphics.fill(x, y, x + width, y + height, backgroundColor);
            graphics.renderOutline(x, y, width, height, lineColor);

            Font font = Minecraft.getInstance().font;
            int maxUnscaledWidth = Math.max(0, Mth.floor((width - 8) / COMPACT_BUTTON_TEXT_SCALE));
            String text = font.plainSubstrByWidth(this.getMessage().getString(), maxUnscaledWidth);
            int scaledWidth = Mth.ceil(font.width(text) * COMPACT_BUTTON_TEXT_SCALE);
            int scaledHeight = Mth.ceil(font.lineHeight * COMPACT_BUTTON_TEXT_SCALE);
            int textX = x + (width - scaledWidth) / 2;
            int textY = y + (height - scaledHeight) / 2;

            PoseStack pose = graphics.pose();
            pose.pushPose();
            pose.translate(textX, textY, 0);
            pose.scale(COMPACT_BUTTON_TEXT_SCALE, COMPACT_BUTTON_TEXT_SCALE, 1.0F);
            graphics.drawString(font, text, 0, 0, 0xFFFFFFFF, false);
            pose.popPose();
        }
    }

    private static class CustomizeFlagButton extends AbstractButton {
        private final Runnable onPress;
        private final ResourceLocation imageLocation;

        public CustomizeFlagButton(int x, int y, int width, int height, ResourceLocation imageLocation, Runnable onPress) {
            super(x, y, width, height, Component.translatable(Translations.SpaceRace.CUSTOMIZE_FLAG));
            this.onPress = onPress;
            this.imageLocation = imageLocation;
        }

        @Override
        public void onPress() {
            this.onPress.run();
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            graphics.blit(this.imageLocation, this.getX(), this.getY(), this.width, this.height, 0, 0, 48, 32, 48, 32);

            graphics.renderOutline(this.getX(), this.getY(), this.width, this.height, this.isHoveredOrFocused() ? 0xFF3c3c3c : 0xFF2d2d2d);

            Font font = Minecraft.getInstance().font;
            graphics.drawCenteredString(font, Component.translatable(Translations.SpaceRace.CUSTOMIZE_FLAG), this.getX() + this.width / 2, this.getY() + this.height / 2 - font.lineHeight / 2, 0xFFFFFFFF);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput builder) {
            this.defaultButtonNarrationText(builder);
        }
    }

    private class TeamColorButton extends AbstractButton {
        private static final MutableComponent line1 = Component.translatable(Translations.SpaceRace.TEAM_COLOR_1);
        private static final MutableComponent line2 = Component.translatable(Translations.SpaceRace.TEAM_COLOR_2);
        private static final MutableComponent line3 = Component.translatable(Translations.SpaceRace.TEAM_COLOR_3);

        public TeamColorButton(int x, int y, int width, int height) {
            super(x, y, width, height, line1.copy().append(line2).append(line3));
        }

        @Override
        public void onPress() {
            setMenu(Menu.TEAM_COLOR);
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            Font font = Minecraft.getInstance().font;
            graphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), teamColor);
            int centerX = this.getX() + this.getWidth() / 2;
            int centerY = this.getY() + this.getHeight() / 2 - font.lineHeight / 2;
            graphics.drawCenteredString(font, line1, centerX, centerY - font.lineHeight, 0xFFFFFFFF);
            graphics.drawCenteredString(font, line2, centerX, centerY, 0xFFFFFFFF);
            graphics.drawCenteredString(font, line3, centerX, centerY + font.lineHeight, 0xFFFFFFFF);

            graphics.renderOutline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.isHoveredOrFocused() ? 0xFF3c3c3c : 0xFF2d2d2d);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput builder) {
            this.defaultButtonNarrationText(builder);
        }
    }

    private static class ColorSlider extends AbstractSliderButton {
        private final Consumer<Integer> consumer;
        private final Component colorName;

        public ColorSlider(int x, int y, int width, int height, Component colorName, int value, Consumer<Integer> consumer) {
            super(x, y, width, height, Component.translatable("options.percent_value", colorName, (int) (value / 255.0 * 100.0)), value / 255.0);
            this.consumer = consumer;
            this.colorName = colorName;
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.translatable("options.percent_value", this.colorName, (int) (this.value * 100.0)));
        }

        @Override
        protected void applyValue() {
            this.consumer.accept((int) (this.value * 255.0));
        }
    }
}
