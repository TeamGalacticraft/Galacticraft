package dev.galacticraft.mod.content;

import com.mojang.serialization.Codec;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.gen.custom.AsteroidChunkGenerator;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.storage.DimensionDataStorage;


public class GCChunkGenerator {
    public static final GCChunkGeneratorRegistry CHUNK_GENERATOR = new GCChunkGeneratorRegistry();


    public static final Codec<AsteroidChunkGenerator> ASTEROIDS = CHUNK_GENERATOR.register("asteroid_chunk_generator", AsteroidChunkGenerator.CODEC);

    public static <T extends ChunkGenerator> Codec<T> register(String id, Codec<T> codec) {
        return Registry.register(BuiltInRegistries.CHUNK_GENERATOR, Constant.id(id), codec);
    }

    public static void register() {

    }

}
