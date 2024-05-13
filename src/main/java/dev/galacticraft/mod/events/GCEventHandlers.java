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

package dev.galacticraft.mod.events;

import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.landable.Landable;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.CelestialTeleporter;
import dev.galacticraft.impl.rocket.RocketDataImpl;
import dev.galacticraft.mod.content.block.special.CryogenicChamberBlock;
import dev.galacticraft.mod.content.item.GCItems;
import dev.galacticraft.mod.misc.footprint.FootprintManager;
import dev.galacticraft.mod.network.packets.FootprintRemovedPacket;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class GCEventHandlers {
    public static void init() {
        EntitySleepEvents.ALLOW_BED.register(GCEventHandlers::allowCryogenicSleep);
        EntitySleepEvents.MODIFY_SLEEPING_DIRECTION.register(GCEventHandlers::changeSleepPosition);
        EntitySleepEvents.ALLOW_SLEEPING.register(GCEventHandlers::sleepInSpace);
        EntitySleepEvents.ALLOW_SLEEP_TIME.register(GCEventHandlers::canCryoSleep);
        EntitySleepEvents.STOP_SLEEPING.register(GCEventHandlers::onWakeFromCryoSleep);
        GiveCommandEvents.MODIFY.register(GCEventHandlers::modifyOnGive);
        ServerTickEvents.END_WORLD_TICK.register(GCEventHandlers::onWorldTick);
    }

    public static InteractionResult allowCryogenicSleep(LivingEntity entity, BlockPos sleepingPos, BlockState state, boolean vanillaResult) {
        return entity.isInCryoSleep()
                ? InteractionResult.SUCCESS
                : InteractionResult.PASS;
    }

    public static Direction changeSleepPosition(LivingEntity entity, BlockPos sleepingPos, @Nullable Direction sleepingDirection) {
        if (entity.isInCryoSleep()) {
            BlockState state = entity.level().getBlockState(sleepingPos);

            if (state.getBlock() instanceof CryogenicChamberBlock) return state.getValue(CryogenicChamberBlock.FACING);
        }

        return sleepingDirection;
    }

    public static Player.BedSleepingProblem sleepInSpace(Player player, BlockPos sleepingPos) {
        Level level = player.level();
        CelestialBody body = CelestialBody.getByDimension(level).orElse(null);
        if (body != null && level.getBlockState(sleepingPos).getBlock() instanceof BedBlock && !body.atmosphere().breathable()) {
            player.sendSystemMessage(Component.translatable(Translations.Chat.BED_FAIL));
            return Player.BedSleepingProblem.NOT_POSSIBLE_HERE;
        }

        return null;
    }

    public static InteractionResult canCryoSleep(Player player, BlockPos sleepingPos, boolean vanillaResult) {
        return player.isInCryoSleep() || vanillaResult
                ? InteractionResult.SUCCESS
                : InteractionResult.PASS;
    }

    public static void onWakeFromCryoSleep(LivingEntity entity, BlockPos sleepingPos) {
        if (!entity.level().isClientSide() && entity.isInCryoSleep()) {
            entity.endCyroSleep();
        }
    }

    public static ItemStack modifyOnGive(ItemStack previousItemStack) {
        // This will set default data of an empty Rocket item when /give command is used, it also checks required tags for Rocket item to be rendered properly.
        if (previousItemStack.is(GCItems.ROCKET) && (!previousItemStack.hasTag() || previousItemStack.hasTag() && !previousItemStack.getTag().getAllKeys().containsAll(RocketDataImpl.DEFAULT_ROCKET.getAllKeys()))) {
            previousItemStack.setTag(RocketDataImpl.DEFAULT_ROCKET);
        }
        return previousItemStack;
    }

    public static void onPlayerChangePlanets(MinecraftServer server, ServerPlayer player, CelestialBody<?, ?> body, CelestialBody<?, ?> fromBody) {
        if (body.type() instanceof Landable landable && player.galacticraft$isCelestialScreenActive() && (player.galacticraft$getCelestialScreenState() == null || player.galacticraft$getCelestialScreenState().canTravel(server.registryAccess(), fromBody, body))) {
            player.galacticraft$closeCelestialScreen();
            ((CelestialTeleporter) landable.teleporter(body.config()).value()).onEnterAtmosphere(server.getLevel(landable.world(body.config())), player, body, fromBody);
        } else {
            player.connection.disconnect(Component.literal("Invalid planet teleport packet received."));
        }
    }


    public static void onPlayerTick(Player player) {

    }

    public static void onWorldTick(ServerLevel world) {
        FootprintManager footprintManager = world.galacticraft$getFootprintManager();
        if (!footprintManager.footprintBlockChanges.isEmpty()) {
            for (GlobalPos targetPoint : footprintManager.footprintBlockChanges) {
                ;
                if (world.dimension().location().equals(targetPoint.dimension().location())) {
                    long packedPos = ChunkPos.asLong(targetPoint.pos());
                    PlayerLookup.around(world, targetPoint.pos(), 50).forEach(player -> {
                        ServerPlayNetworking.send(player, new FootprintRemovedPacket(packedPos, targetPoint.pos()));
                    });
                }
            }

            footprintManager.footprintBlockChanges.clear();
        }
    }
}