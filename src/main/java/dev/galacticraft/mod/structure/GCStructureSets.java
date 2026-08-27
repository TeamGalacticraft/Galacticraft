package dev.galacticraft.mod.structure;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.gen.structure.GCStructures;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

import java.util.Optional;

public class GCStructureSets {
    public static final class Moon {
        public static final ResourceKey<StructureSet> PILLAGER_BASE =
                ResourceKey.create(
                        Registries.STRUCTURE_SET,
                        Constant.id("moon_pillager_bases")
                );

        public static final ResourceKey<StructureSet> RUINS =
                ResourceKey.create(
                        Registries.STRUCTURE_SET,
                        Constant.id("moon_ruins")
                );

        public static final ResourceKey<StructureSet> DUNGEON =
                ResourceKey.create(
                        Registries.STRUCTURE_SET,
                        Constant.id("moon_dungeons")
                );
    }

    public static void bootstrapRegistries(
            BootstrapContext<StructureSet> context
    ) {
        HolderGetter<Structure> structureLookup =
                context.lookup(
                        Registries.STRUCTURE
                );

        HolderGetter<StructureSet> structureSetLookup =
                context.lookup(
                        Registries.STRUCTURE_SET
                );

        context.register(
                Moon.RUINS,
                new StructureSet(
                        structureLookup.getOrThrow(
                                GCStructures.Moon.RUINS
                        ),
                        new RandomSpreadStructurePlacement(
                                32,
                                8,
                                RandomSpreadType.LINEAR,
                                38245864
                        )
                )
        );

        context.register(
                Moon.DUNGEON,
                new StructureSet(
                        structureLookup.getOrThrow(
                                GCStructures.Moon.DUNGEON
                        ),
                        new RandomSpreadStructurePlacement(
                                64,
                                24,
                                RandomSpreadType.LINEAR,
                                104729
                        )
                )
        );

        context.register(
                Moon.PILLAGER_BASE,
                new StructureSet(
                        structureLookup.getOrThrow(
                                GCStructures.Moon.PILLAGER_BASE
                        ),
                        new RandomSpreadStructurePlacement(
                                Vec3i.ZERO,
                                StructurePlacement
                                        .FrequencyReductionMethod
                                        .LEGACY_TYPE_1,
                                0.1F,
                                5927643,
                                Optional.of(
                                        new StructurePlacement.ExclusionZone(
                                                structureSetLookup.getOrThrow(
                                                        BuiltinStructureSets.VILLAGES
                                                ),
                                                10
                                        )
                                ),
                                32,
                                8,
                                RandomSpreadType.LINEAR
                        )
                )
        );
    }
}