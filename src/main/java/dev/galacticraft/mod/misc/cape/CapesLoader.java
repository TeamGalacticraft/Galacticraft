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
import com.google.gson.GsonBuilder;
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
                        new URL("https://raw.githubusercontent.com/TeamGalacticraft/Galacticraft/main/capes_roles.json"),
                        StandardCharsets.UTF_8
                );
                List<PlayerRoleData> players = new Gson().fromJson(json, TYPE);
                UUID_ROLE.clear();
                for (var p : players) {
                    CapeRole role = switch (p.role.toLowerCase(Locale.ROOT)) {
                        case "developer", "dev" -> CapeRole.DEVELOPER;
                        case "patron", "patreon", "supporter" -> CapeRole.PATRON;
                        default -> CapeRole.NONE;
                    };
                    UUID_ROLE.put(p.uuid.toLowerCase(Locale.ROOT), role);
                }
                Constant.LOGGER.info("Loaded roles for {} players ({} ms).", UUID_ROLE.size(), System.currentTimeMillis() - t0);
            } catch (IOException e) {
                Constant.LOGGER.warn("Failed to load cape roles.", e);
            }
        });
    }

    public static CapeRole roleFor(String dashedUuid) {
        return UUID_ROLE.getOrDefault(dashedUuid.toLowerCase(Locale.ROOT), CapeRole.NONE);
    }
}