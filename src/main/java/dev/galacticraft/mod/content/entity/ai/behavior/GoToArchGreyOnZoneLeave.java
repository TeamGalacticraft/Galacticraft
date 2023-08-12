package dev.galacticraft.mod.content.entity.ai.behavior;

import dev.galacticraft.mod.content.GCEntityMemoryModuleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class GoToArchGreyOnZoneLeave {
    public static BehaviorControl<LivingEntity> create(Function<LivingEntity, Optional<PositionTracker>> function, Predicate<LivingEntity> predicate, int closeEnoughDist, int distanceToLeave, float speedModifier) {
        return BehaviorBuilder.create(instance -> instance.group(instance.registered(MemoryModuleType.LOOK_TARGET), instance.registered(MemoryModuleType.WALK_TARGET), instance.registered(GCEntityMemoryModuleTypes.GREY_LEFT_ARCH_GREY_ZONE)).apply(instance, (memoryAccessor, memoryAccessor2, memoryAccessor3) -> (serverLevel, livingEntity, l) -> {
            Optional optional = (Optional)function.apply(livingEntity);
            Optional<Boolean> outArchGreyZoneOptional = instance.tryGet(memoryAccessor3);
            if (optional.isEmpty() || !predicate.test(livingEntity)) {
                return false;
            }
            if (outArchGreyZoneOptional.isEmpty()) {
                return false;
            }
            if (outArchGreyZoneOptional.get()) { // if false, or in zone
                PositionTracker positionTracker2 = (PositionTracker)optional.get();
                memoryAccessor.set(positionTracker2);
                memoryAccessor2.set(new WalkTarget(positionTracker2, speedModifier, closeEnoughDist));
            }

            return true;
        }));
    }
}
