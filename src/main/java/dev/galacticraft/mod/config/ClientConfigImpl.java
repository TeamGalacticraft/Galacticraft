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

package dev.galacticraft.mod.config;

import com.google.gson.*;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.config.ClientConfig;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ClientConfigImpl implements ClientConfig {
    private transient final Gson gson;
    private transient final File file;

    private boolean hideAlphaWarning = false;
    private boolean enableCreativeGearInv = true;
    private boolean squareCannedFood = true;

    public ClientConfigImpl(File file) {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(ClientConfigImpl.class, (InstanceCreator<ClientConfigImpl>) type -> this)
                .create();
        this.file = file;
        this.load();
    }

    @Override
    public boolean isAlphaWarningHidden() {
        return this.hideAlphaWarning;
    }

    public void setAlphaWarningHidden(boolean flag) {
        this.hideAlphaWarning = flag;
    }

    @Override
    public boolean enableCreativeGearInv() {
        return this.enableCreativeGearInv;
    }

    public void setCreativeGearInv(boolean enableCreativeGearInv) {
        this.enableCreativeGearInv = enableCreativeGearInv;
    }

    @Override
    public boolean squareCannedFood() {
        return this.squareCannedFood;
    }

    public void setSquareCannedFood(boolean squareCannedFood) {
        boolean reload = this.squareCannedFood != squareCannedFood;
        this.squareCannedFood = squareCannedFood;
        if (reload) {
            Constant.LOGGER.info("Reload resource packs");
            Minecraft.getInstance().reloadResourcePacks();
        }
    }

    public void load() {
        if (!this.file.exists()) {
            this.file.getParentFile().mkdirs();
            Constant.LOGGER.info("Failed to find config file, creating one.");
            this.save();
        }

        try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
            this.gson.fromJson(reader, ClientConfigImpl.class);
            this.save();
        } catch (IOException | JsonSyntaxException e) {
            Constant.LOGGER.error("Failed to load config.", e);
        }
    }

    @Override
    public void save() {
        try (FileWriter writer = new FileWriter(this.file, StandardCharsets.UTF_8)) {
            this.gson.toJson(this, writer);
        } catch (IOException e) {
            Constant.LOGGER.error("Failed to save config.", e);
        }
    }
}
