package com.hrznstudio.galacticraft.blocks.special.aluminumwire;

import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Tickable;

public class AluminumWireBlockEntity extends BlockEntity implements Tickable {
    protected int networkId;

    public AluminumWireBlockEntity() {
        super(GalacticraftBlockEntities.ALUMINUM_WIRE_TYPE);
    }

    @Override
    public void tick() {
        /*if (world.getBlockState(pos).getBlock() == GalacticraftBlocks.ALUMINUM_WIRE_BLOCK) {
            for (Direction direction : Direction.values()) {
                if (world.getBlockState(pos).get(BooleanProperty.create("attached_" + direction.getName()))) {
                    if (world.getBlockState(getPosFromDirection(direction, pos)).getBlock() instanceof WireBlock) {
                        connections.replace(direction, WireConnectionType.WIRE);
                    } else if (world.getBlockState(getPosFromDirection(direction, pos)).getBlock() instanceof MachineBlock) {
                        connections.replace(direction, ((WireConnectable)world.getBlockState(getPosFromDirection(direction, pos)).getBlock()).canWireConnect(world, direction.getOpposite(), pos, getPosFromDirection(direction, pos)));
                    } else {
                        connections.replace(direction, WireConnectionType.NONE);
                    }
                } else {
                    connections.replace(direction, WireConnectionType.NONE);
                }
            }
        }*/
        networkId = new WireNetwork(this).getId();
        WireNetwork.getNetworkFromId(networkId).update();
    }

    public void init() {
        networkId = new WireNetwork(this).getId(); //use id for easy replacement
    }

}
