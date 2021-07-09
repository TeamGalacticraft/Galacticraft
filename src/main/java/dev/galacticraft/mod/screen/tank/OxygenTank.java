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

import alexiil.mc.lib.attributes.fluid.FixedFluidInv;
import alexiil.mc.lib.attributes.misc.LimitedConsumer;
import alexiil.mc.lib.attributes.misc.Reference;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.block.entity.OxygenCollectorBlockEntity;
import dev.galacticraft.mod.client.gui.screen.ingame.SpaceRaceScreen;
import dev.galacticraft.mod.util.DrawableUtil;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
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
    public OxygenTank(int index, FixedFluidInv inv, int x, int y) {
        super(index, inv, x, y, 0);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void render(MatrixStack matrices, MinecraftClient client, World world, BlockPos pos, int mouseX, int mouseY, boolean colorize, Int2IntMap color) {
        drawOxygenBuffer(matrices, this.x, this.y, (float) inv.getInvFluid(this.index).amount().div(inv.getMaxAmount_F(this.index)).asInexactDouble(), colorize);
    }

    @Override
    public void drawTooltip(MatrixStack matrices, MinecraftClient client, World world, BlockPos pos, int mouseX, int mouseY) {
        if (SpaceRaceScreen.check(mouseX, mouseY, this.x, this.y, Constant.TextureCoordinate.OVERLAY_WIDTH, Constant.TextureCoordinate.OVERLAY_HEIGHT)) {
            List<Text> lines = new ArrayList<>(2);
            lines.add(new TranslatableText("ui.galacticraft.machine.current_oxygen", new LiteralText(Screen.hasShiftDown() ? this.inv.getInvFluid(this.index).amount().toString() + "B" : (this.inv.getInvFluid(this.index).amount().asInt(1000, RoundingMode.HALF_DOWN) + "mB")).setStyle(Constant.Text.BLUE_STYLE)).setStyle(Constant.Text.GOLD_STYLE));
            lines.add(new TranslatableText("ui.galacticraft.machine.max_oxygen", new LiteralText(String.valueOf(OxygenCollectorBlockEntity.MAX_OXYGEN.asInt(1000, RoundingMode.HALF_DOWN))).setStyle(Constant.Text.BLUE_STYLE)).setStyle(Constant.Text.RED_STYLE));

            client.currentScreen.renderTooltip(matrices, lines, mouseX, mouseY);
        }
    }

    void drawOxygenBuffer(MatrixStack matrices, int x, int y, float scale, boolean colorize) {
        if (colorize) {
            RenderSystem.disableTexture();
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            Matrix4f model = matrices.peek().getModel();
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
    public boolean acceptStack(Reference<ItemStack> stack, LimitedConsumer<ItemStack> excess) {
        return super.acceptStack(stack, excess);
    }

    @Override
    public boolean isHoveredOverTank(int mouseX, int mouseY) {
        return SpaceRaceScreen.check(mouseX, mouseY, this.x, this.y, Constant.TextureCoordinate.OVERLAY_WIDTH, Constant.TextureCoordinate.OVERLAY_HEIGHT);
    }
}
