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
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class GCSolarPanelStates {
    public static final Map<SolarPanelSource, ResourceLocation> DEFAULT_SOLAR_PANELS = new HashMap<SolarPanelSource, ResourceLocation>();

    public static void register() {
        DEFAULT_SOLAR_PANELS.put(SolarPanelSource.DAY, Constant.ScreenTexture.SOLAR_PANEL_DAY);
        DEFAULT_SOLAR_PANELS.put(SolarPanelSource.NIGHT, Constant.ScreenTexture.SOLAR_PANEL_NIGHT);
        DEFAULT_SOLAR_PANELS.put(SolarPanelSource.OVERCAST, Constant.ScreenTexture.SOLAR_PANEL_DAY);
        DEFAULT_SOLAR_PANELS.put(SolarPanelSource.STORMY, Constant.ScreenTexture.SOLAR_PANEL_NIGHT);
        DEFAULT_SOLAR_PANELS.put(SolarPanelSource.NO_LIGHT_SOURCE, Constant.ScreenTexture.SOLAR_PANEL_BLOCKED);
    }
}