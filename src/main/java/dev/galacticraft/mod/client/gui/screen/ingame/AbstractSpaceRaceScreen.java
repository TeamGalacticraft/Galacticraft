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

import dev.galacticraft.mod.client.gui.screen.ingame.spacerace.ScrollbarAxis;
import dev.galacticraft.mod.client.gui.screen.ingame.spacerace.ScrollbarInfo;
import dev.galacticraft.mod.client.gui.screen.ingame.spacerace.ScrollbarType;
import dev.galacticraft.mod.client.gui.widget.SpaceRaceButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.UUID;

abstract class AbstractSpaceRaceScreen extends Screen {
    protected static final int STATS_PANEL_PADDING = 8;
    protected static final int STATS_ENTRY_HEIGHT = 22;
    protected static final int STATS_ENTRY_SPACING = 2;
    protected static final int STATS_ENTRY_CONTENT_PADDING = 6;
    protected static final int OVERVIEW_ENTRY_HEIGHT = 20;
    protected static final int OVERVIEW_ENTRY_SPACING = 2;
    protected static final int PLAYER_HEAD_SIZE = 16;
    protected static final int PLAYER_HEAD_SPACING = 8;
    protected static final int ADVANCEMENT_ICON_SIZE = 16;
    protected static final int ADVANCEMENT_ICON_SPACING = 2;
    protected static final int ADVANCEMENT_TEXT_ICON_SPACING = 6;
    protected static final int STATS_TOGGLE_BUTTON_WIDTH = 170;
    protected static final int STATS_TOGGLE_BUTTON_HEIGHT = 16;
    protected static final int COMPACT_TOGGLE_BUTTON_HEIGHT = 16;
    protected static final int COMPACT_TOGGLE_BUTTON_SPACING = 4;
    protected static final int SERVER_BOTTOM_CONTROL_RESERVED = COMPACT_TOGGLE_BUTTON_HEIGHT + 8;
    protected static final int SERVER_BOTTOM_CONTROL_RESERVED_SERVER_TAB = COMPACT_TOGGLE_BUTTON_HEIGHT + 5;
    protected static final int SERVER_MODE_BUTTON_HEIGHT = 16;
    protected static final int SERVER_MODE_BUTTON_SPACING = 4;
    protected static final int ADVANCEMENT_ROW_HEIGHT = 20;
    protected static final int ADVANCEMENT_ROW_SPACING = 3;
    protected static final int TREE_NODE_SIZE = 20;
    protected static final int TREE_NODE_SPACING_X = 30;
    protected static final int TREE_NODE_SPACING_Y = 26;
    protected static final int TREE_CANVAS_PADDING = 24;
    protected static final int TREE_LINE_COLOR = 0xAA5A5A5A;
    protected static final int TREE_NODE_INCOMPLETE_FILL = 0x55272727;
    protected static final int TREE_NODE_COMPLETE_FILL = 0x553E6231;
    protected static final int TREE_NODE_BORDER_INCOMPLETE = 0xAA666666;
    protected static final int TREE_NODE_BORDER_COMPLETE = 0xAA96C56B;
    protected static final int GLOBAL_TAB_SPACING = 6;
    protected static final int GLOBAL_TAB_HEIGHT = 20;
    protected static final int GLOBAL_TAB_BOTTOM_PADDING = 6;
    protected static final int GLOBAL_CONTENT_SPACING = 6;
    protected static final int GLOBAL_GENERAL_ROW_HEIGHT = 18;
    protected static final int GLOBAL_GENERAL_TOTAL_WIDTH = 54;
    protected static final int GLOBAL_MOBS_ROW_HEIGHT = 18;
    protected static final int GLOBAL_ITEMS_HEADER_HEIGHT = 20;
    protected static final int GLOBAL_ITEMS_ROW_HEIGHT = 24;
    protected static final int GLOBAL_ITEMS_ICON_COLUMN_WIDTH = 22;
    protected static final int GLOBAL_ITEMS_MIN_CELL_WIDTH = 48;
    protected static final int GLOBAL_ITEMS_MAX_CELL_WIDTH = 72;
    protected static final int GLOBAL_HEAD_SIZE = 12;
    protected static final int GLOBAL_ITEM_ICON_SIZE = 16;
    protected static final int SCROLLBAR_WIDTH = 6;
    protected static final int SCROLLBAR_SPACING = 2;

    protected int backgroundWidth = 0;
    protected int backgroundHeight = 0;
    protected boolean animationCompleted = false;
    protected ScrollbarType activeScrollbar = ScrollbarType.NONE;
    protected int activeScrollbarThumbOffset = 0;

    protected AbstractSpaceRaceScreen(Component title) {
        super(title);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void resize(Minecraft client, int width, int height) {
        this.backgroundWidth = (int) (width - ((this.getMarginPercent() * width) * 1.5D));
        this.backgroundHeight = (int) (height - ((this.getMarginPercent() * height) * 1.5D));
        super.resize(client, width, height);
    }

    protected SpaceRaceButton addButton(Component text, int x, int y, int width, int height, Button.OnPress onPress) {
        return this.addRenderableWidget(new SpaceRaceButton(text, x, y, width, height, onPress));
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

    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawCenteredString(this.font, this.title, this.width / 2, this.getTop() - 20, 0xFFFFFF);
    }

    protected void drawMouseoverTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
    }

    protected void clearTransientRenderState() {
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (this.animationCompleted) {
            this.clearTransientRenderState();
            super.render(graphics, mouseX, mouseY, delta);
            this.renderForeground(graphics, mouseX, mouseY);
            this.drawMouseoverTooltip(graphics, mouseX, mouseY);
        } else {
            this.renderBackground(graphics, mouseX, mouseY, delta);
        }
    }

    protected int getBottom() {
        return this.getTop() + this.backgroundHeight;
    }

    protected int getLeft() {
        return (this.width / 2) - (this.backgroundWidth / 2);
    }

    protected int getTop() {
        return (this.height / 2) - (this.backgroundHeight / 2);
    }

    protected int getRight() {
        return this.getLeft() + this.backgroundWidth;
    }

    protected void renderPlayerHead(GuiGraphics graphics, UUID playerId, int x, int y, int size) {
        if (this.minecraft != null && this.minecraft.player != null && this.minecraft.player.connection != null) {
            PlayerInfo playerInfo = this.minecraft.player.connection.getPlayerInfo(playerId);
            if (playerInfo != null) {
                PlayerFaceRenderer.draw(graphics, playerInfo.getSkin(), x, y, size);
                return;
            }
        }

        PlayerFaceRenderer.draw(graphics, DefaultPlayerSkin.get(playerId), x, y, size);
    }

    protected void renderScrollbar(GuiGraphics graphics, ScrollbarInfo scrollbar, int mouseX, int mouseY) {
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

    private float getMarginPercent() {
        return 0.17F;
    }
}
