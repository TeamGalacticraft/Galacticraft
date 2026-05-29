package dev.galacticraft.mod.world.gen.carver;

import com.mojang.serialization.Codec;
import dev.galacticraft.mod.world.gen.carver.config.CraterCarverConfig;
import dev.galacticraft.mod.world.gen.structure.GCStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

import java.util.function.Function;

public class CraterCarver extends WorldCarver<CraterCarverConfig> {
    public CraterCarver(Codec<CraterCarverConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean carve(
            CarvingContext context,
            CraterCarverConfig config,
            ChunkAccess chunk,
            Function<BlockPos, Holder<Biome>> posToBiome,
            RandomSource random,
            Aquifer aquiferSampler,
            ChunkPos pos,
            CarvingMask carvingMask
    ) {
        if (!chunk.getReferencesForStructure(context.registryAccess().registryOrThrow(Registries.STRUCTURE).getOrThrow(GCStructures.Moon.VILLAGE)).isEmpty()) {
            return false;
        }

        int centerY = config.y.sample(random, context);
        BlockPos craterCenter = pos.getBlockAt(random.nextInt(16), centerY, random.nextInt(16));

        double radius = config.minRadius + random.nextDouble() * (config.maxRadius - config.minRadius);

        if ((random.nextBoolean() && radius < config.minRadius + config.idealRangeOffset) || radius > config.maxRadius - config.idealRangeOffset) {
            radius = config.minRadius + random.nextDouble() * (config.maxRadius - config.minRadius);
        }

        double depthMultiplier = 1.0D - ((random.nextDouble() - 0.5D) * 0.3D);
        boolean fresh = random.nextInt(16) == 1;
        boolean changed = false;

        int minGenY = context.getMinGenY() + 1;
        int maxGenY = context.getMinGenY() + context.getGenDepth() - 2;

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos belowCheck = new BlockPos.MutableBlockPos();

        for (int localX = 0; localX < 16; localX++) {
            int worldX = chunk.getPos().getBlockX(localX);

            for (int localZ = 0; localZ < 16; localZ++) {
                int worldZ = chunk.getPos().getBlockZ(localZ);

                double xDev = Math.abs(worldX - craterCenter.getX());
                double zDev = Math.abs(worldZ - craterCenter.getZ());

                if (xDev >= radius || zDev >= radius) {
                    continue;
                }

                double normalizedX = xDev / radius;
                double normalizedZ = zDev / radius;
                double distance = normalizedX * normalizedX + normalizedZ * normalizedZ;

                if (distance >= 1.0D) {
                    continue;
                }

                double yCurve = distance * distance * 6.0D;
                double craterDepth = (5.0D - yCurve) * depthMultiplier;

                if (craterDepth <= 0.0D) {
                    continue;
                }

                int toDig = (int) craterDepth;

                if (toDig >= 1) {
                    toDig++;
                    if (fresh) {
                        toDig++;
                    }
                }

                int surfaceY = findSurfaceY(chunk, carvingMask, localX, localZ, worldX, worldZ, centerY, minGenY, maxGenY, mutable);

                if (surfaceY <= minGenY) {
                    continue;
                }

                BlockState topState = chunk.getBlockState(mutable.set(worldX, surfaceY, worldZ));

                for (int dug = 0; dug < toDig; dug++) {
                    int y = surfaceY - dug;

                    if (y <= minGenY || y >= maxGenY) {
                        break;
                    }

                    if (!isValidMaskY(context, y)) {
                        continue;
                    }

                    mutable.set(worldX, y, worldZ);
                    BlockState state = chunk.getBlockState(mutable);

                    if (state.isAir() && dug == 0) {
                        continue;
                    }

                    chunk.setBlockState(mutable, AIR, false);
                    carvingMask.set(localX, y, localZ);
                    changed = true;

                    if (!fresh && dug + 1 >= toDig) {
                        int replacementY = y - 1;

                        if (replacementY > minGenY && isValidMaskY(context, replacementY)) {
                            belowCheck.set(worldX, replacementY - 1, worldZ);

                            if (!chunk.getBlockState(belowCheck).isAir()) {
                                mutable.set(worldX, replacementY, worldZ);
                                chunk.setBlockState(mutable, topState, false);
                            }
                        }
                    }
                }
            }
        }

        return changed;
    }

    private static int findSurfaceY(
            ChunkAccess chunk,
            CarvingMask carvingMask,
            int localX,
            int localZ,
            int worldX,
            int worldZ,
            int startY,
            int minY,
            int maxY,
            BlockPos.MutableBlockPos mutable
    ) {
        int y = Math.max(minY, Math.min(maxY, startY));

        mutable.set(worldX, y, worldZ);
        BlockState top = chunk.getBlockState(mutable);
        BlockState above = chunk.getBlockState(mutable.move(Direction.UP));

        while (y < maxY && !above.isAir() && !top.isAir() && !safeMaskGet(carvingMask, localX, y, localZ, minY, maxY)) {
            y++;
            top = above;
            mutable.set(worldX, y + 1, worldZ);
            above = chunk.getBlockState(mutable);
        }

        return y;
    }

    private static boolean safeMaskGet(CarvingMask mask, int localX, int y, int localZ, int minY, int maxY) {
        if (y < minY || y > maxY) {
            return false;
        }

        return mask.get(localX, y, localZ);
    }

    private static boolean isValidMaskY(CarvingContext context, int y) {
        return y >= context.getMinGenY() && y < context.getMinGenY() + context.getGenDepth();
    }

    @Override
    public boolean isStartChunk(CraterCarverConfig config, RandomSource random) {
        return random.nextFloat() <= config.probability;
    }
}