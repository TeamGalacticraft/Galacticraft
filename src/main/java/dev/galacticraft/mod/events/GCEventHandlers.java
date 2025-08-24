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

package dev.galacticraft.mod.events;

import dev.galacticraft.api.registry.ExtinguishableBlockRegistry;
import dev.galacticraft.api.registry.AcidTransformItemRegistry;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.landable.Landable;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.CelestialTeleporter;
import dev.galacticraft.mod.misc.footprint.FootprintManager;
import dev.galacticraft.mod.network.s2c.FootprintRemovedPacket;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class GCEventHandlers {
    public static void init() {
        GCSleepEventHandlers.init();
        GCInteractionEventHandlers.init();
        ServerTickEvents.END_WORLD_TICK.register(GCEventHandlers::onWorldTick);
    }

    public static void onPlayerChangePlanets(MinecraftServer server, ServerPlayer player, CelestialBody<?, ?> body, CelestialBody<?, ?> fromBody) {
        if (body.type() instanceof Landable landable && player.galacticraft$isCelestialScreenActive() && (player.galacticraft$getCelestialScreenState() == null || player.galacticraft$getCelestialScreenState().canTravel(server.registryAccess(), fromBody, body))) {
            player.galacticraft$closeCelestialScreen();
            ((CelestialTeleporter) landable.teleporter(body.config()).value()).onEnterAtmosphere(server.getLevel(landable.world(body.config())), player, body, fromBody);
        } else {
            player.connection.disconnect(Component.translatable(Translations.DimensionTp.INVALID_PACKET));
        }
    }

    public static void onWorldTick(ServerLevel level) {
        FootprintManager footprintManager = level.galacticraft$getFootprintManager();
        if (!footprintManager.footprintBlockChanges.isEmpty()) {
            for (GlobalPos targetPoint : footprintManager.footprintBlockChanges) {
                if (level.dimension().location().equals(targetPoint.dimension().location())) {
                    long packedPos = ChunkPos.asLong(targetPoint.pos());
                    PlayerLookup.around(level, targetPoint.pos(), 50).forEach(player -> {
                        ServerPlayNetworking.send(player, new FootprintRemovedPacket(packedPos, targetPoint.pos()));
                    });
                }
            }

            footprintManager.footprintBlockChanges.clear();
        }
        level.galacticraft$getSealerManager().tick();
    }

    public static boolean extinguishBlock(Level level, BlockPos pos, BlockState oldState) {
        ExtinguishableBlockRegistry.Entry entry = ExtinguishableBlockRegistry.INSTANCE.get(oldState.getBlock());
        if (entry == null) return false;
        BlockState newState = entry.transform(oldState);
        if (newState == null) return false;
        level.setBlockAndUpdate(pos, newState);
        entry.callback(new ExtinguishableBlockRegistry.Context(level, pos, oldState));
        return true;
    }

    public static boolean sulfuricAcidTransformItem(ItemEntity itemEntity, ItemStack original) {
        AcidTransformItemRegistry.Entry entry = AcidTransformItemRegistry.INSTANCE.get(original.getItem());
        if (entry == null) return false;
        ItemStack itemStack = entry.transform(original.copy());
        if (itemStack == null) return false;
        itemEntity.setItem(itemStack);
        entry.callback(new AcidTransformItemRegistry.Context(itemEntity, original));
        return true;
    }
}