package dev.galacticraft.mod.world.gen.cave.impl;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.world.biome.GCBiomes;
import dev.galacticraft.mod.world.gen.cave.*;
import dev.galacticraft.mod.world.gen.cave.shape.PathSolvedLavaTubeCaveShape;
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
                new PathSolvedLavaTubeCaveShape(
                        3, 5,
                        3, 5,
                        3, 7,
                        12, 30,
                        1.8D, 3.3D,
                        7.0D, 20.0D,
                        34, 78,
                        -10, 10
                ),
                100,
                0.42F,
                64,
                78,
                -16,
                82,
                Blocks.LIGHT_BLUE_WOOL.defaultBlockState(),
                Blocks.BLUE_WOOL.defaultBlockState(),
                Blocks.WHITE_WOOL.defaultBlockState(),
                CaveTransitionConfig.weak(),
                java.util.List.of(),
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
            if (frost >= 92) {
                return Blocks.BLUE_ICE.defaultBlockState();
            }

            if (frost >= 72) {
                return Blocks.PACKED_ICE.defaultBlockState();
            }

            if (Math.floorMod(layer, 15) <= 2) {
                return Blocks.ICE.defaultBlockState();
            }

            return GCBlocks.MOON_BASALT.defaultBlockState();
        }

        if (zone == CaveZone.OUTER_SHELL) {
            if (frost >= 94) {
                return Blocks.PACKED_ICE.defaultBlockState();
            }

            return Math.floorMod(layer, 17) < 7
                    ? GCBlocks.LUNASLATE.defaultBlockState()
                    : GCBlocks.MOON_BASALT.defaultBlockState();
        }

        return current;
    }

    @Override
    public void decorate(CaveFeatureContext context, BlockPos pos, CaveSampleType type, int hash) {
        ChunkAccess chunk = context.chunk();
        ChunkPos chunkPos = context.chunkPos();
        boolean surfaceLike = pos.getY() >= 58;

        if (type == CaveSampleType.CEILING) {
            int chance = surfaceLike ? 13 : 42;

            if (Math.floorMod(hash, chance) != 0) {
                return;
            }

            int height = surfaceLike
                    ? 5 + Math.floorMod(hash >> 4, 8)
                    : 2 + Math.floorMod(hash >> 4, 4);

            placeSpike(chunk, chunkPos, pos, -1, height, surfaceLike);
            return;
        }

        if (type == CaveSampleType.FLOOR) {
            int chance = surfaceLike ? 11 : 64;

            if (Math.floorMod(hash, chance) != 0) {
                return;
            }

            int height = surfaceLike
                    ? 7 + Math.floorMod(hash >> 5, 10)
                    : 2 + Math.floorMod(hash >> 5, 3);

            placeSpike(chunk, chunkPos, pos, 1, height, surfaceLike);
        }
    }

    @Override
    public boolean paintsSurface() {
        return true;
    }

    @Override
    public BlockState surfaceBlock(int x, int y, int z, BlockState currentSurface) {
        int frost = Math.floorMod(blockHash(x >> 3, y >> 1, z >> 3, 8711), 100);

        if (frost >= 75) {
            return Blocks.SNOW_BLOCK.defaultBlockState();
        }

        return Blocks.POWDER_SNOW.defaultBlockState();
    }

    private BlockState spikeBlock() {
        return Blocks.PACKED_ICE.defaultBlockState();
    }

    private void placeSpike(ChunkAccess chunk, ChunkPos chunkPos, BlockPos start, int yDir, int height, boolean surfaceLike) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int i = 0; i < height; i++) {
            int y = start.getY() + i * yDir;
            int radius = radiusForSpikeLayer(i, height, surfaceLike);

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dz * dz > radius * radius) {
                        continue;
                    }

                    mutable.set(start.getX() + dx, y, start.getZ() + dz);

                    if (insideChunk(chunkPos, mutable) && chunk.getBlockState(mutable).isAir()) {
                        chunk.setBlockState(mutable, this.spikeBlock(), false);
                    }
                }
            }
        }
    }

    private static int radiusForSpikeLayer(int layer, int height, boolean surfaceLike) {
        if (!surfaceLike) {
            return layer == 0 && height >= 5 ? 1 : 0;
        }

        double progress = layer / (double) Math.max(1, height - 1);

        if (progress < 0.25D && height >= 11) {
            return 2;
        }

        if (progress < 0.58D && height >= 7) {
            return 1;
        }

        return 0;
    }

    private static boolean insideChunk(ChunkPos chunkPos, BlockPos pos) {
        return pos.getX() >= chunkPos.getMinBlockX()
                && pos.getX() <= chunkPos.getMaxBlockX()
                && pos.getZ() >= chunkPos.getMinBlockZ()
                && pos.getZ() <= chunkPos.getMaxBlockZ();
    }
}