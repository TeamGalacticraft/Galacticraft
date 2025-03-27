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

package dev.galacticraft.api.satellite;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.impl.satellite.SatelliteOwnershipDataImpl;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.UUID;

public interface SatelliteOwnershipData {
    Codec<SatelliteOwnershipData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("owner").xmap(UUID::fromString, UUID::toString).forGetter(SatelliteOwnershipData::owner),
            Codec.STRING.fieldOf("username").forGetter(SatelliteOwnershipData::username),
            Codec.STRING.xmap(UUID::fromString, UUID::toString).listOf().fieldOf("trusted").forGetter(SatelliteOwnershipData::trusted),
            Codec.BOOL.fieldOf("open").forGetter(SatelliteOwnershipData::open)
    ).apply(instance, SatelliteOwnershipData::create));

    @Contract(value = "_, _, _, _ -> new", pure = true)
    static @NotNull SatelliteOwnershipData create(@NotNull UUID owner, String username, List<UUID> trusted, boolean open) {
        return new SatelliteOwnershipDataImpl(owner, username, trusted, open);
    }

    static @NotNull SatelliteOwnershipData fromNbt(@NotNull CompoundTag nbt) {
        return SatelliteOwnershipDataImpl.fromNbt(nbt);
    }

    static @NotNull SatelliteOwnershipData fromPacket(@NotNull FriendlyByteBuf buf) {
        return SatelliteOwnershipDataImpl.fromPacket(buf);
    }

    void username(String username);

    @NotNull String username();

    @NotNull UUID owner();

    boolean open();

    @NotNull
    @Unmodifiable
    List<UUID> trusted();

    void trust(UUID uuid);

    void distrust(UUID uuid);

    boolean canAccess(@NotNull Player player);

    void writePacket(@NotNull FriendlyByteBuf buf);

    @NotNull CompoundTag toNbt(@NotNull CompoundTag nbt);
}
