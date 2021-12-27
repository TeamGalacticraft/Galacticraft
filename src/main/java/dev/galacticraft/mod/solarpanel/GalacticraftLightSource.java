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

package dev.galacticraft.mod.solarpanel;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.solarpanel.LightSource;
import dev.galacticraft.mod.api.solarpanel.SolarPanelRegistry;
import dev.galacticraft.mod.api.solarpanel.WorldLightSources;
import dev.galacticraft.mod.world.dimension.GalacticraftDimensionType;
import net.minecraft.text.TranslatableText;

public class GalacticraftLightSource {
    private static final WorldLightSources MOON = new WorldLightSources(
            Constant.ScreenTexture.MOON_LIGHT_SOURCES,
            new LightSource(new TranslatableText("ui.galacticraft.machine.solar_panel.source.sun").setStyle(Constant.Text.YELLOW_STYLE), 1.0, 1.0),
            new LightSource(new TranslatableText("ui.galacticraft.machine.solar_panel.source.earth").setStyle(Constant.Text.GREEN_STYLE), 0.07, 1.0),
            new LightSource(new TranslatableText("ui.galacticraft.machine.solar_panel.source.invalid").setStyle(Constant.Text.BLUE_STYLE), 0.0, 1.0),
            new LightSource(new TranslatableText("ui.galacticraft.machine.solar_panel.source.unknown").setStyle(Constant.Text.WHITE_STYLE), 0.0, 1.0));

    public static void register() {
        SolarPanelRegistry.registerLightSources(GalacticraftDimensionType.MOON_KEY, MOON);
    }
}
