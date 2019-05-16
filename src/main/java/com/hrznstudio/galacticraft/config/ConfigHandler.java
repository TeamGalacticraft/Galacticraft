package com.hrznstudio.galacticraft.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hrznstudio.galacticraft.Constants;
import com.hrznstudio.galacticraft.Galacticraft;
import com.hrznstudio.galacticraft.util.FileUtils;
import me.shedaniel.cloth.api.ConfigScreenBuilder;
import me.shedaniel.cloth.gui.ClothConfigScreen;
import me.shedaniel.cloth.gui.entries.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Screen;
import net.minecraft.network.chat.TranslatableComponent;

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
        ClothConfigScreen.Builder builder = new ClothConfigScreen.Builder(parentScreen, new TranslatableComponent(Constants.Config.TITLE).getText(), null);
        ConfigScreenBuilder.CategoryBuilder general = builder.addCategory(new TranslatableComponent(Constants.Config.GENERAL).getText());
        general.addOption(new BooleanListEntry(Constants.Config.BOOLEAN, config.aBoolean, aBoolean -> config.aBoolean = aBoolean));
        general.addOption(new DoubleListEntry(Constants.Config.DOUBLE, config.aDouble, aDouble -> config.aDouble = aDouble));
        general.addOption(new FloatListEntry(Constants.Config.FLOAT, config.aFloat, aFloat -> config.aFloat = aFloat));
        general.addOption(new IntegerListEntry(Constants.Config.INT, config.anInt, anInt -> config.anInt = anInt));
        general.addOption(new LongListEntry(Constants.Config.LONG, config.aLong, aLong -> config.aLong = aLong));
        general.addOption(new StringListEntry(Constants.Config.STRING, config.string, string -> config.string = string));
        general.addOption(new IntegerSliderEntry(Constants.Config.INT_SLIDER, 100, 0, config.anIntS, anInt -> config.anIntS = anInt));
        general.addOption(new LongSliderEntry(Constants.Config.LONG_SLIDER, 100, 0, Math.toIntExact(config.aLongS), aLong -> config.aLongS = aLong));

        builder.setOnSave(savedConfig -> {
            try {
                ConfigHandler.this.saveConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        MinecraftClient.getInstance().openScreen(builder.build());
    }
}
