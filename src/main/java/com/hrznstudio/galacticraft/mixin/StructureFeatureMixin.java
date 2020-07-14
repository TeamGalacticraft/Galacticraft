package com.hrznstudio.galacticraft.mixin;

import com.google.common.collect.ImmutableList;
import com.hrznstudio.galacticraft.world.gen.feature.GalacticraftFeatures;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(StructureFeature.class)
public class StructureFeatureMixin {
    @Mutable
    @Shadow @Final public static List<StructureFeature<?>> field_24861 = addGCJugsawStructures();

    private static List<StructureFeature<?>> addGCJugsawStructures() {
        return ImmutableList.<StructureFeature<?>>builder().addAll(StructureFeature.field_24861).add(GalacticraftFeatures.MOON_VILLAGE).build();
    }

    private void abc() {

    }
}
