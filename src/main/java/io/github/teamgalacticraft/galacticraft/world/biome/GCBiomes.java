package io.github.teamgalacticraft.galacticraft.world.biome;

import io.github.teamgalacticraft.galacticraft.Constants;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class GCBiomes {

    public static final Biome MOON = Registry.register(Registry.BIOME, new Identifier(Constants.MOD_ID, "moon"), new MoonBiome());

    public static void init() {
    }
}
