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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.galacticraft.mod.Constant;
import net.minecraft.Util;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class CapesLoader {
    private static volatile boolean loaded = false;
    public static final class PlayerRoleData {
        public String uuid;
        public String role;
        public String name;
    }

    private static final Type TYPE = new TypeToken<List<PlayerRoleData>>(){}.getType();

    public static final Map<String, CapeRole> UUID_ROLE = new HashMap<>();

    public static void loadAsync() {
        Util.backgroundExecutor().execute(() -> {
            long t0 = System.currentTimeMillis();
            Constant.LOGGER.info("Loading cape roles...");

            try {
                String json = IOUtils.toString(
                        new URL(Constant.CAPES),
                        StandardCharsets.UTF_8
                );

                List<PlayerRoleData> players = new Gson().fromJson(json, TYPE);

                UUID_ROLE.clear();

                for (var p : players) {
                    CapeRole role = switch (String.valueOf(p.role).toLowerCase(Locale.ROOT)) {
                        case "developer", "dev" -> CapeRole.DEVELOPER;
                        case "patron", "patreon", "supporter" -> CapeRole.PATRON;
                        default -> CapeRole.NONE;
                    };

                    String uuid = normalizeUuid(p.uuid);
                    if (!uuid.isEmpty()) {
                        UUID_ROLE.put(uuid, role);
                    }
                }

                loaded = true;

                Constant.LOGGER.info(
                        "Loaded roles for {} players ({} ms).",
                        UUID_ROLE.size(),
                        System.currentTimeMillis() - t0
                );
            } catch (IOException e) {
                loaded = false;
                Constant.LOGGER.warn("Failed to load cape roles.", e);
            }
        });
    }

    public static CapeRole roleFor(String uuid) {
        return UUID_ROLE.getOrDefault(normalizeUuid(uuid), CapeRole.NONE);
    }

    public static boolean isLoaded() {
        return loaded;
    }

    private static String normalizeUuid(String uuid) {
        if (uuid == null) return "";
        return uuid.toLowerCase(Locale.ROOT).replace("-", "");
    }
}