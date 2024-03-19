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

package dev.galacticraft.api.registry;

import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.CelestialBodyType;
import dev.galacticraft.api.universe.celestialbody.CelestialHandler;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.CelestialTeleporter;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.type.CelestialTeleporterType;
import dev.galacticraft.api.universe.display.CelestialDisplayType;
import dev.galacticraft.api.universe.display.ring.CelestialRingDisplayType;
import dev.galacticraft.api.universe.galaxy.Galaxy;
import dev.galacticraft.api.universe.position.CelestialPositionType;
import dev.galacticraft.mod.Constant;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class AddonRegistries {
    private AddonRegistries() {}

    public static final ResourceKey<Registry<CelestialPositionType<?>>> CELESTIAL_POSITION_TYPE = ResourceKey.createRegistryKey(Constant.id("celestial_position_type"));
    public static final ResourceKey<Registry<CelestialDisplayType<?>>> CELESTIAL_DISPLAY_TYPE = ResourceKey.createRegistryKey(Constant.id("celestial_display_type"));
    public static final ResourceKey<Registry<CelestialRingDisplayType<?>>> CELESTIAL_RING_DISPLAY_TYPE = ResourceKey.createRegistryKey(Constant.id("celestial_ring_display_type"));
    public static final ResourceKey<Registry<CelestialBodyType<?>>> CELESTIAL_BODY_TYPE = ResourceKey.createRegistryKey(Constant.id("celestial_body_type"));
    public static final ResourceKey<Registry<CelestialTeleporterType<?>>> CELESTIAL_TELEPORTER_TYPE = ResourceKey.createRegistryKey(Constant.id("celestial_teleporter_type"));

    public static final ResourceKey<Registry<Galaxy>> GALAXY = ResourceKey.createRegistryKey(Constant.id("galaxy"));
    public static final ResourceKey<Registry<CelestialBody<?, ?>>> CELESTIAL_BODY = ResourceKey.createRegistryKey(Constant.id("celestial_body"));
    public static final ResourceKey<Registry<CelestialTeleporter<?, ?>>> CELESTIAL_TELEPORTER = ResourceKey.createRegistryKey(Constant.id("celestial_teleporter"));
    public static final ResourceKey<Registry<CelestialHandler>> CELESTIAL_HANDLER = ResourceKey.createRegistryKey(Constant.id("celestial_handler"));
}
