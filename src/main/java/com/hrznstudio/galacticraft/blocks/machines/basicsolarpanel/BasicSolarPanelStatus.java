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

package com.hrznstudio.galacticraft.blocks.machines.basicsolarpanel;

import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public enum BasicSolarPanelStatus {
    /**
     * Solar panel is active and is generating energy.
     */
    COLLECTING(new TranslatableText("ui.galacticraft-rewoven.machinestatus.collecting").setStyle(Style.field_24360.setColor(Formatting.GREEN)).getString()),
    /**
     * Solar Panel is generating energy, but the buffer is full.
     */
    FULL(new TranslatableText("ui.galacticraft-rewoven.machinestatus.full").setStyle(Style.field_24360.setColor(Formatting.GOLD)).getString()),
    /**
     * Solar Panel is generating energy, but less efficiently.
     */
    RAINING(new TranslatableText("ui.galacticraft-rewoven.machinestatus.raining").setStyle(Style.field_24360.setColor(Formatting.DARK_AQUA)).getString()),
    /**
     * Solar Panel is not generating energy.
     */
    NIGHT(new TranslatableText("ui.galacticraft-rewoven.machinestatus.night").setStyle(Style.field_24360.setColor(Formatting.BLUE)).getString()),
    /**
     * The sun is not visible.
     */
    BLOCKED(new TranslatableText("ui.galacticraft-rewoven.machinestatus.blocked").setStyle(Style.field_24360.setColor(Formatting.DARK_GRAY)).getString());
    private String name;

    BasicSolarPanelStatus(String name) {
        this.name = name;
    }

    public static BasicSolarPanelStatus get(int index) {
        if (index < 0) index = 0;
        return values()[index % values().length];
    }

    @Override
    public String toString() {
        return name;
    }
}
