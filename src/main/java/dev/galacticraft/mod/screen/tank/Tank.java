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

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.*;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.lib.attributes.misc.LimitedConsumer;
import alexiil.mc.lib.attributes.misc.Reference;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.attribute.Automatable;
import dev.galacticraft.mod.client.gui.screen.ingame.SpaceRaceScreen;
import dev.galacticraft.mod.util.DrawableUtil;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.commons.lang3.text.WordUtils;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class Tank {
    private static final int[] FLUID_TANK_8_16_DATA = new int[]{
            Constant.TextureCoordinate.FLUID_TANK_8_16_X,
            Constant.TextureCoordinate.FLUID_TANK_8_16_Y,
            Constant.TextureCoordinate.FLUID_TANK_8_16_HEIGHT
    };
    private static final int[] FLUID_TANK_7_14_DATA = new int[]{
            Constant.TextureCoordinate.FLUID_TANK_7_14_X,
            Constant.TextureCoordinate.FLUID_TANK_7_14_Y,
            Constant.TextureCoordinate.FLUID_TANK_7_14_HEIGHT
    };
    private static final int[] FLUID_TANK_6_12_DATA = new int[]{
            Constant.TextureCoordinate.FLUID_TANK_6_12_X,
            Constant.TextureCoordinate.FLUID_TANK_6_12_Y,
            Constant.TextureCoordinate.FLUID_TANK_6_12_HEIGHT
    };
    private static final int[] FLUID_TANK_5_10_DATA = new int[]{
            Constant.TextureCoordinate.FLUID_TANK_5_10_X,
            Constant.TextureCoordinate.FLUID_TANK_5_10_Y,
            Constant.TextureCoordinate.FLUID_TANK_5_10_HEIGHT
    };
    private static final int[] FLUID_TANK_4_8_DATA = new int[]{
            Constant.TextureCoordinate.FLUID_TANK_4_8_X,
            Constant.TextureCoordinate.FLUID_TANK_4_8_Y,
            Constant.TextureCoordinate.FLUID_TANK_4_8_HEIGHT
    };
    private static final int[] FLUID_TANK_3_6_DATA = new int[]{
            Constant.TextureCoordinate.FLUID_TANK_3_6_X,
            Constant.TextureCoordinate.FLUID_TANK_3_6_Y,
            Constant.TextureCoordinate.FLUID_TANK_3_6_HEIGHT
    };
    private static final int[] FLUID_TANK_2_4_DATA = new int[]{
            Constant.TextureCoordinate.FLUID_TANK_2_4_X,
            Constant.TextureCoordinate.FLUID_TANK_2_4_Y,
            Constant.TextureCoordinate.FLUID_TANK_2_4_HEIGHT
    };
    private static final int[] FLUID_TANK_1_2_DATA = new int[]{
            Constant.TextureCoordinate.FLUID_TANK_1_2_X,
            Constant.TextureCoordinate.FLUID_TANK_1_2_Y,
            Constant.TextureCoordinate.FLUID_TANK_1_2_HEIGHT
    };

    public int id;
    public final int index;
    public final FixedFluidInv inv;
    public final int x;
    public final int y;
    public final int scale;

    public Tank(int index, FixedFluidInv inv, int x, int y, int scale) {
        this.index = index;
        this.inv = inv;
        this.x = x;
        this.y = y;
        this.scale = scale;
    }
    
    public int[] getPositionData() {
        if (this.inv.getMaxAmount_F(this.index).asInexactDouble() != Math.floor(this.inv.getMaxAmount_F(this.index).asInexactDouble())) {
            throw new UnsupportedOperationException("NYI");
        }
        int size = this.inv.getMaxAmount_F(this.index).asInt(1) * scale;

        if (size < 1 || size > 16) {
            throw new UnsupportedOperationException("NYI");
        }
        if (size > 8 && size % 2 == 1) {
            throw new UnsupportedOperationException("NYI");
        }

        if (size == 8 || size == 16) {
            return FLUID_TANK_8_16_DATA;
        } else if (size == 7 || size == 14) {
            return FLUID_TANK_7_14_DATA;
        } else if (size == 6 || size == 12) {
            return FLUID_TANK_6_12_DATA;
        } else if (size == 5 || size == 10) {
            return FLUID_TANK_5_10_DATA;
        } else if (size == 4) {
            return FLUID_TANK_4_8_DATA;
        } else if (size == 3) {
            return FLUID_TANK_3_6_DATA;
        } else if (size == 2) {
            return FLUID_TANK_2_4_DATA;
        } else if (size == 1) {
            return FLUID_TANK_1_2_DATA;
        }
        throw new AssertionError();
    }

    @Environment(EnvType.CLIENT)
    public void render(MatrixStack matrices, MinecraftClient client, World world, BlockPos pos, int mouseX, int mouseY, boolean coloured, Int2IntMap color) {
        if (this.scale == 0) return;
        int[] data = this.getPositionData();
        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.OVERLAY);
        if (coloured) {
            int c = color.get(this.index);
            DrawableUtil.drawTextureColor(matrices, this.x, this.y, 0, data[0], data[1] + Constant.TextureCoordinate.FLUID_TANK_UNDERLAY_OFFSET, Constant.TextureCoordinate.FLUID_TANK_WIDTH, data[2], 128, 128, c >> 16 & 0xFF, c >> 8 & 0xFF, c & 0xFF, 80);
        } else {
            DrawableHelper.drawTexture(matrices, this.x, this.y, 0, data[0], data[1] + Constant.TextureCoordinate.FLUID_TANK_UNDERLAY_OFFSET, Constant.TextureCoordinate.FLUID_TANK_WIDTH, data[2], 128, 128);
        }

        FluidVolume content = this.inv.getInvFluid(this.index);
        if (content.isEmpty()) return;
        matrices.push();
        double scale = content.amount().div(this.inv.getMaxAmount_F(this.index)).asInexactDouble();
        Sprite sprite = FluidRenderHandlerRegistry.INSTANCE.get(content.getRawFluid()).getFluidSprites(world, pos, content.getRawFluid().getDefaultState())[0];
        RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
        drawSprite(matrices, this.x + 1, this.y + (float)(-(data[2] - 1) * scale) + data[2] - 1, 0, Constant.TextureCoordinate.FLUID_TANK_WIDTH - 1, (float)((data[2] - 1) * scale), sprite);
        matrices.pop();
        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.OVERLAY);
        DrawableHelper.drawTexture(matrices, this.x, this.y, 0, data[0], data[1], Constant.TextureCoordinate.FLUID_TANK_WIDTH, data[2], 128, 128);
    }

    public void drawTooltip(MatrixStack matrices, MinecraftClient client, World world, BlockPos pos, int mouseX, int mouseY) {
        matrices.translate(0, 0, 1);
        if (SpaceRaceScreen.check(mouseX, mouseY, this.x, this.y, Constant.TextureCoordinate.FLUID_TANK_WIDTH, this.getPositionData()[2])) {
            List<Text> lines = new ArrayList<>(2);
            FluidVolume volume = this.inv.getInvFluid(this.index);
            if (volume.isEmpty()) {
                client.currentScreen.renderTooltip(matrices, new TranslatableText("ui.galacticraft.machine.fluid_inv.empty").setStyle(Constant.Text.GRAY_STYLE), mouseX, mouseY);
                return;
            }
            MutableText amount;
            if (Screen.hasShiftDown()) {
                amount = new LiteralText(volume.amount().toString() + "B");
            } else {
                amount = new LiteralText((volume.amount().asInt(1000, RoundingMode.HALF_DOWN)) + "mB");
            }

            lines.add(new TranslatableText("ui.galacticraft.machine.fluid_inv.fluid").setStyle(Constant.Text.GRAY_STYLE).append(new LiteralText(getName(volume.getRawFluid())).setStyle(Constant.Text.BLUE_STYLE)));
            lines.add(new TranslatableText("ui.galacticraft.machine.fluid_inv.amount").setStyle(Constant.Text.GRAY_STYLE).append(amount.setStyle(Style.EMPTY.withColor(Formatting.WHITE))));
            client.currentScreen.renderTooltip(matrices, lines, mouseX, mouseY);
        }
        matrices.translate(0, 0, -1);
    }

    private String getName(Fluid fluid) {
        Identifier id = Registry.FLUID.getId(fluid);
        if (I18n.hasTranslation("block." + id.getNamespace() + "." + id.getPath())) {
            return WordUtils.capitalizeFully(I18n.translate("block." + id.getNamespace() + "." + id.getPath()));
        }
        return WordUtils.capitalizeFully(id.getPath().replace("flowing", "").replace("still", "").replace("_", " ").trim());
    }

    public boolean isHoveredOverTank(int mouseX, int mouseY) {
        int[] data = getPositionData();
        return SpaceRaceScreen.check(mouseX, mouseY, this.x, this.y, Constant.TextureCoordinate.FLUID_TANK_WIDTH, data[2]);
    }

    public void renderHighlight(MatrixStack matrices, MinecraftClient client, World world, BlockPos pos, int mouseX, int mouseY) {
        int[] data = getPositionData();
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        DrawableHelper.fill(matrices, this.x, this.y,this.x + Constant.TextureCoordinate.FLUID_TANK_WIDTH, this.y + data[2], -2130706433);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }

    public boolean acceptStack(Reference<ItemStack> stack, LimitedConsumer<ItemStack> excess) {
        if (this.inv instanceof Automatable automatable) {
            FluidExtractable extractable = FluidAttributes.EXTRACTABLE.getFirstOrNull(stack, excess);
            if (extractable != null && !extractable.attemptExtraction(this.inv.getFilterForTank(this.index), FluidAmount.MAX_BUCKETS, Simulation.SIMULATE).isEmpty()) {
                if (automatable.getTypes()[this.index].getType().isInput()) {
                    FluidVolumeUtil.move(extractable, this.inv.getTank(this.index));
                    ClientPlayNetworking.send(new Identifier(Constant.MOD_ID, "tank_modify"), new PacketByteBuf(Unpooled.buffer().writeInt(this.index)));
                    return true;
                }
            } else {
                FluidInsertable insertable = FluidAttributes.INSERTABLE.getFirstOrNull(stack, excess);
                if (insertable != null) {
                    if (automatable.getTypes()[this.index].getType().isOutput()) {
                        FluidVolumeUtil.move(this.inv.getTank(this.index), insertable);
                        ClientPlayNetworking.send(new Identifier(Constant.MOD_ID, "tank_modify"), new PacketByteBuf(Unpooled.buffer().writeInt(this.index)));
                        return true;
                    }

                }
            }
        }
        return false;
    }

    public static void drawSprite(MatrixStack matrices, float x, float y, float z, float width, float height, Sprite sprite) {
        DrawableUtil.drawTexturedQuad_F(matrices.peek().getModel(), x, x + width, y, y + height, z, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
    }
}
