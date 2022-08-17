/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

import dev.galacticraft.api.rocket.part.RocketPartType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.item.GalacticraftItem;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class RocketDesignerBlockEntity extends BlockEntity/* implements BlockEntityClientSerializable*/ {

    public static final int SCHEMATIC_OUTPUT_SLOT = 0;

    private int red = 255;
    private int green = 255;
    private int blue = 255;
    private int alpha = 255;

    private ResourceLocation cone = null;
    private ResourceLocation body = null;
    private ResourceLocation fin = null;
    private ResourceLocation booster = null;
    private ResourceLocation bottom = null;
    private ResourceLocation upgrade = null;

//    private final FullFixedItemInv inventory = new FullFixedItemInv(1) {
//        @Override
//        public boolean isItemValidForSlot(int slot, ItemStack item) {
//            return this.getFilterForSlot(slot).matches(item);
//        }
//
//        @Override
//        public ItemFilter getFilterForSlot(int slot) {
//            return stack -> stack.getItem() == GalacticraftItem.ROCKET_SCHEMATIC;
//        }
//    };

    private ItemStack previous = ItemStack.EMPTY;

    public RocketDesignerBlockEntity(BlockPos pos, BlockState state) {
        super(GalacticraftBlockEntityType.ROCKET_DESIGNER_TYPE, pos, state);

//        this.inventory.addListener((view, slot, prev, cur) -> {
//            if (!previous.getOrCreateNbt().equals(inventory.getInvStack(0).getNbt())) {
//                this.updateSchematic();
//            }
//            previous = inventory.getInvStack(0).copy();
//        }, () -> {});
    }

//    public FullFixedItemInv getInventory() {
//        return inventory;
//    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

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
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains("red")) red = tag.getInt("red");
        if (tag.contains("green")) green = tag.getInt("green");
        if (tag.contains("blue")) tag.getInt("blue");
        if (tag.contains("alpha")) alpha = tag.getInt("alpha");

        if (tag.contains("cone")) cone = new ResourceLocation(tag.getString("cone"));
        if (tag.contains("body")) body = new ResourceLocation(tag.getString("body"));
        if (tag.contains("fin")) fin = new ResourceLocation(tag.getString("fin"));
        if (tag.contains("booster")) booster = new ResourceLocation(tag.getString("booster"));
        if (tag.contains("bottom")) bottom = new ResourceLocation(tag.getString("bottom"));
        if (tag.contains("upgrade")) upgrade = new ResourceLocation(tag.getString("upgrade"));
    }

//    @Override
//    public void fromClientTag(NbtCompound nbtCompound) {
//        this.readNbt(nbtCompound);
//    }
//
//    @Override
//    public NbtCompound toClientTag(NbtCompound nbtCompound) {
//        return this.writeNbt(nbtCompound);
//    }

    @Nullable
    public ResourceLocation getPart(RocketPartType type) {
        return switch (type) {
            case BOOSTER -> booster;
            case BOTTOM -> bottom;
            case CONE -> cone;
            case BODY -> body;
            case FIN -> fin;
            case UPGRADE -> upgrade;
        };
    }

    public void setPart(ResourceLocation part, RocketPartType type) {
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
    public void setPartClient(ResourceLocation part, RocketPartType type) {
        assert level.isClientSide;
        this.setPart(part, type);
        this.sendDesignerPartUpdate(part);
    }

    @Environment(EnvType.CLIENT)
    private void sendDesignerPartUpdate(ResourceLocation part) {
        ClientPlayNetworking.send(new ResourceLocation(Constant.MOD_ID, "designer_part"), PacketByteBufs.create().writeBlockPos(worldPosition).writeResourceLocation(part));
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
        ClientPlayNetworking.send(new ResourceLocation(Constant.MOD_ID, "designer_red"), new FriendlyByteBuf(PacketByteBufs.create().writeBlockPos(worldPosition).writeByte(red - 128)));
        this.red = red;
    }

    @Environment(EnvType.CLIENT)
    public void setGreenClient(int green) {
       ClientPlayNetworking.send(new ResourceLocation(Constant.MOD_ID, "designer_green"), new FriendlyByteBuf(PacketByteBufs.create().writeBlockPos(worldPosition).writeByte(green - 128)));
        this.green = green;
    }

    @Environment(EnvType.CLIENT)
    public void setBlueClient(int blue) {
        ClientPlayNetworking.send(new ResourceLocation(Constant.MOD_ID, "designer_blue"), new FriendlyByteBuf(PacketByteBufs.create().writeBlockPos(worldPosition).writeByte(blue - 128)));
        this.blue = blue;
    }

    @Environment(EnvType.CLIENT)
    public void setAlphaClient(int alpha) {
        ClientPlayNetworking.send(new ResourceLocation(Constant.MOD_ID, "designer_alpha"), new FriendlyByteBuf(PacketByteBufs.create().writeBlockPos(worldPosition).writeByte(alpha - 128)));
        this.alpha = alpha;
    }

    public void updateSchematic() {
        if (this.level != null && !this.level.isClientSide) {
//            if (this.inventory.getInvStack(0).getItem() == GalacticraftItem.ROCKET_SCHEMATIC) {
//                ItemStack stack = this.inventory.getInvStack(0).copy();
//                CompoundTag tag = new CompoundTag();
//                tag.putInt("red", red);
//                tag.putInt("green", green);
//                tag.putInt("blue", blue);
//                tag.putInt("alpha", alpha);
//
//                tag.putString("cone", cone.toString());
//                tag.putString("body", body.toString());
//                tag.putString("fin", fin.toString());
//                tag.putString("booster", booster.toString());
//                tag.putString("bottom", bottom.toString());
//                tag.putString("upgrade", upgrade.toString());
//
//                stack.setTag(tag);
//                if (!this.inventory.getInvStack(0).getOrCreateNbt().equals(tag)) this.inventory.setInvStack(0, stack, Simulation.ACTION);
//            }
        }
    }
}
