/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.impl.internal.fabric;

import dev.galacticraft.api.accessor.SatelliteAccessor;
import dev.galacticraft.api.entity.attribute.GcApiEntityAttributes;
import dev.galacticraft.api.gas.Gases;
import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.registry.BuiltInRocketRegistries;
import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.api.rocket.part.*;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.CelestialTeleporter;
import dev.galacticraft.api.universe.galaxy.Galaxy;
import dev.galacticraft.dynamicdimensions.api.event.DynamicDimensionLoadCallback;
import dev.galacticraft.impl.internal.command.GCApiCommands;
import dev.galacticraft.impl.universe.BuiltinObjects;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.data.gen.SatelliteChunkGenerator;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.SimpleContainer;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class GalacticraftAPI implements ModInitializer {
    public static final SimpleContainer EMPTY_INV = new SimpleContainer(0);

    @Override
    public void onInitialize() {
        long startInitTime = System.currentTimeMillis();
        GCApiCommands.register();
        ServerPlayNetworking.registerGlobalReceiver(Constant.id("flag_data"), (server, player, handler, buf, responseSender) -> {
            int[] array = buf.readVarIntArray();
            for (int i = 0; i < array.length; i++) {
                array[i] &= 0x00FFFFFF;
            }
            // FORMAT: [A - IGNORE]BGR - 48 width 32 height if it is not a 1536 int array then ignore
            // since it is purely colour data, there isn't really much a malicious client could do
            server.execute(() -> {
                //todo: teams
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(Constant.id("team_name"), (server, player, handler, buf, responseSender) -> {
            String s = buf.readUtf();

            server.execute(() -> {
                //todo: teams
            });
        });

        DynamicRegistries.register(AddonRegistries.CELESTIAL_BODY, CelestialBody.DIRECT_CODEC);
        DynamicRegistries.register(AddonRegistries.GALAXY, Galaxy.DIRECT_CODEC);

        DynamicRegistries.register(RocketRegistries.ROCKET_CONE, RocketCone.DIRECT_CODEC);
        DynamicRegistries.register(RocketRegistries.ROCKET_BODY, RocketBody.DIRECT_CODEC);
        DynamicRegistries.register(RocketRegistries.ROCKET_FIN, RocketFin.DIRECT_CODEC);
        DynamicRegistries.register(RocketRegistries.ROCKET_BOOSTER, RocketBooster.DIRECT_CODEC);
        DynamicRegistries.register(RocketRegistries.ROCKET_ENGINE, RocketEngine.DIRECT_CODEC);
        DynamicRegistries.register(RocketRegistries.ROCKET_UPGRADE, RocketUpgrade.DIRECT_CODEC);

        DynamicRegistries.register(AddonRegistries.CELESTIAL_TELEPORTER, CelestialTeleporter.DIRECT_CODEC);

        Registry.register(BuiltInRegistries.CHUNregirsterK_GENERATOR, Constant.id("satellite"), SatelliteChunkGenerator.CODEC);
        BuiltinObjects.register();
        BuiltInRocketRegistries.initialize();
        GcApiEntityAttributes.init();

        DynamicDimensionLoadCallback.register((minecraftServer, dynamicDimensionLoader) -> {
            ((SatelliteAccessor) minecraftServer).galacticraft$loadSatellites(dynamicDimensionLoader);
        });
        Gases.init();
        Constant.LOGGER.info("API Initialization Complete. (Took {}ms).", System.currentTimeMillis() - startInitTime);
    }
}
