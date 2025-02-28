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

package dev.galacticraft.impl.internal.mixin;

import dev.galacticraft.api.accessor.LevelBodyAccessor;
import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.landable.Landable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(value = Level.class, priority = 100) // apply before oxygen level mixin
public abstract class LevelCelestialBodyMixin implements LevelBodyAccessor {
    @Unique
    private Holder<CelestialBody<? ,?>> celestialBody = null;

    @Shadow public abstract RegistryAccess registryAccess();

    @Shadow public abstract DimensionType dimensionType();

    @Inject(method = "<init>(Lnet/minecraft/world/level/storage/WritableLevelData;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/core/Holder;Ljava/util/function/Supplier;ZZJI)V", at = @At("RETURN"))
    private void init(WritableLevelData writableLevelData,
                      ResourceKey<Level> levelKey,
                      RegistryAccess registryAccess,
                      Holder<DimensionType> holder,
                      Supplier<ProfilerFiller> supplier,
                      boolean bl,
                      boolean bl2,
                      long l,
                      int i, CallbackInfo ci) {
        this.celestialBody = registryAccess.registryOrThrow(AddonRegistries.CELESTIAL_BODY).holders().filter(
                b -> b.value().type() instanceof Landable landable && landable.world(b.value().config()).equals(levelKey)
        ).findFirst().orElse(null);
    }

    @Override
    public @Nullable Holder<CelestialBody<?, ?>> galacticraft$getCelestialBody() {
        return this.celestialBody;
    }

    @Override
    public boolean galacticraft$hasDimensionTypeTag(TagKey<DimensionType> tag) {
        Registry<DimensionType> dimensionTypeRegistry = this.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE);
        return dimensionTypeRegistry.getHolder(dimensionTypeRegistry.getId(this.dimensionType())).map(reference -> reference.is(tag)).orElse(false);
    }
}
