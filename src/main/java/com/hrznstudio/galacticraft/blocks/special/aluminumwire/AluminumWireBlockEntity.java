package com.hrznstudio.galacticraft.blocks.special.aluminumwire;

import com.hrznstudio.galacticraft.api.entity.WireBlockEntity;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Tickable;

public class AluminumWireBlockEntity extends BlockEntity implements Tickable, WireBlockEntity {
    private boolean tickedOnce = false;
    protected long networkId;
    protected BlockEntity[] consumers = new BlockEntity[6];
    protected BlockEntity[] producers = new BlockEntity[6];

    public AluminumWireBlockEntity() {
        super(GalacticraftBlockEntities.ALUMINUM_WIRE_TYPE);
    }

    @Override
    public void tick() {
        if (!tickedOnce) {
            networkId = new WireNetwork(this).getId();
            tickedOnce = true;
            WireNetwork.blockPlaced();
        }
        if (WireUtils.getNetworkFromId(getNetworkId()) == null) {
            networkId = new WireNetwork(this).getId();
            WireNetwork.blockPlaced();
            tickedOnce = true;
        }
        //Galacticraft.logger.info(networkId);


    }

    public void init() {
        networkId = new WireNetwork(this).getId(); //use id for easy replacement
        tickedOnce = true;
    }

    public long getNetworkId() {
        return networkId;
    }
}
