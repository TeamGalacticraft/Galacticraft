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
import dev.galacticraft.api.satellite.SatelliteRecipe;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.satellite.Orbitable;
import dev.galacticraft.impl.network.c2s.C2SPayload;
import dev.galacticraft.impl.universe.celestialbody.type.SatelliteType;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.content.advancements.GCTriggers;
import dev.galacticraft.mod.util.StreamCodecs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SatelliteCreationPayload(ResourceKey<CelestialBody<?, ?>> body) implements C2SPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, SatelliteCreationPayload> STREAM_CODEC =
            StreamCodecs.ofResourceKey(AddonRegistries.CELESTIAL_BODY)
                    .map(SatelliteCreationPayload::new, SatelliteCreationPayload::body);

    public static final ResourceLocation ID = Constant.id("create_satellite");
    public static final Type<SatelliteCreationPayload> TYPE = new Type<>(ID);

    @Override
    public void handle(ServerPlayNetworking.@NotNull Context context) {
        if (!Galacticraft.CONFIG.enableSpaceStationCreation()) {
            Constant.LOGGER.warn(
                    "Blocked space station creation from {} because space station creation is disabled in the server config.",
                    context.player().getScoreboardName()
            );
            return;
        }

        try {
            Registry<CelestialBody<?, ?>> celestialBodies = context.server()
                    .registryAccess()
                    .registryOrThrow(AddonRegistries.CELESTIAL_BODY);

            CelestialBody parentBody = celestialBodies.getOrThrow(body);

            if (!(parentBody.type() instanceof Orbitable orbitable)) {
                Constant.LOGGER.warn(
                        "Blocked invalid satellite creation request from {} for non-orbitable body {}.",
                        context.player().getScoreboardName(),
                        body.location()
                );
                return;
            }

            if (!context.player().hasInfiniteMaterials()) {
                SatelliteRecipe recipe = orbitable.satelliteRecipe(parentBody.config());

                if (recipe == null) {
                    Constant.LOGGER.warn(
                            "Blocked satellite creation request from {} because {} has no satellite recipe.",
                            context.player().getScoreboardName(),
                            body.location()
                    );
                    return;
                }

                if (!recipe.handle(context.player().getInventory())) {
                    Constant.LOGGER.error(
                            "Unable to remove the required ingredients for the satellite recipe from player {}.",
                            context.player().getScoreboardName()
                    );
                    return;
                }
            }

            SatelliteType.registerSatellite(
                    context.server(),
                    context.player(),
                    this.body,
                    context.server().getStructureManager().get(Constant.Structure.SPACE_STATION).orElseThrow(),
                    celestialBodies
            );

            GCTriggers.CREATE_SPACE_STATION.trigger(context.player());
        } catch (Exception e) {
            Constant.LOGGER.error(
                    "Failed to create space station for player {}.",
                    context.player().getScoreboardName(),
                    e
            );
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
