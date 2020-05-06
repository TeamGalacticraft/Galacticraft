package com.hrznstudio.galacticraft.client.network.packet;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.blocks.machines.bubbledistributor.BubbleDistributorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.networking.ClientSidePacketRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class GalacticraftClientPackets {
    public static void register() {
        ClientSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "entity_spawn"), (packetContext, packetByteBuf) -> {
            PacketByteBuf buf = new PacketByteBuf(packetByteBuf.copy());
            MinecraftClient.getInstance().execute(() -> {
                int id = buf.readVarInt();
                UUID uuid = buf.readUuid();
                Entity entity = Registry.ENTITY_TYPE.get(buf.readVarInt()).create(MinecraftClient.getInstance().world);
                entity.setEntityId(id);
                entity.setUuid(uuid);
                entity.setPos(buf.readDouble(), buf.readDouble(), buf.readDouble());
                entity.yaw = (float) (buf.readByte() * 360) / 256.0F;
                entity.pitch = (float) (buf.readByte() * 360) / 256.0F;
                entity.setVelocity(buf.readShort(), buf.readShort(), buf.readShort());
                MinecraftClient.getInstance().world.addEntity(id, entity);
            });
        });


        ClientSidePacketRegistryImpl.INSTANCE.register(new Identifier(Constants.MOD_ID, "bubble_size"), (packetContext, packetByteBuf) -> {
            PacketByteBuf buf = new PacketByteBuf(packetByteBuf.copy());
            MinecraftClient.getInstance().execute(() -> {
                BlockPos pos = buf.readBlockPos();
                if (packetContext.getPlayer().world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                    if (packetContext.getPlayer().world.getBlockEntity(pos) instanceof BubbleDistributorBlockEntity) {
                        ((BubbleDistributorBlockEntity) packetContext.getPlayer().world.getBlockEntity(pos)).setSize(buf.readDouble());
                    }
                }
            });
        });
    }
}
