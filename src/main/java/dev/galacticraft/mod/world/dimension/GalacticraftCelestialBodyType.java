/*
 * Copyright (c) 2019-2021 Team Galacticraft
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

package dev.galacticraft.mod.world.dimension;

import com.hrznstudio.galacticraft.api.atmosphere.AtmosphericInfo;
import com.hrznstudio.galacticraft.api.celestialbodies.CelestialBodyDisplayInfo;
import com.hrznstudio.galacticraft.api.celestialbodies.CelestialBodyType;
import com.hrznstudio.galacticraft.api.regisry.AddonRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftCelestialBodyType {
    public static final CelestialBodyType THE_MOON = new CelestialBodyType.Builder(new Identifier("galacticraft", "the_moon"))
            .translationKey("ui.galacticraft.bodies.the_moon")
            .world(GalacticraftDimension.MOON)
            .parent(CelestialBodyType.EARTH)
            .weight(1)
            .gravity(0.165f)
            .display(
                    new CelestialBodyDisplayInfo.Builder()
                            .texture(new Identifier("galacticraft", "planet_icons"))
                            .distance(5)
                            .time(46656000d) // 27 mc days in ticks
                            .build()
            ).atmosphere(
                    new AtmosphericInfo.Builder()
                            .pressure(3.0E-15f)
                            .temperature(25.0f).build()
            )
            .build();

    private static CelestialBodyType register(CelestialBodyType type) {
        return Registry.register(AddonRegistry.CELESTIAL_BODIES, type.getId(), type);
    }

    public static void init() {
    }
}
