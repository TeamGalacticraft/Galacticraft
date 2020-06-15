package com.hrznstudio.galacticraft.api.research;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.rocket.RocketPart;
import com.hrznstudio.galacticraft.server.ServerResearchLoader;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.Unpooled;
import net.minecraft.SharedConstants;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerResearchTracker extends PlayerAdvancementTracker {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(AdvancementProgress.class, new AdvancementProgress.Serializer()).registerTypeAdapter(Identifier.class, new Identifier.Serializer()).setPrettyPrinting().create();
    private static final TypeToken<Map<Identifier, AdvancementProgress>> JSON_TYPE = new TypeToken<Map<Identifier, AdvancementProgress>>() {
    };
    private final File researchFile;
    private final Map<ResearchNode, AdvancementProgress> researchToProgress = new LinkedHashMap<>();
    private final Set<ResearchNode> visibleResearch = new LinkedHashSet<>();
    private final Set<ResearchNode> visibilityUpdates = new LinkedHashSet<>();
    private final Set<ResearchNode> progressUpdates = new LinkedHashSet<>();
    private final Set<RocketPart> unlockedParts = new LinkedHashSet<>();
    private ServerPlayerEntity owner;
    private final ServerResearchLoader researchLoader;
    private boolean dirty = true;

    public PlayerResearchTracker(DataFixer dataFixer, PlayerManager playerManager, ServerResearchLoader researchLoader, File file, ServerPlayerEntity serverPlayerEntity) {
        //noinspection ConstantConditions
        super(dataFixer, playerManager, null, new File(""), serverPlayerEntity);
        this.researchFile = file;
        this.researchLoader = researchLoader;
        this.load();
    }

    public ServerResearchLoader getResearchLoader() {
        return researchLoader;
    }

    @Override
    public void setOwner(ServerPlayerEntity owner) {
        this.owner = owner;
    }

    @Override
    public void clearCriteria() {
        for (Criterion<?> criterion : Criteria.getCriteria()) {
            criterion.endTracking(this);
        }

    }

    public void reload() {
        this.clearCriteria();
        this.researchToProgress.clear();
        this.visibleResearch.clear();
        this.visibilityUpdates.clear();
        this.progressUpdates.clear();
        this.dirty = true;
        this.load();
    }

    private void beginTrackingAllResearch() {

        for (ResearchNode node : researchLoader.getManager().getResearch().values()) {
            this.beginTracking(node);
        }

    }

    private void updateCompleted() {
        List<ResearchNode> list = new ArrayList<>();
        for (Map.Entry<ResearchNode, AdvancementProgress> entry : this.researchToProgress.entrySet()) {
            if ((entry.getValue()).isDone()) {
                list.add(entry.getKey());
                this.progressUpdates.add(entry.getKey());
            }
        }

        for (ResearchNode node : list) {
            this.updateDisplay(node);
        }
    }

    private void rewardEmptyResearch() {
        for (ResearchNode node : researchLoader.getManager().getResearch().values()) {
            if (node.getCriteria().isEmpty()) {
                this.grantCriterion(node, "");
                node.getRewards().apply(this.owner);
            }
        }

    }

    private void load() {
        if (this.researchFile.isFile()) {
            try {
                JsonReader jsonReader = new JsonReader(new StringReader(Files.toString(this.researchFile, StandardCharsets.UTF_8)));
                Throwable var2 = null;

                try {
                    jsonReader.setLenient(false);
                    Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, Streams.parse(jsonReader));
//               if (!dynamic.get("DataVersion").asNumber().isPresent()) {
//                  dynamic = dynamic.set("DataVersion", dynamic.createInt(1343));
//               }
//
//               dynamic = this.server.getDataFixer().update(DataFixTypes.ADVANCEMENTS.getTypeReference(), dynamic, dynamic.get("DataVersion").asInt(0), SharedConstants.getGameVersion().getWorldVersion());
//               dynamic = dynamic.remove("DataVersion");
                    Map<Identifier, AdvancementProgress> map = GSON.getAdapter(JSON_TYPE).fromJsonTree(dynamic.getValue());
                    if (map == null) {
                        throw new JsonParseException("Found null for research");
                    }

                    Stream<Entry<Identifier, AdvancementProgress>> stream = map.entrySet().stream().sorted(Entry.comparingByValue());

                    for (Entry<Identifier, AdvancementProgress> progress : stream.collect(Collectors.toList())) {
                        ResearchNode node = researchLoader.getManager().getResearch().get(progress.getKey());
                        if (node == null) {
                            LOGGER.warn("Ignored research node '{}' in progress file {} - it doesn't exist anymore?", progress.getKey(), this.researchFile);
                        } else {
                            this.initProgress(node, progress.getValue());
                        }
                    }
                } catch (Throwable var18) {
                    var2 = var18;
                    throw var18;
                } finally {
                    if (var2 != null) {
                        try {
                            jsonReader.close();
                        } catch (Throwable var17) {
                            var2.addSuppressed(var17);
                        }
                    } else {
                        jsonReader.close();
                    }

                }
            } catch (JsonParseException var20) {
                LOGGER.error("Couldn't parse player research in {}", this.researchFile, var20);
            } catch (IOException var21) {
                LOGGER.error("Couldn't access player research in {}", this.researchFile, var21);
            }
        }

        this.rewardEmptyResearch();
        this.updateCompleted();
        this.beginTrackingAllResearch();
    }

    public void save() {
        Map<Identifier, AdvancementProgress> map = Maps.newHashMap();

        for (Entry<ResearchNode, AdvancementProgress> entry : this.researchToProgress.entrySet()) {
            AdvancementProgress progress = entry.getValue();
            if (progress.isAnyObtained()) {
                map.put(entry.getKey().getId(), progress);
            }
        }

        if (this.researchFile.getParentFile() != null) {
            this.researchFile.getParentFile().mkdirs();
        }

        JsonElement jsonElement = GSON.toJsonTree(map);
        jsonElement.getAsJsonObject().addProperty("DataVersion", SharedConstants.getGameVersion().getWorldVersion());

        try {
            OutputStream outputStream = new FileOutputStream(this.researchFile);
            Throwable var38 = null;

            try {
                Writer writer = new OutputStreamWriter(outputStream, Charsets.UTF_8.newEncoder());
                Throwable var6 = null;

                try {
                    GSON.toJson(jsonElement, writer);
                } catch (Throwable var31) {
                    var6 = var31;
                    throw var31;
                } finally {
                    if (var6 != null) {
                        try {
                            writer.close();
                        } catch (Throwable var30) {
                            var6.addSuppressed(var30);
                        }
                    } else {
                        writer.close();
                    }

                }
            } catch (Throwable var33) {
                var38 = var33;
                throw var33;
            } finally {
                if (var38 != null) {
                    try {
                        outputStream.close();
                    } catch (Throwable var29) {
                        var38.addSuppressed(var29);
                    }
                } else {
                    outputStream.close();
                }

            }
        } catch (IOException var35) {
            LOGGER.error("Couldn't save player advancements to {}", this.researchFile, var35);
        }

    }

    public boolean grantCriterion(ResearchNode node, String criterionName) {
        if (node.getParents().length > 0) {
            for (ResearchNode parent : node.getParents()) {
                if (!getProgress(parent).isDone()) return false;
            }
        }
        boolean bl = false;
        AdvancementProgress progress = this.getProgress(node);
        boolean bl2 = progress.isDone();
        if (progress.obtain(criterionName)) {
            this.endTrackingCompleted(node);
            this.progressUpdates.add(node);
            bl = true;
            if (!bl2 && progress.isDone()) {
                node.getRewards().apply(this.owner);
                Galacticraft.logger.info("Research completed! " + node.getId());
//            if (node.getInfo() != null && node.getInfo().shouldAnnounceToChat() && this.owner.world.getGameRules().getBoolean(GameRules.ANNOUNCE_ADVANCEMENTS)) { //TODO: Do we need to announce to chat?
//               this.server.getPlayerManager().sendToAll((Text)(new TranslatableText("chat.type.advancement." + node.getDisplay().getFrame().getId(), new Object[]{this.owner.getDisplayName(), node.toHoverableText()})));
//            }
            }
        }

        if (progress.isDone()) {
            this.updateDisplay(node);
        }

        return bl;
    }

    public boolean revokeCriterion(ResearchNode node, String criterionName) {
        boolean bl = false;
        AdvancementProgress progress = this.getProgress(node);
        if (progress.reset(criterionName)) {
            this.beginTracking(node);
            this.progressUpdates.add(node);
            bl = true;
        }

        if (!progress.isAnyObtained()) {
            this.updateDisplay(node);
        }

        return bl;
    }

    private void beginTracking(ResearchNode node) {
        AdvancementProgress progress = this.getProgress(node);
        if (!progress.isDone()) {

            for (Entry<String, AdvancementCriterion> entry : node.getCriteria().entrySet()) {
                CriterionProgress criterionProgress = progress.getCriterionProgress(entry.getKey());
                if (criterionProgress != null && !criterionProgress.isObtained()) {
                    CriterionConditions criterionConditions = entry.getValue().getConditions();
                    if (criterionConditions != null) {
                        Criterion<CriterionConditions> criterion = Criteria.getById(criterionConditions.getId());
                        if (criterion != null) {
                            criterion.beginTrackingCondition(this, new ResearchConditionsContainer<>(criterionConditions, node, entry.getKey()));
                        }
                    }
                }
            }

        }
    }

    private void endTrackingCompleted(ResearchNode node) {
        AdvancementProgress progress = this.getProgress(node);
        Iterator<Map.Entry<String, AdvancementCriterion>> itr = node.getCriteria().entrySet().iterator();

        while (true) {
            Entry<String, AdvancementCriterion> entry;
            CriterionProgress criterionProgress;
            do {
                do {
                    if (!itr.hasNext()) {
                        return;
                    }

                    entry = itr.next();
                    criterionProgress = progress.getCriterionProgress(entry.getKey());
                } while (criterionProgress == null);
            } while (!criterionProgress.isObtained() && !progress.isDone());

            CriterionConditions criterionConditions = (entry.getValue()).getConditions();
            if (criterionConditions != null) {
                Criterion<CriterionConditions> criterion = Criteria.getById(criterionConditions.getId());
                if (criterion != null) {
                    criterion.endTrackingCondition(this, new ResearchConditionsContainer<>(criterionConditions, node, entry.getKey()));
                }
            }
        }
    }

    public void sendUpdate(ServerPlayerEntity player) {
        if (this.dirty || !this.visibilityUpdates.isEmpty() || !this.progressUpdates.isEmpty()) {
            Map<Identifier, AdvancementProgress> map = Maps.newHashMap();
            Set<ResearchNode> set = new LinkedHashSet<>();
            Set<Identifier> set2 = new LinkedHashSet<>();

            for (ResearchNode node : this.progressUpdates) {
                if (this.visibleResearch.contains(node)) {
                    map.put(node.getId(), this.researchToProgress.get(node));
                }
            }

            for (ResearchNode node : this.visibilityUpdates) {
                if (this.visibleResearch.contains(node)) {
                    set.add(node);
                } else {
                    set2.add(node.getId());
                }
            }

            if (this.dirty || !map.isEmpty() || !set.isEmpty() || !set2.isEmpty()) {
                player.networkHandler.sendPacket(new CustomPayloadS2CPacket(new Identifier(Constants.MOD_ID, "research_update"), new PacketByteBuf(createPacket(this.dirty, set, set2, map, new PacketByteBuf(Unpooled.buffer())))));
                this.visibilityUpdates.clear();
                this.progressUpdates.clear();
            }
        }

        this.dirty = false;
    }

    private PacketByteBuf createPacket(boolean clearCurrent, Collection<ResearchNode> toEarn, Set<Identifier> toRemove, Map<Identifier, AdvancementProgress> toSetProgress, PacketByteBuf buf) {
        Map<Identifier, ResearchNode.Builder> map = Maps.newHashMap();

        for (ResearchNode node : toEarn) {
            map.put(node.getId(), node.toBuilder());
        }

        toSetProgress = new HashMap<>(toSetProgress);

        buf.writeBoolean(clearCurrent);
        buf.writeVarInt(map.size());

        for (Entry<Identifier, ResearchNode.Builder> entry : map.entrySet()) {
            Identifier identifier = entry.getKey();
            ResearchNode.Builder task = entry.getValue();
            buf.writeIdentifier(identifier);
            task.toPacket(buf);
        }

        buf.writeVarInt(toRemove.size());

        for (Identifier id : toRemove) {
            buf.writeIdentifier(id);
        }

        buf.writeVarInt(toSetProgress.size());

        for (Entry<Identifier, AdvancementProgress> entry : toSetProgress.entrySet()) {
            buf.writeIdentifier(entry.getKey());
            entry.getValue().toPacket(buf);
        }
        return buf;
    }

