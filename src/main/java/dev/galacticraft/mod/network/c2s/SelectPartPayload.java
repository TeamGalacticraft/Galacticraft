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

import dev.galacticraft.api.rocket.part.RocketPartTypes;
import dev.galacticraft.impl.network.c2s.C2SPayload;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.screen.RocketWorkbenchMenu;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record SelectPartPayload(RocketPartTypes partType, @Nullable ResourceLocation part) implements C2SPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, SelectPartPayload> STREAM_CODEC = StreamCodec.ofMember(
            SelectPartPayload::write,
            SelectPartPayload::new
    );

    public static final ResourceLocation ID = Constant.id("select_part");
    public static final Type<SelectPartPayload> TYPE = new Type<>(ID);

    public SelectPartPayload(FriendlyByteBuf buf) {
        this(buf.readEnum(RocketPartTypes.class), buf.readBoolean() ? buf.readResourceLocation() : null);
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeEnum(this.partType);
        if (this.part != null) {
            buf.writeBoolean(true);
            buf.writeResourceLocation(this.part);
        } else {
            buf.writeBoolean(false);
        }
    }

    @Override
    public void handle(ServerPlayNetworking.@NotNull Context context) {
        if (context.player().containerMenu instanceof RocketWorkbenchMenu menu) {
            menu.getSelection(this.partType).setSelection(this.part);
            menu.workbench.setChanged();
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
