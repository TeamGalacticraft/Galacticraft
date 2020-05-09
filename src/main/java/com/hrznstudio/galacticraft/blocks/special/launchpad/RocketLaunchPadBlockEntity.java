package com.hrznstudio.galacticraft.blocks.special.launchpad;

import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

public class RocketLaunchPadBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
    private BlockPos centerBlock = null;

    public RocketLaunchPadBlockEntity() {
        super(GalacticraftBlockEntities.LAUNCH_PAD_TYPE);
    }

    public BlockPos getCenterBlock() {
        return centerBlock;
    }

    public void setCenterBlock(BlockPos centerBlock) {
        this.centerBlock = centerBlock;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.centerBlock = new BlockPos(tag.getInt("cbX"), tag.getInt("cbY"), tag.getInt("cbZ"));
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt("cbX", centerBlock.getX());
        tag.putInt("cbY", centerBlock.getY());
        tag.putInt("cbZ", centerBlock.getZ());
        return super.toTag(tag);
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        fromTag(compoundTag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return toTag(compoundTag);
    }
}
