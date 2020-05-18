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

package com.hrznstudio.galacticraft.api.block;

import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.block.entity.WireBlockEntity;
import com.hrznstudio.galacticraft.api.wire.NetworkManager;
import com.hrznstudio.galacticraft.api.wire.WireConnectionType;
import com.hrznstudio.galacticraft.api.wire.WireNetwork;
import com.hrznstudio.galacticraft.util.WireConnectable;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class WireBlock extends BlockWithEntity implements WireConnectable {
    public WireBlock(Settings settings) {
        super(settings);
    }

    @Override
    @Deprecated
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient() && Galacticraft.configManager.get().isDebugLogEnabled()) {
            Galacticraft.logger.info(NetworkManager.getManagerForWorld(world).getNetwork(pos));
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    @Deprecated
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        super.onBlockAdded(state, world, pos, oldState, moved);
        if (!world.isClient) {
            WireNetwork network = NetworkManager.getManagerForWorld(world).getNetwork(pos);
            if (network == null) network = new WireNetwork(pos, world.getDimension().getType().getRawId());
            for (Direction d : Direction.values()) {
                if (world.getBlockState(pos.offset(d)).getBlock() instanceof WireConnectable) {
                    WireConnectionType type = ((WireConnectable) world.getBlockState(pos.offset(d)).getBlock()).canWireConnect(world, d.getOpposite(), pos, pos.offset(d));
                    if (type == WireConnectionType.WIRE) {
                        WireNetwork network1 = NetworkManager.getManagerForWorld(world).getNetwork(pos.offset(d));
                        if (network1 != network) {
                            if (network1 != null) {
                                network = network1.merge(network); // prefer other network rather than this one
                            } else {
                                network.addWire(pos.offset(d));
                            }
                            state.updateNeighbors(world, pos, 3);
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
    public BlockState getStateForNeighborUpdate(BlockState state, Direction dir, BlockState otherState, WorldAccess world, BlockPos pos, BlockPos updated) {
        WireConnectionType type = WireConnectionType.NONE;
        if (otherState.getBlock() instanceof WireConnectable) {
            type = ((WireConnectable) otherState.getBlock()).canWireConnect(world, dir.getOpposite(), pos, updated);
        }

        if (!world.isClient() && type != WireConnectionType.NONE) {
            if (world.getBlockState(updated).getBlock() instanceof WireConnectable) {
                WireNetwork network = NetworkManager.getManagerForWorld(world).getNetwork(pos);
                if (network == null) network = new WireNetwork(pos, world.getDimension().getType().getRawId());
                if (type == WireConnectionType.WIRE) {
                    WireNetwork network1 = NetworkManager.getManagerForWorld(world).getNetwork(updated);
                    if (network1 != network) {
                        if (network1 != null) {
                            network1.merge(network); // prefer other network rather than this one
                        } else {
                            network.addWire(updated);
                        }
                        state.updateNeighbors(world, pos, 3);
                    }
                } else if (type == WireConnectionType.ENERGY_INPUT) {
                    network.addConsumer(updated);
                } else {
                    network.addProducer(updated);
                }

            }
        }
        return state;
    }

    @Override
    @Deprecated
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onBlockRemoved(state, world, pos, newState, moved);
        if (!world.isClient()) {
            WireNetwork myNet = NetworkManager.getManagerForWorld(world).getNetwork(pos);
            NetworkManager.getManagerForWorld(world).removeWire(pos);
            myNet.removeWire(pos);
            WireNetwork network = NetworkManager.getManagerForWorld(world).getNetwork(pos);
            if (network == null) return;
            for (Direction d : Direction.values()) {
                if (world.getBlockState(pos.offset(d)).getBlock() instanceof WireConnectable) {
                    WireConnectionType type = ((WireConnectable) world.getBlockState(pos.offset(d)).getBlock()).canWireConnect(world, d.getOpposite(), pos, pos.offset(d));
                    if (type != WireConnectionType.WIRE && type != WireConnectionType.NONE) {
                        if (type == WireConnectionType.ENERGY_INPUT) {
                            network.removeConsumer(pos.offset(d));
                        } else {
                            network.removeProducer(pos.offset(d));
                        }
                    }
                }
            }
        }
    }

    @Override
    @Nonnull
    public WireConnectionType canWireConnect(WorldAccess world, Direction opposite, BlockPos connectionSourcePos, BlockPos connectionTargetPos) {
        return WireConnectionType.WIRE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new WireBlockEntity();
    }
}
