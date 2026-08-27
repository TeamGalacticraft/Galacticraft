package dev.galacticraft.mod.tag;

import dev.galacticraft.mod.Constant;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public class GCStructureTags {
    public static final TagKey<Structure> MOON_RUINS =
            TagKey.create(
                    Registries.STRUCTURE,
                    Constant.id("moon_ruins")
            );

    public static final TagKey<Structure> MOON_DUNGEON =
            TagKey.create(
                    Registries.STRUCTURE,
                    Constant.id("moon_dungeon")
            );

    public static void register() {
    }
}