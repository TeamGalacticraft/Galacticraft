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

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.registry.RocketRegistries;
import dev.galacticraft.api.rocket.part.*;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.landable.teleporter.CelestialTeleporter;
import dev.galacticraft.api.universe.galaxy.Galaxy;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(RegistrySynchronization.class)
public abstract class RegistrySynchronizationMixin {
    @Shadow
    private static <E> void put(ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> builder, ResourceKey<? extends Registry<E>> resourceKey, Codec<E> codec) {
    }

    @Dynamic("1.19.4 synthetic")
    @Inject(method = "method_45958", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;", remap = false), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void addRegistries(CallbackInfoReturnable<ImmutableMap<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>>> cir, ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> builder) {
        put(builder, AddonRegistries.CELESTIAL_BODY, CelestialBody.DIRECT_CODEC);
        put(builder, AddonRegistries.GALAXY, Galaxy.DIRECT_CODEC);
        
        put(builder, RocketRegistries.ROCKET_CONE, RocketCone.DIRECT_CODEC);
        put(builder, RocketRegistries.ROCKET_BODY, RocketBody.DIRECT_CODEC);
        put(builder, RocketRegistries.ROCKET_FIN, RocketFin.DIRECT_CODEC);
        put(builder, RocketRegistries.ROCKET_BOOSTER, RocketBooster.DIRECT_CODEC);
        put(builder, RocketRegistries.ROCKET_ENGINE, RocketEngine.DIRECT_CODEC);
        put(builder, RocketRegistries.ROCKET_UPGRADE, RocketUpgrade.DIRECT_CODEC);

        put(builder, AddonRegistries.CELESTIAL_TELEPORTER, CelestialTeleporter.DIRECT_CODEC);
    }
}
