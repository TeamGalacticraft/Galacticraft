package io.github.teamgalacticraft.galacticraft.blocks.fluid;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.BaseFluid;

public class CrudeOilBlock extends FluidBlock {

    public CrudeOilBlock(BaseFluid fluid, Settings settings) {
        super(fluid, settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
