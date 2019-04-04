package io.github.teamgalacticraft.galacticraft.world.biome.source;

import io.github.teamgalacticraft.galacticraft.Constants;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.BiomeSourceConfig;
import net.minecraft.world.biome.source.BiomeSourceType;

import java.util.function.Function;
import java.util.function.Supplier;

public class GCBiomeSourceTypes {

    public static final BiomeSourceType MOON = register("moon", MoonLayeredBiomeSource::new, MoonLayeredBiomeSourceConfig::new);

    private static <C extends BiomeSourceConfig, T extends BiomeSource> BiomeSourceType register(String string_1, Function<C, T> function_1, Supplier<C> supplier_1) {
        return Registry.register(Registry.BIOME_SOURCE_TYPE, new Identifier(Constants.MOD_ID, string_1), new BiomeSourceType<>(function_1, supplier_1));
    }

}
