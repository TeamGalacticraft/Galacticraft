package com.hrznstudio.galacticraft.api.research;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.hrznstudio.galacticraft.Galacticraft;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class ResearchManager {
    public final List<Listener> listeners = new ArrayList<>();
    private final Map<Identifier, ResearchNode> research = new HashMap<>();
    private final List<ResearchNode> roots = new ArrayList<>();
    private final Set<ResearchNode> dependants = new HashSet<>();

    public void load(Map<Identifier, ResearchNode.Builder> research) {
        long time = System.currentTimeMillis();
        Function<Identifier, ResearchNode> function = Functions.forMap(this.research, null);

        while (!research.isEmpty()) {
            boolean bl = false;
            Iterator<Map.Entry<Identifier, ResearchNode.Builder>> iterator = research.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<Identifier, ResearchNode.Builder> entry = iterator.next();
                Identifier identifier = entry.getKey();
                ResearchNode.Builder builder = entry.getValue();
                if (!this.research.containsKey(identifier)) {
                    if (builder.findParents(function)) {
                        ResearchNode node = builder.build(identifier);
                        this.research.put(identifier, node);
                        bl = true;
                        iterator.remove();
                        if (node.getParents() == null || node.getParents().length == 0) {
                            this.roots.add(node);
                            for (Listener listener : listeners) {
                                listener.onRootAdded(node);
                            }
                        } else {
                            this.dependants.add(node);
                            for (Listener listener : listeners) {
                                listener.onDependentAdded(node);
                            }
                        }
                    }
                }
            }

            if (!bl) {
                iterator = research.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<Identifier, ResearchNode.Builder> entry = iterator.next();
                    Galacticraft.logger.error("Couldn't load research node {}: {}", entry.getKey(), entry.getValue());
                }
                if (System.currentTimeMillis() > time + 30000) {
                    throw new RuntimeException("Research has taken over 30 seconds to load! This isn't supposed to happen! You might have invalid custom nodes.");
                }
            }
        }

        roots.sort((node1, node2) -> {
            if (node1.getInfo().getTier() < node2.getInfo().getTier()) {
                return -1;
            } else if (node1.getInfo().getTier() == node2.getInfo().getTier()) {
                throw new RuntimeException("2 roots for the same tier?!");
            } else {
                return 1;
            }
        });

        for (ResearchNode node : roots) {
            new ResearchPositioner().position(node);
        }
    }

    public List<ResearchNode> getRoots() {
        return roots;
    }

    public Set<ResearchNode> getDependants() {
        return dependants;
    }

    public Map<Identifier, ResearchNode> getResearch() {
        return ImmutableMap.copyOf(research);
    }

    @Environment(EnvType.CLIENT)
    public void clear() {
        this.research.clear();
        this.roots.clear();
        this.dependants.clear();
        for (Listener listener : listeners) {
            if (listener != null) {
                listener.onClear();
            }
        }

    }

    @Environment(EnvType.CLIENT)
    public void removeAll(Set<Identifier> advancements) {
        for (Identifier identifier : advancements) {
            ResearchNode node = this.research.get(identifier);
            if (node == null) {
                Galacticraft.logger.warn("Told to remove research node {} but it's not valid!", identifier);
            } else {
                this.remove(node);
            }
        }
    }

    @Nullable
    public ResearchNode get(Identifier id) {
        return this.research.get(id);
    }

    @Environment(EnvType.CLIENT)
    private void remove(ResearchNode research) {

        for (ResearchNode researchNode : research.getChildren()) {
            this.remove(researchNode);
        }

        Galacticraft.logger.info("Forgot about research {}", research.getId());
        this.research.remove(research.getId());
        if (research.getParents() != null && research.getParents().length == 0) {
            this.roots.remove(research);
            for (Listener listener : listeners) {
                if (listener != null) {
                    listener.onRootRemoved(research);
                }
            }
        } else {
            for (Listener listener : listeners) {
                this.dependants.remove(research);
                if (listener != null) {
                    listener.onDependentRemoved(research);
                }
            }
        }

    }

    public interface Listener {
        void onRootAdded(ResearchNode root);

        @Environment(EnvType.CLIENT)
        void onRootRemoved(ResearchNode root);

        void onDependentAdded(ResearchNode dependent);

        @Environment(EnvType.CLIENT)
        void onDependentRemoved(ResearchNode dependent);

        @Environment(EnvType.CLIENT)
        void onClear();
    }
}
