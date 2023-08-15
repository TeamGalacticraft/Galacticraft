package dev.galacticraft.mod.content.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import dev.galacticraft.mod.content.GCEntityMemoryModuleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

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
        return BehaviorBuilder.create(instance -> instance.group(instance.registered(MemoryModuleType.LOOK_TARGET), instance.registered(MemoryModuleType.WALK_TARGET), instance.registered(GCEntityMemoryModuleTypes.TICKS_TIME_NEAR_PLAYER), instance.registered(GCEntityMemoryModuleTypes.SHOULD_AVOID_PLAYER)).apply(instance, (lookTargetMemoryAccessor, walkTargetMemoryAccessor, ticksNearPlayerMemoryAccessor, shouldAvoidPlayersMemoryAccessor) -> (serverLevel, livingEntity, l) -> {
            Optional entitySelectorOptional = (Optional)function.apply(livingEntity);
            if (entitySelectorOptional.isEmpty() || !predicate.test(livingEntity)) {
                return false;
            }

            Optional<Integer> optionalTicksNearPlayer = instance.tryGet(ticksNearPlayerMemoryAccessor);
            if (optionalTicksNearPlayer.isPresent()) {
                int ticksNearPlayer = optionalTicksNearPlayer.get();
                if (ticksNearPlayer < 40 ) {
                    // go to player
                    PositionTracker playerPosition = (PositionTracker) entitySelectorOptional.get();

                    lookTargetMemoryAccessor.set(playerPosition);
                    walkTargetMemoryAccessor.set(new WalkTarget(playerPosition, speedModifier, closeEnoughDist));
                    shouldAvoidPlayersMemoryAccessor.set(false);
                    // TODO: still working on making greys try to avoid the player after a few seconds

                } else {
                    // avoid player
                    return false;
                }
            }
            return true;
        }));
    }

}
