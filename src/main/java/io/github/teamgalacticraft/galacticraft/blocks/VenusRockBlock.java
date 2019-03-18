package io.github.teamgalacticraft.galacticraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringRepresentable;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class VenusRockBlock extends Block {

    public static EnumProperty<VenusRockBlockVariant> VARIANT = EnumProperty.create("variant", VenusRockBlockVariant.class);

    public VenusRockBlock(Settings block$Settings_1) {
        super(block$Settings_1);
    }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> stateFactory$Builder_1) {
        super.appendProperties(stateFactory$Builder_1);
        stateFactory$Builder_1.with(VARIANT);
    }

    public enum VenusRockBlockVariant implements StringRepresentable {
        V_0("0"),
        V_1("1"),
        V_2("2"),
        V_3("3"),
        V_SCORCHED("scorched");

        String name;
        VenusRockBlockVariant(String name) {
            this.name = name;
        }

        @Override
        public java.lang.String asString() {
            return name;
        }
    }
}
