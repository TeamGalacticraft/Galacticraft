package com.hrznstudio.galacticraft.accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public interface WorldRendererAccessor {
    default void addChunkToRebuild(BlockPos pos) {
        addChunkToRebuild(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
    }

    void addChunkToRebuild(int x, int y, int z);
}
