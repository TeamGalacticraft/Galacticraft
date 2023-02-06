package dev.galacticraft.mod.data.content;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.LevelStem;
import org.jetbrains.annotations.NotNull;

public abstract class LevelStemProvider implements DataProvider {
	private final PackOutput.PathProvider path;
	private final CompletableFuture<HolderLookup.Provider> registriesFuture;

	protected LevelStemProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		this.path = output.createPathProvider(PackOutput.Target.DATA_PACK, "dimension");
		this.registriesFuture = registriesFuture;
	}

	public abstract Registry generate(HolderLookup.Provider registries, Registry registry);

	@Override
	public @NotNull CompletableFuture<?> run(CachedOutput output) {
		return this.registriesFuture.thenCompose(registries -> {
			Registry registry = this.generate(registries, new Registry());
			CompletableFuture<?>[] futures = new CompletableFuture[registry.entries.size()];
			int i = 0;
			RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, registries);
			for (Map.Entry<ResourceLocation, LevelStem> entry : registry.entries.entrySet()) {
				CompletableFuture<?> completableFuture = CompletableFuture.supplyAsync(() -> LevelStem.CODEC.encodeStart(ops, entry.getValue()).get().orThrow())
						.thenCompose((json -> DataProvider.saveStable(output, json, this.path.json(entry.getKey()))));
				futures[i++] = completableFuture;
			}
			return CompletableFuture.allOf(futures);
		});
	}

	@Override
	public @NotNull String getName() {
		return "Level Stem";
	}

	public static final class Registry {
		private final Map<ResourceLocation, LevelStem> entries = new HashMap<>();

		private Registry() {
		}

		public void add(@NotNull ResourceKey<LevelStem> key, @NotNull LevelStem value) {
			this.add(key.location(), value);
		}

		public void add(@NotNull ResourceLocation id, @NotNull LevelStem value) {
			this.entries.put(id, value);
		}
	}
}