package dev.galacticraft.mod.world.gen.cave.impl;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.world.biome.GCBiomes;
import dev.galacticraft.mod.world.gen.cave.CaveTransitionConfig;
import dev.galacticraft.mod.world.gen.cave.MoonCaveRegistry;
import dev.galacticraft.mod.world.gen.cave.MoonCaveShapeType;
import dev.galacticraft.mod.world.gen.cave.PlanetCave;
import dev.galacticraft.mod.world.gen.cave.shape.PathSolvedBranchingCaveShape;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public class OlivineBranchingCave extends PlanetCave {
    public OlivineBranchingCave() {
        super(
                Constant.id("olivine_branching_cave"),
                MoonCaveShapeType.BRANCHING,
                new PathSolvedBranchingCaveShape(
                        3, 5,
                        2, 4,
                        2, 5,
                        5.5D, 10.5D,
                        3.2D, 6.8D,
                        2.0D, 3.5D,
                        42, 92,
                        -60, -10
                ),
                100,
                0.22F,
                42,
                58,
                -60,
                62,
                GCBlocks.MOON_BASALT.defaultBlockState(),
                GCBlocks.OLIVINE_BLOCK.defaultBlockState(),
                GCBlocks.BUDDING_OLIVINE.defaultBlockState(),
                CaveTransitionConfig.weak(),
                java.util.List.of(),
                0
        );
    }

    public static void register() {
        MoonCaveRegistry.register(new OlivineBranchingCave());
    }

    @Override
    public boolean matchesBiome(Holder<Biome> biome) {
        return biome.is(GCBiomes.Moon.OLIVINE_CAVES);
    }
}