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

package dev.galacticraft.mod.gametest;

import dev.galacticraft.machinelib.api.gametest.SimpleGameTest;
import dev.galacticraft.machinelib.api.gametest.annotation.BasicTest;
import dev.galacticraft.mod.config.ConfigImpl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigTestSuite extends SimpleGameTest {
    private static final File CONFIG_FILE = Path.of(".", ".test_config.json").toFile();

    @BasicTest(batch = "config")
    public void create() {
        new ConfigImpl(CONFIG_FILE);
        // constructor creates a new config (when it doesn't exist)
        assertTrue(CONFIG_FILE.exists());
    }

    @BasicTest(batch = "config")
    public void load() {
        writeConfig("""
            {
                "debug_log": true
            }
            """);

        ConfigImpl config = new ConfigImpl(CONFIG_FILE);
        assertTrue(config.isDebugLogEnabled());
    }

    @BasicTest(batch = "config")
    public void modify() {
        ConfigImpl config = new ConfigImpl(CONFIG_FILE);
        config.setDebugLog(true);
        config.save();

        config.setDebugLog(false);
        config.load();

        // should load the saved value
        assertTrue(config.isDebugLogEnabled());
    }

    @BasicTest(batch = "config")
    public void invalidConfigDoesNotModify() {
        ConfigImpl config = new ConfigImpl(CONFIG_FILE);
        config.setDebugLog(true);

        byte[] randomData = new byte[64];
        new Random().nextBytes(randomData);
        writeConfig(Base64.getEncoder().encodeToString(randomData));
        assertDoesNotThrow(config::load); // shouldn't crash with invalid config file

        // the config should not have changed
        assertTrue(config.isDebugLogEnabled());
    }

    private static void writeConfig(String config) {
        try (FileWriter writer = new FileWriter(CONFIG_FILE, StandardCharsets.UTF_8)) {
            writer.write(config);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write test config", e);
        }
    }
}
