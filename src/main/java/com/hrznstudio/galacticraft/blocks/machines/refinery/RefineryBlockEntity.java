package com.hrznstudio.galacticraft.blocks.machines.refinery;

import com.hrznstudio.galacticraft.blocks.machines.MachineBlockEntity;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;

public class RefineryBlockEntity extends MachineBlockEntity {

    public RefineryBlockEntity() {
        super(GalacticraftBlockEntities.REFINERY_TYPE);
    }

    @Override
    protected int getInvSize() {
        return 0;
    }
}
