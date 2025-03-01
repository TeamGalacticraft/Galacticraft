package dev.galacticraft.mod.api.block.entity;

import net.minecraft.world.item.DyeColor;

public enum PipeColor {
    WHITE("white"),
    ORANGE("orange"),
    MAGENTA("magenta"),
    LIGHT_BLUE("light_blue"),
    YELLOW("yellow"),
    LIME("lime"),
    PINK("pink"),
    GRAY("gray"),
    LIGHT_GRAY("light_gray"),
    CYAN("cyan"),
    PURPLE("purple"),
    BLUE("blue"),
    BROWN("brown"),
    GREEN("green"),
    RED("red"),
    BLACK("black"),
    CLEAR("clear");

    private final String name;

    PipeColor(final String name) {
        this.name = name;
    }

    public static PipeColor fromDye(DyeColor dye) {
        return PipeColor.values()[dye.ordinal()];
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
