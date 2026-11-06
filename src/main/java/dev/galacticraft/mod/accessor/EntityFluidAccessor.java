package dev.galacticraft.mod.accessor;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public interface EntityFluidAccessor {
    boolean gc$getWasTouchingGCFluid();
    void gc$setWasTouchingGCFluid(boolean value);

    @Nullable
    TagKey<Fluid> gc$getInFluidType();
    void gc$setInFluidType(@Nullable TagKey<Fluid> fluidType);
}
