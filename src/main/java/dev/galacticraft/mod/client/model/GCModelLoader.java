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

package dev.galacticraft.mod.client.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.event.RocketAtlasCallback;
import dev.galacticraft.mod.client.resources.RocketTextureManager;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.DyeColor;

import java.io.Reader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GCModelLoader implements ModelLoadingPlugin, IdentifiableResourceReloadListener {
    public static final GCModelLoader INSTANCE = new GCModelLoader();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final FileToIdConverter MODEL_LISTER = FileToIdConverter.json("models/misc");
    public static final ResourceLocation MODEL_LOADER_ID = Constant.id("model_loader");
    public static final ResourceLocation WHITE_SPRITE = Constant.id("obj/white");
    static final Map<ResourceLocation, GCModel.GCModelType> REGISTERED_TYPES = new ConcurrentHashMap<>();
    public static final ResourceLocation TYPE_KEY = Constant.id("type");
    public static final Codec<GCModel.GCModelType> MODEL_TYPE_CODEC = ResourceLocation.CODEC.flatXmap(id ->
                    Optional.ofNullable(REGISTERED_TYPES.get(id))
                            .map(DataResult::success).orElseGet(() -> DataResult.error(() -> "(Galacticraft) No model type with id: " + id)),
            type -> DataResult.success(type.getId())
    );
    public static final Codec<GCModel> MODEL_CODEC = MODEL_TYPE_CODEC.dispatch(TYPE_KEY.toString(), GCModel::getType, GCModel.GCModelType::codec);
    private static final ResourceLocation PARACHEST_ITEM = Constant.id("item/parachest");

    private Map<ResourceLocation, GCBakedModel> models = ImmutableMap.of();
    private AtlasSet atlases;

    @Override
    public void onInitializeModelLoader(Context pluginContext) {

        for (var color : DyeColor.values()) {
            pluginContext.addModels(Constant.id("block/" + color + "_fluid_pipe_walkway"));
        }

        pluginContext.resolveModel().register(context -> {
            var resourceId = context.id();

            if (WireBakedModel.WIRE_MARKER.equals(resourceId)) {
                return WireUnbakedModel.INSTANCE;
            } else if (WalkwayBakedModel.WALKWAY_MARKER.equals(resourceId)) {
                return WalkwayUnbakedModel.INSTANCE;
            } else if (WireWalkwayBakedModel.WIRE_WALKWAY_MARKER.equals(resourceId)) {
                return WireWalkwayUnbakedModel.INSTANCE;
            } else if (FluidPipeWalkwayBakedModel.FLUID_PIPE_WALKWAY_MARKER.equals(resourceId)) {
                return FluidPipeWalkwayUnbakedModel.INSTANCE;
            } else if (PipeBakedModel.GLASS_FLUID_PIPE_MARKER.equals(resourceId)) {
                return PipeUnbakedModel.INSTANCE;
            } else if (VacuumGlassBakedModel.VACUUM_GLASS_MODEL.equals(resourceId)) {
                return VacuumGlassUnbakedModel.INSTANCE;
            } else if (PARACHEST_ITEM.equals(resourceId)) {
                var chutes = Maps.<DyeColor, UnbakedModel>newHashMap();
                for (var color : DyeColor.values()) {
                    chutes.put(color, context.getOrLoadModel(Constant.id("block/parachest/" + color + "_chute")));
                }
                return new ParachestUnbakedModel(context.getOrLoadModel(Constant.id("block/parachest/parachest")), chutes);
            }
            return null;
        });
    }

    public static void registerModelType(GCModel.GCModelType type) {
        REGISTERED_TYPES.put(type.getId(), type);
    }

    @Override
    public ResourceLocation getFabricId() {
        return MODEL_LOADER_ID;
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier synchronizer, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        preparationsProfiler.startTick();
        Map<ResourceLocation, ResourceLocation> atlasMap = new HashMap<>();
        atlasMap.put(GCSheets.OBJ_ATLAS, Constant.id("obj"));
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        RocketAtlasCallback.EVENT.invoker().collectAtlases(atlasMap, textureManager);
        this.atlases = new AtlasSet(atlasMap, textureManager);
        CompletableFuture<Map<ResourceLocation, GCModel>> modelsFuture = loadModels(resourceManager, backgroundExecutor);
        Map<ResourceLocation, CompletableFuture<AtlasSet.StitchResult>> stitchResult = this.atlases.scheduleLoad(resourceManager, Minecraft.getInstance().options.mipmapLevels().get(), backgroundExecutor);
        preparationsProfiler.popPush("close_models");
        this.models.values().forEach(gcBakedModel -> {
            try {
                gcBakedModel.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        preparationsProfiler.pop();

        return CompletableFuture.allOf(
                        Stream.concat(stitchResult.values().stream(), Stream.of(modelsFuture)).toArray(CompletableFuture[]::new)
                ).thenApplyAsync(models -> {
                    return bakeModels(
                            preparationsProfiler,
                            stitchResult.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().join())),
                            modelsFuture.join(),
                            resourceManager
                    );
                }, backgroundExecutor)
                .thenCompose(result -> result.readyForUpload.thenApply(void_ -> result))
                .thenCompose(synchronizer::wait)
                .thenAcceptAsync(bakingResult -> {
                    reloadProfiler.startTick();
                    reloadProfiler.push("upload");
                    bakingResult.atlasPreparations.values().forEach(AtlasSet.StitchResult::upload);
                    reloadProfiler.pop();
                    models = bakingResult.bakedModels();
                    reloadProfiler.endTick();
                }, gameExecutor);
    }

    private ReloadState bakeModels(ProfilerFiller profiler, Map<ResourceLocation, AtlasSet.StitchResult> preparations, Map<ResourceLocation, GCModel> models, ResourceManager resourceManager) {
        Map<ResourceLocation, GCBakedModel> bakedModels = new HashMap<>();
        profiler.push("baking");
        Multimap<ResourceLocation, Material> missingTextures = HashMultimap.create();
        models.forEach((modelId, gcModel) -> {
            bakedModels.put(modelId, gcModel.bake(resourceManager, material -> {
                AtlasSet.StitchResult stitchResult = preparations.get(material.atlasLocation());
                TextureAtlasSprite textureAtlasSprite = stitchResult.getSprite(material.texture());
                if (textureAtlasSprite != null) {
                    return textureAtlasSprite;
                } else {
                    missingTextures.put(modelId, material);
                    return stitchResult.missing();
                }
            }));
        });

        missingTextures.asMap().forEach(
                (modelId, spriteIds) -> Constant.LOGGER.warn(
                        "Missing textures in model {}:\n{}",
                        modelId,
                        spriteIds.stream()
                                .sorted(Material.COMPARATOR)
                                .map(material -> "    " + material.atlasLocation() + ":" + material.texture())
                                .collect(Collectors.joining("\n"))
                )
        );
        CompletableFuture<Void> readyForUpload = CompletableFuture.allOf(
                preparations.values().stream().map(AtlasSet.StitchResult::readyForUpload).toArray(CompletableFuture[]::new)
        );

        profiler.pop();
        this.models = bakedModels;
        profiler.endTick();
        return new ReloadState(bakedModels, preparations, readyForUpload);
    }

    private static CompletableFuture<Map<ResourceLocation, GCModel>> loadModels(ResourceManager resourceManager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> MODEL_LISTER.listMatchingResources(resourceManager), executor)
                .thenCompose(
                        map -> {
                            List<CompletableFuture<Pair<ResourceLocation, GCModel>>> models = new ArrayList<>(map.size());

                            for (Map.Entry<ResourceLocation, Resource> entry : map.entrySet()) {
                                models.add(CompletableFuture.supplyAsync(() -> {
                                    ResourceLocation modelId = entry.getKey();
                                    try {
                                        Reader reader = entry.getValue().openAsReader();

                                        Pair<ResourceLocation, GCModel> modelPair;
                                        try {
                                            DataResult<GCModel> model = MODEL_CODEC.parse(JsonOps.INSTANCE, GsonHelper.convertToJsonObject(GsonHelper.fromJson(GSON, reader, JsonElement.class), "top element"));
                                            if (model.error().isPresent())
                                                return null;
                                            modelPair = Pair.of(entry.getKey(), model.getOrThrow(false, error -> Constant.LOGGER.error("Failed to load model: {}, {}", modelId, error)));
                                        } catch (Throwable error) {
                                            if (reader != null) {
                                                try {
                                                    reader.close();
                                                } catch (Throwable nestedError) {
                                                    error.addSuppressed(nestedError);
                                                }
                                            }

                                            throw error;
                                        }

                                        if (reader != null) {
                                            reader.close();
                                        }

                                        return modelPair;
                                    } catch (Exception var6) {
                                        Constant.LOGGER.error("Failed to load model {}", entry.getKey(), var6);
                                        return null;
                                    }
                                }, executor));
                            }

                            return Util.sequence(models)
                                    .thenApply(listx -> listx.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond)));
                        }
                );
    }

    @Override
    public Collection<ResourceLocation> getFabricDependencies() {
        return List.of(RocketTextureManager.ID);
    }

    public Map<ResourceLocation, GCBakedModel> getModels() {
        return models;
    }

    public GCBakedModel getModel(ResourceLocation id) {
        return this.models.getOrDefault(id, new GCMissingModel());
    }

    public AtlasSet getAtlases() {
        return atlases;
    }

    public TextureAtlasSprite getDefaultSprite() {
        return this.atlases.getAtlas(GCSheets.OBJ_ATLAS).getSprite(WHITE_SPRITE);
    }

    public record ReloadState(
            Map<ResourceLocation, GCBakedModel> bakedModels,
            Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations,
            CompletableFuture<Void> readyForUpload
    ) {
    }
}