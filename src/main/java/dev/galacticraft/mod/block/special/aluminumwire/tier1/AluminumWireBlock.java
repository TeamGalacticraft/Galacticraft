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

package dev.galacticraft.mod.block.special.aluminumwire.tier1;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.WireBlock;
import dev.galacticraft.mod.util.ConnectingBlockUtil;
import dev.galacticraft.mod.util.EnergyUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class AluminumWireBlock extends WireBlock {
    // If we start at 8,8,8 and subtract/add to/from 8, we do operations starting from the centre.
    private static final int OFFSET = 2;
    private static final VoxelShape NORTH_SHAPE = createCuboidShape(8 - OFFSET, 8 - OFFSET, 0, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);
    private static final VoxelShape EAST_SHAPE = createCuboidShape(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 16, 8 + OFFSET, 8 + OFFSET);
    private static final VoxelShape SOUTH_SHAPE = createCuboidShape(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 16);
    private static final VoxelShape WEST_SHAPE = createCuboidShape(0, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);
    private static final VoxelShape UP_SHAPE = createCuboidShape(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 16, 8 + OFFSET);
    private static final VoxelShape DOWN_SHAPE = createCuboidShape(8 - OFFSET, 0, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);
    private static final VoxelShape NONE = createCuboidShape(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);    // 6x6x6 box in the center.

    public AluminumWireBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(Properties.NORTH, false).with(Properties.EAST, false).with(Properties.SOUTH, false).with(Properties.WEST, false).with(Properties.UP, false).with(Properties.DOWN, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext context) {
        return ConnectingBlockUtil.getVoxelShape(blockState, NORTH_SHAPE, SOUTH_SHAPE, EAST_SHAPE, WEST_SHAPE, UP_SHAPE, DOWN_SHAPE, NONE);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        BlockState state = this.getDefaultState();
        for (Direction direction : Constant.Misc.DIRECTIONS) {
            BlockState block = context.getWorld().getBlockState(context.getBlockPos().offset(direction));
            state = state.with(ConnectingBlockUtil.getBooleanProperty(direction), !block.isAir() && (block.getBlock() instanceof WireBlock
                    || EnergyUtil.canAccessEnergy(context.getWorld(), context.getBlockPos().offset(direction), direction)));
        }
        return state;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        Direction dir = Direction.fromVector(posFrom.getX() - pos.getX(), posFrom.getY() - pos.getY(), posFrom.getZ() - pos.getZ());
        return dir != null ? state.with(ConnectingBlockUtil.getBooleanProperty(dir), !newState.isAir() && newState.getBlock() instanceof WireBlock || EnergyUtil.canAccessEnergy((World)world, posFrom, dir)) : super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.NORTH, Properties.EAST, Properties.SOUTH, Properties.WEST, Properties.UP, Properties.DOWN);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView view, BlockPos pos) {
        return 1.0F;
    }
}