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

import dev.galacticraft.mod.content.AirlockState;
import dev.galacticraft.mod.content.ProximityAccess;
import dev.galacticraft.mod.network.c2s.AirlockSetProximityAccessPayload;
import dev.galacticraft.mod.network.c2s.AirlockSetProximityPayload;
import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.AirlockControllerBlockEntity;
import dev.galacticraft.mod.screen.AirlockControllerMenu;
import dev.galacticraft.mod.util.DrawableUtil;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class AirlockControllerScreen extends MachineScreen<AirlockControllerBlockEntity, AirlockControllerMenu> {
    private final EditBox textField;

    private IconButton publicBtn;
    private IconButton teamBtn;
    private IconButton privateBtn;

    private static final ResourceLocation MACHINELIB_PANELS =
            ResourceLocation.fromNamespaceAndPath("machinelib", "textures/gui/machine_panels.png");

    private static final int TEX_W = 256, TEX_H = 256;

    private static final int BTN_U = 0;
    private static final int BTN_V_NORMAL   = 196;
    private static final int BTN_V_HOVER    = 216;
    private static final int BTN_V_SELECTED = 236;
    private static final int BTN_W = 20, BTN_H = 20;

    private static final int PUB_U = 208, PUB_V = 49,  PUB_W = 15, PUB_H = 15;
    private static final int TEAM_U = 210, TEAM_V = 71, TEAM_W = 12, TEAM_H = 14;
    private static final int PRIV_U = 231, PRIV_V = 49,  PRIV_W = 10, PRIV_H = 14;

    private static final int ACCESS_BTN_SIZE = 20;
    private static final int ACCESS_BTN_GAP = 6;

    private enum AccessMode { PUBLIC, TEAM, PRIVATE }
    private AccessMode selected = AccessMode.PUBLIC;
    private ProximityAccess cachedAccess = null;

    private ProximityAccess currentAccess() {
        return this.menu.proximityAccess != null ? this.menu.proximityAccess : ProximityAccess.PUBLIC;
    }

    private static class IconButton extends AbstractButton {
        @FunctionalInterface interface PressHandler { void onPress(IconButton b); }

        private final ResourceLocation texture;
        private final int iconU, iconV, iconW, iconH;
        private final PressHandler handler;

        private boolean selected;

        IconButton(int x, int y, int size,
                   ResourceLocation texture,
                   int iconU, int iconV, int iconW, int iconH,
                   PressHandler handler,
                   Component tooltipText) {
            super(x, y, size, size, Component.empty());
            this.texture = texture;
            this.iconU = iconU; this.iconV = iconV; this.iconW = iconW; this.iconH = iconH;
            this.handler = handler;
            if (!tooltipText.getString().isEmpty()) this.setTooltip(Tooltip.create(tooltipText));
        }

        void setSelected(boolean sel) { this.selected = sel; }

        @Override public void onPress() { handler.onPress(this); }

        @Override
        protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float delta) {
            final int v = this.selected ? BTN_V_SELECTED : (this.isHovered() ? BTN_V_HOVER : BTN_V_NORMAL);
            g.blit(texture, getX(), getY(), BTN_U, v, BTN_W, BTN_H, TEX_W, TEX_H);

            int ix = getX() + (this.width  - iconW) / 2 + 1;
            int iy = getY() + (this.height - iconH) / 2;
            g.blit(texture, ix, iy, iconU, iconV, iconW, iconH, TEX_W, TEX_H);
        }

        @Override protected void updateWidgetNarration(NarrationElementOutput out) {}
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        ProximityAccess now = currentAccess();
        if (now != this.cachedAccess) {
            if (publicBtn != null && teamBtn != null && privateBtn != null) {
                publicBtn.setSelected(now == ProximityAccess.PUBLIC);
                teamBtn.setSelected(now == ProximityAccess.TEAM);
                privateBtn.setSelected(now == ProximityAccess.PRIVATE);
            }
            this.cachedAccess = now;
        }
    }

    public AirlockControllerScreen(AirlockControllerMenu menu, Inventory inv, Component title) {
        super(menu, title, Constant.ScreenTexture.AIRLOCK_CONTROLLER_SCREEN);

        this.textField = new EditBox(
                Minecraft.getInstance().font,
                this.leftPos + 132, this.topPos + 65,
                26, 20,
                Component.empty()
        );
        this.textField.setValue(String.valueOf(this.menu.proximityOpen));

        this.textField.setFilter(s -> {
            if (s.isEmpty()) return true;
            try {
                int v = Integer.parseInt(s);
                return v >= 0 && v <= 5;
            } catch (NumberFormatException ignore) {
                return false;
            }
        });

        this.textField.setResponder(s -> {
            if (s.isEmpty()) return;
            byte value;
            try {
                value = Byte.parseByte(s);
            } catch (NumberFormatException ignore) {
                return;
            }
            if (value < 0) value = 0;
            if (value > 5) value = 5;

            String clamped = String.valueOf(value);
            if (!s.equals(clamped)) {
                this.textField.setValue(clamped);
                return;
            }

            if (value != this.menu.proximityOpen) {
                this.menu.proximityOpen = value;
                ClientPlayNetworking.send(new AirlockSetProximityPayload(value));
            }
        });
    }

    @Override
    protected void init() {
        super.init();
        this.imageHeight = 171;
        this.titleLabelX = 90;

        this.textField.setX(this.leftPos + 132);
        this.textField.setY(this.topPos + 65);
        this.addRenderableWidget(this.textField);

        ProximityAccess initial = currentAccess();
        switch (initial) {
            case PUBLIC  -> selected = AccessMode.PUBLIC;
            case TEAM    -> selected = AccessMode.TEAM;
            case PRIVATE -> selected = AccessMode.PRIVATE;
        }

        this.publicBtn = new IconButton(0, 0, ACCESS_BTN_SIZE, MACHINELIB_PANELS, PUB_U,  PUB_V,  PUB_W,  PUB_H, b -> {
            selected = AccessMode.PUBLIC;
            publicBtn.setSelected(true); teamBtn.setSelected(false); privateBtn.setSelected(false);
            this.menu.proximityAccess = ProximityAccess.PUBLIC;
            ClientPlayNetworking.send(new AirlockSetProximityAccessPayload(ProximityAccess.PUBLIC));
        }, Component.literal("Public"));

        this.teamBtn = new IconButton(0, 0, ACCESS_BTN_SIZE, MACHINELIB_PANELS, TEAM_U, TEAM_V, TEAM_W, TEAM_H, b -> {
            selected = AccessMode.TEAM;
            publicBtn.setSelected(false); teamBtn.setSelected(true); privateBtn.setSelected(false);
            this.menu.proximityAccess = ProximityAccess.TEAM;
            ClientPlayNetworking.send(new AirlockSetProximityAccessPayload(ProximityAccess.TEAM));
        }, Component.literal("Team"));

        this.privateBtn = new IconButton(0, 0, ACCESS_BTN_SIZE, MACHINELIB_PANELS, PRIV_U, PRIV_V, PRIV_W, PRIV_H, b -> {
            selected = AccessMode.PRIVATE;
            publicBtn.setSelected(false); teamBtn.setSelected(false); privateBtn.setSelected(true);
            this.menu.proximityAccess = ProximityAccess.PRIVATE;
            ClientPlayNetworking.send(new AirlockSetProximityAccessPayload(ProximityAccess.PRIVATE));
        }, Component.literal("Private"));

        this.addRenderableWidget(this.publicBtn);
        this.addRenderableWidget(this.teamBtn);
        this.addRenderableWidget(this.privateBtn);

        publicBtn.setSelected(initial == ProximityAccess.PUBLIC);
        teamBtn.setSelected(initial == ProximityAccess.TEAM);
        privateBtn.setSelected(initial == ProximityAccess.PRIVATE);

        this.cachedAccess = initial;

        layoutAccessButtons();
    }

    @Override
    protected void repositionElements() {
        super.repositionElements();
        layoutAccessButtons();
    }

    private void layoutAccessButtons() {
        if (publicBtn == null || teamBtn == null || privateBtn == null) return;

        int centerX = this.leftPos + (this.imageWidth / 2);
        int centerY = this.topPos + (this.imageHeight / 4);

        int total = ACCESS_BTN_SIZE * 3 + ACCESS_BTN_GAP * 2;
        int startX = centerX - total / 2;
        int y = centerY - ACCESS_BTN_SIZE / 2;

        this.publicBtn.setPosition(startX, y);
        this.teamBtn.setPosition(startX + ACCESS_BTN_SIZE + ACCESS_BTN_GAP, y);
        this.privateBtn.setPosition(startX + (ACCESS_BTN_SIZE + ACCESS_BTN_GAP) * 2, y);

        this.publicBtn.active = this.publicBtn.visible = true;
        this.teamBtn.active = this.teamBtn.visible = true;
        this.privateBtn.active = this.privateBtn.visible = true;
    }

    @Override
    protected void renderMachineBackground(GuiGraphics g, int mouseX, int mouseY, float delta) {
        AirlockState enabled = this.menu.state;
        Component label;
        int color;
        if (enabled.equals(AirlockState.ALL)) {
            label = Component.translatable(Translations.Ui.AIRLOCK_ENABLED);
            color = ChatFormatting.DARK_GREEN.getColor();
        } else if (enabled.equals(AirlockState.PARTIAL)) {
            label = Component.translatable(Translations.Ui.AIRLOCK_PARTIAL);
            color = ChatFormatting.DARK_PURPLE.getColor();
        } else {
            label = Component.translatable(Translations.Ui.AIRLOCK_DISABLED);
            color = ChatFormatting.RED.getColor();
        }

        drawCenteredString(g, this.font, label, this.leftPos + 90, this.topPos + 22, color, false);

        int upX = this.leftPos + 158, upY = this.topPos + 65;
        int downX = this.leftPos + 158, downY = this.topPos + 75;

        boolean hoverUp = DrawableUtil.isWithin(mouseX, mouseY, upX, upY, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT);
        boolean hoverDown = DrawableUtil.isWithin(mouseX, mouseY, downX, downY, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT);

        g.blit(Constant.ScreenTexture.OVERLAY, upX, upY,
                hoverUp ? Constant.TextureCoordinate.ARROW_UP_HOVER_X : Constant.TextureCoordinate.ARROW_UP_X,
                hoverUp ? Constant.TextureCoordinate.ARROW_UP_HOVER_Y : Constant.TextureCoordinate.ARROW_UP_Y,
                Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT);

        g.blit(Constant.ScreenTexture.OVERLAY, downX, downY,
                hoverDown ? Constant.TextureCoordinate.ARROW_DOWN_HOVER_X : Constant.TextureCoordinate.ARROW_DOWN_X,
                hoverDown ? Constant.TextureCoordinate.ARROW_DOWN_HOVER_Y : Constant.TextureCoordinate.ARROW_DOWN_Y,
                Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT);

        drawStringAlignedRight(g, this.font, Component.translatable(Translations.Ui.AIRLOCK_PROXIMITY_LABEL), this.leftPos + 130, this.topPos + 71, ChatFormatting.DARK_GRAY.getColor(), false);
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderForeground(graphics, mouseX, mouseY, delta);

        this.textField.setX(this.leftPos + 132);
        this.textField.setY(this.topPos + 65);

        String shouldBe = String.valueOf(this.menu.proximityOpen);
        if (!this.textField.getValue().equals(shouldBe) && !this.textField.isFocused()) {
            this.textField.setValue(shouldBe);
        }
    }

    @Override
    protected void drawTitle(@NotNull GuiGraphics graphics) {
        drawCenteredString(graphics, this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF404040, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int upX = this.leftPos + 158, upY = this.topPos + 65;
            int downX = this.leftPos + 158, downY = this.topPos + 75;

            if (DrawableUtil.isWithin(mouseX, mouseY, upX, upY, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT)) {
                if (this.menu.proximityOpen < 5) {
                    byte next = (byte) (this.menu.proximityOpen + 1);
                    this.menu.proximityOpen = next;
                    this.textField.setValue(String.valueOf(next));
                    ClientPlayNetworking.send(new AirlockSetProximityPayload(next));
                    this.playButtonSound();
                    return true;
                }
            }

            if (DrawableUtil.isWithin(mouseX, mouseY, downX, downY, Constant.TextureCoordinate.ARROW_VERTICAL_WIDTH, Constant.TextureCoordinate.ARROW_VERTICAL_HEIGHT)) {
                if (this.menu.proximityOpen > 0) {
                    byte next = (byte) (this.menu.proximityOpen - 1);
                    this.menu.proximityOpen = next;
                    this.textField.setValue(String.valueOf(next));
                    ClientPlayNetworking.send(new AirlockSetProximityPayload(next));
                    this.playButtonSound();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void drawCenteredString(GuiGraphics g, Font font, Component text, int centerX, int y, int color, boolean shadow) {
        g.drawString(font, text, centerX - font.width(text) / 2, y, color, shadow);
    }

    public void drawStringAlignedRight(GuiGraphics g, Font font, Component text, int x, int y, int color, boolean shadow) {
        g.drawString(font, text, x - this.font.width(text), y, color, shadow);
    }
}