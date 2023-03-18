package dev.galacticraft.mod.data.content;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.LevelStem;
import org.jetbrains.annotations.NotNull;

public class GCLevelStemProvider implements DataProvider {
	private final PackOutput.PathProvider path;
	private final CompletableFuture<HolderLookup.Provider> registriesFuture;
	private final Consumer<BootstapContext<LevelStem>> bootstrap;

	public GCLevelStemProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture, Consumer<BootstapContext<LevelStem>> bootstrap) {
		this.path = output.createPathProvider(PackOutput.Target.DATA_PACK, "dimension");
		this.registriesFuture = registriesFuture;
		this.bootstrap = bootstrap;
	}

	@Override
	public @NotNull CompletableFuture<?> run(CachedOutput output) {
		return this.registriesFuture.thenCompose(registries -> {
			Map<ResourceLocation, LevelStem> entries = new HashMap<>();
			this.bootstrap.accept(new BootstapContext<>() {
				@Override
				public Holder.Reference<LevelStem> register(ResourceKey<LevelStem> resourceKey, LevelStem object, Lifecycle lifecycle) {
					entries.put(resourceKey.location(), object);
					return Holder.Reference.createStandAlone(null, resourceKey);
				}

				@Override
				public <S> HolderGetter<S> lookup(ResourceKey<? extends net.minecraft.core.Registry<? extends S>> resourceKey) {
					return registries.lookupOrThrow(resourceKey);
				}
			});
			CompletableFuture<?>[] futures = new CompletableFuture[entries.size()];
			int i = 0;
			RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, registries);
			for (Map.Entry<ResourceLocation, LevelStem> entry : entries.entrySet()) {
				CompletableFuture<?> completableFuture = CompletableFuture.supplyAsync(() -> LevelStem.CODEC.encodeStart(ops, entry.getValue()).get().orThrow())
						.thenCompose((json -> DataProvider.saveStable(output, json, this.path.json(entry.getKey()))));
				futures[i++] = completableFuture;
			}
			return CompletableFuture.allOf(futures);
		});
	}

	@Override
	public @NotNull String getName() {
		return "Level Stems";
	}
}