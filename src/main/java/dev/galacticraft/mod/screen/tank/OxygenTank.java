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

package dev.galacticraft.mod.screen.tank;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.block.entity.OxygenCollectorBlockEntity;
import dev.galacticraft.mod.lookup.storage.MachineFluidStorage;
import dev.galacticraft.mod.util.DrawableUtil;
import dev.galacticraft.mod.util.FluidUtil;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
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

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class OxygenTank extends Tank {
    public OxygenTank(int index, MachineFluidStorage inv, int x, int y) {
        super(index, inv, x, y, 0);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void render(MatrixStack matrices, MinecraftClient client, World world, BlockPos pos, int mouseX, int mouseY, boolean colorize, Int2IntMap color) {
        drawOxygenBuffer(matrices, this.x, this.y, (float) ((double)inv.getFluid(this.index).amount() / (double)inv.getCapacity(this.index)), colorize);
    }

    @Override
    public void drawTooltip(MatrixStack matrices, MinecraftClient client, World world, BlockPos pos, int mouseX, int mouseY) {
        if (DrawableUtil.isWithin(mouseX, mouseY, this.x, this.y, Constant.TextureCoordinate.OVERLAY_WIDTH, Constant.TextureCoordinate.OVERLAY_HEIGHT)) {
            List<Text> lines = new ArrayList<>(2);
            long amount = this.inv.getFluid(this.index).amount();
            lines.add(new TranslatableText("ui.galacticraft.machine.current_oxygen", new LiteralText(!Screen.hasShiftDown() || amount / 81.0 >= 10000 ? (DrawableUtil.roundForDisplay(amount / 81000.0, 2) + FluidUtil.SUFFIX_BUCKETS) : (DrawableUtil.roundForDisplay(amount / 81.0, 0) + FluidUtil.SUFFIX_MILLIBUCKETS)).setStyle(Constant.Text.BLUE_STYLE)).setStyle(Constant.Text.GOLD_STYLE));
            lines.add(new TranslatableText("ui.galacticraft.machine.max_oxygen", new LiteralText(DrawableUtil.roundForDisplay(this.inv.getCapacity(this.index) / 81000.0, 2) + FluidUtil.SUFFIX_BUCKETS).setStyle(Constant.Text.BLUE_STYLE)).setStyle(Constant.Text.RED_STYLE));

            client.currentScreen.renderTooltip(matrices, lines, mouseX, mouseY);
        }
    }

    void drawOxygenBuffer(MatrixStack matrices, int x, int y, float scale, boolean colorize) {
        if (colorize) {
            RenderSystem.disableTexture();
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            Matrix4f model = matrices.peek().getPositionMatrix();
            bufferBuilder.vertex(model, x - 1, y + Constant.TextureCoordinate.OVERLAY_HEIGHT + 1, (float) 0).texture(0, 0).next();
            bufferBuilder.vertex(model, x + Constant.TextureCoordinate.OVERLAY_WIDTH + 1, y + Constant.TextureCoordinate.OVERLAY_HEIGHT + 1, (float) 0).texture(0, 0).next();
            bufferBuilder.vertex(model, x + Constant.TextureCoordinate.OVERLAY_WIDTH + 1, y - 1, (float) 0).texture(0, 0).next();
            bufferBuilder.vertex(model, x - 1, y - 1, (float) 0).texture(0, 0).next();
            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            RenderSystem.enableTexture();
        }

        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.OVERLAY);
        DrawableUtil.drawProgressTexture(matrices, x, y, 0, Constant.TextureCoordinate.OXYGEN_DARK_X, Constant.TextureCoordinate.OXYGEN_DARK_Y, Constant.TextureCoordinate.OVERLAY_WIDTH, Constant.TextureCoordinate.OVERLAY_HEIGHT, 128, 128);
        DrawableUtil.drawProgressTexture(matrices, x, (int) (y + Constant.TextureCoordinate.OVERLAY_HEIGHT - (Constant.TextureCoordinate.OVERLAY_HEIGHT * scale)), 0, Constant.TextureCoordinate.OXYGEN_LIGHT_X, Constant.TextureCoordinate.OXYGEN_LIGHT_Y, Constant.TextureCoordinate.OVERLAY_WIDTH, Constant.TextureCoordinate.OVERLAY_HEIGHT * scale, 128, 128);
    }

    @Override
    public void renderHighlight(MatrixStack matrices, MinecraftClient client, World world, BlockPos pos, int mouseX, int mouseY) {
    }

    @Override
    public boolean acceptStack(ContainerItemContext context) {
        return super.acceptStack(context);
    }

    @Override
    public boolean isHoveredOverTank(int mouseX, int mouseY) {
        return DrawableUtil.isWithin(mouseX, mouseY, this.x, this.y, Constant.TextureCoordinate.OVERLAY_WIDTH, Constant.TextureCoordinate.OVERLAY_HEIGHT);
    }
}
