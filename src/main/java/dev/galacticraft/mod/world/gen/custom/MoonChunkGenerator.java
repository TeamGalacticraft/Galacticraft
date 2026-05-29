package dev.galacticraft.mod.world.gen.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.Galacticraft;
import dev.galacticraft.mod.world.dimension.MoonConstants;
import dev.galacticraft.mod.world.gen.PlanetChunkGenerator;
import dev.galacticraft.mod.world.gen.cave.MoonCaveChunkGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.function.Function;

public class MoonChunkGenerator extends PlanetChunkGenerator {
    public static final MapCodec<MoonChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(MoonChunkGenerator::getBiomeSource),
            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(MoonChunkGenerator::generatorSettings)
    ).apply(instance, MoonChunkGenerator::new));

    public MoonChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(biomeSource, settings);
    }

    @Override
    protected MapCodec<? extends MoonChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    protected void applyPlanetCarvers(
            WorldGenRegion region,
            long seed,
            RandomState randomState,
            BiomeManager biomeManager,
            StructureManager structureManager,
            ChunkAccess chunkAccess,
            GenerationStep.Carving carving
    ) {
        if (carving != GenerationStep.Carving.AIR) {
            return;
        }

        MoonCaveChunkGenerator.generate(
                chunkAccess,
                randomState,
                MoonConstants.Dimension.MIN_DIMENSION_HEIGHT,
                MoonConstants.Dimension.MAX_DIMENSION_HEIGHT - 1,
                biomeManager::getBiome
        );
    }
}