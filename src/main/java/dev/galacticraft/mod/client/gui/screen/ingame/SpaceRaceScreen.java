/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

import com.mojang.blaze3d.systems.RenderSystem;
import dev.galacticraft.mod.Constant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Matrix4f;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class SpaceRaceScreen extends Screen {
    private int widthSize = 0;
    private int heightSize = 0;
    private Menu menu = Menu.MAIN;

    public SpaceRaceScreen() {
        super(new TranslatableText("ui." + Constant.MOD_ID + ".space_race_manager"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public static boolean check(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        this.widthSize = 0;
        this.heightSize = 0;
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

        float f = (float) (color >> 24 & 255) / 255.0F;
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.disableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, (float) x1, (float) y2, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float) x2, (float) y2, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float) x2, (float) y1, 0.0F).color(g, h, k, f).next();
        bufferBuilder.vertex(matrix, (float) x1, (float) y1, 0.0F).color(g, h, k, f).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.enableTexture();
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

        fill(stack, getLeft(), getTop(), getLeft() + widthSize, getTop() + heightSize, 0x80000000);
    }

    private void renderForeground(MatrixStack stack, int mouseX, int mouseY) {
        drawCenteredText(stack, this.textRenderer, I18n.translate("ui.galacticraft.space_race_manager"), this.width / 2, getTop() - 20, 0xFFFFFF);

        if (menu == Menu.MAIN) {
            if (!check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft.space_race_manager.exit"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft.space_race_manager.exit"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            }

            if (!check(mouseX, mouseY, this.getLeft() + 10, this.getBottom() - 85, 100, 30)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft.space_race_manager.add_players"), this.getLeft() + 10, this.getBottom() - 85, 100, 30);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft.space_race_manager.add_players"), this.getLeft() + 10, this.getBottom() - 85, 100, 30);
            }

            if (!check(mouseX, mouseY, this.getLeft() + 10, this.getBottom() - 45, 100, 30)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft.space_race_manager.remove_players"), this.getLeft() + 10, this.getBottom() - 45, 100, 30);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft.space_race_manager.remove_players"), this.getLeft() + 10, this.getBottom() - 45, 100, 30);
            }

            if (!check(mouseX, mouseY, this.getRight() - 100 - 10, this.getBottom() - 85, 100, 30)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft.space_race_manager.server_stats"), this.getRight() - 100 - 10, this.getBottom() - 85, 100, 30);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft.space_race_manager.coming_soon"), this.getRight() - 100 - 10, this.getBottom() - 85, 100, 30);
            }

            if (!check(mouseX, mouseY, this.getRight() - 100 - 10, this.getBottom() - 45, 100, 30)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft.space_race_manager.global_stats"), this.getRight() - 100 - 10, this.getBottom() - 45, 100, 30);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft.space_race_manager.coming_soon"), this.getRight() - 100 - 10, this.getBottom() - 45, 100, 30);
            }

            if (!check(mouseX, mouseY, this.getRight() - 100 - 10, this.getBottom() - 125, 100, 30)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft.space_race_manager.research"), this.getRight() - 100 - 10, this.getBottom() - 125, 100, 30);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft.space_race_manager.research"), this.getRight() - 100 - 10, this.getBottom() - 125, 100, 30);
            }

        } else if (menu == Menu.ADD_PLAYERS) {
            if (!check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            }

        } else if (menu == Menu.REMOVE_PLAYERS) {
            if (!check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            }

        } else if (menu == Menu.TEAM_COLOR) {
            if (!check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            }

        } else if (menu == Menu.TEAM_FLAG) {
            if (!check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            }

        } else if (menu == Menu.RESEARCH) {
            drawCenteredText(stack, this.textRenderer, I18n.translate("ui.galacticraft.space_race_manager.research"), this.width / 2, getTop() + 2, 0xFFFFFF);

            if (!check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                renderButton(stack, textRenderer, new TranslatableText("ui.galacticraft.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            } else {
                renderHoveredButton(stack, textRenderer, new TranslatableText("ui.galacticraft.space_race_manager.back"), this.getLeft() + 5, this.getTop() + 5, 40, 14);
            }

            fillSolid(stack.peek().getModel(), this.getLeft() + 10, this.getTop() + 25, this.getRight() - 10, this.getBottom() - 10, 0x0);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
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

            if (check(mouseX, mouseY, this.getRight() - 100 - 10, this.getBottom() - 125, 100, 30)) {
                setMenu(Menu.RESEARCH);
                return true;
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

        } else if (menu == Menu.RESEARCH) {
            if (check(mouseX, mouseY, this.getLeft() + 5, this.getTop() + 5, 40, 14)) {
                setMenu(Menu.MAIN);
            }

        }
        return false;
    }

    @Override
    public void render(MatrixStack stack, int x, int y, float delta) {
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

    private void drawMouseoverTooltip(MatrixStack stack, int mouseX, int mouseY) {

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

    private int getBottom() {
        return getTop() + heightSize;
    }

    private int getLeft() {
        return (this.width / 2) - (widthSize / 2);
    }

    private int getTop() {
        return (this.height / 2) - (heightSize / 2);
    }

    private int getRight() {
        return getLeft() + widthSize;
    }

    private float getMarginPercent() {
        return 0.17F;
    }

    private void setMenu(Menu menu) {
        this.menu = menu;
    }

    private int getPosXToFit(int x) {
        if (x >= this.getLeft() + 10) return x;
        return x + ((this.getLeft() + 10) - x);
    }

    private int getPosYToFit(int y) {
        if (y >= this.getTop() + 25) return y;
        return y + ((this.getTop() + 25) - y);
    }

    private int getTexPosXToFit(int x, int texPosX, int texWidth) {
        if (x >= this.getLeft() + 10) return texPosX;
        return Math.min(texWidth, texPosX + ((this.getLeft() + 10) - x));
    }

    private int getTexPosYToFit(int y, int texPosY, int texHeight) {
        if (y >= this.getTop() + 25) return texPosY;
        return Math.min(texHeight, texPosY + ((this.getTop() + 25) - y));
    }

    private int getWidthToFit(int x, int width) {
        if (x > this.getRight() - 10) {
            return 0;
        }

        if (x + width > this.getRight() - 10) {
            return Math.min(0, width - (x + width) - (this.getRight() - 10));
        }
        return width;
    }

    private int getHeightToFit(int y, int height) {
        if (y > this.getBottom() - 10) {
            return 0;
        }
        if (y + height > this.getBottom() - 10) {
            return Math.min(0, height - (y + height) - (this.getBottom() - 10));
        }
        return height;
    }

    private boolean isAnimationComplete() {
        int maxWidth = (int) (this.width - (getXMargins() * 1.5D));
        int maxHeight = (int) (this.height - (getYMargins() * 1.5D));

        return widthSize >= maxWidth && heightSize >= maxHeight;
    }

    private void renderHoveredButton(MatrixStack stack, TextRenderer textRenderer, Text text, int x, int y, int width, int height) {
        RenderSystem.disableBlend();
        stack.push();

        fillSolid(stack.peek().getModel(), x, y, x + width, y + height, 0x1e1e1e);

        drawHorizontalLineSolid(stack, x, x + width, y, 0x3c3c3c);
        drawVerticalLineSolid(stack, x + width, y, y + height, 0x3c3c3c);
        drawHorizontalLineSolid(stack, x + width, x, y + height, 0x3c3c3c);
        drawVerticalLineSolid(stack, x, y, y + height, 0x3c3c3c);

        stack.pop();
        RenderSystem.enableBlend();

        textRenderer.draw(stack, text.asOrderedText(), x + (width / 2F) - (textRenderer.getWidth(text) / 2F), y + (height / 2F) - 4F, 0xffffff);
    }

    private int getYMargins() {
        return (int) (this.height * this.getMarginPercent());
    }

    private int getXMargins() {
        return (int) (this.width * this.getMarginPercent());
    }

    private void renderButton(MatrixStack matrices, TextRenderer textRenderer, Text text, int x, int y, int width, int height) {
        RenderSystem.disableBlend();
        matrices.push();

        fillSolid(matrices.peek().getModel(), x, y, x + width, y + height, 0x0);

        drawHorizontalLineSolid(matrices, x, x + width, y, 0x2d2d2d);
        drawVerticalLineSolid(matrices, x + width, y, y + height, 0x2d2d2d);
        drawHorizontalLineSolid(matrices, x + width, x, y + height, 0x2d2d2d);
        drawVerticalLineSolid(matrices, x, y, y + height, 0x2d2d2d);

        matrices.pop();
        RenderSystem.enableBlend();

        textRenderer.draw(matrices, text.asOrderedText(), x + (width / 2F) - (textRenderer.getWidth(text) / 2F), y + (height / 2F) - 4F, 0xffffff);
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