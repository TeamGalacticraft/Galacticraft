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
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.ArrayList;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class AluminumWireBlock extends WireBlock {
    // If we start at 8,8,8 and subtract/add to/from 8, we do operations starting from the centre.
    private static final int OFFSET = 2;
    private static final VoxelShape NORTH = createCuboidShape(8 - OFFSET, 8 - OFFSET, 0, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);
    private static final VoxelShape EAST = createCuboidShape(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 16, 8 + OFFSET, 8 + OFFSET);
    private static final VoxelShape SOUTH = createCuboidShape(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 16);
    private static final VoxelShape WEST = createCuboidShape(0, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);
    private static final VoxelShape UP = createCuboidShape(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 16, 8 + OFFSET);
    private static final VoxelShape DOWN = createCuboidShape(8 - OFFSET, 0, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);
    private static final VoxelShape NONE = createCuboidShape(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);    // 6x6x6 box in the center.

    public AluminumWireBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getStateManager().getDefaultState()
                .with(Properties.NORTH, false).with(Properties.EAST, false)
                .with(Properties.SOUTH, false).with(Properties.WEST, false)
                .with(Properties.UP, false).with(Properties.DOWN, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext context) {
        ArrayList<VoxelShape> shapes = new ArrayList<>();

        if (blockState.get(Properties.NORTH)) {
            shapes.add(NORTH);
        }
        if (blockState.get(Properties.SOUTH)) {
            shapes.add(SOUTH);
        }
        if (blockState.get(Properties.EAST)) {
            shapes.add(EAST);
        }
        if (blockState.get(Properties.WEST)) {
            shapes.add(WEST);
        }
        if (blockState.get(Properties.UP)) {
            shapes.add(UP);
        }
        if (blockState.get(Properties.DOWN)) {
            shapes.add(DOWN);
        }
        if (shapes.isEmpty()) {
            return NONE;
        } else {
            return VoxelShapes.union(NONE, shapes.toArray(new VoxelShape[0]));
        }
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
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        BlockState neighbor = world.getBlockState(fromPos);
        Direction dir = Direction.fromVector(fromPos.getX() - pos.getX(), fromPos.getY() - pos.getY(), fromPos.getZ() - pos.getZ());
        assert dir != null;
        world.setBlockState(pos, state.with(ConnectingBlockUtil.getBooleanProperty(dir), !neighbor.isAir() && block instanceof WireBlock
                || EnergyUtil.canAccessEnergy(world, fromPos, dir)
        ));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Properties.NORTH, Properties.EAST, Properties.SOUTH, Properties.WEST, Properties.UP, Properties.DOWN);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView view, BlockPos pos) {
        return 1.0F;
    }
}