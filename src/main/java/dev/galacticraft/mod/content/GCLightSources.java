/*
 * Copyright (c) 2019-2025 Team Galacticraft
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
import dev.galacticraft.mod.api.block.entity.SolarPanel.SolarPanelSource;
import dev.galacticraft.mod.api.solarpanel.LightSource;
import dev.galacticraft.mod.api.solarpanel.SolarPanelRegistry;
import dev.galacticraft.mod.util.Translations;
import dev.galacticraft.mod.world.dimension.GCDimensions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class GCLightSources {
    public static final Map<SolarPanelSource, LightSource> DEFAULT_LIGHT_SOURCES = new HashMap<SolarPanelSource, LightSource>();
    public static final Map<SolarPanelSource, LightSource> MOON_LIGHT_SOURCES = new HashMap<SolarPanelSource, LightSource>();

    public static void register() {
        DEFAULT_LIGHT_SOURCES.put(SolarPanelSource.DAY,
                new LightSource(Constant.CelestialBody.SOL, Component.translatable(Translations.SolarPanel.LIGHT_SOURCE_SUN).setStyle(Constant.Text.YELLOW_STYLE), 1.0, 1.0));
        DEFAULT_LIGHT_SOURCES.put(SolarPanelSource.NIGHT,
                new LightSource(Constant.CelestialBody.MOON, Component.translatable(Translations.SolarPanel.LIGHT_SOURCE_MOON).setStyle(Constant.Text.GRAY_STYLE), 0.07, 1.0));
        DEFAULT_LIGHT_SOURCES.put(SolarPanelSource.OVERCAST,
                new LightSource(Constant.CelestialBody.SOL_OVERCAST, Component.translatable(Translations.SolarPanel.LIGHT_SOURCE_RAIN).setStyle(Constant.Text.BLUE_STYLE), 1.0, 2.0));
        DEFAULT_LIGHT_SOURCES.put(SolarPanelSource.STORMY,
                new LightSource(Constant.CelestialBody.SOL_OVERCAST, Component.translatable(Translations.SolarPanel.LIGHT_SOURCE_THUNDER).setStyle(Constant.Text.DARK_GRAY_STYLE), 1.0, 10.0));
        DEFAULT_LIGHT_SOURCES.put(SolarPanelSource.NO_LIGHT_SOURCE, 
                new LightSource(null, Component.translatable(Translations.SolarPanel.LIGHT_SOURCE_NONE).setStyle(Constant.Text.WHITE_STYLE), 0.0, 1.0));

        MOON_LIGHT_SOURCES.put(SolarPanelSource.DAY,
                new LightSource(Constant.CelestialBody.SOL_FROM_MOON, Component.translatable(Translations.SolarPanel.LIGHT_SOURCE_SUN).setStyle(Constant.Text.YELLOW_STYLE), 1.0, 1.0));
        MOON_LIGHT_SOURCES.put(SolarPanelSource.NIGHT,
                new LightSource(Constant.CelestialBody.EARTH, Component.translatable(Translations.SolarPanel.LIGHT_SOURCE_EARTH).setStyle(Constant.Text.GREEN_STYLE), 0.07, 1.0));
        MOON_LIGHT_SOURCES.put(SolarPanelSource.OVERCAST,
                new LightSource(null, Component.translatable(Translations.SolarPanel.LIGHT_SOURCE_NONE).setStyle(Constant.Text.BLUE_STYLE), 0.0, 1.0));
        MOON_LIGHT_SOURCES.put(SolarPanelSource.STORMY,
                new LightSource(null, Component.translatable(Translations.SolarPanel.LIGHT_SOURCE_NONE).setStyle(Constant.Text.DARK_GRAY_STYLE), 0.0, 1.0));
        MOON_LIGHT_SOURCES.put(SolarPanelSource.NO_LIGHT_SOURCE, 
                new LightSource(null, Component.translatable(Translations.SolarPanel.LIGHT_SOURCE_NONE).setStyle(Constant.Text.WHITE_STYLE), 0.0, 1.0));

        SolarPanelRegistry.registerLightSources(GCDimensions.MOON, MOON_LIGHT_SOURCES);
    }
}
