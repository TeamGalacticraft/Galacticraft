package io.github.teamgalacticraft.galacticraft.api.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class AbstractHorizontalDirectionalBlock extends Block {
    public static DirectionProperty FACING = Properties.FACING_HORIZONTAL;

    public AbstractHorizontalDirectionalBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> blockStateBuilder) {
        super.appendProperties(blockStateBuilder);
        blockStateBuilder.with(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getPlayerHorizontalFacing().getOpposite());
    }
}
