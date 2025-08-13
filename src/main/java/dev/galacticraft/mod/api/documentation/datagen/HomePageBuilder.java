package dev.galacticraft.mod.api.documentation.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

final class HomePageBuilder extends BasePageBuilder<HomePageBuilder> {
    private final List<ResourceLocation> featured = new ArrayList<>();

    HomePageBuilder(int schema) {
        super(schema);
    }

    /**
     * Add a “featured” page id to show as a quick link on the home page.
     * Kept for compatibility with earlier formats.
     *
     * @param pageId id of the page to feature
     * @return {@link HomePageBuilder}
     */
    public HomePageBuilder addFeatured(ResourceLocation pageId) {
        this.featured.add(pageId);
        return this;
    }

    @Override
    public JsonObject build() {
        JsonObject root = new JsonObject();
        bakeCommon(root);

        if (!featured.isEmpty()) {
            JsonArray links = new JsonArray();
            for (ResourceLocation rl : featured) links.add(rl.toString());
            root.add("links", links);
        }
        return root;
    }
}