/*
 * Copyright (c) 2020 Team Galacticraft
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

package dev.galacticraft.mod.screen.tank;

import alexiil.mc.lib.attributes.fluid.FixedFluidInv;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.galacticraft.mod.Constants;
import dev.galacticraft.mod.block.entity.OxygenCollectorBlockEntity;
import dev.galacticraft.mod.client.gui.screen.ingame.SpaceRaceScreen;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class OxygenTank extends Tank {
    public OxygenTank(int index, FixedFluidInv inv, int x, int y) {
        super(index, inv, x, y, 0);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void render(MatrixStack matrices, MinecraftClient client, World world, BlockPos pos, int mouseX, int mouseY, boolean colorize, Int2IntMap color) {
        drawOxygenBuffer(matrices.peek().getModel(), this.x, this.y, (float) inv.getInvFluid(this.index).getAmount_F().div(inv.getMaxAmount_F(this.index)).asInexactDouble(), colorize);
    }

    @Override
    public void drawTooltip(MatrixStack matrices, MinecraftClient client, World world, BlockPos pos, int mouseX, int mouseY) {
        if (SpaceRaceScreen.check(mouseX, mouseY, this.x, this.y, Constants.TextureCoordinate.OVERLAY_WIDTH, Constants.TextureCoordinate.OVERLAY_HEIGHT)) {
            List<Text> lines = new ArrayList<>(2);
            lines.add(new TranslatableText("ui.galacticraft.machine.current_oxygen", new LiteralText(Screen.hasShiftDown() ? this.inv.getInvFluid(this.index).getAmount_F().toString() + "B" : (this.inv.getInvFluid(this.index).getAmount_F().asInt(1000, RoundingMode.HALF_DOWN) + "mB")).setStyle(Constants.Text.BLUE_STYLE)).setStyle(Constants.Text.GOLD_STYLE));
            lines.add(new TranslatableText("ui.galacticraft.machine.max_oxygen", new LiteralText(String.valueOf(OxygenCollectorBlockEntity.MAX_OXYGEN.asInt(1000, RoundingMode.HALF_DOWN))).setStyle(Constants.Text.BLUE_STYLE)).setStyle(Constants.Text.RED_STYLE));

            client.currentScreen.renderTooltip(matrices, lines, mouseX, mouseY);
        }
    }

    void drawOxygenBuffer(Matrix4f matrices, int x, int y, float scale, boolean colorize) {
        if (colorize) {
            RenderSystem.disableTexture();
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(matrices, x - 1, y + Constants.TextureCoordinate.OVERLAY_HEIGHT + 1, (float) 0).texture(0, 0).next();
            bufferBuilder.vertex(matrices, x + Constants.TextureCoordinate.OVERLAY_WIDTH + 1, y + Constants.TextureCoordinate.OVERLAY_HEIGHT + 1, (float) 0).texture(0, 0).next();
            bufferBuilder.vertex(matrices, x + Constants.TextureCoordinate.OVERLAY_WIDTH + 1, y - 1, (float) 0).texture(0, 0).next();
            bufferBuilder.vertex(matrices, x - 1, y - 1, (float) 0).texture(0, 0).next();
            bufferBuilder.end();
            RenderSystem.enableAlphaTest();
            BufferRenderer.draw(bufferBuilder);
            RenderSystem.enableTexture();
        }

        MinecraftClient.getInstance().getTextureManager().bindTexture(Constants.ScreenTexture.OVERLAY);
        texturedQuad(matrices, x, y, Constants.TextureCoordinate.OXYGEN_DARK_X, Constants.TextureCoordinate.OXYGEN_DARK_Y, Constants.TextureCoordinate.OVERLAY_HEIGHT);
        texturedQuad(matrices, x, y + Constants.TextureCoordinate.OVERLAY_HEIGHT - (Constants.TextureCoordinate.OVERLAY_HEIGHT * scale), Constants.TextureCoordinate.OXYGEN_LIGHT_X, Constants.TextureCoordinate.OXYGEN_LIGHT_Y, Constants.TextureCoordinate.OVERLAY_HEIGHT * scale);
    }

    void texturedQuad(Matrix4f matrices, float x, float y, float u, float v, float height) {
        float x1 = x + (float) Constants.TextureCoordinate.OVERLAY_WIDTH;
        float y1 = y + height;
        float u0 = u / 128f;
        float v0 = v / 128f;
        float u1 = (u + (float) Constants.TextureCoordinate.OVERLAY_WIDTH) / 128f;
        float v1 = (v + height) / 128f;
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrices, x, y1, (float) 0).texture(u0, v1).next();
        bufferBuilder.vertex(matrices, x1, y1, (float) 0).texture(u1, v1).next();
        bufferBuilder.vertex(matrices, x1, y, (float) 0).texture(u1, v0).next();
        bufferBuilder.vertex(matrices, x, y, (float) 0).texture(u0, v0).next();
        bufferBuilder.end();
        RenderSystem.enableAlphaTest();
        BufferRenderer.draw(bufferBuilder);
    }

}
