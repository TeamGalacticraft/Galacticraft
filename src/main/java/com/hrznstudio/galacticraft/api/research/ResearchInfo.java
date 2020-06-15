package com.hrznstudio.galacticraft.api.research;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ResearchInfo {
    private final ItemConvertible[] icons;
    private final TranslatableText title;
    private final TranslatableText description;
    @Nullable
    private final Identifier background;
    private final boolean hidden;
    private final int tier;
    private float x;
    private float y;

    public ResearchInfo(ItemConvertible[] icons, TranslatableText title, TranslatableText description, @Nullable Identifier background, boolean hidden, int tier) {
        this.icons = icons;
        this.title = title;
        this.description = description;
        this.background = background;
        this.hidden = hidden;
        this.tier = tier;
    }

    public void setPos(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getTier() {
        return tier;
    }

    public static ResearchInfo fromJson(JsonObject info) {
        TranslatableText title = new TranslatableText(info.get("title").getAsString());
        TranslatableText desc = new TranslatableText(info.get("description").getAsString());
        JsonArray itemsToRender = info.get("items_to_render").getAsJsonArray();
        List<Item> items = new ArrayList<>();
        for (JsonElement element : itemsToRender) {
            items.add(Registry.ITEM.get(new Identifier(element.getAsString())));
        }

        return new ResearchInfo(items.toArray(new ItemConvertible[0]), title, desc,
                info.has("background") ? new Identifier(info.get("background").getAsString()) : null, (info.has("hidden") && info.get("hidden").getAsBoolean()), info.get("tier").getAsInt());
    }

    public static ResearchInfo fromPacket(PacketByteBuf buf) {
        Item[] items = new Item[buf.readVarInt()];
        for (int i = 0; i < items.length; i++) {
            items[i] = Registry.ITEM.get(buf.readIdentifier());
        }
        TranslatableText title = (TranslatableText) buf.readText();
        TranslatableText desc = null;
        if (buf.readBoolean()) {
            desc = (TranslatableText) buf.readText();
        }
        Identifier back = null;
        if (buf.readBoolean()) {
            back = buf.readIdentifier();
        }

        return new ResearchInfo(items, title, desc, back, buf.readBoolean(), buf.readInt());
    }

    public ItemConvertible[] getIcons() {
        return icons;
    }

    public TranslatableText getTitle() {
        return title;
    }

    public TranslatableText getDescription() {
        return description;
    }

    public Identifier getBackground() {
        return background;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void toPacket(PacketByteBuf buf) {
        buf.writeVarInt(this.icons.length);
        for (ItemConvertible convertible : this.icons) {
            buf.writeIdentifier(Registry.ITEM.getId(convertible.asItem()));
        }

        buf.writeText(title);
        buf.writeBoolean(description != null);
        if (description != null) {
            buf.writeText(description);
        }
        buf.writeBoolean(background != null);
        if (background != null) {
            buf.writeIdentifier(background);
        }

        buf.writeBoolean(hidden);
        buf.writeInt(tier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResearchInfo that = (ResearchInfo) o;
        return hidden == that.hidden &&
                tier == that.tier &&
                Float.compare(that.x, x) == 0 &&
                Float.compare(that.y, y) == 0 &&
                Arrays.equals(icons, that.icons) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(background, that.background);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(title, description, background, hidden, tier, x, y);
        result = 31 * result + Arrays.hashCode(icons);
        return result;
    }

    @Override
    public String toString() {
        return "ResearchInfo{" +
                "icons=" + Arrays.toString(icons) +
                ", title=" + title +
                ", description=" + description +
                ", background=" + background +
                ", hidden=" + hidden +
                ", tier=" + tier +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
