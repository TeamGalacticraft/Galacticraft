package com.hrznstudio.galacticraft.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.util.FileUtils;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author <a href="https://github.com/StellarHorizons">StellarHorizons</a>
 */
public class ConfigHandler {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final File configFile;
    private Config config;

    public ConfigHandler() {
        this.configFile = new File(FabricLoader.getInstance().getConfigDirectory(), "galacticraft/galacticraft.json");
        try {
            loadConfig();
        } catch (IOException e) {
            Galacticraft.logger.error("[Galacticraft] Failed to load config, using default.", e);
        }
    }

    /**
     * Gets the config instance
     *
     * @return the loaded config file
     */
    public Config getConfig() {
        if (config == null) {
            try {
                loadConfig();
            } catch (IOException e) {
                Galacticraft.logger.error("[Galacticraft] Failed to load config, using default.", e);
                return new Config();
            }
        }
        return config;
    }

    /**
     * Gets the config file
     *
     * @return the config file.
     */
    public File getConfigFile() {
        return configFile;
    }

    public void loadConfig() throws IOException {
        configFile.getParentFile().mkdirs();
        if (!configFile.exists()) {
            Galacticraft.logger.error("[Galacticraft] Unable to find config file, creating one for you.");
            config = new Config();
            saveConfig();
        } else {
            config = GSON.fromJson(FileUtils.readFile(configFile.toPath().toString(), Charset.defaultCharset()), Config.class);

        }
    }

    /**
     * Saves the config
     *
     * @throws IOException
     */
    public void saveConfig() throws IOException {
        configFile.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(configFile);
        writer.write(GSON.toJson(config));
        writer.close();
    }

    @Environment(EnvType.CLIENT)
    public void openConfigScreen() {
        Screen parentScreen = MinecraftClient.getInstance().currentScreen;
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parentScreen).setTitle(I18n.translate(Constants.Config.TITLE)).setSavingRunnable(() -> {
            try {
                ConfigHandler.this.saveConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        SubCategoryBuilder subCatGravity = ConfigEntryBuilder.create().startSubCategory(Constants.Config.GRAVITY);
        subCatGravity.add(0, ConfigEntryBuilder.create().startFloatField(Constants.Config.MOON_GRAVITY, config.moonGravity).setMax(2.0F).setMin(0.01F).setSaveConsumer(aFloat -> this.config.moonGravity = aFloat).build());
        subCatGravity.add(1, ConfigEntryBuilder.create().startFloatField(Constants.Config.MARS_GRAVITY, config.marsGravity).setMax(2.0F).setMin(0.01F).setSaveConsumer(aFloat -> this.config.marsGravity = aFloat).build());

        builder.getOrCreateCategory(Constants.Config.GENERAL)
                .addEntry(subCatGravity.build())
        ;

        MinecraftClient.getInstance().openScreen(builder.build());
    }
}
