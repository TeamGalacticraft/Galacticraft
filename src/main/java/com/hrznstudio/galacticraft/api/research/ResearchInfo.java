package com.hrznstudio.galacticraft.api.research;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemConvertible;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ResearchInfo {
    public ResearchInfo(ItemConvertible[] icons, Text title, Text description, Identifier background, boolean hidden) {
    }

    public static ResearchInfo fromJson(JsonObject info) {
        return null;
    }

    public static ResearchInfo fromPacket(PacketByteBuf buf) {
        return null;
    }
}
