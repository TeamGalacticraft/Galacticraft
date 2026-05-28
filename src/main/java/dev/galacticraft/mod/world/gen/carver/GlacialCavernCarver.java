package dev.galacticraft.mod.world.gen.carver;

import com.mojang.serialization.Codec;
import dev.galacticraft.mod.tag.GCBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;

import java.util.function.Function;

public class GlacialCavernCarver extends CaveWorldCarver {
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();

    private static final BlockState MAIN_ICE = Blocks.LIGHT_BLUE_WOOL.defaultBlockState();
    private static final BlockState FROST = Blocks.WHITE_WOOL.defaultBlockState();
    private static final BlockState DEEP_ICE = Blocks.BLUE_WOOL.defaultBlockState();
    private static final BlockState CRYSTAL_ICE = Blocks.CYAN_WOOL.defaultBlockState();

    public GlacialCavernCarver(Codec<CaveCarverConfiguration> codec) {
        super(codec);
    }

    @Override
    protected int getCaveBound() {
        return 10;
    }

    @Override
    protected float getThickness(RandomSource random) {
        if (random.nextInt(9) == 0) {
            return 3.5F + random.nextFloat() * 2.5F;
        }

        return 0.9F + random.nextFloat() * 1.2F;
    }

    @Override
    protected boolean carveEllipsoid(
            CarvingContext context,
            CaveCarverConfiguration configuration,
            ChunkAccess chunk,
            Function<BlockPos, Holder<Biome>> posToBiome,
            Aquifer aquifer,
            double x,
            double y,
            double z,
            double width,
            double height,
            CarvingMask mask,
            CarveSkipChecker carveSkipChecker
    ) {
        ChunkPos chunkPos = chunk.getPos();
        RandomSource random = context.randomState().aquiferRandom().at((int) Math.round(x), (int) Math.round(y), (int) Math.round(z));

        double middleX = chunkPos.getMiddleBlockX();
        double middleZ = chunkPos.getMiddleBlockZ();
        double range = 16.0D + width * 2.0D;

        if (Math.abs(x - middleX) > range || Math.abs(z - middleZ) > range) {
            return false;
        }

        int minChunkX = chunkPos.getMinBlockX();
        int minChunkZ = chunkPos.getMinBlockZ();

        double shellWidth = width * 1.8D;

        int localMinX = Math.max(Mth.floor(x - shellWidth) - minChunkX - 2, 0);
        int localMaxX = Math.min(Mth.floor(x + shellWidth) - minChunkX + 2, 15);
        int localMinZ = Math.max(Mth.floor(z - shellWidth) - minChunkZ - 2, 0);
        int localMaxZ = Math.min(Mth.floor(z + shellWidth) - minChunkZ + 2, 15);

        int minY = Math.max(Mth.floor(y - height) - 2, context.getMinGenY() + 1);
        int surfacePadding = chunk.isUpgrading() ? 0 : 7;
        int maxY = Math.min(Mth.floor(y + height) + 2, context.getMinGenY() + context.getGenDepth() - 1 - surfacePadding);

        boolean carvedAny = false;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        double widened = width;
        double tallened = height;

        for (int localX = localMinX; localX <= localMaxX; localX++) {
            int blockX = chunkPos.getBlockX(localX);
            double normalizedX = (blockX + 0.5D - x) / widened;

            for (int localZ = localMinZ; localZ <= localMaxZ; localZ++) {
                int blockZ = chunkPos.getBlockZ(localZ);
                double normalizedZ = (blockZ + 0.5D - z) / widened;

                for (int blockY = maxY; blockY > minY; blockY--) {
                    double normalizedY = (blockY - 0.5D - y) / tallened;
                    double distance = normalizedX * normalizedX + normalizedY * normalizedY + normalizedZ * normalizedZ;

                    mutable.set(blockX, blockY, blockZ);
                    BlockState current = chunk.getBlockState(mutable);

                    if (distance < 0.82D) {
                        if (!carveSkipChecker.shouldSkip(context, normalizedX, normalizedY, normalizedZ, blockY) && !mask.get(localX, blockY, localZ)) {
                            mask.set(localX, blockY, localZ);
                            chunk.setBlockState(mutable, AIR, true);
                            carvedAny = true;
                        }
                    } else if (distance < 0.94D) {
                        if (canFreeze(current)) {
                            chunk.setBlockState(mutable, pickInnerShell(random), true);
                        }
                    } else if (distance < 1.35D) {
                        if (canFreeze(current)) {
                            chunk.setBlockState(mutable, pickOuterShell(random), true);
                        }
                    } else if (distance < 1.8D) {
                        if (canFreeze(current) && random.nextFloat() < 0.35F) {
                            chunk.setBlockState(mutable, DEEP_ICE, true);
                        }
                    }
                }
            }
        }

        return carvedAny;
    }

    private static boolean canFreeze(BlockState state) {
        return !state.isAir()
                && (state.is(GCBlockTags.MOON_CARVER_REPLACEABLES)
                || state.is(Blocks.LIGHT_BLUE_WOOL)
                || state.is(Blocks.WHITE_WOOL)
                || state.is(Blocks.BLUE_WOOL)
                || state.is(Blocks.CYAN_WOOL));
    }

    private static BlockState pickInnerShell(RandomSource random) {
        float roll = random.nextFloat();

        if (roll < 0.08F) {
            return CRYSTAL_ICE;
        }

        if (roll < 0.35F) {
            return FROST;
        }

        return MAIN_ICE;
    }

    private static BlockState pickOuterShell(RandomSource random) {
        float roll = random.nextFloat();

        if (roll < 0.12F) {
            return FROST;
        }

        if (roll < 0.28F) {
            return DEEP_ICE;
        }

        return MAIN_ICE;
    }
}