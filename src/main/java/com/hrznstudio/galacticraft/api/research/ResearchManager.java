package com.hrznstudio.galacticraft.api.research;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ResearchManager {
    private static final Gson GSON = new GsonBuilder().create();

    private final Map<Identifier, ResearchNode> advancements = new HashMap<>();
    private final Set<ResearchNode> roots = new HashSet<>();

    public void load(Map<Identifier, ResearchNode.Builder> research) {

    }
}
