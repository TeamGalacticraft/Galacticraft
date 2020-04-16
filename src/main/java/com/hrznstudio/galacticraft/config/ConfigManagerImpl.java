/*
 * Copyright (c) 2019 HRZN LTD
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
import me.shedaniel.clothconfig2.impl.builders.IntFieldBuilder;
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

        SubCategoryBuilder wires = ConfigEntryBuilder.create().startSubCategory(Constants.Config.WIRES);

        wires.add(new IntFieldBuilder(
                Constants.Config.RESET,
                Constants.Config.WIRE_ENERGY_TRANSFER_LIMIT,
                this.config.wireTransferLimit())
                .setSaveConsumer(this.config::setWireTransferLimit)
                .setDefaultValue(480)
                .build()
        );

        wires.add(new IntFieldBuilder(
                Constants.Config.RESET,
                Constants.Config.HEAVY_WIRE_ENERGY_TRANSFER_LIMIT,
                this.config.heavyWireTransferLimit())
                .setSaveConsumer(this.config::setHeavyWireTransferLimit)
                .setDefaultValue(1440)
                .build()
        );

        SubCategoryBuilder machines = ConfigEntryBuilder.create().startSubCategory(Constants.Config.MACHINES);

        machines.add(new IntFieldBuilder(
                Constants.Config.RESET,
                Constants.Config.COAL_GENERATOR_ENERGY_PRODUCTION_RATE,
                this.config.coalGeneratorEnergyProductionRate())
                .setSaveConsumer(this.config::setCoalGeneratorEnergyProductionRate)
                .setDefaultValue(120)
                .build()
        );

        machines.add(new IntFieldBuilder(
                Constants.Config.RESET,
                Constants.Config.SOLAR_PANEL_ENERGY_PRODUCTION_RATE,
                this.config.solarPanelEnergyProductionRate())
                .setSaveConsumer(this.config::setSolarPanelEnergyProductionRate)
                .setDefaultValue(44)
                .build()
        );

        machines.add(new IntFieldBuilder(
                Constants.Config.RESET,
                Constants.Config.CIRCUIT_FABRICATOR_ENERGY_PRODUCTION_RATE,
                this.config.circuitFabricatorEnergyConsumptionRate())
                .setSaveConsumer(this.config::setCircuitFabricatorEnergyConsumptionRate)
                .setDefaultValue(20)
                .build()
        );

        machines.add(new IntFieldBuilder(
                Constants.Config.RESET,
                Constants.Config.ELECTRIC_COMPRESSOR_ENERGY_PRODUCTION_RATE,
                this.config.electricCompressorEnergyConsumptionRate())
                .setSaveConsumer(this.config::setElectricCompressorEnergyConsumptionRate)
                .setDefaultValue(75)
                .build()
        );

        machines.add(new IntFieldBuilder(
                Constants.Config.RESET,
                Constants.Config.OXYGEN_COLLECTOR_ENERGY_PRODUCTION_RATE,
                this.config.oxygenCollectorEnergyConsumptionRate())
                .setSaveConsumer(this.config::setOxygenCollectorEnergyConsumptionRate)
                .setDefaultValue(10)
                .build()
        );

        machines.add(new IntFieldBuilder(
                Constants.Config.RESET,
                Constants.Config.REFINERY_ENERGY_PRODUCTION_RATE,
                this.config.refineryEnergyConsumptionRate())
                .setSaveConsumer(this.config::setRefineryEnergyConsumptionRate)
                .setDefaultValue(60)
                .build()
        );

        machines.add(new IntFieldBuilder(
                Constants.Config.RESET,
                Constants.Config.ENERGY_STORAGE_MODULE_STORAGE_SIZE,
                this.config.energyStorageModuleStorageSize())
                .setSaveConsumer(this.config::setEnergyStorageModuleStorageSize)
                .setDefaultValue(500_000)
                .build()
        );

        machines.add(new IntFieldBuilder(
                Constants.Config.RESET,
                Constants.Config.ENERGY_STORAGE_SIZE,
                this.config.machineEnergyStorageSize())
                .setSaveConsumer(this.config::setMachineEnergyStorageSize)
                .setDefaultValue(30_000)
                .requireRestart()
                .build()
        );

        b.getOrCreateCategory(Constants.Config.DEBUG).addEntry(dB.build());
        b.getOrCreateCategory(Constants.Config.ENERGY).addEntry(wires.build()).addEntry(machines.build());

        return b.build();
    }
}