package dev.galacticraft.mod.world.gen.cave.impl;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.world.biome.GCBiomes;
import dev.galacticraft.mod.world.gen.cave.*;
import dev.galacticraft.mod.world.gen.cave.feature.SurfaceSpikeFeature;
import dev.galacticraft.mod.world.gen.cave.shape.GlacialCavernShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class GlacialLavaTubeCave extends PlanetCave {
    public GlacialLavaTubeCave() {
        super(
                Constant.id("glacial_lava_tube_cave"),
                MoonCaveShapeType.LAVA_TUBE,
                new GlacialCavernShape(),
                100,
                0.38F,
                -48,
                54,
                -60,
                88,
                Blocks.LIGHT_BLUE_WOOL.defaultBlockState(),
                Blocks.BLUE_WOOL.defaultBlockState(),
                Blocks.WHITE_WOOL.defaultBlockState(),
                CaveTransitionConfig.weak(),
                java.util.List.of(
                        new SurfaceSpikeFeature(
                                Blocks.PACKED_ICE.defaultBlockState(),
                                68,
                                9,
                                4,
                                12,
                                true
                        )
                ),
                100
        );
    }

    public static void register() {
        MoonCaveRegistry.register(new GlacialLavaTubeCave());
    }

    @Override
    public boolean matchesBiome(Holder<Biome> biome) {
        return biome.is(GCBiomes.Moon.GLACIAL_CAVERNS);
    }

    @Override
    public BlockState wallBlock(CaveZone zone, int x, int y, int z, BlockState current) {
        int frost = Math.floorMod(blockHash(x >> 2, y >> 1, z >> 2, 6641), 100);
        int layer = y + layerNoise(x, z, 2201, 4);

        if (zone == CaveZone.INNER_SHELL) {
            if (frost >= 94) {
                return Blocks.BLUE_ICE.defaultBlockState();
            }

            if (frost >= 76) {
                return Blocks.PACKED_ICE.defaultBlockState();
            }

            if (Math.floorMod(layer, 15) <= 2) {
                return Blocks.ICE.defaultBlockState();
            }

            return GCBlocks.MOON_BASALT.defaultBlockState();
        }

        if (zone == CaveZone.OUTER_SHELL) {
            if (frost >= 95) {
                return Blocks.PACKED_ICE.defaultBlockState();
            }

            return Math.floorMod(layer, 17) < 7
                    ? GCBlocks.LUNASLATE.defaultBlockState()
                    : GCBlocks.MOON_BASALT.defaultBlockState();
        }

        return current;
    }

    @Override
    public boolean paintsSurface() {
        return true;
    }

    @Override
    public BlockState surfaceBlock(int x, int y, int z, BlockState currentSurface) {
        int frost = Math.floorMod(blockHash(x >> 3, y >> 1, z >> 3, 8711), 100);
        return frost >= 75 ? Blocks.SNOW_BLOCK.defaultBlockState() : Blocks.POWDER_SNOW.defaultBlockState();
    }
}