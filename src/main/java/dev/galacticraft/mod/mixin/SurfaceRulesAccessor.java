package dev.galacticraft.mod.mixin;

import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SurfaceRules.Context.class)
public interface SurfaceRulesAccessor {
    @Accessor("randomState")
    RandomState getRandomState();

    @Accessor("blockX")
    int getBlockX();

    @Accessor("blockZ")
    int getBlockZ();
}
