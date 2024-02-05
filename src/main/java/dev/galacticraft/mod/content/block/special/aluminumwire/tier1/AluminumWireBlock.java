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

package dev.galacticraft.mod.content.block.special.aluminumwire.tier1;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.WireBlock;
import dev.galacticraft.mod.content.GCBlockEntityTypes;
import dev.galacticraft.mod.content.block.entity.networked.WireBlockEntity;
import dev.galacticraft.mod.util.DirectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.List;

public class AluminumWireBlock extends WireBlock {
    // If we start at 8,8,8 and subtract/add to/from 8, we do operations starting from the center.
    private static final int OFFSET = 2;
    public static final VoxelShape NORTH = box(8 - OFFSET, 8 - OFFSET, 0, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);
    public static final VoxelShape EAST = box(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 16, 8 + OFFSET, 8 + OFFSET);
    public static final VoxelShape SOUTH = box(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 16);
    public static final VoxelShape WEST = box(0, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);
    public static final VoxelShape UP = box(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 16, 8 + OFFSET);
    public static final VoxelShape DOWN = box(8 - OFFSET, 0, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);
    public static final VoxelShape NONE = box(8 - OFFSET, 8 - OFFSET, 8 - OFFSET, 8 + OFFSET, 8 + OFFSET, 8 + OFFSET);    // 6x6x6 box in the center.

    public AluminumWireBlock(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        if (level.getBlockEntity(blockPos) instanceof WireBlockEntity wire) {
            List<VoxelShape> shapes = new ArrayList<>();

            if (wire.getConnections()[2]) {
                shapes.add(NORTH);
            }
            if (wire.getConnections()[3]) {
                shapes.add(SOUTH);
            }
            if (wire.getConnections()[5]) {
                shapes.add(EAST);
            }
            if (wire.getConnections()[4]) {
                shapes.add(WEST);
            }
            if (wire.getConnections()[1]) {
                shapes.add(UP);
            }
            if (wire.getConnections()[0]) {
                shapes.add(DOWN);
            }
            if (!shapes.isEmpty()) {
                return Shapes.or(NONE, shapes.toArray(VoxelShape[]::new));
            }
        }
        return NONE;
    }

    @Override
    public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState oldState, boolean notify) {
        super.onPlace(blockState, level, blockPos, oldState, notify);

        if (level.getBlockEntity(blockPos) instanceof WireBlockEntity wire) {
            var changed = false;
            for (var direction : Constant.Misc.DIRECTIONS) {
                changed |= wire.getConnections()[direction.ordinal()] = wire.canConnect(direction) && EnergyStorage.SIDED.find(level, blockPos.relative(direction), direction.getOpposite()) != null;
            }
            if (changed) {
                wire.setChanged();
            }
            level.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_IMMEDIATE);
        }
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos neighborPos, boolean notify) {
        super.neighborChanged(blockState, level, blockPos, block, neighborPos, notify);

        if (level.getBlockEntity(blockPos) instanceof WireBlockEntity wire) {
            var direction = DirectionUtil.fromNormal(neighborPos.getX() - blockPos.getX(), neighborPos.getY() - blockPos.getY(), neighborPos.getZ() - blockPos.getZ());

            if (direction != null) {
                if (!level.isClientSide && wire.getConnections()[direction.ordinal()] != (wire.getConnections()[direction.ordinal()] = wire.canConnect(direction) && EnergyStorage.SIDED.find(level, neighborPos, direction.getOpposite()) != null)) {
                    level.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_IMMEDIATE);
                }
            }
            wire.setChanged();
        }
    }

    @Override
    @Nullable
    public WireBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return WireBlockEntity.createT1(GCBlockEntityTypes.WIRE_T1, blockPos, blockState);
    }
}