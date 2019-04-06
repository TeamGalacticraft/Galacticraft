package io.github.teamgalacticraft.galacticraft.api.blocks;

import io.github.teamgalacticraft.galacticraft.util.Rotatable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class AbstractDirectionalBlock extends Block {

    public static DirectionProperty FACING = Properties.FACING;

    public AbstractDirectionalBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> stateBuilder) {
        super.appendProperties(stateBuilder);
    }
}
