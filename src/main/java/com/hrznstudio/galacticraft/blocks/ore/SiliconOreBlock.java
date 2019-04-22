package com.hrznstudio.galacticraft.blocks.ore;

import com.hrznstudio.galacticraft.blocks.GalacticraftBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class SiliconOreBlock extends OreBlock {

    public SiliconOreBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getPickStack(BlockView blockView, BlockPos pos, BlockState state) {
        return new ItemStack(GalacticraftBlocks.SILICON_ORE_BLOCK_ITEM);
    }
}
