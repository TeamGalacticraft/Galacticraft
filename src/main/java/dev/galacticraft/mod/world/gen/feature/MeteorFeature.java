package dev.galacticraft.mod.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MeteorFeature extends Feature<MeteorFeature.Configuration> {
    public MeteorFeature(Codec<Configuration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Configuration> context) {
        BlockPos pos = context.origin();
        RandomSource random = context.random();
        WorldGenLevel world = context.level();
        Configuration config = context.config();

        int radius = config.radius();
        List<Shell> shells = config.shell();
        List<Center> center = config.center();

        double totalParts = shells.stream().mapToInt(Shell::parts).sum();

        List<ShellLayer> layers = new ArrayList<>();
        double innerRadius = radius;

        for (Shell shell : shells) {
            double shellThickness = (shell.parts() / totalParts) * radius;
            double outer = innerRadius;
            double inner = innerRadius - shellThickness;
            layers.add(new ShellLayer(shell.block(), inner, outer));
            innerRadius = inner;
        }

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double dist = Math.sqrt(x * x + y * y + z * z) + (random.nextDouble() - 0.5); // roughness
                    if (dist > radius) continue;

                    for (ShellLayer layer : layers) {
                        if (dist >= layer.inner && dist <= layer.outer) {
                            BlockState block = layer.block.getState(random, pos.offset(x, y, z));
                            world.setBlock(pos.offset(x, y, z), block, 3);
                            break;
                        }
                    }
                }
            }
        }

        for (Center blockCount : center) {
            int count = blockCount.count();
            double spread = Math.sqrt(count) * 2.0;

            for (int i = 0; i < count; i++) {
                // Random spherical direction
                double theta = random.nextDouble() * 2 * Math.PI;
                double phi = Math.acos(2 * random.nextDouble() - 1); // uniform over sphere

                // Biased radial distance (dense near center)
                double r = Math.cbrt(random.nextDouble()) * spread;

                // Convert spherical to cartesian offset
                int ox = (int) (r * Math.sin(phi) * Math.cos(theta));
                int oy = (int) (r * Math.sin(phi) * Math.sin(theta));
                int oz = (int) (r * Math.cos(phi));

                BlockPos p = pos.offset(ox, oy, oz);
                BlockState block = blockCount.block().getState(random, p);
                world.setBlock(p, block, 3);
            }
        }
        return true;
    }

    public enum MeteorType {
        SHATTERED, RAIN, SINGULAR;

        public static final Codec<MeteorType> CODEC = Codec.STRING.xmap(
                name -> MeteorType.valueOf(name.toUpperCase()),
                MeteorType::name
        );
    }

    public record Center(BlockStateProvider block, int count) {
        public static final Codec<Center> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("block").forGetter(Center::block),
                Codec.INT.fieldOf("count").forGetter(Center::count)
        ).apply(instance, Center::new));
    }

    public record Shell(BlockStateProvider block, int parts) {
        public static final Codec<Shell> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("block").forGetter(Shell::block),
                Codec.INT.fieldOf("parts").forGetter(Shell::parts)
        ).apply(instance, Shell::new));
    }

    public static record Configuration(
            boolean hasCrater,
            float meteorAngle,
            MeteorType meteorType,
            int radius,
            List<Center> center,
            List<Shell> shell,
            Optional<ResourceLocation> pool // Use Identifier to reference fluids or leave as None
    ) implements FeatureConfiguration {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.fieldOf("has_crater").forGetter(Configuration::hasCrater),
                Codec.FLOAT.fieldOf("meteor_angle").forGetter(Configuration::meteorAngle),
                MeteorType.CODEC.fieldOf("meteor_type").forGetter(Configuration::meteorType),
                Codec.INT.fieldOf("radius").forGetter(Configuration::radius),
                Center.CODEC.listOf().fieldOf("center").forGetter(Configuration::center),
                Shell.CODEC.listOf().fieldOf("shell").forGetter(Configuration::shell), //outer shell index 0 and increases as get closer to center
                ResourceLocation.CODEC.optionalFieldOf("pool").forGetter(Configuration::pool)
        ).apply(instance, Configuration::new));
    }

    private static class ShellLayer {
        final BlockStateProvider block;
        final double inner;
        final double outer;

        ShellLayer(BlockStateProvider block, double inner, double outer) {
            this.block = block;
            this.inner = inner;
            this.outer = outer;
        }
    }
}
