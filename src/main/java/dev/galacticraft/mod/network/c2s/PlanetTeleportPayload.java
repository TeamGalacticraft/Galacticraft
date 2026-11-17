/*
 * Copyright (c) 2019-2026 Team Galacticraft
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
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.config.GCConfigUtil;
import dev.galacticraft.mod.events.GCEventHandlers;
import dev.galacticraft.mod.util.Translations;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;


public record PlanetTeleportPayload(ResourceLocation id) implements C2SPayload {
    public static final StreamCodec<ByteBuf, PlanetTeleportPayload> STREAM_CODEC =
            ResourceLocation.STREAM_CODEC.map(PlanetTeleportPayload::new, PlanetTeleportPayload::id);

    public static final ResourceLocation ID = Constant.id("planet_teleport");
    public static final Type<PlanetTeleportPayload> TYPE = new Type<>(ID);

    @Override
    public void handle(ServerPlayNetworking.@NotNull Context context) {
        if (!context.player().galacticraft$isCelestialScreenActive()) {
            context.player().connection.disconnect(Component.translatable(Translations.DimensionTp.INVALID_PACKET));
            return;
        }

        if (GCConfigUtil.disabledCelestialScreenDestinations(context.server()).contains(this.id)) {
            Constant.LOGGER.warn(
                    "Blocked celestial screen teleport to disabled destination {} from {}.",
                    this.id,
                    context.player().getScoreboardName()
            );

            context.player().galacticraft$closeCelestialScreen();
            return;
        }

        Registry<CelestialBody<?, ?>> celestialBodies = context.server().registryAccess().registryOrThrow(AddonRegistries.CELESTIAL_BODY);

        CelestialBody<?, ?> destination = celestialBodies.get(this.id);

        if (destination == null) {
            Constant.LOGGER.warn(
                    "Blocked celestial screen teleport from {} because destination {} does not exist.",
                    context.player().getScoreboardName(),
                    this.id
            );
            context.player().connection.disconnect(Component.translatable(Translations.DimensionTp.INVALID_PACKET));
            return;
        }

        Holder<CelestialBody<?, ?>> holder = context.player().level().galacticraft$getCelestialBody();
        CelestialBody<?, ?> fromBody = holder == null ? null : holder.value();

        GCEventHandlers.onPlayerChangePlanets(
                context.server(),
                context.player(),
                destination,
                fromBody
        );
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}