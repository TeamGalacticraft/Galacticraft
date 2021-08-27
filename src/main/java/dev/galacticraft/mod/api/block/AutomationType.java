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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.block.entity.MachineBlockEntity;
import dev.galacticraft.mod.attribute.Automatable;
import dev.galacticraft.mod.attribute.NullAutomatable;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public enum AutomationType implements Comparable<AutomationType> {
    NONE(new TranslatableText("ui.galacticraft.side_option.none").setStyle(Constant.Text.DARK_GRAY_STYLE), false, false, false, false, false),
    POWER_INPUT(new TranslatableText("ui.galacticraft.side_option.energy").setStyle(Constant.Text.LIGHT_PURPLE_STYLE).append(new TranslatableText("ui.galacticraft.side_option.in").setStyle(Constant.Text.GREEN_STYLE)), true, false, false, true, false),
    POWER_OUTPUT(new TranslatableText("ui.galacticraft.side_option.energy").setStyle(Constant.Text.LIGHT_PURPLE_STYLE).append(new TranslatableText("ui.galacticraft.side_option.out").setStyle(Constant.Text.DARK_RED_STYLE)), true, false, false, false, true),
    POWER_IO(new TranslatableText("ui.galacticraft.side_option.energy").setStyle(Constant.Text.LIGHT_PURPLE_STYLE).append(new TranslatableText("ui.galacticraft.side_option.io").setStyle(Constant.Text.BLUE_STYLE)), true, false, false, true, true),
    FLUID_INPUT(new TranslatableText("ui.galacticraft.side_option.fluids").setStyle(Constant.Text.GREEN_STYLE).append(new TranslatableText("ui.galacticraft.side_option.in").setStyle(Constant.Text.GREEN_STYLE)), false, true, false, true, false),
    FLUID_OUTPUT(new TranslatableText("ui.galacticraft.side_option.fluids").setStyle(Constant.Text.GREEN_STYLE).append(new TranslatableText("ui.galacticraft.side_option.out").setStyle(Constant.Text.DARK_RED_STYLE)), false, true, false, false, true),
    FLUID_IO(new TranslatableText("ui.galacticraft.side_option.fluids").setStyle(Constant.Text.GREEN_STYLE).append(new TranslatableText("ui.galacticraft.side_option.io").setStyle(Constant.Text.BLUE_STYLE)), false, true, false, true, true),
    ITEM_INPUT(new TranslatableText("ui.galacticraft.side_option.items").setStyle(Constant.Text.GOLD_STYLE).append(new TranslatableText("ui.galacticraft.side_option.in").setStyle(Constant.Text.GREEN_STYLE)), false, false, true, true, false),
    ITEM_OUTPUT(new TranslatableText("ui.galacticraft.side_option.items").setStyle(Constant.Text.GOLD_STYLE).append(new TranslatableText("ui.galacticraft.side_option.out").setStyle(Constant.Text.DARK_RED_STYLE)), false, false, true, false, true),
    ITEM_IO(new TranslatableText("ui.galacticraft.side_option.items").setStyle(Constant.Text.GOLD_STYLE).append(new TranslatableText("ui.galacticraft.side_option.io").setStyle(Constant.Text.BLUE_STYLE)), false, false, true, true, true);
//    ANY(new TranslatableText("ui.galacticraft.side_option.any").setStyle(Constants.Text.RED_STYLE).append(new TranslatableText("ui.galacticraft.side_option.io").setStyle(Constants.Text.BLUE_STYLE)), true, true, true, true, true);

    private final MutableText name;
    private final boolean energy;
    private final boolean fluid;
    private final boolean item;
    private final boolean input;
    private final boolean output;

    AutomationType(MutableText name, boolean energy, boolean fluid, boolean item, boolean input, boolean output) {
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

    public boolean isBidirectional() {
        return this.isInput() && this.isOutput();
    }

    public boolean canPassAs(AutomationType other) {
        if (other == this) return true;
        if (other.isEnergy() != this.isEnergy()) return false;
        if (other.isFluid() != this.isFluid()) return false;
        if (other.isItem() != this.isItem()) return false;
        if (this.isBidirectional()) return true;
        if (other.isInput() != this.isInput()) return false;
        if (other.isOutput() != this.isOutput()) return false;
        return true;
    }

    public boolean canPassAsIgnoreFlow(AutomationType other) {
        if (other == this) return true;
        if (other.isEnergy() != this.isEnergy()) return false;
        if (other.isFluid() != this.isFluid()) return false;
        if (other.isItem() != this.isItem()) return false;
        return true;
    }

    public Automatable getAutomatable(MachineBlockEntity machine) {
        if (this.isItem()) return machine.itemInv();
        if (this.isFluid()) return machine.fluidInv();
        if (this.isEnergy()) return NullAutomatable.INSTANCE;
        return NullAutomatable.INSTANCE;
    }

    public Text getFormattedName() {
        return this.name;
    }
}
