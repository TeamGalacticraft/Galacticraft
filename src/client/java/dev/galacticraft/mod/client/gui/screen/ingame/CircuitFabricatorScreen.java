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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.galacticraft.machinelib.api.menu.RecipeMachineMenu;
import dev.galacticraft.machinelib.client.api.screen.MachineScreen;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.machine.CircuitFabricatorBlockEntity;
import dev.galacticraft.mod.recipe.FabricationRecipe;
import dev.galacticraft.mod.util.DrawableUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeInput;

public class CircuitFabricatorScreen extends MachineScreen<CircuitFabricatorBlockEntity, RecipeMachineMenu<RecipeInput, FabricationRecipe, CircuitFabricatorBlockEntity>> {
    private static final float A = 23;
    private static final float B = 21;
    private static final float C = 17;
    private static final float D = 82;
    private static final float E = 16;
    private static final float F = (C + E) / C;
    private static final float[] SUMS = {
            A,
            A + B,
            A + B + C,
            A + B + C + 20,
            A + B + C + 47,
            A + B + D,
            A + B + D + E + 1
    };

    public CircuitFabricatorScreen(RecipeMachineMenu<RecipeInput, FabricationRecipe, CircuitFabricatorBlockEntity> handler, Inventory inv, Component title) {
        super(handler, title, Constant.ScreenTexture.CIRCUIT_FABRICATOR_SCREEN);
        this.imageHeight = 176;
        this.imageWidth = 176;

        this.capacitorX = 8;
        this.capacitorY = 17;
    }

    @Override
    protected void renderMachineBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderMachineBackground(graphics, mouseX, mouseY, delta);
        this.drawProgressBar(graphics.pose());
    }

    private void drawProgressBar(PoseStack matrices) {
        assert this.minecraft != null;
        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.CIRCUIT_FABRICATOR_SCREEN);
        if (this.menu.getProgress() > 0) {
            float progress = SUMS[6] * (float) this.menu.getProgress() / (float) this.menu.getMaxProgress();

            if (progress <= SUMS[1]) {
                DrawableUtil.drawProgressTexture(matrices, this.leftPos + 48, this.topPos + 23, 0, 180, Math.min(A, progress), 3);
                if (progress > SUMS[0]) {
                    DrawableUtil.drawProgressTexture(matrices, this.leftPos + 68, this.topPos + 25, 20, 189, 3, Math.min(B, progress - SUMS[0]));
                }
            } else {
                DrawableUtil.drawProgressTexture(matrices, this.leftPos + 48, this.topPos + 23, 0, 187, A, B + 2);
                DrawableUtil.drawProgressTexture(matrices, this.leftPos + 79, this.topPos + 53, 31, 180, Math.min(D, progress - SUMS[1]), 3);

                if (progress <= SUMS[2]) {
                    float concurrent = (progress - SUMS[1]) * F;
                    DrawableUtil.drawProgressTexture(matrices, this.leftPos + 79, this.topPos + 71, 31, 235, Math.min(C, concurrent), 3);
                    if (concurrent > C) {
                        float min = Math.min(E, concurrent - C);
                        DrawableUtil.drawProgressTexture(matrices, this.leftPos + 93, this.topPos + 71 - min, 45, 220, 3, min);
                    }
                } else {
                    DrawableUtil.drawProgressTexture(matrices, this.leftPos + 79, this.topPos + 53, 31, 217, C, 21);
                    if (progress > SUMS[3]) {
                        DrawableUtil.drawProgressTexture(matrices, this.leftPos + 113, this.topPos + 55, 65, 219, 3, Math.min(E, progress - SUMS[3]));
                    }
                    if (progress > SUMS[4]) {
                        DrawableUtil.drawProgressTexture(matrices, this.leftPos + 140, this.topPos + 53 - Math.min(19, progress - SUMS[4]), 92, 198, 3, Math.min(B, progress - SUMS[4]));
                    }
                    if (progress > SUMS[5]) {
                        DrawableUtil.drawProgressTexture(matrices, this.leftPos + 158, this.topPos + 55, 110, 219, 3, progress - SUMS[5]);
                    }
                }
            }
        }
    }
}
