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
import dev.galacticraft.impl.universe.celestialbody.config.PlanetConfig;
import dev.galacticraft.mod.accessor.CryogenicAccessor;
import dev.galacticraft.mod.content.GCEntityTypes;
import dev.galacticraft.mod.content.block.special.CryogenicChamberBlock;
import dev.galacticraft.mod.content.entity.ParachestEntity;
import dev.galacticraft.mod.content.item.GCItems;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
    }

    public static InteractionResult allowCryogenicSleep(LivingEntity entity, BlockPos sleepingPos, BlockState state, boolean vanillaResult) {
        if (entity instanceof CryogenicAccessor player) {
            if (player.galacticraft$isInCryoSleep()) {
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public static Direction changeSleepPosition(LivingEntity entity, BlockPos sleepingPos, @Nullable Direction sleepingDirection) {
        if (entity instanceof CryogenicAccessor player && player.galacticraft$isInCryoSleep()) {
            BlockState state = entity.level().getBlockState(sleepingPos);
            if (state.getBlock() instanceof CryogenicChamberBlock)
                return state.getValue(CryogenicChamberBlock.FACING);
        }

        return sleepingDirection;
    }

    public static Player.BedSleepingProblem sleepInSpace(Player player, BlockPos sleepingPos) {
        Level level = player.level();
        CelestialBody body = CelestialBody.getByDimension(level).orElse(null);
        if (body != null && level.getBlockState(sleepingPos).getBlock() instanceof BedBlock && !body.atmosphere().breathable()) {
            player.sendSystemMessage(Component.translatable("chat.galacticraft.bed_fail"));
            return Player.BedSleepingProblem.NOT_POSSIBLE_HERE;
        }

        return null;
    }

    public static InteractionResult canCryoSleep(Player player, BlockPos sleepingPos, boolean vanillaResult) {
        if (player.galacticraft$isInCryoSleep())
            return InteractionResult.SUCCESS;
        return vanillaResult ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    public static void onWakeFromCryoSleep(LivingEntity entity, BlockPos sleepingPos) {
        Level level = entity.level();
        if (!level.isClientSide && level instanceof ServerLevel serverLevel && entity instanceof CryogenicAccessor player) {
            entity.heal(5.0F);
            player.galacticraft$setCryogenicChamberCooldown(6000);

//            if (serverLevel.areAllPlayersAsleep() && ws.getGameRules().getBoolean("doDaylightCycle")) {
//                WorldUtil.setNextMorning(ws);
//            }
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
            if (body.config() instanceof PlanetConfig planetConfig) {
                var chestSpawn = planetConfig.celestialHandler().getParaChestSpawnLocation(player.serverLevel(), player, player.getRandom());
                if (chestSpawn != null) {
                    ParachestEntity chest = new ParachestEntity(GCEntityTypes.PARACHEST, player.serverLevel(), NonNullList.of(new ItemStack(Items.DIAMOND)), 81000);

                    chest.setPos(chestSpawn);
                    chest.color = DyeColor.RED;//player.getGearInv().getParachuteInSlot().isEmpty() ? EnumDyeColor.WHITE : ItemParaChute.getDyeEnumFromParachuteDamage(stats.getParachuteInSlot().getItemDamage());

                    player.serverLevel().addFreshEntity(chest);
                }
            }
            ((CelestialTeleporter)landable.teleporter(body.config()).value()).onEnterAtmosphere(server.getLevel(landable.world(body.config())), player, body, fromBody);
        } else {
            player.connection.disconnect(Component.literal("Invalid planet teleport packet received."));
        }
    }


    public static void onPlayerTick(Player player) {

    }
}