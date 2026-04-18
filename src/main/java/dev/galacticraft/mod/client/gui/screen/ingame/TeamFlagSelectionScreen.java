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

import dev.galacticraft.impl.network.c2s.FlagDataPayload;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.resources.TeamFlagTextureManager;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class TeamFlagSelectionScreen extends Screen {
    private static final int ERROR_COLOR = 0xFFFF8080;
    private static final int INFO_COLOR = 0xFFE0E0E0;
    private static final int SUCCESS_COLOR = 0xFF9EEB9E;
    private static final int INPUT_WIDTH = 164;
    private static final int URL_BUTTON_WIDTH = 74;
    private static final int MAX_PREVIEW_WIDTH = 192;
    private static final int ACTION_BUTTON_WIDTH = 76;
    private static final int BACK_BUTTON_WIDTH = 48;
    private static final int BACK_BUTTON_HEIGHT = 20;
    private static final int ACTION_BUTTON_HEIGHT = 20;
    private static final int SCREEN_MARGIN = 8;
    private static final int TITLE_TOP = 12;
    private static final int INSTRUCTION_TOP = 24;
    private static final int SUPPORTED_FILES_GAP = 2;
    private static final int INPUT_SECTION_TOP = 60;
    private static final int PREVIEW_GAP = 32;
    private static final int BUTTON_GAP = 10;

    private final Screen parent;
    private EditBox flagUrlInput;
    private Button previewUrlButton;
    private Button confirmButton;
    private Button cancelButton;
    private Component statusMessage = CommonComponents.EMPTY;
    private int statusColor = INFO_COLOR;
    private boolean loadingPreview;
    private long previewRequestId;

    public TeamFlagSelectionScreen(Screen parent) {
        super(Component.translatable(Translations.SpaceRace.FLAG_MENU_TITLE));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        int inputX = this.getInputX();
        int inputY = this.getInputY();
        int buttonRowY = this.getButtonsY();

        this.addRenderableWidget(Button.builder(Component.translatable(Translations.SpaceRace.BACK), button -> this.onClose())
                .bounds(SCREEN_MARGIN, SCREEN_MARGIN, BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT)
                .build());

        this.flagUrlInput = new EditBox(this.font, inputX, inputY, INPUT_WIDTH, 20, Component.translatable(Translations.SpaceRace.FLAG_URL_LABEL));
        this.flagUrlInput.setHint(Component.translatable(Translations.SpaceRace.FLAG_URL_HINT));
        this.flagUrlInput.setMaxLength(2048);
        this.flagUrlInput.setResponder(value -> this.updateButtonStates());
        this.addRenderableWidget(this.flagUrlInput);
        this.setInitialFocus(this.flagUrlInput);

        this.previewUrlButton = this.addRenderableWidget(Button.builder(Component.translatable(Translations.SpaceRace.FLAG_URL_PREVIEW), button -> this.previewFromUrl())
                .bounds(inputX + INPUT_WIDTH + 6, inputY, URL_BUTTON_WIDTH, 20)
                .build());

        this.confirmButton = this.addRenderableWidget(Button.builder(Component.translatable(Translations.SpaceRace.FLAG_CONFIRM), button -> this.confirmPreview())
                .bounds(this.width / 2 - ACTION_BUTTON_WIDTH - 3, buttonRowY, ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT)
                .build());

        this.cancelButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> this.cancelPreview())
                .bounds(this.width / 2 + 3, buttonRowY, ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT)
                .build());

        this.updateButtonStates();
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderTransparentBackground(graphics);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(graphics, mouseX, mouseY, delta);

        int centerX = this.width / 2;
        int inputX = this.flagUrlInput.getX();
        int inputY = this.flagUrlInput.getY();
        int instructionY = this.getInstructionY();

        graphics.drawCenteredString(this.font, this.title, centerX, TITLE_TOP, 0xFFFFFFFF);
        graphics.drawCenteredString(this.font, Component.translatable(Translations.SpaceRace.DRAG_AND_DROP_FLAG), centerX, instructionY, 0xFFFFFFFF);
        graphics.drawCenteredString(this.font, Component.translatable(Translations.SpaceRace.FLAG_SUPPORTED_FILES), centerX, instructionY + this.font.lineHeight + SUPPORTED_FILES_GAP, 0xFFB8B8B8);
        graphics.drawString(this.font, Component.translatable(Translations.SpaceRace.FLAG_URL_LABEL), inputX, inputY - this.font.lineHeight - 3, 0xFFFFFFFF, false);

        this.renderPreview(graphics);
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.flagUrlInput != null && this.flagUrlInput.isFocused() && (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
            this.previewFromUrl();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onFilesDrop(List<Path> paths) {
        if (paths.isEmpty()) {
            return;
        }

        Path path = paths.getFirst();
        this.previewRequestId++;
        this.loadingPreview = false;

        try {
            TeamFlagTextureManager.INSTANCE.applyPreviewFromFile(path);
            this.setStatus(Component.translatable(Translations.SpaceRace.FLAG_PREVIEW_READY), SUCCESS_COLOR);
        } catch (IllegalArgumentException exception) {
            this.setStatus(Component.translatable(Translations.SpaceRace.FLAG_INVALID_FILE), ERROR_COLOR);
        } catch (IOException exception) {
            Constant.LOGGER.warn("Failed to load team flag from dropped file {}", path, exception);
            this.setStatus(Component.translatable(Translations.SpaceRace.FLAG_LOAD_FAILED), ERROR_COLOR);
        }

        this.updateButtonStates();
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }

    @Override
    public void removed() {
        this.previewRequestId++;
        super.removed();
    }

    private void renderPreview(GuiGraphics graphics) {
        int previewWidth = this.getPreviewWidth();
        int previewHeight = this.getPreviewHeight();
        int previewX = this.getPreviewX();
        int previewY = this.getPreviewY();

        graphics.fill(previewX - 4, previewY - 4, previewX + previewWidth + 4, previewY + previewHeight + 4, 0xAA000000);
        graphics.renderOutline(previewX - 4, previewY - 4, previewWidth + 8, previewHeight + 8, 0xFF2D2D2D);

        ResourceLocation textureLocation = TeamFlagTextureManager.INSTANCE.getDisplayTextureLocation();
        graphics.blit(
                textureLocation,
                previewX,
                previewY,
                previewWidth,
                previewHeight,
                0,
                0,
                TeamFlagTextureManager.FLAG_WIDTH,
                TeamFlagTextureManager.FLAG_HEIGHT,
                TeamFlagTextureManager.FLAG_WIDTH,
                TeamFlagTextureManager.FLAG_HEIGHT
        );
    }

    private void previewFromUrl() {
        String url = this.flagUrlInput.getValue().trim();
        if (url.isEmpty()) {
            this.setStatus(Component.translatable(Translations.SpaceRace.FLAG_INVALID_URL), ERROR_COLOR);
            this.updateButtonStates();
            return;
        }

        this.loadingPreview = true;
        this.setStatus(Component.translatable(Translations.SpaceRace.FLAG_LOADING), INFO_COLOR);
        this.updateButtonStates();

        long requestId = ++this.previewRequestId;
        Util.backgroundExecutor().execute(() -> {
            try {
                byte[] flagData = TeamFlagTextureManager.INSTANCE.loadFlagDataFromUrl(url);
                this.finishPreviewRequest(requestId, flagData, null, null);
            } catch (IllegalArgumentException exception) {
                this.finishPreviewRequest(requestId, null, Translations.SpaceRace.FLAG_INVALID_URL, exception);
            } catch (IOException exception) {
                this.finishPreviewRequest(requestId, null, Translations.SpaceRace.FLAG_LOAD_FAILED, exception);
            }
        });
    }

    private void finishPreviewRequest(long requestId, byte[] flagData, String statusTranslationKey, Exception exception) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.execute(() -> {
            if (minecraft.screen != this || this.previewRequestId != requestId) {
                return;
            }

            this.loadingPreview = false;
            if (flagData != null) {
                TeamFlagTextureManager.INSTANCE.applyPreview(flagData);
                this.setStatus(Component.translatable(Translations.SpaceRace.FLAG_PREVIEW_READY), SUCCESS_COLOR);
            } else {
                if (exception != null) {
                    Constant.LOGGER.warn("Failed to load team flag preview from URL", exception);
                }
                this.setStatus(Component.translatable(statusTranslationKey), ERROR_COLOR);
            }

            this.updateButtonStates();
        });
    }

    private void confirmPreview() {
        TeamFlagTextureManager.INSTANCE.confirmPreview();
        byte[] payload = TeamFlagTextureManager.INSTANCE.getCurrentFlagPayload();
        if (payload != null) {
            ClientPlayNetworking.send(new FlagDataPayload(payload));
        }

        this.setStatus(Component.translatable(Translations.SpaceRace.FLAG_UPDATED), SUCCESS_COLOR);
        this.updateButtonStates();
    }

    private void cancelPreview() {
        TeamFlagTextureManager.INSTANCE.clearPreview();
        this.setStatus(Component.translatable(Translations.SpaceRace.FLAG_PREVIEW_CLEARED), INFO_COLOR);
        this.updateButtonStates();
    }

    private void updateButtonStates() {
        boolean hasPreview = TeamFlagTextureManager.INSTANCE.hasPendingPreview();
        boolean canPreviewFromUrl = !this.loadingPreview && this.flagUrlInput != null && !this.flagUrlInput.getValue().trim().isEmpty();

        if (this.flagUrlInput != null) {
            this.flagUrlInput.setEditable(!this.loadingPreview);
        }
        if (this.previewUrlButton != null) {
            this.previewUrlButton.active = canPreviewFromUrl;
        }
        if (this.confirmButton != null) {
            this.confirmButton.active = hasPreview && !this.loadingPreview;
        }
        if (this.cancelButton != null) {
            this.cancelButton.active = hasPreview && !this.loadingPreview;
        }
    }

    private void setStatus(Component message, int color) {
        this.statusMessage = message;
        this.statusColor = color;
    }

    private int getInstructionY() {
        return INSTRUCTION_TOP;
    }

    private int getContentTop() {
        return INPUT_SECTION_TOP;
    }

    private int getInputX() {
        return this.width / 2 - (INPUT_WIDTH + 6 + URL_BUTTON_WIDTH) / 2;
    }

    private int getInputY() {
        return this.getContentTop();
    }

    private int getPreviewX() {
        return this.width / 2 - this.getPreviewWidth() / 2;
    }

    private int getPreviewY() {
        return this.getInputY() + PREVIEW_GAP;
    }

    private int getButtonsY() {
        return this.height - SCREEN_MARGIN - ACTION_BUTTON_HEIGHT;
    }

    private int getPreviewWidth() {
        return this.getPreviewHeight() * TeamFlagTextureManager.FLAG_WIDTH / TeamFlagTextureManager.FLAG_HEIGHT;
    }

    private int getPreviewHeight() {
        int availableHeight = Math.max(24, this.getButtonsY() - BUTTON_GAP - this.getPreviewY());
        int availableWidth = Math.max(36, Math.min(MAX_PREVIEW_WIDTH, this.width - (SCREEN_MARGIN * 2)));
        int widthLimitedHeight = availableWidth * TeamFlagTextureManager.FLAG_HEIGHT / TeamFlagTextureManager.FLAG_WIDTH;
        return Math.min(availableHeight, widthLimitedHeight);
    }
}
