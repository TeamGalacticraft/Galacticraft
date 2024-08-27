package dev.galacticraft.mod.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

interface BlockCheck {
    boolean checkSolid(BlockPos block, ServerLevel world);
    SealerGroupings checkCalculated(BlockPos block, ServerLevel world);
}
