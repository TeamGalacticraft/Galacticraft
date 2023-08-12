package dev.galacticraft.mod.content;

import dev.galacticraft.mod.content.entity.ArchGreyEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;

public class GCEntityMemoryModuleTypes {

    public static final MemoryModuleType<ArchGreyEntity> NEAREST_ARCH_GREY;

    static {
        NEAREST_ARCH_GREY = MemoryModuleType.register("nearest_arch_grey");
    }
    public static void register() {

    }

}
