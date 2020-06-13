package com.hrznstudio.galacticraft.mixin;

import com.hrznstudio.galacticraft.world.biome.source.MoonBiomeSource;
import com.hrznstudio.galacticraft.world.dimension.GalacticraftDimensions;
import com.hrznstudio.galacticraft.world.gen.chunk.MoonChunkGenerator;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(GeneratorOptions.class)
public class GeneratorOptionsMixin {

    @Shadow
    public long seed;

    @Inject(method = "getDimensionMap", at = @At(value = "RETURN"), cancellable = true)
    private void addGCDimsToServer(CallbackInfoReturnable<Map<DimensionType, ChunkGenerator>> cir) {
        Map<DimensionType, ChunkGenerator> map = new HashMap<>(cir.getReturnValue());
        map.put(GalacticraftDimensions.MOON, createMoonChunkGenerator(seed));
        cir.setReturnValue(map);
    }

    private static MoonChunkGenerator createMoonChunkGenerator(long seed) {
//        MoonChunkGeneratorConfig moonConfig = new MoonChunkGeneratorConfig(new FlatChunkGeneratorConfig(
//                new StructuresConfig(Optional.empty(), ImmutableMap.of()))
//                .method_29965(
//                        ImmutableList.of(
//                                new FlatChunkGeneratorLayer(1, Blocks.BEDROCK),
//                                new FlatChunkGeneratorLayer(243, Blocks.STONE),
//                                new FlatChunkGeneratorLayer(1, Blocks.BEDROCK)
//                        ),
//                        new StructuresConfig(Optional.empty(), ImmutableMap.of())
//                ));
        //TODO: above code only works client side
        return new MoonChunkGenerator(new MoonBiomeSource(seed, 4), seed, null); //fixme dont pass null
    }
}
