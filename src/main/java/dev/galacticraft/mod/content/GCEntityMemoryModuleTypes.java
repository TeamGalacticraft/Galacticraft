package dev.galacticraft.mod.content;

import com.mojang.serialization.Codec;
import dev.galacticraft.mod.content.entity.ArchGreyEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;

public class GCEntityMemoryModuleTypes {

    public static final MemoryModuleType<ArchGreyEntity> NEAREST_ARCH_GREY;
    public static final MemoryModuleType<Boolean> GREY_LEFT_ARCH_GREY_ZONE;

    static {
        NEAREST_ARCH_GREY = MemoryModuleType.register("nearest_arch_grey");
        GREY_LEFT_ARCH_GREY_ZONE = MemoryModuleType.register("grey_left_arch_grey_zone");
    }
    public static void register() {

    }

}
