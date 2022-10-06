package dev.galacticraft.mod.block.entity;

import dev.galacticraft.mod.api.block.MultiBlockPart;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CryogenicChamberPartBlockEntity extends BlockEntity implements MultiBlockPart {
    public BlockPos basePos = BlockPos.ZERO;


    public CryogenicChamberPartBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(GCBlockEntityTypes.CRYOGENIC_CHAMBER_PART, blockPos, blockState);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.basePos != BlockPos.ZERO) {
            tag.putLong("Base", this.basePos.asLong());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Base")) {
            this.basePos = BlockPos.of(tag.getLong("Base"));
        }
    }

    @Override
    public void setBasePos(BlockPos pos) {
        this.basePos = pos;
        this.setChanged();
    }
}
