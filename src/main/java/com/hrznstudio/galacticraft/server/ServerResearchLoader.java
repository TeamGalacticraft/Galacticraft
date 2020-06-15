package com.hrznstudio.galacticraft.server;

import com.google.gson.*;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.research.ResearchManager;
import com.hrznstudio.galacticraft.api.research.ResearchNode;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

import java.util.HashMap;
import java.util.Map;

public class ServerResearchLoader extends JsonDataLoader {
    private static final Gson GSON = new GsonBuilder().create();
    private final LootConditionManager conditionManager;
    private ResearchManager manager;

    public ServerResearchLoader(LootConditionManager conditionManager) {
        super(GSON, "gc_research");
        this.conditionManager = conditionManager;
    }

    public ResearchManager getManager() {
        return manager;
    }

    @Override
    public String getName() {
        return "Galacticraft: Rewoven - Research Data Loader";
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> loader, ResourceManager manager, Profiler profiler) {
        Galacticraft.logger.info("Loading research!");
        Map<Identifier, ResearchNode.Builder> map2 = new HashMap<>();
        loader.forEach((identifier, jsonObject) -> {
            try {
                JsonObject jsonObject2 = JsonHelper.asObject(jsonObject, "research");
                ResearchNode.Builder builder = ResearchNode.Builder.fromJson(jsonObject2, new AdvancementEntityPredicateDeserializer(identifier, this.conditionManager));
                map2.put(identifier, builder);
            } catch (IllegalArgumentException | JsonParseException var6) {
                Galacticraft.logger.error("Parsing error loading custom research {}: {}", identifier, var6.getMessage());
            }

        });

        ResearchManager researchManager = new ResearchManager();
        researchManager.load(map2);
        this.manager = researchManager;
    }
}
