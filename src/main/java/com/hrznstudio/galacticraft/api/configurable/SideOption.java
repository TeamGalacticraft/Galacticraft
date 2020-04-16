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
import net.minecraft.util.StringIdentifiable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public enum SideOption implements StringIdentifiable {

    BLANK("default"),
    POWER_INPUT("powerin"),
    POWER_OUTPUT("powerout"),
    OXYGEN_INPUT("oxygenin"),
    OXYGEN_OUTPUT("oxygenout"),
    FLUID_INPUT("fluidin"),
    FLUID_OUTPUT("fluidout");

    private String name;

    SideOption(String name) {
        this.name = name;
    }

    public static List<SideOption> getApplicableValuesForMachine(Block block) {
        if (block instanceof ConfigurableElectricMachineBlock) {
            List<SideOption> options = new ArrayList<>();
            options.add(BLANK);
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

    public String getFormattedName() {
        if (this == SideOption.BLANK) {
            return "\u00a78Blank";
        } else if (this == SideOption.OXYGEN_INPUT) {
            return "\u00a7bOxygen \u00a7ain";
        } else if (this == SideOption.OXYGEN_OUTPUT) {
            return "\u00a78Oxygen \u00a74out";
        } else if (this == SideOption.POWER_INPUT) {
            return "\u00a7dPower \u00a7ain";
        } else if (this == SideOption.POWER_OUTPUT) {
            return "\u00a7dPower \u00a74out";
        } else if (this == FLUID_INPUT) {
            return "\u00a7aFluid \u00a74in";
        } else if (this == FLUID_OUTPUT) {
            return "\u00a7aFluid \u00a74out";
        }
        return "";
    }
}
