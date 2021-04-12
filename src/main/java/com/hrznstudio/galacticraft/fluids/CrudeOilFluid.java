/*
 * Copyright (c) 2019-2021 HRZN LTD
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

package com.hrznstudio.galacticraft.fluids;

import com.hrznstudio.galacticraft.block.GalacticraftBlocks;
import com.hrznstudio.galacticraft.items.GalacticraftItems;
import com.hrznstudio.galacticraft.particle.GalacticraftParticles;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import java.util.Random;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CrudeOilFluid extends FlowingFluid {

    @Override
    public Fluid getFlowing() {
        return GalacticraftFluids.FLOWING_CRUDE_OIL;
    }

    @Override
    public Fluid getSource() {
        return GalacticraftFluids.CRUDE_OIL;
    }

    @Override
    protected boolean canConvertToSource() {
        return false;
    }

    @Override
    public Item getBucket() {
        return GalacticraftItems.CRUDE_OIL_BUCKET;
    }

    @Environment(EnvType.CLIENT)
    public ParticleOptions getDripParticle() {
        return GalacticraftParticles.DRIPPING_CRUDE_OIL_PARTICLE;
    }

    @Override
    public boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN && !fluid.isSame(GalacticraftFluids.CRUDE_OIL);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void animateTick(Level world, BlockPos blockPos, FluidState fluidState, Random random) {
        if (random.nextInt(10) == 0) {
            world.addParticle(GalacticraftParticles.DRIPPING_CRUDE_OIL_PARTICLE,
                    (double) blockPos.getX() + 0.5D - random.nextGaussian() + random.nextGaussian(),
                    (double) blockPos.getY() + 1.1F,
                    (double) blockPos.getZ() + 0.5D - random.nextGaussian() + random.nextGaussian(),
                    0.0D, 0.0D, 0.0D);
        }
    }


    @Override
    public int getTickDelay(LevelReader WorldView) {
        return 7;
    }

    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == getSource() || fluid == getFlowing();
    }

    @Override
    public void beforeDestroyingBlock(LevelAccessor iWorld, BlockPos blockPos, BlockState blockState) {
        BlockEntity blockEntity = blockState.getBlock().isEntityBlock() ? iWorld.getBlockEntity(blockPos) : null;
        Block.dropResources(blockState, iWorld, blockPos, blockEntity);
    }

    @Override
    protected int getSlopeFindDistance(LevelReader world) {
        return 4;
    }

    @Override
    public int getDropOff(LevelReader WorldView) {
        return 1;
    }

    @Override
    public boolean isRandomlyTicking() {
        return true;
    }

    @Override
    public float getExplosionResistance() {
        return 100.f;
    }

    @Override
    public BlockState createLegacyBlock(FluidState fluidState) {
        return GalacticraftBlocks.CRUDE_OIL.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(fluidState));
    }

    @Override
    public boolean isSource(FluidState fluidState) {
        return false;
    }

    @Override
    public int getAmount(FluidState fluidState) {
        return 0;
    }

    public static class Flowing extends CrudeOilFluid {

        public Flowing() {
        }

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> stateBuilder) {
            super.createFluidStateDefinition(stateBuilder);
            stateBuilder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState fluidState) {
            return fluidState.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState fluidState) {
            return false;
        }
    }

    public static class Still extends CrudeOilFluid {

        public Still() {
        }

        @Override
        public int getAmount(FluidState fluidState) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState fluidState) {
            return true;
        }
    }
}
