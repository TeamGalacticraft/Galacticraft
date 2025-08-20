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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.galacticraft.mod.Constant;
import net.minecraft.client.Minecraft;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public final class CapesClientRole {
    private static final Map<String, CapeRole> UUID_ROLE = new HashMap<>();
    private static final AtomicBoolean LOADED = new AtomicBoolean(false);
    private static final Type LIST_TYPE = new TypeToken<List<PlayerRole>>(){}.getType();

    private static final class PlayerRole {
        String uuid;
        String role;
        String name;
    }

    public static void ensureLoadedAsync() {
        if (LOADED.get()) return;
        LOADED.set(true);
        // load off-thread
        new Thread(() -> {
            try (var in = new URL(Constant.CAPES).openStream();
                 var r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                List<PlayerRole> list = new Gson().fromJson(r, LIST_TYPE);
                UUID_ROLE.clear();
                for (var pr : list) {
                    CapeRole role = switch (String.valueOf(pr.role).toLowerCase(Locale.ROOT)) {
                        case "developer", "dev" -> CapeRole.DEVELOPER;
                        case "patron", "patreon", "supporter" -> CapeRole.PATRON;
                        default -> CapeRole.NONE;
                    };
                    UUID_ROLE.put(pr.uuid.toLowerCase(Locale.ROOT), role);
                }
            } catch (Exception e) {
                Constant.LOGGER.warn("Cape roles (client) failed to load; GC cape UI will not unlock.", e);
            }
        }, "GC-CapeRole-ClientLoad").start();
    }

    /** True if this client account (or local player) is Patron or Developer. */
    public static boolean isEligibleClient() {
        var mc = Minecraft.getInstance();
        UUID id = null;
        if (mc.player != null && mc.player.getGameProfile() != null) {
            id = mc.player.getGameProfile().getId();
        }
        if (id == null && mc.getUser() != null) {
            // fallback to logged-in account UUID (still correct for UI gating)
            id = mc.getUser().getProfileId();
        }
        if (id == null) return false;
        CapeRole role = UUID_ROLE.getOrDefault(id.toString().toLowerCase(Locale.ROOT), CapeRole.NONE);
        return role.ordinal() >= CapeRole.PATRON.ordinal();
    }

    public static CapeRole getClientRole() {
        var mc = Minecraft.getInstance();
        UUID id = null;
        if (mc.player != null && mc.player.getGameProfile() != null) {
            id = mc.player.getGameProfile().getId();
        }
        if (id == null && mc.getUser() != null) {
            id = mc.getUser().getProfileId();
        }
        if (id == null) return CapeRole.NONE;
        return UUID_ROLE.getOrDefault(id.toString().toLowerCase(Locale.ROOT), CapeRole.NONE);
    }
}