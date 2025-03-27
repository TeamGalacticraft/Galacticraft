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
            if (inBounds(progress, 1, 9)) {
                //transferring can
                draw(68, 30, 36, 189, 4, progress, matrices);
            }
            if (this.menu.getFirstRowConsumed()) {
                if (inBounds(progress, 10, 36)) {
                    if (inBounds(progress, 10, 17)) {
                        draw(97, 19, 65, 178, 9 - progress, 4, matrices);
                    }
                    if (progress > 17) {
                        draw(89, 19, 57, 178, 8, 4, matrices);
                    }
                    if (inBounds(progress, 18, 19)) {
                        draw(89, 19, 57, 178, 17 - progress, 3, matrices);
                    }
                    if (progress > 19) {
                        draw(86, 19, 54, 178, 3, 3, matrices);
                    }
                    if (inBounds(progress, 20, 36)) {
                        draw(86, 22, 54, 181, 4, progress - 19, matrices);
                    }
                }
                if (inBounds(progress, 37, 106)) {
                    draw(86, 19, 54, 178, 11, 20, matrices);
                }
            }
            if (this.menu.getSecondRowConsumed()) {
                if (inBounds(progress, 37, 44)) {
                    draw(97, 39, 65, 198, 36 - progress, 2, matrices);
                }
                if (inBounds(progress, 45, 106)) {
                    draw(89, 39, 57, 198, 8, 2, matrices);
                }
            }
            if (this.menu.getThirdRowConsumed()) {
                if (inBounds(progress, 79, 86)) {
                    draw(97, 57, 65, 216, 78 - progress, 2, matrices);
                }
                if (inBounds(progress, 87, 106)) {
                    draw(89, 57, 57, 216, 8, 2, matrices);
                }
                if (inBounds(progress, 87, 96)) {
                    draw(87, 59, 55, 218, 2, 86 - progress, matrices);
                }
                if (inBounds(progress, 97, 106)) {
                    draw(87, 59, 55, 218, 2, -10, matrices);
                }
            }
            if (this.menu.getForthRowConsumed()) {
                if (inBounds(progress, 53, 62)) {
                    draw(97, 75, 65, 234, 52 - progress, 2, matrices);
                }
                if (inBounds(progress, 63, 106)) {
                    draw(87, 75, 55, 234, 10, 2, matrices);
                }
                if (inBounds(progress, 63, 78)) {
                    draw(87, 75, 55, 234, 2, 62 - progress, matrices);
                }
                if (inBounds(progress, 79, 106)) {
                    draw(87, 75, 55, 234, 2, -16, matrices);
                }
                if (inBounds(progress, 87, 96)) {
                    draw(87, 59, 55, 218, 2, 86 - progress, matrices);
                }
                if (inBounds(progress, 97, 106)) {
                    draw(87, 59, 55, 218, 2, -10, matrices);
                }
            }
            if (inBounds(progress, 45, 52)) {
                draw(87, 39, 55, 198, 2, progress - 44, matrices);
            }
            if (this.menu.getFirstRowConsumed() || this.menu.getSecondRowConsumed()) {
                if (inBounds(progress, 52, 106)) {
                    draw(87, 39, 55, 198, 2, 8, matrices);
                }
            }
            if (inBounds(progress, 97, 106)) {
                draw(89, 47, 57, 206, 96 - progress, 2, matrices);
            }
            if (progress > 106) {
                //transferring full can
                draw(68, 57, 36, 216, 4, progress - 106, matrices);
            }
        }
    }

    @Override
    protected void renderMachineBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderMachineBackground(graphics, mouseX, mouseY, delta);
        this.drawProgressBar(graphics.pose());
    }

    private boolean inBounds(int value, int min, int max) {
        return (value >= min && value <= max);
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
