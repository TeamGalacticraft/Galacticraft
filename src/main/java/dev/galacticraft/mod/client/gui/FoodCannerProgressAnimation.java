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

package dev.galacticraft.mod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.mod.util.DrawableUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;

import static dev.galacticraft.mod.Constant.FoodCanner.*;

@Environment(EnvType.CLIENT)
public class FoodCannerProgressAnimation {
    public static float renderForRecipeViewer(GuiGraphics graphics, int x, int y, float progress, boolean[] showRow) {
        progress = FoodCannerProgressAnimation.calculateProgress(progress, showRow);
        FoodCannerProgressAnimation.render(graphics, x, y, (int) progress, showRow);
        return progress;
    }

    public static float calculateProgress(float progress, boolean[] showRow) {
        if (progress >= MAX_PROGRESS) {
            return 0.0F;
        }

        int prog = (int) progress;
        if (prog == START_ROW_2 && !showRow[1]) {
            prog = SKIP_ROW_2;
        } else if (prog == START_ROW_4 && !showRow[3]) {
            prog = showRow[2] ? START_ROW_3 : FINAL_PROGRESS;
        } else if (prog == START_ROW_3 && !showRow[2]) {
            prog = FINAL_PROGRESS;
        }

        return prog + progress % 1.0F;
    }

    public static void render(GuiGraphics graphics, int x, int y, int progress, boolean[] showRow) {
        PoseStack matrices = graphics.pose();
        RenderSystem.setShaderTexture(0, SCREEN_TEXTURE);

        if (progress <= TRANSFER_INPUT) {
            // Transferring empty can from top slot to middle slot
            draw(x, y + 11, 180, 30, 3, progress, matrices);
            return;
        } else if (progress >= TRANSFER_OUTPUT) {
            // Transferring full can from middle slot to bottom slot
            draw(x, y + 38, 180, 57, 3, progress - (TRANSFER_OUTPUT - 1), matrices);
            return;
        }

        if (showRow[0]) {
            if (progress >= START_ROW_1 && progress < START_ROW_2 - 1) {
                draw(x + 29, y, 209, 73, Math.max(TRANSFER_INPUT - progress, -11), 3, matrices);
                if (progress > START_ROW_1 + 10) {
                    draw(x + 18, y + 2, 224, 21, 3, progress - START_ROW_1 - 10, matrices);
                }
            } else if (progress >= START_ROW_2 - 1) {
                draw(x + 18, y, 224, 19, 11, 19, matrices);
            }
        }

        if (showRow[1] && progress >= START_ROW_2) {
            draw(x + 29, y + 18, 209, 73, Math.max((START_ROW_2 - 1) - progress, -11), 3, matrices);
            if (progress > START_ROW_2 + 10) {
                draw(x + 18, y + 20, 224, 39, 3, Math.min(progress - START_ROW_2 - 10, 10), matrices);
            }
        } else if (showRow[0] && progress >= SKIP_ROW_2) {
            draw(x + 18, y + 19, 224, 38, 3, Math.min(progress - (SKIP_ROW_2 - 1), 11), matrices);
        }

        if (showRow[2] && progress >= START_ROW_3) {
            draw(x + 29, y + 36, 209, 73, Math.max((START_ROW_3 - 1) - progress, -11), 3, matrices);
            if (progress > START_ROW_3 + 10) {
                draw(x + 18, y + 37, 198, 56, 3, Math.max(START_ROW_3 + 10 - progress, -10), matrices);
            }
        } else if (showRow[3] && progress >= SKIP_ROW_3) {
            draw(x + 18, y + 38, 240, 57, 3, Math.max((SKIP_ROW_3 - 1) - progress, -11), matrices);
        }

        if (showRow[3]) {
            if (progress >= START_ROW_4 && progress < START_ROW_3 - 1) {
                draw(x + 29, y + 54, 209, 73, Math.max((START_ROW_4 - 1) - progress, -11), 3, matrices);
                if (progress > START_ROW_4 + 10) {
                    draw(x + 18, y + 55, 240, 74, 3, START_ROW_4 + 10 - progress, matrices);
                }
            } else if (progress >= START_ROW_3 - 1) {
                draw(x + 18, y + 38, 240, 57, 11, 19, matrices);
            }
        }

        if (progress >= FINAL_PROGRESS) {
            draw(x + 18, y + 27, 198, 46, Math.max((FINAL_PROGRESS - 1) - progress, -7), 3, matrices);
        }
    }

    private static void draw(int x, int y, int drawX, int drawY, int width, int height, PoseStack matrices) {
        if (width < 0) {
            if (height < 0) {
                DrawableUtil.drawProgressTexture(matrices, x + width, y + height, drawX + width, drawY + height, -width, -height);
            } else {
                DrawableUtil.drawProgressTexture(matrices, x + width, y, drawX + width, drawY, -width, height);
            }
        } else if (height < 0) {
            DrawableUtil.drawProgressTexture(matrices, x, y + height, drawX, drawY + height, width, -height);
        } else {
            DrawableUtil.drawProgressTexture(matrices, x, y, drawX, drawY, width, height);
        }
    }
}
