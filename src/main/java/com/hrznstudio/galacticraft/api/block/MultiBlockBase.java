package com.hrznstudio.galacticraft.api.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public interface MultiBlockBase {
    void onPartDestroyed(World world, PlayerEntity player, BlockState state, BlockPos pos, BlockState partState, BlockPos partPos);

    List<BlockPos> getOtherParts(BlockState state, BlockPos pos);
}
