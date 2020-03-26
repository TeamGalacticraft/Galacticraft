package com.hrznstudio.galacticraft.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.api.config.Config;
import com.hrznstudio.galacticraft.api.config.ConfigManager;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigManagerImpl implements ConfigManager {

    private Config config = new ConfigImpl();
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private File file = new File(FabricLoader.getInstance().getConfigDirectory(), "galacticraft/config.json");

    public ConfigManagerImpl() {
        this.load();
    }

    @Override
    public void save() {
        try {
            org.apache.commons.io.FileUtils.writeStringToFile(this.file, this.gson.toJson(this.config), Charset.defaultCharset());
        } catch (IOException e) {
            Galacticraft.logger.error("[Galacticraft] Failed to save config.", e);
        }
    }

    @Override
    public void load() {
        try {
            this.file.getParentFile().mkdirs();
            if(!this.file.exists()) {
                Galacticraft.logger.info("[Galacticraft] Failed to find config file, creating one.");
                this.save();
            } else {
                byte[] bytes = Files.readAllBytes(Paths.get(this.file.getPath()));
                this.config = this.gson.fromJson(new String(bytes, Charset.defaultCharset()), ConfigImpl.class);
            }
        } catch (IOException e) {
            Galacticraft.logger.error("[Galacticraft] Failed to load config.", e);
        }
    }

    @Override
    public Config get() {
        return this.config;
    }

    @Override
    public Screen getScreen(Screen parent) {
        ConfigBuilder b = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(I18n.translate(Constants.Config.TITLE))
                .setSavingRunnable(this::save);

        SubCategoryBuilder dB = ConfigEntryBuilder.create().startSubCategory(Constants.Config.DEBUG);
        dB.add(new BooleanToggleBuilder(
                        Constants.Config.RESET,
                        Constants.Config.DEBUG_LOGGING,
                        this.config.isDebugLogEnabled())
                        .setSaveConsumer(flag -> this.config.setDebugLog(flag))
                        .setDefaultValue(false)
                        .build()
        );

        b.getOrCreateCategory(Constants.Config.DEBUG).addEntry(dB.build());

        return b.build();
    }
}
