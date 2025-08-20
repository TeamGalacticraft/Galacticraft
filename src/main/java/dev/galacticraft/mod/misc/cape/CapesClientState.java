package dev.galacticraft.mod.misc.cape;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class CapesClientState {
    public static final class Entry {
        public final CapeMode mode;
        public final String gcCapeId;
        public Entry(CapeMode mode, String gcCapeId) {
            this.mode = mode;
            this.gcCapeId = gcCapeId;
        }
    }

    private static final Map<String, Entry> ASSIGN = new HashMap<>();

    public static synchronized void apply(Map<String, Entry> snapshot) {
        ASSIGN.clear();
        ASSIGN.putAll(snapshot);
    }

    public static synchronized Entry forPlayer(AbstractClientPlayer player) {
        if (player == null || player.getGameProfile() == null) return null;
        String id = player.getGameProfile().getId().toString().toLowerCase(Locale.ROOT);
        return ASSIGN.get(id);
    }

    public static ResourceLocation gcCapeTexture(String gcCapeId) {
        var def = CapeRegistry.get(gcCapeId);
        return def != null ? def.texture : null;
    }
}