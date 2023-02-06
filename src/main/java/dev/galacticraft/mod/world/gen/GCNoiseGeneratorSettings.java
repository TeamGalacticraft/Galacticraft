package dev.galacticraft.mod.world.gen;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.world.gen.surfacebuilder.MoonSurfaceRules;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class GCNoiseGeneratorSettings {
    public static final ResourceKey<NoiseGeneratorSettings> MOON = key("moon");

    public static void bootstrapRegistries(BootstapContext<NoiseGeneratorSettings> context) {
        HolderGetter<DensityFunction> densityLookup = context.lookup(Registries.DENSITY_FUNCTION);
        HolderGetter<NormalNoise.NoiseParameters> noiseLookup = context.lookup(Registries.NOISE);

        context.register(MOON, new NoiseGeneratorSettings(
                NoiseSettings.create(-64, 384, 1, 2),
                GCBlocks.MOON_ROCK.defaultBlockState(),
                Blocks.WATER.defaultBlockState(),
                NoiseRouterData.overworld(densityLookup, noiseLookup, false, false),
                MoonSurfaceRules.MOON,
                new OverworldBiomeBuilder().spawnTarget(),
                63,
                false,
                true,
                true,
                false
        ));
    }

    @Contract(value = "_ -> new", pure = true)
    private static @NotNull ResourceKey<NoiseGeneratorSettings> key(String id) {
        return Constant.key(Registries.NOISE_SETTINGS, id);
    }
}
