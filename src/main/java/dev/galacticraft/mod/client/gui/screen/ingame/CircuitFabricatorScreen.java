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
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class CircuitFabricatorScreen extends MachineScreen<CircuitFabricatorBlockEntity, RecipeMachineMenu<Container, FabricationRecipe, CircuitFabricatorBlockEntity>> {
    private static final int PROGRESS_SIZE = 4;
    private static final int INITIAL_PROGRESS_U = 0;
    private static final int INITIAL_PROGRESS_V = 186;
    private static final int INITIAL_PROGRESS_X = 48;
    private static final int INITIAL_PROGRESS_Y = 21;
    private static final int SECONDARY_PROGRESS_U = 31;
    private static final int SECONDARY_PROGRESS_V = 216;
    private static final int SECONDARY_PROGRESS_X = 79;
    private static final int SECONDARY_PROGRESS_Y = 51;
    private static final int SECONDARY_CONCURRENT_PROGRESS_U = 31;
    private static final int SECONDARY_CONCURRENT_PROGRESS_V = 237;
    private static final int SECONDARY_CONCURRENT_PROGRESS_X = 79;
    private static final int SECONDARY_CONCURRENT_PROGRESS_Y = 72;
    private static final int SECONDARY_CONCURRENT_PROGRESS_2_U = 45;
    private static final int SECONDARY_CONCURRENT_PROGRESS_2_V = 220;
    private static final int SECONDARY_CONCURRENT_PROGRESS_2_X = 93;
    private static final int SECONDARY_CONCURRENT_PROGRESS_2_Y = 68;
    private static final int TERTIARY_PROGRESS_U = 49;
    private static final int TERTIARY_PROGRESS_V = 216;
    private static final int TERTIARY_PROGRESS_X = 97;
    private static final int TERTIARY_PROGRESS_Y = 51;
    private static final int QUATERNARY_PROGRESS_U = 65;
    private static final int QUATERNARY_PROGRESS_V = 220;
    private static final int QUATERNARY_PROGRESS_X = 113;
    private static final int QUATERNARY_PROGRESS_Y = 55;
    private static final int QUINARY_PROGRESS_U = 92;
    private static final int QUINARY_PROGRESS_V = 197;
    private static final int QUINARY_PROGRESS_X = 140;
    private static final int QUINARY_PROGRESS_Y = 51;
    private static final int QUINARY_PROGRESS_HEIGHT = 19;
    private static final int SENARY_PROGRESS_U = 110;
    private static final int SENARY_PROGRESS_V = 220;
    private static final int SENARY_PROGRESS_X = 158;
    private static final int SENARY_PROGRESS_Y = 55;

    public CircuitFabricatorScreen(RecipeMachineMenu<Container, FabricationRecipe, CircuitFabricatorBlockEntity> handler, Inventory inv, Component title) {
        super(handler, title, Constant.ScreenTexture.CIRCUIT_FABRICATOR_SCREEN);
        this.imageHeight = 176;

        this.capacitorX = 8;
        this.capacitorY = 15;
    }

    @Override
    protected void renderMachineBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderMachineBackground(graphics, mouseX, mouseY, delta);
        this.drawProgressBar(graphics.pose());
    }

    //24 + 19 + 18 + 65 + 14 = 140
    private void drawProgressBar(PoseStack matrices) {
        assert this.minecraft != null;
        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.CIRCUIT_FABRICATOR_SCREEN);
        if (this.menu.getProgress() > 0) {
            float progress = (float) ((((double) this.menu.getProgress()) / ((double) this.menu.getMaxProgress())) * 140.0);
            if (progress <= 24) {
                DrawableUtil.drawProgressTexture(matrices, this.leftPos + INITIAL_PROGRESS_X, this.topPos + INITIAL_PROGRESS_Y, INITIAL_PROGRESS_U, INITIAL_PROGRESS_V, progress, PROGRESS_SIZE);
            } else {
                DrawableUtil.drawProgressTexture(matrices, this.leftPos + INITIAL_PROGRESS_X, this.topPos + INITIAL_PROGRESS_Y, INITIAL_PROGRESS_U, INITIAL_PROGRESS_V, 24, Math.min(19, progress - 24) + 4);
                if (progress > 24 + 19) {
                    DrawableUtil.drawProgressTexture(matrices, this.leftPos + SECONDARY_PROGRESS_X, this.topPos + SECONDARY_PROGRESS_Y, SECONDARY_PROGRESS_U, SECONDARY_PROGRESS_V, Math.min(18, progress - (24 + 19)), PROGRESS_SIZE);
                    DrawableUtil.drawProgressTexture(matrices, this.leftPos + SECONDARY_CONCURRENT_PROGRESS_X, this.topPos + SECONDARY_CONCURRENT_PROGRESS_Y - 4, SECONDARY_CONCURRENT_PROGRESS_U, SECONDARY_CONCURRENT_PROGRESS_V - 4, (float) Math.min(18, ((progress - (24.0 + 19.0)) * 1.77777777778)), 4);

                    if (!(((progress - (24.0 + 19.0)) * 1.77777777778) <= 18.0)) { //18 + 14 = 32 // 32/18
                        double min = Math.min(14, ((progress - (24.0 + 19.0 + (18.0 * (0.5624999999992969)))) * 1.77777777778));
                        DrawableUtil.drawProgressTexture(matrices, this.leftPos + SECONDARY_CONCURRENT_PROGRESS_2_X, (float) (this.topPos + SECONDARY_CONCURRENT_PROGRESS_2_Y - min), SECONDARY_CONCURRENT_PROGRESS_2_U, SECONDARY_CONCURRENT_PROGRESS_2_V, PROGRESS_SIZE, (float) min);
                    }

                    if (progress > 24 + 19 + 18) {
                        DrawableUtil.drawProgressTexture(matrices, this.leftPos + TERTIARY_PROGRESS_X, this.topPos + TERTIARY_PROGRESS_Y, TERTIARY_PROGRESS_U, TERTIARY_PROGRESS_V, progress - (24 + 19 + 18), PROGRESS_SIZE);
                        if (progress > 24 + 19 + 18 + 17 + 3) {
                            DrawableUtil.drawProgressTexture(matrices, this.leftPos + QUATERNARY_PROGRESS_X, this.topPos + QUATERNARY_PROGRESS_Y, QUATERNARY_PROGRESS_U, QUATERNARY_PROGRESS_V, PROGRESS_SIZE, Math.min(14, progress - (24 + 19 + 18 + 17 + 3)));
                        }
                        if (progress > 24 + 19 + 18 + 44 + 3) {
                            DrawableUtil.drawProgressTexture(matrices, this.leftPos + QUINARY_PROGRESS_X, (float) (this.topPos + QUINARY_PROGRESS_Y - Math.floor(Math.min(QUINARY_PROGRESS_HEIGHT, progress - (24 + 19 + 18 + 44 + 3)))), QUINARY_PROGRESS_U, QUINARY_PROGRESS_V, PROGRESS_SIZE, Math.min(19, progress - (24 + 19 + 18 + 44 + 3)));
                        }
                        if (progress > 24 + 19 + 18 + 65) {
                            DrawableUtil.drawProgressTexture(matrices, this.leftPos + SENARY_PROGRESS_X, this.topPos + SENARY_PROGRESS_Y, SENARY_PROGRESS_U, SENARY_PROGRESS_V, PROGRESS_SIZE, progress - (24 + 19 + 18 + 65));
                        }
                    }
                }
            }
        }
    }
}
