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

import com.hrznstudio.galacticraft.world.biome.GalacticraftBiomes;
import com.hrznstudio.galacticraft.world.gen.chunk.GalacticraftChunkGeneratorTypes;
import com.hrznstudio.galacticraft.world.gen.chunk.MoonChunkGeneratorConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import javax.annotation.Nullable;
//import net.minecraft.world.gen.chunk.ChunkGeneratorType;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class MoonDimension extends Dimension {
    public MoonDimension(World world, DimensionType type) {
        super(world, type, 0.0F);
    }

    @Override
    public int getMoonPhase(long long_1) {
        return 0;
    }

    @Override
    public void update() {
        super.update();
//        this.world.getLevelProperties().setThundering(false);
//        this.world.getLevelProperties().setRaining(false);
//        this.world.getLevelProperties().setClearWeatherTime(10000000);
//        this.world.getLevelProperties().setRainTime(0);
//        this.world.getLevelProperties().setThunderTime(0);
    }

    @Nullable
    @Override
    public BlockPos getSpawningBlockInChunk(long l, ChunkPos chunkPos, boolean bl) {
        return null;
    }

    @Nullable
    @Override
    public BlockPos getTopSpawningBlockPosition(long l, int i, int j, boolean bl) {
        return null;
    }

//    @Override
//    public boolean hasSkyLight() {
//        return true;
//    }
//
//    @Override
//    public boolean isNether() {
//        return false;
//    }

//    @Override
//    public Vec3d modifyFogColor(Vec3d vec3d, float tickDelta) {
//        return new Vec3d(0.0D, 0.0D, 0.0D);
//    }

    @Override
    public BlockPos getForcedSpawnPoint() {
        return new BlockPos(0, 100, 0);
    }

//    public ChunkGenerator<?> createChunkGenerator() {
//        MoonChunkGeneratorConfig moonChunkGeneratorConfig = GalacticraftChunkGeneratorTypes.MOON.createConfig();
//        return ChunkGeneratorType.SURFACE.create(this.world, BiomeSourceType.FIXED.applyConfig(BiomeSourceType.FIXED.getConfig(this.world.getLevelProperties().getSeed()).setBiome(GalacticraftBiomes.MOON)), moonChunkGeneratorConfig);
//    }

//    @Override
//    public BlockPos getSpawningBlockInChunk(ChunkPos chunkPos, boolean b) {
//        return new BlockPos(0, 100, 0);
//    }
//
//    @Override
//    public BlockPos getTopSpawningBlockPosition(int i, int i1, boolean b) {
//        return new BlockPos(0, 100, 0);
//    }

    public float getSkyAngle(long long_1, float float_1) {
        double double_1 = MathHelper.fractionalPart((double) long_1 / 24000.0D - 0.25D);
        double double_2 = 0.5D - Math.cos(double_1 * 3.141592653589793D) / 2.0D;
        return (float) (double_1 * 2.0D + double_2) / 3.0F;
    }

    public boolean hasVisibleSky() {
        return true;
    }

    @Environment(EnvType.CLIENT)
    public float[] getBackgroundColor(float l1, float f2) {
        return new float[]{0, 0, 0, 0};
    }
//
//    @Environment(EnvType.CLIENT)
//    @Override
//    public float getCloudHeight() {
//        return -1000.0F;
//    }

    public boolean canPlayersSleep() {
        return false;
    }

//    @Override
//    public boolean isFogThick(int x, int z) {
//        return false;
//    }

    public DimensionType getType() {
        return GalacticraftDimensions.MOON;
    }
}
