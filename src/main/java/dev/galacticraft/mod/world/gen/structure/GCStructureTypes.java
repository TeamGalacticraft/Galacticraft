package dev.galacticraft.mod.world.gen.structure;

import dev.galacticraft.mod.content.GCRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class GCStructureTypes {
    public static final GCRegistry<StructureType<?>> STRUCTURES =
            new GCRegistry<>(
                    BuiltInRegistries.STRUCTURE_TYPE
            );

    public static final StructureType<MoonRuinsStructure>
            MOON_RUINS =
            STRUCTURES.register(
                    "moon_ruins",
                    () -> MoonRuinsStructure.CODEC
            );

    public static final StructureType<LunarDungeonStructure>
            LUNAR_DUNGEON =
            STRUCTURES.register(
                    "moon_dungeon",
                    () -> LunarDungeonStructure.CODEC
            );

    public static void register() {
    }
}