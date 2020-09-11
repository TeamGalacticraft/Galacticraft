/*
 * Copyright (c) 2020 HRZN LTD
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
 *
 */

package com.hrznstudio.galacticraft.api.block;

import com.hrznstudio.galacticraft.api.block.entity.ConfigurableMachineBlockEntity;
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
public enum SideOption implements StringIdentifiable, Comparable<SideOption> {

    DEFAULT("d", false, false, false, false),
    POWER_INPUT("pi", true, false, false, false),
    POWER_OUTPUT("po", true, false, false, false),
    OXYGEN_INPUT("oi", false, false, false, true),
    OXYGEN_OUTPUT("oo", false, false, false, true),
    FLUID_INPUT("fi", false, true, false, false),
    FLUID_OUTPUT("fo", false, true, false, false),
    ITEM_INPUT("ii", false, false, true, false),
    ITEM_OUTPUT("io", false, false, true, false);

    private final String name;
    private final boolean energy;
    private final boolean fluid;
    private final boolean item;
    private final boolean oxygen;

    SideOption(String name, boolean energy, boolean fluid, boolean item, boolean oxygen) {
        this.name = name;

        this.energy = energy;
        this.fluid = fluid;
        this.item = item;
        this.oxygen = oxygen;
    }

    public boolean isEnergy() {
        return energy;
    }

    public boolean isItem() {
        return item;
    }

    public boolean isOxygen() {
        return oxygen;
    }

    public boolean isFluid() {
        return fluid;
    }

    public static List<SideOption> getApplicableValuesForMachine(Block block) {
        if (block instanceof ConfigurableMachineBlock) {
            List<SideOption> options = new ArrayList<>();
            options.add(DEFAULT);
            if (((ConfigurableMachineBlock) block).consumesOxygen()) {
                options.add(OXYGEN_INPUT);
            }
            if (((ConfigurableMachineBlock) block).generatesOxygen()) {
                options.add(OXYGEN_OUTPUT);
            }
            if (((ConfigurableMachineBlock) block).consumesPower()) {
                options.add(POWER_INPUT);
            }
            if (((ConfigurableMachineBlock) block).generatesPower()) {
                options.add(POWER_OUTPUT);
            }
            if (((ConfigurableMachineBlock) block).consumesFluids()) {
                options.add(FLUID_INPUT);
            }
            if (((ConfigurableMachineBlock) block).generatesFluids()) {
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

    public SideOption nextValidOption(ConfigurableMachineBlockEntity blockEntity) {
        List<SideOption> values = new ArrayList<>(blockEntity.validSideOptions());
        int i = values.indexOf(this);
        if (++i == values.size()) {
            return values.get(0);
        } else {
            return values.get(i);
        }
    }

    public SideOption prevValidOption(ConfigurableMachineBlockEntity blockEntity) {
        List<SideOption> values = new ArrayList<>(blockEntity.validSideOptions());
        int i = values.indexOf(this);
        if (i-- == 0) {
            return values.get(values.size() - 1);
        } else {
            return values.get(i);
        }
    }

    public Text getFormattedName() {
        switch (this) {
            case DEFAULT:
                return new LiteralText("Blank").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY));
            case OXYGEN_INPUT:
                return new LiteralText("Oxygen").setStyle(Style.EMPTY.withColor(Formatting.AQUA)).append(new LiteralText(" in").setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
            case OXYGEN_OUTPUT:
                return new LiteralText("Oxygen").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)).append(new LiteralText(" out").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
            case POWER_INPUT:
                return new LiteralText("Power").setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)).append(new LiteralText(" in").setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
            case POWER_OUTPUT:
                return new LiteralText("Power").setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)).append(new LiteralText(" out").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
            case FLUID_INPUT:
                return new LiteralText("Fluid").setStyle(Style.EMPTY.withColor(Formatting.AQUA)).append(new LiteralText(" in").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
            case FLUID_OUTPUT:
                return new LiteralText("Fluid").setStyle(Style.EMPTY.withColor(Formatting.GREEN)).append(new LiteralText(" out").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
            case ITEM_OUTPUT:
            case ITEM_INPUT:
        }
        return new LiteralText("");
    }
}
