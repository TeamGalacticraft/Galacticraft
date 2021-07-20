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

package dev.galacticraft.mod.client.network;

import com.mojang.authlib.GameProfile;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.accessor.ChunkOxygenAccessor;
import dev.galacticraft.mod.accessor.GearInventoryProvider;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.api.machine.RedstoneInteractionType;
import dev.galacticraft.mod.api.machine.SecurityInfo;
import dev.galacticraft.mod.block.entity.BubbleDistributorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.Objects;
import java.util.UUID;

/**
 * Handles client-bound (S2C) packets
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Environment(EnvType.CLIENT)
public class GalacticraftClientPacketReceiver {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "security_update"), (client, handler, buf, responseSender) -> { //todo(marcus): 1.17?
            BlockPos pos = buf.readBlockPos();
            SecurityInfo.Accessibility accessibility = SecurityInfo.Accessibility.values()[buf.readByte()];
            GameProfile profile = NbtHelper.toGameProfile(Objects.requireNonNull(buf.readNbt()));

            client.execute(() -> {
                assert client.world != null;
                BlockEntity entity = client.world.getBlockEntity(pos);
                if (entity instanceof MachineBlockEntity machine) {
                    assert profile != null;
                    assert accessibility != null;
                    machine.getConfiguration().getSecurity().setOwner(/*((ClientWorldTeamsGetter) client.world).getSpaceRaceTeams(), */profile); //todo teams
                    machine.getConfiguration().getSecurity().setAccessibility(accessibility);

                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "redstone_update"), (client, handler, buf, responseSender) -> { //todo(marcus): 1.17?
            BlockPos pos = buf.readBlockPos();
            RedstoneInteractionType redstone = RedstoneInteractionType.values()[buf.readByte()];

            client.execute(() -> {
                assert client.world != null;
                BlockEntity entity = client.world.getBlockEntity(pos);
                if (entity instanceof MachineBlockEntity) {
                    assert redstone != null;
                    ((MachineBlockEntity) entity).getConfiguration().setRedstone(redstone);
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "entity_spawn"), (client, handler, buf, responseSender) -> { //todo(marcus): 1.17?
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            client.execute(() -> {
                int id = buffer.readVarInt();
                UUID uuid = buffer.readUuid();
                Entity entity = Registry.ENTITY_TYPE.get(buffer.readVarInt()).create(MinecraftClient.getInstance().world);
                entity.setId(id);
                entity.setUuid(uuid);
                entity.setPos(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
                entity.setYaw((float) (buffer.readByte() * 360) / 256.0F);
                entity.setPitch((float) (buffer.readByte() * 360) / 256.0F);
                entity.setVelocity(buffer.readShort(), buffer.readShort(), buffer.readShort());
                MinecraftClient.getInstance().world.addEntity(id, entity);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "bubble_size"), (client, handler, buf, responseSender) -> {
            PacketByteBuf buffer = new PacketByteBuf(buf.copy());
            client.execute(() -> {
                BlockPos pos = buffer.readBlockPos();
                if (client.world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
                    BlockEntity entity = client.world.getBlockEntity(pos);
                    if (entity instanceof BubbleDistributorBlockEntity machine) {
                        machine.setSize(buffer.readDouble());
                    }
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "oxygen_update"), (client, handler, buf, responseSender) -> {
            byte b = buf.readByte();
            ChunkOxygenAccessor accessor = ((ChunkOxygenAccessor) handler.getWorld().getChunk(buf.readInt(), buf.readInt()));
            accessor.readOxygenUpdate(b, buf);
        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "open_screen"), (client, handler, buf, responseSender) -> {

        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "gear_inv_sync"), (client, handler, buf, responseSender) -> {
            int entity = buf.readInt();
            int index = buf.readByte();
            ItemStack stack = buf.readItemStack();
            client.execute(() -> ((GearInventoryProvider) client.world.getEntityById(entity)).getGearInv().forceSetInvStack(index, stack));
        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier(Constant.MOD_ID, "gear_inv_sync_full"), (client, handler, buf, responseSender) -> {
            int entity = buf.readInt();
            NbtCompound tag = buf.readNbt();
            client.execute(() -> ((GearInventoryProvider) client.world.getEntityById(entity)).readGearFromNbt(tag));
        });
    }
}
