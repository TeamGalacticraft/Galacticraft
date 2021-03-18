package com.hrznstudio.galacticraft.api.machine;

import alexiil.mc.lib.attributes.misc.Saveable;
import net.minecraft.nbt.CompoundTag;

public class MachineConfiguration implements Saveable {
    private MachineStatus status = MachineStatus.NULL;
    private RedstoneState redstone = RedstoneState.IGNORE;
    private final SideConfiguration configuration;
    private final SecurityInfo security;

    public MachineConfiguration(SideConfiguration configuration, SecurityInfo security) {
        this.configuration = configuration;
        this.security = security;
    }

    public void setStatus(MachineStatus status) {
        this.status = status;
    }

    public void setRedstone(RedstoneState redstone) {
        this.redstone = redstone;
    }

    public SideConfiguration getConfiguration() {
        return configuration;
    }

    public SecurityInfo getSecurity() {
        return security;
    }

    public MachineStatus getStatus() {
        return status;
    }

    public RedstoneState getRedstone() {
        return redstone;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.put("security", this.getSecurity().toTag(new CompoundTag()));
        tag.put("configuration", this.getConfiguration().toTag(new CompoundTag()));
        this.redstone.toTag(tag);
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        this.getSecurity().fromTag(tag.getCompound("security"));
        this.getConfiguration().fromTag(tag.getCompound("configuration"));
        this.redstone = RedstoneState.fromTag(tag);
    }
}
