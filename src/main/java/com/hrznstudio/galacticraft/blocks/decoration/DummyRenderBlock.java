package com.hrznstudio.galacticraft.blocks.decoration;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;

public class DummyRenderBlock extends Block {
    public DummyRenderBlock(Settings block$Settings_1) {
        super(block$Settings_1);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState_1) {
        return BlockRenderType.MODEL;
    }

    @Override
    public float getBlastResistance() {
        return 100000000000000000.0F;
    }
}
