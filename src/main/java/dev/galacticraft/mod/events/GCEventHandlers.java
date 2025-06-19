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

import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.landable.Landable;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.CelestialTeleporter;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.special.CryogenicChamberBlock;
import dev.galacticraft.mod.content.block.special.CryogenicChamberPart;
import dev.galacticraft.mod.content.item.CannedFoodItem;
import dev.galacticraft.mod.misc.footprint.FootprintManager;
import dev.galacticraft.mod.network.s2c.FootprintRemovedPacket;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class GCEventHandlers {
    public static void init() {
        EntitySleepEvents.ALLOW_BED.register(GCEventHandlers::allowCryogenicSleep);
        EntitySleepEvents.ALLOW_SETTING_SPAWN.register(GCEventHandlers::allowSettingSpawn);
        EntitySleepEvents.MODIFY_SLEEPING_DIRECTION.register(GCEventHandlers::changeSleepPosition);
        EntitySleepEvents.ALLOW_SLEEPING.register(GCEventHandlers::sleepInSpace);
        EntitySleepEvents.ALLOW_SLEEP_TIME.register(GCEventHandlers::canCryoSleep);
        EntitySleepEvents.STOP_SLEEPING.register(GCEventHandlers::onWakeFromCryoSleep);
        ServerTickEvents.END_WORLD_TICK.register(GCEventHandlers::onWorldTick);
        UseItemCallback.EVENT.register(GCEventHandlers::onPlayerUseItem);
    }

    private static InteractionResultHolder<ItemStack> onPlayerUseItem(Player player, Level level, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (CannedFoodItem.canAddToCan(heldItem.getItem())) {
            Vec3 playerEyePos = player.getEyePosition();

            if (player.galacticraft$hasMask()) {
                if (heldItem.getItem() instanceof CannedFoodItem) {
                    return InteractionResultHolder.pass(heldItem);
                } else {
                    player.displayClientMessage(Component.translatable(Translations.Chat.CANNOT_EAT_WITH_MASK).withStyle(Constant.Text.RED_STYLE), true);
                    return InteractionResultHolder.fail(heldItem);
                }
            } else if (!level.getDefaultBreathable() && !level.isBreathable(new BlockPos((int) Math.floor(playerEyePos.x), (int) Math.floor(playerEyePos.y), (int) Math.floor(playerEyePos.z)))) { //sealed atmosphere check. they dont have a mask on so make sure they can breathe before eating
                player.displayClientMessage(Component.translatable(Translations.Chat.CANNOT_EAT_IN_NO_ATMOSPHERE).withStyle(Constant.Text.RED_STYLE), true);
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    public static InteractionResult allowCryogenicSleep(LivingEntity entity, BlockPos sleepingPos, BlockState state, boolean vanillaResult) {
        if (state.getBlock() instanceof CryogenicChamberPart && !state.getValue(CryogenicChamberPart.TOP)) {
            return entity.isInCryoSleep()
                    ? InteractionResult.SUCCESS
                    : InteractionResult.PASS;
        }
        return InteractionResult.PASS;
    }

    public static boolean allowSettingSpawn(Player player, BlockPos sleepingPos) {
        Level level = player.level();
        return level.isBreathable(sleepingPos) || level.getBlockState(sleepingPos).getBlock() instanceof CryogenicChamberBlock;
    }

    public static Direction changeSleepPosition(LivingEntity entity, BlockPos sleepingPos, @Nullable Direction sleepingDirection) {
        if (entity.isInCryoSleep()) {
            BlockState state = entity.level().getBlockState(sleepingPos);

            if (state.getBlock() instanceof CryogenicChamberPart) return state.getValue(CryogenicChamberBlock.FACING);
        }

        return sleepingDirection;
    }

    public static Player.BedSleepingProblem sleepInSpace(Player player, BlockPos sleepingPos) {
        Level level = player.level();
        if (!level.isBreathable(sleepingPos) && level.getBlockState(sleepingPos).getBlock() instanceof BedBlock) {
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
        Level level = entity.level();
        if (!level.isClientSide() && entity.isInCryoSleep()) {
            entity.endCryoSleep();
            BlockPos basePos = sleepingPos.below();
            BlockState baseState = level.getBlockState(basePos);
            float angle = 0.0F;
            if (baseState.getBlock() instanceof CryogenicChamberBlock) {
                level.setBlockAndUpdate(basePos, baseState.setValue(BlockStateProperties.OCCUPIED, false));
                angle = baseState.getValue(CryogenicChamberBlock.FACING).toYRot();
                entity.setYRot(angle);
            }

            ServerPlayer serverPlayer = (ServerPlayer) entity;
            if (!(serverPlayer.getRespawnDimension() == level.dimension() && basePos.equals(serverPlayer.getRespawnPosition()))) {
                boolean forceRespawn = false;
                serverPlayer.setRespawnPosition(level.dimension(), basePos, angle, forceRespawn, true);
            }
        }
    }

    public static void onPlayerChangePlanets(MinecraftServer server, ServerPlayer player, CelestialBody<?, ?> body, CelestialBody<?, ?> fromBody) {
        if (body.type() instanceof Landable landable && player.galacticraft$isCelestialScreenActive() && (player.galacticraft$getCelestialScreenState() == null || player.galacticraft$getCelestialScreenState().canTravel(server.registryAccess(), fromBody, body))) {
            player.galacticraft$closeCelestialScreen();
            ((CelestialTeleporter) landable.teleporter(body.config()).value()).onEnterAtmosphere(server.getLevel(landable.world(body.config())), player, body, fromBody);
        } else {
            player.connection.disconnect(Component.translatable(Translations.DimensionTp.INVALID_PACKET));
        }
    }

    public static void onPlayerTick(Player player) {

    }

    public static void onWorldTick(ServerLevel level) {
        FootprintManager footprintManager = level.galacticraft$getFootprintManager();
        if (!footprintManager.footprintBlockChanges.isEmpty()) {
            for (GlobalPos targetPoint : footprintManager.footprintBlockChanges) {
                ;
                if (level.dimension().location().equals(targetPoint.dimension().location())) {
                    long packedPos = ChunkPos.asLong(targetPoint.pos());
                    PlayerLookup.around(level, targetPoint.pos(), 50).forEach(player -> {
                        ServerPlayNetworking.send(player, new FootprintRemovedPacket(packedPos, targetPoint.pos()));
                    });
                }
            }

            footprintManager.footprintBlockChanges.clear();
        }
    }
}