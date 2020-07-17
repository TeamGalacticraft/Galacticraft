package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.world.dimension.GalacticraftDimensions;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionType.class)
public class DimensionTypeMixin {
    @Inject(method = "addRegistryDefaults", at = @At("RETURN"), cancellable = true)
    private static void addGCDims(DynamicRegistryManager.Impl registryTracker, CallbackInfoReturnable<DynamicRegistryManager.Impl> cir) {
        GalacticraftDimensions.addGCDims(registryTracker);
        cir.setReturnValue(registryTracker);
    }

    @Inject(method = "method_28517", at = @At("RETURN"), cancellable = true)
    private static void addGCDimOptions(long seed, CallbackInfoReturnable<SimpleRegistry<DimensionOptions>> cir) {
        cir.setReturnValue(GalacticraftDimensions.addGCDimOptions(seed, cir.getReturnValue()));
    }
}
