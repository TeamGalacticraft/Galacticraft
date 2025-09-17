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

import dev.galacticraft.impl.network.s2c.S2CPayload;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.trance.ClientTranceState;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record HallucinationStatePacket(boolean active) implements S2CPayload {
    public static final ResourceLocation ID = Constant.id("hallucination_state");
    public static final Type<HallucinationStatePacket> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, HallucinationStatePacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, msg) -> buf.writeBoolean(msg.active),
                    buf -> new HallucinationStatePacket(buf.readBoolean())
            );

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

    /** Client handler registration */
    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(TYPE, (packet, ctx) -> {

        });
    }

    @Override
    public Runnable handle(ClientPlayNetworking.@NotNull Context context) {
        return () -> {
            ClientTranceState.setHallucinating(this.active());
        };
    }
}