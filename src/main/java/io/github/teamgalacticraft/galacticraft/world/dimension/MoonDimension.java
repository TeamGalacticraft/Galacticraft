//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.github.teamgalacticraft.galacticraft.world.dimension;

import io.github.teamgalacticraft.galacticraft.world.biome.source.GCBiomeSourceTypes;
import io.github.teamgalacticraft.galacticraft.world.biome.source.MoonLayeredBiomeSource;
import io.github.teamgalacticraft.galacticraft.world.biome.source.MoonLayeredBiomeSourceConfig;
import io.github.teamgalacticraft.galacticraft.world.gen.chunk.GCChunkGeneratorTypes;
import io.github.teamgalacticraft.galacticraft.world.gen.chunk.MoonChunkGenerator;
import io.github.teamgalacticraft.galacticraft.world.gen.chunk.MoonChunkGeneratorConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

public class MoonDimension extends Dimension {
    public MoonDimension(World world_1, DimensionType dimensionType_1) {
        super(world_1, dimensionType_1);
    }

    public DimensionType getType() {
        return DimensionType.OVERWORLD;
    }

    public ChunkGenerator<? extends ChunkGeneratorConfig> createChunkGenerator() {
        ChunkGeneratorType<MoonChunkGeneratorConfig, MoonChunkGenerator> moonChunkGeneratorChunkGeneratorType = GCChunkGeneratorTypes.MOON;
        BiomeSourceType<MoonLayeredBiomeSourceConfig, MoonLayeredBiomeSource> biomeSourceType_2 = GCBiomeSourceTypes.MOON;
        MoonChunkGeneratorConfig moonChunkGeneratorConfig = moonChunkGeneratorChunkGeneratorType.createSettings();
        MoonLayeredBiomeSourceConfig vanillaLayeredBiomeSourceConfig_2 = biomeSourceType_2.getConfig().setLevelProperties(this.world.getLevelProperties()).setGeneratorSettings(moonChunkGeneratorConfig);
        return moonChunkGeneratorChunkGeneratorType.create(this.world, biomeSourceType_2.applyConfig(vanillaLayeredBiomeSourceConfig_2), moonChunkGeneratorConfig);
    }

    public BlockPos getSpawningBlockInChunk(ChunkPos chunkPos_1, boolean boolean_1) {
        for (int int_1 = chunkPos_1.getStartX(); int_1 <= chunkPos_1.getEndX(); ++int_1) {
            for (int int_2 = chunkPos_1.getStartZ(); int_2 <= chunkPos_1.getEndZ(); ++int_2) {
                BlockPos blockPos_1 = this.getTopSpawningBlockPosition(int_1, int_2, boolean_1);
                if (blockPos_1 != null) {
                    return blockPos_1;
                }
            }
        }

        return null;
    }

    public BlockPos getTopSpawningBlockPosition(int int_1, int int_2, boolean boolean_1) {
        Mutable blockPos$Mutable_1 = new Mutable(int_1, 0, int_2);
        Biome biome_1 = this.world.getBiome(blockPos$Mutable_1);
        BlockState blockState_1 = biome_1.getSurfaceConfig().getTopMaterial();
        if (boolean_1 && !blockState_1.getBlock().matches(BlockTags.VALID_SPAWN)) {
            return null;
        } else {
            WorldChunk worldChunk_1 = this.world.method_8497(int_1 >> 4, int_2 >> 4);
            int int_3 = worldChunk_1.sampleHeightmap(Type.MOTION_BLOCKING, int_1 & 15, int_2 & 15);
            if (int_3 < 0) {
                return null;
            } else if (worldChunk_1.sampleHeightmap(Type.WORLD_SURFACE, int_1 & 15, int_2 & 15) > worldChunk_1.sampleHeightmap(Type.OCEAN_FLOOR, int_1 & 15, int_2 & 15)) {
                return null;
            } else {
                for (int int_4 = int_3 + 1; int_4 >= 0; --int_4) {
                    blockPos$Mutable_1.set(int_1, int_4, int_2);
                    BlockState blockState_2 = this.world.getBlockState(blockPos$Mutable_1);
                    if (!blockState_2.getFluidState().isEmpty()) {
                        break;
                    }

                    if (blockState_2.equals(blockState_1)) {
                        return blockPos$Mutable_1.up().toImmutable();
                    }
                }

                return null;
            }
        }
    }

    public float getSkyAngle(long long_1, float float_1) {
        double double_1 = MathHelper.fractionalPart((double) long_1 / 24000.0D - 0.25D);
        double double_2 = 0.5D - Math.cos(double_1 * 3.141592653589793D) / 2.0D;
        return (float) (double_1 * 2.0D + double_2) / 3.0F;
    }

    public boolean hasVisibleSky() {
        return true;
    }

    @Environment(EnvType.CLIENT)
    public Vec3d getFogColor(float float_1, float float_2) {
        float float_3 = MathHelper.cos(float_1 * 6.2831855F) * 2.0F + 0.5F;
        float_3 = MathHelper.clamp(float_3, 0.0F, 1.0F);
        float float_4 = 0.7529412F;
        float float_5 = 0.84705883F;
        float float_6 = 1.0F;
        float_4 *= float_3 * 0.94F + 0.06F;
        float_5 *= float_3 * 0.94F + 0.06F;
        float_6 *= float_3 * 0.91F + 0.09F;
        return new Vec3d((double) float_4, (double) float_5, (double) float_6);
    }

    public boolean canPlayersSleep() {
        return true;
    }

    @Environment(EnvType.CLIENT)
    public boolean shouldRenderFog(int int_1, int int_2) {
        return false;
    }
}
