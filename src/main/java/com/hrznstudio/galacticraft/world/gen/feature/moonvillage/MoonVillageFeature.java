package com.hrznstudio.galacticraft.world.gen.feature.moonvillage;

import com.hrznstudio.galacticraft.structure.MoonVillageStart;
import com.hrznstudio.galacticraft.world.gen.feature.GalacticraftFeatures;
import com.mojang.datafixers.Dynamic;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.Random;
import java.util.function.Function;

public class MoonVillageFeature extends StructureFeature<DefaultFeatureConfig> {

    public MoonVillageFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    protected ChunkPos getStart(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
        int m = chunkGenerator.getConfig().getVillageDistance();
        int n = chunkGenerator.getConfig().getVillageSeparation();
        int o = i + m * k;
        int p = j + m * l;
        int q = o < 0 ? o - m + 1 : o;
        int r = p < 0 ? p - m + 1 : p;
        int s = q / m;
        int t = r / m;
        ((ChunkRandom) random).setStructureSeed(chunkGenerator.getSeed(), s, t, 10387312);
        s *= m;
        t *= m;
        s += random.nextInt(m - n);
        t += random.nextInt(m - n);
        return new ChunkPos(s, t);
    }

    @Override
    public boolean shouldStartAt(BiomeAccess biomeAccess, ChunkGenerator<?> chunkGenerator, Random random, int chunkZ, int i, Biome biome) {
        ChunkPos chunkPos = this.getStart(chunkGenerator, random, chunkZ, i, 0, 0);
        return (chunkZ == chunkPos.x && i == chunkPos.z) && chunkGenerator.hasStructure(biome, this);
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return MoonVillageStart::new;
    }

    @Override
    public String getName() {
        return Registry.STRUCTURE_FEATURE.getId(GalacticraftFeatures.MOON_VILLAGE).toString();
    }

    @Override
    public int getRadius() {
        return 8;
    }

}
