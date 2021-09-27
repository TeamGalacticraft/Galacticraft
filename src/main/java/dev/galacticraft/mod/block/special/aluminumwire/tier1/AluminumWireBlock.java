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
import dev.galacticraft.mod.api.block.entity.WireBlockEntity;
import dev.galacticraft.mod.block.entity.GalacticraftBlockEntityType;
import dev.galacticraft.mod.util.EnergyUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        WireBlockEntity wire = (WireBlockEntity) view.getBlockEntity(pos);
        if (wire != null) {
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
                return VoxelShapes.union(NONE, shapes.toArray(new VoxelShape[0]));
            }
        }
        return NONE;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        WireBlockEntity wire = (WireBlockEntity) world.getBlockEntity(pos);
        assert wire != null;
        boolean b = false;
        for (Direction dir : Constant.Misc.DIRECTIONS) {
            b |= (wire.getConnections()[dir.ordinal()] = EnergyUtil.canAccessEnergy(world, pos.offset(dir), dir) && wire.canConnect(dir));
        }
        if (b) wire.sync();
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        WireBlockEntity wire = (WireBlockEntity) world.getBlockEntity(pos);
        Direction dir = Direction.fromVector(fromPos.getX() - pos.getX(), fromPos.getY() - pos.getY(), fromPos.getZ() - pos.getZ());
        assert dir != null;
        assert wire != null;
        if (wire.getConnections()[dir.ordinal()] != (wire.getConnections()[dir.ordinal()] = EnergyUtil.canAccessEnergy(world, fromPos, dir) && wire.canConnect(dir))) {
            wire.sync();
        }
    }

    @Override
    public @Nullable WireBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return WireBlockEntity.createT1(GalacticraftBlockEntityType.WIRE_T1, pos, state);
    }
}