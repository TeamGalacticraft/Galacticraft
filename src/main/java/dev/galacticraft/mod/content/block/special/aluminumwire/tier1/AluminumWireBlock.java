/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.mod.content.block.special.aluminumwire.tier1;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.WireBlock;
import dev.galacticraft.mod.api.block.entity.WireBlockEntity;
import dev.galacticraft.mod.content.block.entity.GCBlockEntityTypes;
import dev.galacticraft.mod.util.ConnectingBlockUtil;

import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;


/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class AluminumWireBlock extends WireBlock {
    // If we start at 8,8,8 and subtract/add to/from 8, we do operations starting from the centre.
    private static final int OFFSET = 2;
    private static final VoxelShape NORTH = box(8 - OFFSET, 8 - OFFSET, 0, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);
    private static final VoxelShape EAST = box(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 16, 8 + OFFSET, 8 + OFFSET);
    private static final VoxelShape SOUTH = box(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 16);
    private static final VoxelShape WEST = box(0, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);
    private static final VoxelShape UP = box(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 16, 8 + OFFSET);
    private static final VoxelShape DOWN = box(8 - OFFSET, 0, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);
    private static final VoxelShape NONE = box(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);    // 6x6x6 box in the center.

    public AluminumWireBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(BlockStateProperties.NORTH, false).setValue(BlockStateProperties.EAST, false)
                .setValue(BlockStateProperties.SOUTH, false).setValue(BlockStateProperties.WEST, false)
                .setValue(BlockStateProperties.UP, false).setValue(BlockStateProperties.DOWN, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
        WireBlockEntity wire = (WireBlockEntity) view.getBlockEntity(pos);
        if (wire != null) {
            List<VoxelShape> shapes = new ArrayList<>();

            if (state.getValue(BlockStateProperties.NORTH)) {
                shapes.add(NORTH);
            }
            if (state.getValue(BlockStateProperties.EAST)) {
                shapes.add(EAST);
            }
            if (state.getValue(BlockStateProperties.SOUTH)) {
                shapes.add(SOUTH);
            }
            if (state.getValue(BlockStateProperties.WEST)) {
                shapes.add(WEST);
            }
            if (state.getValue(BlockStateProperties.UP)) {
                shapes.add(UP);
            }
            if (state.getValue(BlockStateProperties.DOWN)) {
                shapes.add(DOWN);
            }
            if (!shapes.isEmpty()) {
                return Shapes.or(NONE, shapes.toArray(new VoxelShape[0]));
            }
        }
        return NONE;
    }

    @Override // TODO: investigate speed ups and why single wires are strange
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, world, pos, oldState, notify);
        WireBlockEntity wire = (WireBlockEntity) world.getBlockEntity(pos);
        assert wire != null;
        boolean b = false;
        for (Direction dir : Constant.Misc.DIRECTIONS) {
            b |= (wire.getConnections()[dir.ordinal()] = wire.canConnect(dir) && EnergyStorage.SIDED.find(world, pos.relative(dir), dir.getOpposite()) != null);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) { // first thing that is called, ala before blockEntity is made
        BlockState state = this.defaultBlockState();
        for (Direction dir : Constant.Misc.DIRECTIONS) {
            BlockState block = context.getLevel().getBlockState(context.getClickedPos().relative(dir));
            state = state.setValue(ConnectingBlockUtil.getBooleanProperty(dir), !block.isAir() && (block.getBlock() instanceof WireBlock
                    || EnergyStorage.SIDED.find(context.getLevel(), context.getClickedPos().relative(dir), dir.getOpposite()) != null));
        }

        return state;
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.NORTH, BlockStateProperties.EAST, BlockStateProperties.SOUTH, BlockStateProperties.WEST, BlockStateProperties.UP, BlockStateProperties.DOWN);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborChanged(state, world, pos, block, fromPos, notify);
        WireBlockEntity wire = (WireBlockEntity) world.getBlockEntity(pos);
        Direction dir = Direction.fromNormal(fromPos.getX() - pos.getX(), fromPos.getY() - pos.getY(), fromPos.getZ() - pos.getZ());
        assert dir != null;
        assert wire != null;
        if (!world.isClientSide && wire.getConnections()[dir.ordinal()] != (wire.getConnections()[dir.ordinal()] = wire.canConnect(dir) && EnergyStorage.SIDED.find(world, fromPos, dir.getOpposite()) != null)) {
            world.setBlockAndUpdate(pos, state.setValue(ConnectingBlockUtil.getBooleanProperty(dir), wire.getConnections()[dir.ordinal()]));
        }
    }

    @Override
    public @Nullable WireBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return WireBlockEntity.createT1(GCBlockEntityTypes.WIRE_T1, pos, state);
    }
}