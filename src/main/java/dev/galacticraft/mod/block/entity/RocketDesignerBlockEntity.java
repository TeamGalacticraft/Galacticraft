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
import com.google.common.collect.Lists;
import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.api.rocket.part.RocketPartType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.item.GalacticraftItem;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

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

    private RocketPart cone = null;
    private RocketPart body = null;
    private RocketPart fin = null;
    private RocketPart booster = null;
    private RocketPart bottom = null;
    private RocketPart upgrade = null;

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

    public RocketDesignerBlockEntity() {
        super(GalacticraftBlockEntityType.ROCKET_DESIGNER_TYPE);

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
    public NbtCompound toTag(NbtCompound tag) {
        super.toTag(tag);

        tag.putInt("red", red);
        tag.putInt("green", green);
        tag.putInt("blue", blue);
        tag.putInt("alpha", alpha);

        if (cone != null) tag.putString("cone", Objects.requireNonNull(RocketPart.getId(this.world.getRegistryManager(), cone)).toString());
        if (body != null) tag.putString("body", Objects.requireNonNull(RocketPart.getId(this.world.getRegistryManager(), body)).toString());
        if (fin != null) tag.putString("fin", Objects.requireNonNull(RocketPart.getId(this.world.getRegistryManager(), fin)).toString());
        if (booster != null) tag.putString("booster", Objects.requireNonNull(RocketPart.getId(this.world.getRegistryManager(), booster)).toString());
        if (bottom != null) tag.putString("bottom", Objects.requireNonNull(RocketPart.getId(this.world.getRegistryManager(), bottom)).toString());
        if (upgrade != null) tag.putString("upgrade", Objects.requireNonNull(RocketPart.getId(this.world.getRegistryManager(), upgrade)).toString());

        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);

        if (tag.contains("red")) red = tag.getInt("red");
        if (tag.contains("green")) green = tag.getInt("green");
        if (tag.contains("blue")) tag.getInt("blue");
        if (tag.contains("alpha")) alpha = tag.getInt("alpha");

        if (tag.contains("cone")) cone = RocketPart.getById(this.world.getRegistryManager(), new Identifier(tag.getString("cone")));
        if (tag.contains("body")) body = RocketPart.getById(this.world.getRegistryManager(), new Identifier(tag.getString("body")));
        if (tag.contains("fin")) fin = RocketPart.getById(this.world.getRegistryManager(), new Identifier(tag.getString("fin")));
        if (tag.contains("booster")) booster = RocketPart.getById(this.world.getRegistryManager(), new Identifier(tag.getString("booster")));
        if (tag.contains("bottom")) bottom = RocketPart.getById(this.world.getRegistryManager(), new Identifier(tag.getString("bottom")));
        if (tag.contains("upgrade")) upgrade = RocketPart.getById(this.world.getRegistryManager(), new Identifier(tag.getString("upgrade")));
    }

    @Override
    public void fromClientTag(NbtCompound nbtCompound) {
        this.fromTag(GalacticraftBlock.ROCKET_DESIGNER.getDefaultState(), nbtCompound);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound nbtCompound) {
        return this.toTag(nbtCompound);
    }

    @Nullable
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

    public void setPartServer(RocketPart part) {
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
    }

    @Environment(EnvType.CLIENT)
    public void setPartClient(RocketPart part) {
        assert world.isClient;
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

        sendDesignerPartUpdate(part);
    }

    @Environment(EnvType.CLIENT)
    private void sendDesignerPartUpdate(RocketPart part) {
        Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).sendPacket(new CustomPayloadC2SPacket(new Identifier(Constant.MOD_ID, "designer_part"), new PacketByteBuf(Unpooled.buffer()).writeBlockPos(pos).writeIdentifier(Objects.requireNonNull(RocketPart.getId(this.world.getRegistryManager(), part)))));
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

                tag.putString("cone", Objects.requireNonNull(RocketPart.getId(this.world.getRegistryManager(), cone)).toString());
                tag.putString("body", Objects.requireNonNull(RocketPart.getId(this.world.getRegistryManager(), body)).toString());
                tag.putString("fin", Objects.requireNonNull(RocketPart.getId(this.world.getRegistryManager(), fin)).toString());
                tag.putString("booster", Objects.requireNonNull(RocketPart.getId(this.world.getRegistryManager(), booster)).toString());
                tag.putString("bottom", Objects.requireNonNull(RocketPart.getId(this.world.getRegistryManager(), bottom)).toString());
                tag.putString("upgrade", Objects.requireNonNull(RocketPart.getId(this.world.getRegistryManager(), upgrade)).toString());

//                int tier = 0;
//                for (RocketPart part : getParts()) {
//                    tier = Math.max(part.getTier(getParts()), tier);
//                }
//
//                tag.putInt("tier", tier);

                stack.setTag(tag);
                if (!this.inventory.getInvStack(0).getOrCreateTag().equals(tag)) this.inventory.setInvStack(0, stack, Simulation.ACTION);
            }
        }
    }
}
