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

package dev.galacticraft.api.client.registry;

import dev.galacticraft.api.APIConstants;
import dev.galacticraft.api.client.universe.display.CelestialDisplayType;
import dev.galacticraft.api.client.universe.display.ring.CelestialRingDisplayType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public final class ClientAddonRegistries {
    private ClientAddonRegistries() {
    }

    public static final ResourceKey<Registry<CelestialDisplayType<?>>> CELESTIAL_DISPLAY_TYPE = ResourceKey.createRegistryKey(APIConstants.id("celestial_display_type"));
    public static final ResourceKey<Registry<CelestialRingDisplayType<?>>> CELESTIAL_RING_DISPLAY_TYPE = ResourceKey.createRegistryKey(APIConstants.id("celestial_ring_display_type"));
}
