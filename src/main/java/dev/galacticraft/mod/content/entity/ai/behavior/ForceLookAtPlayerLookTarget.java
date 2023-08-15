package dev.galacticraft.mod.content.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import dev.galacticraft.mod.content.GCEntityMemoryModuleTypes;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.List;
import java.util.Optional;

// copied from MoveToTargetSink.java except modified to look at entity constantly
public class ForceLookAtPlayerLookTarget<E extends Mob> extends ExtendedBehaviour<E> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS = ObjectArrayList.of(Pair.of(MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryStatus.VALUE_PRESENT));

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean timedOut(long gameTime) {
        return false;
    }

    @Override
    protected boolean shouldKeepRunning(E entity) {
        // TODO: turn GCEntityMemoryModuleTypes.GREY_LEFT_ARCH_GREY_ZONE into a memory to just store if it should look at player (so it can be used outside the arch grey)
        Optional<Boolean>  isGreyLeftArchGreyZone = entity.getBrain().getMemory(GCEntityMemoryModuleTypes.GREY_LEFT_ARCH_GREY_ZONE);
        if (isGreyLeftArchGreyZone.isPresent()) {
            if (isGreyLeftArchGreyZone.get()) {
                return false;
            } else {
                return entity.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
            }
        } else {
            return entity.getBrain().hasMemoryValue(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
        }
    }

    @Override
    protected void tick(E entity) {
        // make this player only
        entity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).ifPresent(target -> entity.getLookControl().setLookAt(target.getEyePosition()));
    }



}
