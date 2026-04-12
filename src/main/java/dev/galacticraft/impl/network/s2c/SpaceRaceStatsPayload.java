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

package dev.galacticraft.impl.network.s2c;

import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.client.spacerace.SpaceRaceClientStats;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record SpaceRaceStatsPayload(
        List<PlayerStatsEntry> entries,
        List<TeamStatsEntry> teamEntries,
        List<ServerAdvancementNode> serverAdvancementNodes,
        List<GeneralStatRow> generalRows,
        List<ItemStatRow> itemRows,
        List<MobStatRow> mobRows,
        boolean hideServerAdvancements,
        boolean hideGlobalLeaderboard
) implements S2CPayload {
    public static final ResourceLocation ID = Constant.id("space_race_stats");
    public static final Type<SpaceRaceStatsPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SpaceRaceStatsPayload> CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeBoolean(payload.hideServerAdvancements);
                buf.writeBoolean(payload.hideGlobalLeaderboard);

                VarInt.write(buf, payload.entries.size());
                for (PlayerStatsEntry entry : payload.entries) {
                    UUIDUtil.STREAM_CODEC.encode(buf, entry.playerId);
                    buf.writeUtf(entry.playerName, 64);
                    writeAdvancementIcons(buf, entry.advancementIcons);
                }

                VarInt.write(buf, payload.teamEntries.size());
                for (TeamStatsEntry entry : payload.teamEntries) {
                    buf.writeUtf(entry.teamId, 96);
                    buf.writeUtf(entry.teamName, 96);
                    ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, entry.teamFlag);
                    writeAdvancementIcons(buf, entry.advancementIcons);
                    VarInt.write(buf, entry.members.size());
                    for (TeamMemberEntry member : entry.members) {
                        UUIDUtil.STREAM_CODEC.encode(buf, member.playerId);
                        buf.writeUtf(member.playerName, 64);
                        writeAdvancementIcons(buf, member.advancementIcons);
                    }
                }

                VarInt.write(buf, payload.serverAdvancementNodes.size());
                for (ServerAdvancementNode node : payload.serverAdvancementNodes) {
                    writeResourceLocation(buf, node.advancementId);
                    buf.writeBoolean(node.parentId != null);
                    if (node.parentId != null) {
                        writeResourceLocation(buf, node.parentId);
                    }
                    ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, node.icon);
                    buf.writeUtf(node.title, 256);
                    buf.writeInt(node.color);
                    buf.writeBoolean(node.complete);
                    buf.writeFloat(node.x);
                    buf.writeFloat(node.y);
                    writeStringList(buf, node.firstUnlockers);
                }

                VarInt.write(buf, payload.generalRows.size());
                for (GeneralStatRow row : payload.generalRows) {
                    buf.writeUtf(row.label, 160);
                    VarInt.write(buf, row.total);
                    writeLeader(buf, row.leader);
                }

                VarInt.write(buf, payload.itemRows.size());
                for (ItemStatRow row : payload.itemRows) {
                    ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, row.icon);
                    VarInt.write(buf, row.cells.size());
                    for (ItemStatCell cell : row.cells) {
                        VarInt.write(buf, cell.total);
                        writeLeader(buf, cell.leader);
                    }
                }

                VarInt.write(buf, payload.mobRows.size());
                for (MobStatRow row : payload.mobRows) {
                    buf.writeUtf(row.mobName, 160);
                    VarInt.write(buf, row.totalKilled);
                    writeLeader(buf, row.leader);
                }
            },
            buf -> {
                boolean hideServerAdvancements = buf.readBoolean();
                boolean hideGlobalLeaderboard = buf.readBoolean();

                int playerCount = VarInt.read(buf);
                List<PlayerStatsEntry> entries = new ArrayList<>(playerCount);
                for (int i = 0; i < playerCount; i++) {
                    UUID playerId = UUIDUtil.STREAM_CODEC.decode(buf);
                    String playerName = buf.readUtf(64);
                    entries.add(new PlayerStatsEntry(playerId, playerName, readAdvancementIcons(buf)));
                }

                int teamCount = VarInt.read(buf);
                List<TeamStatsEntry> teamEntries = new ArrayList<>(teamCount);
                for (int i = 0; i < teamCount; i++) {
                    String teamId = buf.readUtf(96);
                    String teamName = buf.readUtf(96);
                    ItemStack teamFlag = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
                    List<AdvancementIconData> advancementIcons = readAdvancementIcons(buf);
                    int memberCount = VarInt.read(buf);
                    List<TeamMemberEntry> members = new ArrayList<>(memberCount);
                    for (int j = 0; j < memberCount; j++) {
                        members.add(new TeamMemberEntry(
                                UUIDUtil.STREAM_CODEC.decode(buf),
                                buf.readUtf(64),
                                readAdvancementIcons(buf)
                        ));
                    }
                    teamEntries.add(new TeamStatsEntry(
                            teamId,
                            teamName,
                            teamFlag,
                            advancementIcons,
                            members
                    ));
                }

                int serverNodeCount = VarInt.read(buf);
                List<ServerAdvancementNode> serverAdvancementNodes = new ArrayList<>(serverNodeCount);
                for (int i = 0; i < serverNodeCount; i++) {
                    ResourceLocation advancementId = readResourceLocation(buf);
                    ResourceLocation parentId = null;
                    if (buf.readBoolean()) {
                        parentId = readResourceLocation(buf);
                    }
                    serverAdvancementNodes.add(new ServerAdvancementNode(
                            advancementId,
                            parentId,
                            ItemStack.OPTIONAL_STREAM_CODEC.decode(buf),
                            buf.readUtf(256),
                            buf.readInt(),
                            buf.readBoolean(),
                            buf.readFloat(),
                            buf.readFloat(),
                            readStringList(buf)
                    ));
                }

                int generalCount = VarInt.read(buf);
                List<GeneralStatRow> generalRows = new ArrayList<>(generalCount);
                for (int i = 0; i < generalCount; i++) {
                    generalRows.add(new GeneralStatRow(
                            buf.readUtf(160),
                            VarInt.read(buf),
                            readLeader(buf)
                    ));
                }

                int itemCount = VarInt.read(buf);
                List<ItemStatRow> itemRows = new ArrayList<>(itemCount);
                for (int i = 0; i < itemCount; i++) {
                    ItemStack icon = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
                    int cellCount = VarInt.read(buf);
                    List<ItemStatCell> cells = new ArrayList<>(cellCount);
                    for (int j = 0; j < cellCount; j++) {
                        cells.add(new ItemStatCell(VarInt.read(buf), readLeader(buf)));
                    }
                    itemRows.add(new ItemStatRow(icon, cells));
                }

                int mobCount = VarInt.read(buf);
                List<MobStatRow> mobRows = new ArrayList<>(mobCount);
                for (int i = 0; i < mobCount; i++) {
                    mobRows.add(new MobStatRow(
                            buf.readUtf(160),
                            VarInt.read(buf),
                            readLeader(buf)
                    ));
                }

                return new SpaceRaceStatsPayload(entries, teamEntries, serverAdvancementNodes, generalRows, itemRows, mobRows, hideServerAdvancements, hideGlobalLeaderboard);
            }
    );

    private static void writeAdvancementIcons(RegistryFriendlyByteBuf buf, List<AdvancementIconData> icons) {
        VarInt.write(buf, icons.size());
        for (AdvancementIconData advancementIcon : icons) {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, advancementIcon.icon);
            buf.writeUtf(advancementIcon.title, 256);
            buf.writeInt(advancementIcon.color);
            buf.writeBoolean(advancementIcon.challenge);
            writeStringList(buf, advancementIcon.firstUnlockers);
        }
    }

    private static List<AdvancementIconData> readAdvancementIcons(RegistryFriendlyByteBuf buf) {
        int iconCount = VarInt.read(buf);
        List<AdvancementIconData> icons = new ArrayList<>(iconCount);
        for (int j = 0; j < iconCount; j++) {
            icons.add(new AdvancementIconData(
                    ItemStack.OPTIONAL_STREAM_CODEC.decode(buf),
                    buf.readUtf(256),
                    buf.readInt(),
                    buf.readBoolean(),
                    readStringList(buf)
            ));
        }
        return icons;
    }

    private static void writeStringList(RegistryFriendlyByteBuf buf, List<String> values) {
        VarInt.write(buf, values.size());
        for (String value : values) {
            buf.writeUtf(value, 64);
        }
    }

    private static List<String> readStringList(RegistryFriendlyByteBuf buf) {
        int count = VarInt.read(buf);
        List<String> values = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            values.add(buf.readUtf(64));
        }
        return values;
    }

    private static void writeResourceLocation(RegistryFriendlyByteBuf buf, ResourceLocation id) {
        buf.writeUtf(id.toString(), 160);
    }

    private static ResourceLocation readResourceLocation(RegistryFriendlyByteBuf buf) {
        return ResourceLocation.parse(buf.readUtf(160));
    }

    private static void writeLeader(RegistryFriendlyByteBuf buf, @Nullable LeaderData leader) {
        buf.writeBoolean(leader != null);
        if (leader == null) {
            return;
        }
        UUIDUtil.STREAM_CODEC.encode(buf, leader.playerId);
        buf.writeUtf(leader.playerName, 64);
        VarInt.write(buf, leader.value);
    }

    private static @Nullable LeaderData readLeader(RegistryFriendlyByteBuf buf) {
        if (!buf.readBoolean()) {
            return null;
        }
        UUID playerId = UUIDUtil.STREAM_CODEC.decode(buf);
        String playerName = buf.readUtf(64);
        int value = VarInt.read(buf);
        return new LeaderData(playerId, playerName, value);
    }

    public SpaceRaceStatsPayload {
        entries = List.copyOf(entries);
        teamEntries = List.copyOf(teamEntries);
        serverAdvancementNodes = List.copyOf(serverAdvancementNodes);
        generalRows = List.copyOf(generalRows);
        itemRows = List.copyOf(itemRows);
        mobRows = List.copyOf(mobRows);
    }

    public record PlayerStatsEntry(UUID playerId, String playerName, List<AdvancementIconData> advancementIcons) {
        public PlayerStatsEntry {
            advancementIcons = List.copyOf(advancementIcons);
        }
    }

    public record AdvancementIconData(ItemStack icon, String title, int color, boolean challenge, List<String> firstUnlockers) {
        public AdvancementIconData {
            firstUnlockers = List.copyOf(firstUnlockers);
        }
    }

    public record TeamStatsEntry(String teamId, String teamName, ItemStack teamFlag, List<AdvancementIconData> advancementIcons, List<TeamMemberEntry> members) {
        public TeamStatsEntry {
            advancementIcons = List.copyOf(advancementIcons);
            members = List.copyOf(members);
        }
    }

    public record TeamMemberEntry(UUID playerId, String playerName, List<AdvancementIconData> advancementIcons) {
        public TeamMemberEntry {
            advancementIcons = List.copyOf(advancementIcons);
        }
    }

    public record ServerAdvancementNode(
            ResourceLocation advancementId,
            @Nullable ResourceLocation parentId,
            ItemStack icon,
            String title,
            int color,
            boolean complete,
            float x,
            float y,
            List<String> firstUnlockers
    ) {
        public ServerAdvancementNode {
            firstUnlockers = List.copyOf(firstUnlockers);
        }
    }

    public record LeaderData(UUID playerId, String playerName, int value) {
    }

    public record GeneralStatRow(String label, int total, @Nullable LeaderData leader) {
    }

    public record ItemStatRow(ItemStack icon, List<ItemStatCell> cells) {
        public ItemStatRow {
            cells = List.copyOf(cells);
        }
    }

    public record ItemStatCell(int total, @Nullable LeaderData leader) {
    }

    public record MobStatRow(String mobName, int totalKilled, @Nullable LeaderData leader) {
    }

    @Override
    public Runnable handle(ClientPlayNetworking.@NotNull Context context) {
        return () -> SpaceRaceClientStats.apply(this);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
