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

package dev.galacticraft.mod.network.s2c;

import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.rocket.RocketData;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.impl.network.s2c.S2CPayload;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.gui.screen.ingame.CelestialSelectionScreen;
import dev.galacticraft.mod.util.StreamCodecs;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record OpenCelestialScreenPayload(@Nullable RocketData data, Holder<CelestialBody<?, ?>> celestialBody) implements S2CPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenCelestialScreenPayload> STREAM_CODEC = StreamCodec.composite(
            StreamCodecs.ofNullable(RocketData.STREAM_CODEC),
            p -> p.data,
            ByteBufCodecs.holderRegistry(AddonRegistries.CELESTIAL_BODY),
            p -> p.celestialBody,
            OpenCelestialScreenPayload::new
    );

    public static final ResourceLocation ID = Constant.id("open_celestial_screen");
    public static final Type<OpenCelestialScreenPayload> TYPE = new Type<>(ID);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public Runnable handle(ClientPlayNetworking.@NotNull Context context) {
        return new Runnable() {
            @Override
            public void run() {
                context.client().setScreen(new CelestialSelectionScreen(false, OpenCelestialScreenPayload.this.data(), true, OpenCelestialScreenPayload.this.celestialBody.value()));
            }
        };
    }
}
