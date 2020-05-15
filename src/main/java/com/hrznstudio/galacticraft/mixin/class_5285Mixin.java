package com.hrznstudio.galacticraft.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.hrznstudio.galacticraft.world.biome.source.MoonBiomeSource;
import com.hrznstudio.galacticraft.world.dimension.GalacticraftDimensions;
import com.hrznstudio.galacticraft.world.gen.chunk.MoonChunkGenerator;
import com.hrznstudio.galacticraft.world.gen.chunk.MoonChunkGeneratorConfig;
import net.minecraft.block.Blocks;
import net.minecraft.class_5285;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.CavesChunkGenerator;
import net.minecraft.world.gen.chunk.CavesChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(class_5285.class)
public class class_5285Mixin {
    @Shadow @Final private long field_24526;

    @Inject(method = "method_28031", at = @At(value = "RETURN"), cancellable = true)
    private void addGCDimsToServer(CallbackInfoReturnable<Map<DimensionType, ChunkGenerator>> cir) {
        Map<DimensionType, ChunkGenerator> map = new HashMap<>(cir.getReturnValue());
        map.put(GalacticraftDimensions.MOON, createMoonChunkGenerator(field_24526));
        cir.setReturnValue(map);
    }

    private static MoonChunkGenerator createMoonChunkGenerator(long seed) {
        MoonChunkGeneratorConfig moonConfig = new MoonChunkGeneratorConfig(new ChunkGeneratorConfig());
        moonConfig.setDefaultBlock(Blocks.NETHERRACK.getDefaultState());
        moonConfig.setDefaultFluid(Blocks.LAVA.getDefaultState());
        return new MoonChunkGenerator(new MoonBiomeSource(seed, 4), seed, moonConfig);
    }
}
