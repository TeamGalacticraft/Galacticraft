/*
 * Copyright (c) 2019-2024 Team Galacticraft
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

public class CapesLoader {
    public static class PlayerCapeData {
        public String uuid;
        public String cape;
        public String name;
    }

    public static HashMap<String,String> UUID_CAPE_MAP = new HashMap<>();
    private static final Type PLAYER_CAPE_DATA_TYPE = new TypeToken<List<PlayerCapeData>>(){}.getType();

    public static void load() {
        Util.backgroundExecutor().execute(() -> {
            long startLoad = System.currentTimeMillis();
            Gson gson = new GsonBuilder().create();
            Constant.LOGGER.info("Loading capes data...");
            try {
                List<PlayerCapeData> players = gson.fromJson(
                        IOUtils.toString(
                                new URL("https://raw.githubusercontent.com/TeamGalacticraft/Galacticraft/main/capes.json"),
                                StandardCharsets.UTF_8
                        ),
                        PLAYER_CAPE_DATA_TYPE
                );
                // It's more efficient while ingame to load the data we actually use into a map
                for (var player : players) {
                    UUID_CAPE_MAP.put(player.uuid, player.cape);
                }
            } catch (IOException e) {
                Constant.LOGGER.warn("Failed to load capes.", e);
            }
            Constant.LOGGER.info("Loaded capes for {} players. (Took {}ms)", UUID_CAPE_MAP.size(), System.currentTimeMillis()-startLoad);
        });
    }
}
