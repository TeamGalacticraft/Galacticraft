package dev.galacticraft.mod.data.content;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

public record GeneratingBootstrapContext<T>(HolderLookup.Provider registries, FabricDynamicRegistryProvider.Entries entries) implements BootstapContext<T> {
    @Override
    public Holder.Reference<T> register(ResourceKey<T> resourceKey, T object, Lifecycle lifecycle) {
        return (Holder.Reference<T>) this.entries.add(resourceKey, object);
    }

    @Override
    public <S> @NotNull HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> resourceKey) {
        return this.registries.lookupOrThrow(resourceKey);
    }
}
