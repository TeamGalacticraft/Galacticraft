/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.fluid;

import dev.galacticraft.mod.block.GalacticraftBlock;
import dev.galacticraft.mod.item.GalacticraftItem;
import dev.galacticraft.mod.particle.GalacticraftParticle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public abstract class CrudeOilFluid extends BasicFluid {
    protected CrudeOilFluid() {
        super(false, true, 5, 1, 7, 100);
    }

    @Override
    public Fluid getFlowing() {
        return GalacticraftFluid.FLOWING_CRUDE_OIL;
    }

    @Override
    public Fluid getStill() {
        return GalacticraftFluid.CRUDE_OIL;
    }

    @Environment(EnvType.CLIENT)
    public ParticleEffect getParticle() {
        return GalacticraftParticle.DRIPPING_CRUDE_OIL_PARTICLE;
    }

    @Override
    public Item getBucketItem() {
        return GalacticraftItem.CRUDE_OIL_BUCKET;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(World world, BlockPos blockPos, FluidState fluidState, Random random) {
        if (random.nextInt(10) == 0) {
            world.addParticle(GalacticraftParticle.DRIPPING_CRUDE_OIL_PARTICLE,
                    (double) blockPos.getX() + 0.5D - random.nextGaussian() + random.nextGaussian(),
                    (double) blockPos.getY() + 1.1F,
                    (double) blockPos.getZ() + 0.5D - random.nextGaussian() + random.nextGaussian(),
                    0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected FluidBlock getBlock() {
        return GalacticraftBlock.CRUDE_OIL;
    }

    public static class Still extends CrudeOilFluid {
        @Override
        public boolean isStill() {
            return true;
        }
    }

    public static class Flowing extends CrudeOilFluid {
        @Override
        public boolean isStill() {
            return false;
        }
    }
}
