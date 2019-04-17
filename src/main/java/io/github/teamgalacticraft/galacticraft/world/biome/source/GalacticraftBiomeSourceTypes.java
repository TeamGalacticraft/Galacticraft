package io.github.teamgalacticraft.galacticraft.world.biome.source;

import io.github.teamgalacticraft.galacticraft.Constants;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.BiomeSourceType;

public class GalacticraftBiomeSourceTypes {

    public static final BiomeSourceType MOON = Registry.register(Registry.BIOME_SOURCE_TYPE, new Identifier(Constants.MOD_ID, "moon"), new BiomeSourceType<>(MoonBiomeSource::new, MoonBiomeSourceConfig::new));

    public static void init() {
    }
}
