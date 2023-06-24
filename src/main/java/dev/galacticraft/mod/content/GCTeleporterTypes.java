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

package dev.galacticraft.mod.content;

import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.registry.BuiltInAddonRegistries;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.CelestialTeleporter;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.type.CelestialTeleporterType;
import dev.galacticraft.impl.universe.celestialbody.landable.teleporter.config.DefaultCelestialTeleporterConfig;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.teleporters.LanderCelestialTeleporterType;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

public class GCTeleporterTypes {
    public static final ResourceKey<CelestialTeleporter<?, ?>> LANDER_CELESTIAL_TELEPORTER = ResourceKey.create(AddonRegistries.CELESTIAL_TELEPORTER, Constant.id("lander"));
    public static final CelestialTeleporterType<DefaultCelestialTeleporterConfig> LANDER = Registry.register(BuiltInAddonRegistries.CELESTIAL_TELEPORTER_TYPE, Constant.id("lander"), LanderCelestialTeleporterType.INSTANCE);

    public static void register() {

    }

    public static void bootstrapRegistries(BootstapContext<CelestialTeleporter<?, ?>> context) {
        context.register(LANDER_CELESTIAL_TELEPORTER, new CelestialTeleporter<>(LANDER, DefaultCelestialTeleporterConfig.INSTANCE));
    }
}
