package com.hrznstudio.galacticraft.world.gen.feature.moonvillage;

import com.hrznstudio.galacticraft.structure.MoonVillageGenerator;
import com.mojang.datafixers.Dynamic;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.VillageStructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
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

import static com.hrznstudio.galacticraft.world.gen.feature.GalacticraftFeatures.MOON_VILLAGE;

public class MoonVillageFeature extends StructureFeature<DefaultFeatureConfig> {
    public MoonVillageFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
        super(configFactory);
    }

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

    public boolean shouldStartAt(BiomeAccess biomeAccess, ChunkGenerator<?> chunkGenerator, Random random, int chunkZ, int i, Biome biome) {
        ChunkPos chunkPos = this.getStart(chunkGenerator, random, chunkZ, i, 0, 0);
        return (chunkZ == chunkPos.x && i == chunkPos.z) && chunkGenerator.hasStructure(biome, this);
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return MoonVillageFeature.Start::new;
    }

    @Override
    public String getName() {
        return Registry.STRUCTURE_FEATURE.getId(MOON_VILLAGE).toString();
    }

    @Override
    public int getRadius() {
        return 8;
    }

    public static class Start extends VillageStructureStart {
        public Start(StructureFeature<?> structureFeature, int chunkX, int chunkZ, BlockBox blockBox, int i, long l) {
            super(structureFeature, chunkX, chunkZ, blockBox, i, l);
        }

        @Override
        public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
            BlockPos blockPos = new BlockPos(x * 16, 0, z * 16);
            MoonVillageGenerator.addPieces(chunkGenerator, structureManager, blockPos, this.children, this.random);
            this.setBoundingBoxFromChildren();
        }
    }
}
