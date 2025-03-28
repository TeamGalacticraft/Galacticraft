package dev.galacticraft.mod.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class MeteorCraterFeature extends Feature<MeteorCraterFeature.Configuration> {
    public MeteorCraterFeature(Codec<Configuration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Configuration> context) {
        BlockPos pos = context.origin();
        RandomSource random = context.random();
        WorldGenLevel world = context.level();
        Configuration config = context.config();

        int radius = config.radius();
        BlockState core = config.coreBlock().getState(random, pos);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -2; y <= 0; y++) {
                    if (x * x + z * z + y * y <= radius * radius) {
                        world.setBlock(pos.offset(x, y, z), Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }

        world.setBlock(pos, core, 3);
        return true;
    }

    public static record Configuration(BlockStateProvider coreBlock, int radius) implements FeatureConfiguration {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        BlockStateProvider.CODEC.fieldOf("core_block").forGetter(Configuration::coreBlock),
                        Codec.INT.fieldOf("radius").orElse(3).forGetter(Configuration::radius)
                ).apply(instance, Configuration::new)
        );
    }
}
