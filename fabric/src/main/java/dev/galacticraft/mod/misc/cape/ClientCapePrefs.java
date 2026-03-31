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
import com.google.gson.GsonBuilder;
import dev.galacticraft.mod.Constant;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class ClientCapePrefs {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "galacticraft_capes.json";

    public CapeMode mode = CapeMode.VANILLA;
    public String gcCapeId = "earth";

    private static File file() {
        return new File(Minecraft.getInstance().gameDirectory, "config/" + FILE_NAME);
    }

    public static ClientCapePrefs load() {
        File f = file();
        if (f.exists()) {
            try (Reader r = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)) {
                ClientCapePrefs p = GSON.fromJson(r, ClientCapePrefs.class);
                if (p == null) return new ClientCapePrefs();
                if (p.mode == null) p.mode = CapeMode.VANILLA;
                if (p.gcCapeId == null || p.gcCapeId.isEmpty()) p.gcCapeId = "earth";
                return p;
            } catch (Exception e) {
                Constant.LOGGER.warn("Failed reading {}", FILE_NAME, e);
            }
        }
        return new ClientCapePrefs();
    }

    public void save() {
        File f = file();
        try {
            File parent = f.getParentFile();
            if (parent != null) parent.mkdirs();
            try (Writer w = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8)) {
                GSON.toJson(this, w);
            }
        } catch (Exception e) {
            Constant.LOGGER.warn("Failed writing {}", FILE_NAME, e);
        }
    }
}