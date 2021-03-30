/*
 * Copyright (c) 2019-2021 HRZN LTD
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.hrznstudio.galacticraft.api.machine;

import com.hrznstudio.galacticraft.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
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

    public Text getName() {
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
