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
import java.util.List;

public class ResearchInfo {
    private final ItemConvertible[] icons;
    private final Text title;
    private final Text description;
    private final Identifier background;
    private final boolean hidden;
    private final int tier;

    public ResearchInfo(ItemConvertible[] icons, Text title, Text description, @Nullable Identifier background, boolean hidden, int tier) {
        this.icons = icons;
        this.title = title;
        this.description = description;
        this.background = background;
        this.hidden = hidden;
        this.tier = tier;
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
        Text title = buf.readText();
        Text desc = null;
        if (buf.readBoolean()) {
            desc = buf.readText();
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

    public Text getTitle() {
        return title;
    }

    public Text getDescription() {
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
}
