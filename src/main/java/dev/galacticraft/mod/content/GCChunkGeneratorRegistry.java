package dev.galacticraft.mod.content;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class GCChunkGeneratorRegistry extends GCRegistry<Codec<? extends ChunkGenerator>> {
    public GCChunkGeneratorRegistry() {
        super(BuiltInRegistries.CHUNK_GENERATOR);
    }
}
