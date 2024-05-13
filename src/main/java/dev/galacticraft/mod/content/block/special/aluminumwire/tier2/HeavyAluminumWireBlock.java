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

package dev.galacticraft.mod.content.block.special.aluminumwire.tier2;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.WireBlock;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.content.block.entity.networked.WireBlockEntity;
import dev.galacticraft.mod.util.ConnectingBlockUtil;
import dev.galacticraft.mod.util.DirectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;

public class HeavyAluminumWireBlock extends WireBlock {
    // If we start at 8,8,8 and subtract/add to/from 8, we do operations starting from the center.
    private static final int OFFSET = 2;
    private static final VoxelShape NORTH = box(8 - OFFSET, 8 - OFFSET, 0, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);
    private static final VoxelShape EAST = box(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 16, 8 + OFFSET, 8 + OFFSET);
    private static final VoxelShape SOUTH = box(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 16);
    private static final VoxelShape WEST = box(0, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);
    private static final VoxelShape UP = box(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 16, 8 + OFFSET);
    private static final VoxelShape DOWN = box(8 - OFFSET, 0, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);
    private static final VoxelShape NONE = box(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);    // 6x6x6 box in the center.

    public HeavyAluminumWireBlock(Properties settings) {
        super(settings);
        registerDefaultState(this.getStateDefinition().any()
                .setValue(BlockStateProperties.NORTH, false).setValue(BlockStateProperties.EAST, false)
                .setValue(BlockStateProperties.SOUTH, false).setValue(BlockStateProperties.WEST, false)
                .setValue(BlockStateProperties.UP, false).setValue(BlockStateProperties.DOWN, false));
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockView, BlockPos blockPos, CollisionContext context) {
        ArrayList<VoxelShape> shapes = new ArrayList<>();

        if (blockState.getValue(BlockStateProperties.NORTH)) {
            shapes.add(NORTH);
        }
        if (blockState.getValue(BlockStateProperties.SOUTH)) {
            shapes.add(SOUTH);
        }
        if (blockState.getValue(BlockStateProperties.EAST)) {
            shapes.add(EAST);
        }
        if (blockState.getValue(BlockStateProperties.WEST)) {
            shapes.add(WEST);
        }
        if (blockState.getValue(BlockStateProperties.UP)) {
            shapes.add(UP);
        }
        if (blockState.getValue(BlockStateProperties.DOWN)) {
            shapes.add(DOWN);
        }
        if (shapes.isEmpty()) {
            return NONE;
        } else {
            return Shapes.or(NONE, shapes.toArray(new VoxelShape[0]));
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        for (Direction direction : Constant.Misc.DIRECTIONS) {
            BlockState block = context.getLevel().getBlockState(context.getClickedPos().relative(direction));
            state = state.setValue(ConnectingBlockUtil.getBooleanProperty(direction), !block.isAir() && (block.getBlock() instanceof WireBlock
                    || EnergyStorage.SIDED.find(context.getLevel(), context.getClickedPos().relative(direction), direction.getOpposite()) != null));

        }

        return state;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean notify) {
        super.neighborChanged(state, level, pos, block, neighborPos, notify);
        BlockState neighbor = level.getBlockState(neighborPos);
        Direction dir = DirectionUtil.fromNormal(neighborPos.getX() - pos.getX(), neighborPos.getY() - pos.getY(), neighborPos.getZ() - pos.getZ());
        assert dir != null;
        level.setBlockAndUpdate(pos, state.setValue(ConnectingBlockUtil.getBooleanProperty(dir), !neighbor.isAir() && block instanceof WireBlock
                || EnergyStorage.SIDED.find(level, neighborPos, dir.getOpposite()) != null
        ));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.NORTH, BlockStateProperties.EAST, BlockStateProperties.SOUTH, BlockStateProperties.WEST, BlockStateProperties.UP, BlockStateProperties.DOWN);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter view, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public @Nullable WireBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return WireBlockEntity.createT2(GCBlockEntityTypes.WIRE_T2, pos, state);
    }
}