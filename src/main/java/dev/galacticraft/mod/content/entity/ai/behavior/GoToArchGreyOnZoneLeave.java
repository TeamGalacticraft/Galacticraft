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
