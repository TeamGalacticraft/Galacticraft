/*
 * Copyright (c) 2019-2026 Team Galacticraft
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