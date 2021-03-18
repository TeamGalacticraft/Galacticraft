package com.hrznstudio.galacticraft.api.machine;

import com.hrznstudio.galacticraft.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.StringIdentifiable;

import java.util.Locale;

public enum RedstoneState implements StringIdentifiable {
    /**
     * Ignores redstone entirely.
     */
    IGNORE(new TranslatableText("ui.galacticraft-rewoven.redstone.ignore"), Constants.Text.GRAY_STYLE),

    /**
     * When powered with redstone, the machine turns off.
     */
    LOW(new TranslatableText("ui.galacticraft-rewoven.redstone.low"), Constants.Text.DARK_RED_STYLE),

    /**
     * When powered with redstone, the machine turns on.
     */
    HIGH(new TranslatableText("ui.galacticraft-rewoven.redstone.high"), Constants.Text.RED_STYLE);

    private final MutableText name;

    RedstoneState(TranslatableText name, Style style) {
        this.name = name.setStyle(style);
    }

    public static RedstoneState fromString(String string) {
        return RedstoneState.valueOf(string.toUpperCase(Locale.ROOT));
    }

    public MutableText getName() {
        return name;
    }

    @Override
    public String asString() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public void toTag(CompoundTag tag) {
        tag.putString("Redstone", this.asString());
    }

    public static RedstoneState fromTag(CompoundTag tag) {
        return fromString(tag.getString("Redstone"));
    }
}
