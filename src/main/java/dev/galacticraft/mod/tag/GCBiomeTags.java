package dev.galacticraft.mod.tag;

import dev.galacticraft.mod.Constant;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class GCBiomeTags {
    public static final TagKey<Biome> MOON =
            TagKey.create(
                    Registries.BIOME,
                    Constant.id("moon")
            );

    public static final TagKey<Biome> VENUS =
            TagKey.create(
                    Registries.BIOME,
                    Constant.id("venus")
            );

    public static final TagKey<Biome> ASTEROID =
            TagKey.create(
                    Registries.BIOME,
                    Constant.id("asteroid")
            );

    public static final TagKey<Biome>
            MOON_PILLAGER_BASE_HAS_STRUCTURE =
            TagKey.create(
                    Registries.BIOME,
                    Constant.id(
                            "has_structure/moon_pillager_base"
                    )
            );

    public static final TagKey<Biome>
            MOON_VILLAGE_HIGHLANDS_HAS_STRUCTURE =
            TagKey.create(
                    Registries.BIOME,
                    Constant.id(
                            "has_structure/moon_village_highlands"
                    )
            );

    public static final TagKey<Biome>
            MOON_RUINS_HAS_STRUCTURE =
            TagKey.create(
                    Registries.BIOME,
                    Constant.id(
                            "has_structure/moon_ruins"
                    )
            );

    public static final TagKey<Biome>
            MOON_DUNGEON_HAS_STRUCTURE =
            TagKey.create(
                    Registries.BIOME,
                    Constant.id(
                            "has_structure/moon_dungeon"
                    )
            );

    public static void register() {
    }
}