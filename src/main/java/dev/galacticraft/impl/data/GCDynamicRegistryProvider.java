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

package dev.galacticraft.impl.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.impl.registry.sync.DynamicRegistriesImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * Modified version of {@link net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider} to support {@link RegistryDataLoader#DIMENSION_REGISTRIES}
 * A provider to help with data-generation of dynamic registry objects,
 * such as biomes, features, or message types.
 */
public abstract class GCDynamicRegistryProvider implements DataProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(GCDynamicRegistryProvider.class);

    private final FabricDataOutput output;
    private final CompletableFuture<HolderLookup.Provider> registriesFuture;

    public GCDynamicRegistryProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        this.output = output;
        this.registriesFuture = registriesFuture;
    }

    protected abstract void configure(HolderLookup.Provider registries, Entries entries);

    public static final class Entries {
        private final HolderLookup.Provider registries;
        // Registry ID -> Entries for that registry
        private final Map<ResourceLocation, RegistryEntries<?>> queuedEntries;
        private final String modId;

        @ApiStatus.Internal
        Entries(HolderLookup.Provider registries, String modId) {
            this.registries = registries;
            List<RegistryDataLoader.RegistryData<?>> allDynReg = new ArrayList<>(DynamicRegistries.getDynamicRegistries());
            allDynReg.addAll(RegistryDataLoader.DIMENSION_REGISTRIES);
            this.queuedEntries = allDynReg.stream()
                    // Some modded dynamic registries might not be in the wrapper lookup, filter them out
                    .filter(e -> registries.lookup(e.key()).isPresent())
                    .collect(Collectors.toMap(
                            e -> e.key().location(),
                            e -> RegistryEntries.create(registries, e)
                    ));
            this.modId = modId;
        }

        /**
         * Gets access to all registry lookups.
         */
        public HolderLookup.Provider getLookups() {
            return registries;
        }

        /**
         * Gets a lookup for entries from the given registry.
         */
        public <T> HolderGetter<T> getLookup(ResourceKey<? extends Registry<T>> registryKey) {
            return registries.lookupOrThrow(registryKey);
        }

        /**
         * Returns a lookup for placed features. Useful when creating biomes.
         */
        public HolderGetter<PlacedFeature> placedFeatures() {
            return getLookup(Registries.PLACED_FEATURE);
        }

        /**
         * Returns a lookup for configured carvers. Useful when creating biomes.
         */
        public HolderGetter<ConfiguredWorldCarver<?>> configuredCarvers() {
            return getLookup(Registries.CONFIGURED_CARVER);
        }

        /**
         * Gets a reference to a registry entry for use in other registrations.
         */
        public <T> Holder<T> ref(ResourceKey<T> key) {
            RegistryEntries<T> entries = getQueuedEntries(key);
            return Holder.Reference.createStandAlone(entries.lookup, key);
        }

        /**
         * Adds a new object to be data generated.
         *
         * @return a reference to it for use in other objects.
         */
        public <T> Holder<T> add(ResourceKey<T> registry, T object) {
            return getQueuedEntries(registry).add(registry.location(), object);
        }

        /**
         * Adds a new {@link ResourceKey} from a given {@link HolderLookup.RegistryLookup} to be data generated.
         *
         * @return a reference to it for use in other objects.
         */
        public <T> Holder<T> add(HolderLookup.RegistryLookup<T> registry, ResourceKey<T> valueKey) {
            return add(valueKey, registry.getOrThrow(valueKey).value());
        }

        /**
         * All the registry entries whose namespace matches the current effective mod ID will be data generated.
         */
        public <T> List<Holder<T>> addAll(HolderLookup.RegistryLookup<T> registry) {
            return registry.listElementIds()
                    .filter(registryKey -> registryKey.location().getNamespace().equals(modId))
                    .map(key -> add(registry, key))
                    .toList();
        }

        @SuppressWarnings("unchecked")
        <T> RegistryEntries<T> getQueuedEntries(ResourceKey<T> key) {
            RegistryEntries<?> regEntries = queuedEntries.get(key.registry());

            if (regEntries == null) {
                throw new IllegalArgumentException("Registry " + key.registry() + " is not loaded from datapacks");
            }

            return (RegistryEntries<T>) regEntries;
        }
    }

    private static class RegistryEntries<T> {
        final HolderOwner<T> lookup;
        final ResourceKey<? extends Registry<T>> registry;
        final Codec<T> elementCodec;
        Map<ResourceKey<T>, T> entries = new IdentityHashMap<>();

        RegistryEntries(HolderOwner<T> lookup,
                        ResourceKey<? extends Registry<T>> registry,
                        Codec<T> elementCodec) {
            this.lookup = lookup;
            this.registry = registry;
            this.elementCodec = elementCodec;
        }

        static <T> RegistryEntries<T> create(HolderLookup.Provider lookups, RegistryDataLoader.RegistryData<T> loaderEntry) {
            HolderLookup.RegistryLookup<T> lookup = lookups.lookupOrThrow(loaderEntry.key());
            return new RegistryEntries<>(lookup, loaderEntry.key(), loaderEntry.elementCodec());
        }

        public Holder<T> add(ResourceKey<T> key, T value) {
            if (entries.put(key, value) != null) {
                throw new IllegalArgumentException("Trying to add registry key " + key + " more than once.");
            }

            return Holder.Reference.createStandAlone(lookup, key);
        }

        public Holder<T> add(ResourceLocation id, T value) {
            return add(ResourceKey.create(registry, id), value);
        }
    }

    @Override
    public CompletableFuture<?> run(CachedOutput writer) {
        return registriesFuture.thenCompose(registries -> {
            return CompletableFuture
                    .supplyAsync(() -> {
                        Entries entries = new Entries(registries, output.getModId());
                        configure(registries, entries);
                        return entries;
                    })
                    .thenCompose(entries -> {
                        final RegistryOps<JsonElement> dynamicOps = RegistryOps.create(JsonOps.INSTANCE, registries);
                        ArrayList<CompletableFuture<?>> futures = new ArrayList<>();

                        for (RegistryEntries<?> registryEntries : entries.queuedEntries.values()) {
                            futures.add(writeRegistryEntries(writer, dynamicOps, registryEntries));
                        }

                        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
                    });
        });
    }

    private <T> CompletableFuture<?> writeRegistryEntries(CachedOutput writer, RegistryOps<JsonElement> ops, RegistryEntries<T> entries) {
        final ResourceKey<? extends Registry<T>> registry = entries.registry;
        final boolean shouldOmitNamespace = registry.location().getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE) || !DynamicRegistriesImpl.FABRIC_DYNAMIC_REGISTRY_KEYS.contains(registry);
        final String directoryName = shouldOmitNamespace ? registry.location().getPath() : registry.location().getNamespace() + "/" + registry.location().getPath();
        final PackOutput.PathProvider pathResolver = output.createPathProvider(PackOutput.Target.DATA_PACK, directoryName);
        final List<CompletableFuture<?>> futures = new ArrayList<>();

        for (Map.Entry<ResourceKey<T>, T> entry : entries.entries.entrySet()) {
            Path path = pathResolver.json(entry.getKey().location());
            futures.add(writeToPath(path, writer, ops, entries.elementCodec, entry.getValue()));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private static <E> CompletableFuture<?> writeToPath(Path path, CachedOutput cache, DynamicOps<JsonElement> json, Encoder<E> encoder, E value) {
        Optional<JsonElement> optional = encoder.encodeStart(json, value).resultOrPartial((error) -> {
            LOGGER.error("Couldn't serialize element {}: {}", path, error);
        });

        if (optional.isPresent()) {
            return DataProvider.saveStable(cache, optional.get(), path);
        }

        return CompletableFuture.completedFuture(null);
    }
}
