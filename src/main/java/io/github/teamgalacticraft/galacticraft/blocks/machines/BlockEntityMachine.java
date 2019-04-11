package io.github.teamgalacticraft.galacticraft.blocks.machines;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public abstract class BlockEntityMachine extends BlockEntity {


    public BlockEntityMachine(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }
}
