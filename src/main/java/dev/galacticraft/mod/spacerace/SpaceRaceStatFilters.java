/*
 * Copyright (c) 2019-2026 Team Galacticraft
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

package dev.galacticraft.mod.spacerace;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCStats;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class SpaceRaceStatFilters {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Path CONFIG_DIRECTORY = FabricLoader.getInstance().getConfigDir().resolve("galacticraft").resolve("space_race_stats");
    private static final Path GENERAL_FILE = CONFIG_DIRECTORY.resolve("general.json");
    private static final Path ITEMS_FILE = CONFIG_DIRECTORY.resolve("items.json");
    private static final Path MOBS_FILE = CONFIG_DIRECTORY.resolve("mobs.json");
    private static final List<ResourceLocation> GENERAL_STAT_IDS = List.of(
            GCStats.CLEAN_PARACHUTE,
            GCStats.OPEN_PARACHEST,
            GCStats.INTERACT_WITH_ROCKET_WORKBENCH,
            GCStats.LAUNCH_ROCKET,
            GCStats.CRASH_LANDING,
            GCStats.SAFE_LANDING,
            GCStats.EAT_CHEESE_WHEEL_SLICE,
            GCStats.CHEESE_SLICED
    );

    private SpaceRaceStatFilters() {
    }

    public static Filters load() {
        ensureFilesExist();
        return new Filters(
                loadConfiguredIds(GENERAL_FILE, GENERAL_STAT_IDS),
                loadConfiguredIds(ITEMS_FILE, collectItemIds()),
                loadConfiguredIds(MOBS_FILE, collectEntityIds())
        );
    }

    private static void ensureFilesExist() {
        try {
            Files.createDirectories(CONFIG_DIRECTORY);
            writeTemplateIfMissing(GENERAL_FILE, new FilterFile(List.of(), stringifyIds(GENERAL_STAT_IDS)));
            writeTemplateIfMissing(ITEMS_FILE, new FilterFile(List.of(), stringifyIds(collectItemIds())));
            writeTemplateIfMissing(MOBS_FILE, new FilterFile(List.of(), stringifyIds(collectEntityIds())));
        } catch (IOException exception) {
            Constant.LOGGER.warn("Failed to create space race stat filter files", exception);
        }
    }

    private static void writeTemplateIfMissing(Path file, FilterFile filterFile) throws IOException {
        if (Files.exists(file)) {
            return;
        }

        JsonElement json = FilterFile.CODEC.encodeStart(JsonOps.INSTANCE, filterFile)
                .getOrThrow(error -> new IllegalStateException("Failed to encode space race stat filters: " + error));
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW)) {
            GSON.toJson(json, writer);
        }
    }

    private static List<ResourceLocation> collectItemIds() {
        List<ResourceLocation> itemIds = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (item == Items.AIR) {
                continue;
            }
            itemIds.add(BuiltInRegistries.ITEM.getKey(item));
        }
        itemIds.sort(Comparator.comparing(ResourceLocation::toString, String.CASE_INSENSITIVE_ORDER));
        return itemIds;
    }

    private static List<ResourceLocation> collectEntityIds() {
        List<ResourceLocation> entityIds = new ArrayList<>(BuiltInRegistries.ENTITY_TYPE.keySet());
        entityIds.sort(Comparator.comparing(ResourceLocation::toString, String.CASE_INSENSITIVE_ORDER));
        return entityIds;
    }

    private static List<String> stringifyIds(List<ResourceLocation> ids) {
        List<String> values = new ArrayList<>(ids.size());
        for (ResourceLocation id : ids) {
            values.add(id.toString());
        }
        return values;
    }

    private static List<ResourceLocation> loadConfiguredIds(Path file, List<ResourceLocation> availableIds) {
        if (!Files.exists(file)) {
            return List.of();
        }

        FilterFile filterFile = readFilterFile(file);
        if (filterFile == null) {
            return List.of();
        }

        Set<ResourceLocation> availableIdSet = Set.copyOf(availableIds);
        LinkedHashSet<ResourceLocation> configuredIds = new LinkedHashSet<>();
        for (String entry : filterFile.enabled()) {
            ResourceLocation id = tryParseResourceLocation(entry);
            if (id == null || !availableIdSet.contains(id)) {
                Constant.LOGGER.warn("Ignoring unknown space race stat filter entry '{}' in {}", entry, file);
                continue;
            }
            configuredIds.add(id);
        }
        return List.copyOf(configuredIds);
    }

    private static @Nullable FilterFile readFilterFile(Path file) {
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonElement json = GSON.fromJson(reader, JsonElement.class);
            if (json == null) {
                return null;
            }
            return FilterFile.CODEC.parse(JsonOps.INSTANCE, json)
                    .getOrThrow(error -> new IllegalStateException("Failed to parse space race stat filters in " + file + ": " + error));
        } catch (Exception exception) {
            Constant.LOGGER.warn("Failed to read space race stat filter file {}", file, exception);
            return null;
        }
    }

    private static @Nullable ResourceLocation tryParseResourceLocation(String token) {
        try {
            return ResourceLocation.parse(token);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private record FilterFile(List<String> enabled, List<String> available) {
        private static final Codec<FilterFile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.listOf().optionalFieldOf("enabled", List.of()).forGetter(FilterFile::enabled),
                Codec.STRING.listOf().optionalFieldOf("available", List.of()).forGetter(FilterFile::available)
        ).apply(instance, FilterFile::new));

        private FilterFile {
            enabled = List.copyOf(enabled);
            available = List.copyOf(available);
        }
    }

    public record Filters(
            List<ResourceLocation> generalStats,
            List<ResourceLocation> itemIds,
            List<ResourceLocation> mobIds
    ) {
        public Filters {
            generalStats = List.copyOf(generalStats);
            itemIds = List.copyOf(itemIds);
            mobIds = List.copyOf(mobIds);
        }
    }
}
