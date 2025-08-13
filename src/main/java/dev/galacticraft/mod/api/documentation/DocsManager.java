package dev.galacticraft.mod.api.documentation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.documentation.client.pages.BlankPageScreen;
import dev.galacticraft.mod.api.documentation.model.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.*;

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
                            String bodyKey    = obj.has("bodyKey")    ? obj.get("bodyKey").getAsString()    : null;
                            return new SectionOverview(type, headingKey, bodyKey);
                        }
                        throw new JsonParseException("Unknown docs section type: " + type);
                    })
            .registerTypeAdapter(Element.class,
                    (com.google.gson.JsonDeserializer<Element>) (json, t, ctx) -> {
                        JsonObject obj = json.getAsJsonObject();
                        String type = obj.get("type").getAsString();

                        switch (type) {
                            case "button" -> {
                                int x = obj.get("x").getAsInt();
                                int y = obj.get("y").getAsInt();
                                int w = obj.get("w").getAsInt();
                                int h = obj.get("h").getAsInt();
                                String textKey = obj.get("textKey").getAsString();
                                String target = obj.get("target").getAsString();
                                return new ButtonElement(type, x, y, w, h, textKey, target);
                            }
                            case "text" -> {
                                int minX = obj.get("minX").getAsInt();
                                int minY = obj.get("minY").getAsInt();
                                int maxX = obj.get("maxX").getAsInt();
                                int maxY = obj.get("maxY").getAsInt();
                                String textKey = obj.get("textKey").getAsString();
                                String align = obj.has("align") ? obj.get("align").getAsString() : null;
                                return new TextElement(type, minX, minY, maxX, maxY, textKey, align);
                            }
                            case "image" -> {
                                int x = obj.get("x").getAsInt();
                                int y = obj.get("y").getAsInt();
                                int w = obj.get("w").getAsInt();
                                int h = obj.get("h").getAsInt();
                                String texture = obj.get("texture").getAsString();
                                int u = obj.get("u").getAsInt();
                                int v = obj.get("v").getAsInt();
                                int texW = obj.get("texW").getAsInt();
                                int texH = obj.get("texH").getAsInt();
                                return new ImageElement(type, x, y, w, h, texture, u, v, texW, texH);
                            }
                            default -> throw new JsonParseException("Unknown docs element type: " + type);
                        }
                    })
            .setPrettyPrinting()
            .create();

    private static HomeDoc HOME;
    private static final Map<ResourceLocation, SubDoc> DOCS = new LinkedHashMap<>();
    private static final Map<Item, ResourceLocation> ITEM_TO_PAGE = new HashMap<>();
    private static final Map<ResourceLocation, ResourceLocation> PARENTS = new HashMap<>();

    private DocsManager() {}

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