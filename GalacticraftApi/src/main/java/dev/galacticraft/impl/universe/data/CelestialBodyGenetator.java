/*
 * Copyright (c) 2019-2023 Team Galacticraft
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

package dev.galacticraft.impl.universe.data;

import dev.galacticraft.api.data.CelestialBodyDataProvider;
import dev.galacticraft.api.gas.GasComposition;
import dev.galacticraft.impl.Constant;
import dev.galacticraft.impl.universe.BuiltinObjects;
import dev.galacticraft.impl.universe.display.config.IconCelestialDisplayConfig;
import dev.galacticraft.impl.universe.display.type.IconCelestialDisplayType;
import dev.galacticraft.impl.universe.position.config.StaticCelestialPositionConfig;
import dev.galacticraft.impl.universe.position.type.StaticCelestialPositionType;
import dev.galacticraft.machinelib.api.gas.Gases;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CelestialBodyGenetator extends CelestialBodyDataProvider {
    public CelestialBodyGenetator(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    public void generateCelestialBodies() {
        star(BuiltinObjects.SOL_KEY.location())
                .name(Component.translatable("star.galacticraft-api.sol.name"))
                .description(Component.translatable("star.galacticraft-api.sol.description"))
                .galaxy(BuiltinObjects.MILKY_WAY_KEY)
                .position(StaticCelestialPositionType.INSTANCE.configure(new StaticCelestialPositionConfig(0, 0)))
                .display(IconCelestialDisplayType.INSTANCE.configure(new IconCelestialDisplayConfig(new ResourceLocation(Constant.MOD_ID, "textures/body_icons.png"), 0, 0, 16, 16, 1.5f)))
                .photosphericComposition(new GasComposition.Builder()
                    .pressure(28)
                    .gas(Gases.HYDROGEN_ID, 734600.000)
                    .gas(Gases.HELIUM_ID, 248500.000)
                    .gas(Gases.OXYGEN_ID, 7700.000)
                    .gas(Gases.NEON_ID, 1200.000)
                    .gas(Gases.NITROGEN_ID, 900.000)
                    .build())
                .gravity(28.0f)
                .luminance(1)
                .surfaceTemperature(5772);

//        celestialBody(BuiltinObjects.SOL_KEY, BuiltinObjects.SOL);
    }

    @Override
    public String getName() {
        return "Galacticraft API Celestial Bodies";
    }
}
