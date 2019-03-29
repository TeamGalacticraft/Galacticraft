package io.github.teamgalacticraft.galacticraft.api.blocks;

import io.github.teamgalacticraft.galacticraft.util.Rotatable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class AbstractMachineBlockAbstract extends AbstractHorizontalDirectionalBlock implements Rotatable {

    /**
     * Set {@link MachineBlockStatus} as a block property.
     */
    public final EnumProperty<MachineBlockStatus> MACHINE_STATUS = EnumProperty.create("status", MachineBlockStatus.class);

    public AbstractMachineBlockAbstract(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> blockStateBuilder) {
        super.appendProperties(blockStateBuilder);
        blockStateBuilder.with(MACHINE_STATUS);
    }
}
