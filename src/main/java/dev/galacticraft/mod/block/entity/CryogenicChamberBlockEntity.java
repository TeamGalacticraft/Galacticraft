package dev.galacticraft.mod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CryogenicChamberBlockEntity extends BlockEntity {
    public CryogenicChamberBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(GCBlockEntityTypes.CRYOGENIC_CHAMBER, blockPos, blockState);
    }
}
