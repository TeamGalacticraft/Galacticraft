package dev.galacticraft.mod.world.gen.cave.impl;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.world.biome.GCBiomes;
import dev.galacticraft.mod.world.gen.cave.CaveTransitionConfig;
import dev.galacticraft.mod.world.gen.cave.MoonCaveRegistry;
import dev.galacticraft.mod.world.gen.cave.MoonCaveShapeType;
import dev.galacticraft.mod.world.gen.cave.PlanetCave;
import dev.galacticraft.mod.world.gen.cave.shape.PathSolvedBranchingCaveShape;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;

public class CheeseBranchingCave extends PlanetCave {
    public CheeseBranchingCave() {
        super(
                Constant.id("cheese_branching_cave"),
                MoonCaveShapeType.BRANCHING,
                new PathSolvedBranchingCaveShape(
                        4, 7,
                        2, 5,
                        5, 12,
                        4.2D, 8.5D,
                        3.0D, 6.0D,
                        2.0D, 3.8D,
                        70, 170,
                        -60, -50
                ),
                60,
                0.20F,
                -4,
                8,
                -60,
                24,
                Blocks.YELLOW_WOOL.defaultBlockState(),
                Blocks.ORANGE_WOOL.defaultBlockState(),
                Blocks.GOLD_BLOCK.defaultBlockState(),
                CaveTransitionConfig.weak()
        );
    }

    public static void register() {
        MoonCaveRegistry.register(new CheeseBranchingCave());
    }

    @Override
    public boolean matchesBiome(Holder<Biome> biome) {
        return biome.is(GCBiomes.Moon.CHEESE_CAVES);
    }
}