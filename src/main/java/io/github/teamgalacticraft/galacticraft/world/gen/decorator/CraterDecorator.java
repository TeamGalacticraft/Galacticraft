package io.github.teamgalacticraft.galacticraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.Decorator;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class CraterDecorator extends Decorator<CraterDecoratorConfig> {

    public CraterDecorator(Function<Dynamic<?>, ? extends CraterDecoratorConfig> function_1) {
        super(function_1);
    }

    public Stream<BlockPos> getPositions(IWorld iWorld_1, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator_1, Random random_1, CraterDecoratorConfig lakeDecoratorConfig_1, BlockPos blockPos_1) {
        if (random_1.nextInt(lakeDecoratorConfig_1.chance) == 0) {
            int int_1 = random_1.nextInt(16);
            int int_2 = random_1.nextInt(chunkGenerator_1.getMaxY());
            int int_3 = random_1.nextInt(16);
            return Stream.of(blockPos_1.add(int_1, int_2, int_3));
        } else {
            return Stream.empty();
        }
    }
}
