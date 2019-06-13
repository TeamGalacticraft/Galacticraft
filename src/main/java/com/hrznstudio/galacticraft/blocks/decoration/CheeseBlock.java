package com.hrznstudio.galacticraft.blocks.decoration;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class CheeseBlock extends CakeBlock {

    private static final IntProperty BITES = IntProperty.of("bites", 0, 6);
    private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public CheeseBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateFactory.getDefaultState().with(WATERLOGGED, false).with(BITES, 0));
    }

    @Override
    public void appendProperties(StateFactory.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(WATERLOGGED).add(BITES);
    }
}
