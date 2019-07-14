package com.hrznstudio.galacticraft.world.biome.source;

import com.hrznstudio.galacticraft.Constants;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.BiomeSourceType;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftBiomeSourceTypes {

    public static final BiomeSourceType MOON = Registry.register(Registry.BIOME_SOURCE_TYPE, new Identifier(Constants.MOD_ID, "moon"), new BiomeSourceType<>(MoonBiomeSource::new, MoonBiomeSourceConfig::new));

    public static void init() {
    }
}
