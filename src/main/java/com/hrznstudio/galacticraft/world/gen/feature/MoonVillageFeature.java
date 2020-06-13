package com.hrznstudio.galacticraft.world.gen.feature;

import com.hrznstudio.galacticraft.structure.MoonVillageStart;
import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class MoonVillageFeature extends StructureFeature<StructurePoolFeatureConfig> {

    public MoonVillageFeature(Codec<StructurePoolFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public StructureFeature.StructureStartFactory<StructurePoolFeatureConfig> getStructureStartFactory() {
        return MoonVillageStart::new;
    }

    @Override
    public String getName() {
        return "Moon_Village";
    }
}
