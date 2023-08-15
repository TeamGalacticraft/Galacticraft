package dev.galacticraft.mod.content.entity.ai.sensor;

import com.google.common.collect.ImmutableSet;
import dev.galacticraft.mod.content.GCEntityMemoryModuleTypes;
import dev.galacticraft.mod.content.entity.ArchGreyEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TimeNearPlayerSensor extends Sensor<LivingEntity> {

    //
    public TimeNearPlayerSensor() {
        super(1); // run sensor every 1 tick
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_PLAYERS, GCEntityMemoryModuleTypes.TICKS_TIME_NEAR_PLAYER);
    }

    @Override
    protected void doTick(ServerLevel serverLevel, LivingEntity mob) {
        Brain<?> brain = mob.getBrain();
        if (brain.getMemory(MemoryModuleType.NEAREST_PLAYERS).isPresent()) {
            List<Player> players = brain.getMemory(MemoryModuleType.NEAREST_PLAYERS).get().stream().filter(player -> player.distanceTo(mob) <= 4).toList();
            if (brain.getMemory(GCEntityMemoryModuleTypes.TICKS_TIME_NEAR_PLAYER).isPresent()) {
                int tickNearPlayer = brain.getMemory(GCEntityMemoryModuleTypes.TICKS_TIME_NEAR_PLAYER).get();
                if (!players.isEmpty()) {
                    Vec3 modifiedDelta = mob.getDeltaMovement().normalize().add(0, 1, 0);
                    if (modifiedDelta.equals(Vec3.ZERO)) {
                        // if not moving, add ticks
                        brain.setMemory(GCEntityMemoryModuleTypes.TICKS_TIME_NEAR_PLAYER, tickNearPlayer + 1);
                    } else {
                        // set tick to 0
                        brain.setMemory(GCEntityMemoryModuleTypes.TICKS_TIME_NEAR_PLAYER, 0);
                    }
                } else {
                    // set tick to 0
                    brain.setMemory(GCEntityMemoryModuleTypes.TICKS_TIME_NEAR_PLAYER, 0);
                }
            } else {
                // set to 0 if it doesn't exist
                brain.setMemory(GCEntityMemoryModuleTypes.TICKS_TIME_NEAR_PLAYER, 0);
            }

        }
    }

}
