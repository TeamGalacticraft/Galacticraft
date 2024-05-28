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
import dev.galacticraft.api.registry.BuiltInRocketRegistries;
import dev.galacticraft.dynamicdimensions.api.DynamicDimensionRegistry;
import dev.galacticraft.dynamicdimensions.api.event.DynamicDimensionLoadCallback;
import dev.galacticraft.impl.internal.command.GCApiCommands;
import dev.galacticraft.impl.universe.BuiltinObjects;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.data.gen.SatelliteChunkGenerator;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.jetbrains.annotations.ApiStatus;

import javax.swing.event.ChangeEvent;
import java.io.File;
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

        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, Constant.id("satellite"), SatelliteChunkGenerator.CODEC);
        BuiltinObjects.register();
        BuiltInRocketRegistries.initialize();
        GcApiEntityAttributes.init();
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);

        DynamicDimensionLoadCallback.register((minecraftServer, dynamicDimensionLoader) -> {
            ((SatelliteAccessor) minecraftServer).galacticraft$loadSatellites(dynamicDimensionLoader);
        });
        Gases.init();
        Constant.LOGGER.info("API Initialization Complete. (Took {}ms).", System.currentTimeMillis() - startInitTime);
    }

    private void onServerStarted(MinecraftServer server) {
        // Update the directory when the server starts
        updateWorldSaveDirectory(server);
    }

    private void updateWorldSaveDirectory(MinecraftServer server) {
        currentWorldSaveDirectory = Path.of(Minecraft.getInstance().getLevelSource().getBaseDir().toString(), server.getWorldData().getLevelName());
        System.out.println("World Save Directory: " + currentWorldSaveDirectory.toString());
    }
}
