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
public enum SideOption implements Comparable<SideOption> {
    DEFAULT(false, false, false, false),
    POWER_INPUT(true, false, false, false),
    POWER_OUTPUT(true, false, false, false),
//    OXYGEN_INPUT(false, false, false, true),
//    OXYGEN_OUTPUT(false, false, false, true),
    FLUID_INPUT(false, true, false, false),
    FLUID_OUTPUT(false, true, false, false),
    ITEM_INPUT(false, false, true, false),
    ITEM_OUTPUT(false, false, true, false);

    private final boolean energy;
    private final boolean fluid;
    private final boolean item;
    private final boolean oxygen;

    SideOption(boolean energy, boolean fluid, boolean item, boolean oxygen) {
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
        return fluid; //todo gas api maybe?
    }

    public boolean isFluid() {
        return fluid;
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
//            case OXYGEN_INPUT:
//                return new LiteralText("Oxygen").setStyle(Style.EMPTY.withColor(Formatting.AQUA)).append(new LiteralText(" in").setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
//            case OXYGEN_OUTPUT:
//                return new LiteralText("Oxygen").setStyle(Style.EMPTY.withColor(Formatting.AQUA)).append(new LiteralText(" out").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
            case POWER_INPUT:
                return new LiteralText("Energy").setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)).append(new LiteralText(" in").setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
            case POWER_OUTPUT:
                return new LiteralText("Energy").setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)).append(new LiteralText(" out").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
            case FLUID_INPUT:
                return new LiteralText("Fluids").setStyle(Style.EMPTY.withColor(Formatting.GREEN)).append(new LiteralText(" in").setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
            case FLUID_OUTPUT:
                return new LiteralText("Fluids").setStyle(Style.EMPTY.withColor(Formatting.GREEN)).append(new LiteralText(" out").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
            case ITEM_INPUT:
                return new LiteralText("Items").setStyle(Style.EMPTY.withColor(Formatting.GOLD)).append(new LiteralText(" in").setStyle(Style.EMPTY.withColor(Formatting.GREEN)));
            case ITEM_OUTPUT:
                return new LiteralText("Items").setStyle(Style.EMPTY.withColor(Formatting.GOLD)).append(new LiteralText(" out").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
        }
        return new LiteralText("");
    }
}
