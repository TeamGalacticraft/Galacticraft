/*
 * Copyright (c) 2019-2025 Team Galacticraft
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