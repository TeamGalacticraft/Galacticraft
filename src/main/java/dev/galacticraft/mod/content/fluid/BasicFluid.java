/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.content.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public abstract class BasicFluid extends FlowingFluid {
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

    protected abstract LiquidBlock getBlock();

    public abstract boolean isStill();

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter world, BlockPos pos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN && this.isSame(fluid);
    }

    @Override
    protected boolean canConvertToSource(Level level) {
        return this.infinite;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropResources(state, world, pos, blockEntity);
    }

    @Override
    protected int getSlopeFindDistance(LevelReader world) {
        return this.flowSpeed;
    }

    @Override
    protected int getDropOff(LevelReader world) {
        return this.levelDecrease;
    }

    @Override
    public int getTickDelay(LevelReader world) {
        return this.tickRate;
    }

    @Override
    protected float getExplosionResistance() {
        return this.blastResistance;
    }

    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == this.getSource() || fluid == this.getFlowing();
    }

    @Override
    protected BlockState createLegacyBlock(FluidState state) {
        return this.getBlock().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    @Override
    public boolean isSource(FluidState state) {
        return this.isStill();
    }

    @Override
    public int getAmount(FluidState state) {
        return !this.isStill() ? state.getValue(LEVEL) : 8;
    }

    @Override
    protected boolean isRandomlyTicking() {
        return randomTicks;
    }

    @Override
    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
        super.createFluidStateDefinition(builder);
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
