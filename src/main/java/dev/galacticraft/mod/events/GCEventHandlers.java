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

package dev.galacticraft.mod.events;

import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.mod.accessor.LivingEntityAccessor;
import dev.galacticraft.mod.content.block.special.CryogenicChamberBlock;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
    }

    public static InteractionResult allowCryogenicSleep(LivingEntity entity, BlockPos sleepingPos, BlockState state, boolean vanillaResult) {
        if (entity instanceof LivingEntityAccessor player) {
            if (player.isInCryoSleep()) {
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public static Direction changeSleepPosition(LivingEntity entity, BlockPos sleepingPos, @Nullable Direction sleepingDirection) {
        if (((LivingEntityAccessor)entity).isInCryoSleep()) {
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
        if (((LivingEntityAccessor)player).isInCryoSleep())
            return InteractionResult.SUCCESS;
        return vanillaResult ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    public static void onWakeFromCryoSleep(LivingEntity entity, BlockPos sleepingPos) {
        Level level = entity.level();
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            entity.heal(5.0F);
            ((LivingEntityAccessor)entity).setCryogenicChamberCooldown(6000);

//            if (serverLevel.areAllPlayersAsleep() && ws.getGameRules().getBoolean("doDaylightCycle")) {
//                WorldUtil.setNextMorning(ws);
//            }
        }
    }
}
