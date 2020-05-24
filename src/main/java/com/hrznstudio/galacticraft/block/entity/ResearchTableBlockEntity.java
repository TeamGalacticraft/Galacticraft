package com.hrznstudio.galacticraft.block.entity;

import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;

public class ResearchTableBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
    public ResearchTableBlockEntity() {
        super(GalacticraftBlockEntities.RESEARCH_TABLE_TYPE);
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {

    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return compoundTag;
    }
}
