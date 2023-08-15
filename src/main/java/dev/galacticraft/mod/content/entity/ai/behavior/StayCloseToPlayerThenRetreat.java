package dev.galacticraft.mod.content.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import dev.galacticraft.mod.content.GCEntityMemoryModuleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class StayCloseToPlayerThenRetreat<E extends Mob> extends ExtendedBehaviour<E> {

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return null;
    }

    public static BehaviorControl<LivingEntity> create(Function<LivingEntity, Optional<PositionTracker>> function, Predicate<LivingEntity> predicate, int closeEnoughDist, float speedModifier) {
        return BehaviorBuilder.create(instance -> instance.group(instance.registered(MemoryModuleType.LOOK_TARGET), instance.registered(MemoryModuleType.WALK_TARGET), instance.registered(GCEntityMemoryModuleTypes.TICKS_TIME_NEAR_PLAYER), instance.registered(GCEntityMemoryModuleTypes.SHOULD_AVOID_PLAYER), instance.registered(MemoryModuleType.NEAREST_VISIBLE_PLAYER)).apply(instance, (lookTargetMemoryAccessor, walkTargetMemoryAccessor, ticksNearPlayerMemoryAccessor, shouldAvoidPlayersMemoryAccessor, nearestVisiblePlayersMemoryAccessor) -> (serverLevel, livingEntity, l) -> {
            Optional entitySelectorOptional = (Optional)function.apply(livingEntity);
            if (entitySelectorOptional.isEmpty() || !predicate.test(livingEntity)) {
                return false;
            }

            Optional<Integer> optionalTicksNearPlayer = instance.tryGet(ticksNearPlayerMemoryAccessor);
            if (optionalTicksNearPlayer.isPresent()) {
                int ticksNearPlayer = optionalTicksNearPlayer.get();
                Optional<Boolean>  shouldAvoidPlayers = instance.tryGet(shouldAvoidPlayersMemoryAccessor);
                if (shouldAvoidPlayers.isPresent()) {
                    if (!shouldAvoidPlayers.get()) {
                        if (ticksNearPlayer < 40 ) {
                            // go to player
                            PositionTracker playerPosition = (PositionTracker) entitySelectorOptional.get();

                            lookTargetMemoryAccessor.set(playerPosition);
                            walkTargetMemoryAccessor.set(new WalkTarget(playerPosition, speedModifier, closeEnoughDist));

                        } else {
                            shouldAvoidPlayersMemoryAccessor.set(true);
                        }
                    } else {
                        // TODO: stop looking at player
                        // TODO: start avoiding player

                        // check if mob is away from player

                        if (livingEntity.getBrain().getMemory(GCEntityMemoryModuleTypes.NEAREST_ARCH_GREY).isPresent()) {
                            livingEntity.getBrain().setMemory(GCEntityMemoryModuleTypes.GREY_LEFT_ARCH_GREY_ZONE, true);
                        } else {
                            // TODO: figure out how to avoid the player
                        }

                    }
                } else {
                    shouldAvoidPlayersMemoryAccessor.set(false);
                }
            }
            return true;
        }));
    }

}
