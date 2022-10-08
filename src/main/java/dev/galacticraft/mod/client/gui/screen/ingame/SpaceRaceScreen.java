/*
 * Copyright (c) 2019-2022 Team Galacticraft
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
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class SpaceRaceScreen extends Screen {
    private int backgroundWidth = 0;
    private int backgroundHeight = 0;
    private Menu menu = Menu.MAIN;
    private EditBox teamNameInput;

    public SpaceRaceScreen() {
        super(Component.translatable("ui." + Constant.MOD_ID + ".space_race_manager"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected static boolean check(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(this.teamNameInput = new EditBox(this.font, this.getLeft() + (this.backgroundWidth / 2) - 64, this.getTop() + 65, 128, 15, this.teamNameInput, Component.empty()) {
            private String prevText;

            @Override
            protected void setFocused(boolean focused) {
                if (this.isFocused() != focused) {
                    if (focused) {
                        this.prevText = this.getValue();
                    } else if (this.prevText == null || !this.prevText.equals(this.getValue())) {
                        ClientPlayNetworking.send(new ResourceLocation(Constant.ADDON_API_ID, "team_name"), PacketByteBufs.create().writeUtf(this.getValue()));
                    }
                }
                super.setFocused(focused);
            }
        });
        this.teamNameInput.setVisible(menu == Menu.MAIN);
    }

    @Override
    public void resize(Minecraft client, int width, int height) {
        this.backgroundWidth = (int) (width - ((this.getMarginPercent() * width) * 1.5D));
        this.backgroundHeight = (int) (height - ((this.getMarginPercent() * height) * 1.5D));
        super.resize(client, width, height);
    }

    private static void fillSolid(Matrix4f matrix, int x1, int y1, int x2, int y2, int color) {
        int j;
        if (x1 < x2) {
            j = x1;
            x1 = x2;
            x2 = j;
        }

        if (y1 < y2) {
            j = y1;
            y1 = y2;
            y2 = j;
        }

        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.disableTexture();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferBuilder.vertex(matrix, (float) x1, (float) y2, 0.0F).color(r, g, b, a).endVertex();
        bufferBuilder.vertex(matrix, (float) x2, (float) y2, 0.0F).color(r, g, b, a).endVertex();
        bufferBuilder.vertex(matrix, (float) x2, (float) y1, 0.0F).color(r, g, b, a).endVertex();
        bufferBuilder.vertex(matrix, (float) x1, (float) y1, 0.0F).color(r, g, b, a).endVertex();
        BufferUploader.draw(bufferBuilder.end());
    }

    @Override
    public void renderBackground(PoseStack stack) {
        // 5% of width
        int maxWidth = (int) (this.width - (getXMargins() * 1.5D));
        if (backgroundWidth < maxWidth) {
            backgroundWidth += Math.min(3, maxWidth - backgroundWidth);
        }

        int maxHeight = (int) (this.height - (getYMargins() * 1.5D));
        if (backgroundHeight < maxHeight) {
            backgroundHeight += Math.min(2, maxHeight - backgroundHeight);
        }

        fill(stack, getLeft(), getTop(), getLeft() + backgroundWidth, getTop() + backgroundHeight, 0x80000000);
    }

    private void renderForeground(PoseStack stack, int mouseX, int mouseY) {
        drawCenteredString(stack, this.font, I18n.get("ui.galacticraft.space_race_manager"), this.width / 2, getTop() - 20, 0xFFFFFF);

        if (menu == Menu.MAIN) {
            if (!check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                renderButton(stack, font, Component.translatable("ui.galacticraft.space_race_manager.exit"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            } else {
                renderHoveredButton(stack, font, Component.translatable("ui.galacticraft.space_race_manager.exit"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            }

            if (!check(mouseX, mouseY, this.getLeft() + 10, this.getBottom() - 85, 100, 30)) {
                renderButton(stack, font, Component.translatable("ui.galacticraft.space_race_manager.add_players"), this.getLeft() + 10, this.getBottom() - 85, 100, 30);
            } else {
                renderHoveredButton(stack, font, Component.translatable("ui.galacticraft.space_race_manager.add_players"), this.getLeft() + 10, this.getBottom() - 85, 100, 30);
            }

            if (!check(mouseX, mouseY, this.getLeft() + 10, this.getBottom() - 45, 100, 30)) {
                renderButton(stack, font, Component.translatable("ui.galacticraft.space_race_manager.remove_players"), this.getLeft() + 10, this.getBottom() - 45, 100, 30);
            } else {
                renderHoveredButton(stack, font, Component.translatable("ui.galacticraft.space_race_manager.remove_players"), this.getLeft() + 10, this.getBottom() - 45, 100, 30);
            }

            if (!check(mouseX, mouseY, this.getRight() - 100 - 10, this.getBottom() - 85, 100, 30)) {
                renderButton(stack, font, Component.translatable("ui.galacticraft.space_race_manager.server_stats"), this.getRight() - 100 - 10, this.getBottom() - 85, 100, 30);
            } else {
                renderHoveredButton(stack, font, Component.translatable("ui.galacticraft.space_race_manager.coming_soon"), this.getRight() - 100 - 10, this.getBottom() - 85, 100, 30);
            }

            if (!check(mouseX, mouseY, this.getRight() - 100 - 10, this.getBottom() - 45, 100, 30)) {
                renderButton(stack, font, Component.translatable("ui.galacticraft.space_race_manager.global_stats"), this.getRight() - 100 - 10, this.getBottom() - 45, 100, 30);
            } else {
                renderHoveredButton(stack, font, Component.translatable("ui.galacticraft.space_race_manager.coming_soon"), this.getRight() - 100 - 10, this.getBottom() - 45, 100, 30);
            }
        } else if (menu == Menu.ADD_PLAYERS) {
            if (!check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                renderButton(stack, font, Component.translatable("ui.galacticraft.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            } else {
                renderHoveredButton(stack, font, Component.translatable("ui.galacticraft.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            }

        } else if (menu == Menu.REMOVE_PLAYERS) {
            if (!check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                renderButton(stack, font, Component.translatable("ui.galacticraft.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            } else {
                renderHoveredButton(stack, font, Component.translatable("ui.galacticraft.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            }

        } else if (menu == Menu.TEAM_COLOR) {
            if (!check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                renderButton(stack, font, Component.translatable("ui.galacticraft.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            } else {
                renderHoveredButton(stack, font, Component.translatable("ui.galacticraft.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            }

        } else if (menu == Menu.TEAM_FLAG) {
            if (!check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                renderButton(stack, font, Component.translatable("ui.galacticraft.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            } else {
                renderHoveredButton(stack, font, Component.translatable("ui.galacticraft.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            }

        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (menu == Menu.MAIN) {
            if (check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                this.onClose();
            }

            if (check(mouseX, mouseY, this.getLeft() + 10, this.getBottom() - 85, 100, 30)) {
                setMenu(Menu.ADD_PLAYERS);
                return true;
            }

            if (check(mouseX, mouseY, this.getLeft() + 10, this.getBottom() - 45, 100, 30)) {
                setMenu(Menu.REMOVE_PLAYERS);
                return true;
            }

            if (check(mouseX, mouseY, this.getRight() - 100 - 10, this.getBottom() - 85, 100, 30)) {
                // server stats
            }

            if (check(mouseX, mouseY, this.getRight() - 100 - 10, this.getBottom() - 45, 100, 30)) {
                //global stats
            }

        } else if (menu == Menu.ADD_PLAYERS) {
            if (check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                setMenu(Menu.MAIN);
            }

        } else if (menu == Menu.REMOVE_PLAYERS) {
            if (check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                setMenu(Menu.MAIN);
            }

        } else if (menu == Menu.TEAM_COLOR) {
            if (check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                setMenu(Menu.MAIN);
            }

        } else if (menu == Menu.TEAM_FLAG) {
            if (check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                setMenu(Menu.MAIN);
            }

        }
        return false;
    }

    @Override
    public void render(PoseStack stack, int x, int y, float delta) {
        this.renderBackground(stack);

        if (this.isAnimationComplete()) {
            this.renderForeground(stack, x, y);
            this.drawMouseoverTooltip(stack, x, y);
        }

        super.render(stack, x, y, delta);

//        this.mouseX = (float) x;
//        this.mouseY = (float)/*y*/ minecraft.window.getScaledHeight() / 2;
//
//        DiffuseLighting.enableForItems();
//        this.itemRenderer.renderGuiItem(Items.GRASS_BLOCK.getDefaultStack(), this.x + 6, this.y - 20);
//        this.itemRenderer.renderGuiItem(GalacticraftItems.OXYGEN_FAN.getDefaultStack(), this.x + 35, this.y - 20);
    }

    private void drawMouseoverTooltip(PoseStack stack, int mouseX, int mouseY) {

    }

    protected void drawHorizontalLineSolid(PoseStack matrices, int x1, int x2, int y, int color) {
        if (x2 < x1) {
            int i = x1;
            x1 = x2;
            x2 = i;
        }

        fillSolid(matrices.last().pose(), x1, y, x2 + 1, y + 1, color);
    }

    protected void drawVerticalLineSolid(PoseStack matrices, int x, int y1, int y2, int color) {
        if (y2 < y1) {
            int i = y1;
            y1 = y2;
            y2 = i;
        }

        fillSolid(matrices.last().pose(), x, y1 + 1, x + 1, y2, color);
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
        this.teamNameInput.setVisible(menu == Menu.MAIN);
    }

    private boolean isAnimationComplete() {
        int maxWidth = (int) (this.width - (getXMargins() * 1.5D));
        int maxHeight = (int) (this.height - (getYMargins() * 1.5D));

        return backgroundWidth >= maxWidth && backgroundHeight >= maxHeight;
    }

    private void renderHoveredButton(PoseStack stack, Font textRenderer, Component text, int x, int y, int width, int height) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        stack.pushPose();
        fillSolid(stack.last().pose(), x, y, x + width, y + height, 0xAA1e1e1e);
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        drawHorizontalLineSolid(stack, x, x + width, y, 0xFF3c3c3c);
        drawVerticalLineSolid(stack, x + width, y, y + height, 0xFF3c3c3c);
        drawHorizontalLineSolid(stack, x + width, x, y + height, 0xFF3c3c3c);
        drawVerticalLineSolid(stack, x, y, y + height, 0xFF3c3c3c);
        stack.popPose();
        textRenderer.draw(stack, text.getVisualOrderText(), x + (width / 2F) - (textRenderer.width(text) / 2F), y + (height / 2F) - 4F, 0xffffff);
    }

    private int getYMargins() {
        return (int) (this.height * this.getMarginPercent());
    }

    private int getXMargins() {
        return (int) (this.width * this.getMarginPercent());
    }

    private void renderButton(PoseStack matrices, Font textRenderer, Component text, int x, int y, int width, int height) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        matrices.pushPose();
        fillSolid(matrices.last().pose(), x, y, x + width, y + height, 0xAA000000);
        RenderSystem.setShader(GameRenderer::getRendertypeLinesShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        drawHorizontalLineSolid(matrices, x, x + width, y, 0xFF2d2d2d);
        drawVerticalLineSolid(matrices, x + width, y, y + height, 0xFF2d2d2d);
        drawHorizontalLineSolid(matrices, x + width, x, y + height, 0xFF2d2d2d);
        drawVerticalLineSolid(matrices, x, y, y + height, 0xFF2d2d2d);
        matrices.popPose();
        textRenderer.draw(matrices, text.getVisualOrderText(), x + (width / 2F) - (textRenderer.width(text) / 2F), y + (height / 2F) - 4F, 0xffffff);
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
                            ClientPlayNetworking.send(new ResourceLocation(Constant.ADDON_API_ID, "flag_data"), PacketByteBufs.create().writeVarIntArray(array));
                        } else {
                            finalImage.close();
                        }
                        this.minecraft.setScreen(SpaceRaceScreen.this);
                    }, Component.translatable("ui.galacticraft.space_race_manager.flag.confirm"), Component.translatable("ui.galacticraft.space_race_manager.flag.confirm.message")));
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
}
