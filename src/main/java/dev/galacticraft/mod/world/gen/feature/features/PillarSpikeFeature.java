package dev.galacticraft.mod.world.gen.feature.features;

import com.mojang.serialization.Codec;
import dev.galacticraft.mod.content.GCBlocks;
import dev.galacticraft.mod.world.dimension.MoonConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.*;

public class PillarSpikeFeature extends Feature<NoneFeatureConfiguration> {
    private final BlockState material;
    private final int attempts;

    public PillarSpikeFeature(Codec<NoneFeatureConfiguration> codec, BlockState material, int attempts) {
        super(codec);
        this.material = material;
        this.attempts = attempts;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        boolean placed = false;

        for (int i = 0; i < this.attempts; i++) {
            int x = origin.getX() + random.nextInt(16);
            int z = origin.getZ() + random.nextInt(16);
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, origin.getY(), z);

            // Find the floor
            while (pos.getY() > level.getMinBuildHeight() + 4 && level.getBlockState(pos).isAir()) {
                pos.move(Direction.DOWN);
            }

            if (pos.getY() <= level.getMinBuildHeight() + 4) continue;

            pos.move(Direction.UP);
            placed |= generatePillar(level, pos, random);
        }

        return placed;
    }

    private boolean generatePillar(WorldGenLevel level, BlockPos base, RandomSource random) {
        boolean placed = false;
        int height = 6 + random.nextInt(6); // 6–11 blocks tall
        int maxRadius = 2 + random.nextInt(2); // base radius 2–3

        BlockPos.MutableBlockPos pos = base.mutable();
        for (int y = 0; y < height; y++) {
            pos.set(base.getX(), base.getY() + y, base.getZ());

            // Stop if we hit something solid
            if (!level.getBlockState(pos).isAir() && !level.getBlockState(pos).is(GCBlocks.OLIVINE_CLUSTER)) break;

            float radius = maxRadius * (1f - (y / (float) height)); // taper
            placed |= fillCircle(level, pos, radius, random);

            // Random side bulges
            if (random.nextFloat() < 0.3f) {
                Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                BlockPos side = pos.relative(dir);
                if (level.getBlockState(side).isAir()) {
                    level.setBlock(side, material, 2);
                    placed = true;
                }
            }
        }

        // Add ground buds around base
        for (int i = 0; i < 5; i++) {
            BlockPos budPos = base.offset(random.nextInt(5) - 2, 0, random.nextInt(5) - 2);
            if (level.getBlockState(budPos).isAir()) {
                level.setBlock(budPos, material, 2);
                placed = true;
            }
        }

        return placed;
    }

    private boolean fillCircle(WorldGenLevel level, BlockPos center, float radius, RandomSource random) {
        boolean placed = false;
        int intRadius = Math.round(radius);

        for (int dx = -intRadius; dx <= intRadius; dx++) {
            for (int dz = -intRadius; dz <= intRadius; dz++) {
                if ((dx * dx + dz * dz) <= radius * radius + random.nextFloat() * 1.5f) {
                    BlockPos pos = center.offset(dx, 0, dz);
                    if (level.getBlockState(pos).isAir()) {
                        level.setBlock(pos, material, 2);
                        placed = true;
                    }
                }
            }
        }

        return placed;
    }
}