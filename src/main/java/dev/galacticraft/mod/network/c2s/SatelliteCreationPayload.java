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

import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.impl.network.c2s.C2SPayload;
import dev.galacticraft.impl.universe.celestialbody.type.SatelliteType;
import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SatelliteCreationPayload(Holder<CelestialBody<?, ?>> body) implements C2SPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, SatelliteCreationPayload> STREAM_CODEC = ByteBufCodecs.holderRegistry(AddonRegistries.CELESTIAL_BODY).map(SatelliteCreationPayload::new, p -> p.body);
    public static final ResourceLocation ID = Constant.id("create_satellite");
    public static final Type<SatelliteCreationPayload> TYPE = new Type<>(ID);

    @Override
    public void handle(ServerPlayNetworking.@NotNull Context context) {
        SatelliteType.registerSatellite(context.server(), context.player(), this.body, context.server().getStructureManager().get(Constant.Structure.SPACE_STATION).orElseThrow());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
