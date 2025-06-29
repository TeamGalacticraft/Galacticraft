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

import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.machine.FoodCannerBlockEntity;
import dev.galacticraft.mod.screen.FoodCannerMenu;
import dev.galacticraft.mod.util.DrawableUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import static dev.galacticraft.mod.content.block.entity.machine.FoodCannerBlockEntity.*;

@Environment(EnvType.CLIENT)
public class FoodCannerScreen extends MachineScreen<FoodCannerBlockEntity, FoodCannerMenu> {
    public FoodCannerScreen(FoodCannerMenu handler, Inventory inv, Component title) {
        super(handler, title, Constant.ScreenTexture.FOOD_CANNER_SCREEN);
        this.imageHeight = 171;
        this.capacitorX = 8;
        this.capacitorY = 13;
        this.titleLabelY = 4;
    }

    private void drawProgressBar(PoseStack matrices) {
        if (this.menu.state.isActive()) {
            int progress = this.menu.getProgress();
            if (progress > 0 && progress <= TRANSFER_INPUT) {
                // Transferring empty can from top slot to middle slot
                this.draw(68, 30, 180, 30, 3, progress, matrices);
                return;
            } else if (progress >= TRANSFER_OUTPUT) {
                // Transferring full can from middle slot to bottom slot
                this.draw(68, 57, 180, 57, 3, progress - (TRANSFER_OUTPUT - 1), matrices);
                return;
            }

            if (this.menu.getFirstRowConsumed()) {
                if (progress >= START_ROW_1 && progress < START_ROW_2 - 1) {
                    this.draw(97, 19, 209, 73, Math.max(TRANSFER_INPUT - progress, -11), 3, matrices);
                    if (progress > START_ROW_1 + 10) {
                        this.draw(86, 21, 224, 21, 3, progress - START_ROW_1 - 10, matrices);
                    }
                } else if (progress >= START_ROW_2 - 1) {
                    this.draw(86, 19, 224, 19, 11, 19, matrices);
                }
            }

            if (this.menu.getSecondRowConsumed() && progress >= START_ROW_2) {
                this.draw(97, 37, 209, 73, Math.max((START_ROW_2 - 1) - progress, -11), 3, matrices);
                if (progress > START_ROW_2 + 10) {
                    this.draw(86, 39, 224, 39, 3, Math.min(progress - START_ROW_2 - 10, 10), matrices);
                }
            } else if (this.menu.getFirstRowConsumed() && progress >= SKIP_ROW_2) {
                this.draw(86, 38, 224, 38, 3, Math.min(progress - (SKIP_ROW_2 - 1), 11), matrices);
            }

            if (this.menu.getThirdRowConsumed() && progress >= START_ROW_3) {
                this.draw(97, 55, 209, 73, Math.max((START_ROW_3 - 1) - progress, -11), 3, matrices);
                if (progress > START_ROW_3 + 10) {
                    this.draw(86, 56, 198, 56, 3, Math.max(START_ROW_3 + 10 - progress, -10), matrices);
                }
            } else if (this.menu.getFourthRowConsumed() && progress >= SKIP_ROW_3) {
                this.draw(86, 57, 240, 57, 3, Math.max((SKIP_ROW_3 - 1) - progress, -11), matrices);
            }

            if (this.menu.getFourthRowConsumed()) {
                if (progress >= START_ROW_4 && progress < START_ROW_3 - 1) {
                    this.draw(97, 73, 209, 73, Math.max((START_ROW_4 - 1) - progress, -11), 3, matrices);
                    if (progress > START_ROW_4 + 10) {
                        this.draw(86, 74, 240, 74, 3, START_ROW_4 + 10 - progress, matrices);
                    }
                } else if (progress >= START_ROW_3 - 1) {
                    this.draw(86, 57, 240, 57, 11, 19, matrices);
                }
            }

            if (progress >= FINAL_PROGRESS) {
                this.draw(86, 46, 198, 46, Math.max((FINAL_PROGRESS - 1) - progress, -7), 3, matrices);
            }
        }
    }

    @Override
    protected void renderMachineBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderMachineBackground(graphics, mouseX, mouseY, delta);
        this.drawProgressBar(graphics.pose());
    }

    private void draw(int x, int y, int drawX, int drawY, int width, int height, PoseStack matrices) {
        if (width < 0) {
            if (height < 0) {
                DrawableUtil.drawProgressTexture(matrices, this.leftPos + x + width, this.topPos + y + height, drawX + width, drawY + height, -width, -height);
            } else {
                DrawableUtil.drawProgressTexture(matrices, this.leftPos + x + width, this.topPos + y, drawX + width, drawY, -width, height);
            }
        } else if (height < 0) {
            DrawableUtil.drawProgressTexture(matrices, this.leftPos + x, this.topPos + y + height, drawX, drawY + height, width, -height);
        } else {
            DrawableUtil.drawProgressTexture(matrices, this.leftPos + x, this.topPos + y, drawX, drawY, width, height);
        }
    }
}
