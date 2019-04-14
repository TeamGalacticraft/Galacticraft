package io.github.teamgalacticraft.galacticraft.blocks.machines.basicsolarpanel;

import io.github.teamgalacticraft.galacticraft.entity.GalacticraftBlockEntities;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class BasicSolarPanelPartBlockEntity extends BlockEntity {
    public BlockPos basePos;

    public BasicSolarPanelPartBlockEntity() {
        super(GalacticraftBlockEntities.BASIC_SOLAR_PANEL_PART_TYPE);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag = super.toTag(tag);
        CompoundTag baseTag = new CompoundTag();
        baseTag.putInt("X", this.basePos.getX());
        baseTag.putInt("Y", this.basePos.getY());
        baseTag.putInt("Z", this.basePos.getZ());

        tag.put("Base", baseTag);
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        CompoundTag base = tag.getCompound("Base");
        this.basePos = new BlockPos(base.getInt("X"), base.getInt("Y"), base.getInt("Z"));
    }
}