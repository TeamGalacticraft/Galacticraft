/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

package dev.galacticraft.mod.content;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.solarpanel.LightSource;
import dev.galacticraft.mod.api.solarpanel.SolarPanelRegistry;
import dev.galacticraft.mod.api.solarpanel.WorldLightSources;
import dev.galacticraft.mod.util.Translations;
import dev.galacticraft.mod.world.dimension.GCDimensions;
import net.minecraft.network.chat.Component;

public class GCLightSources {
    private static final WorldLightSources MOON = new WorldLightSources(
            Constant.ScreenTexture.MOON_LIGHT_SOURCES,
            new LightSource(Component.translatable(Translations.SolarPanel.LIGHT_SOURCE_SUN).setStyle(Constant.Text.Color.YELLOW_STYLE), 1.0, 1.0),
            new LightSource(Component.translatable(Translations.SolarPanel.LIGHT_SOURCE_EARTH).setStyle(Constant.Text.Color.GREEN_STYLE), 0.07, 1.0),
            new LightSource(Component.translatable(Translations.SolarPanel.LIGHT_SOURCE_NONE).setStyle(Constant.Text.Color.BLUE_STYLE), 0.0, 1.0),
            new LightSource(Component.translatable(Translations.SolarPanel.LIGHT_SOURCE_NONE).setStyle(Constant.Text.Color.WHITE_STYLE), 0.0, 1.0));

    public static void register() {
        SolarPanelRegistry.registerLightSources(GCDimensions.MOON, MOON);
    }
}
