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

import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.AirlockState;
import dev.galacticraft.mod.content.ProximityAccess;
import dev.galacticraft.mod.content.block.entity.AirlockControllerBlockEntity;
import dev.galacticraft.mod.network.c2s.AirlockSetKeycardOpenSecondsPayload;
import dev.galacticraft.mod.network.c2s.AirlockSetProximityAccessPayload;
import dev.galacticraft.mod.network.c2s.AirlockSetProximityPayload;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class AirlockControllerScreen extends MachineScreen<AirlockControllerBlockEntity, AirlockControllerMenu> {

    private static final int PROXIMITY_FIELD_Y = 45;
    private static final int KEYCARD_FIELD_Y = 65;
    private static final int STATUS_LABEL_X = 90;
    private static final int STATUS_LABEL_Y = 15;
    private static final int ACCESS_BTN_Y = 25;
    private static final ResourceLocation MACHINELIB_PANELS = ResourceLocation.fromNamespaceAndPath("machinelib", "textures/gui/machine_panels.png");
    private static final int TEX_W = 256;
    private static final int TEX_H = 256;
    private static final int BTN_U = 0;
    private static final int BTN_V_NORMAL = 196;
    private static final int BTN_V_HOVER = 216;
    private static final int BTN_V_SELECTED = 236;
    private static final int BTN_W = 20;
    private static final int BTN_H = 20;
    private static final int PUB_U = 208;
    private static final int PUB_V = 49;
    private static final int PUB_W = 15;
    private static final int PUB_H = 15;
    private static final int TEAM_U = 210;
    private static final int TEAM_V = 71;
    private static final int TEAM_W = 12;
    private static final int TEAM_H = 14;
    private static final int PRIV_U = 231;
    private static final int PRIV_V = 49;
    private static final int PRIV_W = 10;
    private static final int PRIV_H = 14;
    private static final int ACCESS_BTN_SIZE = 20;
    private static final int ACCESS_BTN_GAP = 6;
    private final EditBox proximityField;
    private final EditBox keycardTimeField;
    private IconButton publicBtn;
    private IconButton teamBtn;
    private IconButton privateBtn;
    private ProximityAccess cachedAccess = null;

    public AirlockControllerScreen(
            AirlockControllerMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(
                menu,
                title,
                Constant.AirlockController.SCREEN_TEXTURE
        );

        this.proximityField = new EditBox(
                Minecraft.getInstance().font,
                0,
                0,
                26,
                20,
                Component.empty()
        );

        this.proximityField.setValue(String.valueOf(this.menu.proximityOpen));

        this.proximityField.setFilter(value -> {
            if (value.isEmpty()) {
                return true;
            }

            try {
                int parsed = Integer.parseInt(value);

                return parsed >= 0 && parsed <= 5;
            } catch (NumberFormatException ignored) {
                return false;
            }
        });

        this.proximityField.setResponder(value -> {
            if (value.isEmpty() || this.menu.structureManaged) {
                return;
            }

            try {
                byte parsed = Byte.parseByte(value);

                parsed = (byte) Math.max(
                        0,
                        Math.min(
                                5,
                                parsed
                        )
                );

                if (parsed != this.menu.proximityOpen) {
                    this.menu.proximityOpen = parsed;

                    ClientPlayNetworking.send(new AirlockSetProximityPayload(parsed));
                }
            } catch (NumberFormatException ignored) {
            }
        });

        this.keycardTimeField = new EditBox(
                Minecraft.getInstance().font,
                0,
                0,
                26,
                20,
                Component.empty()
        );

        this.keycardTimeField.setValue(String.valueOf(this.menu.keycardOpenSeconds));

        this.keycardTimeField.setFilter(value -> {
            if (value.isEmpty()) {
                return true;
            }

            try {
                int parsed = Integer.parseInt(value);

                return parsed >= AirlockControllerBlockEntity.MIN_KEYCARD_OPEN_SECONDS && parsed <= AirlockControllerBlockEntity.MAX_KEYCARD_OPEN_SECONDS;
            } catch (NumberFormatException ignored) {
                return false;
            }
        });

        this.keycardTimeField.setResponder(value -> {
            if (value.isEmpty() || this.menu.structureManaged) {
                return;
            }

            try {
                int parsed = Mth.clamp(
                        Integer.parseInt(value),
                        AirlockControllerBlockEntity.MIN_KEYCARD_OPEN_SECONDS,
                        AirlockControllerBlockEntity.MAX_KEYCARD_OPEN_SECONDS
                );

                if (parsed != this.menu.keycardOpenSeconds) {

                    this.menu.keycardOpenSeconds = parsed;

                    ClientPlayNetworking.send(new AirlockSetKeycardOpenSecondsPayload((byte) parsed));
                }
            } catch (NumberFormatException ignored) {
            }
        });
    }

    private ProximityAccess currentAccess() {
        return this.menu.proximityAccess != null ? this.menu.proximityAccess : ProximityAccess.PUBLIC;
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        ProximityAccess access = currentAccess();

        if (access != this.cachedAccess) {
            if (this.publicBtn != null && this.teamBtn != null && this.privateBtn != null) {

                this.publicBtn.setSelected(access == ProximityAccess.PUBLIC);

                this.teamBtn.setSelected(access == ProximityAccess.TEAM);

                this.privateBtn.setSelected(access == ProximityAccess.PRIVATE);
            }

            this.cachedAccess = access;
        }

        if (!this.proximityField.isFocused()) {
            String expected = String.valueOf(this.menu.proximityOpen);

            if (!this.proximityField.getValue().equals(expected)) {
                this.proximityField.setValue(expected);
            }
        }

        if (!this.keycardTimeField.isFocused()) {
            String expected = String.valueOf(this.menu.keycardOpenSeconds);

            if (!this.keycardTimeField.getValue().equals(expected)) {
                this.keycardTimeField.setValue(expected);
            }
        }

        updateManagedWidgets();
    }

    @Override
    protected void init() {
        super.init();

        this.imageHeight = 171;
        this.titleLabelX = 90;

        positionFields();

        this.addRenderableWidget(this.proximityField);

        this.addRenderableWidget(this.keycardTimeField);

        ProximityAccess initial = currentAccess();

        this.publicBtn = new IconButton(
            0,
            0,
            ACCESS_BTN_SIZE,
            MACHINELIB_PANELS,
            PUB_U,
            PUB_V,
            PUB_W,
            PUB_H,
            button -> {
                this.menu.proximityAccess = ProximityAccess.PUBLIC;

                ClientPlayNetworking.send(new AirlockSetProximityAccessPayload(ProximityAccess.PUBLIC));
            },
            Component.translatable(Translations.Ui.MACHINE_LIB_PUBLIC_ACCESS)
        );

        this.teamBtn = new IconButton(
            0,
            0,
            ACCESS_BTN_SIZE,
            MACHINELIB_PANELS,
            TEAM_U,
            TEAM_V,
            TEAM_W,
            TEAM_H,
            button -> {
                this.menu.proximityAccess = ProximityAccess.TEAM;

                ClientPlayNetworking.send(new AirlockSetProximityAccessPayload(ProximityAccess.TEAM));
            },
            Component.translatable(Translations.Ui.MACHINE_LIB_TEAM_ACCESS)
        );

        this.privateBtn = new IconButton(
            0,
            0,
            ACCESS_BTN_SIZE,
            MACHINELIB_PANELS,
            PRIV_U,
            PRIV_V,
            PRIV_W,
            PRIV_H,
            button -> {
                this.menu.proximityAccess = ProximityAccess.PRIVATE;

                ClientPlayNetworking.send(new AirlockSetProximityAccessPayload(ProximityAccess.PRIVATE));
            },
            Component.translatable(Translations.Ui.MACHINE_LIB_PRIVATE_ACCESS)
        );

        this.addRenderableWidget(this.publicBtn);

        this.addRenderableWidget(this.teamBtn);

        this.addRenderableWidget(this.privateBtn);

        this.publicBtn.setSelected(initial == ProximityAccess.PUBLIC);

        this.teamBtn.setSelected(initial == ProximityAccess.TEAM);

        this.privateBtn.setSelected(initial == ProximityAccess.PRIVATE);

        this.cachedAccess = initial;

        layoutAccessButtons();

        updateManagedWidgets();
    }

    @Override
    protected void repositionElements() {
        super.repositionElements();

        positionFields();

        layoutAccessButtons();
    }

    private void positionFields() {
        this.proximityField.setX(this.leftPos + 132);

        this.proximityField.setY(this.topPos + PROXIMITY_FIELD_Y);

        this.keycardTimeField.setX(this.leftPos + 132);

        this.keycardTimeField.setY(this.topPos + KEYCARD_FIELD_Y);
    }

    private void layoutAccessButtons() {
        if (this.publicBtn == null || this.teamBtn == null || this.privateBtn == null) {
            return;
        }

        int centerX = this.leftPos + this.imageWidth / 2;

        int total = ACCESS_BTN_SIZE * 3 + ACCESS_BTN_GAP * 2;

        int startX = centerX - total / 2;

        this.publicBtn.setPosition(
                startX,
                this.topPos + ACCESS_BTN_Y
        );

        this.teamBtn.setPosition(
                startX + ACCESS_BTN_SIZE + ACCESS_BTN_GAP,
                this.topPos + ACCESS_BTN_Y
        );

        this.privateBtn.setPosition(
                startX + (ACCESS_BTN_SIZE + ACCESS_BTN_GAP) * 2,
                this.topPos + ACCESS_BTN_Y
        );
    }

    private void updateManagedWidgets() {
        boolean configurable = !this.menu.structureManaged;

        this.proximityField.visible = configurable;

        this.keycardTimeField.visible = configurable;

        this.proximityField.setEditable(configurable);

        this.keycardTimeField.setEditable(configurable);

        if (this.publicBtn != null) {
            this.publicBtn.visible = configurable;

            this.publicBtn.active = configurable;
        }

        if (this.teamBtn != null) {
            this.teamBtn.visible = configurable;

            this.teamBtn.active = configurable;
        }

        if (this.privateBtn != null) {
            this.privateBtn.visible = configurable;

            this.privateBtn.active = configurable;
        }
    }

    @Override
    protected void renderMachineBackground(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {
        AirlockState enabled = this.menu.state;

        Component label;
        int color;

        if (enabled == AirlockState.ALL) {
            label = Component.translatable(Translations.Ui.AIRLOCK_ENABLED);

            color = ChatFormatting.DARK_GREEN.getColor();
        } else if (enabled == AirlockState.PARTIAL) {
            label = Component.translatable(Translations.Ui.AIRLOCK_PARTIAL);

            color = ChatFormatting.DARK_PURPLE.getColor();
        } else {
            label = Component.translatable(Translations.Ui.AIRLOCK_DISABLED);

            color = ChatFormatting.RED.getColor();
        }

        drawCenteredString(
            graphics,
            this.font,
            label,
            this.leftPos + STATUS_LABEL_X,
            this.topPos + STATUS_LABEL_Y,
            color,
            false
        );

        if (this.menu.structureManaged) {
            drawCenteredString(
                graphics,
                this.font,
                Component.translatable(Translations.Ui.AIRLOCK_STRUCTURE_MANAGED),
                this.leftPos + 90,
                this.topPos + 55,
                ChatFormatting.DARK_GRAY.getColor(),
                false
            );

            drawCenteredString(
                graphics,
                this.font,
                Component.translatable(Translations.Ui.AIRLOCK_CONFIGURATION_LOCKED),
                this.leftPos + 90,
                this.topPos + 72,
                ChatFormatting.RED.getColor(),
                false
            );

            drawCenteredString(
                graphics,
                this.font,
                this.menu.permanentlyUnlocked
                    ? Component.translatable(Translations.Ui.AIRLOCK_UNLOCKED_BY_KEYCARD)
                    : Component.translatable(Translations.Ui.AIRLOCK_REQUIRES_KEYCARD),
                this.leftPos + 90,
                this.topPos + 89,
                ChatFormatting.DARK_GRAY.getColor(),
                false
            );

            return;
        }

        drawControlRow(
            graphics,
            mouseX,
            mouseY,
            PROXIMITY_FIELD_Y,
            Component.translatable(Translations.Ui.AIRLOCK_PROXIMITY_LABEL)
        );

        drawControlRow(
            graphics,
            mouseX,
            mouseY,
            KEYCARD_FIELD_Y,
            Component.translatable(Translations.Ui.AIRLOCK_KEYCARD_OPEN_TIME)
        );
    }

    private void drawControlRow(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            int fieldY,
            Component label
    ) {
        int upX = this.leftPos + 158;

        int upY = this.topPos + fieldY;

        int downX = this.leftPos + 158;

        int downY = this.topPos + fieldY + 10;

        boolean hoverUp = DrawableUtil.mouseIn(
            mouseX,
            mouseY,
            upX,
            upY,
            Constant.AirlockController.ARROW_VERTICAL_WIDTH,
            Constant.AirlockController.ARROW_VERTICAL_HEIGHT
        );

        boolean hoverDown = DrawableUtil.mouseIn(
            mouseX,
            mouseY,
            downX,
            downY,
            Constant.AirlockController.ARROW_VERTICAL_WIDTH,
            Constant.AirlockController.ARROW_VERTICAL_HEIGHT
        );

        graphics.blit(
            Constant.AirlockController.SCREEN_TEXTURE,
            upX,
            upY,
            hoverUp
                ? Constant.AirlockController.ARROW_UP_HOVER_U
                : Constant.AirlockController.ARROW_UP_U,
            Constant.AirlockController.ARROW_UP_HOVER_V,
            Constant.AirlockController.ARROW_VERTICAL_WIDTH,
            Constant.AirlockController.ARROW_VERTICAL_HEIGHT
        );

        graphics.blit(
            Constant.AirlockController.SCREEN_TEXTURE,
            downX,
            downY,
            hoverDown
                ? Constant.AirlockController.ARROW_DOWN_HOVER_U
                : Constant.AirlockController.ARROW_DOWN_U,
            Constant.AirlockController.ARROW_DOWN_HOVER_V,
            Constant.AirlockController.ARROW_VERTICAL_WIDTH,
            Constant.AirlockController.ARROW_VERTICAL_HEIGHT
        );

        drawStringAlignedRight(
            graphics,
            this.font,
            label,
            this.leftPos + 130,
            this.topPos + fieldY + 6,
            ChatFormatting.DARK_GRAY.getColor(),
            false
        );
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (this.menu.structureManaged) {
            return super.mouseClicked(
                    mouseX,
                    mouseY,
                    button
            );
        }

        if (button == 0) {
            if (handleProximityArrow(
                    mouseX,
                    mouseY
            )) {
                return true;
            }

            if (handleKeycardArrow(
                    mouseX,
                    mouseY
            )) {
                return true;
            }
        }

        return super.mouseClicked(
                mouseX,
                mouseY,
                button
        );
    }

    private boolean handleProximityArrow(
            double mouseX,
            double mouseY
    ) {
        int upX = this.leftPos + 158;

        int upY = this.topPos + PROXIMITY_FIELD_Y;

        int downY = upY + 10;

        if (DrawableUtil.mouseIn(
            mouseX,
            mouseY,
            upX,
            upY,
            Constant.AirlockController.ARROW_VERTICAL_WIDTH,
            Constant.AirlockController.ARROW_VERTICAL_HEIGHT
        )) {
            if (this.menu.proximityOpen < 5) {
                byte next = (byte) (this.menu.proximityOpen + 1);

                this.menu.proximityOpen = next;

                this.proximityField.setValue(String.valueOf(next));

                ClientPlayNetworking.send(new AirlockSetProximityPayload(next));

                this.playButtonSound();
            }

            return true;
        }

        if (DrawableUtil.mouseIn(
            mouseX,
            mouseY,
            upX,
            downY,
            Constant.AirlockController.ARROW_VERTICAL_WIDTH,
            Constant.AirlockController.ARROW_VERTICAL_HEIGHT
        )) {
            if (this.menu.proximityOpen > 0) {
                byte next = (byte) (this.menu.proximityOpen - 1);

                this.menu.proximityOpen = next;

                this.proximityField.setValue(String.valueOf(next));

                ClientPlayNetworking.send(new AirlockSetProximityPayload(next));

                this.playButtonSound();
            }

            return true;
        }

        return false;
    }

    private boolean handleKeycardArrow(
            double mouseX,
            double mouseY
    ) {
        int upX = this.leftPos + 158;

        int upY = this.topPos + KEYCARD_FIELD_Y;

        int downY = upY + 10;

        if (DrawableUtil.mouseIn(
                mouseX,
                mouseY,
                upX,
                upY,
                Constant.AirlockController.ARROW_VERTICAL_WIDTH,
                Constant.AirlockController.ARROW_VERTICAL_HEIGHT
        )) {
            if (this.menu.keycardOpenSeconds < AirlockControllerBlockEntity.MAX_KEYCARD_OPEN_SECONDS) {

                int next = this.menu.keycardOpenSeconds + 1;

                setKeycardOpenSeconds(next);
            }

            return true;
        }

        if (DrawableUtil.mouseIn(
            mouseX,
            mouseY,
            upX,
            downY,
            Constant.AirlockController.ARROW_VERTICAL_WIDTH,
            Constant.AirlockController.ARROW_VERTICAL_HEIGHT
        )) {
            if (this.menu.keycardOpenSeconds > AirlockControllerBlockEntity.MIN_KEYCARD_OPEN_SECONDS) {

                int next = this.menu.keycardOpenSeconds - 1;

                setKeycardOpenSeconds(next);
            }

            return true;
        }

        return false;
    }

    private void setKeycardOpenSeconds(
            int seconds
    ) {
        this.menu.keycardOpenSeconds = seconds;

        this.keycardTimeField.setValue(String.valueOf(seconds));

        ClientPlayNetworking.send(new AirlockSetKeycardOpenSecondsPayload((byte) seconds));

        this.playButtonSound();
    }

    @Override
    protected void drawTitle(
            @NotNull GuiGraphics graphics
    ) {
        drawCenteredString(
                graphics,
                this.font,
                this.title,
                this.titleLabelX,
                this.titleLabelY,
                0xFF404040,
                false
        );
    }

    public void drawCenteredString(
            GuiGraphics graphics,
            Font font,
            Component text,
            int centerX,
            int y,
            int color,
            boolean shadow
    ) {
        graphics.drawString(
            font,
            text,
            centerX - font.width(text) / 2,
            y,
            color,
            shadow
        );
    }

    public void drawStringAlignedRight(
            GuiGraphics graphics,
            Font font,
            Component text,
            int x,
            int y,
            int color,
            boolean shadow
    ) {
        graphics.drawString(
            font,
            text,
            x - font.width(text),
            y,
            color,
            shadow
        );
    }

    private static class IconButton extends AbstractButton {
        private final ResourceLocation texture;
        private final int iconU;
        private final int iconV;
        private final int iconW;
        private final int iconH;
        private final PressHandler handler;
        private boolean selected;
        IconButton(
                int x,
                int y,
                int size,
                ResourceLocation texture,
                int iconU,
                int iconV,
                int iconW,
                int iconH,
                PressHandler handler,
                Component tooltipText
        ) {
            super(
                x,
                y,
                size,
                size,
                Component.empty()
            );

            this.texture = texture;

            this.iconU = iconU;
            this.iconV = iconV;
            this.iconW = iconW;
            this.iconH = iconH;

            this.handler = handler;

            if (!tooltipText.getString().isEmpty()) {
                this.setTooltip(Tooltip.create(tooltipText));
            }
        }

        void setSelected(
                boolean selected
        ) {
            this.selected = selected;
        }

        @Override
        public void onPress() {
            this.handler.onPress(this);
        }

        @Override
        protected void renderWidget(
                GuiGraphics graphics,
                int mouseX,
                int mouseY,
                float delta
        ) {
            int v = this.selected ? BTN_V_SELECTED : (this.isHovered() ? BTN_V_HOVER : BTN_V_NORMAL);

            graphics.blit(
                this.texture,
                getX(),
                getY(),
                BTN_U,
                v,
                BTN_W,
                BTN_H,
                TEX_W,
                TEX_H
            );

            int iconX = getX() + (this.width - this.iconW) / 2 + 1;

            int iconY = getY() + (this.height - this.iconH) / 2;

            graphics.blit(
                this.texture,
                iconX,
                iconY,
                this.iconU,
                this.iconV,
                this.iconW,
                this.iconH,
                TEX_W,
                TEX_H
            );
        }

        @Override
        protected void updateWidgetNarration(
                NarrationElementOutput output
        ) {
        }

        @FunctionalInterface
        interface PressHandler {
            void onPress(
                IconButton button
            );
        }
    }
}