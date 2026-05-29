package dev.galacticraft.mod.world.gen.cave.impl;

import dev.galacticraft.mod.Constant;
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
                        5, 8,
                        3, 6,
                        8, 16,
                        12, 34,
                        2.0D, 4.0D,
                        7.0D, 24.0D,
                        36, 96,
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
                CaveTransitionConfig.weak()
        );
    }

    public static void register() {
        MoonCaveRegistry.register(new GlacialLavaTubeCave());
    }

    @Override
    public boolean matchesBiome(Holder<Biome> biome) {
        return biome.is(GCBiomes.Moon.GLACIAL_CAVERNS);
    }

    private BlockState spikeBlock() {
        return Blocks.CYAN_WOOL.defaultBlockState();
    }

    @Override
    public void decorate(
            ChunkAccess chunk,
            ChunkPos chunkPos,
            BlockPos pos,
            CaveSampleType type,
            int hash
    ) {
        boolean surfaceLike = isSurfaceLikeOpening(pos);

        if (type == CaveSampleType.CEILING) {
            int chance = surfaceLike ? 10 : 32;

            if (Math.floorMod(hash, chance) != 0) {
                return;
            }

            int height = surfaceLike
                    ? 6 + Math.floorMod(hash >> 4, 9)
                    : 2 + Math.floorMod(hash >> 4, 5);

            placeSpike(chunk, chunkPos, pos, -1, height, surfaceLike);
            return;
        }

        if (type == CaveSampleType.FLOOR) {
            int chance = surfaceLike ? 8 : 52;

            if (Math.floorMod(hash, chance) != 0) {
                return;
            }

            int height = surfaceLike
                    ? 8 + Math.floorMod(hash >> 5, 12)
                    : 2 + Math.floorMod(hash >> 5, 4);

            placeSpike(chunk, chunkPos, pos, 1, height, surfaceLike);
        }
    }

    private boolean isSurfaceLikeOpening(BlockPos pos) {
        return pos.getY() >= 58;
    }

    private void placeSpike(
            ChunkAccess chunk,
            ChunkPos chunkPos,
            BlockPos start,
            int yDir,
            int height,
            boolean surfaceLike
    ) {
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

        if (progress < 0.22D && height >= 12) {
            return 2;
        }

        if (progress < 0.55D && height >= 8) {
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

    @Override
    public boolean paintsSurface() {
        return true;
    }

    @Override
    public BlockState surfaceBlock(int x, int y, int z, BlockState currentSurface) {
        return Blocks.RED_WOOL.defaultBlockState();
    }
}