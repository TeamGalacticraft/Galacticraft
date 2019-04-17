package io.github.teamgalacticraft.galacticraft.blocks.machines.refinery;

import io.github.teamgalacticraft.galacticraft.blocks.machines.MachineBlockEntity;
import io.github.teamgalacticraft.galacticraft.entity.GalacticraftBlockEntities;

public class RefineryBlockEntity extends MachineBlockEntity {

    public RefineryBlockEntity() {
        super(GalacticraftBlockEntities.REFINERY_TYPE);
    }

    @Override
    protected int getInvSize() {
        return 0;
    }
}
