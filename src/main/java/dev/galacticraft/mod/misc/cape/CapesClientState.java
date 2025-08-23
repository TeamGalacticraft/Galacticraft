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