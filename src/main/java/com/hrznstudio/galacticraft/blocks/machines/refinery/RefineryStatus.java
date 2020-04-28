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

package com.hrznstudio.galacticraft.blocks.machines.refinery;

import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public enum RefineryStatus {

    /**
     * Refinery is active and is converting oil into fuel.
     */
    ACTIVE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.refining").setStyle(Style.EMPTY.withColor(Formatting.GREEN)).getString()),
    /**
     * Refinery has oil but the fuel tank is full.
     */
    FULL(new TranslatableText("ui.galacticraft-rewoven.machinestatus.idle").setStyle(Style.EMPTY.withColor(Formatting.GOLD)).getString()),
    /**
     * The refinery has no oil.
     */
    IDLE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.inactive").setStyle(Style.EMPTY.withColor(Formatting.BLACK)).getString()),
    /**
     * The refinery has no energy.
     */
    INACTIVE(new TranslatableText("ui.galacticraft-rewoven.machinestatus.inactive").setStyle(Style.EMPTY.withColor(Formatting.GRAY)).getString());

    private String name;

    RefineryStatus(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

