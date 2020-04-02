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
import me.shedaniel.clothconfig2.impl.builders.IntSliderBuilder;
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

        SubCategoryBuilder energy = ConfigEntryBuilder.create().startSubCategory(Constants.Config.ENERGY);

        energy.add(new IntSliderBuilder(
                Constants.Config.RESET,
                Constants.Config.WIRE_ENERGY_TRANSFER_LIMIT,
                this.config.wireTransferLimit(),
                1, Integer.MAX_VALUE)
                .setSaveConsumer(this.config::setWireTransferLimit)
                .setDefaultValue(480)
                .build()
        );

        energy.add(new IntSliderBuilder(
                Constants.Config.RESET,
                Constants.Config.HEAVY_WIRE_ENERGY_TRANSFER_LIMIT,
                this.config.heavyWireTransferLimit(),
                1, Integer.MAX_VALUE)
                .setSaveConsumer(this.config::setHeavyWireTransferLimit)
                .setDefaultValue(1440)
                .build()
        );

        energy.add(new IntSliderBuilder(
                Constants.Config.RESET,
                Constants.Config.COAL_GENERATOR_ENERGY_PRODUCTION_RATE,
                this.config.coalGeneratorEnergyProductionRate(),
                1, Integer.MAX_VALUE)
                .setSaveConsumer(this.config::setCoalGeneratorEnergyProductionRate)
                .setDefaultValue(120)
                .build()
        );

        energy.add(new IntSliderBuilder(
                Constants.Config.RESET,
                Constants.Config.SOLAR_PANEL_ENERGY_PRODUCTION_RATE,
                this.config.solarPanelEnergyProductionRate(),
                1, Integer.MAX_VALUE)
                .setSaveConsumer(this.config::setSolarPanelEnergyProductionRate)
                .setDefaultValue(44)
                .build()
        );

        energy.add(new IntSliderBuilder(
                Constants.Config.RESET,
                Constants.Config.CIRCUIT_FABRICATOR_ENERGY_PRODUCTION_RATE,
                this.config.circuitFabricatorEnergyConsumptionRate(),
                1, Integer.MAX_VALUE)
                .setSaveConsumer(this.config::setCircuitFabricatorEnergyConsumptionRate)
                .setDefaultValue(20)
                .build()
        );

        energy.add(new IntSliderBuilder(
                Constants.Config.RESET,
                Constants.Config.ELECTRIC_COMPRESSOR_ENERGY_PRODUCTION_RATE,
                this.config.electricCompressorEnergyConsumptionRate(),
                1, Integer.MAX_VALUE)
                .setSaveConsumer(this.config::setElectricCompressorEnergyConsumptionRate)
                .setDefaultValue(75)
                .build()
        );

        energy.add(new IntSliderBuilder(
                Constants.Config.RESET,
                Constants.Config.OXYGEN_COLLECTOR_ENERGY_PRODUCTION_RATE,
                this.config.oxygenCollectorEnergyConsumptionRate(),
                1, Integer.MAX_VALUE)
                .setSaveConsumer(this.config::setOxygenCollectorEnergyConsumptionRate)
                .setDefaultValue(10)
                .build()
        );

        energy.add(new IntSliderBuilder(
                Constants.Config.RESET,
                Constants.Config.REFINERY_ENERGY_PRODUCTION_RATE,
                this.config.refineryEnergyConsumptionRate(),
                1, Integer.MAX_VALUE)
                .setSaveConsumer(this.config::setRefineryEnergyConsumptionRate)
                .setDefaultValue(60)
                .build()
        );

        energy.add(new IntSliderBuilder(
                Constants.Config.RESET,
                Constants.Config.ENERGY_STORAGE_MODULE_STORAGE_SIZE,
                this.config.energyStorageModuleStorageSize(),
                1, Integer.MAX_VALUE)
                .setSaveConsumer(this.config::setEnergyStorageModuleStorageSize)
                .setDefaultValue(500_000)
                .build()
        );

        energy.add(new IntSliderBuilder(
                Constants.Config.RESET,
                Constants.Config.MACHINE_ENERGY_STORAGE_SIZE,
                this.config.machineEnergyStorageSize(),
                1, Integer.MAX_VALUE)
                .setSaveConsumer(this.config::setMachineEnergyStorageSize)
                .setDefaultValue(30_000)
                .build()
        );

        b.getOrCreateCategory(Constants.Config.DEBUG).addEntry(dB.build());
        b.getOrCreateCategory(Constants.Config.ENERGY).addEntry(energy.build());

        return b.build();
    }
}
