package dev.galacticraft.mod.misc.cape;

import net.minecraft.server.level.ServerPlayer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class ServerCapeManager {
    public static final class Assignment {
        public final CapeMode mode;
        public final String gcCapeId;

        public Assignment(CapeMode mode, String gcCapeId) {
            this.mode = mode;
            this.gcCapeId = gcCapeId;
        }
    }

    private static final Map<String, Assignment> ACTIVE = new HashMap<>();

    public static Map<String, Assignment> snapshot() { return Collections.unmodifiableMap(ACTIVE); }

    public static Assignment get(String uuid) { return ACTIVE.get(uuid.toLowerCase(Locale.ROOT)); }

    public static void set(ServerPlayer player, CapeMode mode, String gcCapeId) {
        String id = player.getUUID().toString().toLowerCase(Locale.ROOT);
        ACTIVE.put(id, new Assignment(mode, gcCapeId));
    }

    public static boolean validateSelection(ServerPlayer player, CapeMode mode, String gcCapeId) {
        if (mode != CapeMode.GC) return true;
        CapeRole role = CapesLoader.roleFor(player.getUUID().toString());
        if (role == CapeRole.NONE) return false;
        if (gcCapeId == null) return false;
        CapeRegistry.CapeDef def = CapeRegistry.get(gcCapeId);
        return def != null && role.atLeast(def.minRole);
    }
}