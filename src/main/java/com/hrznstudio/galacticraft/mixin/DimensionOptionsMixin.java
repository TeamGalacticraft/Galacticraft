package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.Constants;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashSet;

@Mixin(DimensionOptions.class)
public class DimensionOptionsMixin {
    @Shadow @Final private static LinkedHashSet<RegistryKey<DimensionOptions>> BASE_DIMENSIONS;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void addDimOptionsGC(CallbackInfo ci) {
        BASE_DIMENSIONS.add(RegistryKey.of(Registry.DIMENSION_OPTIONS, new Identifier(Constants.MOD_ID, "moon")));
    }
}
