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
        register("earth", CapeRole.PATRON);
        register("jupiter", CapeRole.PATRON);
        register("mars", CapeRole.PATRON);
        register("mercury", CapeRole.PATRON);
        register("moon", CapeRole.PATRON);
        register("neptune", CapeRole.PATRON);
        register("plain", CapeRole.PATRON);
        register("space_station", CapeRole.PATRON);
        register("sun", CapeRole.PATRON);
        register("uranus", CapeRole.PATRON);
        register("venus", CapeRole.PATRON);

        // Developer capes
        register("developer", CapeRole.DEVELOPER);
        register("rewoven", CapeRole.DEVELOPER);
        register("developer_red", CapeRole.DEVELOPER);
    }
}