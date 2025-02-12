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

package dev.galacticraft.mod.data.content;

import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.LevelStem;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class GCLevelStemProvider implements DataProvider {
	private final PackOutput.PathProvider path;
	private final CompletableFuture<HolderLookup.Provider> registriesFuture;
	private final Consumer<BootstrapContext<LevelStem>> bootstrap;

	public GCLevelStemProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture, Consumer<BootstrapContext<LevelStem>> bootstrap) {
		this.path = output.createPathProvider(PackOutput.Target.DATA_PACK, "dimension");
		this.registriesFuture = registriesFuture;
		this.bootstrap = bootstrap;
	}

	@Override
	public @NotNull CompletableFuture<?> run(CachedOutput output) {
		return this.registriesFuture.thenCompose(registries -> {
			Map<ResourceLocation, LevelStem> entries = new HashMap<>();
			this.bootstrap.accept(new BootstrapContext<>() {
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
			var i = 0;
			var ops = RegistryOps.create(JsonOps.INSTANCE, registries);
			for (var entry : entries.entrySet()) {
				var completableFuture = CompletableFuture.supplyAsync(() -> LevelStem.CODEC.encodeStart(ops, entry.getValue()).getOrThrow())
						.thenCompose(json -> DataProvider.saveStable(output, json, this.path.json(entry.getKey())));
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