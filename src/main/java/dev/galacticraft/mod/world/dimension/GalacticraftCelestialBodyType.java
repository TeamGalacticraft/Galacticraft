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

import dev.galacticraft.api.celestialbody.CelestialBodyDisplayInfo;
import dev.galacticraft.api.celestialbody.CelestialBodyType;
import dev.galacticraft.api.celestialbody.CelestialObjectType;
import dev.galacticraft.api.celestialbody.satellite.SatelliteRecipe;
import dev.galacticraft.api.registry.AddonRegistry;
import dev.galacticraft.mod.item.GalacticraftItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class GalacticraftCelestialBodyType {
    public static final CelestialBodyType THE_MOON = new CelestialBodyType.Builder(new Identifier("galacticraft", "the_moon"))
            .translationKey("ui.galacticraft.bodies.the_moon")
            .world(GalacticraftDimension.MOON)
            .type(CelestialObjectType.MOON)
            .parent(CelestialBodyType.EARTH)
            .weight(1)
            .gravity(0.16f)
            .display(
                    new CelestialBodyDisplayInfo.Builder()
                            .texture(new Identifier("galacticraft", "gui/celestialbodies/moon"))
                            .w(8).h(8)
                            .distance(5f)
                            .scale(0.5F)
                            .time(648000) // 27 mc days in ticks
                            .build()
            )
            .recipe(new SatelliteRecipe(new ItemStack(GalacticraftItem.BASIC_WAFER, 100)))
            .build();

    public static void register() {
        Registry.register(AddonRegistry.CELESTIAL_BODY_TYPE, GalacticraftCelestialBodyType.THE_MOON.getId(), GalacticraftCelestialBodyType.THE_MOON);
    }
}