//   public void setDisplayTab(@Nullable ResearchNode advancement) { //not relevant? fixme
//      ResearchNode advancement2 = this.currentDisplayTab;
//      if (advancement != null && advancement.getParent() == null && advancement.getDisplay() != null) {
//         this.currentDisplayTab = advancement;
//      } else {
//         this.currentDisplayTab = null;
//      }
//
//      if (advancement2 != this.currentDisplayTab) {
//         this.owner.networkHandler.sendPacket(new SelectAdvancementTabS2CPacket(this.currentDisplayTab == null ? null : this.currentDisplayTab.getId()));
//      }
//
//   }

    public AdvancementProgress getProgress(ResearchNode node) {
        AdvancementProgress advancementProgress = this.researchToProgress.get(node);
        if (advancementProgress == null) {
            advancementProgress = new AdvancementProgress();
            this.initProgress(node, advancementProgress);
        }

        return advancementProgress;
    }

    private void initProgress(ResearchNode node, AdvancementProgress progress) {
        progress.init(node.getCriteria(), node.getRequirements());
        this.researchToProgress.put(node, progress);
    }

    private void updateDisplay(ResearchNode research) {
        boolean bl = this.canSee(research);
        boolean bl2 = this.visibleResearch.contains(research);
        if (bl && !bl2) {
            this.visibleResearch.add(research);
            this.visibilityUpdates.add(research);
            if (this.researchToProgress.containsKey(research)) {
                this.progressUpdates.add(research);
            }
        } else if (!bl && bl2) {
            this.visibleResearch.remove(research);
            this.visibilityUpdates.add(research); //TODO
        }

        if (bl != bl2) {
            for (ResearchNode node : research.getParents()) {
                this.updateDisplay(node);
            }
        }

        for (ResearchNode node : research.getChildren()) {
            this.updateDisplay(node);
        }

    }

    private boolean canSee(ResearchNode researh) {
        for (int i = 0; researh != null && i <= 2; ++i) {
            if (i == 0 && this.hasChildrenDone(researh)) {
                return true;
            }

            if (researh.getInfo() == null) {
                return false;
            }

            AdvancementProgress advancementProgress = this.getProgress(researh);
            if (advancementProgress.isDone()) {
                return true;
            }

            if (researh.getInfo().isHidden()) {
                return false;
            }

            researh = researh.getParents()[0]; //TODO
        }

        return false;
    }

    private boolean hasChildrenDone(ResearchNode research) {
        AdvancementProgress advancementProgress = this.getProgress(research);
        if (!advancementProgress.isDone()) {
            Iterator<ResearchNode> var3 = research.getChildren().iterator();

            ResearchNode node;
            do {
                if (!var3.hasNext()) {
                    return false;
                }

                node = var3.next();
            } while (!this.hasChildrenDone(node));

        }
        return true;
    }
}
