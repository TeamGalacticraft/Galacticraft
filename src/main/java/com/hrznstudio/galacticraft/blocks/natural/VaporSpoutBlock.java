/*
 * Copyright (c) 2018-2019 Horizon Studio
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

package com.hrznstudio.galacticraft.blocks.natural;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class VaporSpoutBlock extends Block {

    public VaporSpoutBlock(Settings block$Settings_1) {
        super(block$Settings_1);
    }

    @Override
    public boolean hasRandomTicks(BlockState blockState_1) {
        return true;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void randomDisplayTick(BlockState blockState_1, World world_1, BlockPos blockPos_1, Random random_1) {
        spawnSmokeParticle(world_1, blockPos_1);
    }

    public void spawnSmokeParticle(World world_1, BlockPos blockPos_1) {
        Random random_1 = world_1.getRandom();
        world_1.addImportantParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, true, (double) blockPos_1.getX() + 0.5D + random_1.nextDouble() / 3.0D * (double) (random_1.nextBoolean() ? 1 : -1), (double) blockPos_1.getY() + random_1.nextDouble() + random_1.nextDouble(), (double) blockPos_1.getZ() + 0.5D + random_1.nextDouble() / 3.0D * (double) (random_1.nextBoolean() ? 1 : -1), 0.0D, 0.07D, 0.0D);
    }
}
