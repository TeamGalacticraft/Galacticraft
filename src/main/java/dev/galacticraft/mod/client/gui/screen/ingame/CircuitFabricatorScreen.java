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
import dev.galacticraft.mod.api.client.screen.MachineHandledScreen;
import dev.galacticraft.mod.block.entity.CircuitFabricatorBlockEntity;
import dev.galacticraft.mod.client.gui.widget.machine.CapacitorWidget;
import dev.galacticraft.mod.screen.RecipeMachineScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Matrix4f;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class CircuitFabricatorScreen extends MachineHandledScreen<RecipeMachineScreenHandler<CircuitFabricatorBlockEntity>> {
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
    private static final int QUATERNARY_PROGRESS_HEIGHT = 65;
    private static final int QUINARY_PROGRESS_U = 92;
    private static final int QUINARY_PROGRESS_V = 197;
    private static final int QUINARY_PROGRESS_X = 140;
    private static final int QUINARY_PROGRESS_Y = 51;
    private static final int QUINARY_PROGRESS_HEIGHT = 19;
    private static final int SENARY_PROGRESS_U = 110;
    private static final int SENARY_PROGRESS_V = 220;
    private static final int SENARY_PROGRESS_X = 158;
    private static final int SENARY_PROGRESS_Y = 55;
    private static final int SENARY_PROGRESS_HEIGHT = 14;

    public CircuitFabricatorScreen(RecipeMachineScreenHandler<CircuitFabricatorBlockEntity> handler, PlayerInventory inv, Text title) {
        super(handler, inv, inv.player.world, handler.machine.getPos(), title, Constant.ScreenTexture.CIRCUIT_FABRICATOR_SCREEN);
        this.backgroundHeight = 176;
        this.addWidget(new CapacitorWidget(handler.machine.capacitor(), 8, 15, 48, this::getEnergyTooltipLines, handler.machine::getStatus));
    }

    @Override
    protected void renderBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        super.renderBackground(matrices, delta, mouseX, mouseY);
        this.drawProgressBar(matrices);
    }

    @Override
    protected void renderForeground(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderForeground(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, textRenderer, I18n.translate("block.galacticraft.circuit_fabricator"), (this.width / 2), this.y + 5, Formatting.DARK_GRAY.getColorValue());
    }

    //24 + 19 + 18 + 65 + 14 = 140
    private void drawProgressBar(MatrixStack matrices) {
        assert this.client != null;
        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.CIRCUIT_FABRICATOR_SCREEN);
        if (this.handler.machine.progress() > 0) {
            float progress = (float) ((((double) this.handler.machine.progress()) / ((double) this.handler.machine.maxProgress())) * 140.0);
            if (progress <= 24) {
                this.drawTexture(matrices, this.x + INITIAL_PROGRESS_X, this.y + INITIAL_PROGRESS_Y, INITIAL_PROGRESS_U, INITIAL_PROGRESS_V, progress, PROGRESS_SIZE);
            } else {
                this.drawTexture(matrices, this.x + INITIAL_PROGRESS_X, this.y + INITIAL_PROGRESS_Y, INITIAL_PROGRESS_U, INITIAL_PROGRESS_V, 24, Math.min(19, progress - 24) + 4);
                if (progress > 24 + 19) {
                    this.drawTexture(matrices, this.x + SECONDARY_PROGRESS_X, this.y + SECONDARY_PROGRESS_Y, SECONDARY_PROGRESS_U, SECONDARY_PROGRESS_V, Math.min(18, progress - (24 + 19)), PROGRESS_SIZE);
                    this.drawTexture(matrices, this.x + SECONDARY_CONCURRENT_PROGRESS_X, this.y + SECONDARY_CONCURRENT_PROGRESS_Y - 4, SECONDARY_CONCURRENT_PROGRESS_U, SECONDARY_CONCURRENT_PROGRESS_V - 4, Math.min(18, (int)((progress - (24.0 + 19.0)) * 1.77777777778)), 4);

                    if (!(((progress - (24.0 + 19.0)) * 1.77777777778) <= 18.0)) { //18 + 14 = 32 // 32/18
                        this.drawTexture(matrices, this.x + SECONDARY_CONCURRENT_PROGRESS_2_X, this.y + SECONDARY_CONCURRENT_PROGRESS_2_Y - Math.min(14, (int)((progress - (24.0 + 19.0 + (18.0 * (0.5624999999992969)))) * 1.77777777778)), SECONDARY_CONCURRENT_PROGRESS_2_U, SECONDARY_CONCURRENT_PROGRESS_2_V, PROGRESS_SIZE, Math.min(14, (int)((progress - (24.0 + 19.0 + (18.0 * (0.5624999999992969)))) * 1.77777777778)));
                    }

                    if (progress > 24 + 19 + 18) {
                        this.drawTexture(matrices, this.x + TERTIARY_PROGRESS_X, this.y + TERTIARY_PROGRESS_Y, TERTIARY_PROGRESS_U, TERTIARY_PROGRESS_V, progress - (24 + 19 + 18), PROGRESS_SIZE);
                        if (progress > 24 + 19 + 18 + 17 + 3) {
                            this.drawTexture(matrices, this.x + QUATERNARY_PROGRESS_X, this.y + QUATERNARY_PROGRESS_Y, QUATERNARY_PROGRESS_U, QUATERNARY_PROGRESS_V, PROGRESS_SIZE, Math.min(14, progress - (24 + 19 + 18 + 17 + 3)));
                        }
                        if (progress > 24 + 19 + 18 + 44 + 3) {
                            this.drawTexture(matrices, this.x + QUINARY_PROGRESS_X, (int) (this.y + QUINARY_PROGRESS_Y - Math.floor(Math.min(QUINARY_PROGRESS_HEIGHT, progress - (24 + 19 + 18 + 44 + 3)))), QUINARY_PROGRESS_U, QUINARY_PROGRESS_V, PROGRESS_SIZE, Math.min(19, progress - (24 + 19 + 18 + 44 + 3)));
                        }
                        if (progress > 24 + 19 + 18 + 65) {
                            this.drawTexture(matrices, this.x + SENARY_PROGRESS_X, this.y + SENARY_PROGRESS_Y, SENARY_PROGRESS_U, SENARY_PROGRESS_V, PROGRESS_SIZE, progress - (24 + 19 + 18 + 65));
                        }
                    }
                }
            }
        }
    }

    public void drawTexture(MatrixStack matrices, int x, int y, int u, int v, float width, float height) {
        drawTexture(matrices, x, y, this.getZOffset(), (float)u, (float)v, width, height, 256, 256);
    }

    public static void drawTexture(MatrixStack matrices, int x, int y, int z, float u, float v, float width, float height, int textureHeight, int textureWidth) {
        drawTexture(matrices, x, x + width, y, y + height, z, width, height, u, v, textureWidth, textureHeight);
    }

    private static void drawTexture(MatrixStack matrices, float x0, float y0, float x1, float y1, float z, float regionWidth, float regionHeight, float u, float v, int textureWidth, int textureHeight) {
        drawTexturedQuad(matrices.peek().getModel(), x0, y0, x1, y1, z, (u + 0.0F) / (float)textureWidth, (u + regionWidth) / (float)textureWidth, (v + 0.0F) / (float)textureHeight, (v + regionHeight) / (float)textureHeight);
    }

    private static void drawTexturedQuad(Matrix4f matrices, float x0, float x1, float y0, float y1, float z, float u0, float u1, float v0, float v1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrices, x0, y1, z).texture(u0, v1).next();
        bufferBuilder.vertex(matrices, x1, y1, z).texture(u1, v1).next();
        bufferBuilder.vertex(matrices, x1, y0, z).texture(u1, v0).next();
        bufferBuilder.vertex(matrices, x0, y0, z).texture(u0, v0).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }
}
