package com.hrznstudio.galacticraft.api.machine;

import com.hrznstudio.galacticraft.api.block.AutomationType;
import com.hrznstudio.galacticraft.api.block.ConfiguredSideOption;
import com.hrznstudio.galacticraft.api.block.entity.MachineBlockEntity;
import com.hrznstudio.galacticraft.api.block.util.BlockFace;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class SideConfiguration {
    private final ConfiguredSideOption front;
    private final ConfiguredSideOption back;
    private final ConfiguredSideOption left;
    private final ConfiguredSideOption right;
    private final ConfiguredSideOption top;
    private final ConfiguredSideOption bottom;
    private final MachineBlockEntity machine;

    public SideConfiguration(MachineBlockEntity machine) {
        this.machine = machine;

        this.front = new ConfiguredSideOption(AutomationType.NONE);
        this.back = new ConfiguredSideOption(AutomationType.NONE);
        this.left = new ConfiguredSideOption(AutomationType.NONE);
        this.right = new ConfiguredSideOption(AutomationType.NONE);
        this.top = new ConfiguredSideOption(AutomationType.NONE);
        this.bottom = new ConfiguredSideOption(AutomationType.NONE);
    }

    public CompoundTag toTag(CompoundTag tag) {
        tag.put("front", this.front.toTag(new CompoundTag()));
        tag.put("back", this.back.toTag(new CompoundTag()));
        tag.put("left", this.left.toTag(new CompoundTag()));
        tag.put("right", this.right.toTag(new CompoundTag()));
        tag.put("top", this.top.toTag(new CompoundTag()));
        tag.put("bottom", this.bottom.toTag(new CompoundTag()));
        return tag;
    }

    public void fromTag(CompoundTag tag) {
        this.front.fromTag(tag.getCompound("front"));
        this.back.fromTag(tag.getCompound("back"));
        this.left.fromTag(tag.getCompound("left"));
        this.right.fromTag(tag.getCompound("right"));
        this.top.fromTag(tag.getCompound("top"));
        this.bottom.fromTag(tag.getCompound("bottom"));
    }

    /**
     * Please do not modify the returned {@link ConfiguredSideOption}
     *
     * @param face the block face to pull the option from
     * @return a {@link ConfiguredSideOption} assignd to the given face.
     */
    public ConfiguredSideOption get(@NotNull BlockFace face) {
        switch (face) {
            case FRONT:
                return this.front;
            case TOP:
                return this.top;
            case BACK:
                return this.back;
            case RIGHT:
                return this.right;
            case LEFT:
                return this.left;
            case BOTTOM:
                return this.bottom;
        }
        throw new AssertionError();
    }
}
