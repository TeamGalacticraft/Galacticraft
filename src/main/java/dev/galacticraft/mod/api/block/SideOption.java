/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.api.block;

import dev.galacticraft.mod.Constants;
import dev.galacticraft.mod.api.block.entity.ConfigurableMachineBlockEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public enum SideOption implements Comparable<SideOption> {
    DEFAULT(new TranslatableText("ui.galacticraft.side_option.default").setStyle(Constants.Styles.TOOLTIP_STYLE), false, false, false, false, false),
    POWER_INPUT(new TranslatableText("ui.galacticraft.side_option.energy").setStyle(Constants.Styles.LIGHT_PURPLE_STYLE).append(new TranslatableText("ui.galacticraft.side_option.in").setStyle(Constants.Styles.GREEN_STYLE)), true, false, false, true, false),
    POWER_OUTPUT(new TranslatableText("ui.galacticraft.side_option.energy").setStyle(Constants.Styles.LIGHT_PURPLE_STYLE).append(new TranslatableText("ui.galacticraft.side_option.out").setStyle(Constants.Styles.DARK_RED_STYLE)), true, false, false, false, true),
    FLUID_INPUT(new TranslatableText("ui.galacticraft.side_option.fluids").setStyle(Constants.Styles.GREEN_STYLE).append(new TranslatableText("ui.galacticraft.side_option.in").setStyle(Constants.Styles.GREEN_STYLE)), false, true, false, true, false),
    FLUID_OUTPUT(new TranslatableText("ui.galacticraft.side_option.fluids").setStyle(Constants.Styles.GREEN_STYLE).append(new TranslatableText("ui.galacticraft.side_option.out").setStyle(Constants.Styles.DARK_RED_STYLE)), false, true, false, false, true),
    ITEM_INPUT(new TranslatableText("ui.galacticraft.side_option.items").setStyle(Constants.Styles.GOLD_STYLE).append(new TranslatableText("ui.galacticraft.side_option.in").setStyle(Constants.Styles.GREEN_STYLE)), false, false, true, true, false),
    ITEM_OUTPUT(new TranslatableText("ui.galacticraft.side_option.items").setStyle(Constants.Styles.GOLD_STYLE).append(new TranslatableText("ui.galacticraft.side_option.out").setStyle(Constants.Styles.DARK_RED_STYLE)), false, false, true, false, true);

    private final MutableText name;
    private final boolean energy;
    private final boolean fluid;
    private final boolean item;
    private final boolean input;
    private final boolean output;

    SideOption(MutableText name, boolean energy, boolean fluid, boolean item, boolean input, boolean output) {
        this.name = name;
        this.energy = energy;
        this.fluid = fluid;
        this.item = item;
        this.input = input;
        this.output = output;
    }

    public boolean isEnergy() {
        return energy;
    }

    public boolean isItem() {
        return item;
    }

    public boolean isFluid() {
        return fluid;
    }

    public boolean isInput() {
        return input;
    }

    public boolean isOutput() {
        return output;
    }

    public SideOption nextValidOption(ConfigurableMachineBlockEntity machine) {
        List<SideOption> values = machine.validSideOptions();
        int i = values.indexOf(this);
        if (++i == values.size()) {
            return values.get(0);
        } else {
            return values.get(i);
        }
    }

    public SideOption prevValidOption(ConfigurableMachineBlockEntity machine) {
        List<SideOption> values = machine.validSideOptions();
        int i = values.indexOf(this);
        if (i-- == 0) {
            return values.get(values.size() - 1);
        } else {
            return values.get(i);
        }
    }

    public Text getFormattedName() {
        return this.name;
    }
}
