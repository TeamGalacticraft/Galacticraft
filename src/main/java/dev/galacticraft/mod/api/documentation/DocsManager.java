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

package dev.galacticraft.mod.api.documentation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.documentation.client.pages.BlankPageScreen;
import dev.galacticraft.mod.api.documentation.model.HomeDoc;
import dev.galacticraft.mod.api.documentation.model.SectionOverview;
import dev.galacticraft.mod.api.documentation.model.SubDoc;
import dev.galacticraft.mod.api.documentation.model.elements.ButtonElement;
import dev.galacticraft.mod.api.documentation.model.elements.Element;
import dev.galacticraft.mod.api.documentation.model.elements.ImageElement;
import dev.galacticraft.mod.api.documentation.model.elements.TextElement;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class DocsManager {
    public static final ResourceLocation HOME_ID = Constant.id("home");

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class,
                    (com.google.gson.JsonDeserializer<ResourceLocation>) (json, t, ctx) ->
                            ResourceLocation.parse(json.getAsString()))
            .registerTypeAdapter(ResourceLocation.class,
                    (com.google.gson.JsonSerializer<ResourceLocation>) (src, t, ctx) ->
                            new com.google.gson.JsonPrimitive(src.toString()))
            .registerTypeAdapter(dev.galacticraft.mod.api.documentation.model.Section.class,
                    (com.google.gson.JsonDeserializer<dev.galacticraft.mod.api.documentation.model.Section>) (json, t, ctx) -> {
                        JsonObject obj = json.getAsJsonObject();
                        String type = obj.get("type").getAsString();
                        if ("overview".equals(type)) {
                            String headingKey = obj.has("headingKey") ? obj.get("headingKey").getAsString() : null;
                            String bodyKey = obj.has("bodyKey") ? obj.get("bodyKey").getAsString() : null;
                            return new SectionOverview(type, headingKey, bodyKey);
                        }
                        throw new JsonParseException("Unknown docs section type: " + type);
                    })
            .registerTypeAdapter(Element.class,
                    (com.google.gson.JsonDeserializer<Element>) (json, t, ctx) -> {
                        com.google.gson.JsonObject obj = json.getAsJsonObject();
                        String type = obj.get("type").getAsString();

                        switch (type) {
                            case "button" -> {
                                float nx = obj.get("nx").getAsFloat();
                                float ny = obj.get("ny").getAsFloat();
                                float nw = obj.get("nw").getAsFloat();
                                float nh = obj.get("nh").getAsFloat();
                                String textKey = obj.get("textKey").getAsString();
                                String target = obj.get("target").getAsString();
                                int order = obj.get("order").getAsInt();
                                return new ButtonElement(type, nx, ny, nw, nh, textKey, target, order);
                            }
                            case "text" -> {
                                float nminX = obj.get("nminX").getAsFloat();
                                float nminY = obj.get("nminY").getAsFloat();
                                float nmaxX = obj.get("nmaxX").getAsFloat();
                                float nmaxY = obj.get("nmaxY").getAsFloat();
                                String textKey = obj.get("textKey").getAsString();
                                String align = obj.has("align") ? obj.get("align").getAsString() : null;
                                int order = obj.get("order").getAsInt();
                                return new TextElement(type, nminX, nminY, nmaxX, nmaxY, textKey, align, order);
                            }
                            case "image" -> {
                                float nx = obj.get("nx").getAsFloat();
                                float ny = obj.get("ny").getAsFloat();
                                float nw = obj.get("nw").getAsFloat();
                                float nh = obj.get("nh").getAsFloat();
                                String texture = obj.get("texture").getAsString();
                                int u = obj.get("u").getAsInt();
                                int v = obj.get("v").getAsInt();
                                int texW = obj.get("texW").getAsInt();
                                int texH = obj.get("texH").getAsInt();
                                int order = obj.get("order").getAsInt();
                                return new ImageElement(type, nx, ny, nw, nh, texture, u, v, texW, texH, order);
                            }
                            default -> throw new com.google.gson.JsonParseException("Unknown docs element type: " + type);
                        }
                    })
            .setPrettyPrinting()
            .create();
    private static final Map<ResourceLocation, SubDoc> DOCS = new LinkedHashMap<>();
    private static final Map<Item, ResourceLocation> ITEM_TO_PAGE = new HashMap<>();
    private static final Map<ResourceLocation, ResourceLocation> PARENTS = new HashMap<>();
    private static HomeDoc HOME;

    private DocsManager() {
    }

    public static void clear() {
        HOME = null;
        DOCS.clear();
        ITEM_TO_PAGE.clear();
        PARENTS.clear();
    }

    public static void setHome(HomeDoc home) {
        HOME = home;
    }

    public static void addDoc(SubDoc doc, Collection<Item> boundItems) {
        DOCS.put(doc.id(), doc);

        ResourceLocation parent = doc.parent() != null ? doc.parent() : HOME_ID;
        PARENTS.put(doc.id(), parent);

        for (Item it : boundItems) {
            ITEM_TO_PAGE.put(it, doc.id());
        }
    }

    public static ResourceLocation pageFor(Item item) {
        return ITEM_TO_PAGE.get(item);
    }

    public static ResourceLocation parentOf(ResourceLocation id) {
        return PARENTS.get(id);
    }

    public static HomeDoc getHomeDoc() {
        return HOME;
    }

    public static SubDoc getDoc(ResourceLocation id) {
        return DOCS.get(id);
    }

    public static void openHome() {
        final String titleKey = (HOME != null) ? HOME.titleKey() : "doc.gc.home.title";
        Minecraft.getInstance().setScreen(new BlankPageScreen(HOME_ID, titleKey));
    }

    public static void open(ResourceLocation id) {
        SubDoc doc = DOCS.get(id);
        if (doc != null) {
            Minecraft.getInstance().setScreen(new BlankPageScreen(id, doc.titleKey()));
        } else {
            openHome();
        }
    }
}