package dev.galacticraft.mod.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

public class SolidCheck implements BlockCheck {
    @Override
    public boolean checkSolid(BlockPos block, ServerLevel world) {
        if (Block.isShapeFullBlock(world.getBlockState(block).getShape(world, block))) {
            return true;
        }
        return false;
    }

    @Override
    public SealerGroupings checkCalculated(BlockPos block, ServerLevel world) {
        if (SealerManager.INSTANCE.getInsideSealerGroupings(block, world.dimensionType()) != null)
        {
            return SealerManager.INSTANCE.getInsideSealerGroupings(block, world.dimensionType());
        } else if (SealerManager.INSTANCE.getOutsideSealerGroupings(block, world.dimensionType()) != null) {
            return SealerManager.INSTANCE.getOutsideSealerGroupings(block, world.dimensionType());
        }
        return null;
    }
}
