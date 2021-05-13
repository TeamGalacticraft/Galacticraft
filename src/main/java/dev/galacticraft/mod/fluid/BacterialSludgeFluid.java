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
import dev.galacticraft.mod.item.GalacticraftItems;
import dev.galacticraft.mod.particle.GalacticraftParticle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.Random;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class BacterialSludgeFluid extends FlowableFluid {

    @Override
    public Fluid getFlowing() {
        return GalacticraftFluid.FLOWING_BACTERIAL_SLUDGE;
    }

    @Override
    public Fluid getStill() {
        return GalacticraftFluid.BACTERIAL_SLUDGE;
    }

    @Override
    protected boolean isInfinite() {
        return false;
    }

    @Override
    public Item getBucketItem() {
        return GalacticraftItems.BACTERIAL_SLUDGE_BUCKET;
    }

    @Environment(EnvType.CLIENT)
    public ParticleEffect getParticle() {
        return GalacticraftParticle.DRIPPING_BACTERIAL_SLUDGE_PARTICLE;
    }

    @Override
    public boolean canBeReplacedWith(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(World world, BlockPos blockPos, FluidState fluidState, Random random) {
        if (random.nextInt(10) == 0) {
            world.addParticle(GalacticraftParticle.DRIPPING_BACTERIAL_SLUDGE_PARTICLE,
                    (double) blockPos.getX() + 0.5D - random.nextGaussian() + random.nextGaussian(),
                    (double) blockPos.getY() + 1.1F,
                    (double) blockPos.getZ() + 0.5D - random.nextGaussian() + random.nextGaussian(),
                    0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public int getTickRate(WorldView WorldView) {
        return 7;
    }

    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == getStill() || fluid == getFlowing();
    }

    @Override
    public void beforeBreakingBlock(WorldAccess iWorld, BlockPos blockPos, BlockState blockState) {
        BlockEntity blockEntity = blockState.getBlock().hasBlockEntity() ? iWorld.getBlockEntity(blockPos) : null;
        Block.dropStacks(blockState, iWorld, blockPos, blockEntity);
    }

    @Override
    protected int getFlowSpeed(WorldView world) {
        return 4;
    }

    @Override
    public int getLevelDecreasePerBlock(WorldView WorldView) {
        return 1;
    }

    @Override
    public boolean hasRandomTicks() {
        return true;
    }

    @Override
    public float getBlastResistance() {
        return 100.f;
    }

    @Override
    public BlockState toBlockState(FluidState fluidState) {
        return GalacticraftBlock.BACTERIAL_SLUDGE.getDefaultState().with(FluidBlock.LEVEL, method_15741(fluidState));
    }

    @Override
    public boolean isStill(FluidState fluidState) {
        return false;
    }

    @Override
    public int getLevel(FluidState fluidState) {
        return 0;
    }

    public static class Flowing extends BacterialSludgeFluid {

        public Flowing() {
        }

        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> stateBuilder) {
            super.appendProperties(stateBuilder);
            stateBuilder.add(LEVEL);
        }

        @Override
        public int getLevel(FluidState fluidState) {
            return fluidState.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return false;
        }
    }

    public static class Still extends BacterialSludgeFluid {

        public Still() {
        }

        @Override
        public int getLevel(FluidState fluidState) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return true;
        }
    }
}
