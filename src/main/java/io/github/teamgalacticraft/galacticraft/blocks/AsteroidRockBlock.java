package io.github.teamgalacticraft.galacticraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringRepresentable;

/**
 * @author <a href="https://github.com/teamgalacticraft">TeamGalacticraft</a>
 */
public class AsteroidRockBlock extends Block {

    public static EnumProperty<AsteroidRockBlockVariant> VARIANT = EnumProperty.create("variant", AsteroidRockBlockVariant.class);

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> stateFactory$Builder_1) {
        super.appendProperties(stateFactory$Builder_1);
        stateFactory$Builder_1.with(VARIANT);
    }

    public AsteroidRockBlock(Settings settings) {
        super(settings);
    }

    public enum AsteroidRockBlockVariant implements StringRepresentable {
        V_0("0"),
        V_1("1"),
        V_2("2");

        String name;
        AsteroidRockBlockVariant(String name) {
            this.name = name;
        }
        @Override
        public String asString() {
            return name;
        }
    }
}
