/*
 * Copyright (c) 2019 HRZN LTD
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

package com.hrznstudio.galacticraft.api.configurable;

import com.hrznstudio.galacticraft.api.block.ConfigurableElectricMachineBlock;
import net.minecraft.block.Block;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public enum SideOption implements StringIdentifiable {

    DEFAULT("d"),
    POWER_INPUT("pi"),
    POWER_OUTPUT("po"),
    OXYGEN_INPUT("oi"),
    OXYGEN_OUTPUT("oo"),
    FLUID_INPUT("fi"),
    FLUID_OUTPUT("fo");

    private final String name;

    SideOption(String name) {
        this.name = name;
    }

    public static List<SideOption> getApplicableValuesForMachine(Block block) {
        if (block instanceof ConfigurableElectricMachineBlock) {
            List<SideOption> options = new ArrayList<>();
            options.add(DEFAULT);
            if (((ConfigurableElectricMachineBlock) block).consumesOxygen()) {
                options.add(OXYGEN_INPUT);
            }
            if (((ConfigurableElectricMachineBlock) block).generatesOxygen()) {
                options.add(OXYGEN_OUTPUT);
            }
            if (((ConfigurableElectricMachineBlock) block).consumesPower()) {
                options.add(POWER_INPUT);
            }
            if (((ConfigurableElectricMachineBlock) block).generatesPower()) {
                options.add(POWER_OUTPUT);
            }
            if (((ConfigurableElectricMachineBlock) block).consumesFluids()) {
                options.add(FLUID_INPUT);
            }
            if (((ConfigurableElectricMachineBlock) block).generatesFluids()) {
                options.add(FLUID_OUTPUT);
            }
            return options;
        }
        return new ArrayList<>();
    }

    @Override
    public String asString() {
        return this.name;
    }

    public SideOption nextValidOption(Block block) {
        List<SideOption> values = new ArrayList<>(getApplicableValuesForMachine(block));
        int i = values.indexOf(this);
        if (i + 1 >= values.size()) {
            return values.get(0);
        } else {
            return values.get(i + 1);
        }
    }

    public Text getFormattedName() {
        if (this == SideOption.DEFAULT) {
            return new LiteralText("Blank").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
        } else if (this == SideOption.OXYGEN_INPUT) {
            return new LiteralText("Oxygen").setStyle(Style.EMPTY.withColor(Formatting.AQUA)).append(new LiteralText(" in").setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
        } else if (this == SideOption.OXYGEN_OUTPUT) {
            return new LiteralText("Oxygen").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)).append(new LiteralText(" out").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
        } else if (this == SideOption.POWER_INPUT) {
            return new LiteralText("Power").setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)).append(new LiteralText(" in").setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
        } else if (this == SideOption.POWER_OUTPUT) {
            return new LiteralText("Power").setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)).append(new LiteralText(" out").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
        } else if (this == FLUID_INPUT) {
            return new LiteralText("Fluid").setStyle(Style.EMPTY.withColor(Formatting.AQUA)).append(new LiteralText(" in").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
        } else if (this == FLUID_OUTPUT) {
            return new LiteralText("Fluid").setStyle(Style.EMPTY.withColor(Formatting.GREEN)).append(new LiteralText(" out").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
        }
        return new LiteralText("");
    }
}
