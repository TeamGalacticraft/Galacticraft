/*
 * Copyright (c) 2019-2025 Team Galacticraft
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

package dev.galacticraft.mod.network.c2s;

import dev.galacticraft.impl.network.c2s.C2SPayload;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.GCAccessorySlots;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.VarInt;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record CreativeGcTransferItemPayload(int containerType, int slotIndex, int action, ItemStack stack)
        implements C2SPayload {

    public static final ResourceLocation ID = Constant.id("gc_slot_click");

    public static final Type<CreativeGcTransferItemPayload> TYPE = new Type<>(ID);

    public static final StreamCodec<ByteBuf, CreativeGcTransferItemPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, p) -> {
                        VarInt.write(buf, p.containerType);
                        VarInt.write(buf, p.slotIndex);
                        VarInt.write(buf, p.action);
                        boolean has = !p.stack.isEmpty();
                        buf.writeBoolean(has);
                        if (has) {
                            ItemStack.STREAM_CODEC.encode((RegistryFriendlyByteBuf) buf, p.stack);
                        }
                    },
                    buf -> {
                        int ct = VarInt.read(buf);
                        int si = VarInt.read(buf);
                        int mode = VarInt.read(buf);
                        boolean has = buf.readBoolean();
                        ItemStack st = has
                                ? ItemStack.STREAM_CODEC.decode((RegistryFriendlyByteBuf) buf)
                                : ItemStack.EMPTY;
                        return new CreativeGcTransferItemPayload(ct, si, mode, st);
                    }
            );

    @Override
    public void handle(@NotNull ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if (!Galacticraft.CONFIG.enableCreativeGearInv()) {
                Constant.LOGGER.info("{} failed to execute packet. Galacticraft creative gear inventory is disabled.", this.getClass().getTypeName());
                return;
            }
            var player = context.player();
            if (!player.isCreative()) {
                return;
            }
            Container gcInv = player.galacticraft$getGearInv();
            Container playerInv = player.getInventory();
            if (containerType == 1) {
                if (action == 0) {
                    gcInv.setItem(slotIndex, ItemStack.EMPTY);
                } else if (action == 1) {
                    if (canPlaceItem(stack)) {
                        ItemStack previous = gcInv.getItem(slotIndex).copy();
                        gcInv.setItem(slotIndex, stack);
                        player.galacticraft$onEquipAccessory(previous, stack);
                    } else {
                        Constant.LOGGER.info("{} failed to place item to GC creative slot. Incorrect item.", this.getClass().getTypeName());
                    }
                }
            } else if (containerType == 0) {
                if (action == 0) {
                    playerInv.setItem(slotIndex, ItemStack.EMPTY);
                } else if (action == 1) {
                    playerInv.setItem(slotIndex, stack);
                }
            }
        });
    }

    private boolean canPlaceItem(ItemStack item) {
        for (int i = 0; i < 12; i++) {
            if (item.is(GCAccessorySlots.SLOT_TAGS.get(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
