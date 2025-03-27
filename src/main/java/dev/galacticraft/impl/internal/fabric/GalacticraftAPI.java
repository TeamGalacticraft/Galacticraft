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
import dev.galacticraft.impl.network.GCApiPackets;
import dev.galacticraft.impl.network.GCApiServerPacketReceivers;
import dev.galacticraft.impl.universe.BuiltinObjects;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.data.gen.SatelliteChunkGenerator;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;

@ApiStatus.Internal
public class GalacticraftAPI implements ModInitializer {
    public static final SimpleContainer EMPTY_INV = new SimpleContainer(0);
    //don't know if this is the best way to get file path, but it works
    public static Path currentWorldSaveDirectory;

    @Override
    public void onInitialize() {
        long startInitTime = System.currentTimeMillis();
        GCApiCommands.register();

        DynamicRegistries.registerSynced(AddonRegistries.CELESTIAL_BODY, CelestialBody.DIRECT_CODEC);
        DynamicRegistries.registerSynced(AddonRegistries.GALAXY, Galaxy.DIRECT_CODEC);

        DynamicRegistries.registerSynced(RocketRegistries.ROCKET_CONE, RocketCone.DIRECT_CODEC);
        DynamicRegistries.registerSynced(RocketRegistries.ROCKET_BODY, RocketBody.DIRECT_CODEC);
        DynamicRegistries.registerSynced(RocketRegistries.ROCKET_FIN, RocketFin.DIRECT_CODEC);
        DynamicRegistries.registerSynced(RocketRegistries.ROCKET_BOOSTER, RocketBooster.DIRECT_CODEC);
        DynamicRegistries.registerSynced(RocketRegistries.ROCKET_ENGINE, RocketEngine.DIRECT_CODEC);
        DynamicRegistries.registerSynced(RocketRegistries.ROCKET_UPGRADE, RocketUpgrade.DIRECT_CODEC);

        DynamicRegistries.registerSynced(AddonRegistries.CELESTIAL_TELEPORTER, CelestialTeleporter.DIRECT_CODEC);

        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, Constant.id("satellite"), SatelliteChunkGenerator.CODEC);
        BuiltinObjects.register();
        BuiltInRocketRegistries.initialize();
        GcApiEntityAttributes.init();
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);

        DynamicDimensionLoadCallback.register((minecraftServer, dynamicDimensionLoader) -> {
            ((SatelliteAccessor) minecraftServer).galacticraft$loadSatellites(dynamicDimensionLoader);
        });

        // todo: update celestial body level cache
        DynamicRegistrySetupCallback.EVENT.register(view -> {
            view.registerEntryAdded(AddonRegistries.CELESTIAL_BODY, (rawId, id, object) -> {

            });
        });
        Gases.init();
        GCApiPackets.register();
        GCApiServerPacketReceivers.register();
        Constant.LOGGER.info("API Initialization Complete. (Took {}ms).", System.currentTimeMillis() - startInitTime);
    }

    private void onServerStarted(MinecraftServer server) {
        // Update the directory when the server starts
        updateWorldSaveDirectory(server);
    }

    private void updateWorldSaveDirectory(MinecraftServer server) {
        // Get the main (overworld) level from the server
        ServerLevel overworld = server.getLevel(net.minecraft.world.level.Level.OVERWORLD);

        if (overworld != null) {
            currentWorldSaveDirectory = overworld.getServer().getWorldPath(LevelResource.ROOT);
            Constant.LOGGER.info("World Save Directory: {}", currentWorldSaveDirectory.toString());
        } else {
            Constant.LOGGER.error("Error: Overworld level is null");
        }
    }
}
