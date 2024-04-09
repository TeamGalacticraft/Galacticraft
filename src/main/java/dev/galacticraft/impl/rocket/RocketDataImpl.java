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

package dev.galacticraft.impl.rocket;

import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.rocket.part.*;
import dev.galacticraft.mod.content.GCRocketParts;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

@ApiStatus.Internal
public record RocketDataImpl(int color, @Nullable ResourceKey<RocketCone<?, ?>> cone, @Nullable ResourceKey<RocketBody<?, ?>> body,
                             @Nullable ResourceKey<RocketFin<?, ?>> fin, @Nullable ResourceKey<RocketBooster<?, ?>> booster,
                             @Nullable ResourceKey<RocketEngine<?, ?>> engine, @Nullable ResourceKey<RocketUpgrade<?, ?>> upgrade) implements RocketData {

    public static final CompoundTag DEFAULT_ROCKET = Util.make(new CompoundTag(), tag -> {
        RocketData.create(0xFFFFFFFF,
                        GCRocketParts.TIER_1_CONE,
                        GCRocketParts.TIER_1_BODY,
                        GCRocketParts.TIER_1_FIN,
                        null,
                        GCRocketParts.TIER_1_ENGINE,
                        null)
                .toNbt(tag);
    });

    @Contract("_ -> new")
    public static @NotNull RocketDataImpl fromNbt(CompoundTag nbt) {
        return new RocketDataImpl(
                nbt.getInt("Color"),
                nbt.contains("Cone") ? ResourceKey.create(RocketRegistries.ROCKET_CONE, new ResourceLocation(nbt.getString("Cone"))) : null,
                nbt.contains("Body") ? ResourceKey.create(RocketRegistries.ROCKET_BODY, new ResourceLocation(nbt.getString("Body"))) : null,
                nbt.contains("Fin") ? ResourceKey.create(RocketRegistries.ROCKET_FIN, new ResourceLocation(nbt.getString("Fin"))) : null,
                nbt.contains("Booster") ? ResourceKey.create(RocketRegistries.ROCKET_BOOSTER, new ResourceLocation(nbt.getString("Booster"))) : null,
                nbt.contains("Engine") ? ResourceKey.create(RocketRegistries.ROCKET_ENGINE, new ResourceLocation(nbt.getString("Engine"))) : null,
                nbt.contains("Upgrade") ? ResourceKey.create(RocketRegistries.ROCKET_UPGRADE, new ResourceLocation(nbt.getString("Upgrade"))) : null
        );
    }

    @Contract("_ -> new")
    public static @NotNull RocketDataImpl fromNetwork(FriendlyByteBuf buf) {
        int color = buf.readVarInt();
        boolean hasCone = buf.readBoolean();
        boolean hasBody = buf.readBoolean();
        boolean hasFin = buf.readBoolean();
        boolean hasBooster = buf.readBoolean();
        boolean hasEngine = buf.readBoolean();
        boolean hasUpgrade = buf.readBoolean();
        return new RocketDataImpl(
                color,
                hasCone ? ResourceKey.create(RocketRegistries.ROCKET_CONE, buf.readResourceLocation()) : null,
                hasBody ? ResourceKey.create(RocketRegistries.ROCKET_BODY, buf.readResourceLocation()) : null,
                hasFin ? ResourceKey.create(RocketRegistries.ROCKET_FIN, buf.readResourceLocation()) : null,
                hasBooster ? ResourceKey.create(RocketRegistries.ROCKET_BOOSTER, buf.readResourceLocation()) : null,
                hasEngine ? ResourceKey.create(RocketRegistries.ROCKET_ENGINE, buf.readResourceLocation()) : null,
                hasUpgrade ? ResourceKey.create(RocketRegistries.ROCKET_UPGRADE, buf.readResourceLocation()) : null
        );
    }
}
