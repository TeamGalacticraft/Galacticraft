/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.block.entity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.item.impl.FullFixedItemInv;
import com.google.common.collect.Lists;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.rocket.RocketPart;
import com.hrznstudio.galacticraft.api.rocket.RocketPartType;
import com.hrznstudio.galacticraft.api.rocket.RocketParts;
import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

import java.util.List;
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

    private RocketPart cone = RocketParts.DEFAULT_CONE;
    private RocketPart body = RocketParts.DEFAULT_BODY;
    private RocketPart fin = RocketParts.DEFAULT_FIN;
    private RocketPart booster = RocketParts.NO_BOOSTER;
    private RocketPart bottom = RocketParts.DEFAULT_BOTTOM;
    private RocketPart upgrade = RocketParts.NO_UPGRADE;

    private final FullFixedItemInv inventory = new FullFixedItemInv(1) {
        @Override
        public boolean isItemValidForSlot(int slot, ItemStack item) {
            return getFilterForSlot(slot).matches(item);
        }

        @Override
        public ItemFilter getFilterForSlot(int slot) {
            if (slot == 0) {
                return (itemStack -> itemStack.getItem() == GalacticraftItems.ROCKET_SCHEMATIC);
            } else {
                return (itemStack -> false);
            }
        }
    };

    public RocketDesignerBlockEntity() {
        super(GalacticraftBlockEntities.ROCKET_DESIGNER_TYPE);

        this.inventory.addListener((fixedItemInvView, i, itemStack, itemStack1) -> {
            if (itemStack.isEmpty()) {
                this.updateSchematic();
            } else if (!itemStack.getOrCreateTag().equals(itemStack1.getTag())) {
                this.updateSchematic();
            }
        }, () -> {
        });
    }

    public FullFixedItemInv getInventory() {
        return inventory;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        tag.putInt("red", red);
        tag.putInt("green", green);
        tag.putInt("blue", blue);
        tag.putInt("alpha", alpha);

        tag.putString("cone", Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(cone)).toString());
        tag.putString("body", Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(body)).toString());
        tag.putString("fin", Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(fin)).toString());
        tag.putString("booster", Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(booster)).toString());
        tag.putString("bottom", Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(bottom)).toString());
        tag.putString("upgrade", Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(upgrade)).toString());

        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);

        if (tag.contains("red") && tag.contains("cone")) {
            red = tag.getInt("red");
            green = tag.getInt("green");
            blue = tag.getInt("blue");
            alpha = tag.getInt("alpha");

            cone = Galacticraft.ROCKET_PARTS.get(new Identifier(tag.getString("cone")));
            body = Galacticraft.ROCKET_PARTS.get(new Identifier(tag.getString("body")));
            fin = Galacticraft.ROCKET_PARTS.get(new Identifier(tag.getString("fin")));
            booster = Galacticraft.ROCKET_PARTS.get(new Identifier(tag.getString("booster")));
            bottom = Galacticraft.ROCKET_PARTS.get(new Identifier(tag.getString("bottom")));
            upgrade = Galacticraft.ROCKET_PARTS.get(new Identifier(tag.getString("upgrade")));
        }
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        this.fromTag(GalacticraftBlocks.ROCKET_DESIGNER.getDefaultState(), compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return this.toTag(compoundTag);
    }

    public RocketPart getPart(RocketPartType type) {
        switch (type) {
            case BOOSTER:
                return booster;
            case BOTTOM:
                return bottom;
            case CONE:
                return cone;
            case BODY:
                return body;
            case FIN:
                return fin;
            case UPGRADE:
                return upgrade;
            default:
                return null;
        }
    }

    public void setPart(RocketPart part) {
        switch (part.getType()) {
            case BOOSTER:
                booster = part;
                break;
            case BOTTOM:
                bottom = part;
                break;
            case CONE:
                cone = part;
                break;
            case BODY:
                body = part;
                break;
            case FIN:
                fin = part;
                break;
            case UPGRADE:
                upgrade = part;
                break;
        }

        if (this.world != null && this.world.isClient && FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            sendDesignerPartUpdate(part);
        }
    }

    @Environment(EnvType.CLIENT)
    private void sendDesignerPartUpdate(RocketPart part) {
        Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "designer_part"), new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeIdentifier(Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(part)))));
    }


    public List<RocketPart> getParts() {
        return Lists.newArrayList(cone, body, booster, fin, bottom, upgrade);
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
        if (this.world != null && this.world.isClient && this.red != red) {
            Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "designer_red"), new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeByte(red - 128))));
        }
        this.red = red;
    }

    public void setGreen(int green) {
        if (this.world != null && this.world.isClient && this.green != green) {
            Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "designer_green"), new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeByte(green - 128))));
        }
        this.green = green;
    }

    public void setBlue(int blue) {
        if (this.world != null && this.world.isClient && this.blue != blue) {
            Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "designer_blue"), new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeByte(blue - 128))));
        }
        this.blue = blue;
    }

    public void setAlpha(int alpha) {
        if (this.world != null && this.world.isClient && this.alpha != alpha) {
            Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).sendPacket(new CustomPayloadC2SPacket(new Identifier(Constants.MOD_ID, "designer_alpha"), new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeByte(alpha - 128))));
        }
        this.alpha = alpha;
    }

    public void updateSchematic() {
        if (this.world != null && !this.world.isClient) {
            if (this.inventory.getStack(0).getItem() == GalacticraftItems.ROCKET_SCHEMATIC) {
                ItemStack stack = new ItemStack(GalacticraftItems.ROCKET_SCHEMATIC);
                CompoundTag tag = new CompoundTag();
                tag.putInt("red", red);
                tag.putInt("green", green);
                tag.putInt("blue", blue);
                tag.putInt("alpha", alpha);

                tag.putString("cone", Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(cone)).toString());
                tag.putString("body", Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(body)).toString());
                tag.putString("fin", Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(fin)).toString());
                tag.putString("booster", Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(booster)).toString());
                tag.putString("bottom", Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(bottom)).toString());
                tag.putString("upgrade", Objects.requireNonNull(Galacticraft.ROCKET_PARTS.getId(upgrade)).toString());

                int tier = 0;
                for (RocketPart part : getParts()) {
                    tier = Math.max(part.getTier(getParts()), tier);
                }

                tag.putInt("tier", tier);

                stack.setTag(tag);

                this.inventory.setStack(0, stack, Simulation.ACTION);
            }
        }
    }
}
