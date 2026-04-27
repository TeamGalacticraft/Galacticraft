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
import dev.galacticraft.impl.network.c2s.FlagDataPayload;
import dev.galacticraft.impl.network.c2s.TeamNamePayload;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.gui.widget.ColorSlider;
import dev.galacticraft.mod.client.gui.widget.CustomizeFlagButton;
import dev.galacticraft.mod.client.gui.widget.TeamColorButton;
import dev.galacticraft.mod.util.Translations;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class SpaceRaceScreen extends Screen {
    private long openingStartTime = -1;
    private int backgroundWidth = 0;
    private int backgroundHeight = 0;
    private Menu menu = Menu.MAIN;
    private EditBox teamNameInput;
    private int teamColor = 0xFF000000;
    private final ResourceLocation teamFlag = Constant.id("team_name/id_here");

    public SpaceRaceScreen() {
        super(Component.translatable(Translations.SpaceRace.SPACE_RACE_MANAGER));
    }

    @Override
    protected void init() {
        super.init();
        this.createMenu(this.menu);
    }

    private void mainMenu() {
        this.addButton(Component.translatable(Translations.SpaceRace.EXIT), this.getLeft() + 5, this.getTop() + 5, 40, 14, button -> this.onClose());
        this.addButton(Component.translatable(Translations.SpaceRace.ADD_PLAYERS), this.getLeft() + 10, this.getBottom() - 85, 100, 30, button -> this.setMenu(Menu.ADD_PLAYERS));
        this.addButton(Component.translatable(Translations.SpaceRace.REMOVE_PLAYERS), this.getLeft() + 10, this.getBottom() - 45, 100, 30, button -> this.setMenu(Menu.REMOVE_PLAYERS));
        this.addButton(Component.translatable(Translations.SpaceRace.SERVER_STATS), this.getRight() - 100 - 10, this.getBottom() - 85, 100, 30, button -> this.minecraft.setScreen(new RaceAdvancementsScreen(this)));
        this.addButton(Component.translatable(Translations.SpaceRace.GLOBAL_STATS), this.getRight() - 100 - 10, this.getBottom() - 45, 100, 30, button -> this.minecraft.setScreen(new ServerStatisticsScreen(this)));

        int flagButtonWidth = 96;
        int flagButtonHeight = 64;
        int flagButtonX = this.width / 2 - flagButtonWidth / 2;
        int flagButtonY = this.getTop() + 10;
        this.addRenderableWidget(new CustomizeFlagButton(flagButtonX, flagButtonY, flagButtonWidth, flagButtonHeight, this.teamFlag, () -> this.setMenu(Menu.TEAM_FLAG)));
        this.addRenderableWidget(new TeamColorButton(flagButtonX + flagButtonWidth + 10, flagButtonY + flagButtonHeight / 2 - 45 / 2, 45, 45, () -> this.teamColor, () -> this.setMenu(Menu.TEAM_COLOR)));
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

    private void addPlayersMenu() {
        this.addBackButton();
    }

    private void removePlayersMenu() {
        this.addBackButton();
    }

    private void teamColorMenu() {
        this.addBackButton();
        this.addRenderableOnly((graphics, mouseX, mouseY, delta) -> graphics.fill(this.width / 2 - 50, this.getTop() + 10, this.width / 2 + 50, this.getTop() + 110, this.teamColor));

        int sliderWidth = 200;
        int sliderX = this.width / 2 - sliderWidth / 2;
        this.addRenderableWidget(new ColorSlider(sliderX, this.getBottom() - 80, sliderWidth, 20, Component.translatable(Translations.SpaceRace.RED), FastColor.ARGB32.red(this.teamColor), value -> this.teamColor = (this.teamColor & 0xFF00FFFF) + (value << 16)));
        this.addRenderableWidget(new ColorSlider(sliderX, this.getBottom() - 55, sliderWidth, 20, Component.translatable(Translations.SpaceRace.GREEN), FastColor.ARGB32.green(this.teamColor), value -> this.teamColor = (this.teamColor & 0xFFFF00FF) + (value << 8)));
        this.addRenderableWidget(new ColorSlider(sliderX, this.getBottom() - 30, sliderWidth, 20, Component.translatable(Translations.SpaceRace.BLUE), FastColor.ARGB32.blue(this.teamColor), value -> this.teamColor = (this.teamColor & 0xFFFFFF00) + value));
    }

    @Override
    protected void init() {
        super.init();
        if (this.openingStartTime == -1) {
            this.openingStartTime = System.currentTimeMillis();
        }
        createMenu(this.menu);
    }

    @Override
    public void onClose() {
        this.openingStartTime = -1;
        super.onClose();
    }

    @Override
    public void resize(Minecraft client, int width, int height) {
        this.backgroundWidth = (int) (width - ((this.getMarginPercent() * width) * 1.5D));
        this.backgroundHeight = (int) (height - ((this.getMarginPercent() * height) * 1.5D));
        super.resize(client, width, height);
    }

    private void addButton(Component text, int x, int y, int width, int height, Button.OnPress onPress) {
        this.addRenderableWidget(new SpaceRaceButton(text, x, y, width, height, onPress));
    }

    private void addComingSoonButton(Component text, int x, int y, int width, int height) {
        this.addRenderableWidget(new ComingSoonButton(text, x, y, width, height));
    }

    private void addBackButton() {
        addButton(Component.translatable(Translations.SpaceRace.BACK), this.getLeft() + 5, this.getTop() + 5, 40, 14, button -> setMenu(Menu.MAIN));
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (!this.animationCompleted) {
            int maxWidth = (int) (this.width - (getXMargins() * 1.5D));
            int maxHeight = (int) (this.height - (getYMargins() * 1.5D));

            if (this.backgroundWidth >= maxWidth && this.backgroundHeight >= maxHeight) {
                this.repositionElements();
                this.animationCompleted = true;
            }

            long elapsed = System.currentTimeMillis() - this.openingStartTime;
            long duration = 1000; // This is how long (ms) it will take to open the GUI
            float progress = Math.min(1.0f, (float) elapsed / duration);

            float smoothedProgress = 1.01f - (float) Math.pow(1.0f - progress, 3);

            this.backgroundWidth = (int) (maxWidth * smoothedProgress);
            this.backgroundHeight = (int) (maxHeight * smoothedProgress);
        }

        graphics.fill(getLeft(), getTop(), getRight(), getBottom(), 0x80000000);
    }

    private void renderForeground(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawCenteredString(this.font, Component.translatable(Translations.SpaceRace.SPACE_RACE_MANAGER), this.width / 2, getTop() - 20, 0xFFFFFF);
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, float delta) {
        if (this.animationCompleted) {
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
        this.createMenu(menu);
    }

    private void createMenu(Menu menu) {
        this.clearWidgets();
        this.clearFocus();
        switch (menu) {
            case MAIN -> this.mainMenu();
            case ADD_PLAYERS -> this.addPlayersMenu();
            case REMOVE_PLAYERS -> this.removePlayersMenu();
            case TEAM_COLOR -> this.teamColorMenu();
            case TEAM_FLAG -> this.teamFlagMenu();
        }
    }

    private int getYMargins() {
        return (int) (this.height * this.getMarginPercent());
    }

    private int getXMargins() {
        return (int) (this.width * this.getMarginPercent());
    }

    @Override
    public void onFilesDrop(List<Path> paths) {
        if (this.menu != Menu.TEAM_FLAG || paths.isEmpty()) {
            return;
        }

        File file = paths.get(0).toFile();
        NativeImage image;
        assert file.exists();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            image = NativeImage.read(fileInputStream);
        } catch (IOException exception) {
            exception.printStackTrace();
            return;
        }

        if (image.getWidth() != 48 || image.getHeight() != 32) {
            return;
        }

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
            this.minecraft.setScreen(this);
        }, location, Component.translatable(Translations.SpaceRace.FLAG_CONFIRM), Component.translatable(Translations.SpaceRace.FLAG_CONFIRM_MESSAGE)));
    }

    private enum Menu {
        MAIN,
        ADD_PLAYERS,
        REMOVE_PLAYERS,
        TEAM_COLOR,
        TEAM_FLAG
    }
}
