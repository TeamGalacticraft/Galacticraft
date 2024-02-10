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

package dev.galacticraft.impl.universe;

import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.registry.BuiltInAddonRegistries;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.CelestialTeleporter;
import dev.galacticraft.api.universe.galaxy.Galaxy;
import dev.galacticraft.impl.universe.celestialbody.landable.teleporter.type.DirectCelestialTeleporterType;
import dev.galacticraft.impl.universe.celestialbody.landable.teleporter.type.FixedCelestialTeleporterType;
import dev.galacticraft.impl.universe.celestialbody.type.DecorativePlanet;
import dev.galacticraft.impl.universe.celestialbody.type.PlanetType;
import dev.galacticraft.impl.universe.celestialbody.type.StarType;
import dev.galacticraft.impl.universe.display.type.EmptyCelestialDisplayType;
import dev.galacticraft.impl.universe.display.type.IconCelestialDisplayType;
import dev.galacticraft.impl.universe.display.type.SpinningIconCelestialDisplayType;
import dev.galacticraft.impl.universe.display.type.ring.AsteroidCelestialRingDisplayType;
import dev.galacticraft.impl.universe.display.type.ring.DefaultCelestialRingDisplayType;
import dev.galacticraft.impl.universe.display.type.ring.EmptyCelestialRingDisplayType;
import dev.galacticraft.impl.universe.position.type.OrbitalCelestialPositionType;
import dev.galacticraft.impl.universe.position.type.StaticCelestialPositionType;
import dev.galacticraft.mod.Constant;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class BuiltinObjects {
    public static final ResourceKey<Galaxy> MILKY_WAY_KEY = ResourceKey.create(AddonRegistries.GALAXY, Constant.id("milky_way"));
    public static final ResourceKey<CelestialBody<?, ?>> SOL_KEY = ResourceKey.create(AddonRegistries.CELESTIAL_BODY, Constant.id("sol"));
    public static final ResourceKey<CelestialBody<?, ?>> EARTH_KEY = ResourceKey.create(AddonRegistries.CELESTIAL_BODY, Constant.id("earth"));

    public static final ResourceKey<CelestialTeleporter<?, ?>> DIRECT_CELESTIAL_TELEPORTER = ResourceKey.create(AddonRegistries.CELESTIAL_TELEPORTER, Constant.id("direct"));

    public static void register() {
        Registry.register(BuiltInAddonRegistries.CELESTIAL_POSITION_TYPE, Constant.id("static"), StaticCelestialPositionType.INSTANCE);
        Registry.register(BuiltInAddonRegistries.CELESTIAL_POSITION_TYPE, Constant.id("orbital"), OrbitalCelestialPositionType.INSTANCE);

        Registry.register(BuiltInAddonRegistries.CELESTIAL_DISPLAY_TYPE, Constant.id("empty"), EmptyCelestialDisplayType.INSTANCE);
        Registry.register(BuiltInAddonRegistries.CELESTIAL_DISPLAY_TYPE, Constant.id("icon"), IconCelestialDisplayType.INSTANCE);
        Registry.register(BuiltInAddonRegistries.CELESTIAL_DISPLAY_TYPE, Constant.id("spinning_icon"), SpinningIconCelestialDisplayType.INSTANCE);

        Registry.register(BuiltInAddonRegistries.CELESTIAL_RING_DISPLAY_TYPE, Constant.id("empty"), EmptyCelestialRingDisplayType.INSTANCE);
        Registry.register(BuiltInAddonRegistries.CELESTIAL_RING_DISPLAY_TYPE, Constant.id("default"), DefaultCelestialRingDisplayType.INSTANCE);
        Registry.register(BuiltInAddonRegistries.CELESTIAL_RING_DISPLAY_TYPE, Constant.id("asteroid"), AsteroidCelestialRingDisplayType.INSTANCE);

        Registry.register(BuiltInAddonRegistries.CELESTIAL_BODY_TYPE, Constant.id("star"), StarType.INSTANCE);
        Registry.register(BuiltInAddonRegistries.CELESTIAL_BODY_TYPE, Constant.id("planet"), PlanetType.INSTANCE);
        Registry.register(BuiltInAddonRegistries.CELESTIAL_BODY_TYPE, Constant.id("decorative_planet"), DecorativePlanet.INSTANCE);

        Registry.register(BuiltInAddonRegistries.CELESTIAL_TELEPORTER_TYPE, Constant.id("direct"), DirectCelestialTeleporterType.INSTANCE);
        Registry.register(BuiltInAddonRegistries.CELESTIAL_TELEPORTER_TYPE, Constant.id("fixed"), FixedCelestialTeleporterType.INSTANCE);
    }
}
