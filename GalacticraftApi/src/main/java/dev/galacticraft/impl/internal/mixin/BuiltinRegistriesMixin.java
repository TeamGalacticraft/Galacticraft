/*
 * Copyright (c) 2019-2022 Team Galacticraft
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

package dev.galacticraft.impl.internal.mixin;

import com.mojang.serialization.Lifecycle;
import dev.galacticraft.api.registry.AddonRegistry;
import dev.galacticraft.impl.Constant;
import dev.galacticraft.impl.universe.BuiltinObjects;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltinRegistries.class)
public abstract class BuiltinRegistriesMixin {
    @Shadow
    private static <T, R extends WritableRegistry<T>> R internalRegister(ResourceKey<? extends Registry<T>> registryRef, R registry, BuiltinRegistries.RegistryBootstrap<T> initializer, Lifecycle lifecycle) {
        throw new UnsupportedOperationException("Untransformed mixin");
    }

    @Inject(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/data/BuiltinRegistries;registerSimple(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/data/BuiltinRegistries$RegistryBootstrap;)Lnet/minecraft/core/Registry;", ordinal = 0))
    private static void galacticraft_addDynamicRegistries(CallbackInfo ci) {
        BuiltinObjects.register();
        internalRegister(AddonRegistry.GALAXY_KEY, AddonRegistry.GALAXY, (registry) -> AddonRegistry.GALAXY.getHolderOrThrow(BuiltinObjects.MILKY_WAY_KEY), Lifecycle.experimental());
        internalRegister(AddonRegistry.CELESTIAL_BODY_KEY, AddonRegistry.CELESTIAL_BODY, (registry) -> AddonRegistry.CELESTIAL_BODY.getHolderOrThrow(BuiltinObjects.SOL_KEY), Lifecycle.experimental());
        internalRegister(AddonRegistry.ROCKET_PART_KEY, AddonRegistry.ROCKET_PART, (registry) -> AddonRegistry.ROCKET_PART.getHolderOrThrow(ResourceKey.create(AddonRegistry.ROCKET_PART_KEY, new ResourceLocation(Constant.MOD_ID, "invalid"))), Lifecycle.experimental());
    }
}