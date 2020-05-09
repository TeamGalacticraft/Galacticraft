package com.hrznstudio.galacticraft.blocks.special.rocketlaunchpad;

import com.hrznstudio.galacticraft.entity.GalacticraftBlockEntities;
import com.hrznstudio.galacticraft.entity.rocket.RocketEntity;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Box;

import java.util.UUID;

public class RocketLaunchPadBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    private UUID rocketEntityUUID = null;
    private int rocketEntityId = Integer.MIN_VALUE;

    public RocketLaunchPadBlockEntity() {
        super(GalacticraftBlockEntities.LAUNCH_PAD_TYPE);
    }

    public void setRocketEntityUUID(UUID rocketEntityUUID) {
        this.rocketEntityUUID = rocketEntityUUID;

    }

    public boolean hasRocket() {
        return this.rocketEntityUUID != null && this.rocketEntityId != Integer.MIN_VALUE;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        if (tag.contains("rocketUuid")) {
            this.rocketEntityUUID = tag.getUuid("rocketUuid");
            for (Entity entity : world.getEntities(null, new Box(-3, -2, -3, 3, 9, 3))) {
                if (entity instanceof RocketEntity) {
                    if (entity.getUuid() == this.rocketEntityUUID) {
                        this.rocketEntityId = entity.getEntityId();
                    }
                }
            }
            if (rocketEntityId == Integer.MIN_VALUE) throw new IllegalStateException("Unable to find linked rocket!");
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (hasRocket()) tag.putUuid("rocketUuid", rocketEntityUUID);
        return super.toTag(tag);
    }

    public void setRocketEntityId(int entityId) {
        rocketEntityId = entityId;
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {

    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        return compoundTag;
    }
}
