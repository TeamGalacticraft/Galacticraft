package dev.galacticraft.mod.api.documentation.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.galacticraft.mod.Constant;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

final class SubPageBuilder extends BasePageBuilder<SubPageBuilder> {
    private final ResourceLocation id;
    private ResourceLocation parent;
    private final JsonArray bind = new JsonArray();
    private final JsonArray sections = new JsonArray();

    SubPageBuilder(int schema, ResourceLocation id) {
        super(schema);
        this.id = id;
    }

    /**
     * Set an explicit parent page for this subpage.
     * If not set, the runtime will default the parent to the home page.
     *
     * @param parent id of the parent page
     * @return {@link SubPageBuilder}
     */
    public SubPageBuilder setParent(ResourceLocation parent) {
        this.parent = parent;
        return this;
    }

    /**
     * Add an "overview" section to this page.
     *
     * @param headingKey translatable key for the heading
     * @param bodyKey translatable key for the body text
     * @return {@link SubPageBuilder}
     */
    public SubPageBuilder addOverviewSection(String headingKey, String bodyKey) {
        JsonObject o = new JsonObject();
        o.addProperty("type", "overview");
        o.addProperty("headingKey", headingKey);
        o.addProperty("bodyKey", bodyKey);
        sections.add(o);
        return this;
    }

    /**
     * Bind an item so hovering and pressing Inspect opens this page.
     *
     * @param item item to bind
     * @return {@link SubPageBuilder}
     */
    public SubPageBuilder bindItem(Item item) {
        bind.add(BuiltInRegistries.ITEM.getKey(item).toString());
        return this;
    }

    /**
     * Bind an item by its id so hovering and pressing Inspect opens this page.
     *
     * @param itemId id of the item to bind
     * @return {@link SubPageBuilder}
     */
    public SubPageBuilder bindItem(ResourceLocation itemId) {
        bind.add(itemId.toString());
        return this;
    }

    @Override
    public JsonObject build() {
        JsonObject root = new JsonObject();
        bakeCommon(root);

        root.addProperty("id", id.toString());
        if (parent != null) root.addProperty("parent", parent.toString());
        if (bind.size() > 0) root.add("bind", bind);
        if (sections.size() > 0) root.add("sections", sections);

        return root;
    }
}