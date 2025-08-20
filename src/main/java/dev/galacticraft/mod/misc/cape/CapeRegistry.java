package dev.galacticraft.mod.misc.cape;

import dev.galacticraft.mod.Constant;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public final class CapeRegistry {
    public static final class CapeDef {
        public final String id;
        public final CapeRole minRole;
        public final ResourceLocation texture;

        public CapeDef(String id, CapeRole minRole) {
            this.id = id;
            this.minRole = minRole;
            this.texture = Constant.id("textures/cape/cape_" + id + ".png");
        }
    }

    private static final Map<String, CapeDef> CAPES = new LinkedHashMap<>();

    public static synchronized void register(String id, CapeRole minRole) {
        CAPES.put(id, new CapeDef(id, minRole));
    }

    public static CapeDef get(String id) { return CAPES.get(id); }

    public static Collection<CapeDef> all() { return Collections.unmodifiableCollection(CAPES.values()); }

    public static Collection<CapeDef> allowedFor(CapeRole role) {
        List<CapeDef> out = new ArrayList<>();
        for (CapeDef def : CAPES.values()) if (role.atLeast(def.minRole)) out.add(def);
        return out;
    }

    public static void bootstrap() {
        // Patron capes
        register("cape_earth", CapeRole.PATRON);
        register("cape_jupiter", CapeRole.PATRON);
        register("cape_mars", CapeRole.PATRON);
        register("cape_mercury", CapeRole.PATRON);
        register("cape_moon", CapeRole.PATRON);
        register("cape_neptune", CapeRole.PATRON);
        register("cape_plain", CapeRole.PATRON);
        register("cape_space_station", CapeRole.PATRON);
        register("cape_sun", CapeRole.PATRON);
        register("cape_uranus", CapeRole.PATRON);
        register("cape_venus", CapeRole.PATRON);

        // Developer capes
        register("cape_developer", CapeRole.DEVELOPER);
        register("cape_rewoven", CapeRole.DEVELOPER);
        register("cape_developer_red", CapeRole.DEVELOPER);
    }
}