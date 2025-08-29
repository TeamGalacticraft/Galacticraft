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

package dev.galacticraft.impl.satellite;

import com.google.common.collect.ImmutableList;
import dev.galacticraft.api.satellite.SatelliteOwnershipData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SatelliteOwnershipDataImpl implements SatelliteOwnershipData {
    private final UUID owner;
    private final List<UUID> trusted;
    private String username;
    private final boolean open;

    public SatelliteOwnershipDataImpl(@NotNull UUID owner, String username, List<UUID> trusted, boolean open) {
        this.owner = owner;
        this.username = username;
        this.open = open;
        this.trusted = trusted;
    }

    public static @NotNull SatelliteOwnershipDataImpl fromNbt(@NotNull CompoundTag nbt) {
        long[] trusted = nbt.getLongArray("trusted");
        SatelliteOwnershipDataImpl data = new SatelliteOwnershipDataImpl(nbt.getUUID("owner"), nbt.getString("username"), new ArrayList<>(trusted.length / 2), nbt.getBoolean("open"));
        for (int i = 0; i < trusted.length; i += 2) {
            data.trusted.add(new UUID(trusted[i], trusted[i + 1]));
        }
        return data;
    }

    public static @NotNull SatelliteOwnershipDataImpl fromPacket(@NotNull FriendlyByteBuf buf) {
        int size = buf.readInt();
        SatelliteOwnershipDataImpl data = new SatelliteOwnershipDataImpl(buf.readUUID(), buf.readUtf(), new ArrayList<>(size), buf.readBoolean());
        for (int i = 0; i < size; i++) {
            data.trusted.add(buf.readUUID());
        }
        return data;
    }

    @Override
    public void username(String username) {
        this.username = username;
    }

    @Override
    public @NotNull String username() {
        return username;
    }

    @Override
    public @NotNull UUID owner() {
        return owner;
    }

    @Override
    public boolean open() {
        return this.open;
    }

    @Override
    public @NotNull @Unmodifiable List<UUID> trusted() {
        return ImmutableList.copyOf(trusted);
    }

    @Override
    public void trust(UUID uuid) {
        this.trusted.add(uuid);
    }

    @Override
    public void distrust(UUID uuid) {
        this.trusted.remove(uuid);
    }

    @Override
    public boolean canAccess(@NotNull Player player) {
        return player.getUUID().equals(this.owner()) || trusted.contains(player.getUUID());
    }

    @Override
    public void writePacket(@NotNull FriendlyByteBuf buf) {
        buf.writeUUID(this.owner());
        buf.writeUtf(this.username());
        buf.writeBoolean(this.open());
        buf.writeInt(this.trusted.size());
        for (UUID uuid : this.trusted) {
            buf.writeUUID(uuid);
        }
    }

    @Override
    public @NotNull CompoundTag toNbt(@NotNull CompoundTag nbt) {
        nbt.putUUID("owner", owner());
        nbt.putString("username", username());
        nbt.putBoolean("open", open());
        long[] trusted = new long[this.trusted.size() * 2];
        List<UUID> uuids = this.trusted;
        for (int i = 0, uuidsSize = uuids.size(); i < uuidsSize; i++) {
            UUID uuid = uuids.get(i);
            trusted[i * 2] = uuid.getMostSignificantBits();
            trusted[(i * 2) + 1] = uuid.getLeastSignificantBits();
        }
        nbt.putLongArray("trusted", trusted);
        return nbt;
    }
}
