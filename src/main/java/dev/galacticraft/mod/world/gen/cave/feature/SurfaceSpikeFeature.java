package dev.galacticraft.mod.world.gen.cave.feature;

import dev.galacticraft.mod.world.gen.cave.CaveFeature;
import dev.galacticraft.mod.world.gen.cave.CaveFeatureContext;
import dev.galacticraft.mod.world.gen.cave.CaveSampleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class SurfaceSpikeFeature implements CaveFeature {
    private final BlockState block;
    private final int minY;
    private final int chance;
    private final int minHeight;
    private final int maxHeight;
    private final boolean floorOnly;

    public SurfaceSpikeFeature(
            BlockState block,
            int minY,
            int chance,
            int minHeight,
            int maxHeight,
            boolean floorOnly
    ) {
        this.block = block;
        this.minY = minY;
        this.chance = chance;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.floorOnly = floorOnly;
    }

    @Override
    public void decorate(CaveFeatureContext context, BlockPos pos, CaveSampleType type, int hash) {
        if (this.floorOnly && type != CaveSampleType.FLOOR) {
            return;
        }

        if (pos.getY() < this.minY || Math.floorMod(hash, this.chance) != 0) {
            return;
        }

        int heightRange = Math.max(1, this.maxHeight - this.minHeight + 1);
        int height = this.minHeight + Math.floorMod(hash >> 4, heightRange);

        placeSpike(context.chunk(), context.chunkPos(), pos, height);
    }

    private void placeSpike(ChunkAccess chunk, ChunkPos chunkPos, BlockPos start, int height) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int i = 0; i < height; i++) {
            int radius = radiusForLayer(i, height);
            int y = start.getY() + i;

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dz * dz > radius * radius) {
                        continue;
                    }

                    mutable.set(start.getX() + dx, y, start.getZ() + dz);

                    if (insideChunk(chunkPos, mutable) && chunk.getBlockState(mutable).isAir()) {
                        chunk.setBlockState(mutable, this.block, false);
                    }
                }
            }
        }
    }

    private static int radiusForLayer(int layer, int height) {
        double progress = layer / (double) Math.max(1, height - 1);

        if (progress < 0.22D && height >= 9) {
            return 2;
        }

        if (progress < 0.55D && height >= 5) {
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