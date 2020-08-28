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
 *
 */

package com.hrznstudio.galacticraft.block.special.aluminumwire.tier1;

import com.hrznstudio.galacticraft.accessor.ServerWorldAccessor;
import com.hrznstudio.galacticraft.api.block.WireBlock;
import com.hrznstudio.galacticraft.api.wire.WireConnectable;
import com.hrznstudio.galacticraft.api.wire.WireConnectionType;
import com.hrznstudio.galacticraft.api.wire.WireNetwork;
import com.hrznstudio.galacticraft.util.ConnectingBlockUtils;
import io.github.cottonmc.component.UniversalComponents;
import nerdhub.cardinal.components.api.component.ComponentProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class AluminumWireBlock extends WireBlock implements WireConnectable {

    // If we start at 8,8,8 and subtract/add to/from 8, we do operations starting from the centre.
    private static final VoxelShape NORTH = createCuboidShape(8 - 3, 8 - 3, 0, 8 + 3, 8 + 3, 8 + 3);
    private static final VoxelShape EAST = createCuboidShape(8 - 3, 8 - 3, 8 - 3, 16, 8 + 3, 8 + 3);
    private static final VoxelShape SOUTH = createCuboidShape(8 - 3, 8 - 3, 8 - 3, 8 + 3, 8 + 3, 16);
    private static final VoxelShape WEST = createCuboidShape(0, 8 - 3, 8 - 3, 8 + 3, 8 + 3, 8 + 3);
    private static final VoxelShape UP = createCuboidShape(8 - 3, 8 - 3, 8 - 3, 8 + 3, 16, 8 + 3);
    private static final VoxelShape DOWN = createCuboidShape(8 - 3, 0, 8 - 3, 8 + 3, 8 + 3, 8 + 3);
    private static final VoxelShape NONE = createCuboidShape(8 - 3, 8 - 3, 8 - 3, 8 + 3, 8 + 3, 8 + 3);    // 6x6x6 box in the center.

    public AluminumWireBlock(Settings settings) {
        super(settings);
        setDefaultState(this.getStateManager().getDefaultState().with(ConnectingBlockUtils.ATTACHED_NORTH, false).with(ConnectingBlockUtils.ATTACHED_EAST, false).with(ConnectingBlockUtils.ATTACHED_SOUTH, false).with(ConnectingBlockUtils.ATTACHED_WEST, false).with(ConnectingBlockUtils.ATTACHED_UP, false).with(ConnectingBlockUtils.ATTACHED_DOWN, false));
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        super.onBlockAdded(state, world, pos, oldState, moved);
        if (!world.isClient) {
            WireNetwork network = ((ServerWorldAccessor) world).getNetworkManager().getNetwork(pos);
            if (network == null) network = new WireNetwork(pos, ((ServerWorld) world));
            for (Direction d : Direction.values()) {
                if (state.get(getPropForDirection(d)) && world.getBlockState(pos.offset(d)).getBlock() instanceof WireConnectable) {
                    WireConnectionType type = ((WireConnectable) world.getBlockState(pos.offset(d)).getBlock()).canWireConnect(world, d.getOpposite(), pos, pos.offset(d));
                    if (type == WireConnectionType.WIRE) {
                        WireNetwork network1 = ((ServerWorldAccessor) world).getNetworkManager().getNetwork(pos.offset(d));
                        if (network1 != network) {
                            if (network1 != null) {
                                network = network1.merge(network); // prefer other network rather than this one
                            } else {
                                network.addWire(pos.offset(d));
                            }
                            //this.updateNeighborStates(state, world, pos, 3);
                        }
                    } else if (type != WireConnectionType.NONE) {
                        if (type == WireConnectionType.ENERGY_INPUT) {
                            network.addConsumer(pos.offset(d));
                        } else {
                            network.addProducer(pos.offset(d));
                        }
                    }
                }
            }
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext context) {
        return ConnectingBlockUtils.getVoxelShape(blockState, NORTH, SOUTH, EAST, WEST, UP, DOWN, NONE);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        BlockState state = this.getDefaultState();
        for (Direction direction : Direction.values()) {
            Block block = context.getWorld().getBlockState(context.getBlockPos().offset(direction)).getBlock();
            if (block instanceof WireConnectable) {
                if (((WireConnectable) block).canWireConnect(context.getWorld(), direction.getOpposite(), context.getBlockPos(), context.getBlockPos().offset(direction)) != WireConnectionType.NONE) {
                    state = state.with(propFromDirection(direction), true);
                }
            } else if (block instanceof ComponentProvider && ((ComponentProvider) block).hasComponent(UniversalComponents.CAPACITOR_COMPONENT)) {
                state = state.with(propFromDirection(direction), true);
            }
        }

        return state;
    }

    private BooleanProperty propFromDirection(Direction direction) {
        return ConnectingBlockUtils.getBooleanProperty(direction, ConnectingBlockUtils.ATTACHED_NORTH, ConnectingBlockUtils.ATTACHED_SOUTH, ConnectingBlockUtils.ATTACHED_EAST, ConnectingBlockUtils.ATTACHED_WEST, ConnectingBlockUtils.ATTACHED_UP, ConnectingBlockUtils.ATTACHED_DOWN);
    }

    private BooleanProperty getPropForDirection(Direction dir) {
        return ConnectingBlockUtils.getBooleanProperty(dir, ConnectingBlockUtils.ATTACHED_SOUTH, ConnectingBlockUtils.ATTACHED_EAST, ConnectingBlockUtils.ATTACHED_WEST, ConnectingBlockUtils.ATTACHED_NORTH, ConnectingBlockUtils.ATTACHED_UP, ConnectingBlockUtils.ATTACHED_DOWN);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction dir, BlockState neighbor, WorldAccess world, BlockPos thisWire, BlockPos otherConnectable) {
        return state.with(getPropForDirection(dir), (
                !(neighbor).isAir()
                        && neighbor.getBlock() instanceof WireConnectable
                        // get opposite of direction so the WireConnectable can check from its perspective.
                        && (((WireConnectable) neighbor.getBlock()).canWireConnect(world, dir.getOpposite(), thisWire, otherConnectable) != WireConnectionType.NONE)
        ));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ConnectingBlockUtils.ATTACHED_NORTH, ConnectingBlockUtils.ATTACHED_EAST, ConnectingBlockUtils.ATTACHED_SOUTH, ConnectingBlockUtils.ATTACHED_WEST, ConnectingBlockUtils.ATTACHED_UP, ConnectingBlockUtils.ATTACHED_DOWN);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView view, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView view, BlockPos pos) {
        return true;
    }
}