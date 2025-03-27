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

import dev.galacticraft.impl.network.c2s.C2SPayload;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.entity.ControllableEntity;
import dev.galacticraft.mod.util.StreamCodecs;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ControlEntityPayload(float leftImpulse, float forwardImpulse, boolean up, boolean down, boolean left,
                                   boolean right, boolean jumping, boolean shiftKeyDown) implements C2SPayload {
    public static final StreamCodec<ByteBuf, ControlEntityPayload> STREAM_CODEC = StreamCodecs.composite(
            ByteBufCodecs.FLOAT,
            p -> p.leftImpulse,
            ByteBufCodecs.FLOAT,
            p -> p.forwardImpulse,
            ByteBufCodecs.BOOL,
            p -> p.up,
            ByteBufCodecs.BOOL,
            p -> p.down,
            ByteBufCodecs.BOOL,
            p -> p.left,
            ByteBufCodecs.BOOL,
            p -> p.right,
            ByteBufCodecs.BOOL,
            p -> p.jumping,
            ByteBufCodecs.BOOL,
            p -> p.shiftKeyDown,
            ControlEntityPayload::new
    );
    public static final ResourceLocation ID = Constant.id("control_entity");
    public static final CustomPacketPayload.Type<ControlEntityPayload> TYPE = new CustomPacketPayload.Type<>(ID);

    @Override
    public void handle(ServerPlayNetworking.@NotNull Context context) {
        if (context.player().isPassenger() && context.player().getVehicle() instanceof ControllableEntity controllable) {
            controllable.inputTick(leftImpulse(), forwardImpulse(), up(), down(), left(), right(), jumping(), shiftKeyDown());
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
