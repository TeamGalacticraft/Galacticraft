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

package dev.galacticraft.impl.internal.mixin.registry;

import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.api.rocket.part.*;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.CelestialTeleporter;
import dev.galacticraft.api.universe.galaxy.Galaxy;
import net.minecraft.resources.RegistryDataLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(RegistryDataLoader.class)
public abstract class RegistryDataLoaderMixin {
    @Mutable
    @Shadow @Final public static List<RegistryDataLoader.RegistryData<?>> WORLDGEN_REGISTRIES;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void addGalacticraftAPIDynamicRegistries(CallbackInfo ci) {
        WORLDGEN_REGISTRIES = new ArrayList<>(WORLDGEN_REGISTRIES);
        WORLDGEN_REGISTRIES.add(new RegistryDataLoader.RegistryData<>(AddonRegistries.GALAXY, Galaxy.DIRECT_CODEC));
        WORLDGEN_REGISTRIES.add(new RegistryDataLoader.RegistryData<>(AddonRegistries.CELESTIAL_BODY, CelestialBody.DIRECT_CODEC));

        WORLDGEN_REGISTRIES.add(new RegistryDataLoader.RegistryData<>(RocketRegistries.ROCKET_CONE, RocketCone.DIRECT_CODEC));
        WORLDGEN_REGISTRIES.add(new RegistryDataLoader.RegistryData<>(RocketRegistries.ROCKET_BODY, RocketBody.DIRECT_CODEC));
        WORLDGEN_REGISTRIES.add(new RegistryDataLoader.RegistryData<>(RocketRegistries.ROCKET_FIN, RocketFin.DIRECT_CODEC));
        WORLDGEN_REGISTRIES.add(new RegistryDataLoader.RegistryData<>(RocketRegistries.ROCKET_BOOSTER, RocketBooster.DIRECT_CODEC));
        WORLDGEN_REGISTRIES.add(new RegistryDataLoader.RegistryData<>(RocketRegistries.ROCKET_BOTTOM, RocketBottom.DIRECT_CODEC));
        WORLDGEN_REGISTRIES.add(new RegistryDataLoader.RegistryData<>(RocketRegistries.ROCKET_UPGRADE, RocketUpgrade.DIRECT_CODEC));

        WORLDGEN_REGISTRIES.add(new RegistryDataLoader.RegistryData<>(AddonRegistries.CELESTIAL_TELEPORTER, CelestialTeleporter.DIRECT_CODEC));
    }
}
