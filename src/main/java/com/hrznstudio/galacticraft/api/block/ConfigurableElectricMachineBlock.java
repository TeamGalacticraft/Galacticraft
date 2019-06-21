package com.hrznstudio.galacticraft.api.block;

import com.hrznstudio.galacticraft.api.configurable.SideOption;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.state.StateFactory;


public abstract class ConfigurableElectricMachineBlock extends BlockWithEntity implements MachineBlock {

    public ConfigurableElectricMachineBlock(Settings block$Settings_1) {
        super(block$Settings_1);
    }

    public BlockState blankConfig(BlockState state) {
        return state.with(SideOption.FRONT_SIDE_OPTION, SideOption.BLANK).with(SideOption.BACK_SIDE_OPTION, SideOption.BLANK)
                .with(SideOption.RIGHT_SIDE_OPTION, SideOption.BLANK).with(SideOption.LEFT_SIDE_OPTION, SideOption.BLANK)
                .with(SideOption.TOP_SIDE_OPTION, SideOption.BLANK).with(SideOption.BOTTOM_SIDE_OPTION, SideOption.BLANK);
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> stateFactory$Builder_1) {
        super.appendProperties(stateFactory$Builder_1);
        stateFactory$Builder_1.add(SideOption.FRONT_SIDE_OPTION);
        stateFactory$Builder_1.add(SideOption.BACK_SIDE_OPTION);
        stateFactory$Builder_1.add(SideOption.RIGHT_SIDE_OPTION);
        stateFactory$Builder_1.add(SideOption.LEFT_SIDE_OPTION);
        stateFactory$Builder_1.add(SideOption.TOP_SIDE_OPTION);
        stateFactory$Builder_1.add(SideOption.BOTTOM_SIDE_OPTION);
    }

    public static SideOption[] optionsToArray(BlockState state) {
        return new SideOption[]{state.get(SideOption.FRONT_SIDE_OPTION), state.get(SideOption.BACK_SIDE_OPTION), state.get(SideOption.RIGHT_SIDE_OPTION), state.get(SideOption.LEFT_SIDE_OPTION), state.get(SideOption.TOP_SIDE_OPTION), state.get(SideOption.BOTTOM_SIDE_OPTION)};
    }

    abstract public boolean consumesOxygen();
    abstract public boolean generatesOxygen();

    abstract public boolean consumesPower();
    abstract public boolean generatesPower();
}
