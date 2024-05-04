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

import dev.galacticraft.api.rocket.part.RocketPart;
import dev.galacticraft.api.rocket.part.RocketPartTypes;
import dev.galacticraft.mod.Constant.Packet;
import dev.galacticraft.mod.screen.RocketWorkbenchMenu;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class SelectPartPacket implements GCPacket {
    public static final PacketType<SelectPartPacket> TYPE = PacketType.create(Packet.SELECT_PART, SelectPartPacket::new);

    private final RocketPartTypes type;
    @Nullable
    private final ResourceKey<RocketPart<?, ?>> key;

    public SelectPartPacket(RocketPartTypes type, @Nullable ResourceKey<? extends RocketPart<?, ?>> key) {
        this.type = type;
        this.key = (ResourceKey<RocketPart<?, ?>>) key;
    }

    public SelectPartPacket(FriendlyByteBuf buf) {
        this.type = buf.readEnum(RocketPartTypes.class);
        this.key = buf.readBoolean() ? ResourceKey.create(((ResourceKey<? extends Registry<RocketPart<?, ?>>>) type.key), buf.readResourceLocation()) : null;
    }

    @Override
    public void handle(Player player, PacketSender responseSender) {
        if (player.containerMenu instanceof RocketWorkbenchMenu menu) {
            menu.getSelection(type).setSelection(key == null ? null : key.location());
            menu.workbench.setChanged();
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(type);
        if (key != null) {
            buf.writeBoolean(true);
            buf.writeResourceLocation(key.location());
        } else {
            buf.writeBoolean(false);
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
