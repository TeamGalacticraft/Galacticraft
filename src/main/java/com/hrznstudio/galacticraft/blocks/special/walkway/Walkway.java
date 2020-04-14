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

package com.hrznstudio.galacticraft.blocks.special.walkway;

import com.hrznstudio.galacticraft.blocks.FluidLoggableBlock;
import com.hrznstudio.galacticraft.blocks.decoration.GratingBlock;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.*;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class Walkway extends HorizontalConnectedBlock implements FluidLoggableBlock {

    public Walkway(Settings settings) {
        super(8,8,16,16,16f,settings);

        this.setDefaultState(this.stateFactory.getDefaultState()
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(WATERLOGGED, false)
                .with(FLUID, new Identifier("empty"))
                .with(BaseFluid.LEVEL, 8));
    }

    public boolean canConnect(BlockState state) {
        return state.getBlock() instanceof Walkway;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
        return this.getDefaultState()
                .with(FLUID, Registry.FLUID.getId(fluidState.getFluid()))
                .with(BaseFluid.LEVEL, Math.max(fluidState.getLevel(), 1));
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        if (!state.get(FLUID).equals(new Identifier("empty"))) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return facing.getAxis().getType() == Direction.Type.HORIZONTAL ? state.with(FACING_PROPERTIES.get(facing), this.canConnect(neighborState)) : super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState_1) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        // For 1.15, in client init, do BlockRenderLayerMap.putBlock(Walkway, RenderLayer.getCutout()); or something
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        FluidState state1 = Registry.FLUID.get(state.get(FLUID)).getDefaultState();
        if (state1.getEntries().containsKey(BaseFluid.LEVEL)) {
            state1 = state1.with(BaseFluid.LEVEL, state.get(BaseFluid.LEVEL));
        }
        return state1;
    }

    @Override
    public void appendProperties(StateFactory.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED, FLUID, BaseFluid.LEVEL);
    }
}