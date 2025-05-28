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

package dev.galacticraft.impl.network.s2c;

import dev.galacticraft.impl.client.accessor.ClientResearchAccessor;
import dev.galacticraft.mod.Constant;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ResearchUpdatePayload(boolean add, List<ResourceLocation> ids) implements S2CPayload {
    public static final ResourceLocation ID = Constant.id("research_update");
    public static final Type<ResearchUpdatePayload> TYPE = new Type<>(ID);
    public static final StreamCodec<ByteBuf, ResearchUpdatePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            p -> p.add,
            ByteBufCodecs.<ByteBuf, ResourceLocation>list().apply(ResourceLocation.STREAM_CODEC),
            p -> p.ids,
            ResearchUpdatePayload::new
    );

    @Override
    public Runnable handle(ClientPlayNetworking.@NotNull Context context) {
        return () -> ((ClientResearchAccessor) context.player()).galacticraft$updateResearch(this.add, this.ids);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
