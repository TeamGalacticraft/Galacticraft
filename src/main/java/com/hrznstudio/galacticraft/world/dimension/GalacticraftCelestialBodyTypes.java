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

package com.hrznstudio.galacticraft.world.dimension;

import com.hrznstudio.galacticraft.api.addon.AddonRegistry;
import com.hrznstudio.galacticraft.api.celestialbodies.CelestialBodyDisplayInfo;
import com.hrznstudio.galacticraft.api.celestialbodies.CelestialBodyType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class GalacticraftCelestialBodyTypes {
    //    public static final CelestialBodyType MARS = register(
//            new CelestialBodyType.Builder(new Identifier("galacticraft-rewoven", "mars"))
//                    .translationKey("ui.galacticraft-rewoven.bodies.mars")
//                    .dimension(GalacticraftDimensions.MARS)
//                    .parent(CelestialBodyType.THE_SUN)
//                    .weight(2)
//                    .gravity(0.37f)
//                    .display(
//                            new CelestialBodyDisplayInfo.Builder()
//                                    .texture(new Identifier("galacticraft-rewoven", "planet_icons"))
//                                    .y(16)
//                                    .distance(15d)
//                                    .time(1187136000d) // 687 days in ticks
//                                    .build()
//                    )
//                    .atmosphere(
//                            new AtmosphericInfo.Builder()
//                                    .temperature(-63f)
//                                    .pressure(0.62f)
//                                    .gas(AtmosphericGas.CARBON_DIOXIDE, 953500d)
//                                    .gas(AtmosphericGas.NITROGEN, 27000d)
//                                    .gas(AtmosphericGas.ARGON, 16000d)
//                                    .gas(AtmosphericGas.OXYGEN, 1300d)
//                                    .gas(AtmosphericGas.CARBON_MONOXIDE, 800d)
//                                    .gas(AtmosphericGas.WATER_VAPOR, 210d)
//                                    .gas(GalacticraftGases.NITROGEN_OXIDE, 100d)
//                                    .gas(AtmosphericGas.NEON, 2.5d)
//                                    .gas(GalacticraftGases.HYDROGEN_DEUTERIUM_OXYGEN, 0.85d)
//                                    .gas(AtmosphericGas.KRYPTON, 0.3d)
//                                    .gas(AtmosphericGas.XENON, 0.08d)
//                                    .build()
//                    )
//                    .build()
//    );
    public static final CelestialBodyType THE_MOON = register(
            new CelestialBodyType.Builder(new Identifier("galacticraft-rewoven", "the_moon"))
                    .translationKey("ui.galacticraft-rewoven.bodies.the_moon")
                    .dimension(GalacticraftDimensions.MOON)
                    .parent(CelestialBodyType.EARTH)
                    .weight(1)
                    .gravity(0.16f)
                    .display(
                            new CelestialBodyDisplayInfo.Builder()
                                    .texture(new Identifier("galacticraft-rewoven", "planet_icons"))
                                    .distance(5d)
                                    .time(46656000d) // 27 mc days in ticks
                                    .build()
                    )
                    .build()
    );

    private static CelestialBodyType register(CelestialBodyType type) {
        return Registry.register(AddonRegistry.CELESTIAL_BODIES, type.getId(), type);
    }

    public static void init() {
    }
}
