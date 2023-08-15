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

package dev.galacticraft.api.registry;

import com.mojang.serialization.Lifecycle;
import dev.galacticraft.api.universe.celestialbody.CelestialBodyType;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.type.CelestialTeleporterType;
import dev.galacticraft.api.universe.display.CelestialDisplayType;
import dev.galacticraft.api.universe.position.CelestialPositionType;
import dev.galacticraft.mod.Constant;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceLocation;

public final class BuiltInAddonRegistries {
    private BuiltInAddonRegistries() {}

    public static final WritableRegistry<CelestialPositionType<?>> CELESTIAL_POSITION_TYPE = FabricRegistryBuilder.from(
            new DefaultedMappedRegistry<>(new ResourceLocation(Constant.MOD_ID, "static").toString(),
                    AddonRegistries.CELESTIAL_POSITION_TYPE, Lifecycle.experimental(), false)).buildAndRegister();

    public static final WritableRegistry<CelestialDisplayType<?>> CELESTIAL_DISPLAY_TYPE = FabricRegistryBuilder.from(
            new DefaultedMappedRegistry<>(new ResourceLocation(Constant.MOD_ID, "empty").toString(),
                    AddonRegistries.CELESTIAL_DISPLAY_TYPE, Lifecycle.experimental(), false)).buildAndRegister();

    public static final WritableRegistry<CelestialBodyType<?>> CELESTIAL_BODY_TYPE = FabricRegistryBuilder.from(
            new DefaultedMappedRegistry<>(new ResourceLocation(Constant.MOD_ID, "star").toString(),
                    AddonRegistries.CELESTIAL_BODY_TYPE, Lifecycle.experimental(), false)).buildAndRegister();

    public static final WritableRegistry<CelestialTeleporterType<?>> CELESTIAL_TELEPORTER_TYPE = FabricRegistryBuilder.createDefaulted(
            AddonRegistries.CELESTIAL_TELEPORTER_TYPE, Constant.id("direct")).buildAndRegister();
}
