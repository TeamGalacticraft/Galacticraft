package com.hrznstudio.galacticraft.world.gen.feature.moonvillage;

import com.hrznstudio.galacticraft.structure.MoonVillageStart;
import com.mojang.datafixers.Dynamic;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.function.Function;

public class MoonVillageFeature extends StructureFeature<DefaultFeatureConfig> {

    public MoonVillageFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

    protected int getSpacing(DimensionType dimensionType, ChunkGeneratorConfig chunkGeneratorConfig) {
        return chunkGeneratorConfig.getVillageDistance();
    }

    protected int getSeparation(DimensionType dimensionType, ChunkGeneratorConfig chunkGenerationConfig) {
        return chunkGenerationConfig.getVillageSeparation();
    }

    protected int getSeedModifier(ChunkGeneratorConfig chunkGeneratorConfig) {
        return 312378912;
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return MoonVillageStart::new;
    }

    @Override
    public String getName() {
        return "Moon_Village";
    }

    @Override
    public int getRadius() {
        return 8;
    }

}
