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

import com.mojang.serialization.Lifecycle;
import dev.galacticraft.api.universe.celestialbody.CelestialBodyType;
import dev.galacticraft.api.universe.celestialbody.CelestialHandler;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.type.CelestialTeleporterType;
import dev.galacticraft.api.universe.display.CelestialDisplayType;
import dev.galacticraft.api.universe.display.ring.CelestialRingDisplayType;
import dev.galacticraft.api.universe.position.CelestialPositionType;
import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.WritableRegistry;

public final class BuiltInAddonRegistries {
    private BuiltInAddonRegistries() {}

    public static final WritableRegistry<CelestialPositionType<?>> CELESTIAL_POSITION_TYPE = FabricRegistryBuilder.from(
            new DefaultedMappedRegistry<>(Constant.id("static").toString(),
                    AddonRegistries.CELESTIAL_POSITION_TYPE, Lifecycle.experimental(), false)).buildAndRegister();

    public static final WritableRegistry<CelestialDisplayType<?>> CELESTIAL_DISPLAY_TYPE = FabricRegistryBuilder.from(
            new DefaultedMappedRegistry<>(Constant.id("empty").toString(),
                    AddonRegistries.CELESTIAL_DISPLAY_TYPE, Lifecycle.experimental(), false)).buildAndRegister();

    public static final WritableRegistry<CelestialRingDisplayType<?>> CELESTIAL_RING_DISPLAY_TYPE = FabricRegistryBuilder.from(
            new DefaultedMappedRegistry<>(Constant.id("empty").toString(),
                    AddonRegistries.CELESTIAL_RING_DISPLAY_TYPE, Lifecycle.experimental(), false)).buildAndRegister();

    public static final WritableRegistry<CelestialBodyType<?>> CELESTIAL_BODY_TYPE = FabricRegistryBuilder.from(
            new DefaultedMappedRegistry<>(Constant.id("star").toString(),
                    AddonRegistries.CELESTIAL_BODY_TYPE, Lifecycle.experimental(), false)).buildAndRegister();

    public static final WritableRegistry<CelestialTeleporterType<?>> CELESTIAL_TELEPORTER_TYPE = FabricRegistryBuilder.createDefaulted(
            AddonRegistries.CELESTIAL_TELEPORTER_TYPE, Constant.id("direct")).buildAndRegister();

    public static final WritableRegistry<CelestialHandler> CELESTIAL_HANDLER = FabricRegistryBuilder.createSimple(AddonRegistries.CELESTIAL_HANDLER)
            .buildAndRegister();
}
