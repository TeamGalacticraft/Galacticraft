package dev.galacticraft.mod.block.entity;

import dev.galacticraft.mod.api.block.MultiBlockPart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CryogenicChamberPartBlockEntity extends BlockEntity implements MultiBlockPart {
    public BlockPos basePos = BlockPos.ZERO;


    public CryogenicChamberPartBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(GCBlockEntityTypes.CRYOGENIC_CHAMBER_PART, blockPos, blockState);
    }

    @Override
    public void setBasePos(BlockPos pos) {
        this.basePos = pos;
        this.setChanged();
    }
}
