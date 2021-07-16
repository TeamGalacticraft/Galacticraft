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

package dev.galacticraft.mod.block.entity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import dev.galacticraft.api.rocket.part.RocketPartType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.item.GalacticraftItem;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketDesignerBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    public static final int SCHEMATIC_OUTPUT_SLOT = 0;

    private int red = 255;
    private int green = 255;
    private int blue = 255;
    private int alpha = 255;

    private Identifier cone = null;
    private Identifier body = null;
    private Identifier fin = null;
    private Identifier booster = null;
    private Identifier bottom = null;
    private Identifier upgrade = null;

    private final FullFixedItemInv inventory = new FullFixedItemInv(1) {
        @Override
        public boolean isItemValidForSlot(int slot, ItemStack item) {
            return this.getFilterForSlot(slot).matches(item);
        }

        @Override
        public ItemFilter getFilterForSlot(int slot) {
            return stack -> stack.getItem() == GalacticraftItem.ROCKET_SCHEMATIC;
        }
    };

    private ItemStack previous = ItemStack.EMPTY;

    public RocketDesignerBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.ROCKET_DESIGNER_TYPE, pos, state);

        this.inventory.addListener((view, slot, prev, cur) -> {
            if (!previous.getOrCreateTag().equals(inventory.getInvStack(0).getTag())) {
                this.updateSchematic();
            }
            previous = inventory.getInvStack(0).copy();
        }, () -> {});
    }

    public FullFixedItemInv getInventory() {
        return inventory;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);

        tag.putInt("red", red);
        tag.putInt("green", green);
        tag.putInt("blue", blue);
        tag.putInt("alpha", alpha);

        if (cone != null) tag.putString("cone", cone.toString());
        if (body != null) tag.putString("body", body.toString());
        if (fin != null) tag.putString("fin", fin.toString());
        if (booster != null) tag.putString("booster", booster.toString());
        if (bottom != null) tag.putString("bottom", bottom.toString());
        if (upgrade != null) tag.putString("upgrade", upgrade.toString());

        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);

        if (tag.contains("red")) red = tag.getInt("red");
        if (tag.contains("green")) green = tag.getInt("green");
        if (tag.contains("blue")) tag.getInt("blue");
        if (tag.contains("alpha")) alpha = tag.getInt("alpha");

        if (tag.contains("cone")) cone = new Identifier(tag.getString("cone"));
        if (tag.contains("body")) body = new Identifier(tag.getString("body"));
        if (tag.contains("fin")) fin = new Identifier(tag.getString("fin"));
        if (tag.contains("booster")) booster = new Identifier(tag.getString("booster"));
        if (tag.contains("bottom")) bottom = new Identifier(tag.getString("bottom"));
        if (tag.contains("upgrade")) upgrade = new Identifier(tag.getString("upgrade"));
    }

    @Override
    public void fromClientTag(NbtCompound nbtCompound) {
        this.readNbt(nbtCompound);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound nbtCompound) {
        return this.writeNbt(nbtCompound);
    }

    @Nullable
    public Identifier getPart(RocketPartType type) {
        return switch (type) {
            case BOOSTER -> booster;
            case BOTTOM -> bottom;
            case CONE -> cone;
            case BODY -> body;
            case FIN -> fin;
            case UPGRADE -> upgrade;
        };
    }

    public void setPart(Identifier part, RocketPartType type) {
        switch (type) {
            case BOOSTER -> booster = part;
            case BOTTOM -> bottom = part;
            case CONE -> cone = part;
            case BODY -> body = part;
            case FIN -> fin = part;
            case UPGRADE -> upgrade = part;
        }
    }

    @Environment(EnvType.CLIENT)
    public void setPartClient(Identifier part, RocketPartType type) {
        assert world.isClient;
        this.setPart(part, type);
        this.sendDesignerPartUpdate(part);
    }

    @Environment(EnvType.CLIENT)
    private void sendDesignerPartUpdate(Identifier part) {
        Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).sendPacket(new CustomPayloadC2SPacket(new Identifier(Constant.MOD_ID, "designer_part"), new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeIdentifier(part)));
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    @Environment(EnvType.CLIENT)
    public void setRedClient(int red) {
        Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).sendPacket(new CustomPayloadC2SPacket(new Identifier(Constant.MOD_ID, "designer_red"), new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeByte(red - 128))));
        this.red = red;
    }

    @Environment(EnvType.CLIENT)
    public void setGreenClient(int green) {
        Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).sendPacket(new CustomPayloadC2SPacket(new Identifier(Constant.MOD_ID, "designer_green"), new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeByte(green - 128))));
        this.green = green;
    }

    @Environment(EnvType.CLIENT)
    public void setBlueClient(int blue) {
        Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).sendPacket(new CustomPayloadC2SPacket(new Identifier(Constant.MOD_ID, "designer_blue"), new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeByte(blue - 128))));
        this.blue = blue;
    }

    @Environment(EnvType.CLIENT)
    public void setAlphaClient(int alpha) {
        Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).sendPacket(new CustomPayloadC2SPacket(new Identifier(Constant.MOD_ID, "designer_alpha"), new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeByte(alpha - 128))));
        this.alpha = alpha;
    }

    public void updateSchematic() {
        if (this.world != null && !this.world.isClient) {
            if (this.inventory.getInvStack(0).getItem() == GalacticraftItem.ROCKET_SCHEMATIC) {
                ItemStack stack = this.inventory.getInvStack(0).copy();
                NbtCompound tag = new NbtCompound();
                tag.putInt("red", red);
                tag.putInt("green", green);
                tag.putInt("blue", blue);
                tag.putInt("alpha", alpha);

                tag.putString("cone", cone.toString());
                tag.putString("body", body.toString());
                tag.putString("fin", fin.toString());
                tag.putString("booster", booster.toString());
                tag.putString("bottom", bottom.toString());
                tag.putString("upgrade", upgrade.toString());

                stack.setTag(tag);
                if (!this.inventory.getInvStack(0).getOrCreateTag().equals(tag)) this.inventory.setInvStack(0, stack, Simulation.ACTION);
            }
        }
    }
}
