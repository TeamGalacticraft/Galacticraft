package com.hrznstudio.galacticraft.api.biome;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GalacticraftBiomeProperties {
    public static final BiomePropertyType<Boolean> IS_MARE = Registry.register(Galacticraft.BIOME_PROPERTY_TYPE_REGISTRY, new Identifier(Constants.MOD_ID, "is_mare"),
            new BiomePropertyType.Builder<Boolean>().defaultValue(false).name(new TranslatableText("galacticraft-rewoven.is_moon_mare")).build());

    public static final BiomePropertyType<Boolean> IS_SPACE_BIOME = Registry.register(Galacticraft.BIOME_PROPERTY_TYPE_REGISTRY, new Identifier(Constants.MOD_ID, "is_space"),
            new BiomePropertyType.Builder<Boolean>().defaultValue(false).name(new TranslatableText("galacticraft-rewoven.is_space_biome")).build());

    public static final BiomePropertyType<Boolean> HAS_CRATERS = Registry.register(Galacticraft.BIOME_PROPERTY_TYPE_REGISTRY, new Identifier(Constants.MOD_ID, "has_craters"),
            new BiomePropertyType.Builder<Boolean>().defaultValue(false).name(new TranslatableText("galacticraft-rewoven.has_craters")).build());

    public static final BiomePropertyType<Double> CRATER_CHANCE = Registry.register(Galacticraft.BIOME_PROPERTY_TYPE_REGISTRY, new Identifier(Constants.MOD_ID, "crater_chance"),
            new BiomePropertyType.Builder<Double>().defaultValue(300.0D).name(new TranslatableText("galacticraft-rewoven.crater_chance")).build());

    public static void register() {

    }
}
