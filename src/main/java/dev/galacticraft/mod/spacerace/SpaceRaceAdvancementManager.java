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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import dev.galacticraft.impl.network.s2c.SpaceRaceStatsPayload;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.GCStats;
import dev.galacticraft.mod.content.item.GCItems;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.scores.PlayerTeam;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public final class SpaceRaceAdvancementManager {
    private static final String HIDE_SERVER_ADVANCEMENTS_TAG = "galacticraft.hide_server_advancements";
    private static final String HIDE_GLOBAL_LEADERBOARD_TAG = "galacticraft.hide_global_leaderboard";
    private static final String VISIBILITY_FILE_NAME = "spacerace_visibility.dat";
    private static final String VISIBILITY_ENTRIES_KEY = "entries";
    private static final String VISIBILITY_UUID_KEY = "uuid";
    private static final String VISIBILITY_HIDE_SERVER_KEY = "hide_server_advancements";
    private static final String VISIBILITY_HIDE_GLOBAL_KEY = "hide_global_leaderboard";
    private static final String STAT_ROOT_KEY = "stats";
    private static final String CUSTOM_STAT_KEY = "minecraft:custom";
    private static final String BLOCK_MINED_STAT_KEY = "minecraft:mined";
    private static final String ITEM_CRAFTED_STAT_KEY = "minecraft:crafted";
    private static final String ITEM_USED_STAT_KEY = "minecraft:used";
    private static final String ITEM_BROKEN_STAT_KEY = "minecraft:broken";
    private static final String ITEM_PICKED_UP_STAT_KEY = "minecraft:picked_up";
    private static final String ITEM_DROPPED_STAT_KEY = "minecraft:dropped";
    private static final String ENTITY_KILLED_STAT_KEY = "minecraft:killed";
    private static final long UNKNOWN_COMPLETION_TIME = Long.MAX_VALUE;
    private static final int MAX_UNLOCKER_TOOLTIP_NAMES = 3;
    private static final DateTimeFormatter LEGACY_ADVANCEMENT_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z", Locale.ROOT);

    private SpaceRaceAdvancementManager() {
    }

    public static void setVisibility(ServerPlayer player, boolean hideServerAdvancements, boolean hideGlobalLeaderboard) {
        setVisibilityTag(player, HIDE_SERVER_ADVANCEMENTS_TAG, hideServerAdvancements);
        setVisibilityTag(player, HIDE_GLOBAL_LEADERBOARD_TAG, hideGlobalLeaderboard);
        saveVisibilityPreference(player.server, player.getUUID(), hideServerAdvancements, hideGlobalLeaderboard);
    }

    public static boolean isServerAdvancementsHidden(ServerPlayer player) {
        if (player.getTags().contains(HIDE_SERVER_ADVANCEMENTS_TAG)) {
            return true;
        }
        VisibilityPreference preference = loadVisibilityPreferences(player.server).get(player.getUUID());
        return preference != null && preference.hideServerAdvancements();
    }

    public static boolean isGlobalLeaderboardHidden(ServerPlayer player) {
        if (player.getTags().contains(HIDE_GLOBAL_LEADERBOARD_TAG)) {
            return true;
        }
        VisibilityPreference preference = loadVisibilityPreferences(player.server).get(player.getUUID());
        return preference != null && preference.hideGlobalLeaderboard();
    }

    public static void sendServerStats(ServerPlayer receiver) {
        Map<UUID, VisibilityPreference> visibilityPreferences = loadVisibilityPreferences(receiver.server);
        List<ServerPlayer> onlinePlayers = new ArrayList<>(receiver.server.getPlayerList().getPlayers());
        onlinePlayers.sort(Comparator.comparing(player -> player.getGameProfile().getName(), String.CASE_INSENSITIVE_ORDER));

        Map<ResourceLocation, AdvancementHolder> treeAdvancements = collectGalacticraftAdvancements(receiver.server.getAdvancements(), true);
        Map<ResourceLocation, AdvancementHolder> nonRootAdvancements = collectGalacticraftAdvancements(receiver.server.getAdvancements(), false);
        Map<ResourceLocation, Integer> depthCache = new HashMap<>();

        List<PlayerAdvancementSource> advancementSources = collectPlayerAdvancementSources(
                receiver,
                onlinePlayers,
                visibilityPreferences,
                treeAdvancements,
                depthCache
        );
        List<SpaceRaceStatsPayload.PlayerStatsEntry> playerEntries = buildPlayerEntries(advancementSources, nonRootAdvancements);
        List<SpaceRaceStatsPayload.TeamStatsEntry> teamEntries = buildTeamEntries(receiver.server, advancementSources, treeAdvancements, nonRootAdvancements, depthCache);
        Map<ResourceLocation, List<String>> serverFirstUnlockers = buildFirstUnlockersByAdvancement(advancementSources);
        List<SpaceRaceStatsPayload.ServerAdvancementNode> serverAdvancementNodes = buildServerAdvancementNodes(treeAdvancements, advancementSources, serverFirstUnlockers);

        List<PlayerStatsSource> allPlayers = collectPlayerStatsSources(receiver.server, onlinePlayers, visibilityPreferences);
        GlobalLeaderboardData globalLeaderboardData = buildGlobalLeaderboardData(allPlayers);
        ServerPlayNetworking.send(receiver, new SpaceRaceStatsPayload(
                playerEntries,
                teamEntries,
                serverAdvancementNodes,
                globalLeaderboardData.generalRows(),
                globalLeaderboardData.itemRows(),
                globalLeaderboardData.mobRows(),
                isServerAdvancementsHidden(receiver, visibilityPreferences),
                isGlobalLeaderboardHidden(receiver, visibilityPreferences)
        ));
    }

    private static List<PlayerAdvancementSource> collectPlayerAdvancementSources(
            ServerPlayer receiver,
            List<ServerPlayer> onlinePlayers,
            Map<UUID, VisibilityPreference> visibilityPreferences,
            Map<ResourceLocation, AdvancementHolder> allGalacticraftAdvancements,
            Map<ResourceLocation, Integer> depthCache
    ) {
        List<PlayerAdvancementSource> entries = new ArrayList<>();
        Map<UUID, ServerPlayer> onlinePlayersById = new HashMap<>();
        Path advancementsDirectory = receiver.server.getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR);

        for (ServerPlayer player : onlinePlayers) {
            onlinePlayersById.put(player.getUUID(), player);
            List<AdvancementHolder> orderedAdvancements = getOrderedAdvancements(player, allGalacticraftAdvancements, depthCache);
            List<ResourceLocation> orderedIds = toAdvancementIds(orderedAdvancements);
            Map<ResourceLocation, Long> completionTimes = loadCompletedAdvancements(advancementsDirectory.resolve(player.getUUID() + ".json"));
            boolean hidden = isServerAdvancementsHidden(player, visibilityPreferences);
            entries.add(new PlayerAdvancementSource(
                    player.getUUID(),
                    player.getGameProfile().getName(),
                    hidden,
                    orderedIds,
                    normalizeCompletionTimes(orderedIds, completionTimes)
            ));
        }

        if (Files.isDirectory(advancementsDirectory)) {
            try (Stream<Path> files = Files.list(advancementsDirectory)) {
                List<Path> advancementFiles = files
                        .filter(path -> path.getFileName().toString().endsWith(".json"))
                        .sorted(Comparator.comparing(path -> path.getFileName().toString(), String.CASE_INSENSITIVE_ORDER))
                        .toList();

                for (Path file : advancementFiles) {
                    String fileName = file.getFileName().toString();
                    UUID playerId = tryParseUuid(fileName.substring(0, fileName.length() - 5));
                    if (playerId == null || onlinePlayersById.containsKey(playerId)) {
                        continue;
                    }

                    VisibilityPreference preference = visibilityPreferences.getOrDefault(playerId, VisibilityPreference.DEFAULT);
                    Map<ResourceLocation, Long> completionTimes = loadCompletedAdvancements(file);
                    List<AdvancementHolder> orderedAdvancements = getOrderedAdvancements(new ArrayList<>(completionTimes.keySet()), allGalacticraftAdvancements, depthCache);
                    List<ResourceLocation> orderedIds = toAdvancementIds(orderedAdvancements);
                    boolean hidden = preference.hideServerAdvancements();
                    entries.add(new PlayerAdvancementSource(
                            playerId,
                            resolvePlayerName(receiver.server, playerId),
                            hidden,
                            orderedIds,
                            normalizeCompletionTimes(orderedIds, completionTimes)
                    ));
                }
            } catch (IOException exception) {
                Constant.LOGGER.warn("Failed to read advancement files for space race server stats", exception);
            }
        }

        entries.sort(Comparator
                .comparingInt((PlayerAdvancementSource entry) -> entry.completedAdvancementIds().size()).reversed()
                .thenComparing(PlayerAdvancementSource::playerName, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(entry -> entry.playerId().toString()));
        return entries;
    }

    private static List<SpaceRaceStatsPayload.PlayerStatsEntry> buildPlayerEntries(
            List<PlayerAdvancementSource> advancementSources,
            Map<ResourceLocation, AdvancementHolder> nonRootAdvancements
    ) {
        List<SpaceRaceStatsPayload.PlayerStatsEntry> entries = new ArrayList<>();
        for (PlayerAdvancementSource source : advancementSources) {
            if (source.hiddenFromReceiver()) {
                continue;
            }
            entries.add(new SpaceRaceStatsPayload.PlayerStatsEntry(
                    source.playerId(),
                    source.playerName(),
                    toAdvancementIcons(source.completedAdvancementIds(), nonRootAdvancements)
            ));
        }
        return entries;
    }

    private static List<SpaceRaceStatsPayload.TeamStatsEntry> buildTeamEntries(
            MinecraftServer server,
            List<PlayerAdvancementSource> advancementSources,
            Map<ResourceLocation, AdvancementHolder> treeAdvancements,
            Map<ResourceLocation, AdvancementHolder> nonRootAdvancements,
            Map<ResourceLocation, Integer> depthCache
    ) {
        Map<String, TeamAdvancementAccumulator> accumulators = new LinkedHashMap<>();
        for (PlayerAdvancementSource source : advancementSources) {
            TeamDescriptor descriptor = resolveTeamDescriptor(server, source);
            if (descriptor == null) {
                continue;
            }
            TeamAdvancementAccumulator accumulator = accumulators.computeIfAbsent(
                    descriptor.teamId(),
                    id -> new TeamAdvancementAccumulator(id, descriptor.teamName(), descriptor.flagIcon().copy())
            );
            accumulator.advancementIds().addAll(source.completedAdvancementIds());
            accumulator.members().add(source);
        }

        List<TeamAdvancementAccumulator> sortedTeams = new ArrayList<>(accumulators.values());
        sortedTeams.sort(Comparator
                .comparingInt((TeamAdvancementAccumulator team) -> team.advancementIds().size()).reversed()
                .thenComparing(TeamAdvancementAccumulator::teamName, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(TeamAdvancementAccumulator::teamId));

        List<SpaceRaceStatsPayload.TeamStatsEntry> entries = new ArrayList<>(sortedTeams.size());
        for (TeamAdvancementAccumulator team : sortedTeams) {
            List<ResourceLocation> orderedIds = sortAdvancementIds(team.advancementIds(), treeAdvancements, depthCache);
            Map<ResourceLocation, List<String>> firstUnlockersByAdvancement = buildFirstUnlockersByAdvancement(team.members());
            List<SpaceRaceStatsPayload.TeamMemberEntry> members = buildTeamMembers(team.members(), nonRootAdvancements);
            entries.add(new SpaceRaceStatsPayload.TeamStatsEntry(
                    team.teamId(),
                    team.teamName(),
                    team.flagIcon().copy(),
                    toAdvancementIcons(orderedIds, nonRootAdvancements, firstUnlockersByAdvancement),
                    members
            ));
        }
        return entries;
    }

    private static List<SpaceRaceStatsPayload.TeamMemberEntry> buildTeamMembers(
            List<PlayerAdvancementSource> memberSources,
            Map<ResourceLocation, AdvancementHolder> nonRootAdvancements
    ) {
        List<PlayerAdvancementSource> sortedMembers = new ArrayList<>(memberSources);
        sortedMembers.sort(Comparator
                .comparingInt((PlayerAdvancementSource source) -> source.completedAdvancementIds().size()).reversed()
                .thenComparing(PlayerAdvancementSource::playerName, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(source -> source.playerId().toString()));

        List<SpaceRaceStatsPayload.TeamMemberEntry> members = new ArrayList<>(sortedMembers.size());
        for (PlayerAdvancementSource member : sortedMembers) {
            if (member.hiddenFromReceiver()) {
                continue;
            }
            members.add(new SpaceRaceStatsPayload.TeamMemberEntry(
                    member.playerId(),
                    member.playerName(),
                    toAdvancementIcons(member.completedAdvancementIds(), nonRootAdvancements)
            ));
        }
        return members;
    }

    private static @Nullable TeamDescriptor resolveTeamDescriptor(MinecraftServer server, PlayerAdvancementSource source) {
        PlayerTeam team = server.getScoreboard().getPlayerTeam(source.playerName());
        if (team == null) {
            return null;
        }

        String teamName = team.getDisplayName().getString();
        if (teamName.isBlank()) {
            teamName = team.getName();
        }
        return new TeamDescriptor(
                team.getName(),
                teamName,
                createTeamFlagStack(team.getColor())
        );
    }

    private static ItemStack createTeamFlagStack(ChatFormatting formatting) {
        DyeColor dyeColor = chatFormattingToDyeColor(formatting);
        Item flagItem = GCItems.FLAGS.get(dyeColor);
        if (flagItem == null) {
            flagItem = GCItems.FLAGS.get(DyeColor.WHITE);
        }
        return new ItemStack(flagItem);
    }

    private static DyeColor chatFormattingToDyeColor(ChatFormatting formatting) {
        if (formatting == null) {
            return DyeColor.WHITE;
        }
        return switch (formatting) {
            case BLACK -> DyeColor.BLACK;
            case DARK_BLUE, BLUE -> DyeColor.BLUE;
            case DARK_GREEN, GREEN -> DyeColor.GREEN;
            case DARK_AQUA, AQUA -> DyeColor.CYAN;
            case DARK_RED, RED -> DyeColor.RED;
            case DARK_PURPLE, LIGHT_PURPLE -> DyeColor.PURPLE;
            case GOLD, YELLOW -> DyeColor.YELLOW;
            case GRAY -> DyeColor.GRAY;
            case DARK_GRAY -> DyeColor.LIGHT_GRAY;
            case WHITE -> DyeColor.WHITE;
            default -> DyeColor.WHITE;
        };
    }

    private static List<ResourceLocation> sortAdvancementIds(
            Collection<ResourceLocation> ids,
            Map<ResourceLocation, AdvancementHolder> allGalacticraftAdvancements,
            Map<ResourceLocation, Integer> depthCache
    ) {
        if (ids.isEmpty()) {
            return List.of();
        }
        List<AdvancementHolder> orderedAdvancements = getOrderedAdvancements(new ArrayList<>(ids), allGalacticraftAdvancements, depthCache);
        return toAdvancementIds(orderedAdvancements);
    }

    private static List<SpaceRaceStatsPayload.ServerAdvancementNode> buildServerAdvancementNodes(
            Map<ResourceLocation, AdvancementHolder> treeAdvancements,
            List<PlayerAdvancementSource> advancementSources,
            Map<ResourceLocation, List<String>> firstUnlockersByAdvancement
    ) {
        Set<ResourceLocation> serverCompletedAdvancements = new HashSet<>();
        for (PlayerAdvancementSource source : advancementSources) {
            serverCompletedAdvancements.addAll(source.completedAdvancementIds());
        }

        List<AdvancementHolder> orderedAdvancements = new ArrayList<>(treeAdvancements.values());
        orderedAdvancements.sort(Comparator
                .comparingDouble((AdvancementHolder advancement) -> advancement.value().display().map(display -> display.getY()).orElse(0.0F))
                .thenComparingDouble(advancement -> advancement.value().display().map(display -> display.getX()).orElse(0.0F))
                .thenComparing(advancement -> advancement.id().toString()));

        List<SpaceRaceStatsPayload.ServerAdvancementNode> nodes = new ArrayList<>(orderedAdvancements.size());
        for (AdvancementHolder advancement : orderedAdvancements) {
            var displayInfo = advancement.value().display().orElse(null);
            if (displayInfo == null) {
                continue;
            }

            ResourceLocation parentId = advancement.value().parent().orElse(null);
            if (parentId != null && !treeAdvancements.containsKey(parentId)) {
                parentId = null;
            }

            nodes.add(new SpaceRaceStatsPayload.ServerAdvancementNode(
                    advancement.id(),
                    parentId,
                    displayInfo.getIcon().copy(),
                    displayInfo.getTitle().getString(),
                    getAdvancementTitleColor(displayInfo.getType()),
                    serverCompletedAdvancements.contains(advancement.id()),
                    displayInfo.getX(),
                    displayInfo.getY(),
                    firstUnlockersByAdvancement.getOrDefault(advancement.id(), List.of())
            ));
        }
        return nodes;
    }

    private static Map<ResourceLocation, List<String>> buildFirstUnlockersByAdvancement(List<PlayerAdvancementSource> sources) {
        Map<ResourceLocation, List<UnlockerCandidate>> candidatesByAdvancement = new HashMap<>();
        for (PlayerAdvancementSource source : sources) {
            if (source.hiddenFromReceiver()) {
                continue;
            }
            for (ResourceLocation advancementId : source.completedAdvancementIds()) {
                long completionTime = source.completionTimesByAdvancement().getOrDefault(advancementId, UNKNOWN_COMPLETION_TIME);
                candidatesByAdvancement
                        .computeIfAbsent(advancementId, id -> new ArrayList<>())
                        .add(new UnlockerCandidate(source.playerId(), source.playerName(), completionTime));
            }
        }

        Map<ResourceLocation, List<String>> firstUnlockersByAdvancement = new HashMap<>();
        for (Map.Entry<ResourceLocation, List<UnlockerCandidate>> entry : candidatesByAdvancement.entrySet()) {
            List<UnlockerCandidate> candidates = entry.getValue();
            candidates.sort(Comparator
                    .comparingLong(UnlockerCandidate::completionTime)
                    .thenComparing(UnlockerCandidate::playerName, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(candidate -> candidate.playerId().toString()));

            List<String> firstUnlockers = new ArrayList<>(MAX_UNLOCKER_TOOLTIP_NAMES);
            Set<UUID> seenPlayerIds = new HashSet<>();
            for (UnlockerCandidate candidate : candidates) {
                if (!seenPlayerIds.add(candidate.playerId())) {
                    continue;
                }
                firstUnlockers.add(candidate.playerName());
                if (firstUnlockers.size() >= MAX_UNLOCKER_TOOLTIP_NAMES) {
                    break;
                }
            }
            if (!firstUnlockers.isEmpty()) {
                firstUnlockersByAdvancement.put(entry.getKey(), List.copyOf(firstUnlockers));
            }
        }
        return firstUnlockersByAdvancement;
    }

    private static boolean isServerAdvancementsHidden(ServerPlayer player, Map<UUID, VisibilityPreference> visibilityPreferences) {
        if (player.getTags().contains(HIDE_SERVER_ADVANCEMENTS_TAG)) {
            return true;
        }
        VisibilityPreference preference = visibilityPreferences.get(player.getUUID());
        return preference != null && preference.hideServerAdvancements();
    }

    private static boolean isGlobalLeaderboardHidden(ServerPlayer player, Map<UUID, VisibilityPreference> visibilityPreferences) {
        if (player.getTags().contains(HIDE_GLOBAL_LEADERBOARD_TAG)) {
            return true;
        }
        VisibilityPreference preference = visibilityPreferences.get(player.getUUID());
        return preference != null && preference.hideGlobalLeaderboard();
    }

    private static int getAdvancementTitleColor(AdvancementType advancementType) {
        Integer color = advancementType.getChatColor().getColor();
        return color == null ? 0xFFFFFF : color;
    }

    private static List<AdvancementHolder> getOrderedAdvancements(
            ServerPlayer player,
            Map<ResourceLocation, AdvancementHolder> allGalacticraftAdvancements,
            Map<ResourceLocation, Integer> depthCache
    ) {
        PlayerAdvancements playerAdvancements = player.getAdvancements();
        List<AdvancementHolder> completed = new ArrayList<>();
        for (AdvancementHolder advancement : allGalacticraftAdvancements.values()) {
            if (playerAdvancements.getOrStartProgress(advancement).isDone()) {
                completed.add(advancement);
            }
        }
        completed.sort(Comparator
                .comparingInt((AdvancementHolder advancement) -> advancementDepth(advancement, allGalacticraftAdvancements, depthCache))
                .reversed()
                .thenComparing(advancement -> advancement.id().toString()));
        return completed;
    }

    private static List<AdvancementHolder> getOrderedAdvancements(
            List<ResourceLocation> completedAdvancements,
            Map<ResourceLocation, AdvancementHolder> allGalacticraftAdvancements,
            Map<ResourceLocation, Integer> depthCache
    ) {
        List<AdvancementHolder> completed = new ArrayList<>(completedAdvancements.size());
        for (ResourceLocation advancementId : completedAdvancements) {
            AdvancementHolder advancement = allGalacticraftAdvancements.get(advancementId);
            if (advancement != null) {
                completed.add(advancement);
            }
        }
        completed.sort(Comparator
                .comparingInt((AdvancementHolder advancement) -> advancementDepth(advancement, allGalacticraftAdvancements, depthCache))
                .reversed()
                .thenComparing(advancement -> advancement.id().toString()));
        return completed;
    }

    private static Map<ResourceLocation, AdvancementHolder> collectGalacticraftAdvancements(ServerAdvancementManager advancementManager, boolean includeRoot) {
        Map<ResourceLocation, AdvancementHolder> allGalacticraftAdvancements = new HashMap<>();
        for (AdvancementHolder advancement : advancementManager.getAllAdvancements()) {
            if (!isGalacticraftAdvancement(advancement.id())) {
                continue;
            }
            if (advancement.value().display().isEmpty()) {
                continue;
            }
            if (!includeRoot && advancement.value().parent().isEmpty()) {
                continue;
            }
            allGalacticraftAdvancements.put(advancement.id(), advancement);
        }
        return allGalacticraftAdvancements;
    }

    private static List<SpaceRaceStatsPayload.AdvancementIconData> toAdvancementIcons(List<AdvancementHolder> orderedAdvancements) {
        List<SpaceRaceStatsPayload.AdvancementIconData> icons = new ArrayList<>(orderedAdvancements.size());
        for (AdvancementHolder advancement : orderedAdvancements) {
            var displayInfo = advancement.value().display().orElse(null);
            if (displayInfo == null) {
                continue;
            }
            icons.add(new SpaceRaceStatsPayload.AdvancementIconData(
                    displayInfo.getIcon().copy(),
                    displayInfo.getTitle().getString(),
                    getAdvancementTitleColor(displayInfo.getType()),
                    displayInfo.getType() == AdvancementType.CHALLENGE,
                    List.of()
            ));
        }
        return icons;
    }

    private static List<SpaceRaceStatsPayload.AdvancementIconData> toAdvancementIcons(
            List<ResourceLocation> orderedAdvancementIds,
            Map<ResourceLocation, AdvancementHolder> advancementLookup
    ) {
        return toAdvancementIcons(orderedAdvancementIds, advancementLookup, Map.of());
    }

    private static List<SpaceRaceStatsPayload.AdvancementIconData> toAdvancementIcons(
            List<ResourceLocation> orderedAdvancementIds,
            Map<ResourceLocation, AdvancementHolder> advancementLookup,
            Map<ResourceLocation, List<String>> firstUnlockersByAdvancement
    ) {
        List<SpaceRaceStatsPayload.AdvancementIconData> icons = new ArrayList<>(orderedAdvancementIds.size());
        for (ResourceLocation advancementId : orderedAdvancementIds) {
            AdvancementHolder advancement = advancementLookup.get(advancementId);
            if (advancement == null) {
                continue;
            }
            var displayInfo = advancement.value().display().orElse(null);
            if (displayInfo == null) {
                continue;
            }
            icons.add(new SpaceRaceStatsPayload.AdvancementIconData(
                    displayInfo.getIcon().copy(),
                    displayInfo.getTitle().getString(),
                    getAdvancementTitleColor(displayInfo.getType()),
                    displayInfo.getType() == AdvancementType.CHALLENGE,
                    firstUnlockersByAdvancement.getOrDefault(advancementId, List.of())
            ));
        }
        return icons;
    }

    private static List<ResourceLocation> toAdvancementIds(List<AdvancementHolder> orderedAdvancements) {
        List<ResourceLocation> ids = new ArrayList<>(orderedAdvancements.size());
        for (AdvancementHolder advancement : orderedAdvancements) {
            ids.add(advancement.id());
        }
        return ids;
    }

    private static Map<ResourceLocation, Long> normalizeCompletionTimes(
            List<ResourceLocation> completedAdvancementIds,
            Map<ResourceLocation, Long> completionTimes
    ) {
        Map<ResourceLocation, Long> normalized = new HashMap<>();
        for (ResourceLocation advancementId : completedAdvancementIds) {
            normalized.put(advancementId, completionTimes.getOrDefault(advancementId, UNKNOWN_COMPLETION_TIME));
        }
        return normalized;
    }

    private static Map<ResourceLocation, Long> loadCompletedAdvancements(Path file) {
        Map<ResourceLocation, Long> completed = new HashMap<>();
        if (!Files.isRegularFile(file)) {
            return completed;
        }

        try (Reader reader = Files.newBufferedReader(file)) {
            JsonElement rootElement = JsonParser.parseReader(reader);
            if (!rootElement.isJsonObject()) {
                return completed;
            }

            JsonObject rootObject = rootElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : rootObject.entrySet()) {
                ResourceLocation advancementId = ResourceLocation.tryParse(entry.getKey());
                if (advancementId == null || !isGalacticraftAdvancement(advancementId)) {
                    continue;
                }
                if (!entry.getValue().isJsonObject()) {
                    continue;
                }

                JsonObject advancementProgress = entry.getValue().getAsJsonObject();
                JsonElement doneElement = advancementProgress.get("done");
                if (doneElement != null
                        && doneElement.isJsonPrimitive()
                        && doneElement.getAsJsonPrimitive().isBoolean()
                        && doneElement.getAsBoolean()) {
                    completed.put(advancementId, extractCompletionTime(advancementProgress));
                }
            }
        } catch (IOException | JsonParseException exception) {
            Constant.LOGGER.warn("Failed to read advancement file {}", file.getFileName(), exception);
        }
        return completed;
    }

    private static long extractCompletionTime(JsonObject advancementProgress) {
        JsonElement criteriaElement = advancementProgress.get("criteria");
        if (criteriaElement == null || !criteriaElement.isJsonObject()) {
            return UNKNOWN_COMPLETION_TIME;
        }

        JsonObject criteria = criteriaElement.getAsJsonObject();
        long latestCriterionTime = Long.MIN_VALUE;
        for (Map.Entry<String, JsonElement> entry : criteria.entrySet()) {
            JsonElement value = entry.getValue();
            if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) {
                continue;
            }
            long parsedTime = parseCompletionTime(value.getAsString());
            if (parsedTime == UNKNOWN_COMPLETION_TIME) {
                continue;
            }
            latestCriterionTime = Math.max(latestCriterionTime, parsedTime);
        }
        if (latestCriterionTime == Long.MIN_VALUE) {
            return UNKNOWN_COMPLETION_TIME;
        }
        return latestCriterionTime;
    }

    private static long parseCompletionTime(String rawTime) {
        try {
            return Instant.parse(rawTime).toEpochMilli();
        } catch (DateTimeParseException ignored) {
        }

        try {
            return OffsetDateTime.parse(rawTime, LEGACY_ADVANCEMENT_TIME_FORMAT).toInstant().toEpochMilli();
        } catch (DateTimeParseException ignored) {
            return UNKNOWN_COMPLETION_TIME;
        }
    }

    private static boolean isGalacticraftAdvancement(ResourceLocation id) {
        return Constant.MOD_ID.equals(id.getNamespace()) || id.getPath().startsWith(Constant.MOD_ID + "/");
    }

    private static int advancementDepth(AdvancementHolder advancement, Map<ResourceLocation, AdvancementHolder> lookup, Map<ResourceLocation, Integer> depthCache) {
        ResourceLocation id = advancement.id();
        Integer cached = depthCache.get(id);
        if (cached != null) {
            return cached;
        }

        int depth = 0;
        Optional<ResourceLocation> parentId = advancement.value().parent();
        if (parentId.isPresent()) {
            AdvancementHolder parent = lookup.get(parentId.get());
            if (parent != null) {
                depth = advancementDepth(parent, lookup, depthCache) + 1;
            }
        }

        depthCache.put(id, depth);
        return depth;
    }

    private static GlobalLeaderboardData buildGlobalLeaderboardData(List<PlayerStatsSource> allPlayers) {
        List<PlayerStatsSource> visiblePlayers = new ArrayList<>();
        for (PlayerStatsSource player : allPlayers) {
            if (!player.hideGlobalLeaderboard()) {
                visiblePlayers.add(player);
            }
        }

        return new GlobalLeaderboardData(
                buildGeneralRows(allPlayers, visiblePlayers),
                buildItemRows(allPlayers, visiblePlayers),
                buildMobRows(allPlayers, visiblePlayers)
        );
    }

    private static List<PlayerStatsSource> collectPlayerStatsSources(MinecraftServer server, List<ServerPlayer> onlinePlayers, Map<UUID, VisibilityPreference> visibilityPreferences) {
        Map<UUID, PlayerStatsSource> playersById = new HashMap<>();
        Path statsDirectory = server.getWorldPath(LevelResource.ROOT).resolve("stats");
        if (Files.isDirectory(statsDirectory)) {
            try (Stream<Path> files = Files.list(statsDirectory)) {
                files.forEach(file -> {
                    String fileName = file.getFileName().toString();
                    if (!fileName.endsWith(".json")) {
                        return;
                    }

                    UUID playerId = tryParseUuid(fileName.substring(0, fileName.length() - 5));
                    if (playerId == null) {
                        return;
                    }

                    OfflineStatBuckets statBuckets = loadOfflineStats(file);
                    VisibilityPreference preference = visibilityPreferences.getOrDefault(playerId, VisibilityPreference.DEFAULT);
                    playersById.put(playerId, PlayerStatsSource.offline(
                            playerId,
                            resolvePlayerName(server, playerId),
                            preference.hideServerAdvancements(),
                            preference.hideGlobalLeaderboard(),
                            statBuckets
                    ));
                });
            } catch (IOException exception) {
                Constant.LOGGER.warn("Failed to list stat files for space race leaderboard", exception);
            }
        }

        for (ServerPlayer player : onlinePlayers) {
            VisibilityPreference preference = visibilityPreferences.getOrDefault(player.getUUID(), VisibilityPreference.DEFAULT);
            boolean hideServerAdvancements = player.getTags().contains(HIDE_SERVER_ADVANCEMENTS_TAG) || preference.hideServerAdvancements();
            boolean hideGlobalLeaderboard = player.getTags().contains(HIDE_GLOBAL_LEADERBOARD_TAG) || preference.hideGlobalLeaderboard();
            playersById.put(player.getUUID(), PlayerStatsSource.online(player, hideServerAdvancements, hideGlobalLeaderboard));
        }

        List<PlayerStatsSource> players = new ArrayList<>(playersById.values());
        players.sort(Comparator.comparing(PlayerStatsSource::playerName, String.CASE_INSENSITIVE_ORDER));
        return players;
    }

    private static String resolvePlayerName(MinecraftServer server, UUID playerId) {
        Optional<GameProfile> profile = server.getProfileCache().get(playerId);
        if (profile.isPresent() && profile.get().getName() != null && !profile.get().getName().isBlank()) {
            return profile.get().getName();
        }
        return playerId.toString().substring(0, 8);
    }

    private static OfflineStatBuckets loadOfflineStats(Path file) {
        OfflineStatBuckets buckets = new OfflineStatBuckets();
        try (Reader reader = Files.newBufferedReader(file)) {
            JsonElement rootElement = JsonParser.parseReader(reader);
            if (!rootElement.isJsonObject()) {
                return buckets;
            }

            JsonObject root = rootElement.getAsJsonObject();
            JsonObject stats = getJsonObject(root, STAT_ROOT_KEY);
            if (stats == null) {
                return buckets;
            }

            readCustomStatCategory(stats, buckets.customStats());
            readStatCategory(stats, BLOCK_MINED_STAT_KEY, buckets.blocksMinedStats());
            readStatCategory(stats, ITEM_CRAFTED_STAT_KEY, buckets.itemCraftedStats());
            readStatCategory(stats, ITEM_USED_STAT_KEY, buckets.itemUsedStats());
            readStatCategory(stats, ITEM_BROKEN_STAT_KEY, buckets.itemBrokenStats());
            readStatCategory(stats, ITEM_PICKED_UP_STAT_KEY, buckets.itemPickedUpStats());
            readStatCategory(stats, ITEM_DROPPED_STAT_KEY, buckets.itemDroppedStats());
            readStatCategory(stats, ENTITY_KILLED_STAT_KEY, buckets.entityKilledStats());
        } catch (IOException | JsonParseException exception) {
            Constant.LOGGER.warn("Failed to read stat file {}", file.getFileName(), exception);
        }
        return buckets;
    }

    private static @Nullable JsonObject getJsonObject(JsonObject root, String key) {
        JsonElement element = root.get(key);
        if (element == null || !element.isJsonObject()) {
            return null;
        }
        return element.getAsJsonObject();
    }

    private static void readCustomStatCategory(JsonObject stats, Map<ResourceLocation, Integer> output) {
        JsonObject categoryObject = getJsonObject(stats, CUSTOM_STAT_KEY);
        if (categoryObject == null) {
            return;
        }

        for (Map.Entry<String, JsonElement> entry : categoryObject.entrySet()) {
            ResourceLocation rawId = ResourceLocation.tryParse(entry.getKey());
            if (rawId == null || !entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isNumber()) {
                continue;
            }

            int value = Math.max(0, entry.getValue().getAsInt());
            ResourceLocation resolvedId = rawId;
            if (BuiltInRegistries.CUSTOM_STAT.containsKey(rawId)) {
                resolvedId = BuiltInRegistries.CUSTOM_STAT.get(rawId);
            }
            output.put(resolvedId, value);
        }
    }

    private static void readStatCategory(JsonObject stats, String category, Map<ResourceLocation, Integer> output) {
        JsonObject categoryObject = getJsonObject(stats, category);
        if (categoryObject == null) {
            return;
        }

        for (Map.Entry<String, JsonElement> entry : categoryObject.entrySet()) {
            ResourceLocation id = ResourceLocation.tryParse(entry.getKey());
            if (id == null || !entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isNumber()) {
                continue;
            }
            output.put(id, Math.max(0, entry.getValue().getAsInt()));
        }
    }

    private static Map<UUID, VisibilityPreference> loadVisibilityPreferences(MinecraftServer server) {
        Map<UUID, VisibilityPreference> preferences = new HashMap<>();
        Path dataPath = server.getWorldPath(LevelResource.ROOT).resolve(VISIBILITY_FILE_NAME);
        if (!Files.exists(dataPath)) {
            return preferences;
        }

        try {
            CompoundTag tag = NbtIo.readCompressed(dataPath, NbtAccounter.unlimitedHeap());
            ListTag entries = tag.getList(VISIBILITY_ENTRIES_KEY, Tag.TAG_COMPOUND);
            for (Tag entry : entries) {
                if (!(entry instanceof CompoundTag compound)) {
                    continue;
                }
                UUID playerId = tryParseUuid(compound.getString(VISIBILITY_UUID_KEY));
                if (playerId == null) {
                    continue;
                }
                preferences.put(playerId, new VisibilityPreference(
                        compound.getBoolean(VISIBILITY_HIDE_SERVER_KEY),
                        compound.getBoolean(VISIBILITY_HIDE_GLOBAL_KEY)
                ));
            }
        } catch (Throwable exception) {
            Constant.LOGGER.warn("Failed to load space race visibility preferences", exception);
        }
        return preferences;
    }

    private static void saveVisibilityPreference(MinecraftServer server, UUID playerId, boolean hideServerAdvancements, boolean hideGlobalLeaderboard) {
        Map<UUID, VisibilityPreference> preferences = loadVisibilityPreferences(server);
        preferences.put(playerId, new VisibilityPreference(hideServerAdvancements, hideGlobalLeaderboard));
        writeVisibilityPreferences(server, preferences);
    }

    private static void writeVisibilityPreferences(MinecraftServer server, Map<UUID, VisibilityPreference> preferences) {
        List<Map.Entry<UUID, VisibilityPreference>> entries = new ArrayList<>(preferences.entrySet());
        entries.sort(Comparator.comparing(entry -> entry.getKey().toString()));

        ListTag list = new ListTag();
        for (Map.Entry<UUID, VisibilityPreference> entry : entries) {
            CompoundTag compound = new CompoundTag();
            compound.putString(VISIBILITY_UUID_KEY, entry.getKey().toString());
            compound.putBoolean(VISIBILITY_HIDE_SERVER_KEY, entry.getValue().hideServerAdvancements());
            compound.putBoolean(VISIBILITY_HIDE_GLOBAL_KEY, entry.getValue().hideGlobalLeaderboard());
            list.add(compound);
        }

        CompoundTag root = new CompoundTag();
        root.put(VISIBILITY_ENTRIES_KEY, list);

        Path dataPath = server.getWorldPath(LevelResource.ROOT).resolve(VISIBILITY_FILE_NAME);
        try {
            NbtIo.writeCompressed(root, dataPath);
        } catch (Throwable exception) {
            Constant.LOGGER.warn("Failed to save space race visibility preferences", exception);
        }
    }

    private static @Nullable UUID tryParseUuid(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    @FunctionalInterface
    private interface StatValueGetter<T> {
        int get(PlayerStatsSource source, T value);
    }

    private static <T> int sumStat(List<PlayerStatsSource> players, T value, StatValueGetter<T> getter) {
        int total = 0;
        for (PlayerStatsSource player : players) {
            total += getter.get(player, value);
        }
        return total;
    }

    private static <T> StatWinner findWinner(List<PlayerStatsSource> players, T value, StatValueGetter<T> getter) {
        PlayerStatsSource winner = null;
        int winnerValue = 0;
        for (PlayerStatsSource player : players) {
            int statValue = getter.get(player, value);
            if (statValue < winnerValue) {
                continue;
            }
            if (statValue == winnerValue && winner != null && player.playerName().compareToIgnoreCase(winner.playerName()) >= 0) {
                continue;
            }
            winner = player;
            winnerValue = statValue;
        }
        return winner == null ? null : new StatWinner(winner, winnerValue);
    }

    private static List<SpaceRaceStatsPayload.GeneralStatRow> buildGeneralRows(List<PlayerStatsSource> allPlayers, List<PlayerStatsSource> visiblePlayers) {
        List<SpaceRaceStatsPayload.GeneralStatRow> rows = new ArrayList<>();
        for (ResourceLocation statId : GCStats.getAllStatIds()) {
            int total = sumStat(allPlayers, statId, PlayerStatsSource::getCustomStat);
            StatWinner winner = findWinner(visiblePlayers, statId, PlayerStatsSource::getCustomStat);
            rows.add(new SpaceRaceStatsPayload.GeneralStatRow(
                    Component.translatable(statId.toLanguageKey("stat")).getString(),
                    total,
                    toLeaderData(winner)
            ));
        }

        rows.sort(Comparator.comparing(SpaceRaceStatsPayload.GeneralStatRow::label, String.CASE_INSENSITIVE_ORDER));
        return List.copyOf(rows);
    }

    private static List<SpaceRaceStatsPayload.ItemStatRow> buildItemRows(List<PlayerStatsSource> allPlayers, List<PlayerStatsSource> visiblePlayers) {
        List<SortableItemRow> sortableRows = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (item == Items.AIR) {
                continue;
            }

            List<SpaceRaceStatsPayload.ItemStatCell> cells = new ArrayList<>(6);

            int minedTotal = 0;
            StatWinner minedWinner = null;
            if (item instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                minedTotal = sumStat(allPlayers, block, PlayerStatsSource::getMinedStat);
                minedWinner = findWinner(visiblePlayers, block, PlayerStatsSource::getMinedStat);
            }
            cells.add(new SpaceRaceStatsPayload.ItemStatCell(minedTotal, toLeaderData(minedWinner)));

            int craftedTotal = sumStat(allPlayers, item, PlayerStatsSource::getCraftedStat);
            StatWinner craftedWinner = findWinner(visiblePlayers, item, PlayerStatsSource::getCraftedStat);
            cells.add(new SpaceRaceStatsPayload.ItemStatCell(craftedTotal, toLeaderData(craftedWinner)));

            int usedTotal = sumStat(allPlayers, item, PlayerStatsSource::getUsedStat);
            StatWinner usedWinner = findWinner(visiblePlayers, item, PlayerStatsSource::getUsedStat);
            cells.add(new SpaceRaceStatsPayload.ItemStatCell(usedTotal, toLeaderData(usedWinner)));

            int brokenTotal = sumStat(allPlayers, item, PlayerStatsSource::getBrokenStat);
            StatWinner brokenWinner = findWinner(visiblePlayers, item, PlayerStatsSource::getBrokenStat);
            cells.add(new SpaceRaceStatsPayload.ItemStatCell(brokenTotal, toLeaderData(brokenWinner)));

            int pickedUpTotal = sumStat(allPlayers, item, PlayerStatsSource::getPickedUpStat);
            StatWinner pickedUpWinner = findWinner(visiblePlayers, item, PlayerStatsSource::getPickedUpStat);
            cells.add(new SpaceRaceStatsPayload.ItemStatCell(pickedUpTotal, toLeaderData(pickedUpWinner)));

            int droppedTotal = sumStat(allPlayers, item, PlayerStatsSource::getDroppedStat);
            StatWinner droppedWinner = findWinner(visiblePlayers, item, PlayerStatsSource::getDroppedStat);
            cells.add(new SpaceRaceStatsPayload.ItemStatCell(droppedTotal, toLeaderData(droppedWinner)));

            boolean hasData = false;
            for (SpaceRaceStatsPayload.ItemStatCell cell : cells) {
                if (cell.total() > 0) {
                    hasData = true;
                    break;
                }
            }
            if (!hasData) {
                continue;
            }

            String label = Component.translatable(item.getDescriptionId()).getString();
            sortableRows.add(new SortableItemRow(label, new SpaceRaceStatsPayload.ItemStatRow(new ItemStack(item), cells)));
        }

        sortableRows.sort(Comparator.comparing(SortableItemRow::label, String.CASE_INSENSITIVE_ORDER));
        List<SpaceRaceStatsPayload.ItemStatRow> rows = new ArrayList<>(sortableRows.size());
        for (SortableItemRow row : sortableRows) {
            rows.add(row.payload());
        }
        return List.copyOf(rows);
    }

    private static List<SpaceRaceStatsPayload.MobStatRow> buildMobRows(List<PlayerStatsSource> allPlayers, List<PlayerStatsSource> visiblePlayers) {
        List<SpaceRaceStatsPayload.MobStatRow> rows = new ArrayList<>();
        for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
            int totalKilled = sumStat(allPlayers, entityType, PlayerStatsSource::getKilledStat);
            if (totalKilled <= 0) {
                continue;
            }

            StatWinner winner = findWinner(visiblePlayers, entityType, PlayerStatsSource::getKilledStat);
            rows.add(new SpaceRaceStatsPayload.MobStatRow(
                    entityType.getDescription().getString(),
                    totalKilled,
                    toLeaderData(winner)
            ));
        }
        rows.sort(Comparator.comparing(SpaceRaceStatsPayload.MobStatRow::mobName, String.CASE_INSENSITIVE_ORDER));
        return List.copyOf(rows);
    }

    private static SpaceRaceStatsPayload.LeaderData toLeaderData(StatWinner winner) {
        if (winner == null || winner.value() <= 0) {
            return null;
        }
        return new SpaceRaceStatsPayload.LeaderData(
                winner.player().playerId(),
                winner.player().playerName(),
                winner.value()
        );
    }

    private static void setVisibilityTag(ServerPlayer player, String tag, boolean hidden) {
        if (hidden) {
            player.addTag(tag);
            return;
        }
        player.removeTag(tag);
    }

    private record GlobalLeaderboardData(
            List<SpaceRaceStatsPayload.GeneralStatRow> generalRows,
            List<SpaceRaceStatsPayload.ItemStatRow> itemRows,
            List<SpaceRaceStatsPayload.MobStatRow> mobRows
    ) {
    }

    private record StatWinner(PlayerStatsSource player, int value) {
    }

    private record SortableItemRow(String label, SpaceRaceStatsPayload.ItemStatRow payload) {
    }

    private record PlayerAdvancementSource(
            UUID playerId,
            String playerName,
            boolean hiddenFromReceiver,
            List<ResourceLocation> completedAdvancementIds,
            Map<ResourceLocation, Long> completionTimesByAdvancement
    ) {
        private PlayerAdvancementSource {
            completedAdvancementIds = List.copyOf(completedAdvancementIds);
            completionTimesByAdvancement = Map.copyOf(completionTimesByAdvancement);
        }
    }

    private record TeamDescriptor(String teamId, String teamName, ItemStack flagIcon) {
    }

    private record TeamAdvancementAccumulator(
            String teamId,
            String teamName,
            ItemStack flagIcon,
            Set<ResourceLocation> advancementIds,
            List<PlayerAdvancementSource> members
    ) {
        private TeamAdvancementAccumulator(String teamId, String teamName, ItemStack flagIcon) {
            this(teamId, teamName, flagIcon, new HashSet<>(), new ArrayList<>());
        }
    }

    private record UnlockerCandidate(UUID playerId, String playerName, long completionTime) {
    }

    private record VisibilityPreference(boolean hideServerAdvancements, boolean hideGlobalLeaderboard) {
        private static final VisibilityPreference DEFAULT = new VisibilityPreference(false, false);
    }

    private static class OfflineStatBuckets {
        private final Map<ResourceLocation, Integer> customStats = new HashMap<>();
        private final Map<ResourceLocation, Integer> blocksMinedStats = new HashMap<>();
        private final Map<ResourceLocation, Integer> itemCraftedStats = new HashMap<>();
        private final Map<ResourceLocation, Integer> itemUsedStats = new HashMap<>();
        private final Map<ResourceLocation, Integer> itemBrokenStats = new HashMap<>();
        private final Map<ResourceLocation, Integer> itemPickedUpStats = new HashMap<>();
        private final Map<ResourceLocation, Integer> itemDroppedStats = new HashMap<>();
        private final Map<ResourceLocation, Integer> entityKilledStats = new HashMap<>();

        public Map<ResourceLocation, Integer> customStats() {
            return this.customStats;
        }

        public Map<ResourceLocation, Integer> blocksMinedStats() {
            return this.blocksMinedStats;
        }

        public Map<ResourceLocation, Integer> itemCraftedStats() {
            return this.itemCraftedStats;
        }

        public Map<ResourceLocation, Integer> itemUsedStats() {
            return this.itemUsedStats;
        }

        public Map<ResourceLocation, Integer> itemBrokenStats() {
            return this.itemBrokenStats;
        }

        public Map<ResourceLocation, Integer> itemPickedUpStats() {
            return this.itemPickedUpStats;
        }

        public Map<ResourceLocation, Integer> itemDroppedStats() {
            return this.itemDroppedStats;
        }

        public Map<ResourceLocation, Integer> entityKilledStats() {
            return this.entityKilledStats;
        }
    }

    private static class PlayerStatsSource {
        private final UUID playerId;
        private final String playerName;
        private final boolean hideServerAdvancements;
        private final boolean hideGlobalLeaderboard;
        private final @Nullable ServerPlayer onlinePlayer;
        private final OfflineStatBuckets offlineStats;

        private PlayerStatsSource(UUID playerId, String playerName, boolean hideServerAdvancements, boolean hideGlobalLeaderboard, @Nullable ServerPlayer onlinePlayer, OfflineStatBuckets offlineStats) {
            this.playerId = playerId;
            this.playerName = playerName;
            this.hideServerAdvancements = hideServerAdvancements;
            this.hideGlobalLeaderboard = hideGlobalLeaderboard;
            this.onlinePlayer = onlinePlayer;
            this.offlineStats = offlineStats;
        }

        public static PlayerStatsSource offline(UUID playerId, String playerName, boolean hideServerAdvancements, boolean hideGlobalLeaderboard, OfflineStatBuckets offlineStats) {
            return new PlayerStatsSource(playerId, playerName, hideServerAdvancements, hideGlobalLeaderboard, null, offlineStats);
        }

        public static PlayerStatsSource online(ServerPlayer player, boolean hideServerAdvancements, boolean hideGlobalLeaderboard) {
            return new PlayerStatsSource(player.getUUID(), player.getGameProfile().getName(), hideServerAdvancements, hideGlobalLeaderboard, player, new OfflineStatBuckets());
        }

        public UUID playerId() {
            return this.playerId;
        }

        public String playerName() {
            return this.playerName;
        }

        public boolean hideGlobalLeaderboard() {
            return this.hideGlobalLeaderboard;
        }

        public int getCustomStat(ResourceLocation statId) {
            if (this.onlinePlayer != null) {
                return this.onlinePlayer.getStats().getValue(Stats.CUSTOM, statId);
            }
            int value = this.offlineStats.customStats().getOrDefault(statId, 0);
            if (value > 0) {
                return value;
            }

            // Vanilla stat files persist custom stats by registry key (often minecraft:*),
            // but the in-game stat object uses the registry value (our galacticraft:* id).
            ResourceLocation registryKey = BuiltInRegistries.CUSTOM_STAT.getKey(statId);
            if (registryKey != null && !registryKey.equals(statId)) {
                return this.offlineStats.customStats().getOrDefault(registryKey, 0);
            }
            return 0;
        }

        public int getMinedStat(Block block) {
            if (this.onlinePlayer != null) {
                return this.onlinePlayer.getStats().getValue(Stats.BLOCK_MINED, block);
            }
            ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
            return this.offlineStats.blocksMinedStats().getOrDefault(blockId, 0);
        }

        public int getCraftedStat(Item item) {
            if (this.onlinePlayer != null) {
                return this.onlinePlayer.getStats().getValue(Stats.ITEM_CRAFTED, item);
            }
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            return this.offlineStats.itemCraftedStats().getOrDefault(itemId, 0);
        }

        public int getUsedStat(Item item) {
            if (this.onlinePlayer != null) {
                return this.onlinePlayer.getStats().getValue(Stats.ITEM_USED, item);
            }
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            return this.offlineStats.itemUsedStats().getOrDefault(itemId, 0);
        }

        public int getBrokenStat(Item item) {
            if (this.onlinePlayer != null) {
                return this.onlinePlayer.getStats().getValue(Stats.ITEM_BROKEN, item);
            }
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            return this.offlineStats.itemBrokenStats().getOrDefault(itemId, 0);
        }

        public int getPickedUpStat(Item item) {
            if (this.onlinePlayer != null) {
                return this.onlinePlayer.getStats().getValue(Stats.ITEM_PICKED_UP, item);
            }
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            return this.offlineStats.itemPickedUpStats().getOrDefault(itemId, 0);
        }

        public int getDroppedStat(Item item) {
            if (this.onlinePlayer != null) {
                return this.onlinePlayer.getStats().getValue(Stats.ITEM_DROPPED, item);
            }
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            return this.offlineStats.itemDroppedStats().getOrDefault(itemId, 0);
        }

        public int getKilledStat(EntityType<?> entityType) {
            if (this.onlinePlayer != null) {
                return this.onlinePlayer.getStats().getValue(Stats.ENTITY_KILLED, entityType);
            }
            ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
            return this.offlineStats.entityKilledStats().getOrDefault(entityId, 0);
        }
    }
}
