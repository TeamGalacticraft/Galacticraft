package com.hrznstudio.galacticraft.world.gen.chunk;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftChunkGeneratorTypes {

    public static final ChunkGeneratorType<MoonChunkGeneratorConfig, MoonChunkGenerator> MOON = Registry.register(Registry.CHUNK_GENERATOR_TYPE, "galacticraft-rewoven:moon", new ChunkGeneratorType<>(null, true, MoonChunkGeneratorConfig::new));
    public static final ChunkGeneratorType<MarsChunkGeneratorConfig, MarsChunkGenerator> MARS = Registry.register(Registry.CHUNK_GENERATOR_TYPE, "galacticraft-rewoven:mars", new ChunkGeneratorType<>(null, true, MarsChunkGeneratorConfig::new));

    public static void init() {
    }
}
