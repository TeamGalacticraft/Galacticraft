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
import dev.galacticraft.mod.content.ProximityAccess;
import dev.galacticraft.mod.content.block.entity.AirlockControllerBlockEntity;
import dev.galacticraft.mod.screen.AirlockControllerMenu;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record AirlockSetProximityAccessPayload(ProximityAccess access) implements C2SPayload {
    public static final StreamCodec<ByteBuf, AirlockSetProximityAccessPayload> STREAM_CODEC =
            ByteBufCodecs.VAR_INT.map(
                    i -> {
                        ProximityAccess[] vals = ProximityAccess.values();
                        ProximityAccess a = (i >= 0 && i < vals.length) ? vals[i] : ProximityAccess.PUBLIC;
                        return new AirlockSetProximityAccessPayload(a);
                    },
                    p -> p.access.ordinal()
            );

    public static final ResourceLocation ID = Constant.id("airlock_set_proximity_access");
    public static final CustomPacketPayload.Type<AirlockSetProximityAccessPayload> TYPE = new CustomPacketPayload.Type<>(ID);

    @Override
    public void handle(ServerPlayNetworking.@NotNull Context context) {
        if (context.player().containerMenu instanceof AirlockControllerMenu menu) {
            AirlockControllerBlockEntity be = menu.be;
            if (be != null && be.getLevel() != null && be.getLevel().isLoaded(be.getBlockPos())) {
                if (!(be instanceof dev.galacticraft.machinelib.api.block.entity.MachineBlockEntity mb)
                        || mb.getSecurity().hasAccess(context.player())) {
                    be.setProximityAccess(this.access);
                    be.setChanged();
                }
            }
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}