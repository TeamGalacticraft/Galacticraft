package dev.galacticraft.impl.internal.mixin;

import dev.galacticraft.api.accessor.LevelBodyAccessor;
import dev.galacticraft.api.registry.AddonRegistries;
import dev.galacticraft.api.universe.celestialbody.CelestialBody;
import dev.galacticraft.api.universe.celestialbody.landable.Landable;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(value = Level.class, priority = 100) // apply before oxygen level mixin
public class LevelCelestialBodyMixin implements LevelBodyAccessor {
    @Unique
    private Holder<CelestialBody<? ,?>> celestialBody = null;

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
}
