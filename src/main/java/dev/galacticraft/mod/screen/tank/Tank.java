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

import dev.galacticraft.api.fluid.FluidStack;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.lookup.storage.MachineFluidStorage;
import dev.galacticraft.mod.screen.slot.ResourceFlow;
import dev.galacticraft.mod.util.DrawableUtil;
import dev.galacticraft.mod.util.FluidUtil;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class Tank {
    public int id;
    public final int index;
    public final MachineFluidStorage inv;
    public final int x;
    public final int y;
    public final int scale;

    public Tank(int index, MachineFluidStorage inv, int x, int y, int scale) {
        this.index = index;
        this.inv = inv;
        this.x = x;
        this.y = y;
        this.scale = scale;
    }

    @Environment(EnvType.CLIENT)
    public void render(MatrixStack matrices, MinecraftClient client, World world, BlockPos pos, int mouseX, int mouseY, boolean coloured, Int2IntMap color) {
//        if (this.scale == 0) return;
//        int[] data = this.getPositionData();
//        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.OVERLAY);
//        if (coloured) {
//            int c = color.get(this.index);
//            DrawableUtil.drawTextureColor(matrices, this.x, this.y, 0, data[0], data[1] + Constant.TextureCoordinate.FLUID_TANK_UNDERLAY_OFFSET, Constant.TextureCoordinate.FLUID_TANK_WIDTH, data[2], 128, 128, c >> 16 & 0xFF, c >> 8 & 0xFF, c & 0xFF, 80);
//        } else {
//            DrawableHelper.drawTexture(matrices, this.x, this.y, 0, data[0], data[1] + Constant.TextureCoordinate.FLUID_TANK_UNDERLAY_OFFSET, Constant.TextureCoordinate.FLUID_TANK_WIDTH, data[2], 128, 128);
//        }
//
//        FluidStack content = this.inv.getStack(this.index);
//        if (content.isEmpty()) return;
//        matrices.push();
//        double scale = content.amount().div(this.inv.getCapacity(this.index)).asInexactDouble();
//        Sprite sprite = FluidRenderHandlerRegistry.INSTANCE.get(content.getRawFluid()).getFluidSprites(world, pos, content.getRawFluid().getDefaultState())[0];
//        RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
//        drawSprite(matrices, this.x + 1, this.y + (float)(-(data[2] - 1) * scale) + data[2] - 1, 0, Constant.TextureCoordinate.FLUID_TANK_WIDTH - 1, (float)((data[2] - 1) * scale), sprite);
//        matrices.pop();
//        RenderSystem.setShaderTexture(0, Constant.ScreenTexture.OVERLAY);
//        DrawableHelper.drawTexture(matrices, this.x, this.y, 0, data[0], data[1], Constant.TextureCoordinate.FLUID_TANK_WIDTH, data[2], 128, 128);
    }

    public void drawTooltip(MatrixStack matrices, MinecraftClient client, World world, BlockPos pos, int mouseX, int mouseY) {
        matrices.translate(0, 0, 1);
        if (this.isHoveredOverTank(mouseX, mouseY)) {
            List<Text> lines = new ArrayList<>(2);
            FluidStack volume = this.inv.getFluid(this.index);
            if (volume.isEmpty()) {
                client.currentScreen.renderTooltip(matrices, new TranslatableText("ui.galacticraft.machine.fluid_inv.empty").setStyle(Constant.Text.GRAY_STYLE), mouseX, mouseY);
                return;
            }
            MutableText amount;
            if (Screen.hasShiftDown() || volume.amount() / 81.0 < 10000) {
                amount = new LiteralText(DrawableUtil.roundForDisplay(volume.amount() / 81.0, 0) + FluidUtil.SUFFIX_MILLIBUCKETS);
            } else {
                amount = new LiteralText(DrawableUtil.roundForDisplay(volume.amount() / 81000.0, 2) + FluidUtil.SUFFIX_BUCKETS);
            }

            lines.add(new TranslatableText("ui.galacticraft.machine.fluid_inv.fluid").setStyle(Constant.Text.GRAY_STYLE).append(new LiteralText(getName(volume.fluid().getFluid())).setStyle(Constant.Text.BLUE_STYLE)));
            lines.add(new TranslatableText("ui.galacticraft.machine.fluid_inv.amount").setStyle(Constant.Text.GRAY_STYLE).append(amount.setStyle(Style.EMPTY.withColor(Formatting.WHITE))));
            client.currentScreen.renderTooltip(matrices, lines, mouseX, mouseY);
        }
        matrices.translate(0, 0, -1);
    }

    private String getName(Fluid fluid) {
        Identifier id = Registry.FLUID.getId(fluid);
        if (I18n.hasTranslation("block." + id.getNamespace() + "." + id.getPath())) {
            return I18n.translate("block." + id.getNamespace() + "." + id.getPath());
        }
        char[] chars = id.getPath().replace("flowing", "").replace("still", "").replace("_", " ").trim().toCharArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (i == 0 || chars[i - 1] == ' ') {
                builder.append(Character.toUpperCase(chars[i]));
            } else {
                builder.append(chars[i]);
            }
        }
        return builder.toString();
    }

    public boolean isHoveredOverTank(int mouseX, int mouseY) {
        return false;//DrawableUtil.isWithin(mouseX, mouseY, this.x, this.y, Constant.TextureCoordinate.FLUID_TANK_WIDTH, data[2]);
    }

    public void renderHighlight(MatrixStack matrices, MinecraftClient client, World world, BlockPos pos, int mouseX, int mouseY) {
//        int[] data = getPositionData();
//        RenderSystem.disableDepthTest();
//        RenderSystem.colorMask(true, true, true, false);
//        DrawableHelper.fill(matrices, this.x, this.y,this.x + Constant.TextureCoordinate.FLUID_TANK_WIDTH, this.y + data[2], 0x80ffffff);
//        RenderSystem.colorMask(true, true, true, true);
//        RenderSystem.enableDepthTest();
    }

    public boolean acceptStack(ContainerItemContext context) {
        Storage<FluidVariant> storage = context.find(FluidStorage.ITEM);
        if (storage != null) {
            if (storage.supportsExtraction()) {
                try (Transaction transaction = Transaction.openOuter()) {
                    FluidVariant storedResource = StorageUtil.findStoredResource(storage, this.inv.getFilter(this.index), transaction);
                    if (storedResource != null) {
                        if (this.inv.getTypes()[this.index].getType().canFlow(ResourceFlow.INPUT)) {
                            if (FluidUtil.move(storedResource, storage, this.inv.getTank(this.index), Long.MAX_VALUE, transaction) != 0) {
                                ClientPlayNetworking.send(new Identifier(Constant.MOD_ID, "tank_modify"), new PacketByteBuf(Unpooled.buffer().writeInt(this.index)));
                                transaction.commit();
                            }
                            return true;
                        }
                    }
                }
            } else if (storage.supportsInsertion()) {
                FluidVariant storedResource = this.inv.getTank(this.index).getResource();
                if (!storedResource.isBlank()) {
                    if (this.inv.getTypes()[this.index].getType().canFlow(ResourceFlow.OUTPUT)) {
                        try (Transaction transaction = Transaction.openOuter()) {
                            if (FluidUtil.move(storedResource, this.inv.getTank(this.index), storage, Long.MAX_VALUE, transaction) != 0) {
                                ClientPlayNetworking.send(new Identifier(Constant.MOD_ID, "tank_modify"), new PacketByteBuf(Unpooled.buffer().writeInt(this.index)));
                                transaction.commit();
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
