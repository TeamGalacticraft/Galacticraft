package dev.galacticraft.mod.network.c2s;

import dev.galacticraft.impl.network.c2s.C2SPayload;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.GCAccessorySlots;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.VarInt;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public record CreativeGcTransferItemPayload(int containerType, int slotIndex, int action, ItemStack stack)
        implements C2SPayload {

    private static final Logger LOG = LoggerFactory.getLogger(CreativeGcTransferItemPayload.class);

    public static final ResourceLocation ID =
            Constant.id("gc_slot_click");

    public static final Type<CreativeGcTransferItemPayload> TYPE =
            new Type<>(ID);

    public static final StreamCodec<ByteBuf, CreativeGcTransferItemPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, p) -> {
                        VarInt.write(buf, p.containerType);
                        VarInt.write(buf, p.slotIndex);
                        VarInt.write(buf, p.action);
                        boolean has = !p.stack.isEmpty();
                        buf.writeBoolean(has);
                        if(has)
                        {
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

            if(!Galacticraft.CONFIG.enableCreativeGearInv())
            {
                Constant.LOGGER.info("{} failed to execute packet. Galacticraft creative gear inventory is disabled.", this.getClass().getTypeName());
                return;
            }
            var player = context.player();
            if(!player.isCreative()) return;
            var menu = player.inventoryMenu;
            ItemStack carried = menu.getCarried();

            Container inv = player.galacticraft$getGearInv();
            Container plinv = player.getInventory();
            if(containerType == 1)
            {
                if(action == 0)
                {
                    inv.setItem(slotIndex, ItemStack.EMPTY);
                }
                else if (action == 1) {

                    if(canPlaceItem(stack))
                    {
                        inv.setItem(slotIndex, stack);
                    }
                    else
                    {
                        Constant.LOGGER.info("{} failed to place item to GC creative slot. Incorrect item.", this.getClass().getTypeName());
                    }
                }
            }
            else if(containerType == 0)
            {
                if(action == 0)
                {
                    plinv.setItem(slotIndex, ItemStack.EMPTY);
                }
                else if (action == 1) {
                    plinv.setItem(slotIndex, stack);
                }
            }



        });
    }

    private boolean canPlaceItem(ItemStack item)
    {
        boolean result = false;
        for(int i = 0; i < 12; i++)
        {
            if(item.is(GCAccessorySlots.SLOT_TAGS.get(i)))
            {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
