package io.github.teamgalacticraft.galacticraft.world.dimension;

import io.github.teamgalacticraft.galacticraft.blocks.GalacticraftBlocks;
import io.github.teamgalacticraft.galacticraft.world.biome.GCBiomes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class MoonDimension extends Dimension {
    private final float[] sunriseSunsetColors = new float[4];

    public MoonDimension(World worldIn, DimensionType typeIn) {
        super(worldIn, typeIn);
    }

    @Override
    public Vec3d getFogColor(float v, float v1) {
        int i = 8421536;
        float f2 = MathHelper.cos(v * 3.141593F * 2.0F) * 2.0F + 0.5F;
        f2 = MathHelper.clamp(f2, 0.0F, 1.0F);
        float f3 = (float) (i >> 16 & 255) / 255.0F;
        float f4 = (float) (i >> 8 & 255) / 255.0F;
        float f5 = (float) (i & 255) / 255.0F;
        f3 = f3 * (f2 * 0.94F + 0.06F);
        f4 = f4 * (f2 * 0.94F + 0.06F);
        f5 = f5 * (f2 * 0.91F + 0.09F);
        return new Vec3d((double) f3, (double) f4, (double) f5);
    }

    @Override
    public BlockPos getForcedSpawnPoint() {
        return super.getForcedSpawnPoint();
    }

    public ChunkGenerator<?> createChunkGenerator() {
        OverworldChunkGeneratorConfig cavesChunkGeneratorConfig_1 = ChunkGeneratorType.SURFACE.createSettings();
        cavesChunkGeneratorConfig_1.setDefaultBlock(GalacticraftBlocks.MOON_TURF_BLOCK.getDefaultState());
        cavesChunkGeneratorConfig_1.setDefaultFluid(Blocks.AIR.getDefaultState());
        return ChunkGeneratorType.SURFACE.create(this.world, BiomeSourceType.FIXED.applyConfig(BiomeSourceType.FIXED.getConfig().setBiome(GCBiomes.MOON)), cavesChunkGeneratorConfig_1);
    }

    @Override
    public BlockPos getSpawningBlockInChunk(ChunkPos chunkPos, boolean b) {
        return null;
    }

    @Override
    public BlockPos getTopSpawningBlockPosition(int i, int i1, boolean b) {
        return null;
    }

    @Override
    public float getSkyAngle(long var1, float var3) {
        int int_1 = (int) (var1 % 24000L);
        float float_2 = ((float) int_1 + (float) var1) / 24000.0F - 0.25F;
        if (float_2 < 0.0F) {
            ++float_2;
        }

        if (float_2 > 1.0F) {
            --float_2;
        }

        float var7 = 1.0F - (float) ((Math.cos((double) float_2 * Math.PI) + 1.0D) / 2.0D);
        float_2 = float_2 + (var7 - float_2) / 3.0F;
        return float_2;
    }

    public boolean hasVisibleSky() {
        return true;
    }

    @Environment(EnvType.CLIENT)
    public float[] getBackgroundColor(float var1, float var2) {
        float f2 = 0.4F;
        float f3 = MathHelper.cos(var1 * 3.141593F * 2.0F) - 0.0F;
        float f4 = -0.0F;
        if (f3 >= f4 - f2 && f3 <= f4 + f2) {
            float f5 = (f3 - f4) / f2 * 0.5F + 0.5F;
            float f6 = 1.0F - (1.0F - MathHelper.cos(f5 * 3.141593F)) * 0.99F;
            f6 = f6 * f6;
            this.sunriseSunsetColors[0] = f5 * 0.3F + 0.1F;
            this.sunriseSunsetColors[1] = f5 * f5 * 0.7F + 0.2F;
            this.sunriseSunsetColors[2] = f5 * f5 * 0.7F + 0.2F;
            this.sunriseSunsetColors[3] = f6;
            return this.sunriseSunsetColors;
        } else {
            return null;
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public float getCloudHeight() {
        return -100.0F;
    }

    public boolean canPlayersSleep() {
        return false;
    }

    public boolean shouldRenderFog(int var1, int var2) {
        return false;
    }

    public DimensionType getType() {
        return GalacticraftDimensions.MOON;
    }
}
