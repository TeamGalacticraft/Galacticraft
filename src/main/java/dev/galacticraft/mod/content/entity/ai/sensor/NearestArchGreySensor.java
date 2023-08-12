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

package dev.galacticraft.mod.content.entity.ai.sensor;

import com.google.common.collect.ImmutableSet;
import dev.galacticraft.mod.content.GCEntityMemoryModuleTypes;
import dev.galacticraft.mod.content.entity.ArchGreyEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class NearestArchGreySensor extends Sensor<LivingEntity> {

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(GCEntityMemoryModuleTypes.NEAREST_ARCH_GREY);
    }
    @Override
    protected void doTick(ServerLevel serverLevel, LivingEntity mob) {
        Brain<?> brain = mob.getBrain();
        List<ArchGreyEntity> list = serverLevel.getEntitiesOfClass(ArchGreyEntity.class, mob.getBoundingBox().inflate(60.0, 16.0, 60.0), archGrey -> true);
        list.sort(Comparator.comparingDouble(mob::distanceToSqr));
        Optional<ArchGreyEntity> archGrey = list.stream().findFirst();
        brain.setMemory(GCEntityMemoryModuleTypes.NEAREST_ARCH_GREY, archGrey);

         this.leftArchGreyZone(brain, serverLevel, mob, archGrey);

    }

    private void leftArchGreyZone(Brain<?> brain, ServerLevel serverLevel, LivingEntity mob, Optional<ArchGreyEntity> entity) {
        if (entity.isPresent()) {
            ArchGreyEntity archGrey = entity.get();
            Optional<Boolean> greyLeftArchGreyZone= brain.getMemory(GCEntityMemoryModuleTypes.GREY_LEFT_ARCH_GREY_ZONE);
            if (greyLeftArchGreyZone.isPresent()) {
                if (mob.distanceTo(archGrey) < 19.0) {
                    if (mob.distanceTo(archGrey) < 4.0 ) {
                        brain.setMemory(GCEntityMemoryModuleTypes.GREY_LEFT_ARCH_GREY_ZONE, false);
                    }
                } else {
                    brain.setMemory(GCEntityMemoryModuleTypes.GREY_LEFT_ARCH_GREY_ZONE, true);
                }
            } else {
                brain.setMemory(GCEntityMemoryModuleTypes.GREY_LEFT_ARCH_GREY_ZONE, false);
            }
        }
    }

}
