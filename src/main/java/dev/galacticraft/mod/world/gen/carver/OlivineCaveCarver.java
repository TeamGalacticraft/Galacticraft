/*
 * Copyright (c) 2019-2025 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.world.gen.carver;

import com.mojang.serialization.Codec;
import dev.galacticraft.mod.content.GCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class OlivineCaveCarver extends CaveWorldCarver {
    private static final BlockState OLIVINE_BLOCK = GCBlocks.OLIVINE_BLOCK.defaultBlockState();
    private static final BlockState BUDDING_OLIVINE = Blocks.BUDDING_AMETHYST.defaultBlockState(); //placeholder
    private static final BlockState OLIVINE_CLUSTER = Blocks.AMETHYST_CLUSTER.defaultBlockState(); //placeholder

    public OlivineCaveCarver(Codec<CaveCarverConfiguration> codec) {
        super(codec);
    }

    private final List<BlockPos> caveSurfaceBlocks = new ArrayList<>();

    @Override
    protected boolean carveBlock(CarvingContext context, CaveCarverConfiguration config, @NotNull ChunkAccess chunk, Function<BlockPos, Holder<Biome>> posToBiome, CarvingMask carvingMask, BlockPos.MutableBlockPos pos, BlockPos.MutableBlockPos belowPos, Aquifer aquiferSampler, MutableBoolean reachedSurface) {
        BlockState originalState = chunk.getBlockState(pos);
        if (!this.canReplaceBlock(config, originalState) && !isDebugEnabled(config)) {
            return false;
        }

        BlockState newState = getState(context, config, pos, aquiferSampler);
        if (newState == null) return false;

        chunk.setBlockState(pos, newState, false);
        carvingMask.set(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
        caveSurfaceBlocks.add(pos.immutable());

        return true;
    }

    @Override
    public boolean carve(CarvingContext context, CaveCarverConfiguration config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> posToBiome, RandomSource random, Aquifer aquiferSampler, ChunkPos chunkPos, CarvingMask carvingMask) {
        caveSurfaceBlocks.clear();
        boolean carved = super.carve(context, config, chunk, posToBiome, random, aquiferSampler, chunkPos, carvingMask);
        applyOlivineLayersAndCrystals(chunk, random);
        return carved;
    }

    private void applyOlivineLayersAndCrystals(ChunkAccess chunk, RandomSource random) {
        for (BlockPos surfacePos : caveSurfaceBlocks) {
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = surfacePos.relative(dir);

                if (!chunk.getBlockState(neighbor).isAir()) continue;

                int thickness = 2 + random.nextInt(2);
                for (int i = 1; i <= thickness; i++) {
                    BlockPos out = surfacePos.relative(dir, i);
                    if (chunk.getBlockState(out).isAir()) continue;

                    // Decide if this block becomes budding olivine or normal olivine
                    boolean exposedToAir = false;
                    for (Direction checkDir : Direction.values()) {
                        if (chunk.getBlockState(out.relative(checkDir)).isAir()) {
                            exposedToAir = true;
                            break;
                        }
                    }

                    if (exposedToAir && random.nextFloat() < 0.15f) { // 15% chance to bud
                        chunk.setBlockState(out, BUDDING_OLIVINE, false);
                        placeOlivineCrystals(chunk, out, random);
                    } else {
                        chunk.setBlockState(out, OLIVINE_BLOCK, false);
                    }
                }
            }
        }
    }

    private void placeOlivineCrystals(ChunkAccess chunk, BlockPos buddingPos, RandomSource random) {
        List<Direction> validFaces = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            if (chunk.getBlockState(buddingPos.relative(dir)).isAir()) {
                validFaces.add(dir);
            }
        }

        int maxCrystals = Math.min(3, validFaces.size());
        if (maxCrystals == 0) return;

        int count = 1 + random.nextInt(maxCrystals);
        for (int i = 0; i < count; i++) {
            int index = random.nextInt(validFaces.size());
            Direction face = validFaces.remove(index); // safely removes without reshuffling
            BlockPos target = buddingPos.relative(face);
            BlockState crystal = OLIVINE_CLUSTER
                    .setValue(BlockStateProperties.FACING, face); // adjust if needed
            chunk.setBlockState(target, crystal, false);
        }
    }

    @Nullable
    private BlockState getState(CarvingContext context, CaveCarverConfiguration config, BlockPos pos, Aquifer sampler) {
        if (pos.getY() <= config.lavaLevel.resolveY(context)) {
            return CAVE_AIR;
        }
        return sampler.computeSubstance(new DensityFunction.SinglePointContext(pos.getX(), pos.getY(), pos.getZ()), 0.0);
    }

    private static boolean isDebugEnabled(CarverConfiguration config) {
        return config.debugSettings.isDebugMode();
    }
}