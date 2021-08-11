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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public abstract class BasicFluid extends FlowableFluid {
    private final boolean infinite;
    private final boolean randomTicks;
    private final int flowSpeed;
    private final int levelDecrease;
    private final int tickRate;
    private final float blastResistance;

    public BasicFluid(boolean infinite, boolean randomTicks, int flowSpeed, int levelDecrease, int tickRate, float blastResistance) {
        super();
        this.infinite = infinite;
        this.randomTicks = randomTicks;
        this.flowSpeed = flowSpeed;
        this.levelDecrease = levelDecrease;
        this.tickRate = tickRate;
        this.blastResistance = blastResistance;
    }

    protected abstract FluidBlock getBlock();

    public abstract boolean isStill();

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN && this.matchesType(fluid);
    }

    @Override
    protected boolean isInfinite() {
        return this.infinite;
    }

    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropStacks(state, world, pos, blockEntity);
    }

    @Override
    protected int getFlowSpeed(WorldView world) {
        return this.flowSpeed;
    }

    @Override
    protected int getLevelDecreasePerBlock(WorldView world) {
        return this.levelDecrease;
    }

    @Override
    public int getTickRate(WorldView world) {
        return this.tickRate;
    }

    @Override
    protected float getBlastResistance() {
        return this.blastResistance;
    }

    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == this.getStill() || fluid == this.getFlowing();
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return this.getBlock().getDefaultState().with(FluidBlock.LEVEL, getBlockStateLevel(state));
    }

    @Override
    public boolean isStill(FluidState state) {
        return this.isStill();
    }

    @Override
    public int getLevel(FluidState state) {
        return !this.isStill() ? state.get(LEVEL) : 8;
    }

    @Override
    protected boolean hasRandomTicks() {
        return randomTicks;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
        super.appendProperties(builder);
        if (!this.isStill()) {
            builder.add(LEVEL);
        }
    }

    @Override
    public String toString() {
        return "BasicFluid{" +
                "isStill=" + isStill() +
                ", infinite=" + infinite +
                ", randomTicks=" + randomTicks +
                ", flowSpeed=" + flowSpeed +
                ", levelDecrease=" + levelDecrease +
                ", tickRate=" + tickRate +
                ", blastResistance=" + blastResistance +
                '}';
    }
}
