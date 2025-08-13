package dev.galacticraft.mod.api.documentation.reload;

import com.google.gson.JsonElement;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.documentation.DocsManager;
import dev.galacticraft.mod.api.documentation.model.HomeDoc;
import dev.galacticraft.mod.api.documentation.model.SubDoc;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.*;

public final class DocsReloadListener extends SimpleJsonResourceReloadListener implements IdentifiableResourceReloadListener {
    private static final String ROOT = "docs";
    public static final ResourceLocation ID = Constant.id("docs_resource_reload_listener");

    public DocsReloadListener() {
        super(DocsManager.GSON, ROOT);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> elements, ResourceManager rm, ProfilerFiller profiler) {
        DocsManager.clear();

        ResourceLocation homeKey = Constant.id("home");
        JsonElement homeEl = elements.get(homeKey);
        if (homeEl != null && homeEl.isJsonObject()) {
            try {
                HomeDoc home = DocsManager.GSON.fromJson(homeEl, HomeDoc.class);
                DocsManager.setHome(home);
            } catch (Exception ex) {
                Constant.LOGGER.warn("[GC Docs] Failed to parse home.json ({}).", homeKey, ex);
            }
        } else {
            Constant.LOGGER.debug("[GC Docs] home.json not found at {}", homeKey);
        }

        for (var entry : elements.entrySet()) {
            ResourceLocation res = entry.getKey();
            String path = res.getPath();
            if (!path.startsWith("pages/")) continue;

            try {
                SubDoc raw = DocsManager.GSON.fromJson(entry.getValue(), SubDoc.class);

                if (raw.sections() != null && raw.sections().contains(null)) {
                    var cleaned = raw.sections().stream().filter(Objects::nonNull).toList();
                    raw = new SubDoc(
                            raw.schema(),
                            raw.id(),
                            raw.parent(),
                            raw.titleKey(),
                            raw.bind(),
                            raw.tags(),
                            cleaned,
                            raw.titlePos(),
                            raw.elements()
                    );
                }

                List<net.minecraft.world.item.Item> bound = new ArrayList<>();
                if (raw.bind() != null) {
                    for (ResourceLocation itemId : raw.bind()) {
                        BuiltInRegistries.ITEM.getOptional(itemId).ifPresentOrElse(
                                bound::add,
                                () -> Constant.LOGGER.warn("[GC Docs] Unknown item in bind list: {}", itemId)
                        );
                    }
                }

                DocsManager.addDoc(raw, bound);
            } catch (Exception ex) {
                Constant.LOGGER.warn("[GC Docs] Failed to parse doc {}", res, ex);
            }
        }
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }
}