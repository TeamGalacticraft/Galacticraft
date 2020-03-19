/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.world.dimension;

import com.hrznstudio.galacticraft.world.biome.source.GalacticraftBiomeSourceTypes;
import com.hrznstudio.galacticraft.world.biome.source.MoonBiomeSourceConfig;
import com.hrznstudio.galacticraft.world.gen.chunk.GalacticraftChunkGeneratorTypes;
import com.hrznstudio.galacticraft.world.gen.chunk.MoonChunkGeneratorConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

import javax.annotation.Nullable;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class MoonDimension extends Dimension {
    public MoonDimension(World world, DimensionType type) {
        super(world, type, 0.0F);
    }

    @Override
    public DimensionType getType() {
        return GalacticraftDimensions.MOON;
    }

    @Override
    public float getCloudHeight() {
        return Float.MIN_VALUE;
    }

    @Override
    public boolean hasSkyLight() {
        return true;
    }

    @Override
    public int getMoonPhase(long time) {
        return 0;
    }

    @Override
    public ChunkGenerator<? extends ChunkGeneratorConfig> createChunkGenerator() {
        MoonChunkGeneratorConfig config = GalacticraftChunkGeneratorTypes.MOON.createSettings();
        MoonBiomeSourceConfig moonBiomeSourceConfig = GalacticraftBiomeSourceTypes.MOON.getConfig(world.getLevelProperties()).setGeneratorSettings(config);
        return GalacticraftChunkGeneratorTypes.MOON.create(this.world, GalacticraftBiomeSourceTypes.MOON.applyConfig(moonBiomeSourceConfig), config);
    }

    @Override
    @Nullable
    public BlockPos getSpawningBlockInChunk(ChunkPos chunkPos, boolean checkMobSpawnValidity) {
        for (int i = chunkPos.getStartX(); i <= chunkPos.getEndX(); ++i) {
            for (int j = chunkPos.getStartZ(); j <= chunkPos.getEndZ(); ++j) {
                BlockPos blockPos = this.getTopSpawningBlockPosition(i, j, checkMobSpawnValidity);
                if (blockPos != null) {
                    return blockPos;
                }
            }
        }

        return null;
    }

    @Nullable
    @Override
    public BlockPos getTopSpawningBlockPosition(int x, int z, boolean checkMobSpawnValidity) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, 0, z);
        Biome biome = this.world.getBiome(mutable);
        BlockState blockState = biome.getSurfaceConfig().getTopMaterial();
        if (checkMobSpawnValidity && !blockState.getBlock().matches(BlockTags.VALID_SPAWN)) {
            return null;
        } else {
            WorldChunk worldChunk = this.world.getChunk(x >> 4, z >> 4);
            int i = worldChunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, x & 15, z & 15);
            if (i < 0) {
                return null;
            } else if (worldChunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, x & 15, z & 15) > worldChunk.sampleHeightmap(Heightmap.Type.OCEAN_FLOOR, x & 15, z & 15)) {
                return null;
            } else {
                for (int j = i + 1; j >= 0; --j) {
                    mutable.set(x, j, z);
                    BlockState blockState2 = this.world.getBlockState(mutable);
                    if (!blockState2.getFluidState().isEmpty()) {
                        break;
                    }

                    if (blockState2.equals(blockState)) {
                        return mutable.up().toImmutable();
                    }
                }

                return null;
            }
        }
    }

    public float getSkyAngle(long timeOfDay, float tickDelta) {
        double d = MathHelper.fractionalPart((double) timeOfDay / 24000.0D - 0.25D);
        double e = 0.5D - Math.cos(d * Math.PI) / 2.0D;
        return (float) (d * 2.0D + e) / 3.0F;
    }

    @Override
    public boolean hasVisibleSky() {
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Vec3d getFogColor(float skyAngle, float tickDelta) {
        return new Vec3d(0, 0, 0);
    }

    @Override
    public boolean canPlayersSleep() {
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean isFogThick(int x, int z) {
        return false;
    }

    @Nullable
    @Override
    public float[] getBackgroundColor(float skyAngle, float tickDelta) {
        return super.getBackgroundColor(skyAngle, tickDelta);
    }
}