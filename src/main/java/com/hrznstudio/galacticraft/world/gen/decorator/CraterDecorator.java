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

package com.hrznstudio.galacticraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.decorator.Decorator;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CraterDecorator extends Decorator<CraterDecoratorConfig> {

    public CraterDecorator(Function<Dynamic<?>, ? extends CraterDecoratorConfig> function_1) {
        super(function_1);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldAccess world, ChunkGenerator generator, Random random, CraterDecoratorConfig config, BlockPos pos) {
        return Stream.empty();
    }
}

//    public Stream<BlockPos> getPositions(WorldAccess iWorld_1, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator_1, Random random_1, CraterDecoratorConfig lakeDecoratorConfig_1, BlockPos blockPos_1) {
//        if (random_1.nextInt(lakeDecoratorConfig_1.chance) == 0) {
//            int int_1 = random_1.nextInt(16);
//            int int_2 = random_1.nextInt(chunkGenerator_1.getMaxY());
//            int int_3 = random_1.nextInt(16);
//            return Stream.of(blockPos_1.add(int_1, int_2, int_3));
//        } else {
//            return Stream.empty();
//        }
//    }
//}
