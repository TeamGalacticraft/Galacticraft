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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeInput;

@Environment(EnvType.CLIENT)
public class CircuitFabricatorScreen extends MachineScreen<CircuitFabricatorBlockEntity, RecipeMachineMenu<RecipeInput, FabricationRecipe, CircuitFabricatorBlockEntity>> {
    private static final int PROGRESS_SIZE = 3;
    private static final int INITIAL_PROGRESS_U = 0;
    private static final int INITIAL_PROGRESS_V = 187;
    private static final int INITIAL_PROGRESS_X = 48;
    private static final int INITIAL_PROGRESS_Y = 23;
    private static final int SECONDARY_PROGRESS_U = 31;
    private static final int SECONDARY_PROGRESS_V = 217;
    private static final int SECONDARY_PROGRESS_X = 79;
    private static final int SECONDARY_PROGRESS_Y = 53;
    private static final int SECONDARY_CONCURRENT_PROGRESS_U = 31;
    private static final int SECONDARY_CONCURRENT_PROGRESS_V = 235;
    private static final int SECONDARY_CONCURRENT_PROGRESS_X = 79;
    private static final int SECONDARY_CONCURRENT_PROGRESS_Y = 71;
    private static final int SECONDARY_CONCURRENT_PROGRESS_2_U = 45;
    private static final int SECONDARY_CONCURRENT_PROGRESS_2_V = 220;
    private static final int SECONDARY_CONCURRENT_PROGRESS_2_X = 93;
    private static final int SECONDARY_CONCURRENT_PROGRESS_2_Y = 71;
    private static final int TERTIARY_PROGRESS_U = 48;
    private static final int TERTIARY_PROGRESS_V = 217;
    private static final int TERTIARY_PROGRESS_X = 96;
    private static final int TERTIARY_PROGRESS_Y = 53;
    private static final int QUATERNARY_PROGRESS_U = 65;
    private static final int QUATERNARY_PROGRESS_V = 220;
    private static final int QUATERNARY_PROGRESS_X = 113;
    private static final int QUATERNARY_PROGRESS_Y = 56;
    private static final int QUINARY_PROGRESS_U = 92;
    private static final int QUINARY_PROGRESS_V = 198;
    private static final int QUINARY_PROGRESS_X = 140;
    private static final int QUINARY_PROGRESS_Y = 53;
    private static final int QUINARY_PROGRESS_HEIGHT = 19;
    private static final int SENARY_PROGRESS_U = 110;
    private static final int SENARY_PROGRESS_V = 220;
    private static final int SENARY_PROGRESS_X = 158;
    private static final int SENARY_PROGRESS_Y = 56;

    private static final float A = 24;
    private static final float B = 20;
    private static final float C = 18;
    private static final float D_1 = 17 + PROGRESS_SIZE;
    private static final float D_2 = 44 + PROGRESS_SIZE;
    private static final float D = 65;
    private static final float E = 15;
    private static final float[] SUMS = {
            A,
            A + B,
            A + B + C,
            A + B + C + D_1,
            A + B + C + D_2,
            A + B + C + D,
            A + B + C + D + E
    };
    private static final float F = (C + E) / C;

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
            if (progress <= SUMS[0]) {
                DrawableUtil.drawProgressTexture(matrices, this.leftPos + INITIAL_PROGRESS_X, this.topPos + INITIAL_PROGRESS_Y, INITIAL_PROGRESS_U, INITIAL_PROGRESS_V, progress, PROGRESS_SIZE);
            } else {
                DrawableUtil.drawProgressTexture(matrices, this.leftPos + INITIAL_PROGRESS_X, this.topPos + INITIAL_PROGRESS_Y, INITIAL_PROGRESS_U, INITIAL_PROGRESS_V, A, Math.min(B, progress - SUMS[0]) + 4);
                if (progress > SUMS[1]) {
                    float concurrent = (progress - SUMS[1]) * F;
                    DrawableUtil.drawProgressTexture(matrices, this.leftPos + SECONDARY_PROGRESS_X, this.topPos + SECONDARY_PROGRESS_Y, SECONDARY_PROGRESS_U, SECONDARY_PROGRESS_V, Math.min(C, progress - SUMS[1]), PROGRESS_SIZE);
                    DrawableUtil.drawProgressTexture(matrices, this.leftPos + SECONDARY_CONCURRENT_PROGRESS_X, this.topPos + SECONDARY_CONCURRENT_PROGRESS_Y, SECONDARY_CONCURRENT_PROGRESS_U, SECONDARY_CONCURRENT_PROGRESS_V, Math.min(C, concurrent), PROGRESS_SIZE);

                    if (concurrent > C) {
                        float min = Math.min(E, concurrent - C);
                        DrawableUtil.drawProgressTexture(matrices, this.leftPos + SECONDARY_CONCURRENT_PROGRESS_2_X, this.topPos + SECONDARY_CONCURRENT_PROGRESS_2_Y - min, SECONDARY_CONCURRENT_PROGRESS_2_U, SECONDARY_CONCURRENT_PROGRESS_2_V, PROGRESS_SIZE, min);
                    }

                    if (progress > SUMS[2]) {
                        DrawableUtil.drawProgressTexture(matrices, this.leftPos + TERTIARY_PROGRESS_X, this.topPos + TERTIARY_PROGRESS_Y, TERTIARY_PROGRESS_U, TERTIARY_PROGRESS_V, progress - SUMS[2], PROGRESS_SIZE);
                        if (progress > SUMS[3]) {
                            DrawableUtil.drawProgressTexture(matrices, this.leftPos + QUATERNARY_PROGRESS_X, this.topPos + QUATERNARY_PROGRESS_Y, QUATERNARY_PROGRESS_U, QUATERNARY_PROGRESS_V, PROGRESS_SIZE, Math.min(E, progress - SUMS[3]));
                        }
                        if (progress > SUMS[4]) {
                            DrawableUtil.drawProgressTexture(matrices, this.leftPos + QUINARY_PROGRESS_X, this.topPos + QUINARY_PROGRESS_Y - Math.min(QUINARY_PROGRESS_HEIGHT, progress - SUMS[4]), QUINARY_PROGRESS_U, QUINARY_PROGRESS_V, PROGRESS_SIZE, Math.min(B, progress - SUMS[4]));
                        }
                        if (progress > SUMS[5]) {
                            DrawableUtil.drawProgressTexture(matrices, this.leftPos + SENARY_PROGRESS_X, this.topPos + SENARY_PROGRESS_Y, SENARY_PROGRESS_U, SENARY_PROGRESS_V, PROGRESS_SIZE, progress - SUMS[5]);
                        }
                    }
                }
            }
        }
    }
}
