package com.hrznstudio.galacticraft.api.research;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ResearchNode {
    @Nullable
    private final ResearchNode[] children;

    private final Identifier id;
    private final ResearchRewards rewards;
    private final Map<String, AdvancementCriterion> criteria;
    private final String[][] requirements;
    private final CriteriaMerger merger;

    public ResearchNode(@Nullable ResearchNode[] children, Identifier id, ResearchRewards rewards, Map<String, AdvancementCriterion> criteria, String[][] requirements, ResearchRewards researchRewards) {
        this.children = children;
        this.id = id;
        this.rewards = rewards;
        this.criteria = criteria;
        this.requirements = requirements;
        this.merger = CriteriaMerger.AND;
    }

    public ResearchNode[] getChildren() {
        return children;
    }

    public Identifier getId() {
        return id;
    }


    public static class Builder {
        private List<Identifier> childrenIds;
        private final List<ResearchNode> children =  new ArrayList<>();
        private ResearchInfo info;
        private ResearchRewards rewards;
        private Map<String, AdvancementCriterion> criteria;
        private String[][] requirements;
        private CriteriaMerger merger;

        private Builder(List<Identifier> childrenIds, @javax.annotation.Nullable ResearchInfo info, ResearchRewards rewards, Map<String, AdvancementCriterion> criteria, String[][] requirements) {
            this.rewards = ResearchRewards.NONE;
            this.criteria = Maps.newLinkedHashMap();
            this.merger = CriteriaMerger.AND;
            this.childrenIds = childrenIds;
            this.info = info;
            this.rewards = rewards;
            this.criteria = criteria;
            this.requirements = requirements;
        }

        private Builder() {
            this.rewards = ResearchRewards.NONE;
            this.criteria = Maps.newLinkedHashMap();
            this.merger = CriteriaMerger.AND;
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder child(ResearchNode child) {
            this.children.add(child);
            return this;
        }

        public Builder child(Identifier childId) {
            this.childrenIds.add(childId);
            return this;
        }

        public Builder info(Item[] icons, Text title, Text description, @javax.annotation.Nullable Identifier background, boolean hidden) {
            return this.info(new ResearchInfo(icons, title, description, background, hidden));
        }

        public Builder info(ItemConvertible[] icons, Text title, Text description, @javax.annotation.Nullable Identifier background, boolean hidden) {
            return this.info(new ResearchInfo(icons, title, description, background, hidden));
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

        public Builder criteriaMerger(CriteriaMerger merger) {
            this.merger = merger;
            return this;
        }

        public boolean findChildren(Function<Identifier, ResearchNode> parentProvider) {
            if (this.childrenIds.isEmpty()) {
                return true;
            } else {
                boolean allgood  = true;
                for (Identifier id : childrenIds) {
                    ResearchNode node = parentProvider.apply(id);
                    if (node != null) {
                        this.children.add(node);
                    } else {
                        allgood =false;
                    }
                }
                return allgood;
            }
        }

        public ResearchNode build(Identifier id) {
            if (!this.findChildren((identifier) -> {
                return null;
            })) {
                throw new IllegalStateException("Tried to build incomplete advancement!");
            } else {
                if (this.requirements == null) {
                    this.requirements = this.merger.createRequirements(this.criteria.keySet());
                }

                return new ResearchNode(this.children.toArray(new ResearchNode[0]), id, this.rewards, this.criteria, this.requirements, this.rewards);
            }
        }

        public ResearchNode build(Consumer<ResearchNode> consumer, String string) {
            ResearchNode advancement = this.build(new Identifier(string));
            consumer.accept(advancement);
            return advancement;
        }

//        public JsonObject toJson() {
//            if (this.requirements == null) {
//                this.requirements = this.merger.createRequirements(this.criteria.keySet());
//            }
//
//            JsonObject jsonObject = new JsonObject();
//            if (this.children.isEmpty()) {
//                jsonObject.add
//                jsonObject.addProperty("parent", this.parentObj.getId().toString());
//            } else if (this.childrenIds != null) {
//                jsonObject.addProperty("parent", this.childrenIds.toString());
//            }
//
//            if (this.info != null) {
//                jsonObject.add("info", this.info.toJson());
//            }
//
//            jsonObject.add("rewards", this.rewards.toJson());
//            JsonObject jsonObject2 = new JsonObject();
//
//            for (Map.Entry<String, AdvancementCriterion> stringAdvancementCriterionEntry : this.criteria.entrySet()) {
//                jsonObject2.add((String) ((Map.Entry<String, AdvancementCriterion>) (Map.Entry) stringAdvancementCriterionEntry).getKey(), ((AdvancementCriterion) ((Map.Entry<String, AdvancementCriterion>) (Map.Entry) stringAdvancementCriterionEntry).getValue()).toJson());
//            }
//
//            jsonObject.add("criteria", jsonObject2);
//            JsonArray jsonArray = new JsonArray();
//            String[][] var14 = this.requirements;
//            int var5 = var14.length;
//
//            for (String[] strings : var14) {
//                JsonArray jsonArray2 = new JsonArray();
//                int var10 = strings.length;
//
//                for (int var11 = 0; var11 < var10; ++var11) {
//                    String string = strings[var11];
//                    jsonArray2.add(string);
//                }
//
//                jsonArray.add(jsonArray2);
//            }
//
//            jsonObject.add("requirements", jsonArray);
//            return jsonObject;
//        }
//
//        public void toPacket(PacketByteBuf buf) {
//            if (this.childrenIds == null) {
//                buf.writeBoolean(false);
//            } else {
//                buf.writeBoolean(true);
//                buf.writeIdentifier(this.childrenIds);
//            }
//
//            if (this.info == null) {
//                buf.writeBoolean(false);
//            } else {
//                buf.writeBoolean(true);
//                this.info.toPacket(buf);
//            }
//
//            AdvancementCriterion.criteriaToPacket(this.criteria, buf);
//            buf.writeVarInt(this.requirements.length);
//            String[][] var2 = this.requirements;
//            int var3 = var2.length;
//
//            for (String[] strings : var2) {
//                buf.writeVarInt(strings.length);
//                int var7 = strings.length;
//
//                for (String string : strings) {
//                    buf.writeString(string);
//                }
//            }
//
//        }

        public String toString() {
            return "Task Advancement{parentId=" + this.childrenIds + ", info=" + this.info + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + '}';
        }

        public static Builder fromJson(JsonObject obj, AdvancementEntityPredicateDeserializer predicateDeserializer) {
            List<Identifier> children = new ArrayList<>(); //obj.has("children") ? new Identifier(JsonHelper.getString(obj, "children")) : null;
            if (obj.has("children")) {
                for (JsonElement element : obj.get("children").getAsJsonArray()) {
                    children.add(new Identifier(element.getAsString()));
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
                        return new Builder(children, info, rewards, map, strings);
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
            List<Identifier> children = new ArrayList<>();
            byte b = buf.readByte();
            if (b > 0) {
                for (int i = 0; i < b; i++) {
                    children.add(buf.readIdentifier());
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

            return new Builder(children, info, ResearchRewards.NONE, map, strings);
        }

        public Map<String, AdvancementCriterion> getCriteria() {
            return this.criteria;
        }
    }
}
