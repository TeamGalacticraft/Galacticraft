package com.hrznstudio.galacticraft.api.configurable;

import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public enum SideOption implements StringIdentifiable {

    BLANK,
    POWER_INPUT,
    POWER_OUTPUT,
    OXYGEN_INPUT,
    OXYGEN_OUTPUT;

    public static final EnumProperty<SideOption> FRONT_SIDE_OPTION = EnumProperty.of("front_config", SideOption.class, SideOption.values());
    public static final EnumProperty<SideOption> BACK_SIDE_OPTION = EnumProperty.of("back_config", SideOption.class, SideOption.values());
    public static final EnumProperty<SideOption> RIGHT_SIDE_OPTION = EnumProperty.of("right_config", SideOption.class, SideOption.values());
    public static final EnumProperty<SideOption> LEFT_SIDE_OPTION = EnumProperty.of("left_config", SideOption.class, SideOption.values());
    public static final EnumProperty<SideOption> TOP_SIDE_OPTION = EnumProperty.of("top_config", SideOption.class, SideOption.values());
    public static final EnumProperty<SideOption> BOTTOM_SIDE_OPTION = EnumProperty.of("bottom_config", SideOption.class, SideOption.values());

    public static EnumProperty<SideOption> getOptionForDirection(Direction direction) {
        switch (direction) {
            case NORTH:
                return SideOption.FRONT_SIDE_OPTION;
            case SOUTH:
                return SideOption.BACK_SIDE_OPTION;
            case EAST:
                return SideOption.RIGHT_SIDE_OPTION;
            case WEST:
                return SideOption.LEFT_SIDE_OPTION;
            case UP:
                return SideOption.TOP_SIDE_OPTION;
            case DOWN:
                return SideOption.BOTTOM_SIDE_OPTION;
        }
        return null;
    }

    public static SideOption[] fromTag(String s) {
        String[] options = s.split(",");
        SideOption[] sideOptions = new SideOption[6];
        sideOptions[0] = SideOption.valueOf(options[0]);
        sideOptions[1] = SideOption.valueOf(options[1]);
        sideOptions[2] = SideOption.valueOf(options[2]);
        sideOptions[3] = SideOption.valueOf(options[3]);
        sideOptions[4] = SideOption.valueOf(options[4]);
        sideOptions[5] = SideOption.valueOf(options[5]);
        return sideOptions;
    }

    public static String toTag(SideOption[] sideOptions) {
        return sideOptions[0].name() + "," + sideOptions[1].name() + "," + sideOptions[2].name() + "," + sideOptions[3].name() + "," + sideOptions[4].name() + "," + sideOptions[5].name();
    }

    @Override
    public String asString() {
        return this.name().toLowerCase();
    }
}
