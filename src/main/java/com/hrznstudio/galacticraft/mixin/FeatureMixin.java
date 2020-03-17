package com.hrznstudio.galacticraft.mixin;

import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Mixin(Feature.class)
public abstract class FeatureMixin {
    @Shadow
    @Final
    @Mutable
    public static List<StructureFeature<?>> JIGSAW_STRUCTURES = createJigsawList();

    private static List<StructureFeature<?>> createJigsawList() {
        assert JIGSAW_STRUCTURES != null;
        return new ArrayList<>(JIGSAW_STRUCTURES);
    }
}
