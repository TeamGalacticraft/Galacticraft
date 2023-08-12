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
