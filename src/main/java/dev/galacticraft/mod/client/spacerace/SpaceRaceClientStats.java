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

package dev.galacticraft.mod.client.spacerace;

import dev.galacticraft.impl.network.s2c.SpaceRaceStatsPayload;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class SpaceRaceClientStats {
    private static final List<PlayerStatsEntry> SERVER_STATS = new ArrayList<>();
    private static final Map<UUID, PlayerStatsEntry> SERVER_STATS_BY_PLAYER = new HashMap<>();
    private static final List<TeamStatsEntry> TEAM_STATS = new ArrayList<>();
    private static final Map<String, TeamStatsEntry> TEAM_STATS_BY_ID = new HashMap<>();
    private static final List<ServerAdvancementNode> SERVER_ADVANCEMENT_NODES = new ArrayList<>();
    private static final List<GeneralStatRow> GENERAL_STAT_ROWS = new ArrayList<>();
    private static final List<ItemStatRow> ITEM_STAT_ROWS = new ArrayList<>();
    private static final List<MobStatRow> MOB_STAT_ROWS = new ArrayList<>();
    private static boolean hideServerAdvancements;
    private static boolean hideGlobalLeaderboard;

    private SpaceRaceClientStats() {
    }

    public static synchronized void apply(SpaceRaceStatsPayload payload) {
        SERVER_STATS.clear();
        SERVER_STATS_BY_PLAYER.clear();
        TEAM_STATS.clear();
        TEAM_STATS_BY_ID.clear();
        SERVER_ADVANCEMENT_NODES.clear();
        GENERAL_STAT_ROWS.clear();
        ITEM_STAT_ROWS.clear();
        MOB_STAT_ROWS.clear();

        hideServerAdvancements = payload.hideServerAdvancements();
        hideGlobalLeaderboard = payload.hideGlobalLeaderboard();

        for (SpaceRaceStatsPayload.PlayerStatsEntry entry : payload.entries()) {
            List<AdvancementIconData> icons = new ArrayList<>(entry.advancementIcons().size());
            for (SpaceRaceStatsPayload.AdvancementIconData advancementIcon : entry.advancementIcons()) {
                icons.add(new AdvancementIconData(
                        advancementIcon.icon().copy(),
                        advancementIcon.title(),
                        advancementIcon.color(),
                        advancementIcon.challenge(),
                        List.copyOf(advancementIcon.firstUnlockers())
                ));
            }
            PlayerStatsEntry copiedEntry = new PlayerStatsEntry(entry.playerId(), entry.playerName(), Collections.unmodifiableList(icons));
            SERVER_STATS.add(copiedEntry);
            SERVER_STATS_BY_PLAYER.put(entry.playerId(), copiedEntry);
        }

        for (SpaceRaceStatsPayload.TeamStatsEntry entry : payload.teamEntries()) {
            List<AdvancementIconData> icons = new ArrayList<>(entry.advancementIcons().size());
            for (SpaceRaceStatsPayload.AdvancementIconData advancementIcon : entry.advancementIcons()) {
                icons.add(new AdvancementIconData(
                        advancementIcon.icon().copy(),
                        advancementIcon.title(),
                        advancementIcon.color(),
                        advancementIcon.challenge(),
                        List.copyOf(advancementIcon.firstUnlockers())
                ));
            }
            List<TeamMemberEntry> members = new ArrayList<>(entry.members().size());
            for (SpaceRaceStatsPayload.TeamMemberEntry member : entry.members()) {
                List<AdvancementIconData> memberIcons = new ArrayList<>(member.advancementIcons().size());
                for (SpaceRaceStatsPayload.AdvancementIconData memberIcon : member.advancementIcons()) {
                    memberIcons.add(new AdvancementIconData(
                            memberIcon.icon().copy(),
                            memberIcon.title(),
                            memberIcon.color(),
                            memberIcon.challenge(),
                            List.copyOf(memberIcon.firstUnlockers())
                    ));
                }
                members.add(new TeamMemberEntry(member.playerId(), member.playerName(), List.copyOf(memberIcons)));
            }
            TeamStatsEntry copiedEntry = new TeamStatsEntry(
                    entry.teamId(),
                    entry.teamName(),
                    entry.teamFlag().copy(),
                    List.copyOf(icons),
                    List.copyOf(members)
            );
            TEAM_STATS.add(copiedEntry);
            TEAM_STATS_BY_ID.put(entry.teamId(), copiedEntry);
        }

        for (SpaceRaceStatsPayload.ServerAdvancementNode node : payload.serverAdvancementNodes()) {
            SERVER_ADVANCEMENT_NODES.add(new ServerAdvancementNode(
                    node.advancementId(),
                    node.parentId(),
                    node.icon().copy(),
                    node.title(),
                    node.color(),
                    node.complete(),
                    node.x(),
                    node.y(),
                    List.copyOf(node.firstUnlockers())
            ));
        }

        for (SpaceRaceStatsPayload.GeneralStatRow row : payload.generalRows()) {
            GENERAL_STAT_ROWS.add(new GeneralStatRow(row.label(), row.total(), copyLeader(row.leader())));
        }

        for (SpaceRaceStatsPayload.ItemStatRow row : payload.itemRows()) {
            List<ItemStatCell> cells = new ArrayList<>(row.cells().size());
            for (SpaceRaceStatsPayload.ItemStatCell cell : row.cells()) {
                cells.add(new ItemStatCell(cell.total(), copyLeader(cell.leader())));
            }
            ITEM_STAT_ROWS.add(new ItemStatRow(row.icon().copy(), List.copyOf(cells)));
        }

        for (SpaceRaceStatsPayload.MobStatRow row : payload.mobRows()) {
            MOB_STAT_ROWS.add(new MobStatRow(row.mobName(), row.totalKilled(), copyLeader(row.leader())));
        }
    }

    private static LeaderCell copyLeader(SpaceRaceStatsPayload.LeaderData leader) {
        if (leader == null) {
            return null;
        }
        return new LeaderCell(leader.playerId(), leader.playerName(), leader.value());
    }

    public static synchronized List<AdvancementIconData> getServerStats(UUID playerId) {
        if (playerId == null) {
            return List.of();
        }
        PlayerStatsEntry entry = SERVER_STATS_BY_PLAYER.get(playerId);
        if (entry == null) {
            return List.of();
        }
        return entry.advancementIcons();
    }

    public static synchronized List<PlayerStatsEntry> getServerStatsEntries() {
        return List.copyOf(SERVER_STATS);
    }

    public static synchronized PlayerStatsEntry getServerStatsEntry(UUID playerId) {
        return SERVER_STATS_BY_PLAYER.get(playerId);
    }

    public static synchronized List<TeamStatsEntry> getTeamStatsEntries() {
        return List.copyOf(TEAM_STATS);
    }

    public static synchronized TeamStatsEntry getTeamStatsEntry(String teamId) {
        if (teamId == null) {
            return null;
        }
        return TEAM_STATS_BY_ID.get(teamId);
    }

    public static synchronized List<ServerAdvancementNode> getServerAdvancementNodes() {
        return List.copyOf(SERVER_ADVANCEMENT_NODES);
    }

    public static synchronized List<GeneralStatRow> getGeneralStatRows() {
        return List.copyOf(GENERAL_STAT_ROWS);
    }

    public static synchronized List<ItemStatRow> getItemStatRows() {
        return List.copyOf(ITEM_STAT_ROWS);
    }

    public static synchronized List<MobStatRow> getMobStatRows() {
        return List.copyOf(MOB_STAT_ROWS);
    }

    public static synchronized boolean isHideServerAdvancements() {
        return hideServerAdvancements;
    }

    public static synchronized boolean isHideGlobalLeaderboard() {
        return hideGlobalLeaderboard;
    }

    public static synchronized void setVisibility(boolean hideServerAdvancementsValue, boolean hideGlobalLeaderboardValue) {
        hideServerAdvancements = hideServerAdvancementsValue;
        hideGlobalLeaderboard = hideGlobalLeaderboardValue;
    }

    public record PlayerStatsEntry(UUID playerId, String playerName, List<AdvancementIconData> advancementIcons) {
    }

    public record AdvancementIconData(ItemStack icon, String title, int color, boolean challenge, List<String> firstUnlockers) {
    }

    public record TeamStatsEntry(
            String teamId,
            String teamName,
            ItemStack teamFlag,
            List<AdvancementIconData> advancementIcons,
            List<TeamMemberEntry> members
    ) {
    }

    public record TeamMemberEntry(UUID playerId, String playerName, List<AdvancementIconData> advancementIcons) {
    }

    public record ServerAdvancementNode(
            net.minecraft.resources.ResourceLocation advancementId,
            @Nullable net.minecraft.resources.ResourceLocation parentId,
            ItemStack icon,
            String title,
            int color,
            boolean complete,
            float x,
            float y,
            List<String> firstUnlockers
    ) {
    }

    public record LeaderCell(UUID playerId, String playerName, int value) {
    }

    public record GeneralStatRow(String label, int total, LeaderCell leader) {
    }

    public record ItemStatRow(ItemStack icon, List<ItemStatCell> cells) {
    }

    public record ItemStatCell(int total, LeaderCell leader) {
    }

    public record MobStatRow(String mobName, int totalKilled, LeaderCell leader) {
    }
}
