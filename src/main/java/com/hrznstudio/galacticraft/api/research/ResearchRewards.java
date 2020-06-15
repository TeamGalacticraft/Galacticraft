package com.hrznstudio.galacticraft.api.research;

import com.google.common.collect.Lists;
import com.google.gson.*;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ResearchRewards {
    public static final ResearchRewards NONE = new ResearchRewards(0, new Identifier[0], new Identifier[0], CommandFunction.LazyContainer.EMPTY);
    private final int experience;
    private final Identifier[] loot;
    private final Identifier[] rocketParts;
    private final CommandFunction.LazyContainer function;

    public ResearchRewards(int experience, Identifier[] loot, Identifier[] rocketParts, CommandFunction.LazyContainer function) {
        this.experience = experience;
        this.loot = loot;
        this.rocketParts = rocketParts;
        this.function = function;
    }

    public static ResearchRewards fromJson(JsonObject json) throws JsonParseException {
        int i = JsonHelper.getInt(json, "experience", 0);
        JsonArray jsonArray = JsonHelper.getArray(json, "loot", new JsonArray());
        Identifier[] identifiers = new Identifier[jsonArray.size()];

        for (int j = 0; j < identifiers.length; ++j) {
            identifiers[j] = new Identifier(JsonHelper.asString(jsonArray.get(j), "loot[" + j + "]"));
        }

        JsonArray jsonArray2 = JsonHelper.getArray(json, "rocket_parts", new JsonArray());
        Identifier[] identifiers2 = new Identifier[jsonArray2.size()];

        for (int k = 0; k < identifiers2.length; ++k) {
            identifiers2[k] = new Identifier(JsonHelper.asString(jsonArray2.get(k), "rocket_parts[" + k + "]"));
        }

        CommandFunction.LazyContainer lazyContainer2;
        if (json.has("function")) {
            lazyContainer2 = new CommandFunction.LazyContainer(new Identifier(JsonHelper.getString(json, "function")));
        } else {
            lazyContainer2 = CommandFunction.LazyContainer.EMPTY;
        }

        return new ResearchRewards(i, identifiers, identifiers2, lazyContainer2);
    }

    public void apply(ServerPlayerEntity player) {
        player.addExperience(this.experience);
        LootContext lootContext = (new LootContext.Builder(player.getServerWorld())).parameter(LootContextParameters.THIS_ENTITY, player).parameter(LootContextParameters.POSITION, player.getBlockPos()).random(player.getRandom()).build(LootContextTypes.ADVANCEMENT_REWARD);
        boolean stackAdded = false;

        for (Identifier identifier : this.loot) {
            for (ItemStack itemStack : player.server.getLootManager().getTable(identifier).generateLoot(lootContext)) {
                if (player.giveItemStack(itemStack)) {
                    player.world.playSound(null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.ENTITY_ITEM_PICKUP,
                            SoundCategory.PLAYERS,
                            0.2F,
                            ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    stackAdded = true;
                } else {
                    ItemEntity itemEntity = player.dropItem(itemStack, false);
                    if (itemEntity != null) {
                        itemEntity.resetPickupDelay();
                        itemEntity.setOwner(player.getUuid());
                    }
                }
            }
        }

        if (stackAdded) {
            player.playerScreenHandler.sendContentUpdates();
        }

        for (Identifier id : this.rocketParts) {
            //todo
        }

        MinecraftServer minecraftServer = player.server;
        this.function.get(minecraftServer.getCommandFunctionManager()).ifPresent((commandFunction) -> {
            minecraftServer.getCommandFunctionManager().execute(commandFunction, player.getCommandSource().withSilent().withLevel(2));
        });
    }

    public String toString() {
        return "ResearchRewards{experience=" + this.experience + ", loot=" + Arrays.toString(this.loot) + ", rocketParts=" + Arrays.toString(this.rocketParts) + ", function=" + this.function + '}';
    }

    public JsonElement toJson() {
        if (this == NONE) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonObject = new JsonObject();
            if (this.experience != 0) {
                jsonObject.addProperty("experience", this.experience);
            }

            JsonArray jsonArray2;
            Identifier[] var3;
            int var4;
            int var5;
            Identifier identifier2;
            if (this.loot.length > 0) {
                jsonArray2 = new JsonArray();
                var3 = this.loot;
                var4 = var3.length;

                for (var5 = 0; var5 < var4; ++var5) {
                    identifier2 = var3[var5];
                    jsonArray2.add(identifier2.toString());
                }

                jsonObject.add("loot", jsonArray2);
            }

            if (this.rocketParts.length > 0) {
                jsonArray2 = new JsonArray();
                var3 = this.rocketParts;
                var4 = var3.length;

                for (var5 = 0; var5 < var4; ++var5) {
                    identifier2 = var3[var5];
                    jsonArray2.add(identifier2.toString());
                }

                jsonObject.add("rocket_parts", jsonArray2);
            }

            if (this.function.getId() != null) {
                jsonObject.addProperty("function", this.function.getId().toString());
            }

            return jsonObject;
        }
    }

    public static class Builder {
        private final List<Identifier> loot = Lists.newArrayList();
        private final List<Identifier> parts = Lists.newArrayList();
        private int experience;
        @Nullable
        private Identifier function;

        public static Builder experience(int experience) {
            return (new Builder()).addExperience(experience);
        }

        public static Builder loot(Identifier loot) {
            return (new Builder()).addLoot(loot);
        }

        public static Builder function(Identifier loot) {
            return (new Builder()).setFunction(loot);
        }

        public static Builder part(Identifier recipe) {
            return (new Builder()).addPart(recipe);
        }

        public Builder addExperience(int experience) {
            this.experience += experience;
            return this;
        }

        public Builder addLoot(Identifier loot) {
            this.loot.add(loot);
            return this;
        }

        public Builder setFunction(Identifier loot) {
            this.function = loot;
            return this;
        }

        public Builder addPart(Identifier recipe) {
            this.parts.add(recipe);
            return this;
        }

        public ResearchRewards build() {
            return new ResearchRewards(this.experience, this.loot.toArray(new Identifier[0]), this.parts.toArray(new Identifier[0]), this.function == null ? CommandFunction.LazyContainer.EMPTY : new CommandFunction.LazyContainer(this.function));
        }
    }

}
