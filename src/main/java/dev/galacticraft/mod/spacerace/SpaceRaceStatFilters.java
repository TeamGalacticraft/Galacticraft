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

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCStats;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class SpaceRaceStatFilters {
    private static final Path CONFIG_DIRECTORY = FabricLoader.getInstance().getConfigDir().resolve("galacticraft").resolve("space_race_stats");
    private static final Path GENERAL_FILE = CONFIG_DIRECTORY.resolve("general.cfg");
    private static final Path ITEMS_FILE = CONFIG_DIRECTORY.resolve("items.cfg");
    private static final Path MOBS_FILE = CONFIG_DIRECTORY.resolve("mobs.cfg");
    private static final Map<String, ResourceLocation> GENERAL_STATS_BY_NAME = collectGeneralStatsByName();
    private static final Set<ResourceLocation> GENERAL_STAT_IDS = Set.copyOf(GCStats.getAllStatIds());

    private SpaceRaceStatFilters() {
    }

    public static Filters load() {
        ensureFilesExist();
        return new Filters(
                loadConfiguredIds(GENERAL_FILE, SpaceRaceStatFilters::resolveGeneralStat),
                loadConfiguredIds(ITEMS_FILE, SpaceRaceStatFilters::tryParseResourceLocation),
                loadConfiguredIds(MOBS_FILE, SpaceRaceStatFilters::tryParseResourceLocation)
        );
    }

    private static void ensureFilesExist() {
        try {
            Files.createDirectories(CONFIG_DIRECTORY);
            writeTemplateIfMissing(GENERAL_FILE, "general", new ArrayList<>(GENERAL_STATS_BY_NAME.keySet()));
            writeTemplateIfMissing(ITEMS_FILE, "items", collectItemIds());
            writeTemplateIfMissing(MOBS_FILE, "mobs", collectEntityIds());
        } catch (IOException exception) {
            Constant.LOGGER.warn("Failed to create space race stat filter files", exception);
        }
    }

    private static void writeTemplateIfMissing(Path file, String sectionName, List<String> entries) throws IOException {
        if (Files.exists(file)) {
            return;
        }

        List<String> lines = new ArrayList<>(entries.size() + 3);
        lines.add("# Uncomment entries to include them in the Space Race " + sectionName + " tab.");
        lines.add(sectionName + " {");
        for (String entry : entries) {
            lines.add("    #" + entry);
        }
        lines.add("}");
        Files.write(file, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
    }

    private static List<String> collectItemIds() {
        List<String> itemIds = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (item == Items.AIR) {
                continue;
            }
            itemIds.add(BuiltInRegistries.ITEM.getKey(item).toString());
        }
        itemIds.sort(String.CASE_INSENSITIVE_ORDER);
        return itemIds;
    }

    private static List<String> collectEntityIds() {
        List<String> entityIds = new ArrayList<>();
        for (ResourceLocation id : BuiltInRegistries.ENTITY_TYPE.keySet()) {
            entityIds.add(id.toString());
        }
        entityIds.sort(String.CASE_INSENSITIVE_ORDER);
        return entityIds;
    }

    private static Map<String, ResourceLocation> collectGeneralStatsByName() {
        Map<String, ResourceLocation> generalStats = new LinkedHashMap<>();
        List<Field> fields = new ArrayList<>();
        for (Field field : GCStats.class.getDeclaredFields()) {
            if (field.getType() == ResourceLocation.class && Modifier.isStatic(field.getModifiers())) {
                fields.add(field);
            }
        }
        fields.sort(Comparator.comparing(Field::getName, String.CASE_INSENSITIVE_ORDER));

        for (Field field : fields) {
            try {
                ResourceLocation statId = (ResourceLocation) field.get(null);
                if (statId != null) {
                    generalStats.put(field.getName().toUpperCase(Locale.ROOT), statId);
                }
            } catch (IllegalAccessException ignored) {
            }
        }
        return generalStats;
    }

    private static List<ResourceLocation> loadConfiguredIds(Path file, Function<String, @Nullable ResourceLocation> resolver) {
        LinkedHashSet<ResourceLocation> configuredIds = new LinkedHashSet<>();
        if (!Files.exists(file)) {
            return List.of();
        }

        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String token = extractToken(line);
                if (token == null) {
                    continue;
                }

                ResourceLocation id = resolver.apply(token);
                if (id != null) {
                    configuredIds.add(id);
                } else {
                    Constant.LOGGER.warn("Ignoring unknown space race stat filter entry '{}' in {}", token, file);
                }
            }
        } catch (IOException exception) {
            Constant.LOGGER.warn("Failed to read space race stat filter file {}", file, exception);
        }
        return List.copyOf(configuredIds);
    }

    private static @Nullable String extractToken(String line) {
        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.endsWith("{") || trimmed.equals("}")) {
            return null;
        }

        int commentIndex = trimmed.indexOf('#');
        if (commentIndex >= 0) {
            trimmed = trimmed.substring(0, commentIndex).trim();
        }
        if (trimmed.isEmpty() || trimmed.endsWith("{") || trimmed.equals("}")) {
            return null;
        }
        return trimmed;
    }

    private static @Nullable ResourceLocation resolveGeneralStat(String token) {
        ResourceLocation statId = GENERAL_STATS_BY_NAME.get(token.toUpperCase(Locale.ROOT));
        if (statId != null) {
            return statId;
        }

        ResourceLocation parsed = tryParseResourceLocation(token);
        if (parsed != null && GENERAL_STAT_IDS.contains(parsed)) {
            return parsed;
        }

        ResourceLocation implicitGalacticraftId = Constant.id(token.toLowerCase(Locale.ROOT));
        if (GENERAL_STAT_IDS.contains(implicitGalacticraftId)) {
            return implicitGalacticraftId;
        }
        return null;
    }

    private static @Nullable ResourceLocation tryParseResourceLocation(String token) {
        try {
            return ResourceLocation.parse(token);
        } catch (IllegalArgumentException exception) {
            return null;
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
