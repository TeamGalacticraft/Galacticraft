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

package com.hrznstudio.galacticraft.hooks;

import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.addon.AddonInitializer;
import com.hrznstudio.galacticraft.world.dimension.GalacticraftCelestialBodyTypes;
import com.hrznstudio.galacticraft.world.dimension.GalacticraftGases;

public class GalacticraftAddon implements AddonInitializer {

    @Override
    public void onAddonInitialize() {
        long startAddonInit = System.currentTimeMillis();
        Galacticraft.logger.info("[Galacticraft] Started loading addon core.");

        GalacticraftCelestialBodyTypes.init();

        Galacticraft.logger.info("[Galacticraft] Finished loading addon core. (Took {}ms)", System.currentTimeMillis() - startAddonInit);
    }

    @Override
    public void onCompatInitialize() {
        long startAddonInit = System.currentTimeMillis();
        Galacticraft.logger.info("[Galacticraft] Started loading addon compat.");

        GalacticraftGases.init();

        Galacticraft.logger.info("[Galacticraft] Finished loading addon compat. (Took {}ms)", System.currentTimeMillis() - startAddonInit);
    }

    @Override
    public String getModId() {
        return Constants.MOD_ID;
    }
}
