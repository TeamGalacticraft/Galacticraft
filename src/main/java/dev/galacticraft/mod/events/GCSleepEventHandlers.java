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

import dev.galacticraft.mod.content.block.special.CryogenicChamberBlock;
import dev.galacticraft.mod.content.block.special.CryogenicChamberPart;
import dev.galacticraft.mod.util.Translations;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class GCSleepEventHandlers {
    public static void init() {
        EntitySleepEvents.ALLOW_BED.register(GCSleepEventHandlers::allowCryogenicSleep);
        EntitySleepEvents.ALLOW_SETTING_SPAWN.register(GCSleepEventHandlers::allowSettingSpawn);
        EntitySleepEvents.MODIFY_SLEEPING_DIRECTION.register(GCSleepEventHandlers::changeSleepPosition);
        EntitySleepEvents.ALLOW_SLEEPING.register(GCSleepEventHandlers::sleepInSpace);
        EntitySleepEvents.ALLOW_SLEEP_TIME.register(GCSleepEventHandlers::canCryoSleep);
        EntitySleepEvents.STOP_SLEEPING.register(GCSleepEventHandlers::onWakeFromCryoSleep);
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
}