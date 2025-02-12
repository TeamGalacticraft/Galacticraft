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

import dev.galacticraft.api.registry.BuiltInAddonRegistries;
import dev.galacticraft.api.universe.celestialbody.CelestialHandler;
import dev.galacticraft.api.universe.celestialbody.DefaultCelestialHandler;
import dev.galacticraft.api.universe.celestialbody.OverworldHandler;

public class GCCelestialHandlers {
    public static final GCRegistry<CelestialHandler> CELESTIAL_HANDLERS = new GCRegistry<>(BuiltInAddonRegistries.CELESTIAL_HANDLER);
    public static final CelestialHandler DEFAULT = CELESTIAL_HANDLERS.register("default", new DefaultCelestialHandler());
    public static final CelestialHandler OVERWORLD = CELESTIAL_HANDLERS.register("overworld", new OverworldHandler());

    public static void register() {}
}
