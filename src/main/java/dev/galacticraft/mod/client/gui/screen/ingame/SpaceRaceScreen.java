/*
 * Copyright (c) 2019-2024 Team Galacticraft
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
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class SpaceRaceScreen extends Screen {
    private int backgroundWidth = 0;
    private int backgroundHeight = 0;
    private Menu menu = Menu.MAIN;
    private EditBox teamNameInput;
    private boolean animationCompleted = false;
    private int teamColor = 0xFF000000;

    public SpaceRaceScreen() {
        super(Component.translatable(Translations.SpaceRace.SPACE_RACE_MANAGER));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected void mainMenu() {
        addButton(Component.translatable(Translations.SpaceRace.EXIT), this.getLeft() + 5, this.getTop() + 5, 40, 14, button -> this.onClose());
        addButton(Component.translatable(Translations.SpaceRace.ADD_PLAYERS), this.getLeft() + 10, this.getBottom() - 85, 100, 30, button -> setMenu(Menu.ADD_PLAYERS));
        addButton(Component.translatable(Translations.SpaceRace.REMOVE_PLAYERS), this.getLeft() + 10, this.getBottom() - 45, 100, 30, button -> setMenu(Menu.REMOVE_PLAYERS));
        addComingSoonButton(Component.translatable(Translations.SpaceRace.SERVER_STATS), this.getRight() - 100 - 10, this.getBottom() - 85, 100, 30);
        addComingSoonButton(Component.translatable(Translations.SpaceRace.GLOBAL_STATS), this.getRight() - 100 - 10, this.getBottom() - 45, 100, 30);

        int flagButtonWidth = 81;
        int flagButtonHeight = 58;
        int flagButtonX = this.width/2 - flagButtonWidth/2, flagButtonY = this.getTop() + 10;
        this.addRenderableWidget(new CustomizeFlagButton(flagButtonX, flagButtonY, flagButtonWidth, flagButtonHeight, () -> setMenu(Menu.TEAM_FLAG)));
        this.addRenderableWidget(new TeamColorButton(flagButtonX + flagButtonWidth + 10, flagButtonY + flagButtonHeight/2 - 45/2, 45, 45));
        this.addRenderableWidget(this.teamNameInput = new EditBox(this.font, this.getLeft() + (this.backgroundWidth / 2) - 64, flagButtonY + 65, 128, 15, this.teamNameInput, Component.empty()) {
            private String prevText;

            @Override
            public void setFocused(boolean focused) {
                if (this.isFocused() != focused) {
                    if (focused) {
                        this.prevText = this.getValue();
                    } else if (this.prevText == null || !this.prevText.equals(this.getValue())) {
                        ClientPlayNetworking.send(Constant.id("team_name"), PacketByteBufs.create().writeUtf(this.getValue()));
                    }
                }
                super.setFocused(focused);
            }
        });
    }

    protected void addPlayersMenu() {
        addBackButton();
    }

    protected void removePlayersMenu() {
        addBackButton();
    }

    protected void teamColorMenu() {
        addBackButton();

        this.addRenderableOnly((graphics, mouseX, mouseY, delta) -> {
            graphics.fill(this.width/2 - 50, this.getTop() + 10, this.width/2 + 50, this.getTop() + 10 + 100, this.teamColor);
        });

        int sliderWidth = 100;
        int sliderX = this.width/2 - sliderWidth/2;
        this.addRenderableWidget(new ColorSlider(sliderX, this.getBottom() - 80, sliderWidth, 20, Component.literal("Red"), FastColor.ARGB32.red(this.teamColor), value -> {
            this.teamColor = (this.teamColor & 0xFF00FFFF) + (value << 16);
        }));
        this.addRenderableWidget(new ColorSlider(sliderX, this.getBottom() - 55, sliderWidth, 20, Component.literal("Green"), FastColor.ARGB32.green(this.teamColor), value -> {
            this.teamColor = (this.teamColor & 0xFFFF00FF) + (value << 8);
        }));
        this.addRenderableWidget(new ColorSlider(sliderX, this.getBottom() - 30, sliderWidth, 20, Component.literal("Blue"), FastColor.ARGB32.blue(this.teamColor), value -> {
            this.teamColor = (this.teamColor & 0xFFFFFF00) + value;
        }));
    }

    protected void teamFlagMenu() {
        addBackButton();
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
        // 5% of width
        int maxWidth = (int) (this.width - (getXMargins() * 1.5D));
        if (backgroundWidth < maxWidth) {
            backgroundWidth += (int) Math.min(60*delta, maxWidth - backgroundWidth);
        }

        int maxHeight = (int) (this.height - (getYMargins() * 1.5D));
        if (backgroundHeight < maxHeight) {
            backgroundHeight += (int) Math.min(40*delta, maxHeight - backgroundHeight);
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
            super.render(graphics, x, y, delta);
            this.renderForeground(graphics, x, y);
            this.drawMouseoverTooltip(graphics, x, y);
        } else {
            this.renderBackground(graphics, x, y, delta);
        }

//        this.mouseX = (float) x;
//        this.mouseY = (float)/*y*/ minecraft.window.getScaledHeight() / 2;
//
//        DiffuseLighting.enableForItems();
//        this.itemRenderer.renderGuiItem(Items.GRASS_BLOCK.getDefaultStack(), this.x + 6, this.y - 20);
//        this.itemRenderer.renderGuiItem(GCItems.OXYGEN_FAN.getDefaultStack(), this.x + 35, this.y - 20);
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
        switch (menu) {
            case MAIN -> mainMenu();
            case ADD_PLAYERS -> addPlayersMenu();
            case REMOVE_PLAYERS -> removePlayersMenu();
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
        super.onFilesDrop(paths);
        if (this.menu == Menu.TEAM_FLAG) {
            if (paths.size() > 0) {
                File file = paths.get(0).toFile();
                NativeImage image;
                assert file.exists();
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    image = NativeImage.read(fileInputStream); //ABGR ONLY
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                if (image.getWidth() == 48 && image.getHeight() == 32) {
                    final NativeImage finalImage = image;
                    this.minecraft.setScreen(new ConfirmScreen(yes -> {
                        if (yes) {
                            int[] array = new int[48 * 32];
                            for (int y = 0; y < 32; y++) {
                                for (int x = 0; x < 48; x++) {
                                    array[y * 48 + x] = (finalImage.getPixelRGBA(x, y) /*& 0x00FFFFFF will be done on server (don't trust clients, so why do extra work?)*/); //ignore alpha channel
                                }
                            }
                            ClientPlayNetworking.send(Constant.id("flag_data"), PacketByteBufs.create().writeVarIntArray(array));
                        } else {
                            finalImage.close();
                        }
                        this.minecraft.setScreen(SpaceRaceScreen.this);
                    }, Component.translatable(Translations.SpaceRace.FLAG_CONFIRM), Component.translatable(Translations.SpaceRace.FLAG_CONFIRM_MESSAGE)));
                }
            }
        }
    }

    private enum Menu {
        MAIN,
        ADD_PLAYERS,
        REMOVE_PLAYERS,
        TEAM_COLOR,
        TEAM_FLAG
    }

    private static class SpaceRaceButton extends Button {

        public SpaceRaceButton(Component component, int x, int y, int width, int height, OnPress onPress) {
            super(x, y, width, height, component, onPress, DEFAULT_NARRATION);
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            int x = this.getX(), y = this.getY();
            int backgroundColor = this.isHoveredOrFocused() ? 0xAA1e1e1e : 0xAA000000;
            int lineColor = this.isHoveredOrFocused() ? 0xFF3c3c3c : 0xFF2d2d2d;
            graphics.fill(x, y, x + width, y + height, backgroundColor);
            graphics.renderOutline(x, y, width, height, lineColor);
            this.renderString(graphics, Minecraft.getInstance().font, 0xFFFFFFFF);
        }
    }

    private static class ComingSoonButton extends SpaceRaceButton {
        private final Component originalMessage;

        public ComingSoonButton(Component component, int x, int y, int width, int height) {
            super(component, x, y, width, height, button -> {});
            this.originalMessage = component;
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            this.setMessage(this.isHoveredOrFocused() ? Component.translatable(Translations.SpaceRace.COMING_SOON) : this.originalMessage);
            super.renderWidget(graphics, mouseX, mouseY, delta);
        }
    }

    private static class CustomizeFlagButton extends AbstractButton {
        private final Runnable onPress;

        public CustomizeFlagButton(int x, int y, int width, int height, Runnable onPress) {
            super(x, y, width, height, Component.literal("Customize Flag"));
            this.onPress = onPress;
        }

        @Override
        public void onPress() {
            this.onPress.run();
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput builder) {
            this.defaultButtonNarrationText(builder);
        }
    }

    private class TeamColorButton extends AbstractButton {
        private static final Component line1 = Component.literal("Change");
        private static final Component line2 = Component.literal("Team");
        private static final Component line3 = Component.literal("Color");

        public TeamColorButton(int x, int y, int width, int height) {
            super(x, y, width, height, Component.literal("Change Team Color"));
        }

        @Override
        public void onPress() {
            setMenu(Menu.TEAM_COLOR);
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            Font font = Minecraft.getInstance().font;
            graphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), teamColor);
            int centerX = this.getX() + this.getWidth()/2;
            int centerY = this.getY() + this.getHeight()/2 - font.lineHeight/2;
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

        public ColorSlider(int x, int y, int width, int height, Component component, int value, Consumer<Integer> consumer) {
            super(x, y, width, height, component, value/255.0);
            this.consumer = consumer;
        }

        @Override
        protected void updateMessage() {

        }

        @Override
        protected void applyValue() {
            this.consumer.accept((int) (this.value * 255.0));
        }
    }
}
