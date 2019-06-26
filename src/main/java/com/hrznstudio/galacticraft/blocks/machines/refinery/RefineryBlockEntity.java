package com.hrznstudio.galacticraft.blocks.machines.refinery;

import com.hrznstudio.galacticraft.api.block.entity.ConfigurableElectricMachineBlockEntity;
import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;

public class RefineryBlockEntity extends ConfigurableElectricMachineBlockEntity {

    public RefineryBlockEntity() {
        super(GalacticraftBlockEntities.REFINERY_TYPE);
    }

    @Override
    protected int getInvSize() {
        return 0;
    }
}
