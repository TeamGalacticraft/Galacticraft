package com.hrznstudio.galacticraft.mixin;

import com.google.common.collect.ImmutableMap;
import com.hrznstudio.galacticraft.world.gen.feature.GalacticraftFeatures;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructuresConfig.class)
public class StructuresConfigMixin {
    @Mutable @Shadow @Final public static ImmutableMap<StructureFeature<?>, StructureConfig> DEFAULT_STRUCTURES;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/util/registry/Registry;STRUCTURE_FEATURE:Lnet/minecraft/util/registry/Registry;"))
    private static void addGCStructures(CallbackInfo ci) {
        DEFAULT_STRUCTURES = ImmutableMap.<StructureFeature<?>, StructureConfig>builder().putAll(DEFAULT_STRUCTURES).put(GalacticraftFeatures.MOON_VILLAGE, new StructureConfig(32, 8, 8426492)).build();
    }
}
