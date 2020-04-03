package com.hrznstudio.galacticraft.api.block;

import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.wire.NetworkManager;
import com.hrznstudio.galacticraft.api.wire.WireConnectionType;
import com.hrznstudio.galacticraft.api.wire.WireNetwork;
import com.hrznstudio.galacticraft.util.WireConnectable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class WireBlock extends Block implements WireConnectable {
    public WireBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient() && Galacticraft.configManager.get().isDebugLogEnabled()) {
            Galacticraft.logger.info(NetworkManager.getManagerForWorld(world).getNetwork(pos));
        }
        return super.activate(state, world, pos, player, hand, hit);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient) {
            WireNetwork network = NetworkManager.getManagerForWorld(world).getNetwork(pos);
            if (network == null) network = new WireNetwork(pos, world.dimension.getType().getRawId());
            for (Direction d : Direction.values()) {
                if (world.getBlockState(pos.offset(d)).getBlock() instanceof WireConnectable) {
                    WireConnectionType type = ((WireConnectable) world.getBlockState(pos.offset(d)).getBlock()).canWireConnect(world, d.getOpposite(), pos, pos.offset(d));
                    if (type == WireConnectionType.WIRE) {
                        WireNetwork network1 = NetworkManager.getManagerForWorld(world).getNetwork(pos.offset(d));
                        if (network1 != null) {
                            network = network1.merge(network); // prefer other network rather than this one
                        } else {
                            network.addWire(pos.offset(d));
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
    public BlockState getStateForNeighborUpdate(BlockState state, Direction dir, BlockState otherState, IWorld world, BlockPos pos, BlockPos updated) {
        WireConnectionType type = WireConnectionType.NONE;
        if (otherState.getBlock() instanceof WireConnectable) {
            type = ((WireConnectable) otherState.getBlock()).canWireConnect(world, dir.getOpposite(), pos, updated);
        }

        if (!world.isClient()) {
            WireNetwork myNet = NetworkManager.getManagerForWorld(world).getNetwork(pos);
            if (type == WireConnectionType.WIRE) {
                WireNetwork network1 = NetworkManager.getManagerForWorld(world).getNetwork(updated);
                if (!myNet.equals(network1)) {
                    if (network1 != null) {
                        network1.merge(myNet); // prefer other network rather than this one
                    } else {
                        myNet.addWire(updated);
                    }
                }
            } else if (type != WireConnectionType.NONE) {
                if (type == WireConnectionType.ENERGY_INPUT) {
                    myNet.addConsumer(updated);
                } else {
                    myNet.addProducer(updated);
                }
            } else {
                if (!myNet.removeConsumer(pos)) {
                    myNet.removeProducer(pos);
                }
            }
        }
        return state;
    }

    @Override
    public void onBroken(IWorld world, BlockPos pos, BlockState state) {
        super.onBroken(world, pos, state);
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
    public WireConnectionType canWireConnect(IWorld world, Direction opposite, BlockPos connectionSourcePos, BlockPos connectionTargetPos) {
        return WireConnectionType.WIRE;
    }
}
