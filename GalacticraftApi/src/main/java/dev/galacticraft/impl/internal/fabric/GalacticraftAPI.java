/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

import dev.galacticraft.api.accessor.ServerResearchAccessor;
import dev.galacticraft.api.entity.attribute.GcApiEntityAttributes;
import dev.galacticraft.impl.Constant;
import dev.galacticraft.impl.internal.command.GCApiCommands;
import dev.galacticraft.impl.internal.world.gen.SatelliteChunkGenerator;
import dev.galacticraft.impl.internal.world.gen.biome.GcApiBiomes;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class GalacticraftAPI implements ModInitializer {
    public static final SimpleContainer EMPTY_INV = new SimpleContainer(0);

    @Override
    public void onInitialize() {
        long startInitTime = System.currentTimeMillis();
        Constant.LOGGER.info("Initializing...");
        GCApiCommands.register();
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (((ServerResearchAccessor) player).isResearchDirty()) {
                    ServerPlayNetworking.send(player, new ResourceLocation(Constant.MOD_ID, "research_update"), ((ServerResearchAccessor) player).writeResearchChanges(new FriendlyByteBuf(Unpooled.buffer())));
                }
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation(Constant.MOD_ID, "flag_data"), (server, player, handler, buf, responseSender) -> {
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
        ServerPlayNetworking.registerGlobalReceiver(new ResourceLocation(Constant.MOD_ID, "team_name"), (server, player, handler, buf, responseSender) -> {
            String s = buf.readUtf();

            server.execute(() -> {
                //todo: teams
            });
        });

        Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(Constant.MOD_ID, "satellite"), SatelliteChunkGenerator.CODEC);
        GcApiBiomes.register();
        GcApiEntityAttributes.init();
        Constant.LOGGER.info("Initialization Complete. (Took {}ms).", System.currentTimeMillis() - startInitTime);
    }
}
