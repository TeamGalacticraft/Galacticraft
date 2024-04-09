/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.network.packets;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.misc.footprint.Footprint;
import dev.galacticraft.mod.misc.footprint.FootprintManager;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public record FootprintRemovedPacket(long packedPos, BlockPos pos) implements GCPacket {
    public static final PacketType<FootprintRemovedPacket> TYPE = PacketType.create(Constant.Packet.FOOTPRINT_REMOVED, FootprintRemovedPacket::new);

    public FootprintRemovedPacket(FriendlyByteBuf buf) {
        this(buf.readLong(), buf.readBlockPos());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeLong(packedPos);
        buf.writeBlockPos(pos);
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        long packedPos = packedPos();
        BlockPos pos = pos();
        FootprintManager manager = player.level().galacticraft$getFootprintManager();
        List<Footprint> footprintList = manager.getFootprints().get(packedPos);
        List<Footprint> toRemove = new ArrayList<>();

        if (footprintList != null) {
            for (Footprint footprint : footprintList) {
                if (footprint.position.x > pos.getX() && footprint.position.x < pos.getX() + 1 && footprint.position.z > pos.getZ() && footprint.position.z < pos.getZ() + 1) {
                    toRemove.add(footprint);
                }
            }
        }

        if (!toRemove.isEmpty()) {
            footprintList.removeAll(toRemove);
            manager.getFootprints().put(packedPos, footprintList);
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
