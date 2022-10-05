package dev.galacticraft.mod.events;

import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.mod.accessor.LivingEntityAccessor;
import dev.galacticraft.mod.block.special.CryogenicChamberBlock;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class GCEventHandler {
    public static void init() {
        EntitySleepEvents.ALLOW_BED.register(GCEventHandler::allowCryogenicSleep);
        EntitySleepEvents.MODIFY_SLEEPING_DIRECTION.register(GCEventHandler::changeSleepPosition);
        EntitySleepEvents.ALLOW_SLEEPING.register(GCEventHandler::sleepInSpace);

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
            BlockState state = entity.getLevel().getBlockState(sleepingPos);
            if (state.getBlock() instanceof CryogenicChamberBlock)
                return state.getValue(CryogenicChamberBlock.FACING);
        }

        return sleepingDirection;
    }

    public static Player.BedSleepingProblem sleepInSpace(Player player, BlockPos sleepingPos) {
        Level level = player.getLevel();
        CelestialBody body = CelestialBody.getByDimension(level).orElse(null);
        if (body != null && level.getBlockState(sleepingPos).getBlock() instanceof BedBlock && !body.atmosphere().breathable()) {
            player.sendSystemMessage(Component.translatable("chat.galacticraft.bed_fail"));
            return Player.BedSleepingProblem.NOT_POSSIBLE_HERE;
        }
        return null;
    }
}
