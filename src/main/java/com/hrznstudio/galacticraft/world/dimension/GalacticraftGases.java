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

package com.hrznstudio.galacticraft.world.dimension;

import com.hrznstudio.galacticraft.api.atmosphere.AtmosphericGas;
import net.minecraft.resources.ResourceLocation;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftGases {

    public static final AtmosphericGas NITROGEN_OXIDE =
            new AtmosphericGas(
                    new ResourceLocation("galacticraft-rewoven", "nitrogen_oxide"),
                    "ui.galacticraft-rewoven.nitrogen_oxide",
                    "NO"
            );

    public static final AtmosphericGas HYDROGEN_DEUTERIUM_OXYGEN =
            new AtmosphericGas(
                    new ResourceLocation("galacticraft-rewoven", "hydrogen_deuterium_oxygen"),
                    "ui.galacticraft-rewoven.hydrogen_deuterium_oxygen",
                    "HDO"

            );

    public static void init() {
    }
}
