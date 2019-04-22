package com.hrznstudio.galacticraft.blocks.environment;

import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class UnlitTorchBlock extends TorchBlock {

    public UnlitTorchBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void randomDisplayTick(BlockState blockState_1, World world_1, BlockPos blockPos_1, Random random_1) {
        // stop partials from spawning
    }
}
