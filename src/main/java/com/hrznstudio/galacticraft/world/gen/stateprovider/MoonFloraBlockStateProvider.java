package com.hrznstudio.galacticraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

import java.util.Random;

public class MoonFloraBlockStateProvider extends BlockStateProvider {
    public static final Codec<MoonFloraBlockStateProvider> CODEC = Codec.unit(() -> MoonFloraBlockStateProvider.INSTANCE);
    public static final MoonFloraBlockStateProvider INSTANCE = new MoonFloraBlockStateProvider();

    public static BlockState[] mix1 = new BlockState[]{Blocks.ORANGE_TULIP.getDefaultState(), Blocks.RED_TULIP.getDefaultState(), Blocks.PINK_TULIP.getDefaultState(), Blocks.WHITE_TULIP.getDefaultState()};
    public static BlockState[] mix2 = new BlockState[]{Blocks.POPPY.getDefaultState(), Blocks.AZURE_BLUET.getDefaultState(), Blocks.OXEYE_DAISY.getDefaultState(), Blocks.CORNFLOWER.getDefaultState()};

    protected BlockStateProviderType<?> method_28862() {
        return BlockStateProviderType.PLAIN_FLOWER_PROVIDER;
    }

    public BlockState getBlockState(Random random, BlockPos pos) {
        double d = Biome.FOLIAGE_NOISE.sample((double) pos.getX() / 200.0D, (double) pos.getZ() / 200.0D, false);
        if (d < -0.8D) {
            return (BlockState) Util.getRandom((Object[]) mix1, random);
        } else {
            return random.nextInt(3) > 0 ? (BlockState) Util.getRandom((Object[]) mix2, random) : Blocks.DANDELION.getDefaultState();
        }
    }

}
