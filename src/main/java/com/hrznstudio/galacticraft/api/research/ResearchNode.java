package com.hrznstudio.galacticraft.api.research;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.hrznstudio.galacticraft.Galacticraft;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.item.ItemConvertible;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ResearchNode {
    @NotNull
    private final ResearchNode[] parents;
    private final List<ResearchNode> children = new ArrayList<>();

    private final Identifier id;
    private final ResearchRewards rewards;
    private final ResearchInfo info;
    private final Map<String, AdvancementCriterion> criteria;
    private final String[][] requirements;


    public ResearchNode(Identifier id, ResearchInfo info, ResearchRewards rewards, @NotNull ResearchNode[] parents, Map<String, AdvancementCriterion> criteria, String[][] requirements) {
        this.parents = parents;
        this.id = id;
        this.info = info;
        this.rewards = rewards;
        this.criteria = criteria;
        this.requirements = requirements;
        for (ResearchNode parent : parents) {
            parent.addChild(this);
        }
    }

    public ResearchNode[] getParents() {
        return parents;
    }

    public Identifier getId() {
        return id;
    }

    public void addChild(ResearchNode child) {
        this.children.add(child);
    }

    public List<ResearchNode> getChildren() {
        return new ArrayList<>(children);
    }

    public ResearchRewards getRewards() {
        return rewards;
    }

    public Map<String, AdvancementCriterion> getCriteria() {
        return new HashMap<>(criteria);
    }

    public String[][] getRequirements() {
        return requirements;
    }

    public ResearchInfo getInfo() {
        return info;
    }

    public Builder toBuilder() {
        Identifier[] parentIds = new Identifier[this.parents.length];
        for (int i = 0; i < this.parents.length; i++) {
            parentIds[i] = this.parents[i].getId();
        }
        return new Builder(Arrays.asList(parentIds), this.info, this.rewards, this.criteria, this.requirements);
    }

    public static class Builder {
        private final List<ResearchNode> parents = new ArrayList<>();
        private List<Identifier> parentIds;
        private ResearchInfo info;
        private ResearchRewards rewards;
        private Map<String, AdvancementCriterion> criteria;
        private String[][] requirements;
        private CriterionMerger merger;

        private Builder(List<Identifier> parentIds, @Nullable ResearchInfo info, ResearchRewards rewards, Map<String, AdvancementCriterion> criteria, String[][] requirements) {
            this.rewards = ResearchRewards.NONE;
            this.criteria = Maps.newLinkedHashMap();
            this.merger = CriterionMerger.AND;
            this.parentIds = parentIds;
            this.info = info;
            this.rewards = rewards;
            this.criteria = criteria;
            this.requirements = requirements;
        }

        private Builder() {
            this.rewards = ResearchRewards.NONE;
            this.criteria = Maps.newLinkedHashMap();
            this.merger = CriterionMerger.AND;
        }

        public static Builder create() {
            return new Builder();
        }

        public static Builder fromJson(JsonObject obj, AdvancementEntityPredicateDeserializer predicateDeserializer) {
            List<Identifier> parents = new ArrayList<>(); //obj.has("parents") ? new Identifier(JsonHelper.getString(obj, "parents")) : null;
            if (obj.has("parents")) {
                for (JsonElement element : obj.get("parents").getAsJsonArray()) {
                    parents.add(new Identifier(element.getAsString()));
                }
            }

            ResearchInfo info = obj.has("info") ? ResearchInfo.fromJson(JsonHelper.getObject(obj, "info")) : null;
            ResearchRewards rewards = obj.has("rewards") ? ResearchRewards.fromJson(JsonHelper.getObject(obj, "rewards")) : ResearchRewards.NONE;
            Map<String, AdvancementCriterion> map = AdvancementCriterion.criteriaFromJson(JsonHelper.getObject(obj, "criteria"), predicateDeserializer);
            if (map.isEmpty()) {
                throw new JsonSyntaxException("Advancement criteria cannot be empty");
            } else {
                JsonArray jsonArray = JsonHelper.getArray(obj, "requirements", new JsonArray());
                String[][] strings = new String[jsonArray.size()][];

                int i;
                int j;
                for (i = 0; i < jsonArray.size(); ++i) {
                    JsonArray jsonArray2 = JsonHelper.asArray(jsonArray.get(i), "requirements[" + i + "]");
                    strings[i] = new String[jsonArray2.size()];

                    for (j = 0; j < jsonArray2.size(); ++j) {
                        strings[i][j] = JsonHelper.asString(jsonArray2.get(j), "requirements[" + i + "][" + j + "]");
                    }
                }

                if (strings.length == 0) {
                    strings = new String[map.size()][];
                    i = 0;

                    for (String s : map.keySet()) {
                        strings[i++] = new String[]{s};
                    }
                }

                String[][] var17 = strings;
                int var18 = strings.length;

                int var13;
                for (j = 0; j < var18; ++j) {
                    String[] strings2 = var17[j];
                    if (strings2.length == 0 && map.isEmpty()) {
                        throw new JsonSyntaxException("Requirement entry cannot be empty");
                    }

                    var13 = strings2.length;

                    for (int var14 = 0; var14 < var13; ++var14) {
                        String string2 = strings2[var14];
                        if (!map.containsKey(string2)) {
                            throw new JsonSyntaxException("Unknown required criterion '" + string2 + "'");
                        }
                    }
                }

                Iterator<String> var19 = map.keySet().iterator();

                String string3;
                boolean bl;
                do {
                    if (!var19.hasNext()) {
                        return new Builder(parents, info, rewards, map, strings);
                    }

                    string3 = var19.next();
                    bl = false;
                    int var24 = strings.length;

                    for (var13 = 0; var13 < var24; ++var13) {
                        String[] strings3 = strings[var13];
                        if (ArrayUtils.contains(strings3, string3)) {
                            bl = true;
                            break;
                        }
                    }
                } while (bl);

                throw new JsonSyntaxException("Criterion '" + string3 + "' isn't a requirement for completion. This isn't supported behaviour, all criteria must be required.");
            }
        }

        public static Builder fromPacket(PacketByteBuf buf) {
            List<Identifier> parents = new ArrayList<>();
            int b = buf.readVarInt();
            if (b > 0) {
                for (int i = 0; i < b; i++) {
                    parents.add(buf.readIdentifier());
                }
            }

            ResearchInfo info = buf.readBoolean() ? ResearchInfo.fromPacket(buf) : null;
            Map<String, AdvancementCriterion> map = AdvancementCriterion.criteriaFromPacket(buf);
            String[][] strings = new String[buf.readVarInt()][];

            for (int i = 0; i < strings.length; ++i) {
                strings[i] = new String[buf.readVarInt()];

                for (int j = 0; j < strings[i].length; ++j) {
                    strings[i][j] = buf.readString(32767);
                }
            }

            return new Builder(parents, info, ResearchRewards.NONE, map, strings);
        }

        public Builder parent(ResearchNode child) {
            this.parents.add(child);
            return this;
        }

        public Builder info(ResearchInfo info) {
            this.info = info;
            return this;
        }

        public Builder rewards(ResearchRewards.Builder builder) {
            return this.rewards(builder.build());
        }

        public Builder rewards(ResearchRewards rewards) {
            this.rewards = rewards;
            return this;
        }

        public Builder criterion(String name, CriterionConditions criterionConditions) {
            return this.criterion(name, new AdvancementCriterion(criterionConditions));
        }

        public Builder criterion(String name, AdvancementCriterion advancementCriterion) {
            if (this.criteria.containsKey(name)) {
                throw new IllegalArgumentException("Duplicate criterion " + name);
            } else {
                this.criteria.put(name, advancementCriterion);
                return this;
            }
        }

        public Builder CriterionMerger(CriterionMerger merger) {
            this.merger = merger;
            return this;
        }

        public Builder parent(Identifier childId) {
            this.parentIds.add(childId);
            return this;
        }

        public Builder info(ItemConvertible[] icons, TranslatableText title, TranslatableText description, @Nullable Identifier background, boolean hidden, int tier) {
            return this.info(new ResearchInfo(icons, title, description, background, hidden, tier));
        }

        public ResearchNode build(Consumer<ResearchNode> consumer, String string) {
            ResearchNode node = this.build(new Identifier(string));
            consumer.accept(node);
            return node;
        }

        public boolean findParents(Function<Identifier, ResearchNode> parentProvider) {
            if (this.parentIds.isEmpty()) {
                return true;
            } else {
                boolean allgood = true;
                for (Identifier id : parentIds) {
                    boolean preCalculated = false;
                    for (ResearchNode parent : this.parents) {
                        if (parent.getId().equals(id)) {
                            preCalculated = true;
                            break;
                        }
                    }
                    if (preCalculated) {
                        continue;
                    }
                    ResearchNode node = parentProvider.apply(id);
                    if (node != null) {
                        this.parents.add(node);
                    } else {
                        allgood = false;
                    }
                }
                return allgood;
            }
        }

        public ResearchNode build(Identifier id) {
            if (!this.findParents((identifier) -> {
                return null;
            })) {
                Galacticraft.logger.fatal(this.toString());
                throw new IllegalStateException("Tried to build incomplete research node!");
            } else {
                if (this.requirements == null) {
                    this.requirements = this.merger.createRequirements(this.criteria.keySet());
                }

                return new ResearchNode(id, this.info, this.rewards, this.parents.toArray(new ResearchNode[0]), this.criteria, this.requirements);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Builder builder = (Builder) o;
            return Objects.equals(parents, builder.parents) &&
                    Objects.equals(parentIds, builder.parentIds) &&
                    Objects.equals(info, builder.info) &&
                    Objects.equals(rewards, builder.rewards) &&
                    Objects.equals(criteria, builder.criteria) &&
                    Arrays.equals(requirements, builder.requirements) &&
                    Objects.equals(merger, builder.merger);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(parents, parentIds, info, rewards, criteria, merger);
            result = 31 * result + Arrays.hashCode(requirements);
            return result;
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "parents=" + parents +
                    ", parentIds=" + parentIds +
                    ", info=" + info +
                    ", rewards=" + rewards +
                    ", criteria=" + criteria +
                    ", requirements=" + Arrays.toString(requirements) +
                    ", merger=" + merger +
                    '}';
        }

        public Map<String, AdvancementCriterion> getCriteria() {
            return this.criteria;
        }

        public List<Identifier> getParents() {
            return parentIds;
        }

        public void toPacket(PacketByteBuf buf) {
            buf.writeVarInt(parentIds.size());
            for (Identifier id : parentIds) {
                buf.writeIdentifier(id);
            }

            buf.writeBoolean(info != null);
            if (info != null) {
                info.toPacket(buf);
            }

            AdvancementCriterion.criteriaToPacket(criteria, buf);

            buf.writeVarInt(requirements.length);
            for (String[] requirement : requirements) {
                buf.writeVarInt(requirement.length);
                for (String s : requirement) {
                    buf.writeString(s);
                }
            }
        }
    }
}
