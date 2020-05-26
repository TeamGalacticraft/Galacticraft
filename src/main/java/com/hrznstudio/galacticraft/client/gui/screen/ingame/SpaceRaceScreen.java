/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.client.gui.screen.ingame;

import com.hrznstudio.galacticraft.util.DrawableUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Matrix4f;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class SpaceRaceScreen extends Screen {
    private int widthSize = 0;
    private int heightSize = 0;
    private Menu menu = Menu.MAIN;

    public SpaceRaceScreen() {
        super(new TranslatableText("ui.galacticraft-rewoven.space_race_manager"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static boolean check(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    @Override
    public void resize(MinecraftClient minecraftClient_1, int int_1, int int_2) {
        this.widthSize = 0;
        this.heightSize = 0;
        super.resize(minecraftClient_1, int_1, int_2);
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

        float f = (float) (color >> 24 & 255) / 255.0F;
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.disableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, (float) x1, (float) y2, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float) x2, (float) y2, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float) x2, (float) y1, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y1, 0.0F).color(g, h, k, f).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
    }

    private boolean isAnimationComplete() {
        int maxWidth = (int) (this.width - (getXMargins() * 1.5D));
        int maxHeight = (int) (this.height - (getYMargins() * 1.5D));

        return widthSize >= maxWidth
                && heightSize >= maxHeight;
    }

    @Override
    public void renderBackground(MatrixStack stack) {
        // 5% of width
        int maxWidth = (int) (this.width - (getXMargins() * 1.5D));
        if (widthSize < maxWidth) {
            widthSize += Math.min(3, maxWidth - widthSize);
        }

        int maxHeight = (int) (this.height - (getYMargins() * 1.5D));
        if (heightSize < maxHeight) {
            heightSize += Math.min(2, maxHeight - heightSize);
        }

        int midX = this.width / 2;
        int midY = this.height / 2;

        int x = midX - widthSize / 2;
        int y = midY - heightSize / 2;

        fill(stack, x, y, x + widthSize, y + heightSize, 0x80000000);
    }

    private void renderForeground(MatrixStack stack, int mouseX, int mouseY) {
        TextRenderer font = this.client.textRenderer;
        DrawableUtils.drawCenteredString(stack, font, new TranslatableText("ui.galacticraft-rewoven.space_race_manager"), this.width / 2, getY() - 20, 0xFFFFFF);

        if (menu == Menu.MAIN) {
            if (!check(mouseX, mouseY, this.getX() + 10, this.getBottom() - 85, 100, 30)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.add_players"), this.getX() + 10, this.getBottom() - 85);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.add_players"), this.getX() + 10, this.getBottom() - 85);
            }

            if (!check(mouseX, mouseY, this.getX() + 10, this.getBottom() - 45, 100, 30)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.remove_players"), this.getX() + 10, this.getBottom() - 45);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.remove_players"), this.getX() + 10, this.getBottom() - 45);
            }

            if (!check(mouseX, mouseY, this.getX() + 180, this.getBottom() - 85, 100, 30)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.server_stats"), this.getX() + 180, this.getBottom() - 85);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.coming_soon"), this.getX() + 180, this.getBottom() - 85);
            }
            if (!check(mouseX, mouseY, this.getX() + 180, this.getBottom() - 45, 100, 30)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.global_stats"), this.getX() + 180, this.getBottom() - 45);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft-rewoven.space_race_manager.coming_soon"), this.getX() + 180, this.getBottom() - 45);
            }
        } else if (menu == Menu.ADD_PLAYERS) {

        } else if (menu == Menu.REMOVE_PLAYERS) {

        } else if (menu == Menu.TEAM_COLOR) {

        } else if (menu == Menu.TEAM_FLAG) {

        } else if (menu == Menu.RESEARCH) {

        }
    }

    private void renderHoveredButton(MatrixStack stack, TextRenderer textRenderer, Text text, int x, int y) {
        RenderSystem.disableBlend();
        stack.push();
        fillSolid(stack.peek().getModel(), x, y, x + 100, y + 30, 0x1e1e1e);
        drawHorizontalLineSolid(stack, x, x + 100, y, 0x3c3c3c);
        drawVerticalLineSolid(stack, x + 100, y, y + 30, 0x3c3c3c);
        drawHorizontalLineSolid(stack, x + 100, x, y + 30, 0x3c3c3c);
        drawVerticalLineSolid(stack, x, y, y + 30, 0x3c3c3c);
        stack.pop();
        RenderSystem.enableBlend();
        textRenderer.draw(stack, text, x + (100 / 2F) - (textRenderer.getWidth(text) / 2F), y + (30 / 2F) - 4.5F, 0xffffff);
    }

    private int getYMargins() {
        return (int) (this.height * this.getMarginPercent());
    }

    private int getXMargins() {
        return (int) (this.width * this.getMarginPercent());
    }

    private void renderButton(MatrixStack stack, TextRenderer textRenderer, Text text, int x, int y) {
        RenderSystem.disableBlend();
        stack.push();
        fillSolid(stack.peek().getModel(), x, y, x + 100, y + 30, 0x0);
        drawHorizontalLineSolid(stack, x, x + 100, y, 0x2d2d2d);
        drawVerticalLineSolid(stack, x + 100, y, y + 30, 0x2d2d2d);
        drawHorizontalLineSolid(stack, x + 100, x, y + 30, 0x2d2d2d);
        drawVerticalLineSolid(stack, x, y, y + 30, 0x2d2d2d);
        stack.pop();
        RenderSystem.enableBlend();
        textRenderer.draw(stack, text, x + (100 / 2F) - (textRenderer.getWidth(text) / 2F), y + (30 / 2F) - 4.5F, 0xffffff);
    }

    private float getMarginPercent() {
        return 0.17F;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (menu == Menu.MAIN) {
            if (check(mouseX, mouseY, this.getX() + 10, this.getBottom() - 85, 100, 30)) {
                menu = Menu.ADD_PLAYERS;
                return true;
            }

            if (check(mouseX, mouseY, this.getX() + 10, this.getBottom() - 45, 100, 30)) {
                menu = Menu.REMOVE_PLAYERS;
                return true;
            }

            if (check(mouseX, mouseY, this.getX() + 180, this.getBottom() - 85, 100, 30)) {
                // server stats
            }

            if (check(mouseX, mouseY, this.getX() + 180, this.getBottom() - 45, 100, 30)) {
                //global stats
            }
        } else if (menu == Menu.ADD_PLAYERS) {

        } else if (menu == Menu.REMOVE_PLAYERS) {

        } else if (menu == Menu.TEAM_COLOR) {

        } else if (menu == Menu.TEAM_FLAG) {

        } else if (menu == Menu.RESEARCH) {

        }
        return false;
    }

    private int getBottom() {
        return this.height - getYMargins();
    }

    private int getY() {
        return getYMargins();
    }

    private int getRight() {
        return this.widthSize - getXMargins();
    }

    private int getX() {
        return getXMargins();
    }

    @Override
    public void render(MatrixStack stack, int x, int y, float lastFrameDuration) {
        this.renderBackground(stack);

        if (this.isAnimationComplete()) {
            this.renderForeground(stack, x, y);
        }

        super.render(stack, x, y, lastFrameDuration);
//        this.drawMouseoverTooltip(x, y);

//        this.mouseX = (float) x;
//        this.mouseY = (float)/*y*/ minecraft.window.getScaledHeight() / 2;
//
//        DiffuseLighting.enableForItems();
//        this.itemRenderer.renderGuiItem(Items.GRASS_BLOCK.getStackForRender(), this.x + 6, this.y - 20);
//        this.itemRenderer.renderGuiItem(GalacticraftItems.OXYGEN_FAN.getStackForRender(), this.x + 35, this.y - 20);
    }

    protected void drawHorizontalLineSolid(MatrixStack matrices, int x1, int x2, int y, int color) {
        if (x2 < x1) {
            int i = x1;
            x1 = x2;
            x2 = i;
        }

        fillSolid(matrices.peek().getModel(), x1, y, x2 + 1, y + 1, color);
    }

    protected void drawVerticalLineSolid(MatrixStack matrices, int x, int y1, int y2, int color) {
        if (y2 < y1) {
            int i = y1;
            y1 = y2;
            y2 = i;
        }

        fillSolid(matrices.peek().getModel(), x, y1 + 1, x + 1, y2, color);
    }

    private enum Menu {
        MAIN,
        ADD_PLAYERS,
        REMOVE_PLAYERS,
        TEAM_COLOR,
        TEAM_FLAG,
        RESEARCH
    }
}