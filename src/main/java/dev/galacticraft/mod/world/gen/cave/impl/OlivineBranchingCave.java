package dev.galacticraft.mod.world.gen.cave.impl;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.world.biome.GCBiomes;
import dev.galacticraft.mod.world.gen.cave.*;
import dev.galacticraft.mod.world.gen.cave.shape.PathSolvedBranchingCaveShape;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

public class OlivineBranchingCave extends PlanetCave {
    public OlivineBranchingCave() {
        super(
                Constant.id("olivine_branching_cave"),
                MoonCaveShapeType.BRANCHING,
                new PathSolvedBranchingCaveShape(
                        3, 5,
                        3, 5,
                        2, 4,
                        5.5D, 10.5D,
                        3.2D, 6.8D,
                        1.9D, 3.3D,
                        16, 34,
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
                20
        );
    }

    public static void register() {
        MoonCaveRegistry.register(new OlivineBranchingCave());
    }

    @Override
    public boolean matchesBiome(Holder<Biome> biome) {
        return biome.is(GCBiomes.Moon.OLIVINE_CAVES);
    }

    @Override
    public BlockState wallBlock(CaveZone zone, int x, int y, int z, BlockState current) {
        int layer = y + layerNoise(x, z, 8142, 5);
        int vein = Math.floorMod(blockHash(x >> 2, y >> 1, z >> 2, 9021), 100);

        if (zone == CaveZone.INNER_SHELL) {
            if (vein >= 94) {
                return GCBlocks.BUDDING_OLIVINE.defaultBlockState();
            }

            if (vein >= 82 || Math.floorMod(layer, 19) <= 1) {
                return GCBlocks.OLIVINE_BLOCK.defaultBlockState();
            }

            return GCBlocks.MOON_BASALT.defaultBlockState();
        }

        if (zone == CaveZone.OUTER_SHELL) {
            if (vein >= 97) {
                return GCBlocks.OLIVINE_BLOCK.defaultBlockState();
            }

            return Math.floorMod(layer, 13) < 5
                    ? GCBlocks.MOON_BASALT.defaultBlockState()
                    : GCBlocks.LUNASLATE.defaultBlockState();
        }

        return current;
    }
}