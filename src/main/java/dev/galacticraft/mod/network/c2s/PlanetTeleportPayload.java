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
import dev.galacticraft.mod.Galacticraft;
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

import java.util.HashSet;
import java.util.Set;

public record PlanetTeleportPayload(ResourceLocation id) implements C2SPayload {
    public static final StreamCodec<ByteBuf, PlanetTeleportPayload> STREAM_CODEC =
            ResourceLocation.STREAM_CODEC.map(PlanetTeleportPayload::new, PlanetTeleportPayload::id);

    public static final ResourceLocation ID = Constant.id("planet_teleport");
    public static final Type<PlanetTeleportPayload> TYPE = new Type<>(ID);

    private static final Set<String> WARNED_INVALID_DISABLED_IDS = new HashSet<>();

    @Override
    public void handle(ServerPlayNetworking.@NotNull Context context) {
        if (!context.player().galacticraft$isCelestialScreenActive()) {
            context.player().connection.disconnect(Component.translatable(Translations.DimensionTp.INVALID_PACKET));
            return;
        }

        if (this.isDisabledForCelestialScreen()) {
            Constant.LOGGER.warn(
                    "Blocked celestial screen teleport to disabled destination {} from {}.",
                    this.id,
                    context.player().getScoreboardName()
            );
            return;
        }

        Registry<CelestialBody<?, ?>> celestialBodies = context.server()
                .registryAccess()
                .registryOrThrow(AddonRegistries.CELESTIAL_BODY);

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

    /**
     * Returns whether this destination has been disabled for the celestial selection screen by the server config.
     *
     * <p>This only blocks the normal celestial screen packet path. Admin commands and other direct teleport systems
     * can still teleport to the dimension/body separately.</p>
     *
     * @return {@code true} if this payload's destination id is listed in the celestial screen blacklist.
     */
    private boolean isDisabledForCelestialScreen() {
        for (String rawId : Galacticraft.CONFIG.disabledCelestialScreenDimensions()) {
            ResourceLocation disabledId = parseDisabledDestination(rawId);

            if (disabledId != null && disabledId.equals(this.id)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Parses a disabled celestial screen destination id from config.
     *
     * @param rawId The raw config entry, normally in {@code modid:dimension_id} / {@code modid:body_id} format.
     * @return The parsed id, or {@code null} if the entry is blank or invalid.
     */
    private static ResourceLocation parseDisabledDestination(String rawId) {
        if (rawId == null || rawId.isBlank()) {
            return null;
        }

        String trimmed = rawId.trim();

        try {
            return ResourceLocation.parse(trimmed);
        } catch (Exception e) {
            if (WARNED_INVALID_DISABLED_IDS.add(trimmed)) {
                Constant.LOGGER.warn(
                        "Ignoring invalid disabled celestial screen destination id '{}'. Expected format: modid:dimension_id",
                        trimmed
                );
            }

            return null;
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}