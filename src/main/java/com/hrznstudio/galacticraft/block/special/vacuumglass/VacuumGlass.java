/*
 * Copyright (c) 2020 HRZN LTD
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

package com.hrznstudio.galacticraft.block.special.vacuumglass;

import com.hrznstudio.galacticraft.api.block.FluidLoggableBlock;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.*;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class VacuumGlass extends Block implements FluidLoggableBlock {
    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final BooleanProperty UP;
    public static final BooleanProperty DOWN;
    public static final DirectionProperty FACING;

    private static final VoxelShape[] shape = new VoxelShape[16];
    private final Object2IntMap<BlockState> SHAPE_INDEX_CACHE = new Object2IntOpenHashMap<>();

    public VacuumGlass(Settings settings) {
        super(settings);

        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(UP, false)
                .with(DOWN, false)
                .with(FACING, Direction.NORTH)
                .with(FLUID, new Identifier("empty"))
                .with(FlowableFluid.LEVEL, 8));
    }

    private static int getDirectionMask(Direction dir) {
        return 1 << dir.getHorizontal();
    }

    private static VoxelShape createShape(boolean north, boolean south, boolean east, boolean west, boolean up, boolean down, Direction facing) {
        VoxelShape core;

        VoxelShape debug_empty = Block.createCuboidShape(7.0D, 7.0D, 7.0D, 9.0D, 9.0D, 9.0D);

        VoxelShape u = VoxelShapes.union(
                Block.createCuboidShape(4.0D, 15.0D, 4.0D, 12.0D, 16.0D, 12.0D),
                Block.createCuboidShape(5.0D, 14.0D, 5.0D, 11.0D, 15.0D, 11.0D));
        VoxelShape d = VoxelShapes.union(
                    Block.createCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 1.0D, 12.0D),
                    Block.createCuboidShape(5.0D, 1.0D, 5.0D, 11.0D, 2.0D, 11.0D));
        VoxelShape n = VoxelShapes.union(
                Block.createCuboidShape(4.0D, 1.0D, 0.0D, 12.0D, 15.0D, 1.0D),
                Block.createCuboidShape(5.0D, 1.0D, 1.0D, 11.0D, 15.0D, 2.0D),
                Block.createCuboidShape(4.0D, 15.0D, 0.0D, 12.0D, 16.0D, 4.0D),
                Block.createCuboidShape(5.0D, 14.0D, 2.0D, 11.0D, 15.0D, 5.0D),
                Block.createCuboidShape(5.0D, 1.0D, 2.0D, 11.0D, 2.0D, 5.0D),
                Block.createCuboidShape(4.0D, 0.0D, 0.0D, 12.0D, 1.0D, 4.0D));
        VoxelShape ng = VoxelShapes.union(
                Block.createCuboidShape(6.0D, 2.0D, 0.0D, 10.0D, 14.0D, 2.0D),
                Block.createCuboidShape(4.0D, 15.0D, 0.0D, 12.0D,16.0D,4.0D),
                Block.createCuboidShape(5.0D,14.0D,0.0D,11.0D,15.0D,5.0D),
                Block.createCuboidShape(4.0D, 0.0D, 0.0D, 12.0D, 1.0D, 4.0D),
                Block.createCuboidShape(5.0D, 1.0D, 0.0D, 11.0D, 2.0D, 5.0D));
        VoxelShape nsg = Block.createCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
        VoxelShape ewg = Block.createCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);

        core = debug_empty;

        if (!(north || south || east || west)) {
            if (facing.equals(Direction.NORTH) || facing.equals(Direction.SOUTH)) {
                // e-w
                core = ewg;
            } else if (facing.equals(Direction.EAST) || facing.equals(Direction.WEST)) {
                // n-s
                core = nsg;
            }
        } else if (!(east || west)) {
            // n-s
            core = nsg;
        } else if (!(north || south)){
            // e-w
            core = ewg;
        } else if (north && south && east && !west) {
            // |-
        } else if (north && south && !east && west) {
            // -|
        } else if (!north && south && east && west) {
            // T
        } else if (north && !south && east && west) {
            // upside down T
        } else {
            // x
        }

        return core;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShape(state);
    }

    private VoxelShape getShape(BlockState state) {
        int index = getShapeIndex(state);
        if (shape[index] != null) {
            return shape[index];
        }
        return shape[index] = createShape(state.get(NORTH),
                state.get(SOUTH),
                state.get(EAST),
                state.get(WEST),
                state.get(UP),
                state.get(DOWN),
                state.get(FACING));
    }

    protected int getShapeIndex(BlockState state) {
        return this.SHAPE_INDEX_CACHE.computeIntIfAbsent(state, (blockState) -> {
            int i = 0;
            if (blockState.get(NORTH)) {
                i |= getDirectionMask(Direction.NORTH);
            }
            if (blockState.get(EAST)) {
                i |= getDirectionMask(Direction.EAST);
            }
            if (blockState.get(SOUTH)) {
                i |= getDirectionMask(Direction.SOUTH);
            }
            if (blockState.get(WEST)) {
                i |= getDirectionMask(Direction.WEST);
            }
            return i;
        });
    }

    private BooleanProperty getPropForDir(Direction direction) {
        switch (direction) {
            case NORTH:
                return NORTH;
            case WEST:
                return WEST;
            case EAST:
                return EAST;
            case SOUTH:
                return SOUTH;
            case UP:
                return UP;
            case DOWN:
                return DOWN;
        }
        throw new IllegalArgumentException("Wrong direction for VacuumGlass!");
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    private BooleanProperty propFromDirection(Direction direction) {
        switch (direction) {
            case NORTH:
                return NORTH;
            case SOUTH:
                return SOUTH;
            case EAST:
                return EAST;
            case WEST:
                return WEST;
            case UP:
                return UP;
            case DOWN:
                return DOWN;
            default:
                return null;
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        BlockState state = this.getDefaultState();
        for (Direction direction : Direction.values()) {
            Block block = context.getWorld().getBlockState(context.getBlockPos().offset(direction)).getBlock();
            if (block instanceof VacuumGlass) {
                state = state.with(propFromDirection(direction), true);
            }
        }
        FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
        return state.with(FACING, context.getPlayerFacing())
                    .with(FLUID, Registry.FLUID.getId(fluidState.getFluid()))
                    .with(FlowableFluid.LEVEL, Math.max(fluidState.getLevel(), 1));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!state.get(FLUID).equals(new Identifier("empty"))) {
            world.getFluidTickScheduler().schedule(pos, Registry.FLUID.get(state.get(FLUID)), Registry.FLUID.get(state.get(FLUID)).getTickRate(world));
        }
        return state.with(getPropForDir(facing), this.canConnect(state, pos, neighborState, neighborPos));
    }

    public boolean canConnect(BlockState state, BlockPos thisPos, BlockState neighborState, BlockPos neighborPos) {
        if (neighborState.getBlock() instanceof VacuumGlass) {
            if ((thisPos.down().equals(neighborPos)) || (thisPos.up().equals(neighborPos))) {
                // special cases: allow us to introduce ourselves
                boolean sn = state.get(NORTH);
                boolean ss = state.get(SOUTH);
                boolean se = state.get(EAST);
                boolean sw = state.get(WEST);
                boolean nn = neighborState.get(NORTH);
                boolean ns = neighborState.get(SOUTH);
                boolean ne = neighborState.get(EAST);
                boolean nw = neighborState.get(WEST);

                Direction sf = state.get(FACING);
                Direction nf = neighborState.get(FACING);

                if (sn || ss || se || sw || nn || ns || ne || nw) {
                    // only connected on east-west
                    if (!(sn || ss) && !(nn || ns)) {
                        // if all east-west sides active, works
                        if (se && sw && ne && nw) {
                            return true;
                        // if one of them is not connected to anything, it def. works
                        } else if (!(se || sw) && (sf.equals(Direction.NORTH) || sf.equals(Direction.SOUTH)) ||
                                   !(ne || nw) && (nf.equals(Direction.NORTH) || nf.equals(Direction.SOUTH))) {
                            return true;
                        // if one has one connection and one has two
                        } else if ((se?1:0) + (sw?1:0) + (ne?1:0) + (nw?1:0) == 3) {
                            return true;
                        }
                        return ((se == ne) && !sw && !nw) ||
                               ((sw == nw) && !se && !ne);
                    // only connected on north-south
                    } else if (!(se || sw) && !(ne || nw)) {
                        if (sn && nn && ss && ns) {
                            return true;
                        // if one of them is not connected to anything, it def. works
                        } else if (!(sn || ss) && (sf.equals(Direction.EAST) || sf.equals(Direction.WEST)) ||
                                   !(nn || ne) && (nf.equals(Direction.EAST) || nf.equals(Direction.WEST))) {
                            return true;
                        } else if ((sn?1:0) + (ss?1:0) + (nn?1:0) + (ns?1:0) == 3) {
                            return true;
                        }
                        return ((sn == nn) && !ss && !ns) ||
                               ((ss == ns) && !sn && !nn);
                    } else {
                        return (sn == nn) && (ss == ns) && (se == ne) && (sw == nw);
                    }
                }
                return sf.equals(nf);
            }
            return true;
        }
        return false;
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState_1) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        FluidState state1 = Registry.FLUID.get(state.get(FLUID)).getDefaultState();
        if (state1.getEntries().containsKey(FlowableFluid.LEVEL)) {
            state1 = state1.with(FlowableFluid.LEVEL, state.get(FlowableFluid.LEVEL));
        }
        return state1;
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(NORTH, EAST, WEST, SOUTH, UP, DOWN, FACING, FLUID, FlowableFluid.LEVEL);
    }

    static {
        NORTH = Properties.NORTH;
        EAST = Properties.EAST;
        SOUTH = Properties.SOUTH;
        WEST = Properties.WEST;
        UP = Properties.UP;
        DOWN = Properties.DOWN;
        FACING = Properties.HORIZONTAL_FACING;
    }
}