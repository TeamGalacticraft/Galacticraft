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

package com.hrznstudio.galacticraft.blocks.machines.circuitfabricator;

import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public enum CircuitFabricatorStatus {
    /**
     * Fabricator is active and is processing.
     */
    PROCESSING(new TranslatableText("ui.galacticraft-rewoven.machinestatus.processing").setStyle(new Style().setColor(Formatting.GREEN)).asFormattedString()),
    /**
     * Fabricator is not processing.
     */
    IDLE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.idle").setStyle(new Style().setColor(Formatting.GOLD)).asFormattedString()),
    /**
     * The fabricator has no energy.
     */
    NOT_ENOUGH_POWER(new TranslatableText("ui.galacticraft-rewoven.machinestatus.not_enough_power").setStyle(new Style().setColor(Formatting.GRAY)).asFormattedString()),
    /**
     * The fabricator has been switched off.
     */
    OFF(new TranslatableText("ui.galacticraft-rewoven.machinestatus.off").setStyle(new Style().setColor(Formatting.GRAY)).asFormattedString());


    private String name;

    CircuitFabricatorStatus(String name) {
        this.name = name;
    }

    public static CircuitFabricatorStatus get(int index) {
        switch (index) {
            case 0:
                return PROCESSING;
            case 1:
                return IDLE;
            case 3:
                return OFF;
            default:
                return NOT_ENOUGH_POWER;
        }
    }

    @Override
    public String toString() {
        return name;
    }
}